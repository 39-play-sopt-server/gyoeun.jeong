package org.sopt.post.dto;

import org.sopt.member.domain.Member;
import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostCategory;

import java.time.LocalDateTime;

public record PostResponse(
        long id,
        String title,
        String content,
        long authorId,
        String authorName,
        PostCategory category,
        long ViewCount,
        LocalDateTime createdAt
) {
    public static PostResponse from(Post post) {
        Member author = post.getAuthor();
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                author.getId(),
                author.isDeleted() ? "탈퇴한 회원" : author.getName(),
                post.getCategory(),
                post.getViewCount(),
                post.getCreatedAt()
        );
    }
}
