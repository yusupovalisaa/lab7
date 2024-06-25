package network;

import models.User;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.tuple.Pair;

import org.example.ServerStart;
import reqyest.Request;
import response.NoSuchCommandResponse;
import response.Response;
import org.apache.logging.log4j.Logger;
import response.UnauthorizedResponse;
import util.Decrypto;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

/**
 * Абстрактный класс для управления TCP-сервером.
 */
public abstract class TCPServer {


    private final InetSocketAddress addr;
    private final CommandHandler commandHandler;
    private Runnable afterHook;

    private final Logger logger = ServerStart.logger;

    private boolean running = true;

    private final ExecutorService requestPool = Executors.newFixedThreadPool(5);
    private final ForkJoinPool processingPool = new ForkJoinPool();
    private final ForkJoinPool responsePool = new ForkJoinPool();

    public TCPServer(InetSocketAddress addr, CommandHandler commandHandler) {
        this.addr = addr;
        this.commandHandler = commandHandler;
    }

    /**
     * Получает адрес сервера.
     *
     * @return Адрес сервера.
     */
    public InetSocketAddress getAddr() {
        return addr;
    }

    /**
     * Получает данные с клиента.
     * Возвращает пару из данных и адреса клиента
     */
    public abstract Pair<byte[], SocketAddress> receiveData() throws IOException;

    /**
     * Отправляет данные клиенту
     */
    public abstract void sendData(byte[] data, SocketAddress addr) throws IOException;

    /**
     * Абстрактный метод для подключения к клиенту.
     *
     * @param addr Адрес клиента.
     * @throws IOException Если произошла ошибка ввода-вывода.
     */
    public abstract void connectToClient(SocketAddress addr) throws IOException;

    /**
     * Абстрактный метод для отключения от клиента.
     *
     * @throws IOException Если произошла ошибка ввода-вывода.
     */
    public abstract void disconnectFromClient() throws IOException;


    /**
     * Абстрактный метод для закрытия серверного соединения.
     *
     * @throws IOException Если произошла ошибка ввода-вывода.
     */
    public abstract void close() throws IOException;

    /**
     * Запускает сервер.
     *
     * @throws IOException Если произошла ошибка ввода-вывода.
     */
    public void run() throws IOException {
        logger.info("Сервер запущен по адресу " + addr);

        while (running) {
                Pair<byte[], SocketAddress> dataPair;
                try {
                    dataPair = receiveData();
                    requestPool.submit(() -> {
                        processRequest(dataPair);
                    });
                } catch (Exception e) {
                    logger.error("Ошибка получения данных: " + e.toString(), e);
                }
        }
        try {
            close();
        } catch (IOException e) {
            logger.error("Ошибка при закрытии сервера: " + e.toString(), e);
        }
    }


    private void processRequest(Pair<byte[], SocketAddress> dataPair) {
        var dataFromClient = dataPair.getKey();
        var clientAddr = dataPair.getValue();

        Request request = null;
        try {
            logger.debug("Полученные данные: " + Arrays.toString(dataFromClient));
            request = SerializationUtils.deserialize(dataFromClient);
            logger.info("Обработка " + request + " из " + clientAddr);
        } catch (SerializationException e) {
            logger.error("Невозможно десериализовать объект запроса.", e);
            try {
                disconnectFromClient();
            } catch (IOException ioException) {
                logger.error("Ошибка отключения от клиента: " + ioException.toString(), ioException);
            }
            return;
        }

        if (request != null) {
            Request finalRequest = request;
            processingPool.submit(() -> {
                Response response = null;
                try {
                    if (finalRequest.getUser() != null) {
                        User decryptedForm = Decrypto.getDencryptedForm(finalRequest.getUser());
                        finalRequest.setUser(decryptedForm);
                        response = commandHandler.handle(finalRequest);
                    } else {
                        response = new UnauthorizedResponse("Nan", "Пользователь не авторизован");
                    }
                    if (afterHook != null) afterHook.run();
                } catch (Exception e) {
                    logger.error("Ошибка выполнения команды: " + e.toString(), e);
                }
                if (response == null) response = new NoSuchCommandResponse(finalRequest.getName());

                var data = SerializationUtils.serialize(response);
                logger.info("Ответ: " + response);

                responsePool.submit(() -> {
                    try {
                        sendData(data, clientAddr);
                        logger.info("Отправлен ответ клиенту " + clientAddr);
                    } catch (Exception e) {
                        logger.error("Ошибка ввода-вывода: " + e.toString(), e);
                    } finally {
                        try {
                            disconnectFromClient();
                        } catch (IOException ioException) {
                            logger.error("Ошибка отключения от клиента: " + ioException.toString(), ioException);
                        }
                        logger.info("Отключение от клиента " + clientAddr);
                    }
                });
            });
        }
    }

    /**
     * Вызывает хук после каждого запроса.
     * @param afterHook хук, вызываемый после каждого запроса
     */
    public void setAfterHook(Runnable afterHook) {
        this.afterHook = afterHook;
    }

    /**
     * Останавливает сервер.
     */
    public void stop() {
        running = false;
    }
}
