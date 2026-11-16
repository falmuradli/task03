package mygov.task03.exceptions;

public class InvalidPaginationException extends IllegalArgumentException {

    private final String field;

    public InvalidPaginationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
