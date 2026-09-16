package com.geodevai.service;

import com.geodevai.config.WebSecurityConfig;
import com.geodevai.data.model.Login;
import com.geodevai.data.model.User;
import com.geodevai.data.repository.LoginRepository;
import com.geodevai.data.repository.UserRepository;
import com.geodevai.security.OAuth2Provider;
import com.geodevai.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {
    private final LoginRepository loginRepository;
    private final UserRepository userRepository;
    private final TokenProvider jwtService;

    public Login linkGoogleAccount(UUID uuid, String googleId, String email, String displayName, String accessToken, String refreshToken, Instant tokenExpiry) {
        Optional<Login> loginOpt = loginRepository.findByProviderAndProviderId(OAuth2Provider.GOOGLE, googleId);
        if (loginOpt.isPresent()) {
            Login existingLogin = loginOpt.get();
            if (!existingLogin.getUser().getUserId().equals(uuid)) {
                throw new IllegalStateException("Google account is already linked to a different user");
            }
            Login login = existingLogin;
            login.setAccessToken(accessToken);
            if (StringUtils.hasLength(refreshToken)) {
                login.setRefreshToken(refreshToken);
            }
            login.setTokenExpiry(tokenExpiry);
            return loginRepository.save(login);
        }

        User user = userRepository.getReferenceById(uuid);
        Login login = new Login();
        login.setAccessToken(accessToken);
        if (StringUtils.hasLength(refreshToken)) {
            login.setRefreshToken(refreshToken);
        }
        login.setTokenExpiry(tokenExpiry);
        login.setProvider(OAuth2Provider.GOOGLE);
        login.setProviderId(googleId);
        login.setUser(user);
        return loginRepository.save(login);
    }

    public String findOrCreateGoogleUser(String googleId, String email, String displayName, String accessToken, String refreshToken, Instant tokenExpiry) {
        Optional<Login> loginOpt = loginRepository.findByProviderAndProviderId(OAuth2Provider.GOOGLE, googleId);
        User user;
        if (loginOpt.isPresent()) {
            Login login = loginOpt.get();
            login.setAccessToken(accessToken);
            if (StringUtils.hasLength(refreshToken)) {
                login.setRefreshToken(refreshToken);
            }
            login.setTokenExpiry(tokenExpiry);
            loginRepository.save(login);
            user = login.getUser();
        } else {
            user = new User();
            user.setDisplayName(displayName);
            user.setRole(WebSecurityConfig.USER);
            user.setContactEmail(email);

            user = userRepository.save(user);

            Login login = new Login();
            login.setAccessToken(accessToken);
            if (StringUtils.hasLength(refreshToken)) {
                login.setRefreshToken(refreshToken);
            }
            login.setTokenExpiry(tokenExpiry);
            login.setProvider(OAuth2Provider.GOOGLE);
            login.setProviderId(googleId);
            login.setUser(user);
            loginRepository.save(login);
        }
        return jwtService.generate(user);
    }
}
