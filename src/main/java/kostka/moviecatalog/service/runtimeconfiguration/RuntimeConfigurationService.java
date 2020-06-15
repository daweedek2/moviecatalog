package kostka.moviecatalog.service.runtimeconfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import kostka.moviecatalog.dto.RuntimeConfigDto;
import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfiguration;
import kostka.moviecatalog.enumeration.RuntimeConfigurationEnum;
import kostka.moviecatalog.exception.MissingRuntimeConfigurationException;
import kostka.moviecatalog.repository.RuntimeConfigRepository;
import kostka.moviecatalog.service.JsonConvertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

import static kostka.moviecatalog.factory.RuntimeConfigurationFactory.getOptionsClass;

@Service
public class RuntimeConfigurationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeConfigurationService.class);
    private RuntimeConfigRepository runtimeConfigRepository;
    private JsonConvertService jsonConvertService;
    private final RuntimeConfigRepository runtimeConfigRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public RuntimeConfigurationService(final RuntimeConfigRepository runtimeConfigRepository,
                                       final JsonConvertService jsonConvertService) {
        this.runtimeConfigRepository = runtimeConfigRepository;
        this.jsonConvertService = jsonConvertService;
    }

    public RuntimeConfiguration updateRuntimeConfiguration(
            final RuntimeConfigDto dto) throws JsonProcessingException {
        String configName = dto.getConfigName();
    public RuntimeConfiguration update(
            final RuntimeConfigDto dto) {
        RuntimeConfigurationEnum runtimeConfigType = getTypeByName(dto.getConfigName());
        Map<String, String> options = dto.getOptions();
        RuntimeConfiguration runtimeConfig = this.getByName(configName);
        runtimeConfig.setOptions(jsonConvertService.dataToJson(options));
        RuntimeConfiguration runtimeConfig = this.getConfigByType(runtimeConfigType);
        runtimeConfig.setOptions(getOptionsJson(options));

        LOGGER.info("Runtime Configuration '{}' has new options: '{}'.", runtimeConfigType.getName(), options);
        return runtimeConfigRepository.save(runtimeConfig);
    }

    public <T> T getRuntimeConfigurationOptions(
            final RuntimeConfigurationEnum runtimeConfigEnum) {
        LOGGER.info("Getting options of the Runtime Configuration '{}'.", runtimeConfigEnum.getName());
        RuntimeConfiguration runtimeConfig = this.getConfigByType(runtimeConfigEnum);
        try {
            return jsonConvertService.jsonToData(runtimeConfig.getOptions(), getOptionsClass(runtimeConfigEnum));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    private String getOptionsJson(final Object options) {
        try {
            return jsonConvertService.dataToJson(options);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "ERROR";
        }
    }

    public RuntimeConfiguration getConfigByType(final RuntimeConfigurationEnum configType) {
        LOGGER.info("Getting config by Type {}", configType);
        return runtimeConfigRepository.findByConfigType(configType)
                .orElseThrow(MissingRuntimeConfigurationException::new);
    }

    public RuntimeConfiguration create(final @Valid RuntimeConfigDto dto) {
        LOGGER.info("creating new config with name '{}'", dto.getConfigName());
        RuntimeConfigurationEnum configType = RuntimeConfigurationEnum.valueOf(dto.getConfigName());
        RuntimeConfiguration config = new RuntimeConfiguration();
        config.setConfigType(configType);
        Map<String, String> options = dto.getOptions();
        config.setOptions(getOptionsJson(options));
        return runtimeConfigRepository.save(config);
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
}
