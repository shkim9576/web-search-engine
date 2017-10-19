package crawl;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import util.CSVUtil;

public class Controller {

    private static final String CRAWL_STORAGE_FOLDER = "/data/crawl";
    private static final String SEED_URL = "http://www.latimes.com/";
    private static final int NUM_OF_CRAWLERS = 13;
    private static final int MAX_PAGE_TO_FETCH = 20000;
    private static final int MAX_DEPTH_TO_CRAWL = 16;
    private static final int DELAY = 1000;

    public static void main(String[] args) throws Exception {
        CSVUtil.reset();

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(CRAWL_STORAGE_FOLDER);
        config.setMaxPagesToFetch(MAX_PAGE_TO_FETCH);
        config.setMaxDepthOfCrawling(MAX_DEPTH_TO_CRAWL);
        config.setPolitenessDelay(DELAY);
        config.setMaxDownloadSize(Integer.MAX_VALUE);

         /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

         /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */
        //controller.addSeed("http://www.viterbi.usc.edu/");
        controller.addSeed(SEED_URL);

         /*
         * Start the crawl. This is a blocking operation, meaning that your code7
         * will reach the line after this only when crawling is finished.
         */
        controller.start(MyCrawler.class, NUM_OF_CRAWLERS);
    }
}
