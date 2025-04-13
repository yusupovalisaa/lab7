package reqyest;

import models.User;
import utilities.Commands;

public class PrintFieldDescendingOscarsCountRequest extends Request {
    public PrintFieldDescendingOscarsCountRequest(User user) {
        super(Commands.print_field_descending_oscars_count, user);
    }
}
