package kimSeulHa.stockDividend.scraper;

import kimSeulHa.stockDividend.model.Company;
import kimSeulHa.stockDividend.model.ScrapedResult;

public interface Scraper {
    ScrapedResult scrap(Company company);
    Company scrapCompanyNmByTicker(String ticker);
}
