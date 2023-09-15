package com.testtask;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails managerUser = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .roles("MANAGER")
                .build();

        UserDetails clientUser = User.withDefaultPasswordEncoder()
                .username("user")
                .password("1234")
                .roles("CLIENT")
                .build();

        return new InMemoryUserDetailsManager(managerUser, clientUser);
    }

    @Bean
    public SecurityFilterChain managerFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/api/products").hasAnyRole("MANAGER","CLIENT")
                        .requestMatchers(HttpMethod.POST, "/api/products").hasRole("MANAGER")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());
        http.csrf().disable();
        return http.build();
    }

}

