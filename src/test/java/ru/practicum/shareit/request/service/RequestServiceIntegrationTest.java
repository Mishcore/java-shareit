package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.model.RequestClientDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class RequestServiceIntegrationTest {

    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    private final User user1 = new User(1L, "test user 1", "test@mail.ru");
    private final User user2 = new User(2L, "test user 2", "test@mail.com");

    private final Request request1 = new Request(1, "test1", user1, LocalDateTime.of(2000, 1, 1, 0, 0, 0));
    private final Request request2 = new Request(2, "test2", user2, LocalDateTime.of(2000, 1, 2, 0, 0, 0));

    @BeforeEach
    void setUp() {
        userRepository.saveAll(List.of(user1, user2));
        requestRepository.saveAll(List.of(request1, request2));
    }

    @Test
    @DirtiesContext
    void addRequest() {
        RequestClientDto requestClientDto = new RequestClientDto("test new");
        requestService.addRequest(1L, requestClientDto);

        Request actualRequest = em.createQuery("select r from Request r where r.id = :id", Request.class)
                .setParameter("id", 3)
                .getSingleResult();
        assertEquals(requestClientDto.getDescription(), actualRequest.getDescription());
        assertEquals(user1, actualRequest.getRequestor());
    }

    @Test
    @DirtiesContext
    void getUserRequests() {
        List<Request> actualRequests = em
                .createQuery("select r from Request r where r.requestor.id = :requestorId", Request.class)
                .setParameter("requestorId", 1L)
                .getResultList();
        assertEquals(1, actualRequests.size());
        assertEquals(request1.getId(), actualRequests.get(0).getId());
        assertEquals(request1.getDescription(), actualRequests.get(0).getDescription());
        assertEquals(request1.getRequestor(), actualRequests.get(0).getRequestor());
    }

    @Test
    @DirtiesContext
    void getOtherRequests() {
        List<Request> actualRequests = em
                .createQuery("select r from Request r where r.requestor.id <> :requestorId", Request.class)
                .setParameter("requestorId", 1L)
                .getResultList();
        assertEquals(1, actualRequests.size());
        assertEquals(request2.getId(), actualRequests.get(0).getId());
        assertEquals(request2.getDescription(), actualRequests.get(0).getDescription());
        assertEquals(request2.getRequestor(), actualRequests.get(0).getRequestor());
    }

    @Test
    @DirtiesContext
    void getRequest() {
        Request actualRequest = em
                .createQuery("select r from Request r where r.id = :id", Request.class)
                .setParameter("id", 1)
                .getSingleResult();
        assertEquals(request1.getId(), actualRequest.getId());
        assertEquals(request1.getDescription(), actualRequest.getDescription());
        assertEquals(request1.getRequestor(), actualRequest.getRequestor());
    }
}