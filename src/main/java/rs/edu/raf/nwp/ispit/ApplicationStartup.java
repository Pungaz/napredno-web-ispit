package rs.edu.raf.nwp.ispit;

import lombok.AllArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.entity.security.Permission;
import rs.edu.raf.nwp.ispit.entity.security.UserPermission;
import rs.edu.raf.nwp.ispit.repository.PermissionRepository;
import rs.edu.raf.nwp.ispit.repository.UserPermissionRepository;
import rs.edu.raf.nwp.ispit.repository.UserRepository;

import java.util.ArrayList;

@Component
@AllArgsConstructor
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        permissionRepository.save(Permission.builder()
                .name("can_create_users")
                .build());
        permissionRepository.save(Permission.builder()
                .name("can_read_users")
                .build());
        permissionRepository.save(Permission.builder()
                .name("can_update_users")
                .build());
        permissionRepository.save(Permission.builder()
                .name("can_delete_users")
                .build());

        permissionRepository.save(Permission.builder()
                .name("can_search_machines")
                .build());
        permissionRepository.save(Permission.builder()
                .name("can_start_machines")
                .build());
        permissionRepository.save(Permission.builder()
                .name("can_stop_machines")
                .build());
        permissionRepository.save(Permission.builder()
                .name("can_restart_machines")
                .build());
        permissionRepository.save(Permission.builder()
                .name("can_create_machines")
                .build());
        permissionRepository.save(Permission.builder()
                .name("can_destroy_machines")
                .build());


        ArrayList<Permission> permissions = (ArrayList<Permission>) permissionRepository.findAll();

        User user = User.builder()
                .username("Ja")
                .password(passwordEncoder.encode("Neki"))
                .firstname("Ime")
                .lastname("Prezime")
                .address("Neki br 1")
                .build();

        userRepository.save(user);

        for (Permission permission : permissions) {
            userPermissionRepository.save(
                    UserPermission.builder()
                            .user(user)
                            .permission(permission)
                            .permissionName(permission.getName())
                            .username(user.getUsername())
                            .build());
        }
    }
}
