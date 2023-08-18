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
                response = completeBalanceTransaction(transaction);
                if(!BaseResponse.isSuccessFullMessage(response)){
                    BankServices.bankService
                            .updateBankTransactionState(transaction.id, BankTransactionState.ERROR);
                }
            }
        }
    }

    private JsonObject completeBalanceTransaction(BankTransaction transaction) {
        if(transaction.amount < StarConstant.STAR_PRICE_RATE){
            BankServices.bankService.updateBankTransactionState(transaction.id, BankTransactionState.ERROR);
            return BaseResponse.createFullMessageResponse(10, "invalid_star_amount");
        }
        AoeBankAction message = AoeBankAction.createFromBalanceTransaction(transaction);
        if(message == null){
            return BaseResponse.createFullMessageResponse(11, "extract_message_error");
        }
        switch (message.service){
            case StarConstant.SERVICE_STAR_RECHARGE:
                return starRecharge(transaction, message);
            case StarConstant.SERVICE_DONATE_GAMER:
                return donateGamer(transaction, message);
            case StarConstant.SERVICE_DONATE_MATCH:
                return donateMatch(transaction, message);
        }
        return BaseResponse.createFullMessageResponse(12, "service_not_found");
    }

    private JsonObject donateMatch(BankTransaction transaction, AoeBankAction message) {
        JsonObject response = starRecharge(transaction, message);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return response;
        }
        response = AoeServices.matchService.getById(message.receiverId);
        if(!BaseResponse.isSuccessFullMessage(response)){
            BaseResponse.createFullMessageResponse(30, "match_not_found");
        }
        JsonObject match = response.getAsJsonObject("data");
        long star = transaction.amount / StarConstant.STAR_PRICE_RATE;
        response = AoeServices.starService.exchangeStar(star,
                StarConstant.SERVICE_DONATE_MATCH,
                message.sender, 0);
        if (!BaseResponse.isSuccessFullMessage(response)) {
            BaseResponse.createFullMessageResponse(31, "exchange_star_failed");
        }

        /**
         * CREATE TABLE IF NOT EXISTS aoe_match_donate (
         *     id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
         *     user_id BIGINT  NOT NULL,
         *     user_name VARCHAR(256) NOT NULL,
         *     nick_name VARCHAR(256) NOT NULL,
         *     phone_number VARCHAR(256) NOT NULL,
         *     amount INT  NOT NULL,
         *     match_id BIGINT NOT NULL,
         *     message VARCHAR(256) NOT NULL,
         *     create_time BIGINT  NOT NULL
         * );
         */
        JsonObject starTransaction = response.getAsJsonObject("data");
        JsonObject data = new JsonObject();
        data.addProperty("user_id", starTransaction.get("user_id").getAsString());
        data.addProperty("user_name", starTransaction.get("user_name").getAsString());
        data.addProperty("nick_name", starTransaction.get("user_name").getAsString());
        data.addProperty("phone_number", starTransaction.get("user_name").getAsString());
        data.addProperty("amount", star);
        data.addProperty("match_id", match.get("id").getAsLong());
        AoeServices.donateService.createDonateMatch(data);
    }

    private JsonObject donateGamer(BankTransaction transaction, AoeBankAction message) {
        JsonObject response = AoeServices.gamerService.getById(message.receiverId);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return false;
        }
        JsonObject gamer = response.getAsJsonObject("data");
        //TODO: waiting for aoe_donate_gamer
        return true;

    }

    private JsonObject starRecharge(BankTransaction transaction, AoeBankAction message) {
        try {
            String username = message.sender;
            JsonObject response = ApiServices.authService.get(username);
            if(!BaseResponse.isSuccessFullMessage(response)){
                String password = StringUtil.generateRandomStringNumberCharacter(8);
                response =  ApiServices.authService.register(username, password, UserConstant.ROLE_USER, UserConstant.STATUS_NORMAL);
                if(!BaseResponse.isSuccessFullMessage(response)){
                    BaseResponse.createFullMessageResponse(20, "create_sender_user_failed");
                }
            }
            long star = transaction.amount / StarConstant.STAR_PRICE_RATE;
            response = AoeServices.starService.exchangeStar(star,
                    StarConstant.SERVICE_STAR_RECHARGE,
                    message.sender, transaction.id);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                BaseResponse.createFullMessageResponse(21, "exchange_star_failed");
            }
            JsonObject data = response.getAsJsonObject("data");
            long starTransactionId = response.getAsJsonObject("data").get("id").getAsLong();
            BankServices.bankService.completeBankTransaction(transaction, starTransactionId);
            return BaseResponse.createFullMessageResponse(0,"success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
