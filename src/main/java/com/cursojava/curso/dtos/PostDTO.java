package com.cursojava.curso.dtos;

import com.cursojava.curso.models.Post;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.text.SimpleDateFormat;

@Data
public class PostDTO {
    private Long id;
    @NotBlank(message = "El título no puede estar vacío")
    private String title;
    @NotBlank(message = "La descripción no puede estar vacía")
    private String description;
    private String createdAt;
    private Long likes;
    private Long comments;
    private Boolean likedByUser;
    private BasicUserDataDTO user;
    public PostDTO(Post post, Long likes, Long comments, Boolean likedByUser, BasicUserDataDTO user) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(post.getCreatedAt());
        this.id = post.getId();
        this.title = post.getTitle();
        this.description = post.getDescription();
        this.createdAt = formattedDate;
        this.likes = likes;
        this.comments = comments;
        this.likedByUser = likedByUser;
        this.user = user;
    }

    public PostDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public PostDTO() {
    }
}
