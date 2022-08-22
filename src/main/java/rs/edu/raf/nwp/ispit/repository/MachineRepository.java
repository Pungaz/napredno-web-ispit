package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.nwp.ispit.entity.Machine;

@Repository
public interface MachineRepository extends CrudRepository<Machine, Long> {
    void deleteById(Long machineId);

    Machine findMachinesById(Long machineId);
}
