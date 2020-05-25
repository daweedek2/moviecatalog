package kostka.moviecatalog.service.runtimeconfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfig;
import kostka.moviecatalog.enums.RuntimeConfigurationEnum;
import kostka.moviecatalog.exception.MissingRuntimeConfigurationException;
import kostka.moviecatalog.repository.RuntimeConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuntimeConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeConfigurationService.class);

    private RuntimeConfigRepository runtimeConfigRepository;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public RuntimeConfigurationService(
            final RuntimeConfigRepository runtimeConfigRepository) {
        this.runtimeConfigRepository = runtimeConfigRepository;
    }

    public <T> void updateRuntimeConfiguration(
            final String configName,
            final T value) throws JsonProcessingException {
        RuntimeConfig runtimeConfig = this.getByName(configName);
        runtimeConfig.setValue(getValueJson(value));

        runtimeConfigRepository.save(runtimeConfig);
        LOGGER.info("Runtime Configuration '{}' is updated to value '{}'.", configName, value);
    }

    public <T> T getRuntimeConfigurationValue(
            final RuntimeConfigurationEnum runtimeConfigEnum,
            final Class<T> valueType) throws JsonProcessingException {
        LOGGER.info("Getting value of the Runtime Configuration '{}'.", runtimeConfigEnum.getName());
        RuntimeConfig runtimeConfig = this.getByName(runtimeConfigEnum.getName());
        return mapper.readValue(runtimeConfig.getValue(), valueType);
    }

    private String getValueJson(final Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    private RuntimeConfig getByName(final String name) {
        return runtimeConfigRepository.findByName(name).orElseThrow(MissingRuntimeConfigurationException::new);
    }
}
