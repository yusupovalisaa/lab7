package reqyest;

import models.User;
import utilities.Commands;

public class ShuffleRequest extends Request{
    public ShuffleRequest(User user) {
        super(Commands.shuffle, user);
    }
}
