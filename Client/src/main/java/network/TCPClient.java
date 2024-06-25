package network;

import models.User;
import reqyest.Request;
import response.Response;
import org.apache.logging.log4j.Logger;
import org.example.Main;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;


/**
 * TCP-клиент для отправки запросов и получения ответов от сервера.
 */
public class TCPClient {
    private final int PACKET_SIZE = 2048;
    private final int DATA_SIZE = PACKET_SIZE - 1;
    private final int MAX_RETRIES = 4; // Максимальное количество попыток подключения

    private final InetSocketAddress addr;
    private final Logger logger = Main.logger;
    private Socket clientSocket;
    private User user;



    public TCPClient(InetAddress address, int port) throws IOException {
        this.addr = new InetSocketAddress(address, port);
        this.clientSocket = new Socket();
        this.clientSocket.connect(addr);
    }

    /**
     * Отправляет запрос на сервер и получает ответ с повторной попыткой в случае ошибки.
     *
     * @param request Запрос, который нужно отправить.
     * @return Ответ от сервера.
     * @throws IOException Если произошла ошибка ввода-вывода после всех попыток.
     */
    public Response sendAndReceiveCommandWithRetry(Request request) throws IOException {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                if (clientSocket.isClosed()) {
                    clientSocket = new Socket();
                    clientSocket.connect(addr);
                }
                logger.info("Установлено соединение с сервером : " + addr);
                return sendAndReceiveCommand(request, clientSocket);
            } catch (IOException e) {
                logger.error("Ошибка при подключении к серверу: " + e.getMessage());
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    logger.info("Попытка подключения № " + retryCount);
                    try {
                        Thread.sleep(5000); // Пауза перед следующей попыткой (5 секунд)
                    } catch (InterruptedException interruptedException) {
                        logger.error("Ошибка при остановки выполнения: " + interruptedException.getMessage());
                    }
                } else {
                    logger.info("Достигнуто максимальное количество попыток. Отключение клиента.");
                    break;
                }
            } finally {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    try {
                        clientSocket.close();
                    } catch (IOException closeException) {
                        logger.error("Ошибка при закрытии сокета: " + closeException.getMessage());
                    }
                }
            }
        }
        System.out.println("Все попытки исчерпаны, выполнение операции не удалось. " +
                "Пожалуйста, проверьте подключение к сети и попробуйте снова позже.");
        System.exit(0);
        return null;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public User getUser() {
        return user;
    }
    /**
     * Отправляет запрос на сервер и получает ответ.
     *
     * @param request Запрос, который нужно отправить.
     * @return Ответ от сервера.
     * @throws IOException            Если произошла ошибка ввода-вывода.
     * @throws ClassNotFoundException Если произошла ошибка при десериализации ответа.
     */
    public Response sendAndReceiveCommand(Request request, Socket clientSocket) throws IOException {
        try  {
            OutputStream outputStream = clientSocket.getOutputStream();
            InputStream inputStream = clientSocket.getInputStream();
            logger.info("Отправка запроса на сервер : ");
            logger.info(clientSocket.getRemoteSocketAddress());
            byte[] data = serialize(request);

            logger.debug("Сериализованные данные: " + Arrays.toString(data));

            byte[][] chunks = new byte[(int) Math.ceil(data.length / (double) DATA_SIZE)][DATA_SIZE];

            int start = 0;
            for (int i = 0; i < chunks.length; i++) {
                chunks[i] = Arrays.copyOfRange(data, start, start + DATA_SIZE);
                start += DATA_SIZE;
            }

            for (int i = 0; i < chunks.length; i++) {
                byte[] chunk = chunks[i];
                if (i == chunks.length - 1) {
                    byte[] lastChunk = Arrays.copyOf(chunk, chunk.length + 1);
                    lastChunk[chunk.length] = 1;
                    outputStream.write(lastChunk);
                } else {
                    byte[] intermediateChunk = Arrays.copyOf(chunk, chunk.length + 1);
                    intermediateChunk[chunk.length] = 0;
                    outputStream.write(intermediateChunk);
                }
            }

            // Чтение данных от сервера
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[PACKET_SIZE];
            boolean received = false;

            logger.info("Ожидание ответа от сервера");

            while (!received) {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                if (buffer[bytesRead - 1] == 1) {
                    received = true;
                }
                logger.debug("Прочитано " + bytesRead + " байт, полученные данные: " + Arrays.toString(Arrays.copyOf(buffer, bytesRead)));
            }

            byte[] responseBytes = byteArrayOutputStream.toByteArray();
            logger.info("Получен ответ от сервера: " + deserialize(responseBytes));
            return deserialize(responseBytes);
        } catch (ClassNotFoundException e) {
            logger.error("Ошибка при десериализации ответа от сервера: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
//        finally {
//            clientSocket.close();
//        }
    }
    /**
     * Сериализует запрос в массив байтов.
     *
     * @param request Запрос для сериализации.
     * @return Сериализованный запрос.
     * @throws IOException Если произошла ошибка ввода-вывода.
     */
    private byte[] serialize(Request request) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(request);
            oos.flush();
            return baos.toByteArray();
        }
    }

    /**
     * Десериализует массив байтов в ответ.
     *
     * @param data Массив байтов для десериализации.
     * @return Десериализованный ответ.
     * @throws IOException            Если произошла ошибка ввода-вывода.
     * @throws ClassNotFoundException Если произошла ошибка при десериализации.
     */
    private Response deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Response) ois.readObject();
        }
    }
}
