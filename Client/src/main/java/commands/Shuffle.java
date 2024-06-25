package commands;

import network.TCPClient;
import utilities.StandartConsole;
import exception.APIException;
import exception.WrongAmountOfElementsException;
import reqyest.ShuffleRequest;
import response.ShuffleResponse;

import java.io.IOException;


/**
 * Команда 'shuffle'. Перемешивает элементы коллекции в случайном порядке.
 */
public class Shuffle extends Command{
    private final StandartConsole console;
    private final TCPClient client;

    public Shuffle(StandartConsole console, TCPClient client) {
        super("shuffle", "перемешать элементы коллекции в случайном порядке");
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
            if (!arguments[1].isEmpty()) throw new WrongAmountOfElementsException();

            var response = (ShuffleResponse) client.sendAndReceiveCommandWithRetry(new ShuffleRequest(client.getUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            if (response.m.isEmpty()) {
                console.println("Коллекция пуста!");
                return true;
            }

            for (var product : response.m) {
                console.println(product + "\n");
            }
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
