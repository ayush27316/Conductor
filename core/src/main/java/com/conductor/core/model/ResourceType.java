    package com.conductor.core.model;

    import java.util.stream.Stream;

    /**
     * Defines all the resources in the system.
     */
    public enum ResourceType implements Option {

        ORGANIZATION("organization"),
        EVENT("event"),
        USER("user"),
        OPERATOR("operator"),
        FORM("form"),
        FILE("file"),
        APPLICATION("application"),
        TICKET("ticket");

        private final String name;

        ResourceType(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        /**
         * Returns a stream of all ResourceType values.
         */
        public static Stream<ResourceType> stream() {
            return Stream.of(values());
        }

    }
