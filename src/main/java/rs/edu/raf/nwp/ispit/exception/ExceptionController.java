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

    @ExceptionHandler(value = UsernameAlreadyExistException.class)
    public ResponseEntity<Object> exception(UsernameAlreadyExistException exception) {
        return new ResponseEntity<>("Username already exist" , HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = UserNotExistException.class)
    public ResponseEntity<Object> exception(UserNotExistException exception) {
        return new ResponseEntity<>("User doesn't exist" , HttpStatus.INTERNAL_SERVER_ERROR);
    }


}