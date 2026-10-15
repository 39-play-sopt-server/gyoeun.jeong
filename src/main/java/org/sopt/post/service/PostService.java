package org.sopt.post.service;

import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.exception.InvalidPostException;
import org.sopt.post.exception.PostErrorMessage;
import org.sopt.post.exception.PostNotFoundException;
import org.sopt.post.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository repository;
    private long nextId = 1;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public Post createPost(String title, String content, String author, PostCategory category) {
        validatePost(title, content, category);
        validateAuthor(author);
        Post post = new Post(title, content, author, category, nextId++);
        repository.add(post);
        return post;
    }

    public List<Post> getPosts() {
        return repository.findAll();
    }

    public Post getPost(long postId) {
        return repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException());
    }

    public Post viewPost(long postId) {
        Post post = getPost(postId);
        post.increaseViewCount();
        return post;
    }

    public void updatePost(long postId, String title, String content, PostCategory category) {
        Post post = getPost(postId);
        validatePost(title, content, category);
        post.update(title, content, category);
    }

    public void deletePost(long postId) {
        getPost(postId);
        repository.deleteById(postId);
    }

    private void validatePost(String title, String content, PostCategory category) {
        if (title == null || title.isBlank()) {
            throw new InvalidPostException(PostErrorMessage.TITLE_REQUIRED);
        }
        if (content == null || content.isBlank()) {
            throw new InvalidPostException(PostErrorMessage.CONTENT_REQUIRED);
        }
        if (category == null) {
            throw new InvalidPostException(PostErrorMessage.CATEGORY_REQUIRED);
        }
    }

    private void validateAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new InvalidPostException(PostErrorMessage.AUTHOR_REQUIRED);
        }
    }
}