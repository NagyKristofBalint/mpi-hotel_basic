package edu.codespring.mpihotel.web;

import com.github.jknack.handlebars.Template;
import edu.codespring.mpihotel.backend.model.Bookings;
import edu.codespring.mpihotel.backend.service.ServiceException;
import edu.codespring.mpihotel.backend.service.ServiceFactory;
import edu.codespring.mpihotel.web.templates.HandlebarsTemplateFactory;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@WebServlet("/index.hbs")
public class IndexServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(IndexServlet.class);

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Map<String, Object> model = new ConcurrentHashMap<>();
        model.put("username", req.getSession().getAttribute("username"));

        Template template;
        List<Bookings> bookingsOfActualUser = null;

        try {
            template = HandlebarsTemplateFactory.getTemplate("index");

            bookingsOfActualUser = ServiceFactory.getInstance().getBookingsService().
                    getAllByUserID(Long.parseLong(req.getSession().getAttribute("userId").toString()));


            List<Map<String, Object>> bookingDatesOfActualUser = new LinkedList<>();
            if (bookingsOfActualUser != null) {
                for (Bookings b : bookingsOfActualUser) {
                    Map<String, Object> bookingElement = new ConcurrentHashMap<>();
                    bookingElement.put("id", b.getId());
                    bookingElement.put("date", b.getStartDate());
                    bookingDatesOfActualUser.add(bookingElement);
                }
            }

            model.put("list", bookingDatesOfActualUser);

            // rendering
            LOG.info("Main page has been built up successfully;");
            template.apply(model, res.getWriter());

        } catch (IOException e) {
            LOG.info("Failed to load template; " + e.getMessage(), e);
            res.getWriter().println("Failed to load template; " + e.getMessage());
            res.setStatus(500);
        } catch (ServiceException e) {
            LOG.error("Failed to insert bookings of user {} to page; " + e.getMessage(), req.getSession().getAttribute("username"), e);
            res.getWriter().println("Failed to insert bookings of user {} to page; " + e.getMessage());
            res.setStatus(204);
        }
    }
}