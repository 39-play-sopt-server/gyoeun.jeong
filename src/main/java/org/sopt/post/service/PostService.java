package org.sopt.post.service;

import org.sopt.member.domain.Member;
import org.sopt.member.exception.InvalidMemberException;
import org.sopt.member.exception.MemberNotFoundException;
import org.sopt.member.message.MemberErrorMessage;
import org.sopt.member.repository.MemberRepository;
import org.sopt.post.domain.Post;
import org.sopt.post.domain.PostCategory;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.exception.InvalidPostException;
import org.sopt.post.message.PostErrorMessage;
import org.sopt.post.exception.PostNotFoundException;
import org.sopt.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository repository;
    private final MemberRepository memberRepository;

    public PostService(PostRepository repository, MemberRepository memberRepository) {
        this.repository = repository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public PostResponse createPost(String title, String content, Long memberId, PostCategory category) {
        validatePost(title, content, category);
        if (memberId == null) {
            throw new InvalidPostException(PostErrorMessage.MEMBER_ID_REQUIRED);
        }
        if (memberId <= 0) {
            throw new InvalidMemberException(MemberErrorMessage.INVALID_MEMBER_ID);
        }
        Member author = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(MemberNotFoundException::new);
        Post post = new Post(title, content, author, category);
        return PostResponse.from(repository.save(post));
    }

    public List<PostResponse> getPosts(PostCategory category) {
        List<Post> posts = category == null
                ? repository.findAll()
                : repository.findByCategory(category);
        return posts.stream().map(PostResponse::from).toList();
    }

    private Post getPost(long postId) {
        return repository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException());
    }

    @Transactional
    public PostResponse viewPost(long postId) {
        Post post = getPost(postId);
        post.increaseViewCount();
        return PostResponse.from(post);
    }

    @Transactional
    public void updatePost(long postId, String title, String content, PostCategory category) {
        Post post = getPost(postId);
        validatePost(title, content, category);
        post.update(title, content, category);
    }

    @Transactional
    public void deletePost(long postId) {
        getPost(postId);
        repository.deleteById(postId);
    }

    private void validatePost(String title, String content, PostCategory category) {
        if (title == null || title.isBlank()) {
            throw new InvalidPostException(PostErrorMessage.TITLE_REQUIRED);
        }
        if (content == null || content.isBlank()) {
            throw new InvalidPostException(PostErrorMessage.CONTENT_REQUIRED);
        }
        if (category == null) {
            throw new InvalidPostException(PostErrorMessage.CATEGORY_REQUIRED);
        }
    }
}
