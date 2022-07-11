package edu.codespring.mpihotel.backend.repository.jdbc;

import edu.codespring.mpihotel.backend.repository.BookingsDAO;
import edu.codespring.mpihotel.backend.repository.AbstractDAOFactory;
import edu.codespring.mpihotel.backend.repository.HotelDAO;
import edu.codespring.mpihotel.backend.repository.UserDAO;

public class JdbcDAOFactory extends AbstractDAOFactory {

    @Override
    public UserDAO getUserDAO() {
        return new JdbcUserDAO();
    }

    @Override
    public BookingsDAO getBookingsDAO() {
        return new JdbcBookingsDAO();
    }

    @Override
    public HotelDAO getHotelDAO() {
        return new JdbcHotelDAO();
    }
}
