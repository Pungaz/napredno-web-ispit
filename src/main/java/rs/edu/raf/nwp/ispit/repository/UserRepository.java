package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.nwp.ispit.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUsername(String username);

    User findUserById(long id);

    boolean existsByUsername(String username);

    boolean existsById(long id);



//    @Modifying
//    @Query("update User u set u.balance = u.balance + :amount")
//    @Transactional
//    public void increaseBalance(@Param("amount") Integer amount);

}
