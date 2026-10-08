package org.sopt.service;

import org.sopt.domain.Post;
import org.sopt.repository.PostRepository;

public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public void createPost(String title, String content) {
        repository.add(new Post(title, content));
    }

    public boolean isEmpty() {
        return repository.isEmpty();
    }

    public int getPostCount() {
        return repository.size();
    }

    public boolean isValidIndex(int index) {
        return index >= 0 && index < repository.size();
    }

    public Post getPost(int index) {
        return repository.get(index);
    }

    public void updatePost(int index, String title, String content) {
        Post post = repository.get(index);
        post.updateTitle(title);
        post.updateContent(content);
    }

    public void deletePost(int index) {
        repository.remove(index);
    }
}