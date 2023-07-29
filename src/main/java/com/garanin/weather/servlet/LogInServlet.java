package com.garanin.weather.servlet;

import com.garanin.weather.dao.UserDAO;
import com.garanin.weather.dto.LocationDTO;
import com.garanin.weather.dto.SessionDTO;
import com.garanin.weather.dto.UserDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@WebServlet("/login")
public class LogInServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("authorizationForm/logIn.html");
        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Configuration con = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(UserDTO.class)
                .addAnnotatedClass(LocationDTO.class).addAnnotatedClass(SessionDTO.class);
        StandardServiceRegistryBuilder sBuilder = new StandardServiceRegistryBuilder()
                .applySettings(con.getProperties());
        SessionFactory sessionFactory = con.buildSessionFactory(sBuilder.build());

        String user = req.getParameter("login");
        String pas = req.getParameter("password");

        UserDTO newUser = new UserDTO();
        LocalDateTime time = LocalDateTime.now();
        SessionDTO sessionDTO = new SessionDTO(UUID.randomUUID(), newUser, time.plus(24, ChronoUnit.HOURS));

        if (!user.equals("") && !pas.equals("")) {
            newUser = userDAO.findUserLogPas(user, pas);
        }

        if (newUser.getLogin() != "") {
            sessionDTO.setUserId(newUser);
            try {
                Session session = sessionFactory.openSession();
                session.beginTransaction();
                session.save(sessionDTO);
                session.getTransaction().commit();
            } finally {
                sessionFactory.close();
            }

            Cookie cookie = new Cookie("weather", sessionDTO.getId().toString());
            cookie.setMaxAge(86400);
            resp.addCookie(cookie);
            resp.sendRedirect("/weather/index");
        } else {
            //такого пользователя не существует или ввели не верный пароль
            req.getRequestDispatcher("/weather/login").forward(req, resp);
        }
    }
}