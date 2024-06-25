package commands;

import reqyest.Request;
import response.PrintAscendingResponse;
import response.Response;
import managers.CollectionManager;

/**
 * Команда 'print_ascending'. Выводит в стандартный поток вывода все элементы коллекции в порядке возрастания ID.
 */
public class PrintAscending extends Command {
    private final CollectionManager collectionManager;

    public PrintAscending(CollectionManager collectionManager) {
        super("print_ascending", " вывести элементы коллекции в порядке возрастания ID");
        this.collectionManager = collectionManager;
    }


    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        try {
            return new PrintAscendingResponse(collectionManager.sortedById(), null);
        } catch (Exception e) {
            return new PrintAscendingResponse(null, e.toString());
        }
    }
}
