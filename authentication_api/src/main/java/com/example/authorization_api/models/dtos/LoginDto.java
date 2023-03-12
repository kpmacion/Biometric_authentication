package com.example.authorization_api.models.dtos;


public class LoginDto
{
    public String app_key;
    public String token_value;
    public String user_mail_address;

    public LoginDto(String app_key, String token_value, String user_mail_address)
    {
        this.app_key = app_key;
        this.token_value = token_value;
        this.user_mail_address = user_mail_address;
    }
}