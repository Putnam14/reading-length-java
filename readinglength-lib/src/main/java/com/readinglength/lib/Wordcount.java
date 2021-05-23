package com.readinglength.lib;

public class Wordcount {
    private String id;
    private Integer words;
    private String type;
    private String source;

    public Wordcount(String id, Integer words, String type, String source) {
        this.id = id;
        this.words = words;
        this.type = type;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public Integer getWords() {
        return words;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }
}
