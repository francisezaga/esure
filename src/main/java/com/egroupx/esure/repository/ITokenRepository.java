package com.egroupx.esure.repository;

import com.egroupx.esure.dto.auth.Token;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ITokenRepository extends ReactiveCrudRepository<Token, UUID> {

    @Query("SELECT Token FROM api_merchant WHERE MerchantID='SECURECITIZEN'")
    Mono<String> findCitizenAPIToken();

}
