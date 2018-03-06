package org.ystashko.docsearch.service;

import org.junit.Test;
import org.ystashko.docsearch.model.Document;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DocumentIndexTest {

    @Test
    public void shouldAddToIndexAndReturn() {
        DocumentIndex documentIndex = new DocumentIndex();
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was there");
        Document doc3 = new Document("doc3", "just there");
        documentIndex.putDocuments(Arrays.asList(doc1, doc2, doc3));
        List<Document> documents = documentIndex.getDocuments(Arrays.asList("doc1", "doc2", "doc3"));
        assertEquals(3, documents.size());
        assertTrue(documents.containsAll(Arrays.asList(doc1, doc2, doc3)));
    }

    @Test
    public void shouldReturnRequestedOnly() {
        DocumentIndex documentIndex = new DocumentIndex();
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was there");
        Document doc3 = new Document("doc3", "just there");
        documentIndex.putDocuments(Arrays.asList(doc1, doc2, doc3));
        List<Document> documents = documentIndex.getDocuments(Arrays.asList("doc1"));
        assertEquals(documents, Arrays.asList(doc1));
    }

    @Test
    public void shouldReturnEmptyListIfDocIsAbsent() {
        DocumentIndex documentIndex = new DocumentIndex();
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was there");
        Document doc3 = new Document("doc3", "just there");
        documentIndex.putDocuments(Arrays.asList(doc1, doc2, doc3));
        List<Document> documents = documentIndex.getDocuments(Arrays.asList("doc4"));
        assertEquals(0, documents.size());
    }

    @Test
    public void shouldFindDocsDirectTokenOrder() {
        DocumentIndex documentIndex = new DocumentIndex();
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was there");
        Document doc3 = new Document("doc3", "just there");
        documentIndex.putDocuments(Arrays.asList(doc1, doc2, doc3));
        List<String> keys = documentIndex.findDocumentsContaining(Arrays.asList("marry", "was", "there"));
        assertEquals(2, keys.size());
        assertTrue(keys.containsAll(Arrays.asList("doc1", "doc2")));
    }

    @Test
    public void shouldFindDocsRundomTokenOrder(){
        DocumentIndex documentIndex = new DocumentIndex();
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was there");
        Document doc3 = new Document("doc3", "just there");
        documentIndex.putDocuments(Arrays.asList(doc1, doc2, doc3));
        List<String> keys = documentIndex.findDocumentsContaining(Arrays.asList("THERE", "Marry", "blood", "Was"));
        assertEquals(Arrays.asList("doc2"), keys);
    }

    @Test
    public void shouldReturnEmptyListIfNothingFound() {
        DocumentIndex documentIndex = new DocumentIndex();
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was there");
        Document doc3 = new Document("doc3", "just there");
        documentIndex.putDocuments(Arrays.asList(doc1, doc2, doc3));
        List<String> keys = documentIndex.findDocumentsContaining(Arrays.asList("just", "Marry", "Was", "there"));
        assertEquals(0, keys.size());
    }

    @Test(expected = NullPointerException.class)
    public void shouldThroughExceptionIfPutNull() {
        DocumentIndex documentIndex = new DocumentIndex();
        documentIndex.putDocuments(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThroughExceptionIfGetNull() {
        DocumentIndex documentIndex = new DocumentIndex();
        documentIndex.getDocuments(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThroughExceptionIfFindNull() {
        DocumentIndex documentIndex = new DocumentIndex();
        documentIndex.findDocumentsContaining(null);
    }

}
