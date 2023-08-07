package com.garanin.weather.servlet;

import com.garanin.weather.dao.UserDAO;
import com.garanin.weather.util.ThymeleafUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;

@WebServlet("/login")
public class LogInServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher requestDispatcher = req.getRequestDispatcher("view/login.html");
        requestDispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String user = req.getParameter("login");
        String pas = req.getParameter("password");

        if (user != null && pas != null && !user.equals("") && !pas.equals("")) {
            if (userDAO.findUser(user, pas, resp)) {
                resp.sendRedirect("/weather/index");
            } else {
                boolean nouser = true;
                TemplateEngine engine = ThymeleafUtil.buildTemplateEngine(req.getServletContext());
                WebContext context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
                context.setVariable("login", user);
                context.setVariable("nouser", nouser);
                engine.process("login", context, resp.getWriter());
            }
        }
    }
}