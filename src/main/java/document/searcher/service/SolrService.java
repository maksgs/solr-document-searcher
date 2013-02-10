package document.searcher.service;

import document.searcher.model.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by: Maksym Goroshkevych
 */

@Service
public class SolrService
{
    private static final Log log = LogFactory.getLog(SolrService.class);

    @Autowired
    private SolrServer solrServer;

    public List<Document> getDocuments(){
        SolrQuery query = new SolrQuery();
        //set query
        query.setQuery("*:*");
        query.setStart(0);
        query.setRows(100);
        query.addSortField("name", SolrQuery.ORDER.asc );
        List<Document> docList;
        try {
            QueryResponse rsp = solrServer.query( query );
            docList = rsp.getBeans(Document.class);
        }catch (Exception ex){
            log.warn("Error retrieve solr documents");
            return new ArrayList<Document>();
        }
        return docList;
    }

    public Document getDocument(String id)
    {
        SolrQuery query = new SolrQuery();
        //set query
        query.setQuery("id:" + id);
        List<Document> docList;
        try{
            QueryResponse rsp = solrServer.query( query );
            docList = rsp.getBeans(Document.class);
            if (docList.isEmpty()){
                return null;
            }
        }catch (SolrServerException se){
            log.warn("Error retrieve solr documents");
            return null;
        }
        return docList.get(0);
    }

    public int add(List<Document> docList)
    {
        try{
            solrServer.addBeans(docList);
            UpdateResponse response = solrServer.commit();
            return response.getStatus();
        }catch (Exception ex){
            log.warn("Error commit solr documents");
            return HttpStatus.BAD_REQUEST.value();
        }

    }

    public int add(Document doc)
    {
        List<Document> documents = new ArrayList<Document>();
        documents.add(doc);
        return add(documents);
    }

    public void optimize() throws IOException, SolrServerException {
        solrServer.optimize();
    }
}
