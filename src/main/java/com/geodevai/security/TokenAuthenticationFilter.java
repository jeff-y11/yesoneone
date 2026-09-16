package com.geodevai.security;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.geodevai.data.model.McpKey;
import com.geodevai.mcp.service.McpKeyService;
import com.geodevai.data.model.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    final private TokenProvider tokenProvider;
    final private McpKeyService keyService;

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            if (isMcpRequest(request)) {
                authenticateWithKey(request);
            } else {
                authenticateWithJwt(request);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication", e);
        }
        chain.doFilter(request, response);
    }

    private boolean isMcpRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/mcp/");
    }

    private void authenticateWithKey(HttpServletRequest request) {
        String key = extractKeyFromRequest(request);
        if (key == null || !keyService.validateKey(key)) {
            return;
        }
        Optional<McpKey> keyRecord = keyService.getKeyByHash(keyService.hashKey(key));
        if (keyRecord.isPresent()) {
            User user = keyRecord.get().getUser();
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
            UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUserId().toString()).password("").authorities(authorities).build();
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, key, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            AuthChannelHolder.setChannel("MCP");
            AuthChannelHolder.setDisplayName(user.getDisplayName());
        }
    }

    private void authenticateWithJwt(HttpServletRequest request) {
        getJwtFromRequest(request)
                .flatMap(tokenProvider::validateTokenAndGetJws)
                .ifPresent(jws -> {
                    String username = jws.getPayload().getSubject();
                    String role = jws.getPayload().get("role", String.class);
                    String displayName = jws.getPayload().get("displayName", String.class);

                    List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(username).password("").authorities(authorities).build();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, jws, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    AuthChannelHolder.setChannel("Web");
                    AuthChannelHolder.setDisplayName(displayName);
                });
    }

    private Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String tokenHeader = request.getHeader(TOKEN_HEADER);
        if (tokenHeader == null || tokenHeader.isEmpty()) {
            return Optional.empty();
        }
        tokenHeader = tokenHeader.trim();
        if (StringUtils.hasText(tokenHeader) && tokenHeader.startsWith(TOKEN_PREFIX)) {
            return Optional.of(tokenHeader.replace(TOKEN_PREFIX, ""));
        }
        return Optional.empty();
    }

    private String extractKeyFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
