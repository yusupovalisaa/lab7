package commands;

import reqyest.Request;
import response.Response;
import response.ShowResponse;
import response.ShuffleResponse;
import managers.CollectionManager;

/**
 * Команда 'shuffle'. Перемешивает элементы коллекции в случайном порядке.
 */
public class Shuffle extends Command {
    private final CollectionManager collectionManager;

    public Shuffle(CollectionManager collectionManager) {
        super("shuffle", "перемешать элементы коллекции в случайном порядке");
        this.collectionManager = collectionManager;
    }


    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        try {
            var m = collectionManager.shuffle();
            collectionManager.save();
            return new ShuffleResponse(m, null);
        } catch (Exception e) {
            return new ShowResponse(null, e.toString());
        }
    }
}
