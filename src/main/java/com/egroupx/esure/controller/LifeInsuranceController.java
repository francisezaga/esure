package com.egroupx.esure.controller;

import com.egroupx.esure.dto.life.*;
import com.egroupx.esure.dto.v360.ProductDTO;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.services.KYCVerificationService;
import com.egroupx.esure.services.LifeInsuranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/esure/life")
public class LifeInsuranceController {

    private final LifeInsuranceService lifeInsuranceService;

    private final KYCVerificationService kycVerificationService;

    public LifeInsuranceController(LifeInsuranceService lifeInsuranceService, KYCVerificationService kycVerificationService) {
        this.lifeInsuranceService = lifeInsuranceService;
        this.kycVerificationService = kycVerificationService;
    }

    @PostMapping(value = {"/createMember"})
    public Mono<ResponseEntity<APIResponse>> createMember(@RequestBody MemberDTO member)  {
        return lifeInsuranceService.createMember(member);
    }

    @PostMapping(value = {"/saveMemberPersonalDetails"})
    public Mono<ResponseEntity<APIResponse>> saveMemberPersonalDetails(@RequestBody MemberDTO member)  {
        return lifeInsuranceService.saveMemberPersonalDetails(member);
    }

    @PostMapping(value = {"/createBeneficiary"})
    public Mono<ResponseEntity<APIResponse>> createBeneficiary(@RequestBody BeneficiaryDTO beneficiary)  {
        return lifeInsuranceService.createBeneficiary(beneficiary);
    }

    @PostMapping(value = {"/createDependent"})
    public Mono<ResponseEntity<APIResponse>> createDependent(@RequestBody DependentDTO dependent)  {
        return lifeInsuranceService.createDependent(dependent);
    }

    @PostMapping(value = {"/createExtendedMember"})
    public Mono<ResponseEntity<APIResponse>> createExMember(@RequestBody ExtendedMemberDTO extendedMember)  {
        return lifeInsuranceService.createExtendedMember(extendedMember);
    }
    @PostMapping(value = {"/createSpouse"})
    public Mono<ResponseEntity<APIResponse>> createSpouse(@RequestBody SpouseDTO spouse)  {
        return lifeInsuranceService.createSpouse(spouse);
    }

    @PostMapping(value = {"/sendSMSFromPol360"})
    public Mono<ResponseEntity<APIResponse>> sendSMSFromPol360(@RequestBody SendPolicySMSDTO sendPolicySMSDTO)  {
        return lifeInsuranceService.sendSMSFromPol360(sendPolicySMSDTO);
    }

    @GetMapping(value = {"/viewPolicyDocument/{clientName}/{relatedId}"})
    public Mono<ResponseEntity<APIResponse>> viewPolicyDocument(@PathVariable String clientName,@PathVariable String relatedId)  {
        return lifeInsuranceService.viewPolicyDocument(clientName,relatedId);
    }

    @GetMapping(value = {"/downloadPolicyDocument/{clientName}/{relatedId}"})
    public Mono<ResponseEntity<?>> downloadPolicyDocument(@PathVariable String clientName,@PathVariable String relatedId)  {
        return lifeInsuranceService.downloadPolicyDocument(clientName,relatedId);
    }

    @PostMapping(value = {"/sendPol360SMSForPolicyDocLink/{clientName}/{relatedId}"})
    public Mono<ResponseEntity<APIResponse>> sendPol360SMSForPolicyDocLink(@PathVariable String clientName,@PathVariable String relatedId)  {
        return lifeInsuranceService.sendPol360SMSForPolicyDocLink(clientName,relatedId);
    }

    @PostMapping(value = {"/sendPol360SMSReferAFriend/{clientName}/{relatedId}"})
    public Mono<ResponseEntity<APIResponse>> sendPol360SMSReferAFriend(@PathVariable String clientName,@PathVariable String relatedId,@RequestBody String[] cellNumbers)  {
        return lifeInsuranceService.sendPol360SMSReferAFriend(clientName,relatedId,cellNumbers);
    }

    @PostMapping(value = {"/sendESureSMSPolicyDocLink/{clientName}/{relatedId}"})
    public Mono<ResponseEntity<APIResponse>> sendESureSMSPolicyDocLink(@PathVariable String clientName,@PathVariable String relatedId)  {
        return lifeInsuranceService.sendESureSMSForPolicyDocLink(clientName,relatedId);
    }

    @GetMapping(value = {"/getPayAt/{clientName}/{policyNumber}"})
    public Mono<ResponseEntity<APIResponse>> getPayAt(@PathVariable String clientName,@PathVariable String policyNumber)  {
        return lifeInsuranceService.getPayAt(clientName,policyNumber);
    }

    @GetMapping(value = {"/getSanctionScreening/{name}"})
    public Mono<ResponseEntity<APIResponse>> getSanctionScreening(@PathVariable String name)  {
        return lifeInsuranceService.getSanctionScreening(name);
    }

    @PostMapping(value = {"/saveKYCMemberReport/{memberId}"})
    public Mono<ResponseEntity<APIResponse>> saveKYCMemberReport(@PathVariable String memberId)  {
        return lifeInsuranceService.saveKYCMemberReport(memberId);
    }

    @GetMapping(value = {"/getKYCMemberReport/{memberId}"})
    public Mono<ResponseEntity<APIResponse>> getKYCMemberReport(@PathVariable String memberId)  {
        return lifeInsuranceService.getMemberKYCDetails(memberId);
    }

    @PostMapping(value = {"/addBankDetails"})
    public Mono<ResponseEntity<APIResponse>> addBankDetails(@RequestBody BankDetailsDTO bankDetails)  {
        return lifeInsuranceService.addBankDetails(bankDetails);
    }

    @PostMapping(value = {"/findProductByCode"})
    public Mono<ResponseEntity<APIResponse>> findProductByCode(@RequestBody ProductDTO productDTO)  {
        return lifeInsuranceService.findProductsByCode(productDTO);
    }

    @GetMapping(value = {"/getUserLifeCoverRecord/{idNumber}"})
    public Mono<ResponseEntity<APIResponse>> getQuotationsByUserId(@PathVariable String idNumber)  {
        return lifeInsuranceService.getLifeInsuranceByUserId(idNumber).flatMap(apiRes->{
            return Mono.just(ResponseEntity.ok().body(apiRes));
        });
    }

    @GetMapping(value = {"/getMemberStep/{idNumber}"})
    public Mono<ResponseEntity<APIResponse>> geMemberStep(@PathVariable String idNumber)  {
        return lifeInsuranceService.getMemberStep(idNumber);
    }

    @PostMapping(value = {"/submitMemberDetails/{memberId}"})
    public Mono<ResponseEntity<APIResponse>> submitMemberDetails(@PathVariable Long memberId)  {
        return kycVerificationService.verifyPersonalDetailsForLifeCover(memberId);
    }

    @PostMapping(value = {"/uploadMemberDoc"},produces="application/json")
    public Mono<ResponseEntity<APIResponse>> uploadMemberDoc(@RequestPart("MemberID") String mainMemberId,@RequestPart("PolicyNumber") String policyNumber,@RequestPart("ClientName") String clientName,@RequestPart("Function") String function,@RequestPart("File") Mono<FilePart> filePartMono,@RequestPart("DocType") String docType){
        return lifeInsuranceService.saveMemberDocument(mainMemberId,policyNumber,clientName,function,filePartMono,docType);
    }
}
