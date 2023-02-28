package com.cursojava.curso.repositories;

import com.cursojava.curso.dtos.PostDTO;
import com.cursojava.curso.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p, (SELECT COUNT(pl.id) FROM PostLike pl WHERE pl.postId = p.id) as likes, (SELECT COUNT(pc.id) FROM PostComment pc WHERE pc.postId = p.id) as comments FROM Post p " +
            "WHERE p.id = (SELECT max(id) FROM Post)")
    List<Object[]> findLatestPostWithLikesAndCommentsCount();

    @Query("SELECT p, (SELECT COUNT(pl.id) FROM PostLike pl WHERE pl.postId = p.id) as likes, (SELECT COUNT(pc.id) FROM PostComment pc WHERE pc.postId = p.id) as comments FROM Post p " +
            "WHERE p.id = :postId")
    List<Object[]> findPostWithLikesAndCommentsCountByPostId(Long postId);

    @Query("SELECT p, (SELECT COUNT(pl.id) FROM PostLike pl WHERE pl.postId = p.id) as likes, (SELECT COUNT(pc.id) FROM PostComment pc WHERE pc.postId = p.id) as comments FROM Post p")
    List<Object[]> findAllWithLikesAndCommentsCount();

    @Query("SELECT p, (SELECT COUNT(pl.id) FROM PostLike pl WHERE pl.postId = p.id) as likes, (SELECT COUNT(pc.id) FROM PostComment pc WHERE pc.postId = p.id) as comments FROM Post p " +
            "WHERE p.userId = :userId")
    List<Object[]> findAllWithLikesAndCommentsCountByUserId(Long userId);
    @Query("SELECT COUNT(p.id) FROM Post p WHERE p.id = :postId AND p.userId = :userId")
    int countPostByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    default boolean postExistsForUser(Long postId, Long userId) {
        return countPostByPostIdAndUserId(postId, userId) > 0;
    }
}
