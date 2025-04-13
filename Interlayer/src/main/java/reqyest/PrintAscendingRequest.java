package reqyest;

import models.User;
import utilities.Commands;

public class PrintAscendingRequest extends Request{
    public PrintAscendingRequest(User user) {
        super(Commands.print_ascending, user);
    }
}
