package com.cursojava.curso.repositories;

import com.cursojava.curso.models.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    @Query("SELECT p, COUNT(pl.id) as likes FROM PostComment p LEFT JOIN PostCommentLike pl ON p.id = pl.postCommentId WHERE p.id = (SELECT max(id) FROM PostComment) GROUP BY p.id")
    List<Object[]> findLatestPostCommentWithLikesCount();

    @Query("SELECT p, COUNT(pl.id) as likes FROM PostComment p LEFT JOIN PostCommentLike pl ON p.id = pl.postCommentId WHERE p.postId = :postId GROUP BY p.id")
    List<Object[]> findAllWithLikesCountByPostId(Long postId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
