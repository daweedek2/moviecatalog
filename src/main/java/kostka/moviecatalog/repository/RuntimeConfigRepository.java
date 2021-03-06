package kostka.moviecatalog.repository;

import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfiguration;
import kostka.moviecatalog.enumeration.RuntimeConfigurationEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RuntimeConfigRepository extends JpaRepository<RuntimeConfiguration, Long> {
    Optional<RuntimeConfiguration> findByConfigType(RuntimeConfigurationEnum configType);
}
