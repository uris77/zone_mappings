package org.pasmo.jwt

import com.auth0.jwt.Algorithm
import com.auth0.jwt.ClaimSet
import com.auth0.jwt.JwtProxy
import com.auth0.jwt.impl.BasicPayloadHandler
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import ratpack.groovy.handling.GroovyChainAction

import static ratpack.jackson.Jackson.json

/**
 * Created by uris77 on 9/25/14.
 */
class GoogleJwtHandler extends GroovyChainAction {
    private final String accessTokenUrl = "https://accounts.google.com"
    private final String peopleApiUrl = "https://www.googleapis.com"
    private final String GRANT_TYPE = "authorization_code"
    private final String GOOGLE_SECRET= System.getProperty("GOOGLE_SECRET")

    @Override
    protected void execute() throws Exception {
        handler("google") {
            byMethod {
                post {
                    blocking {
                        Map params = parse Map
                        println "got params: ${params}"
                        getToken(params)
                    } then { token ->
                        println "token: ${token}"
                        render json(token)
                    }
                }
            }

        }
    }

    Map<String, String> getToken(Map googleRequest) {
        def http = new HTTPBuilder(accessTokenUrl)

        def postBody = [
                client_id: googleRequest.clientId,
                redirect_uri: googleRequest.redirectUri,
                code: googleRequest.code,
                client_secret: GOOGLE_SECRET,
                grant_type: GRANT_TYPE
        ]
        println "\n\npostBody: ${postBody}"

        http.request(Method.POST) {
            uri.path = "/o/oauth2/token"
            send ContentType.URLENC, postBody

            response.success = {resp, reader ->
                println "Got reader: ${reader}"
                def googleProfile = getGoogleId(reader.access_token)
                JwtProxy jwtProxy = new UserJwtProxy()
                jwtProxy.setPayloadHandler(new BasicPayloadHandler())
                ClaimSet claimSet = new ClaimSet()
                claimSet.setExp(24 * 60 * 60) //expire in 24 hrs
                googleProfile.apiToken = UUID.randomUUID().toString()
                def toEncode = [

                        firstName: googleProfile.firstName,
                        lastName: googleProfile.lastName,
                        google: googleProfile.google
                ]

                String token = jwtProxy.encode(Algorithm.HS256, toEncode, "secret", claimSet)
                return [token: token]
            }
        }
    }

    Map<String, String> getGoogleId(String accessToken) {
        def http = new HTTPBuilder(peopleApiUrl)
        def googleProfile = [:]
        http.request(Method.GET) {
            uri.path = "/plus/v1/people/me/openIdConnect"
            headers["Authorization"] = "Bearer ${accessToken}"
            headers["Accept"] = "application/json"

            response.success = {resp, data ->
                googleProfile.google = data.sub
                googleProfile.firstName = data.given_name
                googleProfile.lastName= data.family_name
            }
        }
        googleProfile
    }
}
