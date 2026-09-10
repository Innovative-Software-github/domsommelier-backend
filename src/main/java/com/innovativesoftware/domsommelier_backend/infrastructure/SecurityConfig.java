package com.innovativesoftware.domsommelier_backend.infrastructure;

import com.innovativesoftware.domsommelier_backend.auth_management.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    // Полностью открытые пути (любой HTTP-метод)
    private static final String[] PUBLIC_PATHS = {
            "/api/v1/auth/**",
            "/api/v1/products/**",
            "/api/v1/saved/**",
            "/api/v1/basket/**",
            "/api/v1/event-orders/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            // Редирект-шорткат springdoc, отдельно от "/swagger-ui/**" — паттерн
            // не покрывает путь без "/" после "swagger-ui". Именно на него
            // проксирует /docs в infrastructure/nginx/nginx.conf.
            "/swagger-ui.html",
            "/dev/**",
            // Spring Boot 3 + Security 6 фильтруют ERROR-dispatch: без открытого /error
            // любое исключение контроллера (404/400/500) маскируется под 401.
            "/error"
    };

    // Открытые только для чтения (GET)
    private static final String[] PUBLIC_GET_PATHS = {
            "/api/v1/events/**",
            "/api/v1/news/**",
            "/api/v1/wine-stores/**",
            "/api/v1/cities/**",
            "/api/v1/filters/**",
            "/products/files/**",
            "/events/files/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_PATHS).permitAll()
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
