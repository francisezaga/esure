package com.egroupx.esure.repository;

import com.egroupx.esure.model.Home;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeInsuranceRepository extends ReactiveCrudRepository<Home,Long> {
}
