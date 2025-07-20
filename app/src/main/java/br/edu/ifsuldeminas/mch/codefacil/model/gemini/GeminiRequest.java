package br.edu.ifsuldeminas.mch.codefacil.model.gemini;

import java.util.Collections;
import java.util.List;

public class GeminiRequest {
    private final List<Content> contents;

    public GeminiRequest(String text) {
        Part part = new Part(text);
        Content content = new Content(Collections.singletonList(part));
        this.contents = Collections.singletonList(content);
    }

    // A classe GenerationConfig foi removida daqui

    static class Content {
        private final List<Part> parts;
        Content(List<Part> parts) { this.parts = parts; }
    }

    static class Part {
        private final String text;
        Part(String text) { this.text = text; }
    }
}