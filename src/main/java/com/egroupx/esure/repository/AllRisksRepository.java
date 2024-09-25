package com.egroupx.esure.repository;

import com.egroupx.esure.model.AllRisks;
import com.egroupx.esure.model.Customer;
import com.egroupx.esure.model.PHShortTerm;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AllRisksRepository extends ReactiveCrudRepository<AllRisks,Long> {

    @Query("INSERT IGNORE INTO esure_all_risks SET fsp_quote_ref_id=:fspQouteRefId,item_description=:itemDescription,sum_insured=:sumInsured,cover_type_id=:coverTypeId")
    Mono<AllRisks> saveAllRisksDetails(Long fspQouteRefId,String itemDescription,String sumInsured,String coverTypeId);

    @Query("select * from esure_all_risks where fsp_quote_ref_id=:fspQuoteRef")
    Flux<AllRisks> getAllRisksByQuoteRefId(Long fspQuoteRef);
}
