package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@ToString
@Table(name = "hits")
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String app;

    private String uri;

    private String ip;

    private LocalDateTime timeStamp;

}
