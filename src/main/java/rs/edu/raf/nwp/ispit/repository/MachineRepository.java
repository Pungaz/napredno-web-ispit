package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs.edu.raf.nwp.ispit.entity.Machine;

import java.util.List;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    void deleteById(Long machineId);

    Machine findMachineById(Long id);

    boolean existsById(Long id);

    @Query(value = "SELECT * FROM machine WHERE user_id = ?1 AND active = ?2", nativeQuery = true)
    List<Machine> findAllRunningMachinesByUserIdAndActive(Long userId, boolean active);
}
