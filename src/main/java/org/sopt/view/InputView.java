package org.sopt.view;

import java.util.Locale;
import java.util.Scanner;
import org.sopt.domain.PostCategory;
import java.util.NoSuchElementException;
import org.sopt.service.exception.InputClosedException;

public class InputView {
    private final Scanner scanner = new Scanner(System.in);

    private String readLine() {
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            throw new InputClosedException();
        }
    }

    public int readCommand() {
        try {
            return Integer.parseInt(readLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("메뉴 번호를 숫자로 입력해 주세요!");
        }
    }

    public String readTitle() {
        return readLine();
    }

    public String readContent() {
        return readLine();
    }

    public String readAuthor() {
        return readLine();
    }

    public PostCategory readCategory() {
        String input = readLine().trim().toUpperCase(Locale.ROOT);

        try {
            return PostCategory.valueOf(input);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바른 카테고리를 입력해 주세요.");
        }
    }

    public int readPostNumber() {
        try {
        return Integer.parseInt(readLine().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("게시글 번호를 숫자로 입력해 주세요!");
        }
    }
}