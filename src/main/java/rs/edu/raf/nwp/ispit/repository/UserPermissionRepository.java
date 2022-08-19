package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.entity.security.UserPermission;

import java.util.List;

public interface UserPermissionRepository extends PagingAndSortingRepository<UserPermission, Long> {
    List<UserPermission> findUserPermissionsByUser(User user);
}
