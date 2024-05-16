package tobi.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tobi.dao.UserDao;
import tobi.domain.Level;
import tobi.domain.User;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static tobi.service.UserService.MIN_LOGIN_COUNT_FOR_SILVER;
import static tobi.service.UserService.MIN_RECOMMEND_COUNT_FOR_GOLD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"/applicationContextTest.xml"})
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserDao userDao;

    List<User> users;

    @BeforeEach
    void setUp() {
        users = Arrays.asList(
                new User("semin", "최세민", "p1", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER - 1, 0),
                new User("tobi", "토비", "p2", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER, 0),
                new User("younghan", "영한", "p3", Level.SILVER, 60, MIN_RECOMMEND_COUNT_FOR_GOLD - 1),
                new User("spring", "스프링", "p4", Level.SILVER, 60, MIN_RECOMMEND_COUNT_FOR_GOLD),
                new User("java", "자바", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
        );
    }

    @AfterEach
    public void destroy() {
        userDao.deleteAll();
    }

    @Test
    void bean() {
        assertThat(userService).isNotNull();
    }

    @Test
    void upgradeLevels() {
        for (User user : users) {
            userDao.add(user);
        }

        userService.upgradeLevels();

        checkLevel(users.get(0), false);
        checkLevel(users.get(1), true);
        checkLevel(users.get(2), false);
        checkLevel(users.get(3),true);
        checkLevel(users.get(4), false);
    }

    void checkLevel(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());

        if (upgraded) {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel().getNext());
        } else {
            assertThat(userUpdate.getLevel()).isEqualTo(user.getLevel());
        }
    }

    @Test
    void add() {
        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);


        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelAdd = userDao.get(userWithLevel.getId());
        User userWithoutLevelAdd = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelAdd.getLevel()).isEqualTo(userWithLevel.getLevel());
        assertThat(userWithoutLevelAdd.getLevel()).isEqualTo(Level.BASIC);
    }
}
