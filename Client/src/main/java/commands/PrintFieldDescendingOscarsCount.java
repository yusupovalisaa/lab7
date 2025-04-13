package commands;

import network.TCPClient;
import utilities.StandartConsole;
import exception.APIException;
import reqyest.PrintFieldDescendingOscarsCountRequest;
import response.PrintFieldDescendingOscarsCountResponse;

import java.io.IOException;

/**
 * Команда 'print_field_descending_oscars_count'.
 * Выводит значения поля oscarsCount всех элементов в порядке убывания.
 */
public class PrintFieldDescendingOscarsCount extends Command {
    private final StandartConsole console;
    private final TCPClient client;


    public PrintFieldDescendingOscarsCount(StandartConsole console, TCPClient client) {
        super("print_field_descending_oscars_count", "вывести значения поля oscarsCount всех элементов в порядке убывания");
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
            var response = (PrintFieldDescendingOscarsCountResponse) client.sendAndReceiveCommandWithRetry(new PrintFieldDescendingOscarsCountRequest(client.getUser()));

            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }

            if (response.oscars.length == 0) {
                console.println("Коллекция пуста!");
                return true;
            }
            for (var oscar : response.oscars) {
                console.println(oscar);
            }
            return true;
        } catch (IOException e) {
            console.printError("Ошибка взаимодействия с сервером!");
            return false;
        } catch (APIException e) {
            console.printError(e.getMessage());
            return false;
        }catch (NullPointerException e){
            console.printError("Ошибка взаимодействия с сервером");
            return false;
        }
    }
}