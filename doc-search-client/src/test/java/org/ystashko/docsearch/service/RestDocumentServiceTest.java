package org.ystashko.docsearch.service;

import com.github.restdriver.clientdriver.ClientDriverRequest;
import com.github.restdriver.clientdriver.ClientDriverRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import org.ystashko.docsearch.model.Document;

import java.util.Arrays;
import java.util.Collections;

import static com.github.restdriver.clientdriver.RestClientDriver.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class RestDocumentServiceTest {

    private static final String DOCUMENTS = "[{\"key\":\"doc1\",\"content\":\"A Couple of words\"}]";
    private static final String DOCUMENTS_RESPONSE = "{\"result\":[{\"key\":\"doc1\",\"content\":\"A Couple of words\"}]}";
    private static final String KEYS_RESPONSE = "{\"result\":[\"doc1\"]}";

    @Rule
    public ClientDriverRule driver = new ClientDriverRule();

    private DocumentService documentService;

    @Before
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        documentService = new RestDocumentService(driver.getBaseUrl(), restTemplate);
    }

    @Test
    public void shouldAddDocument() {
        driver.addExpectation(onRequestTo("/documents")
                .withBody(DOCUMENTS, APPLICATION_JSON_VALUE)
                .withMethod(ClientDriverRequest.Method.PUT), giveEmptyResponse().withStatus(200));
        documentService.addDocuments(Arrays.asList(new Document("doc1", "A Couple of words")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfEmptyDocuments() {
        documentService.addDocuments(Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNullDocuments() {
        documentService.addDocuments(null);
    }

    @Test
    public void shouldGetDocumentsByKey() {
        driver.addExpectation(onRequestTo("/documents")
                .withParam("keys", "doc1,doc2")
                .withMethod(ClientDriverRequest.Method.GET), giveResponse(DOCUMENTS_RESPONSE, APPLICATION_JSON_VALUE).withStatus(200));
        documentService.getDocumentsByKey(Arrays.asList("doc1,doc2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfEmptyKeys() {
        documentService.getDocumentsByKey(Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfNullKeys() {
        documentService.getDocumentsByKey(null);
    }

    @Test
    public void shouldFindDocumentKeys() {
        driver.addExpectation(onRequestTo("/documents/search")
                .withParam("tokens", "A,words,of")
                .withMethod(ClientDriverRequest.Method.GET), giveResponse(KEYS_RESPONSE, APPLICATION_JSON_VALUE).withStatus(200));
        documentService.findDocuments("A, words of");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfTokensIsEmpty() {
        documentService.findDocuments("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfTokensIsNull() {
        documentService.findDocuments(null);
    }


}
