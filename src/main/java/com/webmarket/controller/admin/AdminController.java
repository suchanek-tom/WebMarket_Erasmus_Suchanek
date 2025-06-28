package com.webmarket.controller.admin;

import com.webmarket.dao.PurchaseProposalDAO;
import com.webmarket.dao.PurchaseRequestDAO;
import com.webmarket.model.PurchaseProposal;
import com.webmarket.model.PurchaseRequest;
import com.webmarket.utils.FreemarkerConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/admin/*")
public class AdminController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();
    private final PurchaseProposalDAO proposalDAO = new PurchaseProposalDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        String path = request.getPathInfo();
        if (path == null || path.equals("/") || path.equals("/dashboard")) {
            showDashboard(request, response, session);
        } else if (path.equals("/viewProposals")) {
            showProposals(request, response, session);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        List<PurchaseRequest> requests = requestDAO.findAllWithCategoryAndUser();

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("admin_dashboard.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("requests", requests);
        data.put("username", session.getAttribute("username"));

        String message = (String) session.getAttribute("message");
        if (message != null) {
            data.put("message", message);
            session.removeAttribute("message");
        }

        response.setContentType("text/html; charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Freemarker rendering error", e);
        }
    }

    private void showProposals(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        String reqIdStr = request.getParameter("requestId");
        if (reqIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        int requestId;
        try {
            requestId = Integer.parseInt(reqIdStr);
        } catch (NumberFormatException e) {
            session.setAttribute("message", "Invalid request ID.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        PurchaseRequest pr = requestDAO.findById(requestId);
        if (pr == null) {
            session.setAttribute("message", "Request not found.");
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            return;
        }

        List<PurchaseProposal> proposals = proposalDAO.findByRequestId(requestId);

        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("admin_view_proposals.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("requestId", requestId);
        data.put("proposals", proposals);
        data.put("requestStatus", pr.getStatus());

        response.setContentType("text/html");
        try (PrintWriter out = response.getWriter()) {
            template.process(data, out);
        } catch (Exception e) {
            throw new ServletException("Template error", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        String action = request.getParameter("action");
        String message;

        try {
            int requestId = Integer.parseInt(request.getParameter("requestId"));

            switch (action) {
                case "approve":
                    requestDAO.updateStatus(requestId, "approved");
                    message = "Request #" + requestId + " was approved.";
                    break;

                case "reject":
                    requestDAO.updateStatus(requestId, "rejected");
                    message = "Request #" + requestId + " was rejected.";
                    break;

                case "delete":
                    requestDAO.deleteById(requestId);
                    message = "Request #" + requestId + " was deleted.";
                    break;

                case "selectWinner":
                    int proposalId = Integer.parseInt(request.getParameter("proposalId"));
                    boolean winnerMarked = proposalDAO.setWinner(proposalId, requestId);
                    if (winnerMarked) {
                        requestDAO.updateStatus(requestId, "winner_selected");
                        message = "Proposal #" + proposalId + " was marked as the winner.";
                    } else {
                        message = "Failed to mark proposal as winner.";
                    }
                    break;

                default:
                    message = "Unknown action: " + action;
            }
        } catch (NumberFormatException e) {
            message = "Invalid ID format.";
        }

        session.setAttribute("message", message);
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
