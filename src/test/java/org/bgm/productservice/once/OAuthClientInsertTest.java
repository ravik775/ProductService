package org.bgm.productservice.once;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.UUID;

@SpringBootTest
public class OAuthClientInsertTest {

    @Autowired
    private RegisteredClientRepository registeredClientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void addSampleClientToDB(){
        /*RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("cred-oidc-client")
                .clientSecret(passwordEncoder.encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oauth.pstmn.io/v1/callback") // after auth token is sent here
                //.redirectUri("http://127.0.0.1:8080/login/oauth2/code/oidc-client") // after auth token is sent here
                .postLogoutRedirectUri("http://127.0.0.1:8080/")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("USEREMAIL")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();*/

        RegisteredClient oidcClient =
                RegisteredClient.withId(UUID.randomUUID().toString())
                        .clientId("cred-oidc-client")
                        .clientSecret(passwordEncoder.encode("secret"))
                        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                        .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                        .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                        .redirectUri("https://oauth.pstmn.io/v1/callback")
                        .postLogoutRedirectUri("http://127.0.0.1:8080/")
                        .scope(OidcScopes.OPENID)
                        .scope(OidcScopes.PROFILE)
                        .scope("USEREMAIL")
                        .clientSettings(
                                ClientSettings.builder()
                                        .requireAuthorizationConsent(false)
                                        .requireProofKey(false)
                                        .build()
                        )
                        .build();
        registeredClientRepository.save(oidcClient);
    }
}
