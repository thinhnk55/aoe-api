package vn.vietdefi.bank;

import vn.vietdefi.bank.services.BankService;
import vn.vietdefi.bank.services.IBankService;

public class ApiBank {
    public static IBankService bankService = new BankService();
}
