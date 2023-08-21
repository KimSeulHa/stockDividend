package kimSeulHa.stockDividend.scheduler;

import kimSeulHa.stockDividend.model.*;
import kimSeulHa.stockDividend.model.constants.CacheKey;
import kimSeulHa.stockDividend.persist.*;
import kimSeulHa.stockDividend.persist.entity.CompanyEntity;
import kimSeulHa.stockDividend.persist.entity.DividendEntity;
import kimSeulHa.stockDividend.scraper.YahooFinanceScraper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.context.Theme;

import java.time.LocalDate;
import java.util.List;
@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {

    private final YahooFinanceScraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;

    private final DividendRepository dividendRepository;

    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled()
    public void getScraperScheduler(){
        log.info("getScraperScheduler is started");

        /* 1. DB에 저장된 회사 목록 가져오기 */
        List<CompanyEntity> companyEntities = this.companyRepository.findAll();

        /* 2. 회사마다 새로운 배당금 정보 스크래핑 해오기 */
        for(CompanyEntity companyEntity : companyEntities){
            log.info("company name is..."+companyEntity.getName());

            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(new Company(companyEntity.getTicker(),companyEntity.getName()));

            scrapedResult.getDividendList().stream()
                    .map(e -> new DividendEntity(companyEntity.getId(),e))
                    .forEach(e -> {
                                boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(),e.getDate());
                                //스크래핑 정보 중 없는 값은 새로 저장
                                if(!exists){
                                    dividendRepository.save(e);
                                    log.info("new dividend is saved");
                                }
                            });

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }



    }
}
