package edu.codespring.mpihotel.web;

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

@WebServlet("/deleteBooking")
public class DeleteBookingServlet extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteBookingServlet.class);

    private BookingsService bookingsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        bookingsService = ServiceFactory.getInstance().getBookingsService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long bookingId = Long.parseLong(req.getParameter("bookingIdToDelete"));
        try {
            bookingsService.delete(bookingId);

            LOG.info("Booking with id {} has been deleted successfully;", bookingId);
        } catch (ServiceException e) {
            LOG.error("Failed to delete booking with id {}; " + e.getMessage(), bookingId, e);
            resp.getWriter().println("Failed to delete booking with id " + bookingId + "; " + e.getMessage());
            resp.setStatus(500);
        }
    }
}
