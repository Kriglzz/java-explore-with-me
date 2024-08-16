package ru.practicum.comment.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.comment.dto.AdminCommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UserCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.CommentStatus;
import ru.practicum.events.model.Event;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    private final UserMapper userMapper;

    public AdminCommentDto commentToAdminCommentDto(Comment comment) {
        return new AdminCommentDto(
                comment.getId(),
                comment.getText(),
                comment.getEvent().getId(),
                userMapper.userToUserShortDto(comment.getAuthor()),
                comment.getCreatedOn(),
                comment.getUpdatedOn(),
                comment.getStatus()
        );
    }

    public Comment newCommentDtoToComment(NewCommentDto newCommentDto, User user, Event event) {
        return new Comment(
                0L,
                newCommentDto.getText(),
                event,
                user,
                LocalDateTime.now(),
                LocalDateTime.now(),
                CommentStatus.PENDING
        );
    }

    public UserCommentDto commentToUserCommentDto(Comment comment) {
        return new UserCommentDto(
                comment.getId(),
                comment.getText(),
                userMapper.userToUserShortDto(comment.getAuthor()),
                comment.getCreatedOn(),
                comment.getUpdatedOn()
        );
    }
}
