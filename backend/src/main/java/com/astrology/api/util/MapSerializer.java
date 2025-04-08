package com.astrology.api.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Map;

public class MapSerializer extends JsonSerializer<Map<?, ?>> {
    @Override
    public void serialize(Map<?, ?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        for (Map.Entry<?, ?> entry : value.entrySet()) {
            gen.writeFieldName(entry.getKey().toString());
            if (entry.getValue() instanceof double[]) {
                gen.writeStartArray();
                for (double d : (double[]) entry.getValue()) {
                    gen.writeNumber(d);
                }
                gen.writeEndArray();
            } else {
                gen.writeObject(entry.getValue());
            }
        }
        gen.writeEndObject();
    }
} 