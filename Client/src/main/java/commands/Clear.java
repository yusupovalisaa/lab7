package commands;

import network.TCPClient;
import response.ClearResponse;
import exception.APIException;
import exception.WrongAmountOfElementsException;
import reqyest.ClearRequest;
import utilities.StandartConsole;

import java.io.IOException;

/**
 * Команда 'clear'. Очищает коллекцию.
 */

public class Clear extends Command {
    private final StandartConsole console;
    private final TCPClient client;

    public Clear(StandartConsole console, TCPClient client) {
        super("clear", "очистить коллекцию");
        this.console = console;
        this.client = client;
    }

    /**
     * Выполняет команду
     * @return  успешность выполнения команды.
     */
    @Override
    public boolean apply(String[] arguments) {
        try {
            if (!arguments[1].isEmpty()) throw new WrongAmountOfElementsException();

            var response = (ClearResponse) client.sendAndReceiveCommandWithRetry(new ClearRequest(client.getUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            console.println("Коллекция очищена!");
            return true;
        } catch (WrongAmountOfElementsException exception) {
            console.printError("Неправильное количество аргументов!");
            console.println("Использование: '" + getName() + "'");
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
