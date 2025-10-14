package hello.pet.petservice.exception;

public class AlreadyAnnouncedException extends RuntimeException {
    public AlreadyAnnouncedException(String message) {
        super(message);
    }
}
