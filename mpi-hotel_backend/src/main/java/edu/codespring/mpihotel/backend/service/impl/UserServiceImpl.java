package edu.codespring.mpihotel.backend.service.impl;

import edu.codespring.mpihotel.backend.model.User;
import edu.codespring.mpihotel.backend.repository.AbstractDAOFactory;
import edu.codespring.mpihotel.backend.repository.RepositoryException;
import edu.codespring.mpihotel.backend.repository.UserDAO;
import edu.codespring.mpihotel.backend.service.ServiceException;
import edu.codespring.mpihotel.backend.service.UserService;
import edu.codespring.mpihotel.backend.util.PasswordEncrypter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDAO userDAO;

    public UserServiceImpl() {
        userDAO = AbstractDAOFactory.getInstance().getUserDAO();
    }

    @Override
    public Long register(User user) throws ServiceException {
        try {
            if (userDAO.getByUserName(user.getUserName()) != null) {
                LOG.info("Unable to register user " + user.getUserName() + " because it already exists");
                return null;
            }

            user.setPassword(PasswordEncrypter.generateHashedPassword(user.getPassword(), user.getUuid()));

            User tmp = userDAO.create(user);

            LOG.info("User " + user.getUserName() + " has been registered with id {};", tmp.getId());
            return tmp.getId();
        } catch (RepositoryException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
            LOG.error("Failed to register user " + user.getUserName(), e);
            throw new ServiceException("Failed to register user " + user.getUserName() + "; " + e.getMessage(), e);
        }
    }

    @Override
    public boolean login(User user) {
        User u = null;
        try {
            u = userDAO.getByUserName(user.getUserName());

            if (u == null) {
                LOG.info("Failed to login user {}, it is not registered", user.getUserName());
                return false;
            }

            user.setUuid(u.getUuid());
            user.setPassword(PasswordEncrypter.generateHashedPassword(user.getPassword(), user.getUuid()));

            if (!user.getPassword().equals(u.getPassword())) {
                LOG.info("Failed to login user {}, wrong password", user.getUserName());
                return false;
            }

            LOG.info("User {} has logged in successfully", user.getUserName());
            return true;
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | RepositoryException e) {
            LOG.error("Failed to login user " + user.getUserName() + "; " + e.getMessage(), e);
            throw new ServiceException("Failed to login user " + user.getUserName() + "; " + e.getMessage(), e);
        }
    }

    @Override
    public void update(User user) {
        try {
            User u = userDAO.getByUserName(user.getUserName());
            if (u == null) {
                LOG.info("Failed to update user with username " + user.getUserName() + "; non-existing user.");
                return;
            }

            userDAO.update(user);

            LOG.info("Successfully updated user with username " + user.getUserName());
        } catch (RepositoryException e) {
            LOG.error("Failed to update user with username " + user.getUserName() + "; " + e.getMessage());
            throw new ServiceException("Failed to update user with username " + user.getUserName() + "; " + e.getMessage(), e);
        }
    }

    @Override
    public User getById(Long id) {
        User u;
        try {
            u = userDAO.getById(id);
            if (u == null) {
                LOG.info("No user with id {};", id);
                return null;
            }

            LOG.info("Successfully queried user with id {};", u.getUserName());
        } catch (RepositoryException e) {
            LOG.error("Failed to query user by id {}; {}", id, e.getMessage(), e);
            throw new ServiceException("Failed to query user by id " + id + "; " + e.getMessage(), e);
        }

        return u;
    }

    @Override
    public User getByName(String username) {
        User u;
        try {
            u = userDAO.getByUserName(username);
            if (u == null) {
                LOG.info("No user with username {};", username);
                return null;
            }

            LOG.info("Successfully queried user with username {};", u.getUserName());
        } catch (RepositoryException e) {
            LOG.error("Failed to query user by username {}; {}", username, e.getMessage(), e);
            throw new ServiceException("Failed to query user by username " + username + "; " + e.getMessage(), e);
        }

        return u;
    }

    @Override
    public List<User> getAll() {
        List<User> list;
        try {
            list = userDAO.getAllUsers();
        } catch (RepositoryException e) {
            LOG.error("Failed to query all users; " + e.getMessage(), e);
            throw new ServiceException("Failed to query all users; " + e.getMessage(), e);
        }

        return list;
    }
}
