package com.garanin.weather.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garanin.weather.dto.LocationDTO;
import com.garanin.weather.dto.SessionDTO;
import com.garanin.weather.dto.UserDTO;
import com.garanin.weather.service.model.LocationModel;
import com.garanin.weather.service.model.WeatherModel;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public Map<LocationDTO, WeatherModel> selectWeather(UserDTO userDTO) throws IOException {
        String apiKey = "5e595bcf79c3f89d0f975bf24850ed3d";
        ObjectMapper objectMapper = new ObjectMapper();
        Map<LocationDTO, WeatherModel> modelMap = new HashMap<>();
      //  Hibernate.initialize(userDTO.getLocationList());
        List<LocationDTO> locationDTOList = userDTO.getLocationList();

        for (LocationDTO el : locationDTOList) {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + el.getLatitude()
                    + "&lon=" + el.getLongitude() + "&units=metric&appid=" + apiKey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");


            try {
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line;
                line = reader.readLine();

                WeatherModel weatherModel = objectMapper.readValue(line, WeatherModel.class);
                modelMap.put(el, weatherModel);

                reader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.disconnect();
        }
        return modelMap;
    }
}