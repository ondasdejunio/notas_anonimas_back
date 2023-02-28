package com.cursojava.curso.controllers;

import com.cursojava.curso.models.Post;
import com.cursojava.curso.models.PostLike;
import com.cursojava.curso.models.User;
import com.cursojava.curso.repositories.PostLikeRepository;
import com.cursojava.curso.repositories.PostRepository;
import com.cursojava.curso.utils.AuthenticationUtils;
import com.cursojava.curso.utils.TimezoneUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("api/post")
@AllArgsConstructor
public class PostLikeController {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId) {
        try {
            User userLogged = AuthenticationUtils.getUserLogged();
            Optional<Post> post = postRepository.findById(postId);
            if(post.isPresent()){
                Optional<PostLike> postLike = postLikeRepository.findOneByUserIdAndPostId(userLogged.getId(), post.get().getId());
                if(postLike.isPresent()){
                    postLikeRepository.delete(postLike.get());
                    return new ResponseEntity<>("Like eliminado", HttpStatus.OK);
                }
                else {
                    PostLike newPostLike = new PostLike();
                    newPostLike.setUserId(userLogged.getId());
                    newPostLike.setPostId(postRepository.findById(postId).get().getId());
                    newPostLike.setCreatedAt(TimezoneUtils.getCurrentTimestamp());
                    postLikeRepository.save(newPostLike);
                    return new ResponseEntity<>("Like agregado", HttpStatus.CREATED);
                }
            } else {
                return new ResponseEntity<>("El post no existe", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e){
            return new ResponseEntity<>("Error al procesar el like: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
