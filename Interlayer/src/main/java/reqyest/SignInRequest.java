package reqyest;

import models.User;
import utilities.Commands;

public class SignInRequest extends Request{

    public SignInRequest(User user) {
        super(Commands.sign_in, user);
    }
}
