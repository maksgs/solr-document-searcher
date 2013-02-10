package document.searcher.dao;

import document.searcher.model.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by: Maksym Goroshkevych
 */
@Repository
@Transactional
public class DocumentDao
{
    private static final String DOCUMENT_DESCRIPTION = "this file is located in project temp folder...";

    private static final Log log = LogFactory.getLog(DocumentDao.class);

    private static final String SELECT_ALL_DOCUMENTS = "select * from documents";
    private static final String SELECT_DOCUMENTS_BY_NAME = "select * from documents where id = ?";
    private static final String DOCUMENT_COUNT = "select count(0) from documents where name = ?";

    private SimpleJdbcInsert insertDocument;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.insertDocument = new SimpleJdbcInsert(dataSource).withTableName("documents");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void saveDocument(File file)    {
        //get file author (works only in java7)
        Path path = Paths.get(file.getPath());
        String author;
        try{
            author = Files.getOwner(path).getName();
        }catch (IOException ex){
            author = "anonymous";
        }
        //get file extension
        int pos = file.getName().lastIndexOf('.');
        String fileExt = file.getName().substring(pos + 1);

        Document document = new Document();
        document.setId(UUID.randomUUID().toString());
        document.setAuthor(author);
        document.setDescription(DOCUMENT_DESCRIPTION);
        document.setFilePath(file.getAbsolutePath());
        document.setType(fileExt);
        document.setName(file.getName());
        document.setCreatedDate(new Date());


        //check if document already added
        if (!documentExist(file.getName()))
        {
            //save document
            insertDocument.execute(new BeanPropertySqlParameterSource(document));
            log.debug(String.format("Document %s is saved to DB", file.getName()));
        }else
        {
            log.warn(String.format("Document %s is added already!", file.getName()));
        }
    }

    public List<Document> getDocuments()
    {
        List<Document> documentList = jdbcTemplate.query(SELECT_ALL_DOCUMENTS,
                new BeanPropertyRowMapper<Document>(Document.class));
        log.debug(String.format("Documents were found: %s", documentList.size()));
        return documentList;
    }

    public Document getDocument(String id)
    {
        List<Document> documentList = jdbcTemplate.query(SELECT_DOCUMENTS_BY_NAME,
                new BeanPropertyRowMapper<Document>(Document.class), id);
        if (!documentList.isEmpty()){
            log.debug(String.format("Document was found, id = %s", documentList.get(0).getId().toString()));
            return documentList.get(0);
        }
        return null;
    }

    private Boolean documentExist(String documentName)
    {
        return jdbcTemplate.queryForInt(DOCUMENT_COUNT, documentName) > 0;
    }
}
