package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.nwp.ispit.entity.ErrorMessage;
import rs.edu.raf.nwp.ispit.entity.Machine;

import java.util.List;

@Repository
public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {

    List<ErrorMessage> findAllByMachineIdIn(List<Long> machineIdList);
}
