package org.sopt.post.dto;

import org.sopt.post.domain.PostCategory;

public record UpdatePostRequest(
        String title,
        String content,
        PostCategory category
) {
}