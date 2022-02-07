import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class HTMLParser {

    private static Logger logger = Logger.getLogger("logger");

    public HTMLParser() {

    }

    public Map<String, Integer> parse(String url) throws IOException {
        logger.info("Started work");
        Document document = Jsoup.connect(url).get();
        String content = document.text().toUpperCase().replaceAll("[^\\p{L} '/.,‑-]", "");
        String[] words = content.split("[,;.?!:\\[\\](){}_*\n\r\t \"‑/-]+");
        logger.fine("Кол-во слов: " + Integer.toString(words.length));
        Map<String, Integer> statistics = new LinkedHashMap<>();
        for (String string : words) {
            if (statistics.containsKey(string)) {
                statistics.put(string, statistics.get(string) + 1);
            } else {
                statistics.put(string, 1);
            }
        }
        logger.fine("Кол-во уникальных слов: " + Integer.toString(statistics.size()));
        logger.info("Finished work");
        return statistics;
    }
}