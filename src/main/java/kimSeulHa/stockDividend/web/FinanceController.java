package kimSeulHa.stockDividend.web;

import kimSeulHa.stockDividend.model.ScrapedResult;
import kimSeulHa.stockDividend.service.FinanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/finance") //**블로그 정리
@AllArgsConstructor
public class FinanceController {

    private final FinanceService financeService;
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName){
        ScrapedResult scrapedResult = this.financeService.getDividendsByCompanyNm(companyName);
        return ResponseEntity.ok(scrapedResult);
    }
}
