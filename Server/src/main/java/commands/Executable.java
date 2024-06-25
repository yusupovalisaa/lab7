package commands;

import response.Response;
import reqyest.Request;

/**
 * Выполнить что-либо.
 */
public interface Executable {
    /**
     * Выполняет команду
     * @return true.
     */
    Response apply(Request request);
}
