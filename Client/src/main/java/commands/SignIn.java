package commands;

import exception.APIException;
import exception.WrongAmountOfElementsException;
import models.Movie;
import models.User;
import network.TCPClient;
import reqyest.AddRequest;
import reqyest.SignInRequest;
import response.Response;
import utilities.Check;
import utilities.Crypto;
import utilities.StandartConsole;

import java.io.IOException;

public class SignIn extends Command {
    private final StandartConsole console;
    private final TCPClient client;

    public SignIn(StandartConsole console, TCPClient client) {
        super("sign_in", "авторизировать пользователя в системе");
        this.console = console;
        this.client = client;
    }

    @Override
    public boolean apply(String[] arguments) {
        try {
            User user = Check.getEnterForm(console);
            User userWithEncPass = Crypto.getEncryptedForm(user);
            client.setUser(userWithEncPass);
            Response response = client.sendAndReceiveCommandWithRetry(new SignInRequest(userWithEncPass));
            if (response.getError() != null && !response.getError()
                    .isEmpty()) {
                throw new APIException(response.getError());
            }
            console.println("Пользователь успешно авторизован");
            return true;

        } catch (IOException e) {
            console.printError("Ошибка взаимодействия с сервером!");
            return false;
        } catch (APIException e) {
            console.printError(e.getMessage());
            client.setUser(null);
            return false;
        } catch (NullPointerException e) {
            console.printError("Ошибка взаимодействия с сервером");
            return false;
        }

    }
}
