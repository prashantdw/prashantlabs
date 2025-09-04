package com.prashantlabs.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
public class SecurityConfig {

  @Bean
  public JwtUtil jwt(@Value("${app.jwt.secret}") String secret,
                     @Value("${app.jwt.issuer}") String issuer) {
    return new JwtUtil(secret, issuer);
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    // Swagger UI origins (add both localhost & 127.0.0.1 to be safe)
    cfg.setAllowedOrigins(List.of("http://localhost:8083", "http://127.0.0.1:8083"));
    cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
    cfg.setAllowedHeaders(List.of(
            "Authorization","Content-Type","X-Requested-With","X-Signature","Idempotency-Key",
            "Accept","Origin","Cache-Control","Pragma"
    ));
    // Only keep true if you actually need cookies; otherwise you can set false.
    cfg.setAllowCredentials(true);
    // Optional: reduce preflight chattiness
    cfg.setMaxAge(Duration.ofHours(1));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }

  @Bean
  public SecurityFilterChain chain(HttpSecurity http, JwtUtil jwt) throws Exception {
    http.csrf(csrf -> csrf.disable());
    http.cors(cors -> {});                 // enable CORS support

    http.httpBasic(b -> b.disable());      // avoid Basic challenge
    http.formLogin(f -> f.disable());      // avoid login form

    http.addFilterBefore(new JwtAuthFilter(jwt), UsernamePasswordAuthenticationFilter.class);

    http.authorizeHttpRequests(reg -> reg
            // preflight
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // allow OpenAPI & helper endpoints
            .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui.html", "/swagger-ui/**",
                    "/actuator/**",
                    "/auth/login"
            ).permitAll()
            .anyRequest().authenticated()
    );

    return http.build();
  }
}
