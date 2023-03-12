package com.example.authorization_api.tools;

import java.util.Random;


public class TokenGenerator
{
    private static final Random random = new Random();
    private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

    public String generateToken(int length)
    {
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++)
        {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }
}
