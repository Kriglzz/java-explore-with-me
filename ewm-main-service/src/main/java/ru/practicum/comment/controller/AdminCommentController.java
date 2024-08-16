package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.AdminCommentDto;
import ru.practicum.comment.dto.UpdateCommentStatusDto;
import ru.practicum.comment.service.AdminCommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService commentService;

    @GetMapping("/users/{authorId}")
    public ResponseEntity<List<AdminCommentDto>> getAuthorComments(@PathVariable @Positive Long authorId,
                                                                   @RequestParam(defaultValue = "0") int from,
                                                                   @RequestParam(defaultValue = "10") int size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        List<AdminCommentDto> comments = commentService.getAuthorComments(authorId, pageRequest);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<AdminCommentDto> adminUpdateStatus(@PathVariable @Positive Long commentId,
                                                             @RequestBody UpdateCommentStatusDto updateCommentStatusDto) {
        AdminCommentDto adminCommentDto = commentService.adminUpdateStatus(commentId, updateCommentStatusDto);
        return new ResponseEntity<>(adminCommentDto, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> adminDeleteComment(@PathVariable @Positive Long commentId) {
        commentService.adminDeleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
