package reqyest;

import models.User;
import utilities.Commands;

public class InfoRequest extends Request {
    public InfoRequest(User user) {
        super(Commands.info, user);
    }
}
