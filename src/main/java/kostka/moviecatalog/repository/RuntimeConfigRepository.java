package kostka.moviecatalog.repository;

import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuntimeConfigRepository extends JpaRepository<RuntimeConfig, Long> {
    Optional<RuntimeConfig> findByName(String name);
}
