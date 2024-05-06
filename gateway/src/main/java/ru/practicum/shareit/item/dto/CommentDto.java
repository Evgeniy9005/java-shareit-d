package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;


import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class CommentDto {

    private final long id;

    private final String text;

    private final String authorName;

    private final LocalDateTime created;

}
