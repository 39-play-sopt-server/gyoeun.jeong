// PostController
package org.sopt.controller;

import org.sopt.domain.Post;
import org.sopt.service.PostService;
import org.sopt.view.InputView;
import org.sopt.view.OutputView;

public class PostController {
    private final PostService service;
    private final InputView inputView;
    private final OutputView outputView;

    public PostController(PostService service,
                           InputView inputView,
                           OutputView outputView) {
        this.service = service;
        this.inputView = inputView;
        this.outputView = outputView;
    }

    public void run() {
        while (true) {
            outputView.printMenu();
            outputView.printPrompt("선택: ");
            int command = inputView.readCommand();

            switch (command) {
                case 1 -> createPost();
                case 2 -> readPosts();
                case 3 -> readPost();
                case 4 -> updatePost();
                case 5 -> deletePost();
                case 6 -> {
                    outputView.printMessage("프로그램을 종료합니다.");
                    return;
                }
                default -> outputView.printMessage("잘못된 입력입니다.");
            }
        }
    }

    private void createPost() {
        while (true) {
            outputView.printPrompt("제목: ");
            String title = inputView.readTitle();

            outputView.printPrompt("내용: ");
            String content = inputView.readContent();

            try {
                service.createPost(title, content);
                outputView.printMessage("게시글이 작성되었습니다.");
                return;
            } catch (IllegalArgumentException e) {
                outputView.printMessage(e.getMessage());
            }
        }
    }

    private void readPosts() {
        if (service.isEmpty()) {
            outputView.printMessage("게시글이 없습니다.");
            return;
        }

        for (int i = 0; i < service.getPostCount(); i++) {
            outputView.printMessage(
                    (i + 1) + ". " + service.getPost(i).getTitle()
            );
        }
    }

    private void readPost() {
        if (service.isEmpty()) {
            outputView.printMessage("게시글이 없습니다.");
            return;
        }

        outputView.printPrompt("조회할 게시글 번호: ");
        int index = inputView.readPostNumber() - 1;

        if (!service.isValidIndex(index)) {
            outputView.printMessage("존재하지 않는 게시글입니다.");
            return;
        }

        Post post = service.getPost(index);
        outputView.printPost(post);
    }

    private void updatePost() {
        if (service.isEmpty()) {
            outputView.printMessage("게시글이 없습니다.");
            return;
        }

        outputView.printPrompt("수정할 게시글 번호: ");
        int index = inputView.readPostNumber() - 1;

        if (!service.isValidIndex(index)) {
            outputView.printMessage("존재하지 않는 게시글입니다.");
            return;
        }
        outputView.printPrompt("제목: ");
        String newTitle = inputView.readTitle();

        outputView.printPrompt("내용: ");
        String newContent = inputView.readContent();

        service.updatePost(index, newTitle, newContent);
        outputView.printMessage("게시글이 수정되었습니다.");
    }

    private void deletePost() {
        if (service.isEmpty()) {
            outputView.printMessage("게시글이 없습니다.");
            return;
        }

        outputView.printPrompt("삭제할 게시글 번호: ");
        int index = inputView.readPostNumber() - 1;

        if (!service.isValidIndex(index)) {
            outputView.printMessage("존재하지 않는 게시글입니다.");
            return;
        }

        service.deletePost(index);
        outputView.printMessage("게시글이 삭제되었습니다.");
    }
}