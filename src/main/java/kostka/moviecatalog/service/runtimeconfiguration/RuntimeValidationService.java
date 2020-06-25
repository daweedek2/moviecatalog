package kostka.moviecatalog.service.runtimeconfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kostka.moviecatalog.annotation.ValidRuntimeConfiguration;
import kostka.moviecatalog.dto.RuntimeConfigDto;
import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfiguration;
import kostka.moviecatalog.enumeration.RuntimeConfigurationEnum;
import kostka.moviecatalog.exception.MissingRuntimeConfigurationException;
import kostka.moviecatalog.repository.RuntimeConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class RuntimeValidationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeValidationService.class);
    private final RuntimeConfigRepository runtimeConfigRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public RuntimeValidationService(final RuntimeConfigRepository runtimeConfigRepository) {
        this.runtimeConfigRepository = runtimeConfigRepository;
    }

    @ValidRuntimeConfiguration
    public RuntimeConfiguration getValidatedRuntimeConfiguration(final RuntimeConfigDto dto) {
        RuntimeConfiguration runtimeConfiguration =  getConfigByType(getTypeByName(dto.getConfigName()));
        runtimeConfiguration.setOptions(getOptionsJson(dto.getOptions()));
        return runtimeConfiguration;
    }

    String getOptionsJson(final Object options) {
        try {
            return mapper.writeValueAsString(options);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "ERROR";
        }
    }

    public RuntimeConfigurationEnum getTypeByName(final String name) {
        return getValueOf(RuntimeConfigurationEnum.class, name)
                .orElseThrow(MissingRuntimeConfigurationException::new);
    }

    public Optional<RuntimeConfigurationEnum> getValueOf(final Class<RuntimeConfigurationEnum> configType,
                                                         final String name){
        RuntimeConfigurationEnum configValue = null;
        try {
            configValue = Enum.valueOf(configType, name);
        }catch(IllegalArgumentException ex ){
            throw new MissingRuntimeConfigurationException();
        }
        return Optional.of(configValue);
    }

    public RuntimeConfiguration getConfigByType(final RuntimeConfigurationEnum configType) {
        LOGGER.info("Getting config by Type {}", configType);
        return runtimeConfigRepository.findByConfigType(configType)
                .orElseThrow(MissingRuntimeConfigurationException::new);
    }
}
