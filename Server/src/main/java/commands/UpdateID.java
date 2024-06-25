package commands;

import models.Movie;
import reqyest.Request;
import reqyest.UpdateRequest;
import response.Response;
import response.UpdateResponse;
import managers.CollectionManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Команда 'update ID'. Обновляет элемент коллекции по ID.
 */
public class UpdateID extends Command {
    private final CollectionManager collectionManager;

    public UpdateID(CollectionManager collectionManager) {
        super("update Id", "обновить значение элемента коллекции по ID");
        this.collectionManager = collectionManager;
    }


    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (UpdateRequest) request;
        if (!req.updatedMovie.validate()) {
            return new UpdateResponse("Поля продукта не валидны! Продукт не обновлен!");
        }
        try {
            List<Movie> collection = collectionManager.getCollection();
            for (Movie movie : collection) {
                if (movie.getId() == req.id) {
                    if (movie.getUser()
                            .getLogin()
                            .equals(req.getUser()
                                    .getLogin())) {
                        return collectionManager.update(req.updatedMovie) ?
                                new UpdateResponse(null) : new UpdateResponse("Произошла ошибка при обновлении");

                    } else {
                        return new UpdateResponse("У вас не прав для обновления этого объекта");
                    }
                }
            }
            return new UpdateResponse("Продукта с таким ID в коллекции нет!");
        } catch (Exception e) {
            return new UpdateResponse(e.toString());
        }
    }
}
