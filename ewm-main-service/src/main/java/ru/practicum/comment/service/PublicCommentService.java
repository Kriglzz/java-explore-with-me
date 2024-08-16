package ru.practicum.comment.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.comment.dto.UserCommentDto;

import java.util.List;

public interface PublicCommentService {

    UserCommentDto getCommentById(Long commentId);

    List<UserCommentDto> getAllEventComments(Long eventId, PageRequest pageRequest);
}
