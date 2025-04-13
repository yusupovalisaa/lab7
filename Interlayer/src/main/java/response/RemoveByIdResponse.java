package response;

import utilities.Commands;

public class RemoveByIdResponse extends Response {
    public RemoveByIdResponse(String error) {
        super(Commands.remove_by_id, error);
    }
}
