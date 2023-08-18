package vn.vietdefi.aoe.services.bank;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BankTransaction;
import vn.vietdefi.bank.logic.BankTransactionState;
import vn.vietdefi.bank.services.IBankHandlerService;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;

public class AoeBankHandlerService implements IBankHandlerService {
    @Override
    public void completeBalanceTransaction() {
        JsonObject response = BankServices.bankService.listWaitingTransaction();
        if(BaseResponse.isSuccessFullMessage(response)){
            JsonArray data = response.getAsJsonArray("data");
            for(int i = 0; i < data.size(); i++){
                JsonObject json = data.get(i).getAsJsonObject();
                BankTransaction transaction = new BankTransaction(json);
                boolean result = completeBalanceTransaction(transaction);
                if(!result){
                    BankServices.bankService
                            .updateBankTransactionState(transaction.id, BankTransactionState.ERROR);
                }
            }
        }
    }

    private boolean completeBalanceTransaction(BankTransaction transaction) {
        if(transaction.amount < StarConstant.STAR_PRICE_RATE){
            BankServices.bankService.updateBankTransactionState(transaction.id, BankTransactionState.ERROR);
            return false;
        }
        AoeBankAction message = AoeBankAction.createFromBalanceTransaction(transaction);
        if(message == null){
            BankServices.bankService.updateBankTransactionState(transaction.id, BankTransactionState.ERROR);
            return false;
        }
        switch (message.service){
            case StarConstant.SERVICE_STAR_RECHARGE:
                return starRecharge(transaction, message);
            case StarConstant.SERVICE_DONATE_GAMER:
                return donateGamer(transaction, message);
        }
        return false;
    }

    private boolean donateGamer(BankTransaction transaction, AoeBankAction message) {
        AoeServices.gamerService.get
    }

    private boolean starRecharge(BankTransaction transaction, AoeBankAction message) {
        try {
            long amount = transaction.amount / StarConstant.STAR_PRICE_RATE;
            JsonObject response = AoeServices.starService.exchangeStar(amount,
                    StarConstant.SERVICE_STAR_RECHARGE,
                    message.sender, transaction.id);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return false;
            }
            long starTransactionId = response.getAsJsonObject("data").get("id").getAsLong();
            BankServices.bankService.completeBankTransaction(transaction, starTransactionId);
            return true;
        }catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return false;
        }
    }
}
