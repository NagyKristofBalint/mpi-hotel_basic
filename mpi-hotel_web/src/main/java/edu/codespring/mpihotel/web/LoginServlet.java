package edu.codespring.mpihotel.web;

import edu.codespring.mpihotel.backend.model.User;
import edu.codespring.mpihotel.backend.repository.jdbc.ConnectionManager;
import edu.codespring.mpihotel.backend.service.ServiceException;
import edu.codespring.mpihotel.backend.service.ServiceFactory;
import edu.codespring.mpihotel.backend.service.UserService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(LoginServlet.class);

    private UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        userService = ServiceFactory.getInstance().getUserService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if (username == null || password == null || username.length() == 0 || password.length() == 0) {
            resp.setStatus(406);
            resp.getWriter().println("No username or password given");
            LOG.info("No username or password given");
            return;
        }

        try {
            if (!userService.login(new User(username, password))) {
                resp.setStatus(406);
                resp.getWriter().println("Wrong username or password");
                LOG.info("Wrong username or password");
                return;
            }

            req.getSession().setAttribute("username", username);
            req.getSession().setAttribute("userId", userService.getByName(username).getId());
            LOG.info("User {} has logged in successfully, redirecting to main page...;", username);

            resp.sendRedirect(req.getContextPath() + "/index.hbs");
        } catch (ServiceException e) {
            LOG.error("Failed to login user " + e.getMessage() + "; ", e);
            resp.getWriter().println("Failed to login user " + e.getMessage() + "; ");
            resp.setStatus(500);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/login.html");
    }

    @Override
    public void destroy() {
        try {
            ConnectionManager.closeConnections();
            LOG.info("Connections have been closed;");
        } catch (SQLException e) {
            LOG.error("Failed to close connections;" + e);
        }
    }

}
