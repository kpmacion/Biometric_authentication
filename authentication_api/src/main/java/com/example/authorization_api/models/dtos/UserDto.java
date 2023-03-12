package com.example.authorization_api.models.dtos;


public class UserDto
{
    public String user_first_name;
    public String user_last_name;

    public UserDto(String user_first_name, String user_last_name)
    {
        this.user_first_name = user_first_name;
        this.user_last_name = user_last_name;
    }
}