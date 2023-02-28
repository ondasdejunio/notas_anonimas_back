package com.cursojava.curso.dtos;

import com.cursojava.curso.models.PostComment;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.text.SimpleDateFormat;

@Data
public class PostCommentDTO {
    private Long id;
    @NotBlank(message = "El comentario no puede estar vacío")
    private String description;
    private String createdAt;
    private Long likes;
    private Boolean likedByUser;
    private BasicUserDataDTO user;

    public PostCommentDTO(PostComment postComment, Long likes, Boolean likedByUser, BasicUserDataDTO user){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(postComment.getCreatedAt());
        this.id = postComment.getId();
        this.description = postComment.getDescription();
        this.createdAt = formattedDate;
        this.likes = likes;
        this.likedByUser = likedByUser;
        this.user = user;
    }

    public PostCommentDTO(String description) {
        this.description = description;
    }

    public PostCommentDTO() {
    }

}
