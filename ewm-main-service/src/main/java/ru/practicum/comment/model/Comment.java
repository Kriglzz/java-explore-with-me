package ru.practicum.comment.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.events.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Table(name = "comments")
public class Comment {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column(name = "create_date")
    private LocalDateTime createdOn;

    @Column(name = "update_date")
    private LocalDateTime updatedOn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommentStatus status;
}
