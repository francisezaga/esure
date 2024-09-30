package com.egroupx.esure.repository;

import com.egroupx.esure.model.MotorVehicles;
import com.egroupx.esure.model.medical_aid.MedicalAidMemberDetails;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MedicalAidRepository extends ReactiveCrudRepository<MotorVehicles,Long> {

    @Query("INSERT IGNORE INTO esure_medical_aid_member_details SET adults_count=:adultsCount,children_count=:childrenCount,full_name=:fullName,email=:email,phone_number=:phoneNumber,has_medical_aid=:hasMedicalAid,income_category=:incomeCategory,hospital_choice=:hospitalChoice,hospital_rates=:hospitalRates,day_to_day_cover_level=:dayToDayCoverLevel,doctor_choice=:doctorChoice,has_chronic_medication_requirements=:hasChronicMedicationRequirements,hospital_exclusions=:hospitalExclusions")
    Mono<MedicalAidMemberDetails> saveMedicalAidDetails(int adultsCount,int childrenCount,String fullName,String email,String phoneNumber,boolean hasMedicalAid,String incomeCategory,String hospitalChoice,String hospitalRates,String dayToDayCoverLevel,String doctorChoice,boolean hasChronicMedicationRequirements,String hospitalExclusions);

    @Query("SELECT * FROM esure_medical_aid_member_details where phone_number=:phoneNumber order by id desc limit 1")
    Mono<MedicalAidMemberDetails> findMedicalAidRecordByPhoneNumber(String phoneNumber);



}
