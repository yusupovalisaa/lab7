package reqyest;

import models.Movie;
import models.User;
import utilities.Commands;

public class AddMaxRequest extends Request{
    public final Movie m;

    public AddMaxRequest(Movie m, User user) {
        super(Commands.addMax, user);
        this.m = m;
    }
}