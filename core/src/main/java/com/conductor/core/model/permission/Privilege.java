package com.conductor.core.model.permission;

import com.conductor.core.model.Option;
import com.conductor.core.model.Resource;

import java.util.Optional;

/**
 * {@code Privilege} interface is used as a marker interface
 * to reference a group of {@link Option} that represents
 * a privilege to a {@link Resource}
 *
 * Note: Name of options within a privilege must be unique. Otherwise,
 * safeCast will misbehave and might throw {@link ClassCastException}.
 *
 */
public interface Privilege extends Option {

        /**
         * Safely casts a privilege name (string) into the specified {@link Privilege} enum type.
         * <p>
         * This method should ALWAYS be used instead of raw casts or
         * {@code Enum.valueOf()}, as it performs a case-insensitive lookup and
         * guarantees type-safety.
         * <p>
         * If the privilege name does not match any constant in the given enum class,
         * an empty {@link Optional} is returned instead of throwing an exception.
         *
         * <h3>Example Usage</h3>
         * <pre>{@code
         *
         * Optional<OrganizationPrivilege> orgPrivilege =
         *         Privilege.safeCast(OrganizationPrivilege.class, "ADMIN");}
         * </pre>
         *
         * <h3>⚠️ Important Warning</h3>
         * <ul>
         *   <li>NEVER use raw casting like {@code (Privilege) someEnum} or anonymous objects.
         *       It will cause {@link ClassCastException} at runtime.</li>
         *   <li>ALWAYS use {@code safeCast(...)} when converting from a string name
         *       to a {@link Privilege}, especially when reading from external sources
         *       such as JSON or the database.</li>
         *   <li>This method is designed to keep privilege resolution consistent and safe
         *       across the entire codebase.</li>
         * </ul>
         *
         * @param targetPrivilegeClass the target Privilege enum type
         * @param name the string name of the privilege (case-insensitive)
         * @param <E> the specific Privilege enum type
         * @return an Optional containing the resolved enum constant, or empty if no match found
         */
        static <E extends Enum<E> & Privilege> Optional<E> safeCast(Class<E> targetPrivilegeClass, String name) {
            return Option.fromName(targetPrivilegeClass, name);
        }
}


