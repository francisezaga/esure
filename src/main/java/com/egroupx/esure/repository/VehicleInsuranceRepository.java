package com.egroupx.esure.repository;

import com.egroupx.esure.model.*;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface VehicleInsuranceRepository extends ReactiveCrudRepository<MotorVehicles,Long> {

    @Query("INSERT IGNORE INTO motor_vehicles SET fsp_quote_ref_id=:fspQouteRefId,year=:year,make=:make,model=:model,car_colour=:carColor,metallic_paint=:metallicPaint,quotation_basis=:quotationBasis,alarm_type_id=:alarmTypeId,alarm_by_vesa=:alarmByVesa,tracing_device=:tracingDevice")
    Mono<MotorVehicles> saveVehicleDetails(Long fspQouteRefId,String year,String make,String model,String carColor,String metallicPaint,String quotationBasis,String alarmTypeId,String alarmByVesa,String tracingDevice);

    @Query("INSERT IGNORE INTO motor_vehicle_sh_link SET fsp_quote_ref_id=:fspQouteRefId,link_type_id=:linkTypeId,fsp_sh_link_ref_id=:fspShLinkRefId")
    Mono<SHLink> saveVehicleShLinkDetails(Long fspQouteRefId,String linkTypeId,Long fspShLinkRefId);

    @Query("INSERT IGNORE INTO motor_vehicle_short_term SET fsp_quote_ref_id=:fspQouteRefId,cover_type=:coverType,use_type_id=:useTypeId,flat_excess=:flatExcess,overnight_parking_cd=:overnightParkingCd,overnight_parking_type_locked=:overnightParkingTypeLocked")
    Mono<MotorVehicleShortTerm> saveVehicleShortTermDetails(Long fspQouteRefId,String coverType,String useTypeId,String flatExcess,String overnightParkingCd,String overnightParkingTypeLocked);

    @Query("select * from motor_vehicles where fsp_quote_ref_id=:fspQuoteRef")
    Flux<MotorVehicles>  getMotorVehiclesByQuoteRefId(Long fspQuoteRef);

    @Query("select * from motor_vehicle_short_term where fsp_quote_ref_id=:fspQuoteRef")
    Mono<MotorVehicleShortTerm>  getMotorVehicleShortTermByQuoteRefId(Long fspQuoteRef);

    @Query("select * from motor_vehicle_sh_link where fsp_quote_ref_id=:fspQuoteRef")
    Flux<SHLink> getMotorVehicleLinkByQuoteRefId(Long fspQuoteRef);
}
