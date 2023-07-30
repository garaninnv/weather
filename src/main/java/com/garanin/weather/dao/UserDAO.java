package com.garanin.weather.dao;

import com.garanin.weather.dto.LocationDTO;
import com.garanin.weather.dto.SessionDTO;
import com.garanin.weather.dto.UserDTO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class UserDAO {
    UserDTO userDTO = new UserDTO();
    SessionDTO sessionDTO;
    SessionDAO sessionDAO = new SessionDAO();

    public void adduser(String user, String pas, HttpServletResponse resp) {
        Configuration con = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class).addAnnotatedClass(SessionDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());

        LocalDateTime time = LocalDateTime.now();
        sessionDTO = new SessionDTO(UUID.randomUUID(), userDTO, time.plus(24, ChronoUnit.HOURS));

        if (!user.equals("") && !pas.equals("")) {
            userDTO.setLogin(user);
            String salt = BCrypt.gensalt();
            String hachPas = BCrypt.hashpw(pas, salt);
            userDTO.setPassword(hachPas);
        }
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(userDTO);
            session.save(sessionDTO);
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
        Cookie cookie = new Cookie("weather", sessionDTO.getId().toString());
        cookie.setMaxAge(86400);
        resp.addCookie(cookie);
    }

    public boolean findUser(String user, String pas, HttpServletResponse resp) {
        List<UserDTO> list;

        Configuration con = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class).addAnnotatedClass(SessionDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());

        LocalDateTime time = LocalDateTime.now();
        sessionDTO = new SessionDTO(UUID.randomUUID(), userDTO, time.plus(24, ChronoUnit.HOURS));

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            list = session.createQuery("from UserDTO where login='" + user + "'").getResultList();
            session.getTransaction().commit();

            if (list.isEmpty()) {
                return false;
            } else {
                userDTO = list.get(0);
                if (BCrypt.checkpw(pas, userDTO.getPassword())) {
                    sessionDTO.setUserId(userDTO);
                    session.beginTransaction();
                    session.save(sessionDTO);
                    session.getTransaction().commit();

                    Cookie cookie = new Cookie("weather", sessionDTO.getId().toString());
                    cookie.setMaxAge(86400);
                    resp.addCookie(cookie);
                    return true;
                }
            }
        } finally {
            sessionFactory.close();
        }
        return false;
    }

    public UserDTO findUserUUIDSession(UUID uuid) {
        Configuration con = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class).addAnnotatedClass(SessionDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());

        sessionDTO = sessionDAO.findById(uuid).get();

        if (sessionDTO.getId() != null) {
            try {
                Session session = sessionFactory.openSession();
                session.beginTransaction();
                userDTO = session.get(UserDTO.class, sessionDTO.getUserId().getId());
                session.getTransaction().commit();
            } finally {
                sessionFactory.close();
            }
            return userDTO;
        }
        return new UserDTO();
    }
}