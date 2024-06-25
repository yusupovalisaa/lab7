package response;

import utilities.Commands;

public class UpdateResponse extends Response {
    public UpdateResponse(String error) {
        super(Commands.update, error);
    }
}
