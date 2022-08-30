package rs.edu.raf.nwp.ispit.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.edu.raf.nwp.ispit.entity.ErrorMessage;

@Repository
public interface ErrorMessageRepository extends CrudRepository<ErrorMessage, Long> {
}
