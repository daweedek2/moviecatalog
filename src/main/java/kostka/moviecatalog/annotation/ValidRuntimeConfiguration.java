package kostka.moviecatalog.annotation;

import kostka.moviecatalog.service.validation.RuntimeConfigurationsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = RuntimeConfigurationsValidator.class)
@Target({ METHOD, CONSTRUCTOR })
@Retention(RUNTIME)
@Documented
public @interface ValidRuntimeConfiguration {
    String message() default "Options are not valid.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
