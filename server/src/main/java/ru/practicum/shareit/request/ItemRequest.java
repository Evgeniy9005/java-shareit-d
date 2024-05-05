package ru.practicum.shareit.request;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;


@Entity
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_requests", schema = "public")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "requester_id", nullable = false)
    private long requester;

}
