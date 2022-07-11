package edu.codespring.mpihotel.backend.repository.jdbc;

import edu.codespring.mpihotel.backend.model.User;
import edu.codespring.mpihotel.backend.repository.RepositoryException;
import edu.codespring.mpihotel.backend.repository.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserDAO extends JdbcAbstractDAO implements UserDAO {
    private static final Logger LOG = LoggerFactory.getLogger(JdbcUserDAO.class);

    private static boolean tableCreated = false;

    public JdbcUserDAO() {
        if (!tableCreated) {
            tableCreated = true;
            createTable("""
                    CREATE TABLE IF NOT EXISTS `mpi-hotel`.`User` (
                      `id` INT NOT NULL AUTO_INCREMENT,
                      `uuid` VARCHAR(45) NOT NULL,
                      `username` VARCHAR(45) NOT NULL,
                      `password` VARCHAR(45) NOT NULL,
                      PRIMARY KEY (`id`))
                    ENGINE = InnoDB;
                      """, "User");
        }
        connectionManager = ConnectionManager.getInstance();
    }

    @Override
    public User create(User user) {
        Connection c = null;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("INSERT INTO User (`uuid`, `username`, `password`) VALUES (?, ? ,?);", Statement.RETURN_GENERATED_KEYS);
            prepStmt.setString(1, user.getUuid());
            prepStmt.setString(2, user.getUserName());
            prepStmt.setString(3, user.getPassword());
            prepStmt.execute();

            ResultSet rs = prepStmt.getGeneratedKeys();
            rs.next();

            user.setId(rs.getLong(1));

            LOG.info("User with id " + user.getId() + " has been created");
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to create user", e);
            throw new RepositoryException("Failed to create user", e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }

        return user;
    }

    @Override
    public void update(User user) throws RepositoryException {
        Connection c = null;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("UPDATE User SET `username` = ?, `password` = ? WHERE `id` = ?;");
            prepStmt.setString(1, user.getUserName());
            prepStmt.setString(2, user.getPassword());
            prepStmt.setLong(3, user.getId());
            prepStmt.execute();

            LOG.info("User with id " + user.getId() + " has been updated");
        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to update user", e);
            throw new RepositoryException("Failed to update user", e);
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
            PreparedStatement prepStmt = c.prepareStatement("DELETE FROM User WHERE `id` = ?;");
            prepStmt.setLong(1, id);
            prepStmt.execute();

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to delete user", e);
            throw new RepositoryException("Failed to delete user", e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }
    }

    @Override
    public User getById(Long id) {
        Connection c = null;
        User user;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("SELECT * FROM User WHERE `id` = ?;");
            prepStmt.setLong(1, id);

            ResultSet rs = prepStmt.executeQuery();
            if (!rs.next()) {
                LOG.info("No user registered with id " + id);
                return null;
            }

            user = new User();
            user.setId(id);
            user.setUuid(rs.getString(2));
            user.setUserName(rs.getString(3));
            user.setPassword(rs.getString(4));

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to get user by id", e);
            throw new RepositoryException("Failed to get user by id", e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }

        return user;
    }

    @Override
    public User getByUserName(String userName) {
        Connection c = null;
        User user;
        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("SELECT * FROM User WHERE `username` = ?;");
            prepStmt.setString(1, userName);

            ResultSet rs = prepStmt.executeQuery();
            if (!rs.next()) {
                LOG.info("No user registered with username " + userName);
                return null;
            }

            user = new User();
            user.setId(rs.getLong(1));
            user.setUuid(rs.getString(2));
            user.setUserName(rs.getString(3));
            user.setPassword(rs.getString(4));

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to get user by username", e);
            throw new RepositoryException("Failed to get user by username", e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        Connection c = null;
        List<User> users;

        try {
            c = connectionManager.getConnection();
            PreparedStatement prepStmt = c.prepareStatement("SELECT * FROM User;");
            ResultSet rs = prepStmt.executeQuery();

            users = new ArrayList<>();
            User tmp;
            while (rs.next()) {
                tmp = new User();
                tmp.setId(rs.getLong(1));
                tmp.setUuid(rs.getString(2));
                tmp.setUserName(rs.getString(3));
                tmp.setPassword(rs.getString(4));

                users.add(tmp);
            }

        } catch (SQLException | RepositoryException e) {
            LOG.error("Failed to query all users", e);
            throw new RepositoryException("Failed to query all users", e);
        } finally {
            if (c != null) {
                connectionManager.returnConnection(c);
            }
        }

        return users;
    }
}
