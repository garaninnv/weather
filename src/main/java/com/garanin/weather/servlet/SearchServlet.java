package com.garanin.weather.servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garanin.weather.dao.LocationDAO;
import com.garanin.weather.dao.UserDAO;
import com.garanin.weather.dto.LocationDTO;
import com.garanin.weather.dto.UserDTO;
import com.garanin.weather.service.model.LocationModel;
import com.garanin.weather.util.ThymeleafUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    UserDTO userDTO;
    LocationDTO locationDTO;
    UserDAO userDAO = new UserDAO();
    LocationDAO locationDAO = new LocationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Cookie[] cookies = req.getCookies();
        Cookie cookie = Arrays.stream(cookies).filter(n -> n.getName().equals("weather")).findFirst().orElse(null);
        if (cookie != null) {
            userDTO = userDAO.findUserUUIDSession(UUID.fromString(cookie.getValue()));
        }

        String apiKey = "5e595bcf79c3f89d0f975bf24850ed3d";
        String city = URLEncoder.encode(req.getParameter("q"), "UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        List<LocationModel> list = new ArrayList<>();

        URL url = new URL("http://api.openweathermap.org/geo/1.0/direct?q=" + city + "&limit=5&appid=" + apiKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");


        try {
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line;
            line = reader.readLine();

            list = objectMapper.readValue(line, new TypeReference<List<LocationModel>>() {
            });

            reader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.disconnect();

        TemplateEngine engine = ThymeleafUtil.buildTemplateEngine(req.getServletContext());
        WebContext context = ThymeleafUtil.buildWebContext(req, resp, getServletContext());
        context.setVariable("login", userDTO.getLogin());
        context.setVariable("list", list);
        engine.process("search", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie[] cookies = req.getCookies();
        Cookie cookie = Arrays.stream(cookies).filter(n -> n.getName().equals("weather")).findFirst().orElse(null);
        if (cookie != null) {
            userDTO = userDAO.findUserUUIDSession(UUID.fromString(cookie.getValue()));
        }
        String name = req.getParameter("name");
        double lat = Double.parseDouble(req.getParameter("lat"));
        double lon = Double.parseDouble(req.getParameter("lon"));
        locationDTO = locationDAO.createLocation(name, lat, lon, userDTO);
        locationDAO.addLocation(locationDTO);
        resp.sendRedirect("/weather/index");
    }
}
