package org.pasmo.jwt

import com.auth0.jwt.Algorithm
import com.auth0.jwt.ClaimSet
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.naming.OperationNotSupportedException

class ObjectJwtSigner {
    String encode(Algorithm algorithm, Object payload, String payloadId, String key, ClaimSet claimSet) throws Exception {
        List<String> segments = []
        segments << encodedHeader(algorithm)
        segments << encodedPayload(payload, payloadId, claimSet)
        segments << encodedSignature(segments.join("."), key, algorithm)

        segments.join(".")
    }

    /**
     * Generate the header part of a JSON web token
     */
    private String encodedHeader(Algorithm algorithm)
            throws Exception {

        if (algorithm == null) { // default the algorithm if not specified
            algorithm = Algorithm.HS256
        }

        // create the header
        ObjectNode header = JsonNodeFactory.instance.objectNode()
        header.put("type", "JWT")
        header.put("alg", algorithm.name())

        return base64UrlEncode(header.toString().getBytes())
    }


    /**
     * Generate the JSON web token payload, merging it with the claim set
     */
    private String encodedPayload(Object payload, String payloadId, ClaimSet claimSet) throws Exception {

        ObjectNode localClaimSet = JsonNodeFactory.instance.objectNode()
        ObjectNode localPayload = JsonNodeFactory.instance.objectNode()

        localPayload.putPOJO(payloadId, payload)

        if(claimSet != null) {
            if(claimSet.getExp() > 0) {
                localClaimSet.put("exp", claimSet.getExp());
            }
            localPayload.putAll(localClaimSet)
        }

        base64UrlEncode(localPayload.toString().getBytes())
    }

    /**
     * Sign the header and payload
     */
    private String encodedSignature(String signingInput, String key,
                                    Algorithm algorithm) throws Exception {

        byte[] signature = sign(algorithm, signingInput, key)
        base64UrlEncode(signature)
    }

    /**
     * Safe URL encode a byte array to a String
     */
    private String base64UrlEncode(byte[] str) throws Exception {
        new String(org.apache.commons.codec.binary.Base64.encodeBase64URLSafe(str))
    }

    /**
     * Switch the signing algorithm based on input, RSA not supported
     */
    private byte[] sign(Algorithm algorithm, String msg, String key)
            throws Exception {

        switch (algorithm) {
            case Algorithm.HS256:
            case Algorithm.HS384:
            case Algorithm.HS512:
                return signHmac(algorithm, msg, key);
            case Algorithm.RS256:
            case Algorithm.RS384:
            case Algorithm.RS512:
            default:
                throw new OperationNotSupportedException("Unsupported signing method")
        }
    }

    /**
     * Sign an input string using HMAC and return the encrypted bytes
     */
    private byte[] signHmac(Algorithm algorithm, String msg, String key)
            throws Exception {

        Mac mac = Mac.getInstance(algorithm.getValue())
        mac.init(new SecretKeySpec(key.getBytes(), algorithm.getValue()))
        mac.doFinal(msg.getBytes())
    }
}
