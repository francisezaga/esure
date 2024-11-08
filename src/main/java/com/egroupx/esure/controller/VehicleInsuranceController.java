package com.egroupx.esure.controller;

import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.services.VehicleInsuranceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/esure/vehicles")
public class VehicleInsuranceController {

    private final VehicleInsuranceService vehicleInsuranceService;

    public VehicleInsuranceController(VehicleInsuranceService carInsuranceService) {
        this.vehicleInsuranceService = carInsuranceService;
    }

    @PostMapping(value = {"/getVehicleManufacturers/{year}"})
    public Mono<ResponseEntity<APIResponse>> getVehicleManufacturers(@PathVariable String year) {
       return vehicleInsuranceService.getVehicleManufacturersByYear(year);
    }

    @PostMapping(value = {"/getVehicleModels/{manufacturerCode}/{year}"})
    public Mono<ResponseEntity<APIResponse>> getVehicleModelsByYearAndManufacturerCode(@PathVariable String manufacturerCode,@PathVariable String year)  {
        return vehicleInsuranceService.getVehicleModelsByManufacturerCodeAndYear(manufacturerCode,year);
    }

}
