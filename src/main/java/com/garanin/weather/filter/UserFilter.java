package com.garanin.weather.filter;

import com.garanin.weather.dao.SessionDAO;
import com.garanin.weather.dto.SessionDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
                ((HttpServletResponse)response).sendRedirect("/weather/index");
            } else {
                ((HttpServletResponse)response).sendRedirect("/weather/login");
            }
        } else {
            ((HttpServletResponse)response).sendRedirect("/weather/login");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}