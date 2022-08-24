package rs.edu.raf.nwp.ispit.Controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.nwp.ispit.dto.MachineDto;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.Status;
import rs.edu.raf.nwp.ispit.exception.ForbiddenException;
import rs.edu.raf.nwp.ispit.service.MachineService;

import javax.validation.Valid;
import java.time.LocalDate;
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
    public List<Machine> findAll() {
        return machineService.findAll();
    }

    @GetMapping(value = "/search/name/{name}")
    @PreAuthorize("hasAuthority('can_search_machines')")
    public List<Machine> searchByName(@PathVariable String name) {
        return machineService.findByName(name);
    }

    @GetMapping(value = "/search/status/{status}")
    @PreAuthorize("hasAuthority('can_search_machines')")
    public List<Machine> searchByStatus(@PathVariable Status status) {
        return machineService.findByStatus(status);
    }

    @GetMapping(value = "/search/date/{startingDate}/{endingDate}")
    @PreAuthorize("hasAuthority('can_search_machines')")
    public List<Machine> searchByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startingDate,
                                      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endingDate) {
        return machineService.findByDate(startingDate, endingDate);
    }

    @PutMapping(value = "/realStart")
    public ResponseEntity<Machine> startMachine(@RequestParam long machineId, @RequestParam String username, String secret) throws InterruptedException {
        if (secret.equals("nekiMojSecret")) {
            return machineService.realStart(machineId, username);
        } else {
            throw new ForbiddenException();
        }
    }

    @PutMapping(value = "/start/{machineId}")
    @PreAuthorize("hasAuthority('can_start_machines')")
    public void startMachineFirst(@PathVariable long machineId) {
        machineService.start(machineId);
    }

    @PutMapping(value = "/realStop")
    public ResponseEntity<Machine> stopMachine(@RequestParam long machineId, @RequestParam String username, String secret) throws InterruptedException {
        if (secret.equals("nekiMojSecret")) {
            return machineService.realStop(machineId, username);
        } else {
            throw new ForbiddenException();
        }
    }

    @PutMapping(value = "/stop/{machineId}")
    @PreAuthorize("hasAuthority('can_stop_machines')")
    public void stopMachineFirst(@PathVariable long machineId) {
        machineService.stop(machineId);
    }

    @PutMapping(value = "/realRestart")
    public ResponseEntity<Machine> restartMachine(@RequestParam long machineId, @RequestParam String username, String secret) throws InterruptedException {
        if (secret.equals("nekiMojSecret")) {
            return machineService.realRestart(machineId, username);
        } else {
            throw new ForbiddenException();
        }
    }

    @PutMapping(value = "/restart/{machineId}")
    @PreAuthorize("hasAuthority('can_restart_machines')")
    public void restartMachineFirst(@PathVariable long machineId) {
        machineService.restart(machineId);
    }

}

