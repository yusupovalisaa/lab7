package reqyest;

import models.User;
import utilities.Commands;

public class RemoveByIDRequest extends Request {
    public final long id;

    public RemoveByIDRequest(long id, User user ) {
        super(Commands.remove_by_id, user);
        this.id = id;
    }
}
