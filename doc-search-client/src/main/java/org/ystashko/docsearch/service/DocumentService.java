package org.ystashko.docsearch.service;

import org.ystashko.docsearch.model.Document;

import java.util.List;

public interface DocumentService {

    List<Document> getDocumentsByKey(List<String> keys);
    void addDocuments(List<Document> documents);
    List<String> findDocuments(String query);

}
