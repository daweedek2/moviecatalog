package kostka.moviecatalog.service.runtimeconfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kostka.moviecatalog.dto.RuntimeConfigDto;
import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfiguration;
import kostka.moviecatalog.enumeration.RuntimeConfigurationEnum;
import kostka.moviecatalog.exception.MissingRuntimeConfigurationException;
import kostka.moviecatalog.repository.RuntimeConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static kostka.moviecatalog.factory.RuntimeConfigurationFactory.getOptionsClass;

@Service
public class RuntimeConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeConfigurationService.class);
    private RuntimeConfigRepository runtimeConfigRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public RuntimeConfigurationService(final RuntimeConfigRepository runtimeConfigRepository) {
        this.runtimeConfigRepository = runtimeConfigRepository;
    }

    public RuntimeConfiguration updateRuntimeConfiguration(
            final RuntimeConfigDto dto) {
        String configName = dto.getConfigName();
        Map<String, String> options = dto.getOptions();
        RuntimeConfiguration runtimeConfig = this.getByName(configName);
        runtimeConfig.setOptions(getOptionsJson(options));

        LOGGER.info("Runtime Configuration '{}' has new options: '{}'.", configName, options);
        return runtimeConfigRepository.save(runtimeConfig);
    }

    public <T> T getRuntimeConfigurationOptions(
            final RuntimeConfigurationEnum runtimeConfigEnum) {
        LOGGER.info("Getting options of the Runtime Configuration '{}'.", runtimeConfigEnum.getName());
        RuntimeConfiguration runtimeConfig = this.getByName(runtimeConfigEnum.getName());
        try {
            return mapper.readValue(runtimeConfig.getOptions(), getOptionsClass(runtimeConfigEnum));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    private String getOptionsJson(final Object options) {
        try {
            return mapper.writeValueAsString(options);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "ERROR";
        }
    }

    private RuntimeConfiguration getByName(final String name) {
        return runtimeConfigRepository.findByName(name).orElseThrow(MissingRuntimeConfigurationException::new);
    }
}
