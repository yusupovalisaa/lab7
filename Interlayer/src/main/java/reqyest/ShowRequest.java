package reqyest;

import models.User;
import utilities.Commands;

public class ShowRequest extends Request {
    public ShowRequest(User user) {
        super(Commands.show, user);
    }
}
