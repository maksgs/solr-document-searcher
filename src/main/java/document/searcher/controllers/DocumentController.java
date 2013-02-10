package document.searcher.controllers;

import document.searcher.dao.DocumentDao;
import document.searcher.model.Document;
import document.searcher.service.SolrService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.persistence.NoResultException;
import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created by: Maksym Goroshkevych
 */
@Controller
public class DocumentController
{
    private static final Log log = LogFactory.getLog(DocumentController.class);

    @Autowired
    private DocumentDao documentDao;

    private static final URL outputFolderPath = Thread.currentThread().getContextClassLoader().getResource("TEMP");

    @Autowired
    private SolrService solrService;

    /**
     * this service add all files from srs/main/resources/TEMP folder to DB
     */
    @RequestMapping (value = "/document/addAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Document> addDocumentsAll()
    {
        HttpHeaders headers = new HttpHeaders();
        File folder = new File(outputFolderPath.getPath());
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null)
        {
            log.warn("There are no files in temp folder");
            return new ResponseEntity<Document>(headers, HttpStatus.NOT_FOUND);
        }
        for (File file: listOfFiles)
        {
            documentDao.saveDocument(file);
        }

        return new ResponseEntity<Document>(headers, HttpStatus.OK);
    }

    /**
     * this service index all documents from DB to solr
     */
    @RequestMapping (value = "/document/indexAll", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Document> indexDocumentsAll()
    {
        HttpHeaders headers = new HttpHeaders();
        List<Document> docList = documentDao.getDocuments();
        int response = solrService.add(docList);
        if (response == 400){
            return new ResponseEntity<Document>(headers, HttpStatus.BAD_REQUEST);
        }
        log.debug("Document list was indexed" );
        return new ResponseEntity<Document>(headers, HttpStatus.OK);
    }

    /**
     * returns all documents that are indexed in solr, returns 404 if documents were not found
     */
    @RequestMapping (value = "/documents", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<List<Document>> getDocuments()
    {
        HttpHeaders headers = new HttpHeaders();
        List<Document> docList = solrService.getDocuments();
        if (docList.isEmpty())
        {
            return new ResponseEntity<List<Document>>(headers, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Document>>(docList, headers, HttpStatus.OK);
    }

    /**
     * return document by id that is stored in solr, returns 404 if document was not found
     */
    @ExceptionHandler({NoResultException.class})
    @RequestMapping (value = "/document/{id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Document> searchDocument(@PathVariable String id)
    {
        HttpHeaders headers = new HttpHeaders();
        Document doc = solrService.getDocument(id);
        if (doc == null){
            return new ResponseEntity<Document>(headers, HttpStatus.NOT_FOUND);
        }
        log.debug(String.format("Document id = %s was found", id));
        return new ResponseEntity<Document>(doc, headers, HttpStatus.OK);
    }

    /**
     * this service index document with id from DB to solr,
     * returns 404 if document is not found in DB
     */
    @RequestMapping (value = "/document/index/{id}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Document> indexDocument(@PathVariable String id)
    {
        HttpHeaders headers = new HttpHeaders();
        Document doc = documentDao.getDocument(id);
        if (doc == null)
            return new ResponseEntity<Document>(headers, HttpStatus.NOT_FOUND);
        int status = solrService.add(doc);
        if (status == 400){
            return new ResponseEntity<Document>(headers, HttpStatus.BAD_REQUEST);
        }
        log.debug(String.format("Document with id = %s was indexed", id));
        return new ResponseEntity<Document>(headers, HttpStatus.OK);
    }

    /**
     * this service add file with name "file_name"
     * from srs.main/resources/TEMP folder to DB, returns 404 if file was not found
     */
    @RequestMapping (value = "/document/add", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<Document> addDocument(@RequestParam String name)
    {
        HttpHeaders headers = new HttpHeaders();
        File file = new File(outputFolderPath.getPath(), name);
        if (!file.exists())
        {
            return new ResponseEntity<Document>(headers, HttpStatus.NOT_FOUND);
        }
        documentDao.saveDocument(file);
        return new ResponseEntity<Document>(headers, HttpStatus.OK);
    }
}
