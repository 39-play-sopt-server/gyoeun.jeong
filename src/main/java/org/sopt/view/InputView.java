package org.sopt.view;

import java.util.Scanner;

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

    public int readPostNumber() {
        return Integer.parseInt(scanner.nextLine());
    }
}