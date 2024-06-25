package commands;

import network.TCPClient;
import utilities.StandartConsole;
import exception.APIException;
import exception.WrongAmountOfElementsException;
import reqyest.PrintAscendingRequest;
import response.PrintAscendingResponse;

import java.io.IOException;

/**
 * Команда 'print_ascending'. Выводит в стандартный поток вывода все элементы коллекции в порядке возрастания ID.
 */
public class PrintAscending extends Command{
    private final StandartConsole console;
    private final TCPClient client;

    public PrintAscending(StandartConsole console, TCPClient client) {
        super("print_ascending", "вывести в стандартный поток вывода все элементы коллекции в порядке возрастания ID");
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

            var response = (PrintAscendingResponse) client.sendAndReceiveCommandWithRetry(new PrintAscendingRequest(client.getUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            if (response.m.isEmpty()) {
                console.println("Коллекция пуста!");
                return true;
            }

            for (var movie : response.m) {
                console.println(movie + "\n");
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
