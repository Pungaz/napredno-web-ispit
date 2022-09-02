package rs.edu.raf.nwp.ispit.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity<Object> exception(ForbiddenException exception) {
        return new ResponseEntity<>("You are forbidden from accessing this url", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = PermissionNotExistException.class)
    public ResponseEntity<Object> exception(PermissionNotExistException exception) {
        return new ResponseEntity<>("Permission doesn't exist", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = NameAlreadyExistException.class)
    public ResponseEntity<Object> exception(NameAlreadyExistException exception) {
        return new ResponseEntity<>("Name already exist", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = UserNotExistException.class)
    public ResponseEntity<Object> exception(UserNotExistException exception) {
        return new ResponseEntity<>("User doesn't exist", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = MachineNotExistsException.class)
    public ResponseEntity<Object> exception(MachineNotExistsException exception) {
        return new ResponseEntity<>("Machine doesn't exist", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = BadStatusException.class)
    public ResponseEntity<Object> exception(BadStatusException exception) {
        return new ResponseEntity<>("Status sent doesn't exist", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = MachineAlreadyRunningException.class)
    public ResponseEntity<Object> exception(MachineAlreadyRunningException exception) {
        return new ResponseEntity<>("Machine is already running", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = MachineAlreadyStoppedException.class)
    public ResponseEntity<Object> exception(MachineAlreadyStoppedException exception) {
        return new ResponseEntity<>("Machine is already stopped", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = DateIncorrectException.class)
    public ResponseEntity<Object> exception(DateIncorrectException exception){
        return new ResponseEntity<>("Date is incorrect", HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = MachineNotAvailableException.class)
    public ResponseEntity<Object> exception(MachineNotAvailableException exception){
        return new ResponseEntity<>("Machine is currently not available", HttpStatus.LOCKED);
    }



}