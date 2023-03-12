package com.example.authorization_api.models;

import com.example.authorization_api.models.enums.TokenState;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
public class Token
{
    @Id
    @GeneratedValue(generator = "token")
    @GenericGenerator(name = "token", strategy = "increment")
    private long token_id;
    private String token_value;
    private LocalDateTime token_generation_time;
    @Enumerated(EnumType.STRING)
    private TokenState token_state;
    @ManyToOne
    @JsonManagedReference
    private User user;

    public Token() {};
    public Token(String token_value)
    {
        this.token_value = token_value;
        this.token_generation_time = LocalDateTime.now();
        this.token_state = TokenState.GENERATED;
    }

    public long getToken_id()
    {
        return token_id;
    }

    public void setToken_id(long token_id)
    {
        this.token_id = token_id;
    }

    public String getToken_value()
    {
        return token_value;
    }

    public void setToken_value(String token_value)
    {
        this.token_value = token_value;
    }

    public LocalDateTime getToken_generation_time()
    {
        return token_generation_time;
    }

    public void setToken_generation_time(LocalDateTime token_generation_time)
    {
        this.token_generation_time = token_generation_time;
    }

    public TokenState getToken_state()
    {
        return token_state;
    }

    public void setToken_state(TokenState token_state)
    {
        this.token_state = token_state;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        if(this.user == null)
        {
            this.user = user;
            user.addToken(this);
        }
    }
}
