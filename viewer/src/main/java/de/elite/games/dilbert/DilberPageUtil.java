package de.elite.games.dilbert;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;

class DilberPageUtil {

    private DilberPageUtil() {

    }

    static ImageInfo retrieveImageInfo(String date) {
        String url = "https://dilbert.com/strip/" + date;
        String xpathContainer = "//*[@class=\"meta-info-container\"]";
        String xpathImage = "/section/div[2]/a/img";

        System.out.println(url);
        try {
            Document document = Jsoup.connect(url).get();
            Elements metaInfoContainer = Xsoup.compile(xpathContainer).evaluate(document).getElements();
            for (Element element : metaInfoContainer) {
                Elements imageElement = Xsoup.compile(xpathImage).evaluate(element).getElements();
                System.out.println("E:" + imageElement);
                if (!"".equals(imageElement.toString())) {
                    int width = Integer.parseInt(imageElement.attr("width"));
                    int height = Integer.parseInt(imageElement.attr("height"));
                    String src = "http:" + imageElement.attr("src");
                    return new ImageInfo(src, width, height);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return null;
    }
}
