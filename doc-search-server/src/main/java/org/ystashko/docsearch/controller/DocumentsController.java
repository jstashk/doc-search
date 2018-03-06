package org.ystashko.docsearch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.ystashko.docsearch.model.ResponseWrapper;
import org.ystashko.docsearch.service.DocumentIndex;
import org.ystashko.docsearch.model.Document;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentsController {

    @Autowired
    DocumentIndex documentIndex;

    @GetMapping
    public ResponseWrapper<Document> getDocumentsByKeys(@RequestParam List<String> keys){
        return new ResponseWrapper<>(documentIndex.getDocuments(keys));
    }

    @PutMapping
    public void putDocument(@RequestBody List<Document> documents){
        documentIndex.putDocuments(documents);
    }

    @GetMapping("/search")
    public ResponseWrapper<String> searchForDocuments(@RequestParam List<String> tokens) {
        return new ResponseWrapper<>(documentIndex.findDocumentsContaining(tokens));
    }

}
