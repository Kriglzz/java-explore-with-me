package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.dto.UserCommentDto;
import ru.practicum.comment.service.PrivateCommentService;


@RestController
@RequestMapping("/users/{authorId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {

    private final PrivateCommentService commentService;

    @PostMapping
    public ResponseEntity<UserCommentDto> addComment(@RequestBody @Valid NewCommentDto newCommentDTO,
                                                     @PathVariable @Positive Long authorId) {
        UserCommentDto comment = commentService.userAddComment(newCommentDTO, authorId);
        return new ResponseEntity<>(comment, HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<UserCommentDto> updateComment(@RequestBody @Valid UpdateCommentDto updateCommentDTO,
                                                        @PathVariable @Positive Long authorId,
                                                        @PathVariable @Positive Long commentId) {
        UserCommentDto comment = commentService.userUpdateComment(updateCommentDTO, authorId, commentId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable @Positive Long authorId,
                                              @PathVariable @Positive Long commentId) {
        commentService.userDeleteComment(authorId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
