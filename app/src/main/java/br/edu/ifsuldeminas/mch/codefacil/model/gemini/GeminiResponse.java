package br.edu.ifsuldeminas.mch.codefacil.model.gemini;

import java.util.List;

public class GeminiResponse {
    private List<Candidate> candidates;

    public String getResponseText() {
        if (candidates != null && !candidates.isEmpty()) {
            Content content = candidates.get(0).content;
            if (content != null && content.parts != null && !content.parts.isEmpty()) {
                return content.parts.get(0).text;
            }
        }
        return "Não foi possível obter uma resposta.";
    }

    static class Candidate {
        private Content content;
    }

    static class Content {
        private List<Part> parts;
    }

    static class Part {
        private String text;
    }
}