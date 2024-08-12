package ru.practicum.requests.model;

import lombok.*;
import ru.practicum.events.model.Event;
import ru.practicum.user.model.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @Column(name = "created")
    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User requester;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

}
