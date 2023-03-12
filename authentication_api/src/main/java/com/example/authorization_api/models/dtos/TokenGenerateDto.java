package com.example.authorization_api.models.dtos;


public class TokenGenerateDto
{
    public long token_id;
    public String token_value;

    public TokenGenerateDto(long token_id, String token_value)
    {
        this.token_value = token_value;
        this.token_id = token_id;
    }
}
