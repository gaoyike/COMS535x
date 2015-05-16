/**
 * Created by chenguanghe on 3/25/15.
 */
public class WikiTennisCrawler {
    public static void main(String[] args) {
        String[] topics = {"tennis", "grand slam"};
        WikiCrawler wikiCrawler = new WikiCrawler("/wiki/tennis", topics, 1000, "WikiTennisGraph.txt");
        wikiCrawler.crawl();
        System.out.println("# of Requst: " + wikiCrawler.getRequestToWiki());
        System.out.println("# of edges: " + wikiCrawler.getNumOfEdges());
        System.out.println("Time used:" + wikiCrawler.getElapsedTime());
    }

}
