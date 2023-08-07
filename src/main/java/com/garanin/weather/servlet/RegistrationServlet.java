package com.garanin.weather.servlet;

import com.garanin.weather.dao.UserDAO;
import com.garanin.weather.util.ThymeleafUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
    UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("view/registration.html");
        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String user = req.getParameter("login").toLowerCase();
        String pas = req.getParameter("password");
        if (pas != null && !user.equals("") && !pas.equals("")) {
            if (userDAO.userDoesNotExist(user)) {
                userDAO.adduser(user, pas, resp);
                resp.sendRedirect("/weather/index");
            } else {
                boolean doubleuser = true;
                TemplateEngine engine = ThymeleafUtil.buildTemplateEngine(req.getServletContext());
                WebContext context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
                context.setVariable("doubleuser", doubleuser);
                engine.process("registration", context, resp.getWriter());
            }

        } else {
            boolean nologin = true;
            TemplateEngine engine = ThymeleafUtil.buildTemplateEngine(req.getServletContext());
            WebContext context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
            context.setVariable("login", user);
            context.setVariable("nologin", nologin);
            engine.process("registration", context, resp.getWriter());
        }
    }
}