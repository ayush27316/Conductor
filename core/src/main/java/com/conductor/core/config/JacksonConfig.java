package com.conductor.core.config;

import com.conductor.core.model.common.Option;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    @Bean
    public SimpleModule optionAndEnumModule() {
        SimpleModule module = new SimpleModule();

        // Serializer for all enums implementing Option
        module.addSerializer(Option.class, new JsonSerializer<Option>() {
            @Override
            public void serialize(Option value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                // If Option defines getName() use that, otherwise use name()
                gen.writeString(value.getName());
            }
        });

        // Serializer for ALL enums (fallback for non-Option enums)
        module.addSerializer(Enum.class, new JsonSerializer<Enum>() {
            @Override
            public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.name().toLowerCase());
            }
        });

        return module;
    }
}
