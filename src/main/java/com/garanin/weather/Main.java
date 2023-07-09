package com.garanin.weather;

import com.garanin.weather.dto.LocationDTO;
import com.garanin.weather.dto.SessionDTO;
import com.garanin.weather.dto.UserDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        Configuration con = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class).addAnnotatedClass(SessionDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());

        UserDTO user = new UserDTO();
        user.setLogin("nikolas");
        user.setPassword("123");

        LocationDTO location = new LocationDTO();
        location.setName("Tbilisi");
        location.setUserId(user);
        location.setLatitude(123);
        location.setLongitude(321);

        SessionDTO sessionDTO = new SessionDTO();
        sessionDTO.setUserId(user);
        sessionDTO.setExpiresAt(LocalDate.now().atStartOfDay());

        user.setLocationList(new ArrayList<>(Collections.singletonList(location)));
        location.setUserId(user);

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(user);
            //session.save(location);
            session.save(sessionDTO);
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
