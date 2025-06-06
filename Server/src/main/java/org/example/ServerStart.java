package org.example;

import commands.*;
import utilities.Commands;
import managers.CollectionManager;
import managers.CommandsManager;
import managers.WithCsvManager;
import network.CommandHandler;
import network.TCPNetworkChanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Главный класс серверного приложения.
 */
public class ServerStart {
    public static final int PORT = 23586;

    public static Logger logger = LogManager.getLogger("ServerLogger");

    public static void main(String[] args) throws IOException {



        var repository = new CollectionManager();

        repository.loadCollection();

        Runtime.getRuntime().addShutdownHook(new Thread(repository::save));
        var commandManager = new CommandsManager() {{
            register(Commands.help, new Help(this));
            register(Commands.info, new Info(repository));
            register(Commands.show, new Show(repository));
            register(Commands.add, new Add(repository));
            register(Commands.update, new UpdateID(repository));
            register(Commands.remove_by_id, new RemoveByID(repository));
            register(Commands.clear, new Clear(repository));
            register(Commands.addMax, new AddMax(repository));
            register(Commands.print_ascending, new PrintAscending(repository));
            register(Commands.shuffle, new Shuffle(repository));
            register(Commands.filter_than_less_genre, new FilterLessGenre(repository));
            register(Commands.print_field_descending_oscars_count, new PrintFieldDescendingOscarsCount(repository));
            register(Commands.sign_in, new SignIn(repository));
            register(Commands.sign_up, new SignUp(repository));

        }};

        try {
            var server = new TCPNetworkChanel(InetAddress.getLocalHost(), PORT, new CommandHandler(commandManager));
            server.setAfterHook(repository::save);
            server.run();
        } catch (SocketException e) {
            logger.fatal("Случилась ошибка сокета", e);
        } catch (UnknownHostException e) {
            logger.fatal("Неизвестный хост", e);
        }
    }
}