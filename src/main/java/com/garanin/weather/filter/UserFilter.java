package com.garanin.weather.filter;

import com.garanin.weather.dao.SessionDAO;
import com.garanin.weather.dto.SessionDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@WebFilter(urlPatterns = {"/"})
public class UserFilter implements Filter {

    private SessionDAO sessionDAO = new SessionDAO();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //описать логику проверки есть ли данный пользователь с куками в БД
        //если нет, то перекинуть на страницу авторизации, если есть, то перекинуть на главную страницу

        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        UUID uuidCookie = null;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("weather")) {
                    uuidCookie = UUID.fromString(c.getValue());
                }
            }
        }
        Optional<SessionDTO> sessionDTO = sessionDAO.findById(uuidCookie);

        if (sessionDTO.isPresent()) {
            if (sessionDTO.get().getId() != null) {
                request.getRequestDispatcher("view/index.html").forward(request, response);
            } else {
                request.getRequestDispatcher("/login").forward(request, response);
            }
        } else {
            request.getRequestDispatcher("/login").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
