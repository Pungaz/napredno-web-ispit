package rs.edu.raf.nwp.ispit.Controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.nwp.ispit.dto.UserDto;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('can_create_users')")
    public ResponseEntity<User> create(@Valid @RequestBody UserDto userDTO) {
        return userService.create(userDTO);
    }

    @GetMapping(value = "/read")
    @PreAuthorize("hasAuthority('can_read_users')")
    public ResponseEntity<List<User>> read() {
        return userService.read();
    }

    @PutMapping(value = "/update/{userBeingUpdatedId}")
    @PreAuthorize("hasAuthority('can_update_users')")
    public ResponseEntity<User> update(@Valid @RequestBody UserDto userDTO, @PathVariable long userBeingUpdatedId) {
        return userService.update(userDTO, userBeingUpdatedId);
    }

    @DeleteMapping(value = "/delete/{userId}")
    @PreAuthorize("hasAuthority('can_delete_users')")
    public void delete(@PathVariable long userId) {
        userService.delete(userId);
    }

}
