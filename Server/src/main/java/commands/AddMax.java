package commands;


import managers.UserManager;
import reqyest.AddMaxRequest;
import response.AddMaxResponse;
import response.AddResponse;
import response.Response;
import reqyest.Request;
import managers.CollectionManager;

/**
 * Команда 'add_if_max'. Добавляет новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции.
 * Работает по полям oscarsCount.
 */
public class AddMax extends Command {
    private final CollectionManager collectionManager;

    private final UserManager userManager = UserManager.getInstance();

    public AddMax (CollectionManager collectionManager) {
        super("addMax", "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return  успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var req = (AddMaxRequest) request;
        try {
            if (!req.m.validate()) {
                return new AddMaxResponse(false, -1, "Поля фильма не валидны. Фильм не добавлен.");
            }

            Integer id = userManager.getUserIdByLogin(req.m.getUser()
                    .getLogin());

            if (req.m.getOscarsCount()> collectionManager.findMax()){
                req.m.setId(id);
                collectionManager.add(req.m);
                return new AddMaxResponse(true, id, null);
            }
            collectionManager.addLog("addMax" + req.m.getId(), true);
            return new AddMaxResponse(false, id, null);
        }
        catch (Exception e){
            return new AddResponse(-1, e.toString());
        }
    }
}
