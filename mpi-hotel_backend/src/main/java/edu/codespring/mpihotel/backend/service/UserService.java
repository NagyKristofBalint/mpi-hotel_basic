package edu.codespring.mpihotel.backend.service;

import edu.codespring.mpihotel.backend.model.User;

import java.util.List;

public interface UserService {
    Long register(User user);
    boolean login(User user);
    User getById(Long id);
    User getByName(String username);
    List<User> getAll();
    void update(User user);
}
