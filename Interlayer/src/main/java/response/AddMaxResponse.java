package response;

import utilities.Commands;

public class AddMaxResponse extends Response{
    public final boolean isAdded;
    public final long newId;

    public AddMaxResponse(boolean isAdded, long newId, String error) {
        super(Commands.addMax, error);
        this.isAdded = isAdded;
        this.newId = newId;
    }
}
