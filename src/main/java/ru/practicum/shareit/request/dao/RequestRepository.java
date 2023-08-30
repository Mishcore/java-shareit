package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByRequestorIdOrderByCreatedDesc(Long userId);

    List<Request> findAllByRequestorIdNot(Long userId, Pageable pageable);

    int countByRequestorIdNot(Long userId);
}