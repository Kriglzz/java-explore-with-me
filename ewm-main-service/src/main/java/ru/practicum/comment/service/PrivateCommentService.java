package ru.practicum.comment.service;

import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.dto.UserCommentDto;

public interface PrivateCommentService {

    UserCommentDto userAddComment(NewCommentDto newCommentDTO, Long authorId);

    UserCommentDto userUpdateComment(UpdateCommentDto updateCommentDto, Long authorId, Long commentId);

    void userDeleteComment(Long authorId, Long commentId);
}
