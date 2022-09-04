package rs.edu.raf.nwp.ispit.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.edu.raf.nwp.ispit.entity.ErrorMessage;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.repository.ErrorMessageRepository;
import rs.edu.raf.nwp.ispit.repository.MachineRepository;
import rs.edu.raf.nwp.ispit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class ErrorMessageService {
    private final ErrorMessageRepository errorMessageRepository;
    private final UserRepository userRepository;
    private final MachineRepository machineRepository;

    public ResponseEntity<Iterable<ErrorMessage>> findAll() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByUsername(username);

        List<Long> machinesIds = new ArrayList<>();
        List<Machine> machines = machineRepository.findAllByUserId(user.getId());

        for (Machine machine: machines){
            machinesIds.add(machine.getId());
        }

        return ResponseEntity.ok(this.errorMessageRepository.findAllByMachineIdIn(machinesIds));
    }

}
