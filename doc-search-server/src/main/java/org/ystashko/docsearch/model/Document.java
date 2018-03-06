package org.ystashko.docsearch.model;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Document {

    @NotNull
    private String key;
    @NotNull
    private String content;

    public Document() {
    }

    public Document(String key, String content) {
        this.key = key;
        this.content = content;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Document)) return false;
        Document document = (Document) o;
        return Objects.equals(key, document.key) &&
                Objects.equals(content, document.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, content);
    }
}
