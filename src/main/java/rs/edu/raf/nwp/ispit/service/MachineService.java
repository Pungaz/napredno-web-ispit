package rs.edu.raf.nwp.ispit.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.nwp.ispit.dto.MachineDto;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.Status;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.exception.DateIncorrectException;
import rs.edu.raf.nwp.ispit.exception.MachineAlreadyRunningException;
import rs.edu.raf.nwp.ispit.exception.MachineNotExistsException;
import rs.edu.raf.nwp.ispit.exception.NameAlreadyExistException;
import rs.edu.raf.nwp.ispit.repository.MachineRepository;
import rs.edu.raf.nwp.ispit.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static rs.edu.raf.nwp.ispit.entity.Status.RUNNING;
import static rs.edu.raf.nwp.ispit.entity.Status.STOPPED;

@Data
@Service
@RequiredArgsConstructor
public class MachineService {
    private final MachineRepository machineRepository;
    private final UserRepository userRepository;
    private TaskScheduler taskScheduler;

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

    public ResponseEntity<List<Machine>> findByStatus(Status status) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByUsername(username);

        return ResponseEntity.ok(machineRepository.findAllByUserAndStatus(user.getId(), status.name()));
    }

    public ResponseEntity<List<Machine>> findByDate(LocalDate startingDate, LocalDate endingDate) {
        if (startingDate != null && endingDate != null) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findUserByUsername(username);

            return ResponseEntity.ok(machineRepository.findAllByUserAndDate(user.getId(), startingDate, endingDate));
        } else {
            throw new DateIncorrectException();
        }
    }

    @Transactional
    public void start(long machineId, long scheduledTimestamp) {
        LocalDateTime scheduledDateTime = new Timestamp(scheduledTimestamp).toLocalDateTime();

        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        taskScheduler = new ConcurrentTaskScheduler(localExecutor);

        taskScheduler.schedule(() -> {
                    try {
                        Machine machine = machineRepository.findMachineById(machineId);
                        if (machine.getStatus().equals(STOPPED)) {
                            Thread.sleep(10000);

                            machine.setStatus(RUNNING);
                            machineRepository.saveAndFlush(machine);

                        } else {
                            throw new MachineAlreadyRunningException();
                        }


                    } catch (Exception e) {
                        //TODO upisi u bazu
                    }
                },
                Date.from(scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

    @Transactional
    public void stop(long machineId, long scheduledTimestamp) {
        LocalDateTime scheduledDateTime = new Timestamp(scheduledTimestamp).toLocalDateTime();

        taskScheduler.schedule(() -> {
            try {
                Machine machine = machineRepository.findMachineById(machineId);

                if (machine.getStatus().equals(RUNNING)) {
                    Thread.sleep(10000);

                    machine.setStatus(STOPPED);
                    machineRepository.saveAndFlush(machine);
                } else {
                    throw new MachineAlreadyRunningException();
                }
            } catch (Exception e) {
                //Todo upisi u bazu
            }
        }, Date.from(scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant()));

    }

    @Transactional
    public void restart(long machineId, long scheduledTimestamp) {
        LocalDateTime scheduledDateTime = new Timestamp(scheduledTimestamp).toLocalDateTime();

        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
        taskScheduler = new ConcurrentTaskScheduler(localExecutor);

        taskScheduler.schedule(() -> {
                    Machine machine = machineRepository.findMachineById(machineId);

                    try {
                        if (machine.getStatus().equals(RUNNING)) {
                            Thread.sleep(5000);
                            machine.setStatus(STOPPED);
                            machineRepository.saveAndFlush(machine);

                            Thread.sleep(5000);
                            machine.setVersion(machine.getVersion() + 1);
                            machine.setStatus(RUNNING);
                            machineRepository.saveAndFlush(machine);
                        } else {
                            throw new MachineAlreadyRunningException();
                        }
                    } catch (Exception e) {
                        //TODO sacuvaj u bazu
                    }
                },
                Date.from(scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant()));
    }

}
