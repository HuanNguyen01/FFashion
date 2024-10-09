/**
 * @author Tran Thien Thanh 09/04/1996
 */
package fashion.mock.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fashion.mock.model.Payment;
import fashion.mock.model.TransactionHistory;
import fashion.mock.repository.CheckoutRepository;
import fashion.mock.repository.TransactionHistoryRepository;

@Service
public class CheckoutService {

	private final CheckoutRepository checkoutRepository;
	private final TransactionHistoryRepository transactionHistoryRepository;

	public CheckoutService(CheckoutRepository checkoutRepository,
			TransactionHistoryRepository transactionHistoryRepository) {
		this.checkoutRepository = checkoutRepository;
		this.transactionHistoryRepository = transactionHistoryRepository;
	}

	// Method to retrieve only "Hoạt động" (Active) payments
	public List<Payment> getAllPaymentsAvailable() {
		return checkoutRepository.findByStatus("Hoạt động");
	}

	public Payment findPaymentByMethod(String paymentMethod) {
		return checkoutRepository.findByPaymentMethod(paymentMethod);
	}

	public void saveTransactionHistory(TransactionHistory transactionHistory) {
		transactionHistoryRepository.save(transactionHistory);
	}

}
