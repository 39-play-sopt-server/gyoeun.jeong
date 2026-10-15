package org.sopt.post.controller;

import org.sopt.post.domain.Post;
import org.sopt.post.dto.ApiResponse;
import org.sopt.post.dto.CreatePostRequest;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.dto.UpdatePostRequest;
import org.sopt.post.message.PostSuccessMessage;
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
    public ApiResponse<PostResponse> createPost(@RequestBody CreatePostRequest request) {
        Post post = service.createPost(
                request.title(),
                request.content(),
                request.author(),
                request.category()
        );
        return ApiResponse.success(PostSuccessMessage.CREATED.getMessage(), PostResponse.from(post));
    }

    @GetMapping
    public ApiResponse<List<PostResponse>> getPosts() {
        List<PostResponse> posts = service.getPosts().stream()
                .map(PostResponse::from)
                .toList();
        return ApiResponse.success(PostSuccessMessage.LISTED.getMessage(), posts);
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse> getPost(@PathVariable long postId) {
        return ApiResponse.success(
                PostSuccessMessage.FOUND.getMessage(), PostResponse.from(service.viewPost(postId)));
    }

    @PutMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> updatePost(
            @PathVariable long postId,
            @RequestBody UpdatePostRequest request
    ) {
        service.updatePost(
                postId,
                request.title(),
                request.content(),
                request.category()
        );
        return ApiResponse.success(PostSuccessMessage.UPDATED.getMessage(), null);
    }

    @DeleteMapping(path = "/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deletePost(
            @PathVariable(name = "postId") long postId
    ) {
        service.deletePost(postId);
        return ApiResponse.success(PostSuccessMessage.DELETED.getMessage(), null);
    }
}