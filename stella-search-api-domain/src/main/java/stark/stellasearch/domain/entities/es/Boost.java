package stark.stellasearch.domain.entities.es;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
public @interface Boost
{
    float value() default 1.0f;
}
