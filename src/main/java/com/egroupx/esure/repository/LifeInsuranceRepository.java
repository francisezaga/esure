package com.egroupx.esure.repository;

import com.egroupx.esure.model.Life;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LifeInsuranceRepository extends ReactiveCrudRepository<Life,Long> {
}
