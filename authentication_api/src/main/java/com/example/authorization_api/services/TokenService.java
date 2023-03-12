package com.example.authorization_api.services;

import com.example.authorization_api.models.dtos.TokenGenerateDto;
import com.example.authorization_api.models.dtos.TokenStateDto;
import com.example.authorization_api.models.enums.TokenState;
import com.example.authorization_api.tools.HibernateUtil;
import com.example.authorization_api.tools.TokenGenerator;
import com.example.authorization_api.models.Token;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class TokenService
{
    public TokenGenerateDto generateToken()
    {
        String token_value;
        do
        {
            token_value = new TokenGenerator().generateToken(6);
        }
        while(tokenIsUnexpired(token_value));

        Token token = new Token(token_value);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.save(token);

        session.getTransaction().commit();
        session.close();

        return new TokenGenerateDto(token.getToken_id(), token.getToken_value());
    }

    public List<Token> getTokens()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        List<Token> tokens = session.createQuery("from Token").list();

        session.getTransaction().commit();
        session.close();

        return tokens;
    }

    private List<Token> getUnexpiredTokens()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        List<Token> tokens = session.createQuery("from Token").list();

        session.getTransaction().commit();
        session.close();

        tokens = tokens.stream().filter(t -> (t.getToken_generation_time().plusMinutes(3).isAfter(LocalDateTime.now()))).collect(Collectors.toList());
        return tokens;
    }

    public Token getToken(long token_id)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Token where token_id=:token_id");
        query.setParameter("token_id", token_id);

        Token token = (Token) query.getSingleResult();

        session.getTransaction().commit();
        session.close();

        return token;
    }

    public Token getToken(String token_value)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Token where token_value=:token_value");
        query.setParameter("token_value", token_value.toUpperCase());

        List<Token> tokens = query.getResultList();
        Token token = tokens.stream().max(Comparator.comparingLong(Token::getToken_id)).orElse(null);

        session.getTransaction().commit();
        session.close();

        return token;
    }

    public TokenStateDto getTokenState(long token_id)
    {
        Token token = getToken(token_id);
        return new TokenStateDto(token.getToken_state());
    }

    public void updateTokenState(long token_id, TokenState token_state)
    {
        Token token = getToken(token_id);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        token.setToken_state(token_state);

        session.update(token);

        session.getTransaction().commit();
        session.close();
    }

    public boolean tokenIsUnexpired(String token_value)
    {
        List<Token> tokens = getUnexpiredTokens();
        return tokens.stream().anyMatch(t -> t.getToken_value().equalsIgnoreCase(token_value));
    }

    public boolean tokenIsUnexpired(long token_id)
    {
        List<Token> tokens = getUnexpiredTokens();
        return tokens.stream().anyMatch(t -> t.getToken_id() == token_id);
    }

    public boolean tokenIsValid(long token_id)
    {
        List<Token> tokens = getTokens();
        return tokens.stream().anyMatch(t -> t.getToken_id() == token_id);
    }
}
