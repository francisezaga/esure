package com.egroupx.esure.repository;

import com.egroupx.esure.dto.life.*;
import com.egroupx.esure.model.life.*;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
public interface LifeInsuranceRepository extends ReactiveCrudRepository<MemberDTO,Long> {

    @Query("INSERT IGNORE INTO esure_members SET step='ACCOUNT_CREATED', pol_360_main_member_id=:pol360RefId, client=:client, agent_code=:agentCode, policy_number=:policyNumber, broker_code=:brokerCode, title=:title, first_name=:firstName, surname=:surname, id_number=:idNumber, gender=:gender,date_of_birth=:dateOfBirth, age=:age, cell_number=:cellNumber, alt_cell_number=:altCellNumber, work_number=:workNumber, home_number=:homeNumber, email=:email, fax=:fax, contact_type=:contactType, postal_address1=:postalAddress1, postal_address2=:postalAddress2, postal_address3=:postalAddress3, postal_code=:postalCode, residential_address1=:residentialAddress1, residential_address2=:residentialAddress2, residential_address3=:residentialAddress3, residential_code=:residentialCode, member_type=:memberType, premium=:premium, cover=:cover, add_policy_id=:addPolicyId, status_code=:statusCode")
    Mono<MemberDTO> saveMember(String pol360RefId, String client, String agentCode, String policyNumber, String brokerCode, String title, String firstName, String surname, String idNumber, String gender, LocalDate dateOfBirth, String age, String cellNumber, String altCellNumber, String workNumber, String homeNumber, String email, String fax, String contactType, String postalAddress1, String postalAddress2, String postalAddress3, String postalCode, String residentialAddress1, String residentialAddress2, String residentialAddress3, String residentialCode, String memberType, String premium, String cover, String addPolicyId, String statusCode);

    @Query("INSERT IGNORE INTO esure_spouses SET  client=:client, title=:title, firstName=:first_name, surname=:surname, id_number=:idNumber, gender=:gender,date_of_birth=:dateOfBirth, age=:age, main_member_id=:mainMemberId, policy_number=:policyNumber")
    Mono<SpouseDTO> saveSpouse(String client, String title, String firstName, String surname, String idNumber, String gender, LocalDate dateOfBirth, String age, String mainMemberId, String policyNumber);

    @Query("INSERT IGNORE INTO esure_ext_dependents SET client=:client, title =:title, first_name=:firstName, surname=:surname, id_number=:idNumber, gender=:gender,date_of_birth=:dateOfBirth, age=:age, main_member_id=:mainMemberId, policy_number=:policyNumber, relation=:relation")
    Mono<ExtendedMemberDTO> saveExtendedDependent(String client, String title, String firstName, String surname, String idNumber, String gender, LocalDate dateOfBirth, String age, String mainMemberId, String policyNumber,String relation);

    @Query("INSERT IGNORE INTO esure_dependents SET client=:client, title=:title, first_name=:firstName, surname=:surname, id_number=:idNumber, gender=:gender,date_of_birth=:dateOfBirth, age=:age, main_member_id=:mainMemberID, policy_number=:policyNumber")
    Mono<DependentDTO> saveDependent(String client, String title, String firstName, String surname, String idNumber, String gender, LocalDate dateOfBirth, String age, String mainMemberID, String policyNumber);

    @Query("INSERT IGNORE INTO esure_beneficiaries SET policy_number=:policyNumber, client=:client, contact_cell=:contactCell, contact_work_tell=:contactWorkTell, contact_home_tell=:contactHomeTell, contact_fax=:contactFax, contact_email=:contactEmail, session_user_id=:sessionUserID, pref_type=:prefType, ben_last_name=:benLastName, ben_first_name=:benFirstName, ben_id_number=:benIdNumber, ben_percentage=:benPercentage ,ben_dob=:benDOB, ben_relation=:benRelation, ben_type=:benType, title=:title,main_member_id=:mainMemberID")
    Mono<BeneficiaryDTO> saveBeneficiary(String policyNumber, String client, String contactCell, String contactWorkTell, String contactHomeTell, String contactFax, String contactEmail, String sessionUserID, String prefType, String benLastName, String benFirstName, String benIdNumber, String benPercentage, LocalDate benDOB, String benRelation, String benType, String title, String mainMemberID);

    @Query("INSERT IGNORE INTO esure_bank_details SET client=:client, policy_number=:policyNumber, session_user_id=:sessionUserID, branch_code=:branchCode, acc_number=:accNumber, acc_type=:accType, acc_name=:accName, ded_day=:dedDay,from_date=:fromDate, id_number=:idNumber, sub_naedo=:subNaedo")
    Mono<BankDetailsDTO> saveBankDetails(String client, String policyNumber, String sessionUserID, String branchCode, String accNumber, String accType, String accName, String dedDay, LocalDate fromDate, String idNumber, String subNaedo);

    @Query("SELECT * FROM esure_members WHERE id_number=:idNumber")
    Flux<Member> findMemberByIdNumber(String idNumber);

    @Query("SELECT * FROM esure_members WHERE pol_360_main_member_id=:memberId order by id desc limit 1")
    Mono<Member> findMemberLastRecordByMainMemberNumber(Long memberId);

    @Query("SELECT * FROM esure_members WHERE id_number=:idNumber order by id desc limit 1")
    Mono<Member> findMemberLastRecordByIdNumber(String idNumber);

    @Query("SELECT id,pol_360_main_member_id,id_number,step FROM esure_members WHERE pol_360_main_member_id=:memberId order by id desc limit 1")
    Mono<MemberStep> findMemberLastRecordStepByIdNumber(Long memberId);

    @Query("UPDATE esure_members SET name_space_scan_pass=:nameSpaceScanPass, name_space_scan_fail_reason=:nameSpaceScanFailReason, id_verification_pass=:idVerificationPass,id_verification_fail_reason=:idVerificationFailReason WHERE id=:id")
    Mono<Member> updateMemberKycDetails(boolean nameSpaceScanPass,String nameSpaceScanFailReason, boolean idVerificationPass,String idVerificationFailReason,Long id);

    @Query("UPDATE esure_members SET step=:step WHERE id=:id")
    Mono<Member> updateMemberStepDetails(String step,Long id);

    @Query("SELECT * FROM esure_spouses WHERE  main_member_id=:mainMemberId")
    Flux<Spouse> findSpouseByIdNumber(String mainMemberId);

    @Query("SELECT * FROM esure_ext_dependents WHERE  main_member_id=:mainMemberId")
    Flux<ExtendedMember> findExtendedMemberByIdNumber(String mainMemberId);

    @Query("SELECT * FROM esure_dependents WHERE  main_member_id=:mainMemberId")
    Flux<Dependent> findDependentByIdNumber(String mainMemberId);

    @Query("SELECT * FROM esure_beneficiaries WHERE  main_member_id=:mainMemberId")
    Flux<Beneficiary> findBeneficiaryByIdNumber(String mainMemberId);

    @Query("SELECT * FROM esure_bank_details WHERE id_number=:idNumber")
    Flux<BankDetails> findBankDetailsByIdNumber(String idNumber);

}
