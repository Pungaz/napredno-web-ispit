package rs.edu.raf.nwp.ispit.Controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.nwp.ispit.dto.MachineDto;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.Status;
import rs.edu.raf.nwp.ispit.service.MachineService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/machine")
@CrossOrigin
@AllArgsConstructor
public class MachineController {
    private final MachineService machineService;

    @PostMapping(value = "/create")
    @PreAuthorize("hasAuthority('can_create_machines')")
    public ResponseEntity<Machine> create(@Valid @RequestBody MachineDto machineDto) {
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
    public List<Machine> findByName(@PathVariable String name) {
        return machineService.findByName(name);
    }

    @GetMapping(value = "/search/status/{status}")
    @PreAuthorize("hasAuthority('can_search_machines')")
    public ResponseEntity<List<Machine>> findByStatus(@PathVariable Status status) {
        return machineService.findByStatus(status);
    }

    @GetMapping(value = "/search/date/{startingDate}/{endingDate}")
    @PreAuthorize("hasAuthority('can_search_machines')")
    public ResponseEntity<List<Machine>> findByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startingDate,
                                                    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endingDate) {
        return machineService.findByDate(startingDate, endingDate);
    }

    @PutMapping(value = "/start/{machineId}")
    @PreAuthorize("hasAuthority('can_start_machines')")
    public void start(@PathVariable long machineId, @RequestParam Optional<Long> time) {
        if (time.isEmpty()) {
            machineService.start(machineId, System.currentTimeMillis());
        } else {
            machineService.start(machineId, time.get());
        }
    }

    @PutMapping(value = "/stop/{machineId}")
    @PreAuthorize("hasAuthority('can_stop_machines')")
    public void stop(@PathVariable long machineId, @RequestParam Optional<Long> time) {
        if (time.isEmpty()) {
            machineService.stop(machineId, System.currentTimeMillis());
        } else {
            machineService.stop(machineId, time.get());
        }
    }

    @PutMapping(value = "/restart/{machineId}")
    @PreAuthorize("hasAuthority('can_restart_machines')")
    public void restart(@PathVariable long machineId, @RequestParam Optional<Long> time) {
        if (time.isEmpty()) {
            machineService.restart(machineId, System.currentTimeMillis());
        } else {
            machineService.restart(machineId, time.get());
        }

    }
}

