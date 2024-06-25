package reqyest;

import models.User;
import utilities.Commands;

public class ClearRequest extends Request {
    public ClearRequest(User user) {
        super(Commands.clear, user);
    }
}
