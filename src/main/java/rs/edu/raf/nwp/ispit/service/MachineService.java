package rs.edu.raf.nwp.ispit.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.Status;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.exception.MachineNotExistsException;
import rs.edu.raf.nwp.ispit.repository.MachineRepository;
import rs.edu.raf.nwp.ispit.repository.UserRepository;

import javax.persistence.*;

import static rs.edu.raf.nwp.ispit.entity.Status.STOPPED;

@Data
@Service
@AllArgsConstructor
public class MachineService {
    private MachineRepository machineRepository;

    private UserRepository userRepository;

    @Transactional
    public ResponseEntity<Machine> create() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Machine machine = Machine.builder()
                .status(STOPPED)
                .active(false)
                .user(userRepository.findUserByUsername(username))
                .build();

        machineRepository.save(machine);

        return ResponseEntity.ok(machine);
    }

    public void delete(long machineId) {
        Machine machine = machineRepository.findMachinesById(machineId);

        if(machine != null){
            machineRepository.deleteById(machineId);
            return;
        }
        throw new MachineNotExistsException();
    }
}
