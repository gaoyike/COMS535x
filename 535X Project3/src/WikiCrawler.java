import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chenguanghe on 3/25/15.
 */
public class WikiCrawler {
    static class Page {
        private String url;
        private String source;

        private Page() {
        }

        public Page(String url, String source) {
            this.source = source;
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public String getSource() {
            return source;
        }

        public int hashCode() {
            return url.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof Page)
                return ((Page) obj).url.equals(this.url);
            else
                return false;
        }
    }

    private final String header = "http://en.wikipedia.org";
    private int numOfEdges = 0;
    private int RequestToWiki = 0;
    private String seedUrl;
    private String[] keyWords;
    private int max;
    private Pattern p = Pattern.compile("href=\"(.*?)\"");
    private FileWriter fw;
    private HashSet<Page> visited = new HashSet<Page>();
    private HashSet<String> trash = new HashSet<String>();
    private HashSet<String> robots = new HashSet<String>();
    private Queue<Page> queue = new LinkedList<Page>();
    private double elapsedTime = 0.0;

    private WikiCrawler() {
    }

    public WikiCrawler(String seedUrl, String[] keyWords, int max, String fileName) {
        this.seedUrl = seedUrl;
        this.keyWords = keyWords;
        this.max = max;
        try {
            fw = new FileWriter(new File(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        downloadRobotsTxt();
    }

    public int getRequestToWiki() {
        return RequestToWiki;
    }

    public int getNumOfEdges() {
        return numOfEdges;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void crawl() {
        Timer stop = new Timer();
        try {
            // bfs to get the nodes
            String seedPage = downloadPage(seedUrl).getSource();
            fw.write(String.valueOf(max) + "\n");
            queue.add(new Page(seedUrl, seedPage));
            visited.add(new Page(seedUrl, seedPage));
            while (!queue.isEmpty()) {
                if (visited.size() > max)
                    break;
                Page p = queue.poll();
                Page[] subs = filter(p);
                for (Page page : subs) {
                    if (!visited.contains(page)) {
                        if (visited.size() > max)
                            break;
                        visited.add(page);
                        queue.add(page);
                    }
                }

                System.out.println(visited.size());
            }
            // get the edges
            Iterator<Page> pageIterator = visited.iterator();
            while (pageIterator.hasNext()) {
                Page cur = pageIterator.next();
                String[] strings = extractLinks(cur);
                for (String s : strings) {
                    fw.write(cur.url + " " + s + "\n");
                    numOfEdges++;
                }
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        elapsedTime = stop.elapsedTime();
    }

    private String[] extractLinks(Page page) {
        Matcher matcher = p.matcher(page.getSource());
        LinkedList<String> cur = new LinkedList<String>();
        while (matcher.find()) {
            String s1 = matcher.group(1);
            if (visited.contains(new Page(s1, "")) && !cur.contains(s1) && !s1.equals(page.url)) //dummy page
                cur.add(s1);
        }
        return cur.toArray(new String[cur.size()]);
    }

    private Page[] filter(Page in) {
        Matcher matcher = p.matcher(in.getSource());
        HashSet<Page> cur = new HashSet<Page>();
        while (matcher.find()) {
            String s1 = matcher.group(1);
            if (!visited.contains(new Page("s1",""))&&!trash.contains(s1)&&s1.startsWith("/wiki") && !s1.contains(":") && !s1.contains("#") && !s1.equals(in.url) && !robots.contains(s1) && cur.size() + visited.size() < max) {
                Page page = pageHasKeyWords(s1);
                if (page != null && !cur.contains(page))
                    cur.add(page);
                else
                    trash.add(s1);
            } else {
                trash.add(s1);
            }
        }
        return cur.toArray(new Page[cur.size()]);
    }

    private String buildRawPage(String s) {
        return "/w/index.php?title=" + s.split("/")[2] + "&action=raw";
    }

    private Page pageHasKeyWords(String url) {
        Page page = downloadPage(buildRawPage(url));
        for (String s : keyWords) {
            if (!page.getSource().toLowerCase().contains(s.toLowerCase())) {
                return null;
            }
        }
        return downloadPage(url);
    }

//    private Page pageHasKeyWords(String url) {
//        Page page = downloadPage(url);
//        for (String s : keyWords) {
//            if (!page.getSource().toLowerCase().contains(s.toLowerCase())) {
//                return null;
//            }
//        }
//        return page;
//    }

    private void downloadRobotsTxt() {
        try {
            URL url = new URL("http://en.wikipedia.org/robots.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuffer pageBuffer = new StringBuffer();
            boolean start = false;
            while ((line = reader.readLine()) != null) {
                if ((!start) && line.contains("#----------------------------------------------------------#"))
                    start = true;
                if (start && line.contains("Disallow:"))
                    robots.add(line.replace("Disallow: ", ""));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Page downloadPage(String s) {
        String tmp = s;
        boolean filter = true;
        if (s.contains("&action=raw"))
            filter = false;
        RequestToWiki++;
        s = header + s;
        StopForEach100Requests();
        try {
            URL url = new URL(s);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            StringBuffer pageBuffer = new StringBuffer();
            boolean start = false;
            if (filter)
                while ((line = reader.readLine()) != null) {
                    if (!start && line.contains("<p>"))
                        start = true;
                    if (start)
                        pageBuffer.append(line + "\n");
                }
            else {
                while ((line = reader.readLine()) != null)
                    pageBuffer.append(line + "\n");
            }
            return new Page(tmp, pageBuffer.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void StopForEach100Requests() {
        if ((RequestToWiki % 100) == 0)
            try {
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
