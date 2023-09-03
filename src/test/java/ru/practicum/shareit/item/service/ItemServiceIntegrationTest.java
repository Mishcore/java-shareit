package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.comment.model.CommentClientDto;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemClientDto;
import ru.practicum.shareit.item.model.ItemServerDto;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ItemServiceIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private EntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private final User user1 = new User(1L, "test user 1", "test@mail.ru");
    private final User user2 = new User(2L, "test user 2", "test@mail.com");

    private final Request request = new Request(1, "test request", user2, LocalDateTime.now());

    private final Item item1 = new Item(1, "test item 1", "test description 1", true, user1, request);
    private final Item item2 = new Item(2, "test item 2", "test description 2", false, user2, null);

    @BeforeEach
    void setUp() {
        userRepository.saveAll(List.of(user1, user2));
        requestRepository.save(request);
        itemRepository.saveAll(List.of(item1, item2));
    }

    @Test
    @DirtiesContext
    void getAllUserItems() {
        List<Item> actualItems = em
                .createQuery("Select i from Item i join User u on i.owner.id = u.id where u.id = :id", Item.class)
                .setParameter("id", user1.getId())
                .getResultList();
        assertEquals(1, actualItems.size());
        assertEquals(item1.getId(), actualItems.get(0).getId());
        assertEquals(item1.getName(), actualItems.get(0).getName());
        assertEquals(item1.getDescription(), actualItems.get(0).getDescription());
    }

    @Test
    @DirtiesContext
    void getItem() {
        Item actualItem = em.createQuery("Select i from Item i where i.id = :id", Item.class)
                .setParameter("id", item1.getId())
                .getSingleResult();
        assertEquals(item1.getId(), actualItem.getId());
        assertEquals(item1.getName(), actualItem.getName());
        assertEquals(item1.getDescription(), actualItem.getDescription());
        assertEquals(item1.getAvailable(), actualItem.getAvailable());
        assertEquals(item1.getOwner(), user1);
        assertEquals(item1.getRequest(), request);
    }

    @Test
    @DirtiesContext
    void addItem() {
        ItemClientDto itemClientDto = new ItemClientDto("test item 3", "test description 3", true, null);
        itemService.addItem(2L, itemClientDto);

        Item actualItem = em
                .createQuery("Select i from Item i where i.id = :id", Item.class)
                .setParameter("id", 3)
                .getSingleResult();
        assertEquals(itemClientDto.getName(), actualItem.getName());
        assertEquals(itemClientDto.getDescription(), actualItem.getDescription());
        assertEquals(itemClientDto.getAvailable(), actualItem.getAvailable());
    }

    @Test
    @DirtiesContext
    void editItem() {
        ItemClientDto itemClientDto = new ItemClientDto("test item new", "test description new", false, null);
        itemService.editItem(1L, 1, itemClientDto);

        Long count = em.createQuery("Select count(i) from Item i", Long.class).getSingleResult();
        assertEquals(2, count);

        Item actualItem = em
                .createQuery("Select i from Item i where i.id = :id", Item.class)
                .setParameter("id", 1)
                .getSingleResult();
        assertEquals(itemClientDto.getName(), actualItem.getName());
        assertEquals(itemClientDto.getDescription(), actualItem.getDescription());
        assertEquals(itemClientDto.getAvailable(), actualItem.getAvailable());
    }

    @Test
    @DirtiesContext
    void getItemsBySearch() {
        String searchText = "eSt";
        List<Item> actualItems = em
                .createQuery("select i from Item i " +
                        "where i.available = true " +
                        "and (lower(i.name) like lower(concat('%', :text, '%')) " +
                        "or lower(i.description) like lower(concat('%', :text, '%')))", Item.class)
                .setParameter("text", searchText)
                .getResultList();
        assertEquals(1, actualItems.size());
        assertEquals(item1.getId(), actualItems.get(0).getId());
        assertEquals(item1.getName(), actualItems.get(0).getName());
        assertEquals(item1.getDescription(), actualItems.get(0).getDescription());
        assertEquals(item1.getAvailable(), actualItems.get(0).getAvailable());
        assertEquals(item1.getOwner(), actualItems.get(0).getOwner());
        assertEquals(item1.getRequest(), actualItems.get(0).getRequest());
    }

    @Test
    @DirtiesContext
    void addComment() throws InterruptedException {
        Booking booking = new Booking(1, item1, user2,
                LocalDateTime.now().plusSeconds(1), LocalDateTime.now().plusSeconds(2), Status.APPROVED);
        bookingRepository.save(booking);

        CommentClientDto commentClientDto = new CommentClientDto("very good");
        Thread.sleep(2000); // ждём, чтобы сроки аренды ушли в прошлое и можно было оставлять комментарии
        itemService.addComment(2L, 1, commentClientDto);

        ItemServerDto actualItemDto = itemService.getItem(1L, 1);
        assertEquals(1, actualItemDto.getComments().size());
        assertEquals(commentClientDto.getText(), actualItemDto.getComments().get(0).getText());
    }
}