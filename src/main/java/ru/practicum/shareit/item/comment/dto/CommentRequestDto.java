package ru.practicum.shareit.item.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentRequestDto {
    @NotBlank
    private String text;
}
