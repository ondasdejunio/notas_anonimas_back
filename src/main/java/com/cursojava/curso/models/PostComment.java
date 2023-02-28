package com.cursojava.curso.models;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(name = "posts_comments")
@Data
public class PostComment {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description")
    private String description;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "post_id")
    private Long postId;
}
