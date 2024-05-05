package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import javax.validation.constraints.NotBlank;

@Getter
@ToString
@RequiredArgsConstructor
public class CreateCommentDto {

    private final Long commentId;

    @NotBlank
    private final String text;
}
