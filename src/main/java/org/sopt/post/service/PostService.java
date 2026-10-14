package org.sopt.post.service;

import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.repository.PostRepository;
import org.sopt.post.exception.InvalidPostException;
import org.sopt.post.exception.PostNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PostService {
    private final PostRepository repository;
    private long nextId = 1;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public void createPost(String title, String content, String author, PostCategory category) {
        validatePost(title, content, category);
        validateAuthor(author);
        repository.add(new Post(title, content, author, category, nextId++));
    }

    public boolean isEmpty() {
        return repository.isEmpty();
    }

    public List<Post> getPosts() {
        return repository.findAll();
    }

    public void ensurePostExists(int postNumber) {
        toIndex(postNumber);
    }

    public Post getPost(int postNumber) {
        return repository.get(toIndex(postNumber));
    }

    public void updatePost(int postNumber, String title, String content, PostCategory category) {
        Post post = getPost(postNumber);
        validatePost(title, content, category);
        post.update(title, content, category);
    }

    public void deletePost(int postNumber) {
        Post post = PostRepository.findById(postNumber)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "게시글이 없습니다."
                ));

        repository.remove(toIndex(postNumber));
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

    private int toIndex(int postNumber) {
        if (postNumber < 1 || postNumber > repository.size()) {
            throw new PostNotFoundException();
        }
        return postNumber - 1;
    }

//    private Post findPostOrThrow(int postNumber) {
//        return PostRepository.findById(postNumber)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "게시글이 없습니다"
//                ));
//    }
}
