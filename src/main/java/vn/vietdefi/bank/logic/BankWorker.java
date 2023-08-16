package vn.vietdefi.bank.logic;

import vn.vietdefi.util.log.DebugLogger;

import java.util.List;

public class BankWorker {
    private final BankAccount account;

    public BankWorker(BankAccount account) {
        this.account = account;
    }

    public void loop() {
        if (account.bankCode == BankCode.TIMO) {
            List<BalanceTransaction> transaction = TimoApi.update(account);
            if (transaction != null) {
                String refNo = transaction.get(0).getTransactionId();
                String accountNumber = account.accountNumber;
                int row = timoService.updateRefNo(refNo, accountNumber);
                if (row != 0) {
                    updateNewTransaction(transaction);
                } else {
                    DebugLogger.error("Update refNo fail!");
                }
            }
        }
    }

    private void updateNewTransaction(List<BalanceTransaction> update) {
        BankController.instance().bankService.processBalanceTransaction(update);
    }

}
