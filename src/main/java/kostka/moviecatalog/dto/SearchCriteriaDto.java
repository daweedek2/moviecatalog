package kostka.moviecatalog.dto;

/**
 * Used for transferring data from FE to BE during searching movie via JPA specifications.
 */
public class SearchCriteriaDto {
    private String field;
    private String operation;
    private Object value;

    public String getField() {
        return field;
    }

    public void setField(final String field) {
        this.field = field;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(final String operation) {
        this.operation = operation;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(final Object value) {
        this.value = value;
    }
}
