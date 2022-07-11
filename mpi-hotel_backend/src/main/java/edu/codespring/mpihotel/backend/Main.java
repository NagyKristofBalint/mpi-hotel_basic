package edu.codespring.mpihotel.backend;

import edu.codespring.mpihotel.backend.model.User;
import edu.codespring.mpihotel.backend.repository.AbstractDAOFactory;
import edu.codespring.mpihotel.backend.repository.BookingsDAO;
import edu.codespring.mpihotel.backend.repository.HotelDAO;
import edu.codespring.mpihotel.backend.repository.UserDAO;

public class Main {
    public static void main(String[] args) {
        System.out.println("ALMA");
        UserDAO userDAO = AbstractDAOFactory.getInstance().getUserDAO();
        HotelDAO hotelDAO = AbstractDAOFactory.getInstance().getHotelDAO();
        BookingsDAO bookingsDAO = AbstractDAOFactory.getInstance().getBookingsDAO();
        try {
            //hotelDAO.getById(5L);

            userDAO.create(new User("dummyUser", "dummyPassword"));
            for(User u : userDAO.getAllUsers()){
                //userDAO.delete(u.getId());
                System.out.println(u);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
