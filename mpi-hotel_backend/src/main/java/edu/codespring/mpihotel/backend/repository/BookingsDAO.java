package edu.codespring.mpihotel.backend.repository;

import edu.codespring.mpihotel.backend.model.Bookings;

import java.sql.Date;
import java.util.List;

public interface BookingsDAO {
    Bookings create(Bookings booking);
    void update(Bookings booking);
    void delete(Long id);
    int numberOfBookingsOnDate(Date date);
    List<Long> getAvailableRoomsOnDate(Date date);
    Bookings getById(Long id);
    List<Bookings> getAll();
}
