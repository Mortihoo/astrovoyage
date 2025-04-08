package com.astrology.api.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class ArraySerializer extends JsonSerializer<double[]> {
    @Override
    public void serialize(double[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (double d : value) {
            gen.writeNumber(d);
        }
        gen.writeEndArray();
    }
} 