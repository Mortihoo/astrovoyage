package com.astrology.api.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class ArrayDeserializer extends JsonDeserializer<double[]> {
    @Override
    public double[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken() == JsonToken.START_ARRAY) {
            double[] array = new double[0];
            int i = 0;
            while (p.nextToken() != JsonToken.END_ARRAY) {
                if (array.length == i) {
                    double[] newArray = new double[array.length + 1];
                    System.arraycopy(array, 0, newArray, 0, array.length);
                    array = newArray;
                }
                array[i++] = p.getDoubleValue();
            }
            return array;
        }
        return new double[0];
    }
} 