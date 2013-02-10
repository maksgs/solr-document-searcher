package document.searcher.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * Created by: Maksym Goroshkevych
 */
public class Document
{
    @Field
    private String id;

    @Field
    private String name;

    @Field
    private String filePath;

    @Field
    private Date createdDate;

    @Field
    private String type;

    @Field
    private String description;

    @Field
    private String author;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }


    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
