package com.example.authorization_api.models.dtos;

import com.example.authorization_api.models.enums.TokenState;


public class TokenStateDto
{
    public TokenState token_state;

    public TokenStateDto() {}
    public TokenStateDto(TokenState token_state)
    {
        this.token_state = token_state;
    }
}
