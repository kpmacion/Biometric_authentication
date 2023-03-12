package com.example.authorization_api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Application
{
    @Id
    @GeneratedValue(generator = "application")
    @GenericGenerator(name = "application", strategy = "increment")
    private long app_id;
    private String app_name;
    @Column(unique = true)
    private String app_key;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "application", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<User> users = new ArrayList<>();

    public Application() {};
    public Application(String app_name, String app_key)
    {
        this.app_name = app_name;
        this.app_key = app_key;
    }

    public long getApp_id()
    {
        return app_id;
    }

    public void setApp_id(long app_id)
    {
        this.app_id = app_id;
    }

    public String getApp_name()
    {
        return app_name;
    }

    public void setApp_name(String app_name)
    {
        this.app_name = app_name;
    }

    public String getApp_key()
    {
        return app_key;
    }

    public void setApp_key(String app_key)
    {
        this.app_key = app_key;
    }

    public List<User> getUsers()
    {
        return users;
    }

    public void setUsers(List<User> users)
    {
        this.users = users;
    }

    public void addUser(User user)
    {
        if(!this.users.contains(user))
        {
            this.users.add(user);
            user.setApplication(this);
        }
    }
}
