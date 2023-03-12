package com.example.authorization_api.services;

import com.example.authorization_api.models.Application;
import com.example.authorization_api.models.dtos.ApplicationDto;
import com.example.authorization_api.tools.HibernateUtil;
import com.example.authorization_api.tools.TokenGenerator;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;


public class ApplicationService
{
    public Application addApplication(ApplicationDto applicationDto)
    {
        String token_value;
        do
        {
            token_value = new TokenGenerator().generateToken(20);
        }
        while(applicationIsValid(token_value));

        Application app = new Application(applicationDto.app_name, token_value);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.save(app);

        session.getTransaction().commit();
        session.close();

        return app;
    }

    public List<Application> getApplications()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        List<Application> apps = session.createQuery("from Application").list();

        session.getTransaction().commit();
        session.close();

        return apps;
    }

    public Application getApplication(long app_id)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Application where app_id=:app_id");
        query.setParameter("app_id", app_id);

        Application app = (Application) query.getSingleResult();

        session.getTransaction().commit();
        session.close();

        return app;
    }

    public Application getApplication(String app_key)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from Application where app_key=:app_key");
        query.setParameter("app_key", app_key.toUpperCase());

        Application app = (Application) query.getSingleResult();

        session.getTransaction().commit();
        session.close();

        return app;
    }

    public Application modifyApplication(long app_id, ApplicationDto applicationDto)
    {
        Application app = getApplication(app_id);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        app.setApp_name(applicationDto.app_name);
        session.update(app);

        session.getTransaction().commit();
        session.close();

        return app;
    }

    public Application deleteApplication(long app_id)
    {
        Application app = getApplication(app_id);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.delete(app);

        session.getTransaction().commit();
        session.close();

        return app;
    }

    public boolean applicationIsValid(long app_id)
    {
        List<Application> apps = getApplications();
        return apps.stream().anyMatch(a -> a.getApp_id() == app_id);
    }

    public boolean applicationIsValid(String app_key)
    {
        List<Application> apps = getApplications();
        return apps.stream().anyMatch(a -> a.getApp_key().equalsIgnoreCase(app_key));
    }

    public boolean applicationUserIsValid(String app_key, String user_mail_address)
    {
        Application app = getApplication(app_key);
        return app.getUsers().stream().anyMatch(u -> u.getUser_mail_address().equalsIgnoreCase(user_mail_address));
    }
}
