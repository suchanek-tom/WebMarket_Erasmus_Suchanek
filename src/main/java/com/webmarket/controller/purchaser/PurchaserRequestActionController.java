package com.webmarket.controller.purchaser;

import com.webmarket.dao.PurchaseProposalDAO;
import com.webmarket.dao.PurchaseRequestDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/purchaser/requests/action")
public class PurchaserRequestActionController extends HttpServlet {

    private final PurchaseProposalDAO proposalDAO = new PurchaseProposalDAO();
    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"purchaser".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        String action = request.getParameter("action");
        int proposalId = Integer.parseInt(request.getParameter("proposalId"));
        int requestId = Integer.parseInt(request.getParameter("requestId"));

        if ("accept".equals(action)) {
            boolean marked = proposalDAO.markAsWinner(proposalId);
            boolean updated = requestDAO.updateStatus(requestId, "completed");

            if (marked && updated) {
                session.setAttribute("message", "Proposal accepted and request marked as completed.");
            } else {
                session.setAttribute("message", "Error while accepting the proposal.");
            }

        } else if ("reject".equals(action)) {
            proposalDAO.rejectProposal(proposalId);
            session.setAttribute("message", "Proposal rejected.");
        }

        response.sendRedirect(request.getContextPath() + "/purchaser/requests/detail?requestId=" + requestId);
    }
}
