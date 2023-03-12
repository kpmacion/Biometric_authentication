package com.example.authorization_api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="\"user\"")
public class User
{
    @Id
    @GeneratedValue(generator = "user")
    @GenericGenerator(name = "user", strategy = "increment")
    private long user_id;
    private String user_mail_address;
    private String user_first_name;
    private String user_last_name;
    @Column(length = 2048)
    private String user_fingerprint;
    @ManyToOne
    @JsonManagedReference
    private Application application;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Token> tokens = new ArrayList<>();

    public User() {};

    public User(String user_mail_address, String user_first_name, String user_last_name, String user_fingerprint)
    {
        this.user_mail_address = user_mail_address;
        this.user_first_name = user_first_name;
        this.user_last_name = user_last_name;
        this.user_fingerprint = user_fingerprint;
    }

    public long getUser_id()
    {
        return user_id;
    }

    public void setUser_id(long user_id)
    {
        this.user_id = user_id;
    }

    public String getUser_mail_address()
    {
        return user_mail_address;
    }

    public void setUser_mail_address(String user_mail_address)
    {
        this.user_mail_address = user_mail_address;
    }

    public String getUser_first_name()
    {
        return user_first_name;
    }

    public void setUser_first_name(String user_first_name)
    {
        this.user_first_name = user_first_name;
    }

    public String getUser_last_name()
    {
        return user_last_name;
    }

    public void setUser_last_name(String user_last_name)
    {
        this.user_last_name = user_last_name;
    }

    public String getUser_fingerprint()
    {
        return user_fingerprint;
    }

    public void setUser_fingerprint(String user_fingerprint)
    {
        this.user_fingerprint = user_fingerprint;
    }

    public Application getApplication()
    {
        return application;
    }

    public void setApplication(Application application)
    {
        if(this.application == null)
        {
            this.application = application;
            application.addUser(this);
        }
    }

    public List<Token> getTokens()
    {
        return tokens;
    }

    public void setUsers(List<Token> tokens)
    {
        this.tokens = tokens;
    }

    public void addToken(Token token)
    {
        if(!this.tokens.contains(token))
        {
            this.tokens.add(token);
            token.setUser(this);
        }
    }
}
