package dev.velora.keyAllReloaded.manager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *      Annotations for commands
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    String name();
    String description() default "";
    String usage() default "";
    String permission() default "";
    String[] aliases() default {};
    String[] tabComplete() default {};
    String tabCompletionMethod() default "";
    boolean async() default false;
}
