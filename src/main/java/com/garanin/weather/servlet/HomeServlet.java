package com.garanin.weather.servlet;


import com.garanin.weather.dao.SessionDAO;
import com.garanin.weather.dao.UserDAO;
import com.garanin.weather.dto.UserDTO;
import com.garanin.weather.util.ThymeleafUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;


import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@WebServlet("/index")
public class HomeServlet extends HttpServlet {
    UserDAO userDAO = new UserDAO();
    UserDTO userDTO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cookie[] cookies = req.getCookies();
        Cookie cookie = Arrays.stream(cookies).filter(n -> n.getName().equals("weather")).findFirst().orElse(null);
        if (cookie != null) {
            userDTO = userDAO.findUserUUIDSession(UUID.fromString(cookie.getValue()));
        }

        TemplateEngine engine = ThymeleafUtil.buildTemplateEngine(req.getServletContext());
        WebContext context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
        context.setVariable("login", userDTO.getLogin());
        engine.process("index", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}