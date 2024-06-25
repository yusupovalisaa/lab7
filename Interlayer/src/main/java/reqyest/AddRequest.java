package reqyest;

import models.Movie;
import models.User;
import utilities.Commands;

public class AddRequest extends Request{
    public final Movie m;

    public AddRequest(Movie m, User user) {
        super(Commands.add, user);
        this.m = m;
    }
}
