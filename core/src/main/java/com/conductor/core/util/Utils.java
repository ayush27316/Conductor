package com.conductor.core.util;

import com.conductor.core.model.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

@Component
@Slf4j
public class Utils {


    /**
     * A generic helper that applies a value to a setter only if the value is not null.
     * @param setter A method reference to the setter (e.g., event::setName)
     * @param value The value to potentially set
     */
    public static  <T> void updateIfNotNull(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * A generic helper for collections that updates the property only if the collection is not null or empty.
     */
    public static <T extends Collection<?>> void updateIfNotEmpty(Consumer<T> setter, T value) {
        if (value != null && !value.isEmpty()) {
            setter.accept(value);
        }
    }

    /**
     * Checks if an Object is null
     * @param ob object that needs to be verified against
     * @param message This message is included in the exception thrown at runtime
     *                when {@code ob} is null
     * @throws IllegalArgumentException
     */
    public static void notNull(Object ob, String message)
    {
        if(Objects.isNull(ob)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void doIfNotNull(Object ob, Runnable doWork){
        if(!Objects.isNull(ob)){
            doWork.run();
        }
    }

    public static void doIfNotNull(Runnable doWork){
        if(!Objects.isNull(doWork)){
            doWork.run();
        }
    }

    /**
     * Logs a warning if any of the given resources are transient (i.e., their ID is null).
     * Also includes the name of the calling method for traceability.
     *
     * @param resources one or more resources to check
     */
    public static void warnIfTransient(Resource... resources) {
        if (resources == null || resources.length == 0) {
            return;
        }

        // Capture the caller method (one frame up from here)
        String caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames
                        .skip(1) // skip this method
                        .findFirst()
                        .map(f -> f.getClassName() + "#" + f.getMethodName())
                        .orElse("Unknown"));

        for (Resource resource : resources) {
            if (resource != null && Objects.isNull(resource.getId())) {
                log.warn("Transient entity detected of type {} in caller {}. " +
                                "This may cause undefined behaviour.",
                        resource.getResourceType().getName(),
                        caller);
            }
        }
    }

    public static void throwIfAnyFalse(RuntimeException e, boolean... conditions) {
        for (boolean condition : conditions) {
            if (!condition) {
                throw e;
            }
        }
    }

    public static void throwIfFalse(RuntimeException e, boolean condition)
    {
        if(!condition) {
            throw e;
        }
    }

}
