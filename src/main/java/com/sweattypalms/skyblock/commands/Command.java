package com.sweattypalms.skyblock.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String name();

    String[] aliases() default {};

    String description() default "";

    String usage() default "/<command>";

    String permission() default "";

    boolean op() default false;

    boolean inGameOnly() default true;

    boolean runSync() default true;

    String noPerm() default "§cYou must be ADMIN or higher to use that command!";
}
