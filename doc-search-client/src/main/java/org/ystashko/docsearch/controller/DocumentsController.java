package org.ystashko.docsearch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.ystashko.docsearch.model.Document;
import org.ystashko.docsearch.model.ResponseWrapper;
import org.ystashko.docsearch.service.DocumentService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentsController {

    @Autowired
    DocumentService documentService;

    @GetMapping
    public ResponseWrapper<Document> getDocumentsByKeys(@RequestParam List<String> keys){
        return new ResponseWrapper<Document>(documentService.getDocumentsByKey(keys));
    }

    @PutMapping
    public void putDocument(@RequestBody List<Document> documents){
        documentService.addDocuments(documents);
    }

    @GetMapping("/search")
    public ResponseWrapper<String> searchForDocuments(@RequestParam String query) {
        return new ResponseWrapper<>(documentService.findDocuments(query));
    }


    @ExceptionHandler
    void handleIllegalArgumentException(IllegalArgumentException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

}
