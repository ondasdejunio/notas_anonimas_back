package com.cursojava.curso.controllers;
import com.cursojava.curso.dtos.BasicUserDataDTO;
import com.cursojava.curso.dtos.PostDTO;
import com.cursojava.curso.models.Post;
import com.cursojava.curso.models.User;
import com.cursojava.curso.repositories.PostLikeRepository;
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
@RequestMapping("api")
@AllArgsConstructor
public class PostController {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;

    @PostMapping("/post")
    public ResponseEntity<Object> createPost(@RequestBody PostDTO postDTO) {
        try {
            User userLogged = AuthenticationUtils.getUserLogged();
            Post post = new Post();
            post.setTitle(postDTO.getTitle());
            post.setDescription(postDTO.getDescription());
            post.setCreatedAt(TimezoneUtils.getCurrentTimestamp());
            post.setUserId(userLogged.getId());

            postRepository.save(post);

            List<Object[]> createdPostWithLikesAndCommentsCount = postRepository.findLatestPostWithLikesAndCommentsCount();
            if (!createdPostWithLikesAndCommentsCount.isEmpty()) {
                Object postObject = createdPostWithLikesAndCommentsCount.get(0)[0];
                if (postObject instanceof Post) {
                    Post createdPost = (Post) createdPostWithLikesAndCommentsCount.get(0)[0];
                    BasicUserDataDTO basicUserData = new BasicUserDataDTO(userLogged.getUsername(), userLogged.getDateBirth(), userLogged.getGender());
                    Long likes = (Long) createdPostWithLikesAndCommentsCount.get(0)[1];
                    Long comments = (Long) createdPostWithLikesAndCommentsCount.get(0)[2];
                    Boolean likedByUser = postLikeRepository.checkIfPostLikedByUser(userLogged.getId(), post.getId());
                    PostDTO createdPostDTO = new PostDTO(createdPost, likes, comments, likedByUser, basicUserData);
                    Map<String, Object> response = new HashMap<>();
                    response.put("post", createdPostDTO);
                    return new ResponseEntity<>(response, HttpStatus.CREATED);
                } else {
                    return new ResponseEntity<>("Error al crear la publicación: la consulta no devolvió una instancia de la clase Post", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Error al crear la publicación: la consulta no devolvió resultados", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e){
            return new ResponseEntity<>("Error al crear la publicación: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/post")
    public ResponseEntity<List<PostDTO>> getPosts(
            @RequestParam(name = "sortByOldest", required = false, defaultValue = "false") boolean sortByOldest,
            @RequestParam(name = "sortByLikes", required = false, defaultValue = "false") boolean sortByLikes,
            @RequestParam(name = "sortByRecent", required = false, defaultValue = "false") boolean sortByRecent,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page
    ){
        try {
            List<PostDTO> postDTOS = new ArrayList<>();
            Boolean likedByUser = false;
            List<Object[]> postWithLikesAndComments = postRepository.findAllWithLikesAndCommentsCount();
            int start = page * size;
            int end = start + size;
            end = Math.min(end, postWithLikesAndComments.size());

            // Ordena la lista por un solo parametro definido
            if (sortByOldest) {
                postWithLikesAndComments.sort((p1, p2) -> ((Post) p1[0]).getCreatedAt().compareTo(((Post) p2[0]).getCreatedAt()));
            } else if (sortByLikes) {
                postWithLikesAndComments.sort((p1, p2) -> Long.compare((Long) p2[1], (Long) p1[1]));
            } else if (sortByRecent) {
                postWithLikesAndComments.sort((p1, p2) -> ((Post) p2[0]).getCreatedAt().compareTo(((Post) p1[0]).getCreatedAt()));
            }

            if (AuthenticationUtils.getIsUserLogged()) {
                User userLogged = AuthenticationUtils.getUserLogged();
                for (int i = start; i < end; i++) {
                    Object[] postWithLikeAndComment = postWithLikesAndComments.get(i);
                    Post post = (Post) postWithLikeAndComment[0];
                    User userPost = userRepository.findOneById(post.getUserId());
                    BasicUserDataDTO basicUserData = new BasicUserDataDTO(userPost.getUsername(), userPost.getDateBirth(), userPost.getGender());
                    Long likes = (Long) postWithLikeAndComment[1];
                    Long comments = (Long) postWithLikeAndComment[2];
                    likedByUser = postLikeRepository.checkIfPostLikedByUser(userLogged.getId(), post.getId());
                    postDTOS.add(new PostDTO(post, likes, comments, likedByUser, basicUserData));
                }
            } else {
                for (int i = start; i < end; i++) {
                    Object[] postWithLikeAndComment = postWithLikesAndComments.get(i);
                    Post post = (Post) postWithLikeAndComment[0];
                    User userPost = userRepository.findOneById(post.getUserId());
                    BasicUserDataDTO basicUserData = new BasicUserDataDTO(userPost.getUsername(), userPost.getDateBirth(), userPost.getGender());
                    Long likes = (Long) postWithLikeAndComment[1];
                    Long comments = (Long) postWithLikeAndComment[2];
                    postDTOS.add(new PostDTO(post, likes, comments, likedByUser, basicUserData));
                }
            }
                return new ResponseEntity<>(postDTOS, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("" + e.toString());
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/post/user")
    public List<PostDTO> getPostsByUsername(@RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        User userLogged = AuthenticationUtils.getUserLogged();
        List<PostDTO> postDTOS = new ArrayList<>();
        List<Object[]> postWithLikesAndComments = postRepository.findAllWithLikesAndCommentsCountByUserId(userLogged.getId());

        // Ordena lista por más reciente
        postWithLikesAndComments.sort((p1, p2) -> ((Post) p1[0]).getCreatedAt().compareTo(((Post) p2[0]).getCreatedAt()));

        for (int i = 0; i < size && i < postWithLikesAndComments.size(); i++) {
            Object[] postWithLike = postWithLikesAndComments.get(i);
            Post post = (Post) postWithLike[0];
            User userPost = userRepository.findOneById(post.getUserId());
            BasicUserDataDTO basicUserData = new BasicUserDataDTO(userPost.getUsername(), userPost.getDateBirth(), userPost.getGender());
            Long likes = (Long) postWithLike[1];
            Long comments = (Long) postWithLike[1];
            Boolean likedByUser = postLikeRepository.checkIfPostLikedByUser(userLogged.getId(), post.getId());
            postDTOS.add(new PostDTO(post, likes, comments, likedByUser, basicUserData));
        }

        return postDTOS;
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<Object> getPost(@PathVariable Long id) {
        List<Object[]> postWithLikesAndCommentsCount = postRepository.findPostWithLikesAndCommentsCountByPostId(id);
        if (!postWithLikesAndCommentsCount.isEmpty()) {
            boolean likedByUser = false;
            Post post = (Post) postWithLikesAndCommentsCount.get(0)[0];
            Long likes = (Long) postWithLikesAndCommentsCount.get(0)[1];
            Long comments = (Long) postWithLikesAndCommentsCount.get(0)[2];
            User userPost = userRepository.findOneById(post.getUserId());
            BasicUserDataDTO basicUserData = new BasicUserDataDTO(userPost.getUsername(), userPost.getDateBirth(), userPost.getGender());

            if (AuthenticationUtils.getIsUserLogged()) {
                User userLogged = AuthenticationUtils.getUserLogged();
                likedByUser = postLikeRepository.checkIfPostLikedByUser(userLogged.getId(), post.getId());
            }

            PostDTO postDTO = new PostDTO(post, likes, comments, likedByUser, basicUserData);
            Map<String, Object> response = new HashMap<>();
            response.put("post", postDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("El post no existe", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/post/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id){
        User userLogged = AuthenticationUtils.getUserLogged();
        Optional<Post> post = postRepository.findById(id);
        if(post.isPresent()){
            boolean isPostCreatedByUserLogged = postRepository.postExistsForUser(post.get().getId(), userLogged.getId());
            if(isPostCreatedByUserLogged){
                postRepository.deleteById(id);
                return new ResponseEntity<>("Post eliminado exitosamente", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("El post no fue creado por el usuario logueado", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("El post no existe", HttpStatus.BAD_REQUEST);
        }
    }
}
