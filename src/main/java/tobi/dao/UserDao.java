package tobi.dao;

import tobi.domain.User;

import java.util.List;

public interface UserDao {
    void add(User user);
    void update(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    int getCount();
}
