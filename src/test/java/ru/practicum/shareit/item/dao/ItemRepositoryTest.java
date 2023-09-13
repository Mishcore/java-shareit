package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User(1L, "user", "mail@mail.ru");
        userRepository.save(user);

        Item item1 = new Item(1, "test", "item under checks", true, user, null);
        Item item2 = new Item(2, "item", "object under tests", true, user, null);
        Item item3 = new Item(3, "item", "unavailable item under tests", false, user, null);

        itemRepository.saveAll(List.of(item1, item2, item3));
    }

    @Test
    @DirtiesContext
    void testSearchWhenInvokedThenListOfItemsSatisfyingConditions() {
        List<Item> actualItems = itemRepository.search("iTE", PageRequest.ofSize(3));
        assertEquals(2, actualItems.size());
        assertEquals(itemRepository.findById(1).get(), actualItems.get(0));
        assertEquals(itemRepository.findById(2).get(), actualItems.get(1));
    }

    @Test
    @DirtiesContext
    void testSearchWhenNoItemMatchesSearchTextThenEmptyList() {
        List<Item> actualItems = itemRepository.search("mIsMATch", PageRequest.ofSize(3));
        assertEquals(0, actualItems.size());
    }
}