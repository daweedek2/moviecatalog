package kostka.moviecatalog.service.validation;

import kostka.moviecatalog.annotation.ValidRuntimeConfiguration;
import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfiguration;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static kostka.moviecatalog.service.validation.VisibleMoviesRuntimeConfigValidator.validateVisibleMoviesOptions;

@Service
public class RuntimeConfigurationsValidator implements ConstraintValidator<ValidRuntimeConfiguration, RuntimeConfiguration> {

    @Override
    public boolean isValid(final RuntimeConfiguration runtimeConfiguration, final ConstraintValidatorContext context) {
        try {
            validateVisibleMoviesOptions(runtimeConfiguration);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void initialize(final ValidRuntimeConfiguration constraintAnnotation) {
        //initialize validator
    }
}
