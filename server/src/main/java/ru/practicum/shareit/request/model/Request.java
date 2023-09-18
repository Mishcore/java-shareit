package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User requestor;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime created;
}