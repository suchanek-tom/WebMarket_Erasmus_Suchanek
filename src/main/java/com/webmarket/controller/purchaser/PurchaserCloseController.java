package com.webmarket.controller.purchaser;

import com.webmarket.dao.PurchaseRequestDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/purchaser/close")
public class PurchaserCloseController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"purchaser".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        int requestId = Integer.parseInt(request.getParameter("requestId"));
        String result = request.getParameter("result");

        if (!("accepted".equals(result) || 
              "rejected_noncompliant".equals(result) || 
              "rejected_notworking".equals(result))) {
            session.setAttribute("message", "Invalid closure reason selected.");
            response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
            return;
        }

        boolean success = requestDAO.updateStatus(requestId, result);
        if (success) {
            session.setAttribute("message", "Request #" + requestId + " successfully closed as: " + result.replace("_", " "));
        } else {
            session.setAttribute("message", "Error closing the request.");
        }

        response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
    }
}
