package com.garanin.weather.service;

import com.garanin.weather.dao.SessionDAO;
import com.garanin.weather.dto.LocationDTO;
import com.garanin.weather.dto.SessionDTO;
import com.garanin.weather.dto.UserDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class DeletingOldSessions {
    public static void removeSession() {
        Configuration con = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(SessionDTO.class).addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());
        SessionDAO sessionDAO = new SessionDAO();
        List<SessionDTO> sessionDTOList;
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            sessionDTOList = session.createQuery("from SessionDTO").getResultList();
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
        for (SessionDTO el : sessionDTOList) {
            Duration duration = Duration.between(LocalDateTime.now(), el.getExpiresAt());
            if (duration.toHours() <= 0) {
                sessionDAO.delete(el);
            }
        }
    }
}