package edu.codespring.mpihotel.backend.service.impl;

import edu.codespring.mpihotel.backend.model.Bookings;
import edu.codespring.mpihotel.backend.repository.AbstractDAOFactory;
import edu.codespring.mpihotel.backend.repository.BookingsDAO;
import edu.codespring.mpihotel.backend.repository.RepositoryException;
import edu.codespring.mpihotel.backend.service.BookingsService;
import edu.codespring.mpihotel.backend.service.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.util.List;

public class BookingsServiceImpl implements BookingsService {
    private static final Logger LOG = LoggerFactory.getLogger(BookingsServiceImpl.class);

    private final BookingsDAO bookingsDAO;

    public BookingsServiceImpl() {
        bookingsDAO = AbstractDAOFactory.getInstance().getBookingsDAO();
    }

    @Override
    public void create(Bookings booking) {
        try {
            bookingsDAO.create(booking);

            LOG.info("Booking on date {} by user with id {} was inserted successfully;", booking.getStartDate(), booking.getUserID());
        } catch (RepositoryException e) {
            LOG.error("Failed to create booking; " + e.getMessage(), e);
            throw new ServiceException("Failed to create booking; " + e.getMessage(), e);
        }
    }

    @Override
    public boolean canBookOnDate(Date date) {
        try {
            return bookingsDAO.numberOfBookingsOnDate(date) < 5;
        } catch (RepositoryException e) {
            LOG.error("Failed to query if it is possible to book on date {};" + e.getMessage(), date, e);
            throw new ServiceException("Failed to query if it is possible to book on date " + date + "; " + e.getMessage(), e);
        }
    }

    @Override
    public List<Long> getAvailableRoomsOnDate(Date date) {
        try {
            return bookingsDAO.getAvailableRoomsOnDate(date);
        } catch (RepositoryException e) {
            LOG.error("Failed to query available rooms on date {}; " + e.getMessage(), date, e);
            throw new ServiceException("Failed to query available rooms on date " + date + "; " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            bookingsDAO.delete(id);
            LOG.info("Booking with id {} has been deleted successfully;", id);
        } catch (RepositoryException e) {
            LOG.error("Failed to delete booking with id {}; " + e.getMessage(), id, e);
            throw new ServiceException("Failed to delete booking with id " + id + "; " + e.getMessage(), e);
        }
    }

    @Override
    public Bookings getById(Long id) {
        Bookings booking;
        try {
            booking = bookingsDAO.getById(id);
            if (booking == null) {
                LOG.info("No booking with id {} ", id);
                return null;
            }

            LOG.info("Booking with id {} was queried successfully;", id);
        } catch (RepositoryException e) {
            LOG.error("Failed to get booking by id; " + e.getMessage(), e);
            throw new ServiceException("Failed to get booking by id; " + e.getMessage(), e);
        }

        return booking;
    }

    @Override
    public List<Bookings> getAll() {
        List<Bookings> list;
        try {
            list = bookingsDAO.getAll();

            LOG.info("All bookings have been queried with success;");
        } catch (RepositoryException e) {
            LOG.error("Failed to query all bookings " + e.getMessage(), e);
            throw new ServiceException("Failed to query all bookings " + e.getMessage(), e);
        }

        return list;
    }

    @Override
    public List<Bookings> getAllByUserID(Long id) {
        List<Bookings> list;
        try {
            list = bookingsDAO.getAll();
            //filter by id
            list.removeIf(b -> !b.getUserID().equals(id));

            LOG.info("Bookings of user with id {} have been queried with success;", id);
        } catch (RepositoryException e) {
            LOG.error("Failed to query all bookings by id" + e.getMessage(), e);
            throw new ServiceException("Failed to query all bookings by id" + e.getMessage(), e);
        }

        return list;
    }
}
