package commands;

import network.TCPClient;
import utilities.Check;
import utilities.StandartConsole;
import exception.APIException;
import exception.WrongAmountOfElementsException;
import models.MovieGenre;
import reqyest.FilterLessGenreRequest;
import response.FilterLessGenreResponse;

import java.io.IOException;
/**
 * Команда 'filter_less_genre'. Выводит элементы, значение поля genre которых меньше заданного.
 */
public class FillterLessGenre extends Command{
    private final StandartConsole console;
    private final TCPClient client;


    public FillterLessGenre(StandartConsole console, TCPClient client) {
        super("filter_than_less_genre", "вывести элементы, значение поля genre которых меньше заданного");
        this.console = console;
        this.client = client;
    }
    /**
     * Выполняет команду.
     *
     * @param arguments аргументы команды.
     * @return успешность выполнения команды.
     */
    @Override
    public boolean apply(String[] arguments) {
        try {
            if (!arguments[1].isEmpty()) {
                throw new WrongAmountOfElementsException();
            }
            MovieGenre genre = Check.RequestGenre(console);


            var response = (FilterLessGenreResponse) client.sendAndReceiveCommandWithRetry(new FilterLessGenreRequest(genre,client.getUser()));
            if (response.getError() != null && !response.getError().isEmpty()) {
                throw new APIException(response.getError());
            }
            if (response.m.isEmpty()) {
                console.println("Фильмов меньше чем заданный жанр: " + genre + " не существует.");
                return true;
            }
            for (var movie : response.m) {
                console.println(movie + "\n");
            }
            return true;
        } catch (Check.RequestBreak e) {
            console.printError("Ввод некорректный. Повторите.");
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
