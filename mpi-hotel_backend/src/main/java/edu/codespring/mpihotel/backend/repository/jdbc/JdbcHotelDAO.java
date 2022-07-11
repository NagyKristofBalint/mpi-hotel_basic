package edu.codespring.mpihotel.backend.repository.jdbc;

import edu.codespring.mpihotel.backend.model.Hotel;
import edu.codespring.mpihotel.backend.model.Room;
import edu.codespring.mpihotel.backend.repository.HotelDAO;
import edu.codespring.mpihotel.backend.repository.RepositoryException;
import edu.codespring.mpihotel.backend.service.impl.HotelServiceImpl;
import edu.codespring.mpihotel.backend.util.PropertyProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcHotelDAO extends JdbcAbstractDAO implements HotelDAO {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcHotelDAO.class);
    private static boolean tableCreated = false;

    public JdbcHotelDAO() {
        connectionManager = ConnectionManager.getInstance();
        if (!tableCreated) {
            tableCreated = true;
            createTable("""
                    USE `mpi-hotel`;
                    CREATE TABLE `Hotel` (
                      `id` INT NOT NULL AUTO_INCREMENT,
                      `uuid` VARCHAR(45) NOT NULL,
                      `name` VARCHAR(45) NOT NULL,
                      `city` VARCHAR(45) NOT NULL,
                      PRIMARY KEY (`id`))
                    ENGINE = InnoDB;
                                        
                    CREATE TABLE `room` (
                      `id` INT NOT NULL AUTO_INCREMENT,
                      `uuid` VARCHAR(45) NOT NULL,
                      `price` DOUBLE NOT NULL,
                      `capacity` INT NOT NULL,
                      `hotelID` INT NOT NULL,
                      PRIMARY KEY (`id`),
                      INDEX `FK_Room_Hotel_idx` (`hotelID` ASC) VISIBLE,
                      CONSTRAINT `FK_Room_Hotel`
                        FOREIGN KEY (`hotelID`)
                        REFERENCES `mpi-hotel`.`hotel` (`id`))
                    ENGINE = InnoDB;

                    """, "Hotel and Room");
            Connection c = null;
            try {
                c = connectionManager.getConnection();
                //PreparedStatement prepStmt = c.prepareStatement("SELECT * FROM Hotel WHERE `id` = ?;");
                PreparedStatement prepStmt = c.prepareStatement("INSERT INTO hotel (`uuid`, `name`, `city`) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
                prepStmt.setString(1, new Hotel().getUuid());
                prepStmt.setString(2, PropertyProvider.getProperty("hotel_name"));
                prepStmt.setString(3, PropertyProvider.getProperty("hotel_city"));
                prepStmt.executeUpdate();

                ResultSet rs = prepStmt.getGeneratedKeys();
                rs.next();

                HotelServiceImpl.HOTEL_ID = rs.getInt(1);

                LOG.info("Hotel was inserted successfully;");
            } catch (SQLException | RepositoryException e) {
                LOG.error("Failed to insert Hotel; {}", e.getMessage(), e);
                if (c != null) {
                    connectionManager.returnConnection(c);
                }
                throw new RepositoryException("Failed to insert Hotel; " + e.getMessage(), e);
            }

            try {
                PreparedStatement prepStmt = c.prepareStatement("INSERT INTO room (`uuid`, `price`, `capacity`, `hotelID`) VALUES (?, ?, ?, ?)," +
                        "(?, ?, ?, ?), " +
                        "(?, ?, ?, ?), " +
                        "(?, ?, ?, ?), " +
                        "(?, ?, ?, ?);");
                prepStmt.setString(1, new Room().getUuid());
                prepStmt.setDouble(2, 100);
                prepStmt.setInt(3, 1);
                prepStmt.setInt(4, HotelServiceImpl.HOTEL_ID);

                prepStmt.setString(5, new Room().getUuid());
                prepStmt.setDouble(6, 200);
                prepStmt.setInt(7, 2);
                prepStmt.setInt(8, HotelServiceImpl.HOTEL_ID);

                prepStmt.setString(9, new Room().getUuid());
                prepStmt.setDouble(10, 300);
                prepStmt.setInt(11, 3);
                prepStmt.setInt(12, HotelServiceImpl.HOTEL_ID);

                prepStmt.setString(13, new Room().getUuid());
                prepStmt.setDouble(14, 400);
                prepStmt.setInt(15, 4);
                prepStmt.setInt(16, HotelServiceImpl.HOTEL_ID);

                prepStmt.setString(17, new Room().getUuid());
                prepStmt.setDouble(18, 500);
                prepStmt.setInt(19, 5);
                prepStmt.setInt(20, HotelServiceImpl.HOTEL_ID);

                prepStmt.executeUpdate();

            } catch (SQLException | RepositoryException e) {
                LOG.error("Failed to insert rooms; {}", e.getMessage(), e);
                throw new RepositoryException("Failed to insert rooms; " + e.getMessage(), e);
            } finally {
                connectionManager.returnConnection(c);
            }

            LOG.info("Rooms were inserted successfully;");
        }
    }

    @Override
    public Hotel getById(Long id) {
        Connection c = null;
        Hotel hotel;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("SELECT * FROM Hotel WHERE `id` = ?;");
            prepStmt.setLong(1, id);

            ResultSet rs = prepStmt.executeQuery();
            if (!rs.next()) {
                LOG.info("No hotel registered with id " + id);
                return null;
            }

            hotel = new Hotel();
            hotel.setId(id);
            hotel.setUuid(rs.getString(2));
            hotel.setName(rs.getString(3));
            hotel.setCity(rs.getString(4));

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to get hotel by id", e);
            throw new RepositoryException("Failed to get hotel by id", e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }

        return hotel;
    }

    @Override
    public Hotel getByHotelName(String hotelName) {
        Connection c = null;
        Hotel hotel;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("SELECT * FROM Hotel WHERE `name` = ?;");
            prepStmt.setString(1, hotelName);

            ResultSet rs = prepStmt.executeQuery();
            if (!rs.next()) {
                LOG.info("No hotel registered with name " + hotelName);
                return null;
            }

            hotel = new Hotel();
            hotel.setId(rs.getLong(1));
            hotel.setUuid(rs.getString(2));
            hotel.setName(rs.getString(3));
            hotel.setCity(rs.getString(4));

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to get hotel by name", e);
            throw new RepositoryException("Failed to get hotel by name", e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }

        return hotel;
    }

    @Override
    public List<Hotel> getAllHotels() {
        Connection c = null;
        List<Hotel> hotels;

        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("SELECT * FROM Hotel;");
            ResultSet rs = prepStmt.executeQuery();

            hotels = new ArrayList<>();
            Hotel tmp;
            while (rs.next()) {
                tmp = new Hotel();
                tmp.setId(rs.getLong(1));
                tmp.setUuid(rs.getString(2));
                tmp.setName(rs.getString(3));
                tmp.setCity(rs.getString(4));

                hotels.add(tmp);
            }

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query all hotels", e);
            throw new RepositoryException("Failed to query all hotels", e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }

        return hotels;
    }
}
