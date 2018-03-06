package org.ystashko.docsearch.service;

import com.google.common.base.Strings;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.ystashko.docsearch.model.Document;
import org.ystashko.docsearch.model.ResponseWrapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.isNull;

public class RestDocumentService implements DocumentService {

    private static final String DOCUMENTS_PATH = "/documents";
    private static final String DOCUMENTS_PATH_GET = "/documents?keys={parameters}";
    private static final String DOCUMENTS_SERACH_PATH = "/documents/search?tokens={tokens}";
    private RestTemplate restTemplate;
    private String documentsGetEndpoint;
    private String documentsPutEndpoint;
    private String documentsSearchEndpoint;

    public RestDocumentService(String serverLocation, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.documentsPutEndpoint = serverLocation + DOCUMENTS_PATH;
        this.documentsGetEndpoint = serverLocation + DOCUMENTS_PATH_GET;
        this.documentsSearchEndpoint = serverLocation + DOCUMENTS_SERACH_PATH;
    }

    @Override
    public List<Document> getDocumentsByKey(List<String> keys) {
        checkArgument(!CollectionUtils.isEmpty(keys), "Keys should not be empty.");
        String flatKeys = keys.stream().collect(Collectors.joining(","));
        ResponseEntity<ResponseWrapper<Document>> serverResponse =
                restTemplate.exchange(documentsGetEndpoint, HttpMethod.GET,
                         null, new ParameterizedTypeReference<ResponseWrapper<Document>>(){}, flatKeys);
        return isNull(serverResponse.getBody()) ? Collections.emptyList() : serverResponse.getBody().getResult();
    }

    @Override
    public void addDocuments(List<Document> documents) {
        checkArgument(!CollectionUtils.isEmpty(documents), "Documents should not be empty.");
        restTemplate.put(documentsPutEndpoint, documents);
    }

    @Override
    public List<String> findDocuments(String query) {
        checkArgument(!Strings.isNullOrEmpty(query), "Query string should not be empty.");
        String tokens = Stream.of(query.replaceAll("[^\\w\\s]","")
                .split(" ")).collect(Collectors.joining(","));
        ResponseEntity<ResponseWrapper<String>> serverResponse =
                restTemplate.exchange(documentsSearchEndpoint, HttpMethod.GET,
                        null, new ParameterizedTypeReference<ResponseWrapper<String>>(){}, tokens);
        return isNull(serverResponse.getBody()) ? Collections.emptyList() : serverResponse.getBody().getResult();
    }
}
