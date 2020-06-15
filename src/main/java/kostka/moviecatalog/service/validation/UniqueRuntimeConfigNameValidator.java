package kostka.moviecatalog.service.validation;

import kostka.moviecatalog.annotation.UniqueRuntimeConfigName;
import kostka.moviecatalog.exception.MissingRuntimeConfigurationException;
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

    @Override
    public boolean isValid(final String configName, final ConstraintValidatorContext context) {
        try {
            runtimeConfigurationService.getByName(configName);
            return false;
        } catch (MissingRuntimeConfigurationException e) {
            return true;
        }
    }

    @Override
    public void initialize(final UniqueRuntimeConfigName constraintAnnotation) {
    }
}
