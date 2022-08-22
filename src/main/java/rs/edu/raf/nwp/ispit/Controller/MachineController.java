package rs.edu.raf.nwp.ispit.Controller;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.service.MachineService;

@RestController
@RequestMapping("/machine")
@CrossOrigin
@AllArgsConstructor
public class MachineController {
    private final MachineService machineService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('can_create_machines')")
    public ResponseEntity<Machine> create() {
        return machineService.create();
    }

    @PostMapping(value = "/delete/{machineId}")
    @PreAuthorize("hasAuthority('can_destroy_machines')")
    public ResponseEntity<?> delete(@PathVariable long machineId) {
        machineService.delete(machineId);
        return ResponseEntity.ok("Machine deleted successfully");
    }

}

