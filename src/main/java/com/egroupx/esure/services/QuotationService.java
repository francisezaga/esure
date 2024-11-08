package com.egroupx.esure.services;

import com.egroupx.esure.dto.fsp_qoute.Quotation;
import com.egroupx.esure.dto.fsp_qoute.ShLink;
import com.egroupx.esure.dto.fsp_qoute.building.Buildings;
import com.egroupx.esure.dto.fsp_qoute.house_hold.HouseholdContents;
import com.egroupx.esure.dto.fsp_qoute.vehicle.MotorVehicles;
import com.egroupx.esure.exceptions.APIErrorException;
import com.egroupx.esure.exceptions.APIErrorHandler;
import com.egroupx.esure.model.Customer;
import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.model.responses.fsp_qoute_policies.QuotationResultResponse;
import com.egroupx.esure.model.responses.fsp_quote.*;
import com.egroupx.esure.repository.CustomerRepository;
import com.egroupx.esure.util.AppUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.*;

@Service
public class QuotationService {

    @Value("${egroupx.services.fsp.endpointUrl}")
    private String fspEndpointUrl;

    @Value("${egroupx.services.fsp.apiKey}")
    private String fspAPIKey;

    private WebClient webClient;

    private final CustomerService customerService;
    private final HomeInsuranceService homeInsuranceService;
    private final VehicleInsuranceService vehicleInsuranceService;

    private final Logger LOG = LoggerFactory.getLogger(CustomerService.class);

    public QuotationService(CustomerService customerService, HomeInsuranceService homeInsuranceService, VehicleInsuranceService vehicleInsuranceService,CustomerRepository customerRepository) {
        this.customerService = customerService;
        this.homeInsuranceService = homeInsuranceService;
        this.vehicleInsuranceService = vehicleInsuranceService;
    }

    private void setConfigs(String endpointUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(endpointUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((fspAPIKey + ":" + "").getBytes()))
                .build();
    }

