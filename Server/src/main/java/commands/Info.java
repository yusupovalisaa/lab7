package commands;


import reqyest.Request;
import response.InfoResponse;
import response.Response;
import managers.CollectionManager;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 */
public class Info extends Command{
    private final CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        super("info", "вывести информацию о коллекции");
        this.collectionManager = collectionManager;
    }


    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        var lastInitTime = collectionManager.getLastInitTime();
        var lastInitTimeString = (lastInitTime == null) ? "в данной сессии инициализации еще не происходило" :
                lastInitTime.toLocalDate().toString() + " " + lastInitTime.toLocalTime().toString();

        var lastSaveTime = collectionManager.getLastSaveTime();
        var lastSaveTimeString = (lastSaveTime == null) ? "в данной сессии сохранения еще не происходило" :
                lastSaveTime.toLocalDate().toString() + " " + lastSaveTime.toLocalTime().toString();

        var message = "Сведения о коллекции:\n" +
                " Тип: " + collectionManager.getCollection().getClass().toString() + "\n" +
                " Количество элементов: " + collectionManager.getCollection().size() + "\n" +
                " Дата последнего сохранения: " + lastSaveTimeString + "\n" +
                " Дата последней инициализации: " + lastInitTimeString;
        return new InfoResponse(message, null);
    }
}