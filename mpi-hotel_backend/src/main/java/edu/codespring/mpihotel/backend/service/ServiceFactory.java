package edu.codespring.mpihotel.backend.service;

import edu.codespring.mpihotel.backend.service.impl.BookingsServiceImpl;
import edu.codespring.mpihotel.backend.service.impl.HotelServiceImpl;
import edu.codespring.mpihotel.backend.service.impl.UserServiceImpl;

public class ServiceFactory {
    private static ServiceFactory instance;

    private ServiceFactory() {
    }

    public UserService getUserService() {
        return new UserServiceImpl();
    }

    public HotelService getHotelService() {
        return new HotelServiceImpl();
    }

    public BookingsService getBookingsService() {
        return new BookingsServiceImpl();
    }

    public static ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }

        return instance;
    }
}
