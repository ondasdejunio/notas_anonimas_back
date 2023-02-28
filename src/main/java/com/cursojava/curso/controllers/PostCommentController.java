package com.cursojava.curso.controllers;

import com.cursojava.curso.dtos.BasicUserDataDTO;
import com.cursojava.curso.dtos.PostCommentDTO;
import com.cursojava.curso.models.Post;
import com.cursojava.curso.models.PostComment;
import com.cursojava.curso.models.User;
import com.cursojava.curso.repositories.PostCommentLikeRepository;
import com.cursojava.curso.repositories.PostCommentRepository;
import com.cursojava.curso.repositories.PostRepository;
import com.cursojava.curso.repositories.UserRepository;
import com.cursojava.curso.utils.AuthenticationUtils;
import com.cursojava.curso.utils.TimezoneUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("api/post")
@AllArgsConstructor
public class PostCommentController {
    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final UserRepository userRepository;

    @PostMapping("/{postId}/comment")
    public ResponseEntity<Object> createPostComment(@RequestBody PostCommentDTO postCommentDTO, @PathVariable Long postId) {
        User userLogged = AuthenticationUtils.getUserLogged();
        Optional<Post> post = postRepository.findById(postId);
        if(post.isPresent()){
            PostComment postComment = new PostComment();
            postComment.setDescription(postCommentDTO.getDescription());
            postComment.setCreatedAt(TimezoneUtils.getCurrentTimestamp());
            postComment.setUserId(userLogged.getId());
            postComment.setPostId(postId);
            postCommentRepository.save(postComment);

            List<Object[]> createdPostCommentWithLikesCount = postCommentRepository.findLatestPostCommentWithLikesCount();
            if (!createdPostCommentWithLikesCount.isEmpty()) {
                Object postCommentObject = createdPostCommentWithLikesCount.get(0)[0];
                if (postCommentObject instanceof PostComment) {
                    PostComment createdComment = (PostComment) createdPostCommentWithLikesCount.get(0)[0];
                    BasicUserDataDTO basicUserData = new BasicUserDataDTO(userLogged.getUsername(), userLogged.getDateBirth(), userLogged.getGender());
                    Long likes = (Long) createdPostCommentWithLikesCount.get(0)[1];
                    Boolean likedByUser = postCommentLikeRepository.checkIfPostCommentLikedByUser(userLogged.getId(), postComment.getId());
                    PostCommentDTO createdPostCommentDTO = new PostCommentDTO(createdComment, likes, likedByUser, basicUserData);
                    Map<String, Object> response = new HashMap<>();
                    response.put("comment", createdPostCommentDTO);
                    return new ResponseEntity<>(response, HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>("Error al crear la publicación: la consulta no devolvió una instancia de la clase PostComment", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Error al crear la publicación: la consulta no devolvió resultados", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>("El post a comentar no existe", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{postId}/comment")
    public ResponseEntity<Object> getPostComments(
            @PathVariable Long postId
    ){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Optional<Post> post = postRepository.findById(postId);
            if(post.isPresent()){
                List<PostCommentDTO> postCommentDTOS = new ArrayList<>();
                Boolean likedByUser = false;
                if (AuthenticationUtils.getIsUserLogged()) {
                    User userLogged = AuthenticationUtils.getUserLogged();
                    List<Object[]> postCommentWithLikes = postCommentRepository.findAllWithLikesCountByPostId(post.get().getId());
                    for (Object[] postCommentWithLike : postCommentWithLikes) {
                        PostComment postComment = (PostComment) postCommentWithLike[0];
                        User userPost = userRepository.findOneById(postComment.getUserId());
                        BasicUserDataDTO basicUserData = new BasicUserDataDTO(userPost.getUsername(), userPost.getDateBirth(), userPost.getGender());
                        Long likes = (Long) postCommentWithLike[1];
                        likedByUser = postCommentLikeRepository.checkIfPostCommentLikedByUser(userLogged.getId(), postComment.getId());
                        postCommentDTOS.add(new PostCommentDTO(postComment, likes, likedByUser, basicUserData));
                    }
                } else {
                    List<Object[]> postCommentWithLikes = postCommentRepository.findAllWithLikesCountByPostId(post.get().getId());
                    for (Object[] postCommentWithLike : postCommentWithLikes) {
                        PostComment postComment = (PostComment) postCommentWithLike[0];
                        User userPost = userRepository.findOneById(postComment.getUserId());
                        BasicUserDataDTO basicUserData = new BasicUserDataDTO(userPost.getUsername(), userPost.getDateBirth(), userPost.getGender());
                        Long likes = (Long) postCommentWithLike[1];
                        postCommentDTOS.add(new PostCommentDTO(postComment, likes, likedByUser, basicUserData));
                    }
                }
                // Ordena la lista por los más antiguos
                    postCommentDTOS.sort((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt()));
                return new ResponseEntity<>(postCommentDTOS, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("El post no existe", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Hubo un problema: " + e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{postCommentId}/comment")
    public ResponseEntity<String> deletePostComment(@PathVariable Long postCommentId){
        User userLogged = AuthenticationUtils.getUserLogged();
        Optional<PostComment> postComment = postCommentRepository.findById(postCommentId);
        if(postComment.isPresent()){
            boolean isPostCommentCreatedByUserLogged = postCommentRepository.existsByIdAndUserId(postComment.get().getId(), userLogged.getId());
            if(isPostCommentCreatedByUserLogged){
                postCommentRepository.deleteById(postCommentId);
                return new ResponseEntity<>("Comentario eliminado exitosamente", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("El comentario no fue creado por el usuario logueado", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("El comentario no existe", HttpStatus.BAD_REQUEST);
        }
    }
}