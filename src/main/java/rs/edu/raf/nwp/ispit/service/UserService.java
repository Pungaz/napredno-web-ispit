package rs.edu.raf.nwp.ispit.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.edu.raf.nwp.ispit.dto.UserDto;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.entity.security.Permission;
import rs.edu.raf.nwp.ispit.entity.security.UserPermission;
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

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User myUser = this.findByUsername(username);
        if (myUser == null) {
            throw new UsernameNotFoundException("User name " + username + " not found");
        }
        return new org.springframework.security.core.userdetails.User(myUser.getUsername(), myUser.getPassword(), findPermissionsByUsername(username));
    }

    @Transactional
    public ResponseEntity<?> create(UserDto userDTO) {

        if (!userRepository.existsByUsername(userDTO.getUsername())) {
            List<Long> userDtoPermissions = userDTO.getPermissions();
            Set<Long> userDtoPermissionsSet = new HashSet<>(userDtoPermissions);

            List<Permission> existingPermissions = permissionRepository.findByIdIn(userDTO.getPermissions());

            List<String> existingPermissionsFromUserDtoString =
                    existingPermissions.stream().map(Permission::getName).toList();

            if (userDtoPermissionsSet.size() != existingPermissionsFromUserDtoString.size()) {
                return ResponseEntity.status(400).body("Invalid permission sent");
            }

            User user = User.builder()
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .firstName(userDTO.getFirstName())
                    .lastName(userDTO.getLastName())
                    .address(userDTO.getAddress())
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

            return ResponseEntity.status(200).body(user);
        }
        return ResponseEntity.status(500).body("Username already exists");
    }

    public Page<User> paginate(Integer page, Integer size) {
        return this.userRepository.findAll(PageRequest.of(page, size, Sort.by("username").descending()));
    }

    public User findByUsername(String username) {
        return this.userRepository.findUserByUsername(username);
    }

    public Collection<? extends GrantedAuthority> findPermissionsByUsername(String username) {
        User user = userRepository.findUserByUsername(username);

        List<UserPermission> userPermissions = userPermissionRepository.findUserPermissionsByUser(user);


        return userPermissions.stream().map(UserPermission::getPermission).toList();
    }

}
