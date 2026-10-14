package org.sopt.post.repository;

import org.sopt.post.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PostRepository {
    private final Map<Long, Post> posts = new LinkedHashMap<>();

    public void add(Post post) {
        posts.put(post.getId(), post);
    }

    public boolean isEmpty() {
        return posts.isEmpty();
    }

    public List<Post> findAll() {
        return List.copyOf(posts.values());
    }

    public int size() {
        return posts.size();
    }

    public Post findById(long id) {
        return posts.get(id);
    }

    public void deleteById(long id) {
        posts.remove(id);
    }
}
