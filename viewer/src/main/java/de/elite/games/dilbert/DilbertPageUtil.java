package de.elite.games.dilbert;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.xsoup.Xsoup;

import java.io.IOException;
import java.time.LocalDate;

class DilbertPageUtil {

    private static final String URL_PREFIX = "https://dilbert.com/strip/";
    private static final String CONTAINER_XPATH = "//*[@class=\"meta-info-container\"]";
    private static final String IMAGE_XPATH = "/section/div[2]/a/img";
    private static final String ATTRIBUTE_WIDTH = "width";
    private static final String ATTRIBUTE_HEIGHT = "height";
    private static final String ATTRIBUTE_SRC = "src";
    private static final String HTTP_PREFIX = "http:";

    private DilbertPageUtil() {

    }

    static ImageInfo retrieveImageInfo(final String urlDateSuffix) {
        String url = URL_PREFIX + urlDateSuffix;
        try {
            Document document = Jsoup.connect(url).get();
            Elements metaInfoContainer = Xsoup.compile(CONTAINER_XPATH).evaluate(document).getElements();
            for (Element element : metaInfoContainer) {
                Elements imageElement = Xsoup.compile(IMAGE_XPATH).evaluate(element).getElements();
                if (!imageElement.toString().isEmpty()) {
                    int width = Integer.parseInt(imageElement.attr(ATTRIBUTE_WIDTH));
                    int height = Integer.parseInt(imageElement.attr(ATTRIBUTE_HEIGHT));
                    String src = HTTP_PREFIX + imageElement.attr(ATTRIBUTE_SRC);
                    return new ImageInfo(src, width, height);
                }
            }
        } catch (IOException | NumberFormatException e) {
            //in case of error return null
        }
        return null;
    }

    static String getUrlDateSuffix(final LocalDate localDate) {
        int year = localDate.getYear();
        int month = localDate.getMonth().getValue();
        int day = localDate.getDayOfMonth();
        return "" + year + "-" +
                (month <= 9 ? "0" : "") + month + "-" +
                (day <= 9 ? "0" : "") + day;
    }
}
