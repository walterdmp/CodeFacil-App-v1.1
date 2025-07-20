package br.edu.ifsuldeminas.mch.codefacil.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class GlossaryTerm implements Serializable {

    @Exclude
    private String id;
    private String term;
    private String definition;
    private String language;
    private String level;
    private String userId;

    public GlossaryTerm() {
        // Construtor padrão necessário para o Firebase
    }

    public GlossaryTerm(String term, String definition, String language, String level, String userId) {
        this.term = term;
        this.definition = definition;
        this.language = language;
        this.level = level;
        this.userId = userId;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    // **CORREÇÃO: Adicionar o getter público para o userId**
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}