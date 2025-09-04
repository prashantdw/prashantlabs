package com.prashantlabs.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
  @Bean
  public JwtUtil jwt(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.issuer}") String issuer){
    return new JwtUtil(secret, issuer);
  }

  @Bean
  public SecurityFilterChain chain(HttpSecurity http, JwtUtil jwt) throws Exception {
    http.csrf(c->c.disable());

    http.httpBasic(basic -> basic.disable());    // disable Basic auth
    http.formLogin(form -> form.disable());

    http.addFilterBefore(new JwtAuthFilter(jwt), UsernamePasswordAuthenticationFilter.class);
    http.authorizeHttpRequests(reg-> reg
      .requestMatchers("/actuator/**","/auth/login").permitAll()
      .requestMatchers("/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**","/proxy/docs/**","/webhooks/**").permitAll()
      .anyRequest().authenticated());
    return http.build();
  }
}
