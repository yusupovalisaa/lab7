package commands;

import network.TCPClient;
import response.UnauthorizedResponse;
import utilities.StandartConsole;
import reqyest.InfoRequest;
import response.InfoResponse;

import java.io.IOException;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 */
public class Info extends Command{
    private final StandartConsole console;
    private final TCPClient client;

    public Info(StandartConsole console, TCPClient client) {
        super("info", "вывести информацию о коллекции");
        this.console = console;
        this.client = client;
    }


    /**
     * Выполняет команду
     * @return  успешность выполнения команды.
     */
    @Override
    public boolean apply(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            console.printError("Неправильное количество аргументов!");
            console.println("Использование: '" + getName() + "'");
            return false;
        }

        try {
            var response =  client.sendAndReceiveCommandWithRetry(new InfoRequest(client.getUser()));
            if (response instanceof UnauthorizedResponse) {
                console.println(response.getError());
                return true;
            }
            InfoResponse info = (InfoResponse) response;
            console.println(info.infoMessage);
            return true;
        } catch(IOException e) {
            console.printError("Ошибка взаимодействия с сервером");
        } catch (NullPointerException e){
            console.printError("Ошибка взаимодействия с сервером");
        }
        return false;
    }
}