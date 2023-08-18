package vn.vietdefi.aoe.services.bank;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BankTransaction;
import vn.vietdefi.bank.logic.BankTransactionState;
import vn.vietdefi.bank.services.IBankHandlerService;
import vn.vietdefi.common.BaseResponse;

public class AoeBankHandlerService implements IBankHandlerService {
    @Override
    public void completeBalanceTransaction() {
        JsonObject response = BankServices.bankService.listWaitingTransaction();
        if(BaseResponse.isSuccessFullMessage(response)){
            JsonArray data = response.getAsJsonArray("data");
            for(int i = 0; i < data.size(); i++){
                JsonObject json = data.get(i).getAsJsonObject();
                BankTransaction transaction = new BankTransaction(json);
                completeBalanceTransaction(transaction);
            }
        }
    }

    private void completeBalanceTransaction(BankTransaction transaction) {
        AoeBankMessage message = AoeBankMessage.createFromBalanceTransaction(transaction);
        if(message == null){
            BankServices.bankService.updateBankState(transaction.id, BankTransactionState.ERROR);
            return;
        }
        switch (message.service){
            case StarConstant.SERVICE_STAR_RECHARGE:
                break;
        }
    }

}
