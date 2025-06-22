package com.webmarket.controller;

import com.webmarket.dao.CategoryDAO;
import com.webmarket.dao.PurchaseRequestDAO;
import com.webmarket.model.Category;
import com.webmarket.model.PurchaseRequest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import com.webmarket.utils.FreemarkerConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/purchaser/request")
public class PurchaserController extends HttpServlet {

    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("purchaser_request.ftl.html");

        Map<String, Object> data = new HashMap<>();
        List<Category> categories = categoryDAO.getAllCategories();
        data.put("categories", categories);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        try {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Template error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int categoryId = Integer.parseInt(request.getParameter("category_id"));
        String notes = request.getParameter("notes");
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        int purchaserId = (int) session.getAttribute("user_id"); // user_id musíš uložit do session

        PurchaseRequest pr = new PurchaseRequest();
        pr.setCategoryId(categoryId);
        pr.setNotes(notes);
        pr.setPurchaserId(purchaserId);

        requestDAO.insert(pr);

        response.sendRedirect("dashboard"); // přesměruj zpět na dashboard
    }
}
