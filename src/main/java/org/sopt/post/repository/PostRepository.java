package org.sopt.post.repository;

import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "author")
    Optional<Post> findById(long id);

    @EntityGraph(attributePaths = "author")
    List<Post> findAll();

    @EntityGraph(attributePaths = "author")
    List<Post> findByCategory(PostCategory category);

    void deleteById(long id);
}
