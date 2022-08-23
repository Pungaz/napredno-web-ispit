package rs.edu.raf.nwp.ispit.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.nwp.ispit.dto.MachineDto;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.Status;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.exception.BadStatusException;
import rs.edu.raf.nwp.ispit.exception.MachineNotExistsException;
import rs.edu.raf.nwp.ispit.exception.NameAlreadyExistException;
import rs.edu.raf.nwp.ispit.repository.MachineRepository;
import rs.edu.raf.nwp.ispit.repository.UserRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static rs.edu.raf.nwp.ispit.entity.Status.STOPPED;

@Data
@Service
@AllArgsConstructor
public class MachineService {
    private MachineRepository machineRepository;

    private UserRepository userRepository;

    @Transactional
    public ResponseEntity<Machine> create(MachineDto machineDto) throws InterruptedException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!machineRepository.existsByName(machineDto.getName())) {
            Machine machine = Machine.builder()
                    .name(machineDto.getName())
                    .status(STOPPED)
                    .active(true)
                    .user(userRepository.findUserByUsername(username))
                    .date(new Date())
                    .build();

            machineRepository.save(machine);

            return ResponseEntity.ok(machine);
        }
        throw new NameAlreadyExistException();
    }

    @Transactional
    public void destroy(long machineId) {
        Machine machine = machineRepository.findMachineById(machineId);

        if (machine != null && machine.getStatus() == STOPPED) {
            machine.setActive(false);
            machineRepository.save(machine);
            return;
        }
        throw new MachineNotExistsException();
    }

    public List<Machine> findAll() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findUserByUsername(username);

        return machineRepository.findAllByUserIdAndActive(user.getId(), true);
    }

    public List<Machine> findByName(String name) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByUsername(username);

        return machineRepository.findAllByUserAndName(user.getId(), name);
    }

    public List<Machine> findByStatus(Status status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByUsername(username);

        return machineRepository.findAllByUserAndStatus(user.getId(), status.name());
    }

}
