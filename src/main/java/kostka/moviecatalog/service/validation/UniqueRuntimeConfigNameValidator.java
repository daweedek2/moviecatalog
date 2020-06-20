package kostka.moviecatalog.service.validation;

import kostka.moviecatalog.annotation.UniqueRuntimeConfigName;
import kostka.moviecatalog.service.runtimeconfiguration.RuntimeConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Service
public class UniqueRuntimeConfigNameValidator implements ConstraintValidator<UniqueRuntimeConfigName, String> {
    private final RuntimeConfigurationService runtimeConfigurationService;

    @Autowired
    public UniqueRuntimeConfigNameValidator(final RuntimeConfigurationService runtimeConfigurationService) {
    this.runtimeConfigurationService = runtimeConfigurationService;
    }

    /**
     * Verifies if the configuration is already used.
     * @param configName string value od the runtime configuration
     * @param context context
     * @return boolean.
     */
    @Override
    public boolean isValid(final String configName, final ConstraintValidatorContext context) {
        try {
            runtimeConfigurationService.getConfigByType(
                    runtimeConfigurationService.getTypeByName(configName)
            );
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void initialize(final UniqueRuntimeConfigName constraintAnnotation) {
    }
}
