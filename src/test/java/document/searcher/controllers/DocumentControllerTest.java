package document.searcher.controllers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import document.searcher.dao.DocumentDao;
import document.searcher.model.Document;
import document.searcher.service.SolrService;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by: Maksym Goroshkevych
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(value ={"file:src/main/webapp/WEB-INF/appServlet/servlet-context.xml",
        "file:src/main/webapp/WEB-INF/applicationContext.xml"})
public class DocumentControllerTest
{
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private SolrService solrService;
    private DocumentDao documentDao;
    private DocumentController documentController;

    @Before
    public void setup() {
        solrService = mock(SolrService.class);
        documentDao = mock(DocumentDao.class);
        documentController = new DocumentController();
        ReflectionTestUtils.setField(documentController,"solrService", solrService);
        ReflectionTestUtils.setField(documentController,"documentDao", documentDao);
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    //integration test
    @Test
    public void testAddDocumentsAll() throws Exception {
        mockMvc.perform(get("/document/addAll").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    //simple mockito test
    @Test
    public void testGetDocuments(){
        List<Document> documents = new ArrayList<Document>();
        Document doc = getDocument();
        documents.add(doc);
        when(solrService.getDocuments()).thenReturn(documents);
        ResponseEntity<List<Document>> response = documentController.getDocuments();
        assertEquals(response.getBody().get(0).getId(), "testId");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        when(solrService.getDocuments()).thenReturn(new ArrayList<Document>());
        response = documentController.getDocuments();
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testIndexDocumentsAll()
    {
        List<Document> documents = new ArrayList<Document>();
        Document doc = getDocument();
        documents.add(doc);
        when(documentDao.getDocuments()).thenReturn(documents);
        when(solrService.add(documents)).thenReturn(200);
        ResponseEntity<Document> response = documentController.indexDocumentsAll();
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        when(solrService.add(documents)).thenReturn(400);
        response = documentController.indexDocumentsAll();
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testIndexDocument()
    {
        Document doc = getDocument();
        when(documentDao.getDocument("testId")).thenReturn(doc);
        when(solrService.add(doc)).thenReturn(200);
        ResponseEntity<Document> response = documentController.indexDocument("testId");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testSearchDocument()
    {
        Document doc = getDocument();
        when(solrService.getDocument("testId")).thenReturn(doc);
        ResponseEntity<Document> response = documentController.searchDocument("testId");
        assertEquals(response.getBody().getId(), "testId");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        when(solrService.getDocuments()).thenReturn(null);
        response = documentController.searchDocument(null);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testAddDocument()
    {
        ResponseEntity<Document> response = documentController.addDocument("test_document1.txt");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    private Document getDocument()
    {
        Document doc = new Document();
        doc.setId("testId");
        return doc;
    }
}
