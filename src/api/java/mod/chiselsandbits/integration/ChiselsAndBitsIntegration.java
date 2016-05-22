package mod.chiselsandbits.integration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.RUNTIME )
public @interface ChiselsAndBitsIntegration
{

	String value();

}
