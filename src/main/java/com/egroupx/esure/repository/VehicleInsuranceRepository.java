package com.egroupx.esure.repository;

import com.egroupx.esure.model.Car;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleInsuranceRepository extends ReactiveCrudRepository<Car,Long> {
}
