package com.egroupx.esure.repository;

import com.egroupx.esure.model.MotorVehicles;
import com.egroupx.esure.model.medical_aid.MedicalAidMemberDetails;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface MedicalAidRepository extends ReactiveCrudRepository<MotorVehicles,Long> {

    @Query("INSERT IGNORE INTO esure_medical_aid_member_details SET adults_count=:adultsCount,children_count=:childrenCount,first_name=:firstName,last_name=:lastName,email=:email,phone_number=:phoneNumber,date_of_birth=:dateOfBirth,has_medical_aid=:hasMedicalAid,name_of_medical_aid_provider=:nameOfMedicalAidProvider,is_gross_income_more_than_14k=:isGrossIncomeMorethan14k,budgeted_amount=:budgetedAmount,medical_priority=:medicalPriority,is_net_income_more_than_14k=:isNetIncomeMoreThan14k,has_or_dependent_has_chronic_medication_requirements=:hasOrDependentHasChronicMedicationRequirements")
    Mono<MedicalAidMemberDetails> saveMedicalAidDetails(int adultsCount, int childrenCount, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth, boolean hasMedicalAid,String nameOfMedicalAidProvider,boolean isGrossIncomeMorethan14k,String budgetedAmount,String medicalPriority, boolean isNetIncomeMoreThan14k, boolean hasOrDependentHasChronicMedicationRequirements);

    @Query("SELECT * FROM esure_medical_aid_member_details where phone_number=:phoneNumber order by id desc limit 1")
    Mono<MedicalAidMemberDetails> findMedicalAidRecordByPhoneNumber(String phoneNumber);

}
