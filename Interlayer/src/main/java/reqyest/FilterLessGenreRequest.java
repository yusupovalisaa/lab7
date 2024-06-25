package reqyest;

import models.MovieGenre;
import models.User;
import utilities.Commands;

public class FilterLessGenreRequest extends Request{
    public final MovieGenre genre;

    public FilterLessGenreRequest(MovieGenre genre, User user) {
        super(Commands.filter_than_less_genre, user);
        this.genre = genre;
    }
}
