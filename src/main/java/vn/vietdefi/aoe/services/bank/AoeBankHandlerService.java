package vn.vietdefi.aoe.services.bank;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import sun.applet.AppletIOException;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BankTransaction;
import vn.vietdefi.bank.logic.BankTransactionState;
import vn.vietdefi.bank.services.IBankHandlerService;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.string.StringUtil;

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
            case StarConstant.SERVICE_DONATE_MATCH:
                return donateMatch(transaction, message);
        }
        return false;
    }

    private boolean donateMatch(BankTransaction transaction, AoeBankAction message) {
        JsonObject response = AoeServices.matchService.getInfoMatch(message.receiverId);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return false;
        }
        JsonObject match = response.getAsJsonObject("data");
    }

    private boolean donateGamer(BankTransaction transaction, AoeBankAction message) {
        JsonObject response = AoeServices.gamerService.getById(message.receiverId);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return false;
        }
        JsonObject gamer = response.getAsJsonObject("data");
        //TODO: waiting for aoe_donate_gamer
        return true;

    }

    private boolean starRecharge(BankTransaction transaction, AoeBankAction message) {
        try {
            String username = message.sender;
            JsonObject response = ApiServices.authService.get(username);
            if(!BaseResponse.isSuccessFullMessage(response)){
                String password = StringUtil.generateRandomStringNumberCharacter(8);
                response =  ApiServices.authService.register(username, password, UserConstant.ROLE_USER, UserConstant.STATUS_NORMAL);
                if(!BaseResponse.isSuccessFullMessage(response)){
                    return false;
                }
            }
            long amount = transaction.amount / StarConstant.STAR_PRICE_RATE;
            response = AoeServices.starService.exchangeStar(amount,
                    StarConstant.SERVICE_STAR_RECHARGE,
                    username, transaction.id);
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
