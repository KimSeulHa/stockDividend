package kimSeulHa.stockDividend.service;

import kimSeulHa.stockDividend.exception.impl.NoCompanyException;
import kimSeulHa.stockDividend.model.Company;
import kimSeulHa.stockDividend.model.Dividend;
import kimSeulHa.stockDividend.model.ScrapedResult;
import kimSeulHa.stockDividend.model.constants.CacheKey;
import kimSeulHa.stockDividend.persist.CompanyRepository;
import kimSeulHa.stockDividend.persist.DividendRepository;
import kimSeulHa.stockDividend.persist.entity.CompanyEntity;
import kimSeulHa.stockDividend.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    @Cacheable(key="#companyNm",value= CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendsByCompanyNm(String companyNm){
        log.info("getDividendsByCompanyNm is started");

        //1.회사명으로 회사 Entity 가져오기 및 존재여부 확인
        CompanyEntity companyEntity = this.companyRepository.findByName(companyNm)
                                                        .orElseThrow(()-> new NoCompanyException());

        //2.회사 Entity로 배당금 조회
        List<DividendEntity> dividendEntityList = this.dividendRepository.findAllByCompanyId(companyEntity.getId());

        List<Dividend> dividends =  dividendEntityList.stream()
                                        .map(e-> new Dividend(e.getDate(),e.getDividend()))
                                                .collect(Collectors.toList());


        return new ScrapedResult(new Company(companyEntity.getTicker(),companyEntity.getName())
                                ,dividends);
    }
}
