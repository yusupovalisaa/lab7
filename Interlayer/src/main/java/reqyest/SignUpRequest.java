package reqyest;

import models.User;
import utilities.Commands;

public class SignUpRequest extends Request{
    public SignUpRequest(User user) {
        super(Commands.sign_up, user);
    }
}
