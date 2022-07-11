package edu.codespring.mpihotel.backend.repository;

import edu.codespring.mpihotel.backend.repository.jdbc.JdbcDAOFactory;

public abstract class AbstractDAOFactory {

    public static AbstractDAOFactory getInstance() {
        return new JdbcDAOFactory();
    }

    public abstract UserDAO getUserDAO();

    public abstract BookingsDAO getBookingsDAO();

    public abstract HotelDAO getHotelDAO();
}
