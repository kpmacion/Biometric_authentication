package com.example.authorization_api.services;

import com.example.authorization_api.models.Application;
import com.example.authorization_api.models.Token;
import com.example.authorization_api.models.User;
import com.example.authorization_api.models.dtos.FingerprintDto;
import com.example.authorization_api.models.dtos.RegistrationDto;
import com.example.authorization_api.models.dtos.UserDto;
import com.example.authorization_api.tools.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;


public class UserService
{
    private static final TokenService tokenService = new TokenService();
    private static final ApplicationService applicationService = new ApplicationService();

    public List<User> getUsers()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        List<User> users = session.createQuery("from User ").list();

        session.getTransaction().commit();
        session.close();

        return users;
    }

    public User getUser(long user_id)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from User where user_id=:user_id");
        query.setParameter("user_id", user_id);

        User user = (User) query.getSingleResult();

        session.getTransaction().commit();
        session.close();

        return user;
    }

    public User getUser(String user_mail_address)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Query query = session.createQuery("from User where user_mail_address=:user_mail_address");
        query.setParameter("user_mail_address", user_mail_address.toLowerCase());

        User user = (User) query.getSingleResult();

        session.getTransaction().commit();
        session.close();

        return user;
    }

    public User modifyUser(long user_id, UserDto userDto)
    {
        User user = getUser(user_id);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        user.setUser_first_name(userDto.user_first_name);
        user.setUser_last_name(userDto.user_last_name);
        session.update(user);

        session.getTransaction().commit();
        session.close();

        return user;
    }

    public User deleteUser(long user_id)
    {
        User user = getUser(user_id);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.delete(user);

        session.getTransaction().commit();
        session.close();

        return user;
    }

    public void registrationCreateUser(long token_id, FingerprintDto fingerprintDto)
    {
        Token token = tokenService.getToken(token_id);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        User user = new User();
        user.setUser_fingerprint(fingerprintDto.template);
        token.setUser(user);

        session.update(token);
        session.save(user);

        session.getTransaction().commit();
        session.close();
    }

    public void registrationModifyUser(long user_id, RegistrationDto registrationDto)
    {
        User user = getUser(user_id);
        Application app = applicationService.getApplication(registrationDto.app_key);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        user.setUser_mail_address(registrationDto.user_mail_address.toLowerCase());
        user.setUser_first_name(registrationDto.user_first_name.substring(0, 1).toUpperCase() + registrationDto.user_first_name.substring(1));
        user.setUser_last_name(registrationDto.user_last_name.substring(0, 1).toUpperCase() + registrationDto.user_last_name.substring(1));
        user.setApplication(app);

        session.update(user);
        session.update(app);

        session.getTransaction().commit();
        session.close();
    }

    public void loginGetUser(long token_id, String user_mail_address)
    {
        Token token = tokenService.getToken(token_id);

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        User user = getUser(user_mail_address);
        token.setUser(user);

        session.update(token);
        session.update(user);

        session.getTransaction().commit();
        session.close();
    }

    public boolean userIsValid(long user_id)
    {
        List<User> users = getUsers();
        return users.stream().anyMatch(u -> u.getUser_id() == user_id);
    }

    public FingerprintDto getUserFingerprint(long user_id)
    {
        User user = getUser(user_id);
        return new FingerprintDto(user.getUser_fingerprint());
    }
}
