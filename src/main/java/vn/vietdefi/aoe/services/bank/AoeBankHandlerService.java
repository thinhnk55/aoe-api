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
                    DebugLogger.error("completeBalanceTransaction {} {}", transaction.id, response);
                    BankServices.bankService.updateBankTransactionState(transaction.id, BankTransactionState.ERROR);
                }
            }
        }
    }

    private JsonObject completeBalanceTransaction(BankTransaction transaction) {
        if(transaction.amount < StarConstant.STAR_PRICE_RATE){
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
        //Chuyen sao vao tai khoan message.sender
        JsonObject response = starRecharge(transaction, message);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return response;
        }
        //Lay thong tin tran dau
        response = AoeServices.matchService.getById(message.receiverId);
        if(!BaseResponse.isSuccessFullMessage(response)){
            BaseResponse.createFullMessageResponse(30, "match_not_found");
        }
        JsonObject match = response.getAsJsonObject("data");
        //Tru sao trong tai khoan message.sender
        long star = transaction.amount / StarConstant.STAR_PRICE_RATE;
        response = AoeServices.starService.exchangeStar(-star,
                StarConstant.SERVICE_DONATE_MATCH,
                message.sender, 0);
        if (!BaseResponse.isSuccessFullMessage(response)) {
            BaseResponse.createFullMessageResponse(31, "exchange_star_failed");
        }
        //Tao giao dich donate
        JsonObject starTransaction = response.getAsJsonObject("data");
        JsonObject data = new JsonObject();
        data.addProperty("user_id", starTransaction.get("user_id").getAsString());
        data.addProperty("user_name", starTransaction.get("user_name").getAsString());
        data.addProperty("nick_name", starTransaction.get("user_name").getAsString());
        data.addProperty("phone_number", starTransaction.get("user_name").getAsString());
        data.addProperty("amount", star);
        data.addProperty("match_id", match.get("id").getAsLong());
        response = AoeServices.donateService.createDonateMatch(data);
        if(!BaseResponse.isSuccessFullMessage(response)){
            BaseResponse.createFullMessageResponse(32, "create_donate_failed");
        }
        JsonObject donate = response.getAsJsonObject("data");
        //Cap nhat lai refer_id cho giao dich tru sao
        AoeServices.starService.updateReferId(starTransaction.get("id").getAsLong(),
                donate.get("id").getAsLong());
        return BaseResponse.createFullMessageResponse(0, "success", donate);
    }

    private JsonObject donateGamer(BankTransaction transaction, AoeBankAction message) {
        //Chuyen sao vao tai khoan message.sender
        JsonObject response = starRecharge(transaction, message);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return response;
        }
        //Lay thong tin gamer
        response = AoeServices.gamerService.getById(message.receiverId);
        if(!BaseResponse.isSuccessFullMessage(response)){
            BaseResponse.createFullMessageResponse(41, "gamer_not_found");
        }
        JsonObject gamer = response.getAsJsonObject("data");
        //Tru sao trong tai khoan message.sender
        long star = transaction.amount / StarConstant.STAR_PRICE_RATE;
        response = AoeServices.starService.exchangeStar(-star,
                StarConstant.SERVICE_DONATE_GAMER,
                message.sender, 0);
        if (!BaseResponse.isSuccessFullMessage(response)) {
            BaseResponse.createFullMessageResponse(31, "exchange_star_failed");
        }
        //Tao giao dich donate
        JsonObject starTransaction = response.getAsJsonObject("data");
        JsonObject data = new JsonObject();
        data.addProperty("user_id", starTransaction.get("user_id").getAsString());
        data.addProperty("user_name", starTransaction.get("user_name").getAsString());
        data.addProperty("nick_name", starTransaction.get("user_name").getAsString());
        data.addProperty("phone_number", starTransaction.get("user_name").getAsString());
        data.addProperty("amount", star);
        data.addProperty("gamer_id", gamer.get("id").getAsLong());
        response = AoeServices.donateService.createDonateGamer(data);
        if(!BaseResponse.isSuccessFullMessage(response)){
            BaseResponse.createFullMessageResponse(32, "create_donate_failed");
        }
        JsonObject donate = response.getAsJsonObject("data");
        //Cap nhat lai refer_id cho giao dich tru sao
        AoeServices.starService.updateReferId(starTransaction.get("id").getAsLong(),
                donate.get("id").getAsLong());
        return BaseResponse.createFullMessageResponse(0, "success", donate);
    }

    private JsonObject starRecharge(BankTransaction transaction, AoeBankAction message) {
        try {
            //Kiem tra user message.sender có ton tai khong
            String username = message.sender;
            JsonObject response = ApiServices.authService.get(username);
            if(!BaseResponse.isSuccessFullMessage(response)){
                //Tao mot tai khoan tuong ung voi mat khau ngau nhien
                String password = StringUtil.generateRandomStringNumberCharacter(8);
                response =  ApiServices.authService.register(username, password, UserConstant.ROLE_USER, UserConstant.STATUS_NORMAL);
                if(!BaseResponse.isSuccessFullMessage(response)){
                    BaseResponse.createFullMessageResponse(20, "create_sender_user_failed");
                }
                long userId = response.get("id").getAsLong();
                //Tao wallet
                response =  AoeServices.starService.getStarWalletByUserId(userId);
                if(!BaseResponse.isSuccessFullMessage(response)){
                    BaseResponse.createFullMessageResponse(21, "create_wallet_failed");
                }
                //Tao profile
                if(!BaseResponse.isSuccessFullMessage(response)){
                    BaseResponse.createFullMessageResponse(22, "create_profile_failed");
                }
            }
            //Nap sao vao tai khoan tuong ung
            long star = transaction.amount / StarConstant.STAR_PRICE_RATE;
            response = AoeServices.starService.exchangeStar(star,
                    StarConstant.SERVICE_STAR_RECHARGE,
                    message.sender, transaction.id);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                BaseResponse.createFullMessageResponse(23, "exchange_star_failed");
            }
            JsonObject data = response.getAsJsonObject("data");
            long starTransactionId = response.getAsJsonObject("data").get("id").getAsLong();
            //Cap nhat lai star_transaction_id trong bank_transaction
            BankServices.bankService.completeBankTransaction(transaction, starTransactionId);
            return BaseResponse.createFullMessageResponse(0,"success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
