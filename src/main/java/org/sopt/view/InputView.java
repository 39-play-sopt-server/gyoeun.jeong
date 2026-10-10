package org.sopt.view;

import org.sopt.domain.PostCategory;
import org.sopt.view.exception.InputClosedException;
import org.sopt.view.exception.InvalidInputException;

import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class InputView {
    private final Scanner scanner = new Scanner(System.in);

    private String readLine() {
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            throw new InputClosedException(e);
        }
    }

    public int readCommand() {
        try {
            return Integer.parseInt(readLine().trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("메뉴 번호를 숫자로 입력해 주세요!", e);
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
            throw new InvalidInputException("올바른 카테고리를 입력해 주세요.", e);
        }
    }

    public int readPostNumber() {
        try {
            return Integer.parseInt(readLine().trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("게시글 번호를 숫자로 입력해 주세요!", e);
        }
    }
}
