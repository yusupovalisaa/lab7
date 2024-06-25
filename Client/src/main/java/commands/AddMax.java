package commands;

import network.TCPClient;
import utilities.Check;
import response.AddMaxResponse;
import exception.APIException;
import exception.WrongAmountOfElementsException;
import models.Movie;
import reqyest.AddMaxRequest;
import utilities.StandartConsole;

import java.io.IOException;


/**
 * Команда 'add_if_max'. Добавляет новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции.
 * Работает по полям oscarsCount.
 */
public class AddMax extends Command {
    private final StandartConsole console;
    private final TCPClient client;


    public AddMax(StandartConsole console, TCPClient client) {
        super("addMax", "добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции");
        this.console = console;
        this.client = client;
    }

    /**
     * Выполняет команду
     *
     * @return успешность выполнения команды.
     */
    @Override
    public boolean apply(String[] arguments) {
        try {
            if (!arguments[1].isEmpty()) {
                throw new WrongAmountOfElementsException();
            }
            console.println("Создание нового фильма:");
            Movie m = Check.RequestMovie(console);
            m.setUser(client.getUser());
            var response = (AddMaxResponse) client.sendAndReceiveCommandWithRetry(new AddMaxRequest(m,client.getUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }
            if (!response.isAdded) {
                console.println("Фильм не добавлен, количество оскаров " + m.getOscarsCount() + " не максимально.");
                return true;
            }
            console.println("Фильм успешно добавлен!");
            return true;
        } catch (Check.RequestBreak e) {
            console.printError("Поля фильма не валидны. Фильм не создан!");
            return false;
        } catch (IOException e) {
            console.printError("Ошибка взаимодействия с сервером!");
            return false;
        } catch (APIException e) {
            console.printError(e.getMessage());
            return false;
        } catch (WrongAmountOfElementsException e) {
            console.printError("Неправильное количество аргументов");
            return false;
        } catch (NullPointerException e){
            console.printError("Ошибка взаимодействия с сервером");
            return false;
        }
    }
}