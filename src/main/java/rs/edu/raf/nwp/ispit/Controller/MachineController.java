package rs.edu.raf.nwp.ispit.Controller;


import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.nwp.ispit.dto.MachineDto;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.service.MachineService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/machine")
@CrossOrigin
@AllArgsConstructor
public class MachineController {
    private final MachineService machineService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('can_create_machines')")
    public ResponseEntity<Machine> create(@Valid @RequestBody MachineDto machineDto) throws InterruptedException {
        return machineService.create(machineDto);
    }

    @PostMapping(value = "/destroy/{machineId}")
    @PreAuthorize("hasAuthority('can_destroy_machines')")
    public ResponseEntity<?> destroy(@PathVariable long machineId) {
        machineService.destroy(machineId);
        return ResponseEntity.ok("Machine deleted successfully");
    }

    @GetMapping(value = "/search")
    @PreAuthorize("hasAuthority('can_search_machines')")
    public List<Machine> search() {
        return machineService.search();
    }

//    @PostMapping(value = "/delete/{machineId}")
//    @PreAuthorize("hasAuthority('can_destroy_machines')")
//    public ResponseEntity<?> destroy(@PathVariable long machineId) {
//        machineService.destroy(machineId);
//        return ResponseEntity.ok("Machine deleted successfully");
//    }
//    @PostMapping(value = "/delete/{machineId}")
//    @PreAuthorize("hasAuthority('can_destroy_machines')")
//    public ResponseEntity<?> destroy(@PathVariable long machineId) {
//        machineService.destroy(machineId);
//        return ResponseEntity.ok("Machine deleted successfully");
//    }
//    @PostMapping(value = "/delete/{machineId}")
//    @PreAuthorize("hasAuthority('can_destroy_machines')")
//    public ResponseEntity<?> destroy(@PathVariable long machineId) {
//        machineService.destroy(machineId);
//        return ResponseEntity.ok("Machine deleted successfully");
//    }
//    SEARCH
//            START
//    STOP
//            RESTART

}

