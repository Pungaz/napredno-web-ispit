package rs.edu.raf.nwp.ispit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

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
        return new ResponseEntity<>("Name already exist" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = UserNotExistException.class)
    public ResponseEntity<Object> exception(UserNotExistException exception) {
        return new ResponseEntity<>("User doesn't exist" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = MachineNotExistsException.class)
    public ResponseEntity<Object> exception(MachineNotExistsException exception) {
        return new ResponseEntity<>("Machine doesn't exist" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = BadStatusException.class)
    public ResponseEntity<Object> exception(BadStatusException exception) {
        return new ResponseEntity<>("Status sent doesn't exist" , HttpStatus.INTERNAL_SERVER_ERROR);
    }


}