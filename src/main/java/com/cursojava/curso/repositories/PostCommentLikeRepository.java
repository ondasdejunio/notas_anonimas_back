package com.cursojava.curso.repositories;

import com.cursojava.curso.models.PostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, Long> {
    List<PostCommentLike> findByPostCommentId(Long postCommentId);
    Long countByPostCommentId(Long postCommentId);
    Optional<PostCommentLike> findOneByUserIdAndPostCommentId(Long userId, Long postCommentId);
    default boolean checkIfPostCommentLikedByUser(Long userId, Long postCommentId) {
        return findOneByUserIdAndPostCommentId(userId, postCommentId).isPresent();
    }
}
