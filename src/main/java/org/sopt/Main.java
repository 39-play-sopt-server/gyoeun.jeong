package org.sopt;

import org.sopt.controller.PostController;
import org.sopt.repository.PostRepository;
import org.sopt.service.PostService;
import org.sopt.view.InputView;
import org.sopt.view.OutputView;

public class Main {
    public static void main(String[] args) {
        PostRepository repository = new PostRepository();
        PostService service = new PostService(repository);
        InputView inputView = new InputView();
        OutputView outputView = new OutputView();

        PostController controller = new PostController(service, inputView, outputView);
        controller.run();
    }
}