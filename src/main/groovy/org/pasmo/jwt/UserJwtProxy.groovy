package org.pasmo.jwt

import com.auth0.jwt.Algorithm
import com.auth0.jwt.ClaimSet
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.JwtProxy
import com.auth0.jwt.PayloadHandler

class UserJwtProxy implements JwtProxy{
    private static final String PAYLOAD_ID = "user"
    private PayloadHandler payloadHandler

    @Override
    void setPayloadHandler(PayloadHandler payloadHandler) {
        this.payloadHandler = payloadHandler
    }

    public getPayloadHandler() {
        payloadHandler
    }

    @Override
    String encode(Algorithm algorithm, Object obj, String secret, ClaimSet claimSet) throws Exception {
        ObjectJwtSigner jwtSigner = new ObjectJwtSigner()
        String payload = payloadHandler.encoding(obj)
        jwtSigner.encode(algorithm, payload, PAYLOAD_ID, secret, claimSet)
    }

    @Override
    Object decode(Algorithm algorithm, String token, String secret) throws Exception {
        JWTVerifier jwtVerifier = new JWTVerifier(org.apache.commons.codec.binary.Base64.encodeBase64(secret.getBytes()))
        Map<String, Object> verify = jwtVerifier.verify(token)
        String payload = (String) verify.get(PAYLOAD_ID)

        return payloadHandler.decoding(payload)
    }
}
