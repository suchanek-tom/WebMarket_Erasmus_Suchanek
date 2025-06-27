package com.webmarket.controller.admin;

import com.webmarket.dao.PurchaseProposalDAO;
import com.webmarket.dao.PurchaseRequestDAO;
import com.webmarket.model.PurchaseRequest;
import com.webmarket.utils.FreemarkerConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/dashboard")
public class AdminController extends HttpServlet {

    private final PurchaseRequestDAO requestDAO = new PurchaseRequestDAO();
    private final PurchaseProposalDAO proposalDAO = new PurchaseProposalDAO();

    /**
     * Renderuje admin dashboard s přehledem všech požadavků
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Ověření role
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login?error=unauthorized");
            return;
        }

        // ✅ Vynucení načtení čerstvé stránky (žádné cachování)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        // ✅ Načtení všech požadavků včetně jmen kategorií a uživatelů
        List<PurchaseRequest> requests = requestDAO.findAllWithCategoryAndUser();

        // ✅ Příprava Freemarker šablony
        Configuration cfg = FreemarkerConfig.getConfig();
        Template template = cfg.getTemplate("admin_dashboard.ftl.html");

        Map<String, Object> data = new HashMap<>();
        data.put("requests", requests);
        data.put("username", session.getAttribute("username"));

        // ✅ Volitelná hláška po akci
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

    /**
     * Zpracuje akce schválení, odmítnutí, mazání a výběru vítězného návrhu
     */
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

        // ✅ Uložení hlášky a redirect
        session.setAttribute("message", message);
        response.sendRedirect(request.getContextPath() + "/admin/dashboard");
    }
}
