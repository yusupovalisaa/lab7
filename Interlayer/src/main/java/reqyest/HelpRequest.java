package reqyest;

import models.User;
import utilities.Commands;

public class HelpRequest extends Request {
    public HelpRequest(User user) {
        super(Commands.help, user);
    }
}
