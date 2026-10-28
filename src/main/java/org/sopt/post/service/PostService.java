package org.sopt.post.service;

import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.exception.InvalidPostException;
import org.sopt.post.message.PostErrorMessage;
import org.sopt.post.exception.PostNotFoundException;
import org.sopt.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public Post createPost(String title, String content, String author, PostCategory category) {
        validatePost(title, content, category);
        validateAuthor(author);
        Post post = new Post(title, content, author, category);
        repository.save(post);
        return post;
    }

    public List<Post> getPosts(PostCategory category) {
        if (category == null) {
            return repository.findAll();
        }
        return repository.findByCategory(category);
    }

    public Post getPost(long postId) {
        return repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException());
    }

    @Transactional
    public Post viewPost(long postId) {
        Post post = getPost(postId);
        post.increaseViewCount();
        return post;
    }

    @Transactional
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
