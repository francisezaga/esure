package com.egroupx.esure.services;

import com.egroupx.esure.model.Car;
import com.egroupx.esure.model.Quotation;

import com.egroupx.esure.model.responses.APIResponse;
import com.egroupx.esure.model.responses.QuotationResponse;
import com.egroupx.esure.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Base64;

@Service
public class QuotationService {

    @Value("${egroupx.services.fspEndpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fspAPIKey:}")
    private String fspAPIKey;

    private WebClient webClient;

    private final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    private void setConfigs(String endpointUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(endpointUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " +  Base64.getEncoder().encodeToString((fspAPIKey+":"+"").getBytes()))
                .build();
    }

    public Mono<ResponseEntity<APIResponse>> requestQuotation(Quotation quotation) {
        setConfigs(fspEndpointUrl);

        String req = "{\n" +
                "    \"Category_Id\": 1,\n" +
                "    \"PolicyHolder\": [\n" +
                "        {\n" +
                "            \"id\": 999999,\n" +
                "            \"private-or-org\": \"1\",\n" +
                "            \"person\": {\n" +
                "                \"name\": \"TEST Name\",\n" +
                "                \"full-names\": \"TEST\",\n" +
                "                \"surname\": \"Name\",\n" +
                "                \"initials\": \"TN\",\n" +
                "                \"identification-no\": \"8705065000001\",\n" +
                "                \"title-cd\": \"1\",\n" +
                "                \"gender-cd\": \"1\",\n" +
                "                \"marital-status-cd\": \"2\",\n" +
                "                \"id-type\": \"1\"\n" +
                "            },\n" +
                "            \"short-term\": {\n" +
                "                \"held-insurance-last-39-days\": \"1\",\n" +
                "                \"period-comp-car-insurance\": \"0\",\n" +
                "                \"period-comp-nonmotor-insurance\": \"0\",\n" +
                "                \"has-consent\": \"1\",\n" +
                "                \"license-detail\": [\n" +
                "                    {\n" +
                "                        \"license-date\": \"01/01/2007\",\n" +
                "                        \"license-category\": \"1\",\n" +
                "                        \"license-type\": \"4\",\n" +
                "                        \"vehicle-restriction\": \"1\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            },\n" +
                "            \"income-details\": {\n" +
                "                \"occupation-category\": \"0\"\n" +
                "            },\n" +
                "            \"address\": [\n" +
                "                {\n" +
                "                    \"type-cd\": \"PHY\",\n" +
                "                    \"line-1\": \"67 ABC Road\",\n" +
                "                    \"code\": \"2191\",\n" +
                "                    \"suburb\": \"RIVONIA\",\n" +
                "                    \"residential-area-type\": \"1\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type-cd\": \"POS\",\n" +
                "                    \"line-1\": \"67 ABC Road\",\n" +
                "                    \"code\": \"2191\",\n" +
                "                    \"suburb\": \"RIVONIA\",\n" +
                "                    \"residential-area-type\": \"1\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type-cd\": \"TEL\",\n" +
                "                    \"code\": \"011\",\n" +
                "                    \"number\": \"8071030\",\n" +
                "                    \"is-cellphone\": \"0\",\n" +
                "                    \"is-telephone\": \"1\",\n" +
                "                    \"is-business\": \"1\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type-cd\": \"TEL\",\n" +
                "                    \"code\": \"011\",\n" +
                "                    \"number\": \"8071030\",\n" +
                "                    \"is-cellphone\": \"0\",\n" +
                "                    \"is-telephone\": \"1\",\n" +
                "                    \"is-residential\": \"1\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type-cd\": \"TEL\",\n" +
                "                    \"code\": \"011\",\n" +
                "                    \"number\": \"8071030\",\n" +
                "                    \"is-cellphone\": \"1\",\n" +
                "                    \"is-telephone\": \"0\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type-cd\": \"ELT\",\n" +
                "                    \"line-1\": \"test@test.co.za\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"AllRisks\": [\n" +
                "        {\n" +
                "            \"item-description\": \"Bicycle - T123456\",\n" +
                "            \"sum.insured\": \"15000\",\n" +
                "            \"cover.type.id\": \"200\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"Buildings\": [\n" +
                "        {\n" +
                "            \"sum.insured\": \"1000000\",\n" +
                "            \"description\": \"0\",\n" +
                "            \"cover.type\": \"0\",\n" +
                "            \"roof.construction\": \"0\",\n" +
                "            \"construction\": \"0\",\n" +
                "            \"geysercover\": \"0\",\n" +
                "            \"recent.loss.count\": \"0\",\n" +
                "            \"property-owned-claim\": \"0\",\n" +
                "            \"sh-link\": [\n" +
                "                {\n" +
                "                    \"id\": 999999,\n" +
                "                    \"link-type-id\": \"SHRiskAddr\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"HouseholdContents\": [\n" +
                "        {\n" +
                "            \"sum.insured\": \"100000\",\n" +
                "            \"description\": \"0\",\n" +
                "            \"restricted.cover\": \"0\",\n" +
                "            \"unocc.period\": \"1\",\n" +
                "            \"unrelated.count\": \"0\",\n" +
                "            \"standard.walls\": \"0\",\n" +
                "            \"thatched.roof\": \"0\",\n" +
                "            \"burglar.bars\": \"1\",\n" +
                "            \"security.gates\": \"1\",\n" +
                "            \"recent.loss.count\": \"0\",\n" +
                "            \"property-owned-claim\": \"0\",\n" +
                "            \"sh-link\": [\n" +
                "                {\n" +
                "                    \"id\": 999999,\n" +
                "                    \"link-type-id\": \"SHRiskAddr\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"MotorVehicles\": [\n" +
                "        {\n" +
                "            \"year\": \"2019\",\n" +
                "            \"make\": \"050\",\n" +
                "            \"model\": \"05011308\",\n" +
                "            \"car-colour\": \"17\",\n" +
                "            \"metalic-paint\": \"0\",\n" +
                "            \"quotation-basis\": \"3\",\n" +
                "            \"alarm-type-id\": \"FFA1996\",\n" +
                "            \"alarm-by-vesa\": \"1\",\n" +
                "            \"tracing-device\": \"VESA\",\n" +
                "            \"short-term\": {\n" +
                "                \"cover-type\": \"0\",\n" +
                "                \"use-type-id\": \"0\",\n" +
                "                \"flat-excess\": \"-1\",\n" +
                "                \"overnight-parking-cd\": \"2\",\n" +
                "                \"overnight-parking-type-locked\": \"1\"\n" +
                "            },\n" +
                "            \"sh-link\": [\n" +
                "                {\n" +
                "                    \"id\": 999999,\n" +
                "                    \"link-type-id\": \"SHRiskAddr\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": 999999,\n" +
                "                    \"link-type-id\": \"RegDrv\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": 999999,\n" +
                "                    \"link-type-id\": \"RegOwn\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        return webClient.post()
                .uri("/api/insure/quotations")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .body(BodyInserters.fromObject(req))
                .retrieve()
                .toEntity(Object.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        Object resObj = responseEntity.getBody();
                        QuotationResponse quotationRes = AppUtil.dtoToEntity(resObj,new QuotationResponse());
                        LOG.info("Successfully received quotation");
                        return ResponseEntity.ok().body(new APIResponse(200, "success",resObj, Instant.now()));
                    } else {
                        LOG.info(MessageFormat.format("Failed to issue quotation. Error code {0}", responseEntity.getStatusCode().value()));
                        return ResponseEntity.badRequest().body(new APIResponse(responseEntity.getStatusCode().value(), "Failed to issue quotation.", responseEntity.getStatusCode().value(), Instant.now()));
                    }
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to to get quotation {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to get quotation", Instant.now())));
                });
    }

    public Flux<Car> calculateQuotation(Long quotationId) {
        setConfigs("/api/insure/calculations/calculate/quotations/"+quotationId);
        return Flux.empty();
    }

    public Flux<Car> getQuotationResult(Long quotationId) {
        setConfigs("/api/insure/calculations/status/quotations/"+quotationId);
        return Flux.empty();
    }
}
