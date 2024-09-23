package com.egroupx.esure.services;

import com.egroupx.esure.repository.AuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;

@Service
public class CustomUserDetailsService implements ReactiveUserDetailsService {


    private final AuthRepository authRepository;

    private final Logger LOG = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public Mono findByUsername(String username) {
        return authRepository.findByUsername(username).flatMap(appUser->{
                    UserDetails user = User.builder()
                            .username(appUser.getUsername().trim())
                            .password(appUser.getPassword().trim())
                            .build();
                    return Mono.just(user);
                })
                .switchIfEmpty(Mono.defer(() ->{
                    LOG.error(MessageFormat.format("Unknown API consumer {0}",username));
                    return Mono.error(new UsernameNotFoundException("User Not Found"));
                }));

    }

}
