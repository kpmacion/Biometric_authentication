package com.example.authorization_api.controllers;

import com.example.authorization_api.models.Token;
import com.example.authorization_api.models.dtos.FingerprintDto;
import com.example.authorization_api.models.dtos.LoginDto;
import com.example.authorization_api.models.enums.TokenState;
import com.example.authorization_api.services.ApplicationService;
import com.example.authorization_api.services.TokenService;
import com.example.authorization_api.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping(value = "/api/login")
public class LoginController
{
    private static final TokenService tokenService = new TokenService();
    private static final ApplicationService applicationService = new ApplicationService();
    private static final UserService userService = new UserService();

    @PostMapping
    public Token login(@RequestBody LoginDto loginDto)
    {
        if(!tokenService.tokenIsUnexpired(loginDto.token_value))
        {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "The token: " + loginDto.token_value + " does not exist or has expired");
        }
        if(!applicationService.applicationIsValid(loginDto.app_key))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Application with KEY: " + loginDto.app_key + " does not exist");
        }
        if(!applicationService.applicationUserIsValid(loginDto.app_key, loginDto.user_mail_address))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User: " + loginDto.user_mail_address + " does not exist");
        }

        Token token = tokenService.getToken(loginDto.token_value);
        tokenService.updateTokenState(token.getToken_id(), TokenState.PENDING_LOGIN);
        userService.loginGetUser(token.getToken_id(), loginDto.user_mail_address);

        while (token.getToken_state() != TokenState.LOGIN && token.getToken_state() != TokenState.INVALID_LOGIN)
        {
            token = tokenService.getToken(loginDto.token_value);
            try
            {
                Thread.sleep(3000);
            }
            catch (InterruptedException e)
            {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The request could not be handled");
            }
        }

        if(token.getToken_state() == TokenState.INVALID_LOGIN)
        {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect login");
        }

        return token;
    }

    @GetMapping(value = "/{token_id}/fingerprint")
    public FingerprintDto getFingerprint(@PathVariable long token_id)
    {
        if(!tokenService.tokenIsValid(token_id))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token with ID: " + token_id + " does not exist");
        }

        Token token = tokenService.getToken(token_id);

        if(token.getUser() == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Token with ID: " + token_id + " has no assigned user");
        }

        return userService.getUserFingerprint(token.getUser().getUser_id());
    }
}
