package network;

import reqyest.Request;
import response.NoSuchCommandResponse;
import response.Response;
import managers.CommandsManager;

/**
 * Обработчик команд, который отвечает за выполнение команд, полученных в запросах.
 */
public class CommandHandler {
    private final CommandsManager manager;

    public CommandHandler(CommandsManager manager) {
        this.manager = manager;
    }

    public Response handle(Request request) {
        var command = manager.getCommands().get(request.getName());
        if (command == null) return new NoSuchCommandResponse(request.getName());
        return command.apply(request);
    }
}