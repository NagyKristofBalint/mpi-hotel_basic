package edu.codespring.mpihotel.backend.repository;

import edu.codespring.mpihotel.backend.model.User;

import java.util.List;

public interface UserDAO {
    User create(User user);
    void update(User user);
    void delete(Long id);
    User getById(Long id);
    User getByUserName(String userName);
    List<User> getAllUsers();
}
