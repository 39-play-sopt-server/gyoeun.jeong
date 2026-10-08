// PostController
package org.sopt.controller;

import org.sopt.domain.Post;
import org.sopt.domain.PostCategory;
import org.sopt.service.PostService;
import org.sopt.service.exception.InvalidPostException;
import org.sopt.service.exception.PostNotFoundException;
import org.sopt.view.InputView;
import org.sopt.view.OutputView;
import org.sopt.view.exception.InputClosedException;
import org.sopt.view.exception.InvalidInputException;

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
            try {
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
            } catch (InvalidInputException | InvalidPostException | PostNotFoundException e) {
                outputView.printMessage(e.getMessage());
            } catch (InputClosedException e) {
                outputView.printMessage("\n" + e.getMessage());
                return;
            }
        }
    }

    private void createPost() {
        while (true) {
            try {
                outputView.printPrompt("제목: ");
                String title = inputView.readTitle();

                outputView.printPrompt("내용: ");
                String content = inputView.readContent();

                outputView.printPrompt("작성자: ");
                String author = inputView.readAuthor();

                outputView.printCategoryOptions();
                outputView.printPrompt("카테고리: ");
                PostCategory category = inputView.readCategory();

                service.createPost(title, content, author, category);
                outputView.printMessage("게시글이 작성되었습니다.");
                return;
            } catch (InvalidInputException | InvalidPostException e) {
                outputView.printMessage(e.getMessage());
            }
        }
    }

    private void readPosts() {
        outputView.printPosts(service.getPosts());
    }

    private void readPost() {
        if (service.isEmpty()) {
            outputView.printMessage("게시글이 없습니다.");
            return;
        }

        outputView.printPrompt("조회할 게시글 번호: ");
        int postNumber = inputView.readPostNumber();
        Post post = service.getPost(postNumber);
        outputView.printPost(post);
    }

    private void updatePost() {
        if (service.isEmpty()) {
            outputView.printMessage("게시글이 없습니다.");
            return;
        }

        outputView.printPrompt("수정할 게시글 번호: ");
        int postNumber = inputView.readPostNumber();
        service.ensurePostExists(postNumber);

        while (true) {
            try {
                outputView.printPrompt("제목: ");
                String newTitle = inputView.readTitle();

                outputView.printPrompt("내용: ");
                String newContent = inputView.readContent();

                outputView.printCategoryOptions();
                outputView.printPrompt("카테고리: ");
                PostCategory newCategory = inputView.readCategory();

                service.updatePost(postNumber, newTitle, newContent, newCategory);
                outputView.printMessage("게시글이 수정되었습니다.");
                return;
            } catch (InvalidInputException | InvalidPostException e) {
                outputView.printMessage(e.getMessage());
            }
        }
    }

    private void deletePost() {
        if (service.isEmpty()) {
            outputView.printMessage("게시글이 없습니다.");
            return;
        }

        outputView.printPrompt("삭제할 게시글 번호: ");
        int postNumber = inputView.readPostNumber();
        service.deletePost(postNumber);
        outputView.printMessage("게시글이 삭제되었습니다.");
    }
}
