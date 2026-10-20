
package com.paypal.api_gateway.filters;

import com.paypal.api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/auth/signup",
            "/auth/login"
    );

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        System.out.println("========== API GATEWAY FILTER ==========");

        String path = exchange.getRequest()
                .getPath()
                .value();

        String normalizedPath = path.replaceAll("/+$", "");

        System.out.println("Request path: " + path);
        System.out.println("Normalized path: " + normalizedPath);

        // Allow public endpoints
        if (PUBLIC_PATHS.contains(normalizedPath)) {
            System.out.println("Public endpoint: skipping JWT validation");
            return chain.filter(exchange);
        }

        // Read Authorization header
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        System.out.println(
                "Authorization header present: " + (authHeader != null)
        );

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.err.println("JWT rejected: missing or invalid Bearer header");

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }

        try {
            // Extract token
            String token = authHeader.substring(7).trim();

            if (token.isEmpty()) {
                System.err.println("JWT rejected: empty token");

                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }

            System.out.println("Validating JWT...");

            // Validate token
            Claims claims = JwtUtil.validateToken(token);

            System.out.println("JWT validation successful");
            System.out.println("JWT subject: " + claims.getSubject());

//            String userId = claims.get("userId", String.class);
//            String role =
            Object userIdClaim = claims.get("userId");
            String userId = userIdClaim != null
                    ? userIdClaim.toString()
                    : null;

            String role = claims.get("role", String.class);

            // Add user information to the request
            var mutatedRequest = exchange.getRequest()
                    .mutate()
                    .headers(headers -> {
                        // Remove any client-supplied identity headers
                        headers.remove("X-User-Email");
                        headers.remove("X-User-Id");
                        headers.remove("X-User-Role");

                        headers.set(
                                "X-User-Email",
                                claims.getSubject()
                        );

                        if (userId != null) {
                            headers.set("X-User-Id", userId);
                        }

                        if (role != null) {
                            headers.set("X-User-Role", role);
                        }
                    })
                    .build();

            var mutatedExchange = exchange.mutate()
                    .request(mutatedRequest)
                    .build();

            System.out.println("Forwarding authenticated request to downstream service");

            return chain.filter(mutatedExchange)
                    .doOnError(error ->
                            System.err.println(
                                    "Downstream request error: "
                                            + error.getMessage()
                            )
                    );

        } catch (Exception e) {
            System.err.println(
                    "JWT validation failed: "
                            + e.getClass().getSimpleName()
                            + ": " + e.getMessage()
            );

            exchange.getResponse()
                    .setStatusCode(HttpStatus.UNAUTHORIZED);

            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
