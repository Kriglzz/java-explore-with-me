package ru.practicum.comment.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.comment.dto.AdminCommentDto;
import ru.practicum.comment.dto.UpdateCommentStatusDto;

import java.util.List;

public interface AdminCommentService {
    List<AdminCommentDto> getAuthorComments(Long authorId, PageRequest pageRequest);

    AdminCommentDto adminUpdateStatus(Long commentId, UpdateCommentStatusDto updateCommentStatusDto);

    void adminDeleteComment(Long commentId);

}
