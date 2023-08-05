package com.garanin.weather.dao;

import com.garanin.weather.dto.LocationDTO;
import com.garanin.weather.dto.SessionDTO;
import com.garanin.weather.dto.UserDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class LocationDAO {
    public LocationDTO createLocation(String name, double lat, double lon, UserDTO userDTO) {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setUserId(userDTO);
        locationDTO.setName(name);
        locationDTO.setLatitude(lat);
        locationDTO.setLongitude(lon);
        return locationDTO;
    }

    public void addLocation(LocationDTO locationDTO) {
        Configuration con = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class).addAnnotatedClass(SessionDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(locationDTO);
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }

    public void delete(int locationID) {
        Configuration con = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class).addAnnotatedClass(SessionDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());

        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            LocationDTO locationDTO = session.get(LocationDTO.class, locationID);
            UserDTO userDTO = locationDTO.getUserId();
            List<LocationDTO> list = userDTO.getLocationList();
            list.remove(locationDTO);
            session.save(userDTO);
            session.delete(locationDTO);
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
