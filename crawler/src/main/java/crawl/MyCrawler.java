package crawl;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import model.All;
import model.Visit;

/**
 * Created by skim on 1/31/17.
 */
public class MyCrawler extends WebCrawler {

    private static final String FILTER = ".*(\\.(html|doc|pdf|gif|jpg|png|bmp))$";
    private static final String NFILTER = ".*(\\.(css|js))$";
    private static final String SEED_URL = "http://www.latimes.com/";
    private static int docID = 1;

    /**
     * This method receives two parameters. The first parameter is the page in which we have discovered this new url and the second parameter is the new url.
     * You should implement this function to specify whether the given url should be crawled or not (based on your crawling logic). In this example, we are
     * instructing the crawler to ignore urls that have css, js, git, ... extensions and to only accept urls that start with "http://www.viterbi.usc.edu/". In
     * this case, we didn't need the referringPage parameter to make the decision.
     */
    @Override
    public synchronized boolean shouldVisit(Page referringPage, WebURL url) {
        // All URL
        All.STATUS status = url.getURL().toLowerCase().startsWith(SEED_URL) ? All.STATUS.OK : All.STATUS.N_OK;
        All all = new All(url.getURL(), status);
        //CSVUtil.writeToFile(all);

        String href = url.getURL().toLowerCase();
        return !Pattern.compile(NFILTER).matcher(href).matches() &&
                (Pattern.compile(FILTER).matcher(href).matches()
                || referringPage.getContentType().toLowerCase().startsWith("text/html")
                || referringPage.getContentType().toLowerCase().startsWith("image/")
                || referringPage.getContentType().toLowerCase().startsWith("application/pdf")
                || referringPage.getContentType().toLowerCase().startsWith("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
               && href.startsWith(SEED_URL);
    }

    /**
     * This function is called when a page is fetched and ready to be processed by your program.
     */
    @Override
    public synchronized void visit(Page page) {
        String url = page.getWebURL().getURL();
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            // Successfully downloaded. GOOD
            Visit visit = new Visit(url, html.length(), links.size(), page.getContentType());
            ////CSVUtil.writeToFile(visit);

            try {
                PrintWriter out = new PrintWriter("./crawl_data/" + String.valueOf(docID++) + ".html");
                out.println(html);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
/*
    @Override
    protected synchronized void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        // Attempts to Fetch. GOOD
        Fetch fetch = new Fetch(webUrl.getURL(), statusCode);
        ////CSVUtil.writeToFile(fetch);

        super.handlePageStatusCode(webUrl, statusCode, statusDescription);
    }

    @Override
    protected WebURL handleUrlBeforeProcess(WebURL curURL) {
        return super.handleUrlBeforeProcess(curURL);
    }
*/
}
