package response;

import utilities.Commands;

public class SignUpResponse extends Response{
    public SignUpResponse(String error) {
        super(Commands.sign_up, error);
    }
}
