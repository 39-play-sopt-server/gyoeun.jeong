package org.sopt.repository;

import org.sopt.domain.Post;

import java.util.ArrayList;
import java.util.List;

public class PostRepository {
    private final List<Post> posts = new ArrayList<>();

    public void add(Post post) {
        posts.add(post);
    }

    public boolean isEmpty() {
        return posts.isEmpty();
    }

    public List<Post> findAll() {
        return List.copyOf(posts);
    }

    public int size() {
        return posts.size();
    }

    public Post get(int index) {
        return posts.get(index);
    }

    public void remove(int index) {
        posts.remove(index);
    }
}
