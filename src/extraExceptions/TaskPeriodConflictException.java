package extraExceptions;

public class TaskPeriodConflictException extends RuntimeException {
    public TaskPeriodConflictException(String message) {
        super(message);
    }
}
