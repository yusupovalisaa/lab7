package commands;


import models.Movie;
import reqyest.RemoveByIDRequest;
import reqyest.Request;
import response.RemoveByIdResponse;
import response.Response;
import managers.CollectionManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции по ID.
 */
public class RemoveByID extends Command {
    private final CollectionManager collectionManager;

    public RemoveByID(CollectionManager collectionManager) {
        super("remove_by_id Id", "удалить элемент из коллекции по ID");
        this.collectionManager = collectionManager;
    }


    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (RemoveByIDRequest) request;

        try {
            List<Movie> collection = collectionManager.getCollection();
            for (var movie : collection) {
                if (movie.getId() == req.id) {
                    if (movie.getUser().getLogin().equals(req.getUser().getLogin())) {
                        collectionManager.remove(req.id);
                        return new RemoveByIdResponse(null);
                    } else {
                        return new RemoveByIdResponse("Нет прав на удаления этого фильма");
                    }
                }
            }
            return new RemoveByIdResponse("Продукта с таким ID в коллекции нет!");
        } catch (Exception e) {
            return new RemoveByIdResponse(e.toString());
        }
    }
}