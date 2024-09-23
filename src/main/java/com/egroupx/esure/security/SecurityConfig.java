package com.egroupx.esure.security;

import com.egroupx.esure.services.AuthService;
import com.egroupx.esure.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                                .pathMatchers(
                                        "/apic-docs/**").permitAll()
                                .anyExchange().authenticated()
                )
                .httpBasic(withDefaults())
                .build();
    }

    @Bean
    PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

   /* @Bean
    public MapReactiveUserDetailsService userDetailsService() {
       UserDetails user = User.builder()
                .username("user")
                .password("{noop}pwd")
                .build();
        return new MapReactiveUserDetailsService(user);
    }*/

    /*@Bean
    public UserDetailsService userDetailsService(){
        return customUserDetailsService
    }*/

}
