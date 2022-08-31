package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    Machine findMachineByIdAndActive(Long id, boolean active);

    boolean existsByNameAndActive(String name, boolean active);

    Set<Machine> findAllByUser(User user);

    @Query(value = "SELECT * FROM machine WHERE user_id = ?1 AND active = true", nativeQuery = true)
    List<Machine> findAllByUserId(Long userId);

    @Query(value = "SELECT * FROM machine WHERE user_id = ?1 AND name LIKE %?2% AND active = true", nativeQuery = true)
    List<Machine> findAllByUserAndName(Long userId, String name);

    @Query(value = "SELECT * FROM machine WHERE user_id = ?1 AND status = ?2 AND active = true", nativeQuery = true)
    List<Machine> findAllByUserAndStatus(Long userId, String status);

    @Query(value = "SELECT * FROM machine WHERE user_id = ?1 AND date_created BETWEEN ?2 AND ?3 AND active = true", nativeQuery = true)
    List<Machine> findAllByUserAndDate(Long userId, LocalDate startingDate, LocalDate endingDate);

}
