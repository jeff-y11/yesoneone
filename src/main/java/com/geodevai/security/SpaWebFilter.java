package com.geodevai.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class SpaWebFilter extends OncePerRequestFilter {
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final String[] spaDEV = { "/services", "/data", "/actuator", "/oauth2", "/login", "/h2-console", "/callback", "/home", "/mcp" };
    private final String[] spaPROD = { "/services", "/data", "/actuator", "/login", "/oauth2", "/callback", "/home", "/mcp" };

    private Boolean matchPaths(String path) {
        String[] match = (activeProfile.contains("DEV")) ? spaDEV : spaPROD;
        
        for (String matchPath : match ) {
            if (path.startsWith(matchPath)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!matchPaths(path) && !path.contains(".") && path.matches("/(.*)")) {
            request.getRequestDispatcher("/").forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
