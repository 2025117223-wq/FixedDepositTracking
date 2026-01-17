package Controller;

import Bean.Bank;
import DAO.BankDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@WebServlet("/BankController")
public class BankController extends HttpServlet {
    private BankDAO bankDAO;

    @Override
    public void init() {
        bankDAO = new BankDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "list";

        try {
            switch (action) {
                case "edit":
                    showEditForm(request, response);
                    break;
                case "list":
                default:
                    listBanks(request, response);
                    break;
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try {
            if ("add".equals(action)) {
                insertBank(request, response);
            } else if ("update".equals(action)) {
                updateBank(request, response);
            } else {
                // unknown action
                response.sendRedirect("BankController?action=list");
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

    private void listBanks(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        request.setAttribute("listBank", bankDAO.getAllBanks());
        request.getRequestDispatcher("BankList.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect("BankController?action=list");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("BankController?action=list");
            return;
        }

        Bank bank = bankDAO.getBankById(id);
        if (bank == null) {
            // bank not found
            response.sendRedirect("BankController?action=list");
            return;
        }

        request.setAttribute("bank", bank);
        request.getRequestDispatcher("EditBank.jsp").forward(request, response);
    }

    private void insertBank(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        String name = request.getParameter("bankName");
        String address = request.getParameter("bankAddress");
        String phone = request.getParameter("bankPhone");

        if (isBlank(name) || isBlank(address) || isBlank(phone)) {
            request.setAttribute("error", "Please fill in all fields.");
            request.getRequestDispatcher("CreateBank.jsp").forward(request, response);
            return;
        }

        Bank bank = new Bank(name.trim(), address.trim(), phone.trim());

        if (bankDAO.insertBank(bank)) {
            response.sendRedirect("BankController?action=list&msg=created&bn="
                    + URLEncoder.encode(name.trim(), StandardCharsets.UTF_8));
        } else {
            request.setAttribute("error", "Failed to create bank.");
            request.getRequestDispatcher("CreateBank.jsp").forward(request, response);
        }
    }

    private void updateBank(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {

        String idStr = request.getParameter("bankId");
        String name = request.getParameter("bankName");
        String address = request.getParameter("bankAddress");
        String phone = request.getParameter("bankPhone");

        if (isBlank(idStr) || isBlank(name) || isBlank(address) || isBlank(phone)) {
            request.setAttribute("error", "Please fill in all fields.");
            request.getRequestDispatcher("EditBank.jsp").forward(request, response);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid bank ID.");
            request.getRequestDispatcher("EditBank.jsp").forward(request, response);
            return;
        }

        Bank bank = new Bank(id, name.trim(), address.trim(), phone.trim());

        if (bankDAO.updateBank(bank)) {
            response.sendRedirect("BankController?action=list&msg=updated&bn="
                    + URLEncoder.encode(name.trim(), StandardCharsets.UTF_8));
        } else {
            request.setAttribute("error", "Failed to update bank.");
            request.setAttribute("bank", bank); // so fields still show
            request.getRequestDispatcher("EditBank.jsp").forward(request, response);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
