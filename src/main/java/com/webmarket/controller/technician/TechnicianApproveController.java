package com.webmarket.controller.technician;

import com.webmarket.dao.PurchaseRequestDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/technician/approve")
public class TechnicianApproveController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int requestId = Integer.parseInt(request.getParameter("id"));
        requestDAO.updateStatus(requestId, "approved");

        response.sendRedirect("dashboard");
    }
}
