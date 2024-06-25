package commands;

import exception.APIException;
import models.User;
import network.TCPClient;
import reqyest.SignInRequest;
import reqyest.SignUpRequest;
import response.Response;
import utilities.Check;
import utilities.Crypto;
import utilities.StandartConsole;

import java.io.IOException;

public class SignUp extends Command {
    private final StandartConsole console;
    private final TCPClient client;

    public SignUp(StandartConsole console, TCPClient client) {
        super("sign_up", "зарегистрировать пользователя в систему");
        this.console = console;
        this.client = client;
    }

    @Override
    public boolean apply(String[] arguments) {
        try {
            User user = Check.getRegisterForm(console);
            User userWithEncPass = Crypto.getEncryptedForm(user);
            client.setUser(userWithEncPass);
            Response response = client.sendAndReceiveCommandWithRetry(new SignUpRequest(userWithEncPass));
            if (response.getError() != null && !response.getError()
                    .isEmpty()) {
                throw new APIException(response.getError());
            }
            console.println("Пользователь успешно зарегистрирован");
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

