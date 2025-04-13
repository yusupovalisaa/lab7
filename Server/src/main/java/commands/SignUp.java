package commands;

import managers.CollectionManager;
import managers.UserManager;
import models.User;
import reqyest.Request;
import reqyest.SignInRequest;
import reqyest.SignUpRequest;
import response.Response;
import response.SignInResponse;
import response.SignUpResponse;

public class SignUp extends Command{
    private final CollectionManager collectionManager;

    private final UserManager userManager = UserManager.getInstance();
    public SignUp(CollectionManager collManager) {
        super("sign_up", "Зарегистрировать пользователя");
        this.collectionManager = collManager;
    }

    @Override
    public Response apply(Request request) {
        var req = (SignUpRequest) request;
        if (req.getUser() != null) {
            if (userManager.register(req.getUser())) {
                return new SignUpResponse(null);
            } else {
                return new SignUpResponse("Неверный логин или пароль");
            }
        }
        return new SignUpResponse("Пользователь не указан");
    }
}
