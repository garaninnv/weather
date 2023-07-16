package com.garanin.weather.dao;

import com.garanin.weather.dto.LocationDTO;
import com.garanin.weather.dto.SessionDTO;
import com.garanin.weather.dto.UserDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

public class UserDAO {
    public UserDTO findUserLogPas(String user, String pas) {
        UserDTO userDTO;
        List<UserDTO> list;

        Configuration con = new Configuration().configure("hibernate.cfg.xml")
                .addAnnotatedClass(SessionDTO.class).addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            list = session.createQuery("from UserDTO where login='" + user + "'").getResultList();
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }

        if (list.isEmpty()) {
            return new UserDTO();
        } else {
            userDTO = list.get(0);
        }

        if (BCrypt.checkpw(pas, userDTO.getPassword())) {
            return userDTO;
        }
        return new UserDTO();
    }
}