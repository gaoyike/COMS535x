/**
 * Created by chenguanghe on 3/29/15.
 */
public class MyWikiCrawler {
    public static void main(String[] args) {
        String[] topics = {"basketball", "nba"};
        WikiCrawler wikiCrawler = new WikiCrawler("/wiki/basketball", topics, 1000, "MyWikiGraph.txt");
        wikiCrawler.crawl();
        System.out.println("# of Requst: " + wikiCrawler.getRequestToWiki());
        System.out.println("# of edges: " + wikiCrawler.getNumOfEdges());
        System.out.println("Time used:" + wikiCrawler.getElapsedTime());
    }
}
