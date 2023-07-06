package com.garanin.weather.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;

@WebFilter("/*")
public class UserFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //описать логику проверки есть ли данный пользователь с куками в БД
        //если нет, то перекинуть на страницу авторизации, если есть, то перекинуть на страницу погоды
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
