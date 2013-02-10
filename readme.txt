Solr Document Searcher
This application can add files from TEMP folder to DB, retrieve files(documents) from DB and add them to solr,
search documents using solr api. Please use API for more details. This project shows basic
features of apache solr api.

Install Guide
    1. Install PostgresSQL
    2. Configure src/main/resources/config.properties (you need to specify user and db password, and db name)
    3. Put solr/solr.war under your tomcat/webapp directory
    4. You need to specify system property solr.solr.home to "solr/solr-data folder". This folder will store solr
    collection. You can do it 2 ways: in your IDE add system property -Dsolr.solr.home={path} or add it in CATALINA OPTS
    5. Run application under your IDEA using tomcat

API
The application has no ui, it uses REST web services:
 - GET - "{host}/document/addAll" - this service add all files from srs/main/resources/TEMP folder to DB
 - GET - "{host}/document/add?name=<file_name>" - this service add file with name "file_name"
    from srs.main/resources/TEMP folder to DB, returns 404 if file was not found
 - GET - "{host}/documents" - returns all documents that are indexed in solr, returns 404 if documents were not found
 - GET - "{host}/document/{}" - return document by id that is stored in solr, returns 404 if document was not found
 - GET - "{host}/document/indexAll" - this service index all documents from DB to solr
 - GET - "{host}/document/index/{id}" - this service index document with id from DB to solr,
    returns 404 if document is not found in DB

Technologies:
- Java
- Spring, Spring MVC, Spring context
- Maven
- Rest
- JDBC
- Apache Solr 4.0
- JSON
- JUNIT
- Mockito
- PostgresSQL

Created by:
    Maksym Goroshkevych
