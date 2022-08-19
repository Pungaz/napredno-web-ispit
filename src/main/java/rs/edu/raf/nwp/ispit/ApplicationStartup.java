package rs.edu.raf.nwp.ispit;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import rs.edu.raf.nwp.ispit.entity.security.Permission;
import rs.edu.raf.nwp.ispit.repository.PermissionRepository;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final PermissionRepository permissionRepository;

    @Autowired
    public ApplicationStartup(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

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

    }
}
