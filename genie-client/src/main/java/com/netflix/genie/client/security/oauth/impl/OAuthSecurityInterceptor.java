/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.client.security.oauth.impl;

import com.netflix.genie.client.security.SecurityInterceptor;
import com.netflix.genie.client.security.oauth.AccessToken;
import com.netflix.genie.client.security.oauth.TokenFetcher;
import com.netflix.genie.common.exceptions.GenieException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * An interceptor that adds security headers to all outgoing requests.
 *
 * @author amsharma
 */
@Slf4j
public class OAuthSecurityInterceptor implements SecurityInterceptor {

    private final TokenFetcher tokenFetcher;

    /**
     * Constructor.
     *
     * @param url The URL of the IDP server for getting oauth token.
     * @param clientId The client id to use to fetch credentials.
     * @param clientSecret The client secret to use to fetch credentials.
     * @param grantType The grant type for the user.
     * @param scope The scope of the user permissions.
     *
     * @throws GenieException If there is a problem initializing the object.
     */
    public OAuthSecurityInterceptor(
        final String url,
        final String clientId,
        final String clientSecret,
        final String grantType,
        final String scope
        ) throws GenieException {
        log.debug("Constructor called.");
        tokenFetcher = new TokenFetcher(url, clientId, clientSecret, grantType, scope);
    }

    @Override
    public Response intercept(
        final Chain chain
    ) throws IOException {
        try {
            final AccessToken accessToken = this.tokenFetcher.getToken();

            final Request newRequest = chain
                .request()
                .newBuilder()
                .addHeader("Authorization", accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .build();

            log.debug("Sending request {} on {} {} {}", newRequest.url(), chain.connection(), newRequest.headers());

            return chain.proceed(newRequest);
        } catch (GenieException e) {
            throw new IOException("Genie client could not add Authorization Headers to outgoing request.");
        }
    }
}
