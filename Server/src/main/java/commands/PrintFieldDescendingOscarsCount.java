package commands;

import reqyest.Request;
import response.PrintFieldDescendingOscarsCountResponse;
import response.Response;
import managers.CollectionManager;

/**
 * Команда 'print_field_descending_oscars_count'.
 * Выводит значения поля oscarsCount всех элементов в порядке убывания.
 */
public class PrintFieldDescendingOscarsCount extends Command {
    private final CollectionManager collectionManager;

    public PrintFieldDescendingOscarsCount(CollectionManager collectionManager) {
        super("print_field_descending_oscars_count", "вывести значения поля oscarsCount всех элементов в порядке убывания");
        this.collectionManager = collectionManager;
    }


    /**
     * Выполняет команду
     * @return  успешность выполнения команды.
     */
    @Override
    public Response apply(Request request) {
        try{
            return new PrintFieldDescendingOscarsCountResponse(collectionManager.fieldOscars(), null);
        }
        catch (Exception e){
            return new PrintFieldDescendingOscarsCountResponse(null, e.toString());
        }
    }
}
