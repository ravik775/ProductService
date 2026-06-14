package org.bgm.productservice.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bgm.productservice.security.AuthenticationSuccessLogger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

public class LoggingAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final AuthenticationSuccessLogger authenticationSuccessLogger;

    public LoggingAuthenticationSuccessHandler(AuthenticationSuccessLogger authenticationSuccessLogger) {
        this.authenticationSuccessLogger = authenticationSuccessLogger;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {
        authenticationSuccessLogger.logSuccessfulAuthentication(authentication);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
