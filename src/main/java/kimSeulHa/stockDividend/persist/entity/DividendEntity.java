package kimSeulHa.stockDividend.persist.entity;

import ch.qos.logback.classic.db.names.ColumnName;
import kimSeulHa.stockDividend.model.Dividend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "DIVIDEND")
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"companyId","date"}
                )
        }
)
public class DividendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private LocalDateTime date;

    private String dividend;

    public DividendEntity(long companyId, Dividend dividend){
        this.companyId = companyId;
        this.date = dividend.getDate();
        this.dividend = dividend.getDividend();
    }
}
