package org.sopt.post.dto;

import org.sopt.post.domain.PostCategory;

public record CreatePostRequest(
        String title,
        String content,
        Long memberId,
        PostCategory category
) {
}
