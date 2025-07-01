package com.webmarket.controller.purchaser;

import com.webmarket.dao.PurchaseRequestDAO;
import com.webmarket.model.PurchaseRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/purchaser/cancel")
public class PurchaserCancelController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"purchaser".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        int purchaserId = (int) session.getAttribute("user_id");
        int requestId = Integer.parseInt(request.getParameter("requestId"));

        PurchaseRequest req = requestDAO.findById(requestId);
        if (req == null || req.getPurchaserId() != purchaserId || !"pending".equals(req.getStatus())) {
            session.setAttribute("message", "You can only cancel your own pending requests.");
            response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
            return;
        }

        boolean updated = requestDAO.updateStatus(requestId, "cancelled");
        if (updated) {
            session.setAttribute("message", "Request #" + requestId + " has been cancelled.");
        } else {
            session.setAttribute("message", "Failed to cancel request.");
        }

        response.sendRedirect(request.getContextPath() + "/purchaser/dashboard");
    }
}
