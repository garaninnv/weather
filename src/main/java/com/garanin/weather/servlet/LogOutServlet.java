package com.garanin.weather.servlet;

import com.garanin.weather.dao.SessionDAO;
import com.garanin.weather.dto.SessionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/logout")
public class LogOutServlet extends HttpServlet {
    SessionDAO sessionDAO = new SessionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        Cookie cookie = Arrays.stream(cookies).filter(n -> n.getName().equals("weather")).findFirst().orElse(null);

        Optional<SessionDTO> sessionDTO = sessionDAO.findById(UUID.fromString(cookie.getValue()));
        sessionDAO.delete(sessionDTO);
        cookie = new Cookie("weather", null);
        cookie.setMaxAge(0);
        resp.addCookie(cookie);
        resp.sendRedirect("/weather/login");
    }
}