    public Mono<ResponseEntity<APIResponse>> requestQuotation(Quotation quotation) {

        setConfigs(fspEndpointUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        String formattedQuotationReq = null;
        if(quotation.getMotorVehicles()!=null && quotation.getMotorVehicles().length>0){
            for(MotorVehicles motorVehicles: quotation.getMotorVehicles()) {
                List<ShLink> shLinks = new ArrayList<>();
                shLinks.add(new ShLink(999999L,"SHRiskAddr"));
                shLinks.add(new ShLink(999999L,"RegDrv"));
                shLinks.add(new ShLink(999999L,"RegOwn"));
                motorVehicles.setShLinks(shLinks);
            }
        }
        if(quotation.getHouseholdContents()!=null && quotation.getHouseholdContents().length>0){
            for(HouseholdContents householdContents: quotation.getHouseholdContents()) {
                List<ShLink> shLinks = new ArrayList<>();
                shLinks.add(new ShLink(999999L,"SHRiskAddr"));
                householdContents.setShLinks(shLinks);
            }
        }

        if(quotation.getBuildings()!=null && quotation.getBuildings().length>=1){
            for(Buildings building: quotation.getBuildings()) {
                List<ShLink> shLinks = new ArrayList<>();
                shLinks.add(new ShLink(999999L,"SHRiskAddr"));
                building.setShLinks(shLinks);
            }
        }

        try {
            formattedQuotationReq = objectMapper.writeValueAsString(quotation);
        } catch (JsonProcessingException ex) {
            return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to get process request", Instant.now())));
        }

        return webClient.post()
                .uri("/api/insure/quotations")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.ACCEPT, "*/*")
                .body(BodyInserters.fromObject(formattedQuotationReq))
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class) // error body as String or other class
                        .flatMap(error -> {
                            LOG.error(error);
                            return Mono.error(new APIErrorException(error));
                        }))
                .toEntity(QuotationResponse.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        Object resObj = responseEntity.getBody();
                        QuotationResponse quotationRes = null;
                        LOG.info("Successfully received quotation");
                        try {
                            String formattedQuotationRes = objectMapper.writeValueAsString(resObj);
                            quotationRes = objectMapper.readValue(formattedQuotationRes, QuotationResponse.class);
                            LOG.info("Successfully deserialized quotation response");
                            saveQuotation(quotationRes).subscribe();
                            return ResponseEntity.ok().body(new APIResponse(200, "success", quotationRes, Instant.now()));
                        } catch (JsonProcessingException ex) {
                            return ResponseEntity.ok().body(new APIResponse(200, "success", resObj, Instant.now()));
                        }
                    } else {
                        LOG.info(MessageFormat.format("Failed to issue quotation. Error code {0}", responseEntity.getStatusCode().value()));
                        return ResponseEntity.badRequest().body(new APIResponse(responseEntity.getStatusCode().value(), "Failed to issue quotation.", responseEntity.getStatusCode().value(), Instant.now()));
                    }
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to to get quotation {0}", error.getMessage()));
                    String errorMsg = APIErrorHandler.handleFSPAPIError(error.getMessage()).isEmpty() ? error.getMessage() : APIErrorHandler.handleFSPAPIError(error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to get quotation. "+errorMsg, Instant.now())));
                });
    }

    public Mono<ResponseEntity<APIResponse>> calculateQuotation(Long quotationId) {
        setConfigs(fspEndpointUrl);
        return webClient.post()
                .uri("/api/insure/calculations/calculate/quotations/" + quotationId)
                .header(HttpHeaders.ACCEPT, "*/*")
                .retrieve()
                .toEntity(CalculationsResponse.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {

                        CalculationsResponse calcResResult = responseEntity.getBody();
                        LOG.info("Successfully triggered calculation of quotation");
                        return ResponseEntity.ok().body(new APIResponse(200, "success", calcResResult, Instant.now()));
                    } else {
                        LOG.info(MessageFormat.format("Failed to trigger calculation of quotation. Error code {0}", responseEntity.getStatusCode().value()));
                        return ResponseEntity.badRequest().body(new APIResponse(responseEntity.getStatusCode().value(), "Failed to trigger calculation of quotation.", responseEntity.getStatusCode().value(), Instant.now()));
                    }
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to trigger calculation of quotation {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to trigger calculation of quotation", Instant.now())));
                });
    }

    public Mono<ResponseEntity<APIResponse>> getQuotationResult(Long quotationId) {
        setConfigs(fspEndpointUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        //delay before retrieving result
        try {
            Thread.sleep(45000);
            LOG.info("Waiting for results");
        } catch (InterruptedException e) {
            LOG.error("Error waiting for results");
        }
        return webClient.get()
                .uri("/api/insure/calculations/status/quotations/" + quotationId)
                .header(HttpHeaders.ACCEPT, "*/*")
                .retrieve()
                .toEntity(Object.class).map(responseEntity -> {
                    if (responseEntity.getStatusCode().is2xxSuccessful()) {
                        Object resObj = responseEntity.getBody();
                        QuotationResultResponse quotationResultRes = null;

                        try {
                            String formattedQuotationRes = objectMapper.writeValueAsString(resObj);
                            quotationResultRes = objectMapper.readValue(formattedQuotationRes, QuotationResultResponse.class);
                            LOG.info("Successfully retrieved quotation result");
                            return ResponseEntity.ok().body(new APIResponse(200, "success", quotationResultRes, Instant.now()));

                        } catch (JsonProcessingException ex) {
                            return ResponseEntity.ok().body(new APIResponse(200, "success", resObj, Instant.now()));
                        }
                    } else {
                        LOG.info(MessageFormat.format("Failed to retrieve quotation result. Error code {0}", responseEntity.getStatusCode().value()));
                        return ResponseEntity.badRequest().body(new APIResponse(responseEntity.getStatusCode().value(), "Failed to retrieve quotation result.", responseEntity.getStatusCode().value(), Instant.now()));
                    }
                }).onErrorResume(error -> {
                    LOG.info(MessageFormat.format("Failed to retrieve quotation result {0}", error.getMessage()));
                    return Mono.just(ResponseEntity.badRequest().body(new APIResponse(400, "Failed", "Failed to retrieve quotation result", Instant.now())));
                });
    }

    public Mono<String> saveQuotation(QuotationResponse quoteRes) {
        PolicyHolderResponse[] policyHolders = quoteRes.getPolicyHolders();
        AllRisksResponse[] allRisks = quoteRes.getAllRisks();
        BuildingsResponse[] buildings = quoteRes.getBuildings();
        HouseholdContentsResponse[] householdContents = quoteRes.getHouseholdContents();
        MotorVehiclesResponse[] motorVehicles = quoteRes.getMotorVehicles();

        //Mono<String> qouteMono =
        customerService.saveQuotationDetails(quoteRes.getId(), String.valueOf(quoteRes.getCategoryId()), quoteRes.getStatus()).subscribe();

        if (policyHolders != null && policyHolders.length > 0) {
            for (PolicyHolderResponse ph : policyHolders) {
                // Mono<String> phMono =
                customerService.savePolicyHolderDetails(quoteRes.getId(), ph.getId(), ph.getTrust(), ph.getIsPrivate(), ph.getStakeHolderType()).subscribe();

                //person
                if (ph.getPerson() != null) {
                    //Mono<String> personMono =
                    customerService.saveCustomerDetails(quoteRes.getId(), ph.getId(), ph.getPerson().getSurname(), ph.getPerson().getInitials(), ph.getPerson().getFullNames(), ph.getPerson().getIdNumber(), ph.getPerson().getTitleCd(), ph.getPerson().getGenderCd(), AppUtil.formatDate(ph.getPerson().getBirthDate()), ph.getPerson().getMaritalStatusCd(), ph.getPerson().getIdType(), ph.getPerson().getPassportNumber()).subscribe();
                }

                //address
                if (ph.getAddress() != null && ph.getAddress().size() > 0) {
                    for (int i = 0; i < ph.getAddress().size(); i++) {
                        AddressResponse address = ph.getAddress().get(i);

                        if (address.getTypeCd().equalsIgnoreCase("PHY") || address.getTypeCd().equalsIgnoreCase("POS")) {
                            customerService.saveAddressDetails(quoteRes.getId(), address.getTypeCd(), address.getLine1(), address.getCode(), address.getSuburb(), address.getResidentialAreaType()).subscribe();
                        } else if (address.getTypeCd().equalsIgnoreCase("ELT")) {
                            customerService.saveEmailAddressDetails(quoteRes.getId(), address.getTypeCd(), address.getLine1()).subscribe();

                        } else if (address.getTypeCd().equalsIgnoreCase("TEL")) {
                            customerService.saveTelAddressDetails(quoteRes.getId(), address.getTypeCd(), address.getCode(), address.getNumber(), address.getIsCellphone(), address.getIsTelephone(), address.getIsBusiness(), address.getIsResidential()).subscribe();
                        }

                    }
                }

                //income details
                if (ph.getIncomeDetails() != null) {
                    //Mono<String> incomeDetailMono =
                    customerService.savePolicyHolderIncomeDetails(quoteRes.getId(), ph.getId(), ph.getIncomeDetails().getOccupationCategory()).subscribe();
                }

                //insurance details
                if (ph.getInsuranceDetails() != null) {
                    /**/
                }

                //short term
                if (ph.getShortTerm() != null) {
                    //Mono<String> phShortTerm =
                    customerService.savePolicyHolderShortTermDetails(quoteRes.getId(), ph.getId(), ph.getShortTerm().getHeldInsuranceLast39Days(), ph.getShortTerm().getPeriodCompCarInsurance(), ph.getShortTerm().getPeriodCompNonMotorInsurance(), ph.getShortTerm().getHasConsent()).subscribe();

                    if (ph.getShortTerm().getLicenseDetails() != null && ph.getShortTerm().getLicenseDetails().length > 0) {

                        for (LicenseDetailResponse licenseRes : ph.getShortTerm().getLicenseDetails()) {
                            //Mono<String> licenseMono =
                            customerService.savePolicyHolderLicenseDetails(quoteRes.getId(), ph.getId(), AppUtil.formatDate(licenseRes.getLicenseDate()), licenseRes.getLicenseCategory(), licenseRes.getLicenseType(), licenseRes.getVehicleRestriction()).subscribe();
                        }
                    }
                }
            }

        }

        if (allRisks != null && allRisks.length > 0) {
            for (AllRisksResponse allRisk : allRisks) {
                // Mono<String> allRiskMono =
                customerService.saveAllRisksDetails(quoteRes.getId(), allRisk.getItemDescription(), allRisk.getSumInsured(), allRisk.getCoverTypeId()).subscribe();
            }
        }

        if (buildings != null && buildings.length > 0) {
            for (BuildingsResponse buildingRes : buildings) {
                //Mono<String> buildingMono =
                homeInsuranceService.saveBuildingDetails(quoteRes.getId(), buildingRes.getDescription(), buildingRes.getCoverType(), buildingRes.getConstruction(), buildingRes.getRoofConstruction(), buildingRes.getSumInsured(), buildingRes.getRecentLossCount(), buildingRes.getPropertyOwnedClaim()).subscribe();

                if (buildingRes.getShLinks() != null && buildingRes.getShLinks().length > 0) {
                    for (ShLinkResponse shLinkRes : buildingRes.getShLinks()) {
                        //Mono<String> buildingShLink =
                        homeInsuranceService.saveBuildingShLinkDetails(quoteRes.getId(), shLinkRes.getLinkTypeId(), shLinkRes.getId()).subscribe();
                    }
                }
            }
        }

        if (householdContents != null && householdContents.length > 0) {
            for (HouseholdContentsResponse householdContentsRes : householdContents) {
                //Mono<String> houseHoldContentsMono =
                homeInsuranceService.saveHouseHoldContentsDetails(quoteRes.getId(), householdContentsRes.getDescription(), householdContentsRes.getSumInsured(), householdContentsRes.getRestrictedCover(), householdContentsRes.getUnoccPeriod(), householdContentsRes.getUnrelatedCount(), householdContentsRes.getStandardWalls(), householdContentsRes.getThatchedRoof(), householdContentsRes.getBurglarBars(), householdContentsRes.getSecurityGates(), householdContentsRes.getAlarmInWorkingOrder(), householdContentsRes.getRecentLossCount(), householdContentsRes.getPropertyOwnedClaim()).subscribe();

                if (householdContentsRes.getShLinks() != null && householdContentsRes.getShLinks().length > 0) {
                    for (ShLinkResponse shLinkRes : householdContentsRes.getShLinks()) {
                        //Mono<String> houseHoldShLink =
                        homeInsuranceService.saveHouseHoldShLinkDetails(quoteRes.getId(), shLinkRes.getLinkTypeId(), shLinkRes.getId()).subscribe();
                    }
                }
            }
        }

        if (motorVehicles != null && motorVehicles.length > 0) {
            for (MotorVehiclesResponse mvRes : motorVehicles) {
                //Mono<String> mvMono =
                vehicleInsuranceService.saveVehicleDetails(quoteRes.getId(), mvRes.getYear(), mvRes.getMake(), mvRes.getModel(), mvRes.getCarColour(), mvRes.getMetallicPaint(), mvRes.getQuotationBasis(), mvRes.getAlarmTypeId(), mvRes.getAlarmByVesa(), mvRes.getTracingDevice()).subscribe();

                if (mvRes.getMotorVechicleShortTerm() != null) {
                    // Mono<String> mvShortTermMono =
                    vehicleInsuranceService.saveVehicleShortTermDetails(quoteRes.getId(), mvRes.getMotorVechicleShortTerm().getCoverType(), mvRes.getMotorVechicleShortTerm().getUseTypeId(), mvRes.getMotorVechicleShortTerm().getFlatExcess(), mvRes.getMotorVechicleShortTerm().getOvernightParkingCd(), mvRes.getMotorVechicleShortTerm().getOvernightParkingTypeLocked()).subscribe();
                }

                if (mvRes.getShLinks() != null && mvRes.getShLinks().length > 0) {
                    for (ShLinkResponse shLinkRes : mvRes.getShLinks()) {
                        //Mono<String> houseHoldShLink =
                        vehicleInsuranceService.saveVehicleShLinkDetails(quoteRes.getId(), shLinkRes.getLinkTypeId(), shLinkRes.getId()).subscribe();
                    }
                }
            }
        }
        return customerService.sendEmailQuotationNotification(quoteRes.getId())
                .then(Mono.just("quotation email"))
                .flatMap(msg->Mono.just("Completed processing quotation details"))
                .then(Mono.just("quotation welcome email"))
                .flatMap(msg->{
                    return customerService.sendEmailFSPCustomerWelcomeNotification(quoteRes.getId());
             });


    }

    public Mono<APIResponse> getQuotationByUserId(String idNumber) {
        Flux<Customer> customerRecordsMono = customerService.getCustomerQuotationByUserId(idNumber);
        return customerRecordsMono.collectList().flatMap(customerList -> {
            List<com.egroupx.esure.model.Quotation> quotations = new ArrayList<>();
            return Flux.fromIterable(customerList).flatMap(customer -> {
                        LOG.info(String.valueOf(customer.getFspQuoteRefId()));
                        return getQuotationByFSPQouteRef(customer.getFspQuoteRefId())
                                .flatMap(quotation -> {
                                    quotations.add(quotation);
                                    LOG.info("ADDING QOUTE");
                                    return Mono.just("next");
                                });


                    }).then(Mono.just(new APIResponse(200, "success", quotations, Instant.now())))
                    .flatMap(apiRes -> {
                        return Mono.just(apiRes);
                    });
        }).onErrorResume(err -> {
            return Mono.just(new APIResponse(500, "Fail", "Request failed", Instant.now()));
        });
    }

    public Mono<com.egroupx.esure.model.Quotation> getQuotationByFSPQouteRef(Long fspQuoteRef) {
        com.egroupx.esure.model.Quotation quotation = new com.egroupx.esure.model.Quotation();
        LOG.info(String.valueOf(fspQuoteRef));
        return customerService.getQuotationByQouteRefId(fspQuoteRef)
                .flatMap(quote -> {
                    quotation.setId(quote.getId());
                    quotation.setStatus(quote.getStatus());
                    quotation.setFspQuoteRefId(quote.getFspQuoteRefId());
                    quotation.setCategoryId(quote.getCategoryId());
                    return Mono.just(quotation);
                })
                .then(Mono.just("Policy Holders").flatMap(pHoldersStep -> {
                    return customerService.getPolicyHolderByQouteRefId(fspQuoteRef).collectList()
                            .flatMap(pHolders -> {
                                List<com.egroupx.esure.model.PolicyHolder> policyHolders = new ArrayList<>();
                                return Flux.fromIterable(pHolders).flatMap(pH -> {
                                    com.egroupx.esure.model.PolicyHolder policyHolder = new com.egroupx.esure.model.PolicyHolder();
                                    policyHolder.setId(pH.getId());
                                    policyHolder.setTrust(pH.getTrust());
                                    policyHolder.setFspQuoteRefId(pH.getFspQuoteRefId());
                                    policyHolder.setIsPrivate(pH.getIsPrivate());
                                    policyHolder.setFspPhRefId(pH.getFspPhRefId());
                                    policyHolder.setStakeholderType(pH.getStakeholderType());

                                    return customerService.getPHShortTermByQouteRefId(fspQuoteRef).flatMap(phShortTerm -> {
                                                policyHolder.setShortTerm(phShortTerm);
                                                return customerService.getPHLicenseDetailQuoteRefId(fspQuoteRef).collectList().flatMap(phLicenseList -> {
                                                    phShortTerm.setPhLicenseDetailList(phLicenseList);
                                                    return Mono.just(phShortTerm);
                                                });

                                            })
                                            .then((Mono.just("Income Details"))).flatMap(phIncomeDetails -> {
                                                return customerService.getPHIncomeDetails(fspQuoteRef).flatMap(phIncome -> {
                                                    policyHolder.setIncomeDetails(phIncome);
                                                    return Mono.just(phIncome);
                                                });
                                            })
                                            .then((Mono.just("Customer details"))).flatMap(phIncomeDetails -> {
                                                return customerService.getCustomerByQuoteRef(fspQuoteRef).flatMap(customer -> {
                                                    policyHolder.setPerson(customer);
                                                    return Mono.just(customer);
                                                });
                                            })
                                            .then(Mono.just("addresses")).flatMap(address -> {
                                                List<Object> addressList = new ArrayList<>();
                                                return customerService.getAddressDetails(fspQuoteRef).collectList().flatMap(addrs -> {
                                                            addressList.addAll(addrs);
                                                            return Mono.just(addrs);
                                                        })
                                                        .then(Mono.just("tel address")).flatMap(tel -> {
                                                            return customerService.getTelAddressDetails(fspQuoteRef).collectList().flatMap(telAddrs -> {
                                                                addressList.addAll(telAddrs);
                                                                return Mono.just(telAddrs);
                                                            });
                                                        })
                                                        .then(Mono.just("email address")).flatMap(email -> {
                                                            return customerService.getEmailAddressDetails(fspQuoteRef).collectList().flatMap(emailAddrs -> {
                                                                addressList.addAll(emailAddrs);
                                                                return Mono.just(emailAddrs);
                                                            });
                                                        })
                                                        .then(Mono.just("addresses")).flatMap(ad -> {
                                                            policyHolder.setAddresses(addressList);
                                                            return Mono.empty();
                                                        });


                                            })
                                            .then(Mono.just("Insurance detail")).flatMap(address -> {
                                                return Mono.just("Insurance details");
                                            })
                                            .then(Mono.just("Ph").flatMap(ph -> {
                                                policyHolders.add(policyHolder);
                                                return Mono.just("Policy holder");
                                            }));
                                }).then(Mono.just("next")).flatMap(res -> {
                                    quotation.setPolicyHolders(policyHolders);
                                    LOG.info("ADDING POLICY");
                                    return Mono.just(policyHolders);
                                });
                            });
                }))
                .then(Mono.just("Add All Risks")).flatMap(allRisksStep -> {
                    LOG.info("All Risks");
                    return customerService.getAllRisksDetails(fspQuoteRef).collectList()
                            .flatMap(allRisks -> {
                                quotation.setAllRisks(allRisks);
                                return Mono.just(allRisks);
                            });
                })
                .then(Mono.just("Buildings")).flatMap(allRisks -> {
                    LOG.info("Buildings");
                    return homeInsuranceService.getBuildingDetails(fspQuoteRef).collectList()
                            .flatMap(buildings -> {
                                List<com.egroupx.esure.model.Building> buildingList = new ArrayList<>();
                                return Flux.fromIterable(buildings).flatMap(building -> {
                                    return homeInsuranceService.getBuildingShLinkDetails(fspQuoteRef).collectList()
                                            .flatMap(shList -> {
                                                building.setShLinks(shList);
                                                return Mono.just(shList);
                                            }).then(Mono.just("building").flatMap(list -> {
                                                buildingList.add(building);
                                                return Mono.just(buildingList);
                                            }));
                                }).then(Mono.just("").flatMap(list -> {
                                    quotation.setBuildings(buildingList);
                                    return Mono.just(buildingList);
                                }));
                            });
                })
                .then(Mono.just("House holds")).flatMap(houseHolds -> {
                    return homeInsuranceService.getHouseHoldContentsDetails(fspQuoteRef).collectList()
                            .flatMap(hseHolds -> {
                                List<com.egroupx.esure.model.HouseHoldContents> houseHoldList = new ArrayList<>();
                                return Flux.fromIterable(hseHolds).flatMap(hse -> {
                                    return homeInsuranceService.getHouseHoldContentsShLink(fspQuoteRef).collectList()
                                            .flatMap(shList -> {
                                                hse.setShLinks(shList);
                                                return Mono.just(shList);
                                            }).then(Mono.just("building").flatMap(list -> {
                                                houseHoldList.add(hse);
                                                return Mono.just(houseHoldList);
                                            }));
                                }).then(Mono.just("").flatMap(list -> {
                                    quotation.setHouseholdContents(houseHoldList);
                                    return Mono.just(houseHoldList);
                                }));
                            });
                })
                .then(Mono.just("Motor Vehicles")).flatMap(vehicleList -> {
                    return vehicleInsuranceService.getMotorVehicleByQuoteRefId(fspQuoteRef).collectList()
                            .flatMap(mvList -> {
                                List<com.egroupx.esure.model.MotorVehicles> motorVehiclesList = new ArrayList<>();
                                return Flux.fromIterable(mvList).flatMap(mv -> {
                                    return vehicleInsuranceService.getMotorVehicleShLink(fspQuoteRef).collectList()
                                            .flatMap(shList -> {
                                                mv.setShLinks(shList);
                                                return Mono.just(shList);
                                            })
                                            .then(Mono.just("mv short term").flatMap(list -> {
                                                return vehicleInsuranceService.getMotorVehicleShortTerm(fspQuoteRef)
                                                        .flatMap(mvShortTerm -> {
                                                            mv.setMotorVehicleShortTerm(mvShortTerm);
                                                            return Mono.just(mvShortTerm);
                                                        });
                                            }))
                                            .then(Mono.just("add to mv List").flatMap(list -> {
                                                motorVehiclesList.add(mv);
                                                return Mono.just(motorVehiclesList);
                                            }));
                                }).then(Mono.just("").flatMap(list -> {
                                    quotation.setMotorVehicles(motorVehiclesList);
                                    return Mono.just(mvList);
                                }));
                            });
                })
                .then(Mono.just("Add qoute")).flatMap(res -> {
                    LOG.info("ADDING QOUTE");
                    return Mono.just(quotation);
                });
    }


}


