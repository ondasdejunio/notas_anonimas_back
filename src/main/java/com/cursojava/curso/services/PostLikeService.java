package com.cursojava.curso.services;

import com.cursojava.curso.models.Post;
import com.cursojava.curso.models.PostLike;
import com.cursojava.curso.models.User;
import com.cursojava.curso.repositories.PostLikeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;

    public Long getPostLikesCount(Long postId){
        return postLikeRepository.countByPostId(postId);
    }
}
