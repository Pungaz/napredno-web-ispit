package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import rs.edu.raf.nwp.ispit.entity.User;
import rs.edu.raf.nwp.ispit.entity.security.UserPermission;

import java.util.List;

public interface UserPermissionRepository extends CrudRepository<UserPermission, Long> {
    List<UserPermission> findUserPermissionsByUser(User user);

    void deleteAllByUserId(Long userId);
}
