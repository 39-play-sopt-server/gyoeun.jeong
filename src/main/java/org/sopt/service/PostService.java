package org.sopt.service;

import org.sopt.domain.Post;
import org.sopt.domain.PostCategory;
import org.sopt.repository.PostRepository;
import org.sopt.service.exception.InvalidPostException;
import org.sopt.service.exception.PostNotFoundException;

import java.util.List;

public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public void createPost(String title, String content, String author, PostCategory category) {
        validatePost(title, content, category);
        validateAuthor(author);
        repository.add(new Post(title, content, author, category));
    }

    public boolean isEmpty() {
        return repository.isEmpty();
    }

    public List<Post> getPosts() {
        return repository.findAll();
    }

    public boolean isValidIndex(int index) {
        return index >= 0 && index < repository.size();
    }

    public Post getPost(int index) {
        validateIndex(index);
        return repository.get(index);
    }

    public void updatePost(int index, String title, String content, PostCategory category) {
        Post post = getPost(index);
        validatePost(title, content, category);
        repository.get(index).update(title, content, category);
    }

    public void deletePost(int index) {
        validateIndex(index);
        repository.remove(index);
    }

    private void validatePost(String title, String content, PostCategory category) {
        if (title == null || title.isBlank()) {
            throw new InvalidPostException("제목이 비어있어요!");
        }
        if (content == null || content.isBlank()) {
            throw new InvalidPostException("본문이 비어있어요!");
        }
        if (category == null) {
            throw new InvalidPostException("카테고리를 선택해 주세요!");
        }
    }

    private void validateAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new InvalidPostException("작성자가 비어있어요!");
        }
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= repository.size()) {
            throw new PostNotFoundException();
        }
    }
}
