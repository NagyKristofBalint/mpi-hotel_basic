package edu.codespring.mpihotel.backend.service.impl;

import edu.codespring.mpihotel.backend.repository.AbstractDAOFactory;
import edu.codespring.mpihotel.backend.repository.HotelDAO;
import edu.codespring.mpihotel.backend.service.HotelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotelServiceImpl implements HotelService {
    private static final Logger LOG = LoggerFactory.getLogger(HotelServiceImpl.class);
    public static int HOTEL_ID;

    private final HotelDAO hotelDAO;

    public HotelServiceImpl() {
        hotelDAO = AbstractDAOFactory.getInstance().getHotelDAO();
    }

}
