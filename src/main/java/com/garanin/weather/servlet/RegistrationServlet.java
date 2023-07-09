package com.garanin.weather.servlet;

import com.garanin.weather.dto.LocationDTO;
import com.garanin.weather.dto.SessionDTO;
import com.garanin.weather.dto.UserDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("authorizationForm/registration.html");
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
        if (!user.equals("") && !pas.equals("")) {
            newUser.setLogin(user);
            String salt = BCrypt.gensalt();
            String hachPas = BCrypt.hashpw(pas, salt);
            newUser.setPassword(hachPas);
        }
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(newUser);
            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}


