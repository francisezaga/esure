package com.egroupx.esure.repository;

import com.egroupx.esure.model.*;
import com.egroupx.esure.model.policies.Policy;
import com.egroupx.esure.model.policies.PolicyAdditionalInfo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer,Long> {

    @Query("INSERT IGNORE INTO esure_customer SET step='REQUESTED_QOUTE', fsp_quote_ref_id=:fspQouteRefId,fsp_ph_ref_id=:fspPhrefId,surname=:surname,initials=:initials,full_names=:fullNames,id_number=:idNumber,title_cd=:titleCd,gender_cd=:genderCd,birth_date=:birthDate,marital_status_cd=:maritalStatusCd,id_type=:idType,passport_number=:passportNumber")
    Mono<Customer> saveCustomerDetails(Long fspQouteRefId,Long fspPhrefId,String surname,String initials,String fullNames,String idNumber,String titleCd,String genderCd,LocalDate birthDate,String maritalStatusCd,String idType,String passportNumber);

    @Query("INSERT IGNORE INTO esure_quote SET fsp_quote_ref_id=:fspQouteRefId,category_id=:categoryId,status=:status")
    Mono<Quotation> saveQuotationDetails(Long fspQouteRefId,String categoryId,String status);

    @Query("INSERT IGNORE INTO esure_policy_holder SET fsp_quote_ref_id=:fspQouteRefId,fsp_ph_ref_id=:fspPhrefId,trust=:trust,is_private=:isPrivate,stakeholder_type=:stakeholderType")
    Mono<PolicyHolder> savePolicyHolderDetails(Long fspQouteRefId,Long fspPhrefId,String trust,String isPrivate,String stakeholderType);

    @Query("INSERT IGNORE INTO esure_policy_holder_short_term SET fsp_quote_ref_id=:fspQoutRefId,fsp_ph_ref_id=:fspPhrefId,held_insurance_last_39_days=:heldInsuranceLast39Days,period_comp_car_insurance=:periodCompCarInsurance,period_comp_non_motor_insurance=:periodCompNonMotorInsurance,has_consent=:hasConsent")
    Mono<PHShortTerm> savePolicyHolderShortTermDetails(Long fspQoutRefId,Long fspPhrefId,String heldInsuranceLast39Days,String periodCompCarInsurance,String periodCompNonMotorInsurance,String hasConsent);

    @Query("INSERT IGNORE INTO esure_policy_holder_short_term_lisense_detail SET fsp_quote_ref_id=:fspQouteRefId,fsp_ph_ref_id=:fspPhrefId,license_date=:licenseDate,license_category=:licenseCategory,license_type=:licenseType,vehicle_restriction=:vehicleRestriction")
    Mono<PHLicenseDetail> savePolicyHolderLicenseDetails(Long fspQouteRefId, Long fspPhrefId, LocalDate licenseDate, String licenseCategory, String licenseType, String vehicleRestriction);

    @Query("INSERT IGNORE INTO esure_policy_holder_income_details SET fsp_quote_ref_id=:fspQouteRefId,fsp_ph_ref_id=:fspPhrefId,occupation_category=:occupationCategory")
    Mono<PHIncomeDetails> savePolicyHolderIncomeDetails(Long fspQouteRefId,Long fspPhrefId,String occupationCategory);

    @Query("INSERT IGNORE INTO esure_address SET fsp_quote_ref_id=:fspQouteRefId,type_cd=:typeCd,line_1=:line1,code=:code,suburb=:suburb,residential_area_type=:residentialAreaType")
    Mono<Address>  saveAddressDetails(Long fspQouteRefId,String typeCd,String line1,String code,String suburb,String residentialAreaType);

    @Query("INSERT IGNORE INTO esure_tel_address SET fsp_quote_ref_id=:fspQouteRefId,type_cd=:typeCd,code=:code,number=:number,is_cellphone=:isCellphone,is_telephone=:isTelephone,is_business=:isBusiness,is_residential=:isResidential")
    Mono<TelAddress>  saveTelAddressDetails(Long fspQouteRefId,String typeCd,String code,String number,String isCellphone,String isTelephone,String isBusiness,String isResidential);

    @Query("INSERT IGNORE INTO esure_email_address SET fsp_quote_ref_id=:fspQouteRefId,type_cd=:typeCd,line_1=:line1")
    Mono<EmailAddress>  saveEmailAddressDetails(Long fspQouteRefId,String typeCd,String line1);

    @Query("INSERT IGNORE INTO esure_policy SET fsp_policy_id=:fspPolicyId,insurer_Id=:insurerId,category_id=:categoryId,date_quoted=:dateQuoted,status=:status,broker_code=:brokerCode,external_policy_no=:externalPolicyNo,quotation_id=:quotationId,quoted_premium=:quotedPremium,offering_id=:offeringId,offering_name=:offeringName,error_status=:errorStatus,id_number=:idNumber,e_sure_status=:eSureStatus ON DUPLICATE KEY UPDATE fsp_policy_id=:fspPolicyId,insurer_Id=:insurerId,category_id=:categoryId,date_quoted=:dateQuoted,status=:status,broker_code=:brokerCode,external_policy_no=:externalPolicyNo,quotation_id=:quotationId,quoted_premium=:quotedPremium,offering_id=:offeringId,offering_name=:offeringName,error_status=:errorStatus,id_number=:idNumber,e_sure_status=:eSureStatus")
    Mono<Policy> savePolicy(Long fspPolicyId, Long insurerId, int categoryId, LocalDateTime dateQuoted, String status, Long brokerCode, String externalPolicyNo, Long quotationId, double quotedPremium, Long offeringId, String offeringName, String errorStatus, String idNumber, String eSureStatus);

    @Query("INSERT IGNORE INTO esure_policy_additional_info SET fsp_quote_ref_id=:fspQuoteRefId, fsp_policy_id=:fspPolicyId,contact=:contact,instructions=:instructions ON DUPLICATE KEY UPDATE fsp_quote_ref_id=:fspQuoteRefId, fsp_policy_id=:fspPolicyId,contact=:contact,instructions=:instructions;")
    Mono<Policy> savePolicyAdditionalInfo(Long fspQouteRefId,Long fspPolicyId,String contact,String instructions);

    @Query("select * from esure_customer where id_number=:idNumber or passport_number=:idNumber")
    Flux<Customer>  getCustomerRecordsByUserId(String idNumber);

    @Query("select * from esure_customer where id_number=:idNumber or passport_number=:idNumber order by id desc limit 1")
    Mono<Customer>  findCustomerLastRecordsByUserIdNumber(String idNumber);

    @Query("select * from esure_customer where fsp_quote_ref_id=:fspQouteRefId order by id desc limit 1")
    Mono<Customer>  findCustomerLastRecordsByQuotationId(Long fspQouteRefId);

    @Query("select id,fsp_quote_ref_id,id_number,passport_number,step from esure_customer where id_number=:idNumber or passport_number=:idNumber order by id desc limit 1")
    Mono<CustomerStep> findCustomerLastRecordStepByUserIdNumber(String idNumber);

    @Query("UPDATE esure_customer SET name_space_scan_pass=:nameSpaceScanPass, name_space_scan_fail_reason=:nameSpaceScanFailReason, id_verification_pass=:idVerificationPass,id_verification_fail_reason=:idVerificationFailReason WHERE id=:id")
    Mono<Customer> updateCustomerKycDetails(boolean nameSpaceScanPass,String nameSpaceScanFailReason, boolean idVerificationPass,String idVerificationFailReason,Long id);

    @Query("UPDATE esure_customer SET step=:step WHERE id=:id")
    Mono<Customer> updateCustomerStepDetails(String step,Long id);

    @Query("select * from esure_quote where fsp_quote_ref_id=:fspQuoteRef")
    Mono<Quotation>  getQuotationByQuoteRefId(Long fspQuoteRef);

    @Query("select * from esure_policy_holder where fsp_quote_ref_id=:fspQuoteRef")
    Flux<PolicyHolder>  getPolicyHolderByQuoteRefId(Long fspQuoteRef);

    @Query("select * from esure_customer where fsp_quote_ref_id=:fspQuoteRef")
    Mono<Customer>  getCustomerByQuoteRefId(Long fspQuoteRef);

    @Query("select * from esure_address where fsp_quote_ref_id=:fspQuoteRef")
    Flux<Address>  getAddressByQuoteRefId(Long fspQuoteRef);

    @Query("select * from esure_tel_address where fsp_quote_ref_id=:fspQuoteRef")
    Flux<TelAddress>  getTelAddressByQuoteRefId(Long fspQuoteRef);

    @Query("select * from esure_email_address where fsp_quote_ref_id=:fspQuoteRef")
    Flux<EmailAddress>  getEmailAddressByQuoteRefId(Long fspQuoteRef);

    @Query("select * from esure_policy_holder_short_term where fsp_quote_ref_id=:fspQuoteRef")
    Mono<PHShortTerm>  getPHShortTermByQuoteRefId(Long fspQuoteRef);

    @Query("select * from esure_policy_holder_income_details where fsp_quote_ref_id=:fspQuoteRef")
    Mono<PHIncomeDetails>  getPHIncomeDetailsByQuoteRefId(Long fspQuoteRef);

    @Query("select * from esure_policy_holder_short_term_lisense_detail where fsp_quote_ref_id=:fspQuoteRef")
    Flux<PHLicenseDetail>  getPHLicenseDetailByQuoteRefId(Long fspQuoteRef);

    @Query("select * from esure_policy where id_number=:idNumber")
    Flux<Policy> getPoliciesByIdNumber(String idNumber);

    @Query("select * from esure_policy_additional_info where fsp_quote_ref_id=:fspQuoteRefId")
    Mono<PolicyAdditionalInfo> getPolicyAdditionalInfo(Long fspQouteRefId);


    @Query("SELECT esure_customer.full_names,esure_customer.fsp_quote_ref_id,esure_customer.id_number, esure_tel_address.code,esure_tel_address.number,esure_email_address.line_1 FROM esure_customer INNER JOIN esure_tel_address ON esure_customer.fsp_quote_ref_id = esure_tel_address.fsp_quote_ref_id INNER JOIN esure_email_address ON esure_tel_address.fsp_quote_ref_id = esure_email_address.fsp_quote_ref_id where esure_customer.fsp_quote_ref_id=:quoteRef order by esure_customer.id desc limit 1;")
    Mono<EmailCustomer> getCustomerEmailDetailsByQuoteRef(Long quoteRef);

    @Query("SELECT esure_policy.fsp_policy_id,esure_customer.full_names,esure_customer.fsp_quote_ref_id,esure_customer.id_number, esure_tel_address.code,esure_tel_address.number,esure_email_address.line_1 FROM esure_policy INNER JOIN esure_customer ON esure_policy.quotation_id = esure_customer.fsp_quote_ref_id INNER JOIN esure_tel_address ON esure_customer.fsp_quote_ref_id = esure_tel_address.fsp_quote_ref_id INNER JOIN esure_email_address ON esure_tel_address.fsp_quote_ref_id = esure_email_address.fsp_quote_ref_id where esure_policy.fsp_policy_id=:policyId order by esure_policy.id desc limit 1;")
    Mono<EmailCustomer> getCustomerEmailDetailsByPolicyId(Long policyId);


}
