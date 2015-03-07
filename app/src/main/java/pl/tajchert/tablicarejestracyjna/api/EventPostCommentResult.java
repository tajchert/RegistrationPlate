package pl.tajchert.tablicarejestracyjna.api;


public class EventPostCommentResult {
    public boolean isSuccess;
    public String message;

    public EventPostCommentResult(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }
}
