package com.telran.securityservice.config;

import com.telran.securityservice.authorization_manager.OwnerAuthorizationManager;
import com.telran.securityservice.authorization_manager.OwnerOrPrincipalAuthorizationManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.springframework.security.web.csrf.CsrfTokenRepository;
//import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static com.telran.securityservice.dto.Roles.PRINCIPAL;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtRequestFilter jwtRequestFilter;
    private final OwnerOrPrincipalAuthorizationManager ownerOrPrincipalAuthorizationManager;
    private final OwnerAuthorizationManager ownerAuthorizationManager;
    private final UserConfig userConfig;
    private final ExpiredPasswordFilter expiredPasswordFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(HttpMethod.GET, "/auth/csrf").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.GET, "/auth/id/{id}", "/auth/login/{login}").access(ownerOrPrincipalAuthorizationManager)
                        .requestMatchers(HttpMethod.POST, "/auth/account", "/auth/rollback").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.POST, "/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.POST, "/auth", "/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/auth/{id}").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.PUT, "/auth/login/{id}", "/auth/password/{id}").access(ownerAuthorizationManager)
                        .requestMatchers(HttpMethod.GET, "/auth/ping").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/subscribe/{clientId}").authenticated()

                        .requestMatchers(HttpMethod.GET, "/branches", "/branches/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/branches").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.DELETE, "/branches/{id}").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.PUT, "/branches/{id}").hasRole(PRINCIPAL.toString())

                        .requestMatchers(HttpMethod.GET, "/contacts", "/contacts/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/contacts", "/contacts/promote/{id}").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/contacts/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/contacts/{id}", "/contacts/archive/{id}/{reason}").authenticated()

                        .requestMatchers(HttpMethod.GET, "/courses", "/courses/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/courses").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.DELETE, "/courses/{id}").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.PUT, "/courses/{id}").hasRole(PRINCIPAL.toString())

                        .requestMatchers(HttpMethod.GET, "/groups", "/groups/{id}", "/groups/paginated").authenticated()
                        .requestMatchers(HttpMethod.POST, "/groups", "/groups/students/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/groups/{id}").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.PUT, "groups/{id}", "/groups/graduate/{id}", "/groups/{fromId}/move/{toId}",
                                "/groups/archive/students/{id}", "/groups/lecturers/{id}").authenticated()

                        .requestMatchers(HttpMethod.GET, "/lecturers", "/lecturers/{id}", "/lecturers/paginated").authenticated()
                        .requestMatchers(HttpMethod.POST, "/lecturers").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.DELETE, "/lecturers/{id}").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.PUT, "/lecturers/{id}", "/lecturers/archive/{id}/{reason}").authenticated()

                        .requestMatchers(HttpMethod.GET, "/logs/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/logs/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/logs/{id}").authenticated()

                        .requestMatchers(HttpMethod.GET, "/notifications/{entityType}/{id}", "/notifications/entityTypes", "/notifications/subscribe/{clientId}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/notifications/{entityType}/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/notifications/{entityType}", "/notifications").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/notifications/{entityType}/{id}").authenticated()

                        .requestMatchers(HttpMethod.GET, "/payments/paymentid/{id}", "/payments/studentid/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/payments").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/payments/{id}").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.PUT, "/payments/{id}", "/payments/archive/{id}").authenticated()

                        .requestMatchers(HttpMethod.GET, "/payment_types/{id}", "/payment_types").authenticated()

                        .requestMatchers(HttpMethod.GET, "/statuses", "/statuses/{statusId}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/statuses").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.DELETE, "/statuses/{id}").hasRole(PRINCIPAL.toString())
                        .requestMatchers(HttpMethod.PUT, "/statuses/{id}").hasRole(PRINCIPAL.toString())

                        .requestMatchers(HttpMethod.GET, "/students", "/students/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/students").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/students/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/students/{id}", "/students/archive/{id}/{reason}", "/students/graduate/{id}").authenticated()

                        .requestMatchers(HttpMethod.GET, "/weekdays", "/weekdays/{id}").authenticated()

                        .anyRequest().denyAll()
        );

//        http.csrf(csrf -> {
//            csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/auth/subscribe/**"));
//            csrf.csrfTokenRepository(csrfTokenRepository());
//        });
//        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(cors -> corsConfigurationSource());
        http.addFilterBefore(expiredPasswordFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://10.0.0.6:3000", "https://vp-licence.ru", "http://vp-licence.ru", "https://5.35.89.231", "http://5.35.89.231"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

//    @Bean
//    public CsrfTokenRepository csrfTokenRepository() {
//        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
//        repository.setHeaderName("X-CSRF-TOKEN");
//        return repository;
//    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userConfig);
        return daoAuthenticationProvider;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

}
