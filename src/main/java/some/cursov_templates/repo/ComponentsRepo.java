package some.cursov_templates.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import some.cursov_templates.entity.PcComponent;

import java.util.List;

import static some.cursov_templates.Constants.*;

@Repository
public interface ComponentsRepo extends JpaRepository<PcComponent, Integer> {

    @Query(value =
            "select * from " + TABLE_COMPONENTS + " where " + TYPE + " = ?1",
        nativeQuery = true)
    List<PcComponent> getAllByType(Integer type);
}
