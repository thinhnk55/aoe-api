package vn.vietdefi.bank.logic;

import vn.vietdefi.bank.logic.timo.TimoApi;

public class BankWorker {
    public final BankAccount account;

    public BankWorker(BankAccount account) {
        this.account = account;
    }

    public void loop() {
        if(account.bank_code == BankCode.TIMO) {
            TimoApi.loop(account);
        }
    }
}
