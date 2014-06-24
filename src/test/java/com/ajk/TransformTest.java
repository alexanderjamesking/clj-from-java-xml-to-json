package com.ajk;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.io.IOException;

public class TransformTest {

    private final Transform transform;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TransformTest() throws IOException {
        transform = new Transform();
    }

    private String loadResource(final String filename) throws IOException {
        return Resources.toString(Resources.getResource(filename), Charsets.UTF_8);
    }

    @Test
    public void transformSportsMLToJson() throws Exception {
        final String xml = loadResource("champions-league.xml");
        final String json = transform.xmlToJson(xml);
        final JsonNode jsonNode = objectMapper.readTree(json);
        assertEquals("UEFA Champions League 2008–09", jsonNode.get("title").asText());
        assertEquals("20080916T000000-0000", jsonNode.get("start-date-time").asText());
        assertEquals("20090527T235959-0000", jsonNode.get("end-date-time").asText());

        System.out.println(json);

        final ArrayNode knockouts = (ArrayNode) jsonNode.get("knockouts");
        assertEquals(3, knockouts.size());
        assertEquals("First knockout stage", knockouts.get(0).get("name").asText());

        final JsonNode firstEvent = knockouts.findValue("events").get(0);
        assertEquals("Atletico Madrid", firstEvent.get("home-team").asText());
    }

}
