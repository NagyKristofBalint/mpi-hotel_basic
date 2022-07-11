package edu.codespring.mpihotel.web;

import edu.codespring.mpihotel.backend.service.ServiceFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebFilter("/*")
public class LoginFilter extends HttpFilter {
    private static final Logger LOG = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public void init(FilterConfig config) throws ServletException {
        //initialize database
        ServiceFactory.getInstance().getHotelService();
    }

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String username = (String) req.getSession().getAttribute("username");
        //cannot register, after login///////////////////////
        if (req.getRequestURL().toString().equals("http://localhost:8080/mpi-hotel_web/login.html") ||
                req.getRequestURL().toString().equals("http://localhost:8080/mpi-hotel_web/login") ||
                req.getRequestURL().toString().equals("http://localhost:8080/mpi-hotel_web/register") ||
                req.getRequestURL().toString().equals("http://localhost:8080/mpi-hotel_web/register.html")) {
            if (username == null) {
                chain.doFilter(req, res);
            } else {
                LOG.info("User {} is logged in already, redirecting to main page;", username);
                res.sendRedirect(req.getContextPath() + "/index.hbs");
            }
        } else {
            if (username == null) {
                LOG.info("Unauthorized access attempt, redirecting to login page...;");
                res.sendRedirect(req.getContextPath() + "/login.html");
            } else {
                LOG.info("User {} is authorized to access page;", username);
                chain.doFilter(req, res);
            }
        }
    }
}
