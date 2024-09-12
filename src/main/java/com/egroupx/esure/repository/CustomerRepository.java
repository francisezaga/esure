package com.egroupx.esure.repository;

import com.egroupx.esure.model.*;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer,Long> {

    @Query("INSERT IGNORE INTO customer SET fsp_quote_ref_id=:fspQouteRefId,fsp_ph_ref_id=:fspPhrefId,surname=:surname,initials=:initials,full_names=:fullNames,id_number=:idNumber,title_cd=:titleCd,gender_cd=:genderCd,birth_date=:birthDate,marital_status_cd=:maritalStatusCd,id_type=:idType,passport_number=:passportNumber")
    Mono<Customer> saveCustomerDetails(Long fspQouteRefId,Long fspPhrefId,String surname,String initials,String fullNames,String idNumber,String titleCd,String genderCd,LocalDate birthDate,String maritalStatusCd,String idType,String passportNumber);

    @Query("INSERT IGNORE INTO quote SET fsp_quote_ref_id=:fspQouteRefId,category_id=:categoryId,status=:status")
    Mono<Quotation> saveQuotationDetails(Long fspQouteRefId,String categoryId,String status);

    @Query("INSERT IGNORE INTO policy_holder SET fsp_quote_ref_id=:fspQouteRefId,fsp_ph_ref_id=:fspPhrefId,trust=:trust,is_private=:isPrivate,stakeholder_type=:stakeholderType")
    Mono<PolicyHolder> savePolicyHolderDetails(Long fspQouteRefId,Long fspPhrefId,String trust,String isPrivate,String stakeholderType);

    @Query("INSERT IGNORE INTO policy_holder_short_term SET fsp_quote_ref_id=:fspQoutRefId,fsp_ph_ref_id=:fspPhrefId,held_insurance_last_39_days=:heldInsuranceLast39Days,period_comp_car_insurance=:periodCompCarInsurance,period_comp_non_motor_insurance=:periodCompNonMotorInsurance,has_consent=:hasConsent")
    Mono<PHShortTerm> savePolicyHolderShortTermDetails(Long fspQoutRefId,Long fspPhrefId,String heldInsuranceLast39Days,String periodCompCarInsurance,String periodCompNonMotorInsurance,String hasConsent);

    @Query("INSERT IGNORE INTO policy_holder_short_term_lisense_detail SET fsp_quote_ref_id=:fspQouteRefId,fsp_ph_ref_id=:fspPhrefId,license_date=:licenseDate,license_category=:licenseCategory,license_type=:licenseType,vehicle_restriction=:vehicleRestriction")
    Mono<PHLicenseDetail> savePolicyHolderLicenseDetails(Long fspQouteRefId, Long fspPhrefId, LocalDate licenseDate, String licenseCategory, String licenseType, String vehicleRestriction);

    @Query("INSERT IGNORE INTO policy_holder_income_details SET fsp_quote_ref_id=:fspQouteRefId,fsp_ph_ref_id=:fspPhrefId,occupation_category=:occupationCategory")
    Mono<PHIncomeDetails> savePolicyHolderIncomeDetails(Long fspQouteRefId,Long fspPhrefId,String occupationCategory);

    @Query("select * from customer where id_number=:idNumber")
    Flux<Customer>  getCustomerRecordsByUserId(String idNumber);

    @Query("select * from quote where fsp_quote_ref_id=:fspQuoteRef")
    Mono<Quotation>  getQuotationByQuoteRefId(Long fspQuoteRef);

    @Query("select * from policy_holder where fsp_quote_ref_id=:fspQuoteRef")
    Flux<PolicyHolder>  getPolicyHolderByQuoteRefId(Long fspQuoteRef);

    @Query("select * from customer where fsp_quote_ref_id=:fspQuoteRef")
    Mono<Customer>  getCustomerByQuoteRefId(Long fspQuoteRef);

    @Query("select * from address where fsp_quote_ref_id=:fspQuoteRef")
    Mono<Address>  getAddressByQuoteRefId(Long fspQuoteRef);

    @Query("select * from tel_address where fsp_quote_ref_id=:fspQuoteRef")
    Mono<TelAddress>  getTelAddressByQuoteRefId(Long fspQuoteRef);

    @Query("select * from email_address where fsp_quote_ref_id=:fspQuoteRef")
    Mono<EmailAddress>  getEmailAddressByQuoteRefId(Long fspQuoteRef);

    @Query("select * from policy_holder_short_term where fsp_quote_ref_id=:fspQuoteRef")
    Mono<PHShortTerm>  getPHShortTermByQuoteRefId(Long fspQuoteRef);

    @Query("select * from policy_holder_income_details where fsp_quote_ref_id=:fspQuoteRef")
    Mono<PHIncomeDetails>  getPHIncomeDetailsByQuoteRefId(Long fspQuoteRef);

    @Query("select * from policy_holder_short_term_lisense_detail where fsp_quote_ref_id=:fspQuoteRef")
    Flux<PHLicenseDetail>  getPHLicenseDetailByQuoteRefId(Long fspQuoteRef);

}
