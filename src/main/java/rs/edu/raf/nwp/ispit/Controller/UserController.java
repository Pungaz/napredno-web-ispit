package rs.edu.raf.nwp.ispit.Controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.nwp.ispit.dto.UserDto;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
@CrossOrigin
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('can_create_users')")
    public ResponseEntity<User> create(@Valid @RequestBody UserDto userDTO) {
            return this.userService.create(userDTO);
    }

    @PostMapping(value = "/update")
    public ResponseEntity<User> update(@Valid @RequestBody UserDto userDTO) {
        return this.userService.create(userDTO);
    }

    @GetMapping(value = "/all")
    public Page<User> all(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return this.userService.paginate(page, size);
    }

    @PreAuthorize("hasAuthority('can_create_users')")
    @GetMapping(value = "/sta")
    public String getString() {
        return "Dobio si string";
    }


//    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public User create(@Valid @RequestBody User user) {
//        return this.userService.create(user);
//    }

//    @GetMapping
//    public Page<User> all(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
//        return this.userService.paginate(page, size);
//    }
//
//    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
//    public User me() {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        return this.userService.findByemail(email);
//    }
//
//    @PostMapping(value = "/hire", produces = MediaType.APPLICATION_JSON_VALUE)
//    public User hire(@RequestParam("salary") Integer salary) {
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
//        return this.userService.hire(email, salary);
//    }
//

}
