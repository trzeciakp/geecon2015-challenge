package pl.allegro.promo.geecon2015.domain;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.allegro.promo.geecon2015.domain.stats.FinancialStatisticsRepository;
import pl.allegro.promo.geecon2015.domain.stats.FinancialStats;
import pl.allegro.promo.geecon2015.domain.transaction.TransactionRepository;
import pl.allegro.promo.geecon2015.domain.transaction.UserTransaction;
import pl.allegro.promo.geecon2015.domain.transaction.UserTransactions;
import pl.allegro.promo.geecon2015.domain.user.User;
import pl.allegro.promo.geecon2015.domain.user.UserRepository;

@Component
public class ReportGenerator {
    
    private final FinancialStatisticsRepository financialStatisticsRepository;
    
    private final UserRepository userRepository;
    
    private final TransactionRepository transactionRepository;

    @Autowired
    public ReportGenerator(FinancialStatisticsRepository financialStatisticsRepository,
                           UserRepository userRepository,
                           TransactionRepository transactionRepository) {
        this.financialStatisticsRepository = financialStatisticsRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public Report generate(ReportRequest request) {
        Report report = new Report();

        FinancialStats uuids = financialStatisticsRepository.listUsersWithMinimalIncome(request.getMinimalIncome(), request.getUsersToCheck());

        for (UUID uuid : uuids) {
            User user = userRepository.detailsOf(uuid);

            UserTransactions userTransactions = transactionRepository.transactionsOf(uuid);
            BigDecimal transactionsAmount = null;
            if (userTransactions != null) {
                transactionsAmount = new BigDecimal(0);

                for (UserTransaction userTransaction : userTransactions.getTransactions()) {
                    transactionsAmount = transactionsAmount.add(userTransaction.getAmount());
                }
            }
            ReportedUser reportedUser = new ReportedUser(uuid, user.getName(), transactionsAmount);
            report.add(reportedUser);

        }
        return report;
    }
    
}
