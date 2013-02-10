package document.searcher.scheduler;



import document.searcher.service.SolrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * This service will optimize solr index daily at midnight
 * Created by: Maksym Goroshkevych
 */

@Service
public class OptimizationScheduler {

    private static Logger logger = LoggerFactory.getLogger(OptimizationScheduler.class);

    @Autowired
    private SolrService solrService;

    @Scheduled(cron="0 0 0 * * ?")
    public void doSchedule(){
        try {
            logger.debug("Daily solr index optimization started");
            solrService.optimize();
            logger.debug("Daily solr index optimization completed");
        }catch (Exception ex) {
            logger.warn("Daily solr index optimization fails");
        }
    }

}
