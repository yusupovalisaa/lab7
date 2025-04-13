package commands;

import managers.UserManager;
import response.AddResponse;
import response.Response;
import reqyest.AddRequest;
import reqyest.Request;
import managers.CollectionManager;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 */
public class Add extends Command{
    private final CollectionManager collectionManager;

    private final UserManager userManager = UserManager.getInstance();

    public Add(CollectionManager collectionManager) {
        super("add", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return  успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (AddRequest) request;
        try {
            if (!req.m.validate()) {
                return new AddResponse(-1, "Поля фильма не валидны. Фильм не добавлен.");
            }
            Integer id = userManager.getUserIdByLogin(req.m.getUser()
                    .getLogin());

            req.m.getUser().setId(id);
            int movId = collectionManager.add(req.m);

            return new AddResponse(movId, null);
        }
        catch (Exception e){
            return new AddResponse(-1, e.toString());
        }
    }
}

