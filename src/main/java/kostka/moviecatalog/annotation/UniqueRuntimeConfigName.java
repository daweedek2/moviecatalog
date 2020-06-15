package kostka.moviecatalog.annotation;

import kostka.moviecatalog.service.validation.UniqueRuntimeConfigNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = UniqueRuntimeConfigNameValidator.class)
public @interface UniqueRuntimeConfigName {
    String message() default "Runtime configuration name has to be unique.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
