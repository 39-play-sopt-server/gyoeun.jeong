package org.sopt.post;

import org.junit.jupiter.api.Test;
import org.sopt.common.exception.GlobalExceptionHandler;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.sopt.post.controller.PostController;
import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.dto.CreatePostRequest;
import org.sopt.post.exception.PostForbiddenException;
import org.sopt.post.repository.PostRepository;
import org.sopt.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostAuthorizationTest {
    private final PostRepository posts = mock(PostRepository.class);
    private final MemberRepository members = mock(MemberRepository.class);
    private final PostService service = new PostService(posts, members);
    private final PostController controller = new PostController(service);

    @Test
    void createUsesAuthenticatedMemberAsAuthor() {
        Member author = member(1L);
        when(members.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(author));
        when(posts.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = controller.createPost(new CreatePostRequest("제목", "본문", PostCategory.FREE), 1L);

        assertEquals(1L, response.data().authorId());
        verify(members).findByIdAndDeletedAtIsNull(1L);
    }

    @Test
    void onlyAuthorCanUpdateAndDelete() {
        Post post = new Post("제목", "본문", member(1L), PostCategory.FREE);
        when(posts.findById(10L)).thenReturn(Optional.of(post));

        PostForbiddenException error = assertThrows(PostForbiddenException.class,
                () -> service.updatePost(10L, 2L, "변경", "본문", PostCategory.FREE));
        assertEquals(HttpStatus.FORBIDDEN, new GlobalExceptionHandler().handlePostForbidden(error).getStatusCode());
        assertThrows(PostForbiddenException.class, () -> service.deletePost(10L, 2L));
        assertEquals("제목", post.getTitle());
        verify(posts, never()).delete(any());

        service.updatePost(10L, 1L, "변경", "본문", PostCategory.FREE);
        assertEquals("변경", post.getTitle());
        service.deletePost(10L, 1L);
        verify(posts).delete(post);
    }

    private Member member(Long id) {
        Member member = new Member("이름", "user@example.com", "password");
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }
}
