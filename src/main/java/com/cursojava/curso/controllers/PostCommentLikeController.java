package com.cursojava.curso.controllers;

import com.cursojava.curso.models.*;
import com.cursojava.curso.repositories.PostCommentLikeRepository;
import com.cursojava.curso.repositories.PostCommentRepository;
import com.cursojava.curso.utils.AuthenticationUtils;
import com.cursojava.curso.utils.TimezoneUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("api/post")
@AllArgsConstructor
public class PostCommentLikeController {
    private PostCommentLikeRepository postCommentLikeRepository;
    private PostCommentRepository postCommentRepository;
    @PostMapping("/{postCommentId}/comment/like")
    public ResponseEntity<String> likePostComment(@PathVariable Long postCommentId) {
        try {
            User userLogged = AuthenticationUtils.getUserLogged();
            Optional<PostComment> postComment = postCommentRepository.findById(postCommentId);
            if(postComment.isPresent()){
                Optional<PostCommentLike> postCommentLike = postCommentLikeRepository.findOneByUserIdAndPostCommentId(userLogged.getId(), postComment.get().getId());
                if(postCommentLike.isPresent()){
                    postCommentLikeRepository.delete(postCommentLike.get());
                    return new ResponseEntity<>("Like eliminado", HttpStatus.OK);
                }
                else {
                    PostCommentLike newPostCommentLike = new PostCommentLike();
                    newPostCommentLike.setUserId(userLogged.getId());
                    newPostCommentLike.setPostCommentId(postCommentRepository.findById(postCommentId).get().getId());
                    newPostCommentLike.setCreatedAt(TimezoneUtils.getCurrentTimestamp());
                    postCommentLikeRepository.save(newPostCommentLike);
                    return new ResponseEntity<>("Like agregado", HttpStatus.CREATED);
                }
            } else {
                return new ResponseEntity<>("El comentario no existe", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e){
            return new ResponseEntity<>("Error al procesar el like: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
