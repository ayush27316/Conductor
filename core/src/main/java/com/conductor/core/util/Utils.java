package com.conductor.core.util;

import com.conductor.core.model.common.Resource;
import jakarta.persistence.Column;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.function.Consumer;

@Component
@Slf4j
public class Utils {

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
}
