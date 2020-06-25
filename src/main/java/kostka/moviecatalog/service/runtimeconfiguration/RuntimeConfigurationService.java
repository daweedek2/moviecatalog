package kostka.moviecatalog.service.runtimeconfiguration;

import com.fasterxml.jackson.databind.ObjectMapper;
import kostka.moviecatalog.dto.RuntimeConfigDto;
import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfiguration;
import kostka.moviecatalog.enumeration.RuntimeConfigurationEnum;
import kostka.moviecatalog.repository.RuntimeConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Map;

import static kostka.moviecatalog.factory.RuntimeConfigurationFactory.getOptionsClass;

@Service
public class RuntimeConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeConfigurationService.class);
    private final RuntimeConfigRepository runtimeConfigRepository;
    private final RuntimeValidationService runtimeValidationService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public RuntimeConfigurationService(
            final RuntimeConfigRepository runtimeConfigRepository,
            final RuntimeValidationService runtimeValidationService) {
        this.runtimeConfigRepository = runtimeConfigRepository;
        this.runtimeValidationService = runtimeValidationService;
    }

    public RuntimeConfiguration create(final @Valid RuntimeConfigDto dto) {
        LOGGER.info("creating new config with name '{}'", dto.getConfigName());
        RuntimeConfigurationEnum configType = RuntimeConfigurationEnum.valueOf(dto.getConfigName());
        RuntimeConfiguration config = new RuntimeConfiguration();
        config.setConfigType(configType);
        Map<String, String> options = dto.getOptions();
        config.setOptions(runtimeValidationService.getOptionsJson(options));
        return save(config);
    }

    public RuntimeConfiguration update(final RuntimeConfigDto dto) {
        var runtimeConfiguration =  runtimeValidationService.getValidatedRuntimeConfiguration(dto);
        return save(runtimeConfiguration);
    }

    public RuntimeConfiguration save(final RuntimeConfiguration runtimeConfiguration) {
        LOGGER.info("Saved Runtime Configuration '{}' with options: '{}'.",
                runtimeConfiguration.getConfigType().getName(),
                runtimeConfiguration.getOptions());
        return runtimeConfigRepository.save(runtimeConfiguration);
    }

    public <T> T getRuntimeConfigurationOptions(
            final RuntimeConfigurationEnum runtimeConfigEnum) {
        LOGGER.info("Getting options of the Runtime Configuration '{}'.", runtimeConfigEnum.getName());
        RuntimeConfiguration runtimeConfig = runtimeValidationService.getConfigByType(runtimeConfigEnum);
        try {
            return mapper.readValue(runtimeConfig.getOptions(), getOptionsClass(runtimeConfigEnum));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
