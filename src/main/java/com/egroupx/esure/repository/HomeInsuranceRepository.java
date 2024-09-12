package com.egroupx.esure.repository;

import com.egroupx.esure.model.AllRisks;
import com.egroupx.esure.model.Building;
import com.egroupx.esure.model.HouseHoldContents;
import com.egroupx.esure.model.SHLink;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface HomeInsuranceRepository extends ReactiveCrudRepository<Building,Long> {

    @Query("INSERT IGNORE INTO buildings SET fsp_quote_ref_id=:fspQouteRefId,description=:description,cover_type=:coverType,construction=:construction,roof_construction=:roofConstruction,sum_insured=:sumInsured,recent_loss_count=:recentLossCount,property_owned_claim=:propertyOwnedclaim")
    Mono<Building> saveBuildingDetails(Long fspQouteRefId,String description,String coverType,String construction,String roofConstruction,String sumInsured,String recentLossCount,String propertyOwnedclaim);

    @Query("INSERT IGNORE INTO buildings_sh_link SET fsp_quote_ref_id=:fspQouteRefId,link_type_id=:linkTypeId,fsp_sh_link_ref_id=:fspShLinkRefId")
    Mono<SHLink> saveBuildingShLinkDetails(Long fspQouteRefId,String linkTypeId,Long fspShLinkRefId);

    @Query("INSERT IGNORE INTO household_contents SET fsp_quote_ref_id=:fspQouteRefId,description=:description,sum_insured=:sumInsured,restricted_cover=:restrictedCover,unocc_period=:unoccPeriod,unrelated_count=:unrelatedCount,standard_walls=:standardWalls,thatched_roof=:thatchedRoof,burglar_bars=:burglarBars,security_gates=:securityGates,alarm_in_working_order=:alarmInWorkingOrder,recent_loss_count=:recentLossCount,property_owned_claim=:propertyOwnedClaim")
    Mono<HouseHoldContents> saveHouseHoldContentsDetails(Long fspQouteRefId,String description,String sumInsured,String restrictedCover,String unoccPeriod,String unrelatedCount,String standardWalls,String thatchedRoof,String burglarBars,String securityGates,String alarmInWorkingOrder,String recentLossCount,String propertyOwnedClaim);

    @Query("INSERT IGNORE INTO household_sh_link SET fsp_quote_ref_id=:fspQouteRefId,link_type_id=:linkTypeId,fsp_sh_link_ref_id=:fspShLinkRefId")
    Mono<SHLink> saveHouseHoldShLinkDetails(Long fspQouteRefId,String linkTypeId,Long fspShLinkRefId);

    @Query("select * from buildings where fsp_quote_ref_id=:fspQuoteRef")
    Flux<Building> getBuildingByQuoteRefId(Long fspQuoteRef);

    @Query("select * from buildings_sh_link where fsp_quote_ref_id=:fspQuoteRef")
    Flux<SHLink>  getBuildingShLinkByQuoteRefId(Long fspQuoteRef);

    @Query("select * from household_contents where fsp_quote_ref_id=:fspQuoteRef")
    Flux<HouseHoldContents>  getHouseHoldContentsByQuoteRefId(Long fspQuoteRef);

    @Query("select * from household_sh_link where fsp_quote_ref_id=:fspQuoteRef")
    Flux<SHLink>  getHouseHoldShLinkByQuoteRefId(Long fspQuoteRef);
}
