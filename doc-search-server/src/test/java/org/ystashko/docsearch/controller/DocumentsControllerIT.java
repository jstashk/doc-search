package org.ystashko.docsearch.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.ystashko.docsearch.model.Document;
import org.ystashko.docsearch.model.ResponseWrapper;
import org.ystashko.docsearch.service.DocumentIndex;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class DocumentsControllerIT {

    @LocalServerPort
    private int port;
    private URL url;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private DocumentIndex documentIndex;


    @Before
    public void setUp() throws MalformedURLException {
        this.url = new URL("http://localhost:" + port + "/documents");
    }

    @Test
    public void shouldGetEmptyListIfDocumentNotPresent() throws URISyntaxException {
        ResponseEntity<ResponseWrapper<Document>> response = testRestTemplate
                .exchange(new URI(url.toString().concat("?keys=1")), HttpMethod.GET, null, new ParameterizedTypeReference<ResponseWrapper<Document>>(){});
        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody().getResult(), empty());
    }

    @Test
    public void shouldGetDocumentsFromIndex() throws URISyntaxException {
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was");
        fillIndex(doc1, doc2);
        ResponseEntity<ResponseWrapper<Document>> response = testRestTemplate
                .exchange(new URI(url.toString().concat("?keys=doc1,doc2")), HttpMethod.GET, null, new ParameterizedTypeReference<ResponseWrapper<Document>>(){});
        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody().getResult(), containsInAnyOrder(doc1, doc2));
    }

    @Test
    public void shouldPutNewDocumentsToIndex() {
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was");
        testRestTemplate.put(url.toString(), Arrays.asList(doc1, doc2));
        assertThat(documentIndex.getDocuments(Arrays.asList("doc1", "doc2")), containsInAnyOrder(doc1, doc2));
    }

    @Test
    public void shouldSearchForDocuments() throws URISyntaxException {
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was");
        Document doc3 = new Document("doc3", "Just Marry was");
        fillIndex(doc1, doc2, doc3);
        ResponseEntity<ResponseWrapper<String>> response = testRestTemplate
                .exchange(new URI(url.toString().concat("/search?tokens=just,Marry")), HttpMethod.GET, null, new ParameterizedTypeReference<ResponseWrapper<String>>(){});
        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody().getResult(), equalTo(Arrays.asList("doc3")));
    }

    @Test
    public void shouldReturnEmptyListIfNothingFound() throws URISyntaxException {
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was");
        Document doc3 = new Document("doc3", "Just Marry was");
        fillIndex(doc1, doc2, doc3);
        ResponseEntity<ResponseWrapper<String>> response = testRestTemplate
                .exchange(new URI(url.toString().concat("/search?tokens=just,Marry,blood")), HttpMethod.GET, null, new ParameterizedTypeReference<ResponseWrapper<String>>(){});
        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody().getResult(), empty());
    }

    private void fillIndex(Document... doc) {
        documentIndex.putDocuments(Arrays.stream(doc).collect(Collectors.toList()));
    }


}
