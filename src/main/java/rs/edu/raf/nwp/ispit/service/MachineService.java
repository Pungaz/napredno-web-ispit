package rs.edu.raf.nwp.ispit.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import rs.edu.raf.nwp.ispit.dto.MachineDto;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.Status;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.exception.MachineAlreadyRunningException;
import rs.edu.raf.nwp.ispit.exception.MachineNotExistsException;
import rs.edu.raf.nwp.ispit.exception.NameAlreadyExistException;
import rs.edu.raf.nwp.ispit.repository.MachineRepository;
import rs.edu.raf.nwp.ispit.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static rs.edu.raf.nwp.ispit.entity.Status.RUNNING;
import static rs.edu.raf.nwp.ispit.entity.Status.STOPPED;

@Data
@Service
@AllArgsConstructor
public class MachineService {
    private MachineRepository machineRepository;
    private UserRepository userRepository;

    @Transactional
    public ResponseEntity<Machine> create(MachineDto machineDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!machineRepository.existsByName(machineDto.getName())) {
            Machine machine = Machine.builder()
                    .name(machineDto.getName())
                    .status(STOPPED)
                    .active(true)
                    .user(userRepository.findUserByUsername(username))
                    .dateCreated(LocalDate.now())
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

    public List<Machine> findByDate(LocalDate startingDate, LocalDate endingDate) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByUsername(username);

        return machineRepository.findAllByUserAndDate(user.getId(), startingDate, endingDate);
    }

    public ResponseEntity<Machine> realStart(long machineId, String username) throws InterruptedException {
        User user = userRepository.findUserByUsername(username);
        Machine machine = machineRepository.findMachineById(machineId);

        if (machine.getUser() == user) {
            if (machine.getStatus().equals(STOPPED)) {
//                Thread.sleep(10000);

                machine.setStatus(RUNNING);
                machineRepository.save(machine);
                return ResponseEntity.ok(machine);

            } else {
                throw new MachineAlreadyRunningException();
            }
        } else {
            throw new MachineNotExistsException();
        }
    }

    public ResponseEntity<?> start(long machineId) {
        WebClient client = WebClient.create("http://localhost:8080/machine");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        client.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/realStart")
                        .queryParam("machineId", machineId)
                        .queryParam("username", username)
                        .queryParam("secret", "nekiMojSecret")
                        .build())
                .retrieve()
                .toBodilessEntity()
                .subscribe();

        return ResponseEntity.ok("Start done successfully");
    }

    @Transactional
    public ResponseEntity<Machine> realStop(long machineId, String username) throws InterruptedException {
        User user = userRepository.findUserByUsername(username);
        Machine machine = machineRepository.findMachineById(machineId);

        if (machine.getUser() == user) {
            if (machine.getStatus().equals(RUNNING)) {
//                Thread.sleep(10000);

                machine.setStatus(STOPPED);
                machineRepository.save(machine);
                return ResponseEntity.ok(machine);

            } else {
                throw new MachineAlreadyRunningException();
            }
        } else {
            throw new MachineNotExistsException();
        }
    }

    public ResponseEntity<?> stop(long machineId) {
        WebClient client = WebClient.create("http://localhost:8080/machine");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        client.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/realStop")
                        .queryParam("machineId", machineId)
                        .queryParam("username", username)
                        .queryParam("secret", "nekiMojSecret")
                        .build())
                .retrieve()
                .toBodilessEntity()
                .subscribe();

        return ResponseEntity.ok("Stop done successfully");
    }

    @Transactional
    public ResponseEntity<Machine> realRestart(long machineId, String username) throws InterruptedException {
        User user = userRepository.findUserByUsername(username);
        Machine machine = machineRepository.findMachineById(machineId);

        if (machine.getUser() == user) {
            if (machine.getStatus().equals(RUNNING)) {
                Thread.sleep(5000);
                machine.setStatus(STOPPED);
                machineRepository.save(machine);
                machineRepository.flush();
                Thread.sleep(5000);

                machine = machineRepository.findMachineById(machineId);
                machine.setStatus(RUNNING);
                machineRepository.save(machine);
                return ResponseEntity.ok(machine);
            } else {
                throw new MachineAlreadyRunningException();
            }
        } else {
            throw new MachineNotExistsException();
        }
    }

    public ResponseEntity<?> restart(long machineId) {
        WebClient client = WebClient.create("http://localhost:8080/machine");
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        client.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/realRestart")
                        .queryParam("machineId", machineId)
                        .queryParam("username", username)
                        .queryParam("secret", "nekiMojSecret")
                        .build())
                .retrieve()
                .toBodilessEntity()
                .subscribe();

        return ResponseEntity.ok("Restart done successfully");
    }

}
