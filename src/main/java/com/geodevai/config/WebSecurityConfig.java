package com.geodevai.config;

import com.geodevai.security.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

import com.geodevai.security.SpaWebFilter;
import com.geodevai.security.TokenAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    final private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    final private TokenAuthenticationFilter tokenAuthenticationFilter;
    final private SpaWebFilter spaWebFilter;
    final private ClientRegistrationRepository clientRegistrationRepository;
    private final Environment environment;

    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String ADVISOR = "ADVISOR";
    public static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        boolean isDev = environment.matchesProfiles("DEV");

        DefaultOAuth2AuthorizationRequestResolver resolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization"
        );

        return http
                .authorizeHttpRequests(authorize -> {
                    // Secure the invite endpoint first (Order matters!)
                    authorize.requestMatchers("/services/auth/invite").hasRole("ADMIN");

                    // Static assets and SPA routes
                    authorize.requestMatchers("/*", "/favicon.ico", "/static/**", "/assets/**", "/error", "/csrf").permitAll();

                    //Open endpoints
                    authorize.requestMatchers("/public/**", "/oauth2/**").permitAll();
                    authorize.requestMatchers("/services/auth/userinfo").authenticated();
                    authorize.requestMatchers("/services/auth/**").permitAll();
                    authorize.requestMatchers("/mcp/**").authenticated();

                    //private endpoints
                    authorize.requestMatchers("/data/**", "/services/**").authenticated();

                    if (isDev) {
                        authorize.requestMatchers("/actuator/**").permitAll();
                        authorize.requestMatchers("/h2-console/**").permitAll();
                    }


                    authorize.anyRequest().authenticated();
                })
                // --- CUSTOM FILTER REGISTRATION ---
                // 1. SPA Web Filter: Catches deep links early to route them to index.html
                .addFilterBefore(spaWebFilter, LogoutFilter.class)

                // 2. JWT Filter: Extracts token and builds SecurityContext before authorization happens
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // ----------------------------------
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2LoginSuccessHandler)
                        .authorizationEndpoint(auth -> auth.authorizationRequestResolver(resolver))
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuditorAware<UUID> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null &&
                    authentication.isAuthenticated() &&
                    !(authentication instanceof AnonymousAuthenticationToken)) {
                try {
                    return Optional.of(UUID.fromString(authentication.getName()));
                } catch (IllegalArgumentException e) {
                    return Optional.of(SYSTEM_USER_ID);
                }
            }
            return Optional.of(SYSTEM_USER_ID);
        };
    }
}
