package kimSeulHa.stockDividend.persist.entity;

import kimSeulHa.stockDividend.model.Company;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "COMPANY")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String ticker;

    private String name;

    public CompanyEntity(Company company){
        this.ticker = company.getTicker();
        this.name = company.getName();
    }
}
