package com.ajk;

import clojure.lang.IFn;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static clojure.java.api.Clojure.read;
import static clojure.lang.RT.loadResourceScript;
import static clojure.lang.RT.var;

public final class Transform {

    private final IFn xmlToJson;

    public Transform() throws IOException {
        // load libraries used by the transform script
        final IFn require = var("clojure.core", "require");
        require.invoke(read("clojure.xml"));
        require.invoke(read("clojure.zip"));
        require.invoke(read("clojure.data.json"));
        require.invoke(read("clojure.data.zip.xml"));

        loadResourceScript("com/ajk/transform.clj");
        xmlToJson = var("transform", "xml-to-json");
    }

    public String xmlToJson(final String xml) {
        return (String) xmlToJson.invoke(new ByteArrayInputStream(xml.getBytes()));
    }

}
