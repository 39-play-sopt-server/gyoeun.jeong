package org.sopt.view;

import org.sopt.domain.Post;
import org.sopt.domain.PostCategory;

import java.util.List;

public class OutputView {
    public void printMenu() {
        System.out.println("\n=== 게시판 ===");
        System.out.println("1. 게시글 작성");
        System.out.println("2. 게시글 목록 조회");
        System.out.println("3. 게시글 단건 조회");
        System.out.println("4. 게시글 수정");
        System.out.println("5. 게시글 삭제");
        System.out.println("6. 종료");
    }

    public void printPrompt(String message) {
        System.out.print(message);
    }

    public void printCategoryOptions() {
        System.out.println("카테고리를 영어로 입력해 주세요.");

        for (PostCategory category : PostCategory.values()) {
            System.out.println(
                    category.name() + " - " + category.getDisplayName()
            );
        }
    }

    public void printPost(Post post) {
        System.out.println("\n=== 게시글 ===");
        System.out.println("카테고리: " + post.getCategory().getDisplayName());
        System.out.println("제목: " + post.getTitle());
        System.out.println("내용: " + post.getContent());
        System.out.println("작성자: " + post.getAuthor());
    }

    public void printPosts(List<Post> posts) {
        if (posts.isEmpty()) {
            printMessage("게시글이 없습니다.");
            return;
        }

        for (int i = 0; i < posts.size(); i++) {
            printMessage((i + 1) + ". " + posts.get(i).getTitle());
        }
    }

    public void printMessage(String message) {
        System.out.println(message);
    }
}
