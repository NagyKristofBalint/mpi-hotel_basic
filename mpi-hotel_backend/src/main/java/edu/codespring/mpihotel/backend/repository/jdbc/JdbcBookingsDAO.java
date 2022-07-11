package edu.codespring.mpihotel.backend.repository.jdbc;

import edu.codespring.mpihotel.backend.model.Bookings;
import edu.codespring.mpihotel.backend.repository.BookingsDAO;
import edu.codespring.mpihotel.backend.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcBookingsDAO extends JdbcAbstractDAO implements BookingsDAO {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcBookingsDAO.class);
    private static boolean tableCreated = false;

    public JdbcBookingsDAO() {
        if (!tableCreated) {
            tableCreated = true;
            createTable("""
                    CREATE TABLE IF NOT EXISTS `mpi-hotel`.`Bookings` (
                      `id` INT NOT NULL AUTO_INCREMENT,
                      `uuid` VARCHAR(45) NOT NULL,
                      `userID` INT NOT NULL,
                      `roomID` INT NOT NULL,
                      `startDate` DATE NOT NULL,
                      `endDate` DATE NOT NULL,
                      `hotelID` INT NOT NULL,
                      INDEX `FK_Bookings_Room_idx` (`roomID` ASC) VISIBLE,
                      PRIMARY KEY (`id`),
                      CONSTRAINT `FK_Bookings_User`
                    	FOREIGN KEY (`userID`)
                    	REFERENCES `mpi-hotel`.`User` (`id`)
                    	ON DELETE NO ACTION
                    	ON UPDATE NO ACTION,
                      CONSTRAINT `FK_Bookings_Room`
                    	FOREIGN KEY (`roomID`)
                    	REFERENCES `mpi-hotel`.`Room` (`id`)
                    	ON DELETE NO ACTION
                    	ON UPDATE NO ACTION,
                      CONSTRAINT `FK_Bookings_Hotel`
                    	FOREIGN KEY (`hotelID`)
                    	REFERENCES `mpi-hotel`.`Hotel` (`id`)
                    	ON DELETE NO ACTION
                    	ON UPDATE NO ACTION,
                      CONSTRAINT `CK_Bookings_Dates`
                    	CHECK (`startDate` <= `endDate`)
                        )
                    ENGINE = InnoDB;
                    """, "Bookings");
        }
        connectionManager = ConnectionManager.getInstance();
    }

    @Override
    public Bookings create(Bookings booking) {
        Connection c = null;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("INSERT INTO Bookings (`uuid`, `userID`, `roomID`, `startDate`, `endDate`, `hotelID`) VALUES (?, ? ,?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
            prepStmt.setString(1, booking.getUuid());
            prepStmt.setLong(2, booking.getUserID());
            prepStmt.setLong(3, booking.getRoomID());
            prepStmt.setDate(4, booking.getStartDate());
            prepStmt.setDate(5, booking.getEndDate());
            prepStmt.setLong(6, booking.getHotelID());
            prepStmt.execute();

            ResultSet rs = prepStmt.getGeneratedKeys();
            rs.next();

            booking.setId(rs.getLong(1));

            LOG.info("Booking with id " + booking.getId() + " has been created");
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to create booking " + e.getMessage(), e);
            throw new RepositoryException("Failed to create booking " + e.getMessage(), e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }

        return booking;
    }

    @Override
    public void update(Bookings booking) {
        Connection c = null;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("UPDATE Bookings SET `userID` = ?, `roomID` = ?, `startDate` = ?, `endDate` = ?, `hotelID` = ? WHERE `id` = ?;");
            prepStmt.setLong(1, booking.getUserID());
            prepStmt.setLong(2, booking.getRoomID());
            prepStmt.setDate(3, booking.getStartDate());
            prepStmt.setDate(4, booking.getEndDate());
            prepStmt.setLong(5, booking.getHotelID());
            prepStmt.execute();

            LOG.info("Booking with id " + booking.getId() + " has been updated");
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to update booking " + e.getMessage(), e);
            throw new RepositoryException("Failed to update booking " + e.getMessage(), e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }
    }

    @Override
    public void delete(Long id) {
        Connection c = null;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("DELETE FROM Bookings WHERE `id` = ?;");
            prepStmt.setLong(1, id);
            prepStmt.execute();

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to delete booking " + e.getMessage(), e);
            throw new RepositoryException("Failed to delete booking " + e.getMessage(), e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }
    }

    @Override
    public int numberOfBookingsOnDate(Date date) {
        Connection c = null;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("SELECT COUNT(*) FROM Bookings WHERE Bookings.`startDate` = ?");
            prepStmt.setDate(1, date);

            ResultSet rs = prepStmt.executeQuery();
            rs.next();

            return rs.getInt(1);
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query number of bookings on date {};" + e.getMessage(), date, e);
            throw new RepositoryException("Failed to query number of bookings on date " + date + "; " + e.getMessage(), e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }
    }

    @Override
    public List<Long> getAvailableRoomsOnDate(Date date) {
        Connection c = null;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("SELECT id FROM Room WHERE id NOT IN (SELECT Bookings.roomID FROM Bookings WHERE startDate = ?);");
            prepStmt.setDate(1, date);

            ResultSet rs = prepStmt.executeQuery();

            List<Long> roomIDs = new ArrayList<>();
            while (rs.next()) {
                roomIDs.add(rs.getLong(1));
            }

            return roomIDs;
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query available rooms on date {}; " + e.getMessage(), date, e);
            throw new RepositoryException("Failed to query available rooms on date " + date + "; " + e.getMessage(), e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }
    }

    @Override
    public Bookings getById(Long id) {
        Connection c = null;
        Bookings booking;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("SELECT * FROM Bookings WHERE `id` = ?;");
            prepStmt.setLong(1, id);

            ResultSet rs = prepStmt.executeQuery();
            if (!rs.next()) {
                LOG.info("No booking registered with id " + id);
                return null;
            }

            booking = new Bookings();
            booking.setId(id);
            booking.setUuid(rs.getString(2));
            booking.setUserID(rs.getLong(3));
            booking.setRoomID(rs.getLong(4));
            booking.setStartDate(rs.getDate(5));
            booking.setEndDate(rs.getDate(6));
            booking.setHotelID(rs.getLong(7));

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to get booking by id " + e.getMessage(), e);
            throw new RepositoryException("Failed to get booking by id " + e.getMessage(), e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }

        return booking;
    }

    @Override
    public List<Bookings> getAll() {
        Connection c = null;
        List<Bookings> bookings;

        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt;
            prepStmt = c.prepareStatement("SELECT * FROM Bookings;");

            ResultSet rs = prepStmt.executeQuery();

            bookings = new ArrayList<>();
            Bookings tmp;
            while (rs.next()) {
                tmp = new Bookings();
                tmp.setId(rs.getLong(1));
                tmp.setUuid(rs.getString(2));
                tmp.setUserID(rs.getLong(3));
                tmp.setRoomID(rs.getLong(4));
                tmp.setStartDate(rs.getDate(5));
                tmp.setEndDate(rs.getDate(6));
                tmp.setHotelID(rs.getLong(7));

                bookings.add(tmp);
            }

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query all bookings " + e.getMessage(), e);
            throw new RepositoryException("Failed to query all bookings " + e.getMessage(), e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }

        return bookings;
    }
}
