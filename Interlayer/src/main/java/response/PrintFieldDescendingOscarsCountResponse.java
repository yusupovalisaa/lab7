package response;

import utilities.Commands;

public class PrintFieldDescendingOscarsCountResponse extends Response {
    public final long[] oscars;

    public PrintFieldDescendingOscarsCountResponse(long[] oscars, String error) {
        super(Commands.print_field_descending_oscars_count, error);
        this.oscars = oscars;
    }
}
