package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.nwp.ispit.entity.security.Permission;

import java.util.List;

@Repository
public interface PermissionRepository extends CrudRepository<Permission, Long> {
    List<Permission> findByIdIn(List<Long> ids);
}
