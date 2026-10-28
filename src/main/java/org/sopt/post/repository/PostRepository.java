package org.sopt.post.repository;

import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    private final Map<Long, Post> posts = new LinkedHashMap<>();

    public void add(Post post) {
        posts.put(post.getId(), post);
    }

    public List<Post> findAll() {
        return List.copyOf(posts.values());
    }

    public List<Post> findByCategory(PostCategory category) {
        return posts.values().stream()
                .filter(post -> post.getCategory() == category)
                .toList();
    }

    public Optional<Post> findById(long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public void deleteById(long id) {
        posts.remove(id);
    }
}
