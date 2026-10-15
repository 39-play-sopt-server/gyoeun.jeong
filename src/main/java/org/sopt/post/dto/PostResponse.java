package org.sopt.post.dto;

import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostCategory;

public record PostResponse(
        long id,
        String title,
        String content,
        String author,
        PostCategory category
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor(),
                post.getCategory()
        );
    }
}