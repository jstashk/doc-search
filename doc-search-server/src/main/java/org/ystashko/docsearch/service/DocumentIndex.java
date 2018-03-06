package org.ystashko.docsearch.service;

import org.springframework.stereotype.Service;
import org.ystashko.docsearch.model.Document;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DocumentIndex {

    private static String TOKEN_SEPARATOR = " ";
    /*
     * Key - Content mapping
     */
    private Map<String, String> documents = new ConcurrentHashMap<>();

    /*
     * Token - Containing documents key mapping
     */
    private Map<String, Set<String>> invertIndex = new ConcurrentHashMap<>();

    public void putDocuments(List<Document> newDocuments) {
        Map<String, String> flatDocuments = newDocuments.stream()
                .collect(Collectors.toMap(document -> document.getKey(), document -> document.getContent()));
        documents.putAll(flatDocuments);
        rebuildIndex(flatDocuments);
    }

    private void rebuildIndex(Map<String,String> newDocuments) {
        newDocuments.entrySet().forEach(document -> {
            String documentKey = document.getKey();
            Stream.of(document.getValue().replaceAll("[^\\w\\s]","")
                    .split(TOKEN_SEPARATOR))
                    .map(String::toLowerCase)
                    .forEach(token -> {
                        if (invertIndex.containsKey(token)) {
                            invertIndex.get(token).add(documentKey);
                        } else {
                            Set<String> documents = new HashSet<>();
                            documents.add(documentKey);
                            invertIndex.put(token, documents);
                        }
                     });
        });
    }

    public List<Document> getDocuments(List<String> keys) {
        List<Document> result = keys.stream()
                .filter(key -> documents.containsKey(key))
                .map(key -> new Document(key, documents.get(key)))
                .collect(Collectors.toList());
        return new ArrayList<>(result);
    }

    public List<String> findDocumentsContaining(List<String> tokens) {
        tokens = tokens.stream().map(String::toLowerCase).collect(Collectors.toList());
        if(!isAllTokensIndexed(tokens)) {
            return new ArrayList<>();
        }
        Set<String> results = new HashSet(invertIndex.get(tokens.get(0)));
        tokens.forEach(token -> {
            results.retainAll(invertIndex.get(token));
        });
        return new ArrayList(results);
    }

    private boolean isAllTokensIndexed(List<String> tokens) {
        return !tokens.stream().filter(token -> !invertIndex.containsKey(token)).findFirst().isPresent();
    }


}
