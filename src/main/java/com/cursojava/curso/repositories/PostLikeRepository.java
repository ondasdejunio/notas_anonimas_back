package com.cursojava.curso.repositories;

import com.cursojava.curso.models.Post;
import com.cursojava.curso.models.PostLike;
import com.cursojava.curso.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    List<PostLike> findByPostId(Long postId);
    Long countByPostId(Long postId);
    Optional<PostLike> findOneByUserIdAndPostId(Long userId, Long postId);
    default boolean checkIfPostLikedByUser(Long userId, Long postId) {
        return findOneByUserIdAndPostId(userId, postId).isPresent();
    }
}
