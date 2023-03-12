package com.example.authorization_api.models.dtos;


public class RegistrationDto
{
    public String app_key;
    public String token_value;
    public String user_mail_address;
    public String user_first_name;
    public String user_last_name;

    public RegistrationDto(String app_key, String token_value, String user_mail_address, String user_first_name, String user_last_name)
    {
        this.app_key = app_key;
        this.token_value = token_value;
        this.user_mail_address = user_mail_address;
        this.user_first_name = user_first_name;
        this.user_last_name = user_last_name;
    }
}
