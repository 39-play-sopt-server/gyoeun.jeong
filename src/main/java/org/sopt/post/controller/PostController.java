package org.sopt.post.controller;

import org.sopt.post.domain.PostCategory;
import org.sopt.common.dto.ApiResponse;
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
public class PostController implements PostApi {
    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ApiResponse<PostResponse> createPost(@RequestBody CreatePostRequest request) {
        PostResponse post = service.createPost(
                request.title(),
                request.content(),
                request.memberId(),
                request.category()
        );
        return ApiResponse.success(PostSuccessMessage.CREATED.getMessage(), post);
    }

    @GetMapping
    @Override
    public ApiResponse<List<PostResponse>> getPosts(
            @RequestParam(required = false) PostCategory category
    ) {
        List<PostResponse> posts = service.getPosts(category);
        return ApiResponse.success(PostSuccessMessage.LISTED.getMessage(), posts);
    }

    @GetMapping("/{postId}")
    @Override
    public ApiResponse<PostResponse> getPost(@PathVariable long postId) {
        return ApiResponse.success(
                PostSuccessMessage.FOUND.getMessage(), service.viewPost(postId));
    }

    @PutMapping("/{postId}")
    @Override
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
    @Override
    public ApiResponse<Void> deletePost(
            @PathVariable(name = "postId") long postId
    ) {
        service.deletePost(postId);
        return ApiResponse.success(PostSuccessMessage.DELETED.getMessage(), null);
    }
}
