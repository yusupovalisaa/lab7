package reqyest;

import models.Movie;
import models.User;
import utilities.Commands;

public class UpdateRequest extends Request {
    public final long id;
    public final Movie updatedMovie;

    public UpdateRequest(long id, Movie updatedMovie, User user) {
        super(Commands.update, user);
        this.id = id;
        this.updatedMovie = updatedMovie;
    }
}
