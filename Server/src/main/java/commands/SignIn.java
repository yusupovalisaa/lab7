package commands;

import managers.CollectionManager;
import managers.UserManager;
import reqyest.RemoveByIDRequest;
import reqyest.Request;
import reqyest.SignInRequest;
import response.RemoveByIdResponse;
import response.Response;
import response.SignInResponse;

public class SignIn extends Command {

    private final CollectionManager collManager;
    private final UserManager userManager = UserManager.getInstance();
    public SignIn(CollectionManager collManager) {
        super("sign_in", "авторизовать пользователя");
        this.collManager = collManager;
    }

    @Override
    public Response apply(Request request) {
        var req = (SignInRequest) request;
            if (req.getUser() != null) {
                if (userManager.authenticate(req.getUser())) {
                    return new SignInResponse(null);
                } else {
                    return new SignInResponse("Неверный логин или пароль");
                }
            }
            return new SignInResponse("Пользователь не указан");
    }
}
