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

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(RegisterServlet.class);

    private UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        userService = ServiceFactory.getInstance().getUserService();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = req.getParameter("username");
        String password1 = req.getParameter("password1");
        String password2 = req.getParameter("password2");
        if (username == null || password1 == null || password2 == null ||
                username.length() == 0 || password1.length() == 0 || password2.length() == 0) {
            resp.setStatus(406);
            resp.getWriter().println("No username or password given");
            return;
        }

        if (!password1.equals(password2)) {
            resp.setStatus(406);
            resp.getWriter().println("The two passwords don't match");
            return;
        }

        Long id;
        try {
            id = userService.register(new User(username, password1));
        } catch (ServiceException e) {
            LOG.error("Registration failed; " + e.getMessage(), e);
            resp.getWriter().println("Registration failed; " + e.getMessage());
            resp.setStatus(500);
            return;
        }

        if (id == null) {
            LOG.info("User {} is already registered;", username);
            resp.getWriter().println("User " + username + " is already registered;");
            resp.setStatus(406);
            return;
        }

        req.getSession().setAttribute("userId", id);
        req.getSession().setAttribute("username", username);

        LOG.info("User {} has successfully registered;", username);
        resp.sendRedirect(req.getContextPath() + "/index.hbs");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/register.html");
    }

    @Override
    public void destroy() {
        try {
            ConnectionManager.closeConnections();
            LOG.info("Connections have been closed;");
        } catch (SQLException e) {
            LOG.error("Failed to close connections;" + e.getMessage(), e);
        }
    }

}
