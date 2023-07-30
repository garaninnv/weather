package com.garanin.weather.dao;

import com.garanin.weather.dto.LocationDTO;
import com.garanin.weather.dto.SessionDTO;
import com.garanin.weather.dto.UserDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.Optional;
import java.util.UUID;

public class SessionDAO {
    public Optional<SessionDTO> findById(UUID id) {
        SessionDTO sessionDTO = new SessionDTO();
        if (id == null) {
            return Optional.of(sessionDTO);
        }
        Configuration con = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(SessionDTO.class).addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            sessionDTO = session.get(SessionDTO.class, id);
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }

        if (sessionDTO != null) {
            return Optional.of(sessionDTO);
        } else {
            return Optional.empty();
        }
    }

    public void delete(Optional<SessionDTO> sessionDTO) {
        Configuration con = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(SessionDTO.class).addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(sessionDTO.get());
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}