package rs.edu.raf.nwp.ispit.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.nwp.ispit.entity.ErrorMessage;
import rs.edu.raf.nwp.ispit.service.ErrorMessageService;

@RestController
@CrossOrigin
@RequestMapping("/error")
@RequiredArgsConstructor
public class ErrorMessageController {

    private final ErrorMessageService errorMessageService;

    @GetMapping(value = "/all")
    public ResponseEntity<Iterable<ErrorMessage>> getAllErrors() {
        return this.errorMessageService.findAll();
    }

}

