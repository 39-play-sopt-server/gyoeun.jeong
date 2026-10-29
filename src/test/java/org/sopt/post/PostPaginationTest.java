package org.sopt.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.sopt.common.exception.GlobalExceptionHandler;
import org.sopt.common.message.CommonErrorMessage;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.sopt.post.controller.PostController;
import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.repository.PostRepository;
import org.sopt.post.service.PostService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostPaginationTest {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 10, 29, 12, 0);

    private PostRepository repository;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        repository = mock(PostRepository.class);
        MemberRepository members = mock(MemberRepository.class);
        PostService service = new PostService(repository, members);
        mvc = MockMvcBuilders.standaloneSetup(new PostController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void defaultRequestReturnsPageMetadataAndAuthorWithFixedLatestSort() throws Exception {
        when(repository.findAll(any(Pageable.class))).thenAnswer(invocation ->
                new PageImpl<>(posts(10), invocation.getArgument(0), 23));

        mvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(10))
                .andExpect(jsonPath("$.data.content[0].authorId").value(1))
                .andExpect(jsonPath("$.data.content[0].authorName").value("교은"))
                .andExpect(jsonPath("$.data.content[0].createdAt").value("2026-10-29T12:00:00"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(23))
                .andExpect(jsonPath("$.data.totalPages").value(3))
                .andExpect(jsonPath("$.data.hasNext").value(true));

        Pageable request = capturedPageable();
        assertEquals(0, request.getOffset());
        assertEquals(10, request.getPageSize());
        assertEquals(List.of(Sort.Order.desc("createdAt"), Sort.Order.desc("id")),
                request.getSort().toList());
    }

    @Test
    void categoryFilterUsesItsOwnCountAndRequestedOffset() throws Exception {
        when(repository.findByCategory(eq(PostCategory.FREE), any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<>(posts(2), invocation.getArgument(1), 5));

        mvc.perform(get("/posts").param("category", "FREE").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.totalElements").value(5))
                .andExpect(jsonPath("$.data.totalPages").value(3))
                .andExpect(jsonPath("$.data.hasNext").value(true));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findByCategory(eq(PostCategory.FREE), captor.capture());
        assertEquals(2, captor.getValue().getOffset());
        verify(repository, never()).findAll(any(Pageable.class));
    }

    @Test
    void lastPageReturnsRemainingContentAndNoNextPage() throws Exception {
        when(repository.findAll(any(Pageable.class))).thenAnswer(invocation ->
                new PageImpl<>(posts(3), invocation.getArgument(0), 23));

        mvc.perform(get("/posts").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.totalElements").value(23))
                .andExpect(jsonPath("$.data.totalPages").value(3))
                .andExpect(jsonPath("$.data.hasNext").value(false));
        assertEquals(20, capturedPageable().getOffset());
    }

    @Test
    void pageBeyondLastReturnsEmptyContentAndPreservesTotal() throws Exception {
        when(repository.findAll(any(Pageable.class))).thenAnswer(invocation ->
                new PageImpl<>(List.of(), invocation.getArgument(0), 23));

        mvc.perform(get("/posts").param("page", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.page").value(3))
                .andExpect(jsonPath("$.data.totalElements").value(23))
                .andExpect(jsonPath("$.data.totalPages").value(3))
                .andExpect(jsonPath("$.data.hasNext").value(false));
    }

    @Test
    void emptyResultAllowsMaximumPageSize() throws Exception {
        when(repository.findAll(any(Pageable.class))).thenAnswer(invocation ->
                new PageImpl<>(List.of(), invocation.getArgument(0), 0));

        mvc.perform(get("/posts").param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.size").value(100))
                .andExpect(jsonPath("$.data.totalElements").value(0))
                .andExpect(jsonPath("$.data.totalPages").value(0))
                .andExpect(jsonPath("$.data.hasNext").value(false));
    }

    @Test
    void invalidPageAndSizesReturn400BeforeQuerying() throws Exception {
        mvc.perform(get("/posts").param("page", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(CommonErrorMessage.INVALID_PAGE.getMessage()));

        for (String size : List.of("-1", "0", "101")) {
            mvc.perform(get("/posts").param("size", size))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value(CommonErrorMessage.INVALID_PAGE_SIZE.getMessage()));
        }
        verifyNoInteractions(repository);
    }

    @Test
    void offsetBeyondJpaIntegerRangeReturns400BeforeQuerying() throws Exception {
        mvc.perform(get("/posts").param("page", "2147483647").param("size", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(CommonErrorMessage.PAGE_OFFSET_TOO_LARGE.getMessage()));
        verifyNoInteractions(repository);
    }

    @Test
    void malformedPageSizeAndCategoryReturnCommon400Response() throws Exception {
        for (String parameter : List.of("page", "size", "category")) {
            mvc.perform(get("/posts").param(parameter, "invalid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value(CommonErrorMessage.INVALID_PARAMETER.getMessage()));
        }
        verifyNoInteractions(repository);
    }

    @Test
    void clientSortCannotOverrideLatestOrder() throws Exception {
        when(repository.findAll(any(Pageable.class))).thenAnswer(invocation ->
                new PageImpl<>(List.of(), invocation.getArgument(0), 0));

        mvc.perform(get("/posts").param("size", "1").param("sort", "id,asc"))
                .andExpect(status().isOk());

        Pageable request = capturedPageable();
        assertEquals(1, request.getPageSize());
        assertEquals(List.of(Sort.Order.desc("createdAt"), Sort.Order.desc("id")),
                request.getSort().toList());
    }

    private Pageable capturedPageable() {
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(repository).findAll(captor.capture());
        return captor.getValue();
    }

    private List<Post> posts(int count) {
        Member author = new Member("교은");
        ReflectionTestUtils.setField(author, "id", 1L);
        return IntStream.range(0, count).mapToObj(index -> {
            Post post = new Post("게시글 " + index, "본문", author, PostCategory.FREE);
            ReflectionTestUtils.setField(post, "id", (long) count - index);
            ReflectionTestUtils.setField(post, "createdAt", CREATED_AT);
            return post;
        }).toList();
    }
}
