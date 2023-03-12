package com.example.authorization_api.controllers;

import com.example.authorization_api.models.Token;
import com.example.authorization_api.models.dtos.FingerprintDto;
import com.example.authorization_api.models.dtos.RegistrationDto;
import com.example.authorization_api.models.enums.TokenState;
import com.example.authorization_api.services.ApplicationService;
import com.example.authorization_api.services.TokenService;
import com.example.authorization_api.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(value = "/api/registration")
public class RegistrationController
{
    private static final TokenService tokenService = new TokenService();
    private static final ApplicationService applicationService = new ApplicationService();
    private static final UserService userService = new UserService();

    @PostMapping
    public Token register(@RequestBody RegistrationDto registrationDto)
    {
        if(!tokenService.tokenIsUnexpired(registrationDto.token_value))
        {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The token: " + registrationDto.token_value + " does not exist or has expired");
        }
        if(!applicationService.applicationIsValid(registrationDto.app_key))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application with KEY: " + registrationDto.app_key + " does not exist");
        }
        if(applicationService.applicationUserIsValid(registrationDto.app_key, registrationDto.user_mail_address))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User: " + registrationDto.user_mail_address + " already exists");
        }

        Token token = tokenService.getToken(registrationDto.token_value);
        tokenService.updateTokenState(token.getToken_id(), TokenState.PENDING_REGISTRATION);

        while (token.getToken_state() != TokenState.REGISTRATION)
        {
            token = tokenService.getToken(registrationDto.token_value);
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The request could not be handled");
            }
        }

        userService.registrationModifyUser(token.getUser().getUser_id(), registrationDto);

        return token;
    }

    @PostMapping(value = "/{token_id}/fingerprint")
    public FingerprintDto setFingerprint(@PathVariable long token_id, @RequestBody FingerprintDto fingerprintDto)
    {
        if(!tokenService.tokenIsValid(token_id))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token with ID: " + token_id + " does not exist");
        }

        userService.registrationCreateUser(token_id, fingerprintDto);
        tokenService.updateTokenState(token_id, TokenState.REGISTRATION);
        Token token = tokenService.getToken(token_id);

        return userService.getUserFingerprint(token.getUser().getUser_id());
    }
}
