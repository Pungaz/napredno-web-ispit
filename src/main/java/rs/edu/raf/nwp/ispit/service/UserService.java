package rs.edu.raf.nwp.ispit.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.nwp.ispit.dto.UserDto;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.entity.security.Permission;
import rs.edu.raf.nwp.ispit.entity.security.UserPermission;
import rs.edu.raf.nwp.ispit.exception.PermissionNotExistException;
import rs.edu.raf.nwp.ispit.exception.UserNotExistException;
import rs.edu.raf.nwp.ispit.exception.NameAlreadyExistException;
import rs.edu.raf.nwp.ispit.repository.MachineRepository;
import rs.edu.raf.nwp.ispit.repository.PermissionRepository;
import rs.edu.raf.nwp.ispit.repository.UserPermissionRepository;
import rs.edu.raf.nwp.ispit.repository.UserRepository;

import java.util.*;

@Data
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final PermissionRepository permissionRepository;

    private final MachineRepository machineRepository;


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User myUser = userRepository.findUserByUsername(username);
        if (myUser == null) {
            throw new UsernameNotFoundException("User name " + username + " not found");
        }
        return new org.springframework.security.core.userdetails.User(myUser.getUsername(), myUser.getPassword(), findPermissionsByUsername(username));
    }

    @Transactional
    public ResponseEntity<User> create(UserDto userDto) {
        if (!userRepository.existsByUsername(userDto.getUsername())) {
            List<Permission> existingPermissions = validatePermissions(userDto);

            User user = User.builder()
                    .username(userDto.getUsername())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .firstname(userDto.getFirstname())
                    .lastname(userDto.getLastname())
                    .address(userDto.getAddress())
                    .userPermissions(null)
                    .build();

            userRepository.save(user);

            for (Permission permission : existingPermissions) {
                userPermissionRepository.save(
                        UserPermission.builder()
                                .user(user)
                                .permission(permission)
                                .permissionName(permission.getName())
                                .username(user.getUsername())
                                .build());
            }
            return ResponseEntity.ok(user);
        }
        throw new NameAlreadyExistException();
    }

    public ResponseEntity<List<User>> read() {
        List<User> users = (List<User>) this.userRepository.findAll();
        if (users.isEmpty()) {
            throw new UserNotExistException();
        }

        for (User user: users){
            user.setMachines(machineRepository.findAllByUser(user));
        }

        return ResponseEntity.ok(users);
    }

    @Transactional
    public ResponseEntity<User> update(UserDto userDto, long userBeingUpdatedId) {
        if (!userRepository.existsByUsername(userDto.getUsername())) {
            if (userRepository.existsById(userBeingUpdatedId)) {
                List<Permission> existingPermissions = validatePermissions(userDto);

                User user = userRepository.findUserById(userBeingUpdatedId);
                user.setId(userBeingUpdatedId);
                user.setUsername(userDto.getUsername());
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                user.setFirstname(userDto.getFirstname());
                user.setLastname(userDto.getLastname());
                user.setAddress(userDto.getAddress());
                user.setUserPermissions(null);

                userRepository.save(user);

                userPermissionRepository.deleteAllByUserId(userBeingUpdatedId);

                for (Permission permission : existingPermissions) {
                    userPermissionRepository.save(
                            UserPermission.builder()
                                    .user(user)
                                    .permission(permission)
                                    .permissionName(permission.getName())
                                    .username(user.getUsername())
                                    .build());
                }
                return ResponseEntity.ok(user);
            }
            throw new UserNotExistException();
        }
        throw new NameAlreadyExistException();
    }

    @Transactional
    public ResponseEntity<?> delete(long userId) {
        User user = userRepository.findUserById(userId);

        if (user != null) {
            List<Machine> usersMachines = machineRepository.findAllByUserId(user.getId());
            List<Machine> machinesWithoutTheUser = new ArrayList<>();

            for (Machine machine : usersMachines) {
                machine.setUser(null);
                machine.setActive(false);
                machinesWithoutTheUser.add(machine);
            }

            machineRepository.saveAll(machinesWithoutTheUser);

            userRepository.deleteById(userId);
            return ResponseEntity.ok("Deleted");
        }
        throw new UserNotExistException();
    }

    private List<Permission> validatePermissions(UserDto userDto) {
        List<Long> userDtoPermissions = userDto.getPermissions();

        Set<Long> userDtoPermissionsSet = new HashSet<>(userDtoPermissions);

        List<Permission> existingPermissions = permissionRepository.findByIdIn(userDto.getPermissions());

        List<String> existingPermissionsFromUserDtoString =
                existingPermissions.stream().map(Permission::getName).toList();

        if (userDtoPermissionsSet.size() != existingPermissionsFromUserDtoString.size()) {
            throw new PermissionNotExistException();
        }
        return existingPermissions;
    }

    public Collection<? extends GrantedAuthority> findPermissionsByUsername(String username) {
        User user = userRepository.findUserByUsername(username);

        List<UserPermission> userPermissions = userPermissionRepository.findUserPermissionsByUser(user);

        return userPermissions.stream().map(UserPermission::getPermission).toList();
    }


}
