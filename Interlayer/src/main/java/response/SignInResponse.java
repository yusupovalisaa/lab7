package response;

import utilities.Commands;

public class SignInResponse extends Response{

    public SignInResponse(String error) {
        super(Commands.sign_in, error);
    }
}
