package org.ystashko.docsearch.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.ystashko.docsearch.model.Document;
import org.ystashko.docsearch.model.ResponseWrapper;
import org.ystashko.docsearch.service.DocumentService;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class DocumentsControllerTest {

    @LocalServerPort
    private int port;
    private URL url;

    @Mock
    DocumentService documentService;

    @Autowired
    @InjectMocks
    DocumentsController documentsController;

    @Autowired
    TestRestTemplate testRestTemplate;

    @Before
    public void setUp() throws MalformedURLException {
        MockitoAnnotations.initMocks(this);
        this.url = new URL("http://localhost:" + port + "/documents");
    }

    @Test
    public void shouldRespondWithDocuments() throws URISyntaxException {
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was");
        when(documentService.getDocumentsByKey(Arrays.asList("doc1", "doc2"))).thenReturn(Arrays.asList(doc1, doc2));
        ResponseEntity<ResponseWrapper<Document>> response = testRestTemplate
                .exchange(new URI(url.toString().concat("?keys=doc1,doc2")), HttpMethod.GET, null,
                        new ParameterizedTypeReference<ResponseWrapper<Document>>(){});
        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody().getResult(), containsInAnyOrder(doc1, doc2));
    }

    @Test
    public void shouldRespondWithBadRequestOnIllegalArgument() throws URISyntaxException {
        when(documentService.getDocumentsByKey(Arrays.asList("1"))).thenThrow(IllegalArgumentException.class);
        ResponseEntity<ResponseWrapper<Document>> response = testRestTemplate
                .exchange(new URI(url.toString().concat("?keys=1")), HttpMethod.GET, null,
                        new ParameterizedTypeReference<ResponseWrapper<Document>>(){});
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    public void shouldSuccessfullyAddNewDocument() {
        doNothing().when(documentService).addDocuments(anyList());
        Document doc1 = new Document("doc1", "Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was");
        testRestTemplate.put(url.toString(), Arrays.asList(doc1, doc2));
        verify(documentService).addDocuments(Arrays.asList(doc1,doc2));
    }

    @Test
    public void shouldFindDocuments() throws URISyntaxException {
        Document doc1 = new Document("doc1", "Blood Marry was there");
        Document doc2 = new Document("doc2", "Blood Marry was");
        when(documentService.findDocuments("Blood marry")).thenReturn(Arrays.asList("doc1", "doc2"));
        ResponseEntity<ResponseWrapper<String>> response = testRestTemplate
                .exchange(new URI(url.toString().concat("/search?query=Blood%20marry")), HttpMethod.GET, null,
                        new ParameterizedTypeReference<ResponseWrapper<String>>(){});
        assertThat(response.getStatusCode(), is(OK));
        assertThat(response.getBody().getResult(), containsInAnyOrder("doc1", "doc2"));
    }


}
