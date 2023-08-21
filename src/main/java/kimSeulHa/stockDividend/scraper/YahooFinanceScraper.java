package kimSeulHa.stockDividend.scraper;

import kimSeulHa.stockDividend.model.Company;
import kimSeulHa.stockDividend.model.Dividend;
import kimSeulHa.stockDividend.model.ScrapedResult;
import kimSeulHa.stockDividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Component //bean으로 사용할 예정
public class YahooFinanceScraper implements Scraper{

    private static final String STATIC_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";
    private static final long STATIC_START_TIME = 86400;
    @Override
    public ScrapedResult scrap(Company company){

        ScrapedResult scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000;

            String url = String.format(STATIC_URL,company.getTicker(),STATIC_START_TIME,now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("data-test","historical-prices");
            Element tableEle = parsingDivs.get(0);

            Element tbody = tableEle.children().get(1);
            List<Dividend> dividends = new ArrayList<>();
            for(Element e: tbody.children()){
                String txt= e.text();

                if(!txt.endsWith("Dividend")){
                    continue;
                }
                System.out.println(txt);

                String arr[] = txt.split(" ");
                int month = Month.StrToNumber(arr[0]);
                int day = Integer.parseInt(arr[1].replace(",",""));
                int year = Integer.parseInt(arr[2]);
                String dividend = arr[3];

                if(month > 0){
                    new RuntimeException("month is incorrect >> "+month);
                }
                dividends.add(new Dividend(LocalDateTime.of(year,month,day,0,0),dividend));
            }
            scrapedResult.setDividendList(dividends);

            return scrapedResult;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Company scrapCompanyNmByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        Connection connection = Jsoup.connect(url);
        try {
            Document document = connection.get();
            Element element = document.getElementsByTag("h1").get(0);
            String companyNm = element.text().split("-")[1].trim();

            return new Company(ticker,companyNm);

        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
}
