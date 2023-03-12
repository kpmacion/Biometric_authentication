package com.example.authorization_api.models.dtos;


public class FingerprintDto
{
    public String template;

    public FingerprintDto() {}
    public FingerprintDto(String template)
    {
        this.template = template;
    }
}
