package com.example.authorization_api.controllers;

import com.example.authorization_api.models.Token;
import com.example.authorization_api.models.dtos.TokenGenerateDto;
import com.example.authorization_api.models.dtos.TokenStateDto;
import com.example.authorization_api.services.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;


@RestController
@RequestMapping(value = "/api/tokens")
public class TokenController
{
    private static final TokenService tokenService = new TokenService();

    @GetMapping(value = "/generate")
    public TokenGenerateDto generateToken()
    {
        return tokenService.generateToken();
    }

    @GetMapping()
    public List<Token> getTokens()
    {
        return tokenService.getTokens();
    }

    @GetMapping(value = "/{token_id}/state")
    public TokenStateDto getTokenState(@PathVariable long token_id)
    {
        if(!tokenService.tokenIsUnexpired(token_id))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token with ID: " + token_id + " does not exist or has expired");
        }
        return tokenService.getTokenState(token_id);
    }

    @PutMapping(value = "/{token_id}/state")
    public TokenStateDto setTokenState(@PathVariable long token_id, @RequestBody TokenStateDto tokenStateDto)
    {
        tokenService.updateTokenState(token_id, tokenStateDto.token_state);
        return tokenService.getTokenState(token_id);
    }
}
