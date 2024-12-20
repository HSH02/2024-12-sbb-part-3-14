package com.mysite.sbb.model.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class CommentRequestDTO {
    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;
}
