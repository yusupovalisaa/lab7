package commands;

import exception.APIException;
import exception.WrongAmountOfElementsException;
import models.Movie;
import reqyest.AddRequest;
import network.TCPClient;
import utilities.Check;

import utilities.StandartConsole;

import java.io.IOException;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 */
public class Add extends Command {
    private final StandartConsole console;
    private final TCPClient client;

    public Add(StandartConsole console, TCPClient client) {
        super("add", "добавить новый элемент в коллекцию");
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
            if (!arguments[1].isEmpty()) throw new WrongAmountOfElementsException();
            console.println("Создание нового фильма:");
            Movie m = Check.RequestMovie(console);
            m.setUser(client.getUser());

            var response = client.sendAndReceiveCommandWithRetry(new AddRequest(m,client.getUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }
            console.println("Фильм успешно добавлен!");
            return true;

        } catch (WrongAmountOfElementsException e) {
            console.printError("Неправильно количество аргументов...");
            return false;
        } catch (Check.RequestBreak e) {
            console.printError("Поля фильма не валидны. Фильм не создан!");
            return false;
        } catch (IOException e) {
            console.printError("Ошибка взаимодействия с сервером!");
            return false;
        } catch (APIException e) {
            console.printError(e.getMessage());
            return false;
        } catch (NullPointerException e){
            console.printError("Ошибка взаимодействия с сервером");
            return false;
        }

    }
}

