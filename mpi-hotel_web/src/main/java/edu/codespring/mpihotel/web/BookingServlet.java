package edu.codespring.mpihotel.web;

import edu.codespring.mpihotel.backend.model.Bookings;
import edu.codespring.mpihotel.backend.service.BookingsService;
import edu.codespring.mpihotel.backend.service.ServiceException;
import edu.codespring.mpihotel.backend.service.ServiceFactory;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet("/booking")
public class BookingServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(BookingServlet.class);

    private static BookingsService bookingsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        bookingsService = ServiceFactory.getInstance().getBookingsService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getParameter("booking-date").length() == 0) {
            LOG.warn("Booking date wasn't selected properly;");
            resp.getWriter().println("Booking date wasn't selected properly");
            resp.setStatus(400);
            return;
        }

        Date bookingDate = Date.valueOf(req.getParameter("booking-date"));

        try {
            if (bookingsService.canBookOnDate(bookingDate)) {
                List<Long> availableRooms = bookingsService.getAvailableRoomsOnDate(bookingDate);
                bookingsService.create(new Bookings(Long.parseLong(req.getSession().getAttribute("userId").toString()),
                        availableRooms.get(0), bookingDate, bookingDate, 1L));

                LOG.info("User {} has booked on date {} successfully, reloading page...;", req.getSession().getAttribute("username"), bookingDate);
                resp.sendRedirect(req.getContextPath() + "/index.hbs");
            } else {
                LOG.warn("No room left on {};", bookingDate);
                resp.setStatus(409);
                resp.getWriter().println("No room left on " + bookingDate);
            }
        } catch (ServiceException e) {
            LOG.error("Failed to book " + e.getMessage() + "; ", e);
            resp.getWriter().println("Failed to book " + e.getMessage() + "; ");
            resp.setStatus(500);
        }
    }
}
