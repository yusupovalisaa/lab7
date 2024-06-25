package response;

import utilities.Commands;

public class InfoResponse extends Response {
    public final String infoMessage;

    public InfoResponse(String infoMessage, String error) {
        super(Commands.info, error);
        this.infoMessage = infoMessage;
    }
}
