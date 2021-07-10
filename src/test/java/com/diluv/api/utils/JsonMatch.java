package com.diluv.api.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersionDetector;
import com.networknt.schema.ValidationMessage;

public class JsonMatch extends TypeSafeMatcher<String> {
    private static ObjectMapper MAPPER = new ObjectMapper();

    private final JsonSchema schema;
    private Set<ValidationMessage> errors;

    public JsonMatch (JsonSchema schema) {

        this.schema = schema;
    }

    public static JsonMatch matchesJsonSchemaInClasspath (String schema) {

        try {
            return new JsonMatch(getJsonSchemaFromClasspath(schema));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void describeTo (Description description) {

        if (errors != null) {
            description.appendText("The content to match the given JSON schema.\n");
            List<ValidationMessage> messages = new ArrayList<>(errors);
            if (!messages.isEmpty()) {
                for (final ValidationMessage message : messages) {
                    description.appendText(message.toString());
                    description.appendText("\n");
                }
            }
        }
    }

    @Override
    protected boolean matchesSafely (String item) {

        try {
            JsonNode tree = JsonMatch.MAPPER.readTree(item);
            this.errors = this.schema.validate(tree);
            return this.errors.size() == 0;
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected static JsonSchema getJsonSchemaFromClasspath (String name) throws IOException {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL is = loader.getResource(name);
        if (is != null) {
            JsonNode jsonNode = JsonMatch.MAPPER.readTree(is);

            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersionDetector.detect(jsonNode));
            try {
                return factory.getSchema(is.toURI());
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("File not found " + name);
    }
}
