package org.sopt.view;

import java.util.Locale;
import java.util.Scanner;
import org.sopt.domain.PostCategory;

public class InputView {
    private final Scanner scanner = new Scanner(System.in);

    public int readCommand() {
        return Integer.parseInt(scanner.nextLine());
    }

    public String readTitle() {
        return scanner.nextLine();
    }

    public String readContent() {
        return scanner.nextLine();
    }

    public String readAuthor() {
        return scanner.nextLine();
    }

    public PostCategory readCategory() {
        String input = scanner.nextLine().trim().toUpperCase(Locale.ROOT);

        try {
            return PostCategory.valueOf(input);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("올바른 카테고리를 입력해 주세요.");
        }
    }

    public int readPostNumber() {
        return Integer.parseInt(scanner.nextLine());
    }
}