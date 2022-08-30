package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.edu.raf.nwp.ispit.entity.Machine;
import rs.edu.raf.nwp.ispit.entity.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    void deleteById(Long machineId);

    Machine findMachineById(Long id);

    boolean existsById(Long id);

    boolean existsByName(String name);

    List<Machine> findAllByUser(User user);

    @Query(value = "SELECT * FROM machine WHERE user_id = ?1 AND active = ?2", nativeQuery = true)
    List<Machine> findAllByUserIdAndActive(Long userId, boolean active);

    @Query(value = "SELECT * FROM machine WHERE user_id = ?1 AND name LIKE %?2% AND active = true", nativeQuery = true)
    List<Machine> findAllByUserAndName(Long userId, String name);

    @Query(value = "SELECT * FROM machine WHERE user_id = ?1 AND status = ?2 AND active = true", nativeQuery = true)
    List<Machine> findAllByUserAndStatus(Long userId, String status);

    @Query(value = "SELECT * FROM machine WHERE user_id = ?1 AND date_created BETWEEN ?2 AND ?3 AND active = true", nativeQuery = true)
    List<Machine> findAllByUserAndDate(Long userId, LocalDate startingDate, LocalDate endingDate);

}
