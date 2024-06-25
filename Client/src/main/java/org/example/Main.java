package org.example;

import network.TCPClient;
import utilities.Run;
import utilities.StandartConsole;
import java.io.IOException;
import java.net.InetAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final int PORT = 23586;

    public static final Logger logger = LogManager.getLogger("ClientLogger");

    public static void main(String[] args) {
        StandartConsole console = new StandartConsole();
        try {
            TCPClient client = new TCPClient(InetAddress.getLocalHost(), 23586);
            Run cli = new Run(client, console);
            cli.interactiveMode();
        } catch (IOException e) {
            logger.info(e);
        }
    }
}