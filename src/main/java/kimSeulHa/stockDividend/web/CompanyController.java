package kimSeulHa.stockDividend.web;

import kimSeulHa.stockDividend.persist.entity.CompanyEntity;
import kimSeulHa.stockDividend.service.CompanyService;
import lombok.AllArgsConstructor;
import kimSeulHa.stockDividend.model.Company;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/company") //**블로그 정리
@AllArgsConstructor
public class CompanyController {
    private final CompanyService companyService;
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword){
        //List<String> autocompletes = this.companyService.autocomplete(keyword);
        List<String> autocompletes = this.companyService.getCompanyNamesByKeyword(keyword);
        return ResponseEntity.ok(autocompletes);
    }
    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable){
        Page<CompanyEntity> companyEntityList = this.companyService.getCompanyListAll(pageable);
        return ResponseEntity.ok(companyEntityList);
    }
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request){

        String ticker = request.getTicker().trim();
        if(ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("ticker is empty");
        }
        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());

        return ResponseEntity.ok(company);
    }
    @DeleteMapping
    public ResponseEntity<?> deleteCompany(){
        return null;
    }
}
