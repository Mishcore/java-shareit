package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    private EntityManager em;

    @Autowired
    private UserRepository userRepository;

    private final User user1 = new User(1L, "test1", "test@mail.ru");
    private final User user2 = new User(2L, "test2", "test@mail.com");

    @BeforeEach
    void setUp() {
        userRepository.saveAll(List.of(user1, user2));
    }

    @Test
    @DirtiesContext
    void getAllUsers() {
        List<User> actualUsers = em.createQuery("Select u from User u", User.class).getResultList();
        assertEquals(2, actualUsers.size());
        assertEquals(user1.getId(), actualUsers.get(0).getId());
        assertEquals(user1.getName(), actualUsers.get(0).getName());
        assertEquals(user1.getEmail(), actualUsers.get(0).getEmail());
        assertEquals(user2.getId(), actualUsers.get(1).getId());
        assertEquals(user2.getName(), actualUsers.get(1).getName());
        assertEquals(user2.getEmail(), actualUsers.get(1).getEmail());
    }

    @Test
    @DirtiesContext
    void getUser() {
        User actualUser = em.createQuery("Select u from User u where u.id = :id", User.class)
            .setParameter("id", user1.getId())
            .getSingleResult();
        assertEquals(user1.getId(), actualUser.getId());
        assertEquals(user1.getName(), actualUser.getName());
        assertEquals(user1.getEmail(), actualUser.getEmail());
    }

    @Test
    @DirtiesContext
    void addUser() {
        UserDto userDto = new UserDto(null, "test3", "test@mail.cn");
        userService.addUser(userDto);

        User actualUser = em.createQuery("Select u from User u where u.id = :id", User.class)
            .setParameter("id", 3L)
            .getSingleResult();
        assertEquals(userDto.getName(), actualUser.getName());
        assertEquals(userDto.getEmail(), actualUser.getEmail());
    }

    @Test
    @DirtiesContext
    void updateUser() {
        UserDto userDto = new UserDto(null, "test new", "test@mail.ru");
        userService.updateUser(1L, userDto);

        Long count = em.createQuery("Select count(u) from User u", Long.class).getSingleResult();
        assertEquals(2, count);

        User actualUser = em.createQuery("Select u from User u where u.id = :id", User.class)
            .setParameter("id", 1L)
            .getSingleResult();
        assertEquals(userDto.getName(), actualUser.getName());
        assertEquals(userDto.getEmail(), actualUser.getEmail());
    }

    @Test
    @DirtiesContext
    void deleteUser() {
        userService.deleteUser(2L);
        List<User> actualUsers = em.createQuery("Select u from User u", User.class).getResultList();
        assertEquals(1, actualUsers.size());
        assertFalse(actualUsers.stream().anyMatch(user ->
                user.getId().equals(user2.getId()) &&
                user.getName().equals(user2.getName()) &&
                user.getEmail().equals(user2.getEmail())
        ));
    }
}