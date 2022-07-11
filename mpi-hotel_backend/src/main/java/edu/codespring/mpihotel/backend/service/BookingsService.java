package edu.codespring.mpihotel.backend.service;

import edu.codespring.mpihotel.backend.model.Bookings;

import java.sql.Date;
import java.util.List;

public interface BookingsService {
    void create(Bookings booking);
    boolean canBookOnDate(Date date);
    List<Long> getAvailableRoomsOnDate(Date date);
    void delete(Long id);
    Bookings getById(Long id);
    List<Bookings> getAll();
    List<Bookings> getAllByUserID(Long id);
}
