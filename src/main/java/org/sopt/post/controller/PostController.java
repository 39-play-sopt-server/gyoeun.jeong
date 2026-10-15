package org.sopt.post.controller;

import org.sopt.post.domain.Post;
import org.sopt.post.dto.CreatePostRequest;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.dto.UpdatePostRequest;
import org.sopt.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(@RequestBody CreatePostRequest request) {
        Post post = service.createPost(
                request.title(),
                request.content(),
                request.author(),
                request.category()
        );
        return PostResponse.from(post);
    }

    @GetMapping
    public List<PostResponse> getPosts() {
        return service.getPosts().stream()
                .map(PostResponse::from)
                .toList();
    }

    @GetMapping("/{postId}")
    public PostResponse getPost(@PathVariable long postId) {
        return PostResponse.from(service.getPost(postId));
    }

    @PutMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePost(
            @PathVariable long postId,
            @RequestBody UpdatePostRequest request
    ) {
        service.updatePost(
                postId,
                request.title(),
                request.content(),
                request.category()
        );
    }

    @DeleteMapping(path = "/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(
            @PathVariable(name = "postId") long postId
    ) {
        service.deletePost(postId);
    }
}
