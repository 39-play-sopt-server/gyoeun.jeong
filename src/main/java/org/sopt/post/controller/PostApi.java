package org.sopt.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.post.domain.PostCategory;
import org.sopt.common.dto.ApiResponse;
import org.sopt.post.dto.CreatePostRequest;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.dto.UpdatePostRequest;

import java.util.List;

@Tag(name = "게시글", description = "게시글 작성, 조회, 수정, 삭제 API")
public interface PostApi {

    @Operation(summary = "게시글 작성", description = "제목, 본문, 작성자, 카테고리로 게시글을 작성합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "작성 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "필수 값이 비어 있거나 요청 본문이 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ApiResponse<PostResponse> createPost(CreatePostRequest request);

    @Operation(summary = "게시글 목록 조회", description = "카테고리를 지정하면 해당 카테고리의 게시글만 조회합니다. 생략하면 전체 게시글을 조회합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "올바르지 않은 카테고리",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ApiResponse<List<PostResponse>> getPosts(
            @Parameter(description = "조회할 카테고리", example = "FREE") PostCategory category);

    @Operation(summary = "게시글 상세 조회", description = "게시글을 조회하고 조회수를 1 증가시킵니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID가 숫자가 아님",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ApiResponse<PostResponse> getPost(@Parameter(description = "게시글 ID", example = "1") long postId);

    @Operation(summary = "게시글 수정", description = "게시글의 제목, 본문, 카테고리를 수정합니다. 세 값 모두 필수입니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 또는 요청 본문이 올바르지 않음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ApiResponse<Void> updatePost(@Parameter(description = "게시글 ID", example = "1") long postId,
                                 UpdatePostRequest request);

    @Operation(summary = "게시글 삭제", description = "게시글 ID로 게시글을 삭제합니다.")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID가 숫자가 아님",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    ApiResponse<Void> deletePost(@Parameter(description = "게시글 ID", example = "1") long postId);
}
