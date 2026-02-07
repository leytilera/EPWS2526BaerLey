package de.thkoeln.chessfed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import de.thkoeln.chessfed.services.LocalUserService;

@Configuration
public class SecurityConfig {
    
    @Autowired
    private LocalUserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/").authenticated();
                auth.requestMatchers("/api/**").authenticated();
                auth.anyRequest().permitAll();
            })
            .oauth2Login(login -> {
                login.permitAll();
                login.defaultSuccessUrl("/");
                login.userInfoEndpoint((userInfo) -> userInfo.oidcUserService(userService));
            });
        return http.build();
    }

}
