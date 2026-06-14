package org.bgm.productservice.controllers;

import org.bgm.productservice.dtos.LoginRequest;
import org.bgm.productservice.dtos.LoginResponse;
import org.bgm.productservice.security.AuthenticationSuccessLogger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

@RestController
public class AuthController {

    private static final long TOKEN_EXPIRY_SECONDS = 3600;

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final AuthorizationServerSettings authorizationServerSettings;
    private final AuthenticationSuccessLogger authenticationSuccessLogger;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            AuthorizationServerSettings authorizationServerSettings,
            AuthenticationSuccessLogger authenticationSuccessLogger) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.authorizationServerSettings = authorizationServerSettings;
        this.authenticationSuccessLogger = authenticationSuccessLogger;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        authenticationSuccessLogger.logSuccessfulAuthentication(authentication);

        Instant now = Instant.now();
        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(authorizationServerSettings.getIssuer())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(TOKEN_EXPIRY_SECONDS))
                .subject(authentication.getName())
                .claim("authorities", authorities)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(new LoginResponse(token, "Bearer", TOKEN_EXPIRY_SECONDS));
    }
}
