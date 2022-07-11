package edu.codespring.mpihotel.backend.repository;

import edu.codespring.mpihotel.backend.model.Hotel;

import java.util.List;

public interface HotelDAO {
    Hotel getById(Long id);
    Hotel getByHotelName(String hotelName);
    List<Hotel> getAllHotels();
}
