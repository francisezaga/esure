package com.egroupx.esure.controller;

import com.egroupx.esure.model.responses.api.APIResponse;
import com.egroupx.esure.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/esure/life/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = {"/verifyUserCellNumber/{cellNumber}"})
    public Mono<ResponseEntity<APIResponse>> verifyUserCellNumber(@PathVariable String cellNumber)  {
        return authService.verifyUserCellNumber(cellNumber);
    }

    @PostMapping(value = {"/validateOTPDetails/{cellNumber}/{otpCode}"})
    public Mono<ResponseEntity<APIResponse>> verifyOTPDetails(@PathVariable String cellNumber, @PathVariable String otpCode)  {
        return authService.verifyOTP(cellNumber,otpCode);
    }

}
