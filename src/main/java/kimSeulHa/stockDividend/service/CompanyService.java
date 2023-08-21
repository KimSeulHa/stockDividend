package kimSeulHa.stockDividend.service;

import lombok.AllArgsConstructor;
import kimSeulHa.stockDividend.model.Company;
import kimSeulHa.stockDividend.model.ScrapedResult;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import kimSeulHa.stockDividend.persist.CompanyRepository;
import kimSeulHa.stockDividend.persist.DividendRepository;
import kimSeulHa.stockDividend.persist.entity.CompanyEntity;
import kimSeulHa.stockDividend.persist.entity.DividendEntity;
import kimSeulHa.stockDividend.scraper.Scraper;


import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;

    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;

    private final DividendRepository dividendRepository;
    public Company save(String ticker){
        if(this.companyRepository.existsByTicker(ticker)){
            throw new RuntimeException("already exists ticker ->" +ticker);
        }
        return storeCompanyAndDividend(ticker);
    }
    private Company storeCompanyAndDividend(String ticker){
        //1.ticker를 기준으로 회사를 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyNmByTicker(ticker);

        //2-1.없을 경우
        if(ObjectUtils.isEmpty(company)){
            throw new RuntimeException("failed ro scrap ticker -> "+ticker);
        }

        //2-2.존재할 경우, 해당 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult =  this.yahooFinanceScraper.scrap(company);

        //db저장
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

        List<DividendEntity> dividendEntityList =  scrapedResult.getDividendList().stream()
                        .map(d -> new DividendEntity(companyEntity.getId(),d))
                        .collect(Collectors.toList());
        this.dividendRepository.saveAll(dividendEntityList);

        return company;
    }
    public Page<CompanyEntity> getCompanyListAll(Pageable pageable){
        return this.companyRepository.findAll(pageable);
    }
    public void addAutocompleteKeyword(String keyword){
        this.trie.put(keyword,null);
    }
    public List<String> autocomplete(String keyword){
        return (List<String>) this.trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)//최대 열 개만 가져오기
                .collect(Collectors.toList());
    }
    public void deleteAutocomplete(String keyword){
        this.trie.remove(keyword);
    }

    public List<String> getCompanyNamesByKeyword(String keyword){
        Pageable limit = PageRequest.of(0,10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);

        return companyEntities.stream()
                .map(e->e.getName())
                .collect(Collectors.toList());
    }
}

