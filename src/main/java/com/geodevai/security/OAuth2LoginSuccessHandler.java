package com.geodevai.security;

import com.geodevai.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final AuthService authService;

    @Value("${geodevai.app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attrs = oauthToken.getPrincipal().getAttributes();
        String googleId = (String)attrs.get("sub");
        String email = (String)attrs.get("email");
        String displayName = (String)attrs.get("name");
        if (!StringUtils.hasText(displayName)) {
            displayName = email;
        }

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName()
        );

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        String refreshToken = authorizedClient.getRefreshToken() != null ? authorizedClient.getRefreshToken().getTokenValue() : null;
        Instant tokenExpiry = authorizedClient.getAccessToken().getExpiresAt();


        String linkingUserId = (String)request.getSession().getAttribute("OAUTH_LINKING_USER_ID");
        if (linkingUserId != null) {
            request.getSession().removeAttribute("OAUTH_LINKING_USER_ID");
            authService.linkGoogleAccount(
                    java.util.UUID.fromString(linkingUserId),
                    googleId, email, displayName,
                    accessToken, refreshToken, tokenExpiry
            );
            response.sendRedirect(frontendUrl+"/link-account?linked=google");
        } else {
             String jwt = authService.findOrCreateGoogleUser(
                    googleId, email, displayName,
                    accessToken, refreshToken, tokenExpiry
            );
            response.sendRedirect(frontendUrl+"/callback?token="+jwt);
        }
    }
}
