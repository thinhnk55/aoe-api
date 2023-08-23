package vn.vietdefi.bank;

import vn.vietdefi.bank.services.BankService;
import vn.vietdefi.bank.services.BankTransactionService;
import vn.vietdefi.bank.services.IBankService;
import vn.vietdefi.bank.services.IBankTransactionService;
import vn.vietdefi.bank.services.timo.ITimoService;
import vn.vietdefi.bank.services.timo.TimoService;

public class BankServices {
    public static IBankService bankService = new BankService();
    public static IBankTransactionService bankTransactionService = new BankTransactionService();
    public static ITimoService timoService = new TimoService();
}
