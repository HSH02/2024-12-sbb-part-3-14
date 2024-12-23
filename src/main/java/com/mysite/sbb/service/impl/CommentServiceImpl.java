package com.mysite.sbb.service.impl;

import com.mysite.sbb.domain.comment.dto.CommentRequestDTO;
import com.mysite.sbb.domain.comment.entity.Comment;
import com.mysite.sbb.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl {

    private CommentRepository commentRepository;

    @Transactional
    public void createComment(CommentRequestDTO dto, String authorName){
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setAuthor(authorName);


        commentRepository.save(comment);
    }
}