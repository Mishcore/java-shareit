package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingClientDto;
import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class BookingServiceIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private EntityManager em;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private final User user1 = new User(1L, "test user 1", "test@mail.ru");
    private final User user2 = new User(2L, "test user 2", "test@mail.com");
    private final User user3 = new User(3L, "test user 3", "test@mail.cn");

    private final Item item1 = new Item(1, "test item 1", "test description 1", true, user1, null);
    private final Item item2 = new Item(2, "test item 2", "test description 2", true, user2, null);
    private final Item item3 = new Item(3, "test item 3", "test description 3", true, user3, null);

    private final Booking booking1 = new Booking(1, item1, user2,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.WAITING);
    private final Booking booking2 = new Booking(2, item3, user2,
            LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), Status.REJECTED);

    @BeforeEach
    void setUp() {
        userRepository.saveAll(List.of(user1, user2, user3));
        itemRepository.saveAll(List.of(item1, item2, item3));
        bookingRepository.saveAll(List.of(booking1, booking2));
    }

    @Test
    @DirtiesContext
    void addBooking() {
        BookingClientDto bookingClientDto =
                new BookingClientDto(1, LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(6));
        bookingService.addBooking(2L, bookingClientDto);

        Booking actualBooking = em.createQuery("select b from Booking b where b.id = :id", Booking.class)
                .setParameter("id", 3)
                .getSingleResult();
        assertEquals(bookingClientDto.getItemId(), actualBooking.getItem().getId());
        assertEquals(bookingClientDto.getStart(), actualBooking.getStart());
        assertEquals(bookingClientDto.getEnd(), actualBooking.getEnd());
    }

    @Test
    @DirtiesContext
    void approveBooking() {
        bookingService.approveBooking(1L, 1, true);

        Booking actualBooking = em.createQuery("select b from Booking b where b.id = :id", Booking.class)
                .setParameter("id", 1)
                .getSingleResult();
        assertEquals(Status.APPROVED, actualBooking.getStatus());
    }

    @Test
    @DirtiesContext
    void getBooking() {
        Booking actualBooking = em.createQuery("select b from Booking b where b.id = :id", Booking.class)
                .setParameter("id", 2)
                .getSingleResult();
        assertEquals(item3, actualBooking.getItem());
        assertEquals(user2, actualBooking.getBooker());
        assertEquals(Status.REJECTED, actualBooking.getStatus());
    }

    @Test
    @DirtiesContext
    void getUserBookings() {
        List<Booking> actualBookings = em
                .createQuery("select b from Booking b where b.booker.id = :bookerId", Booking.class)
                .setParameter("bookerId", 2L)
                .getResultList();
        assertEquals(2, actualBookings.size());
        assertEquals(booking1.getId(), actualBookings.get(0).getId());
        assertEquals(booking1.getStatus(), actualBookings.get(0).getStatus());
        assertEquals(booking2.getId(), actualBookings.get(1).getId());
        assertEquals(booking2.getStatus(), actualBookings.get(1).getStatus());
    }

    @Test
    @DirtiesContext
    void getItemBookings() {
        List<Booking> actualBookings = em
                .createQuery("select b from Booking b where b.item.owner.id = :ownerId", Booking.class)
                .setParameter("ownerId", 3L)
                .getResultList();
        assertEquals(1, actualBookings.size());
        assertEquals(booking2.getId(), actualBookings.get(0).getId());
        assertEquals(booking2.getStatus(), actualBookings.get(0).getStatus());
    }
}