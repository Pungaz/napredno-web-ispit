package rs.edu.raf.nwp.ispit.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs.edu.raf.nwp.ispit.dto.MachineDto;
import rs.edu.raf.nwp.ispit.entity.ErrorMessage;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.Status;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.exception.*;
import rs.edu.raf.nwp.ispit.repository.ErrorMessageRepository;
import rs.edu.raf.nwp.ispit.repository.MachineRepository;
import rs.edu.raf.nwp.ispit.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
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
    private final ErrorMessageRepository errorMessageRepository;
    private TaskScheduler taskScheduler;

    public ResponseEntity<Machine> create(MachineDto machineDto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!machineRepository.existsByNameAndActive(machineDto.getName(), true)) {
            Machine machine = Machine.builder()
                    .name(machineDto.getName())
                    .status(STOPPED)
                    .active(true)
                    .available(true)
                    .user(userRepository.findUserByUsername(username))
                    .dateCreated(LocalDate.now())
                    .build();

            machineRepository.save(machine);

            return ResponseEntity.ok(machine);
        }
        throw new NameAlreadyExistException();
    }

    public ResponseEntity<?> destroy(long machineId) {
        Machine machine = machineRepository.findMachineByIdAndActive(machineId, true);

        if (machine != null) {
            if (machine.getStatus() == STOPPED) {
                machine.setActive(false);
                machineRepository.save(machine);

                return ResponseEntity.ok("Machine deleted");

            } else {
                throw new MachineAlreadyRunningException();
            }
        } else {
            throw new MachineNotExistsException();
        }

    }

    public List<Machine> findAll() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findUserByUsername(username);

        return machineRepository.findAllByUserId(user.getId());
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

    public ResponseEntity<List<Machine>> findByDate(long startingDateLong, long endingDateLong) {
        LocalDate startingDate = Instant.ofEpochMilli(startingDateLong).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endingDate = Instant.ofEpochMilli(endingDateLong).atZone(ZoneId.systemDefault()).toLocalDate();

        if (startingDate != null && endingDate != null) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findUserByUsername(username);

            return ResponseEntity.ok(machineRepository.findAllByUserAndDate(user.getId(), startingDate, endingDate));
        } else {
            throw new DateIncorrectException();
        }
    }

    public ResponseEntity<?> start(long machineId, long scheduledTimestamp) {
        Machine machine = machineRepository.findMachineByIdAndActive(machineId, true);

        try {
            if (machine != null) {
                if (machine.isAvailable()) {
                    if (machine.getStatus().equals(STOPPED)) {

                        machine.setAvailable(false);

                        LocalDateTime scheduledDateTime = new Timestamp(scheduledTimestamp).toLocalDateTime();

                        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
                        taskScheduler = new ConcurrentTaskScheduler(localExecutor);

                        taskScheduler.schedule(() -> {
                                    try {
                                        startAsync(machine, 0);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                Date.from(scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant()));

                        return ResponseEntity.ok("Machine started");
                    } else {
                        throw new MachineAlreadyRunningException();
                    }
                } else {
                    throw new MachineNotAvailableException();
                }
            } else {
                throw new MachineNotExistsException();
            }
        } catch (Exception e) {
            writeErrorToDb("start", e, machine.getId());
            throw e;
        }
    }

    public void startAsync(Machine machine, int retryCount) throws InterruptedException {
        if (retryCount == 3) {
            return;
        }

        try {
            if (machine.getStatus().equals(STOPPED)) {
                Thread.sleep(10000);

                machine.setStatus(RUNNING);
                machine.setAvailable(true);
                machineRepository.saveAndFlush(machine);
            } else {
                throw new MachineAlreadyRunningException();
            }
        } catch (Exception e) {
            if (retryCount == 2) {
                writeErrorToDb("start async", e, machine.getId());
                throw e;
            }

            System.out.println("START has retried");
            Thread.sleep(1000);
            startAsync(machine, ++retryCount);
        }
    }

    public ResponseEntity<?> stop(long machineId, long scheduledTimestamp) {
        Machine machine = machineRepository.findMachineByIdAndActive(machineId, true);

        try {
            if (machine != null) {
                if (machine.isAvailable()) {
                    if (machine.getStatus().equals(RUNNING)) {

                        machine.setAvailable(false);

                        LocalDateTime scheduledDateTime = new Timestamp(scheduledTimestamp).toLocalDateTime();

                        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
                        taskScheduler = new ConcurrentTaskScheduler(localExecutor);

                        taskScheduler.schedule(() -> {
                                    try {
                                        stopAsync(machine, 0);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                Date.from(scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant()));

                        return ResponseEntity.ok("Machine stopped");
                    } else {
                        throw new MachineAlreadyStoppedException();
                    }
                } else {
                    throw new MachineNotAvailableException();
                }
            } else {
                throw new MachineNotExistsException();
            }
        } catch (Exception e) {
            writeErrorToDb("stop", e, machine.getId());
            throw e;
        }
    }

    public void stopAsync(Machine machine, int retryCount) throws InterruptedException {
        if (retryCount == 3) {
            return;
        }

        try {
            if (machine.getStatus().equals(RUNNING)) {
                Thread.sleep(10000);

                machine.setStatus(STOPPED);
                machine.setAvailable(true);
                machineRepository.saveAndFlush(machine);
            } else {
                throw new MachineAlreadyStoppedException();
            }
        } catch (Exception e) {
            if (retryCount == 2) {
                writeErrorToDb("stop async", e, machine.getId());
                throw e;
            }

            System.out.println("STOP retried");
            Thread.sleep(1000);
            stopAsync(machine, ++retryCount);
        }
    }

    public ResponseEntity<?> restart(long machineId, long scheduledTimestamp) {
        Machine machine = machineRepository.findMachineByIdAndActive(machineId, true);

        try {
            if (machine != null) {
                if (machine.isAvailable()) {
                    if (machine.getStatus().equals(RUNNING)) {

                        machine.setAvailable(false);

                        LocalDateTime scheduledDateTime = new Timestamp(scheduledTimestamp).toLocalDateTime();

                        ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
                        taskScheduler = new ConcurrentTaskScheduler(localExecutor);

                        taskScheduler.schedule(() -> {
                                    try {
                                        restartAsync(machine, 0);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                },
                                Date.from(scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant()));

                        return ResponseEntity.ok("Machine restarted");
                    } else {
                        throw new MachineAlreadyStoppedException();
                    }
                } else {
                    throw new MachineNotAvailableException();
                }
            } else {
                throw new MachineNotExistsException();
            }
        } catch (Exception e) {
            writeErrorToDb("restart", e, machine.getId());
            throw e;
        }
    }

    public void restartAsync(Machine machine, int retryCount) throws InterruptedException {
        if (retryCount == 3) {
            return;
        }

        try {
            Thread.sleep(5000);
            machine.setStatus(STOPPED);
            machineRepository.saveAndFlush(machine);

            Thread.sleep(5000);
            machine.setStatus(RUNNING);
            machine.setAvailable(true);
            machineRepository.saveAndFlush(machine);

        } catch (Exception e) {
            if (retryCount == 2) {
                writeErrorToDb("restart async", e, machine.getId());
                throw e;
            }

            System.out.println("RESTART retried");
            Thread.sleep(1000);
            restartAsync(machine, ++retryCount);
        }
    }

    public void writeErrorToDb(String operation, Exception e, long machineId) {
        ErrorMessage errorMessage = ErrorMessage.builder()
                .timestamp(System.currentTimeMillis())
                .operation(operation)
                .message(e.getMessage())
                .machineId(machineId)
                .build();
        errorMessageRepository.save(errorMessage);
    }

}
