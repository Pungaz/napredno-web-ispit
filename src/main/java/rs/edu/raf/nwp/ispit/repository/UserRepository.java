package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.nwp.ispit.entity.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    User findUserByUsername(String username);

    User findUserById(long id);

    boolean existsByUsername(String username);

    boolean existsById(long id);

}
