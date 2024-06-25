package commands;


import network.TCPClient;
import utilities.StandartConsole;
import exception.APIException;
import exception.WrongAmountOfElementsException;
import reqyest.RemoveByIDRequest;
import response.RemoveByIdResponse;

import java.io.IOException;

/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции по ID.
 */
public class RemoveByID extends Command {
    private final StandartConsole console;
    private final TCPClient client;

    public RemoveByID(StandartConsole console, TCPClient client) {
        super("remove_by_id Id", "удалить элемент из коллекции по ID");
        this.console = console;
        this.client = client;
    }


    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public boolean apply(String[] arguments) {
        try {
            if (arguments[1].isEmpty()) throw new WrongAmountOfElementsException();
            var id = Integer.parseInt(arguments[1]);

            var response = (RemoveByIdResponse) client.sendAndReceiveCommandWithRetry(new RemoveByIDRequest(id,client.getUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            console.println("Продукт успешно удален.");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            console.printError("Неправильное количество аргументов!");
            console.println("Использование: '" + getName() + "'");
        } catch (NumberFormatException exception) {
            console.printError("ID должен быть представлен числом!");
        } catch(IOException e) {
            console.printError("Ошибка взаимодействия с сервером");
        } catch (APIException e) {
            console.printError(e.getMessage());
        }catch (NullPointerException e){
            console.printError("Ошибка взаимодействия с сервером");
        }
        return false;
    }
}