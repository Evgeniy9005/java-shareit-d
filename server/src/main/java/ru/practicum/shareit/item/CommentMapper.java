package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "authorName", expression = "java(mapAuthorName(comment))")
    CommentDto toCommentDto(Comment comment);

    default String mapAuthorName(Comment comment) {
        return comment.getAuthor().getName();
    }

   // List<CommentDto> toCommentDtoList(List<Comment> comments);

    default List<CommentDto> toCommentDtoList(List<Comment> comments) {
        return comments.stream().map(comment -> toCommentDto(comment)).collect(Collectors.toList());
    }
}
