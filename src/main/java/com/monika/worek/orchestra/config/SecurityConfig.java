package com.monika.worek.orchestra.config;

import com.monika.worek.orchestra.service.TwoFactorAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private final TwoFactorAuthService twoFactorAuthService;

    public SecurityConfig(TwoFactorAuthService twoFactorAuthService) {
        this.twoFactorAuthService = twoFactorAuthService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/login", "/forgot-password", "/reset-password", "/set-password", "/error").permitAll()
                .requestMatchers("/inspectorPage").hasRole("INSPECTOR")
                .requestMatchers("/adminPage", "/admin/**").hasRole("ADMIN")
                .requestMatchers("/inspector/**").hasAnyRole("ADMIN", "INSPECTOR")
                .anyRequest().authenticated());

        http.formLogin(login -> login
                .loginPage("/login").permitAll()
                .successHandler(customAuthenticationSuccessHandler()));

        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout/**", HttpMethod.GET.name()))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true));


        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String userEmail = authentication.getName();
            twoFactorAuthService.sendVerificationCode(userEmail);
            response.sendRedirect("/2fa-page?email=" + userEmail);
        };
    }
}
