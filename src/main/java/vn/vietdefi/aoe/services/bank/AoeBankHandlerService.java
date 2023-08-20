package vn.vietdefi.aoe.services.bank;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
                AoeBankAction message = AoeBankAction.createFromBalanceTransaction(transaction);
                if (message == null) {
                    DebugLogger.error("extract error id {} note {}", transaction.id, transaction.note);
                    BankServices.bankService.updateBankTransactionState(transaction.id, BankTransactionState.ERROR);
                }else {
                    response = completeBalanceTransaction(transaction, message);
                    if (!BaseResponse.isSuccessFullMessage(response)) {
                        DebugLogger.error("completeBalanceTransaction {} {}", transaction.id, response);
                        BankServices.bankService.updateBankTransactionState(transaction.id, BankTransactionState.ERROR);
                    } else {
                        long targetId = response.getAsJsonObject("data").get("id").getAsLong();
                        BankServices.bankService.doneBankTransactionState(transaction.id, message.service
                                ,targetId);
                    }
                }
            }
        }
    }

    public JsonObject completeBalanceTransaction(BankTransaction transaction, AoeBankAction message) {
        try {
            if (transaction.amount < StarConstant.STAR_PRICE_RATE) {
                return BaseResponse.createFullMessageResponse(10, "invalid_star_amount");
            }
            switch (message.service) {
                case StarConstant.SERVICE_STAR_RECHARGE:
                    return starRecharge(transaction, message);
                case StarConstant.SERVICE_DONATE_MATCH:
                    return donateMatch(transaction, message);
                case StarConstant.SERVICE_DONATE_GAMER:
                    return donateGamer(transaction, message);
                case StarConstant.SERVICE_DONATE_CASTER:
                    return donateCaster(transaction, message);
            }
            return BaseResponse.createFullMessageResponse(12, "service_not_found");
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private JsonObject donateMatch(BankTransaction transaction, AoeBankAction message) {
        //Chuyen sao vao tai khoan message.sender
        JsonObject response = starRecharge(transaction, message);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return response;
        }
        //Lay profile message.sender
        long userId = response.getAsJsonObject("data").get("user_id").getAsLong();
        long star = transaction.amount / StarConstant.STAR_PRICE_RATE;
        return AoeServices.donateService.donate(userId, star,
                StarConstant.SERVICE_DONATE_MATCH, message.receiverId, "");
    }

    private JsonObject donateCaster(BankTransaction transaction, AoeBankAction message) {
        //Chuyen sao vao tai khoan message.sender
        JsonObject response = starRecharge(transaction, message);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return response;
        }
        //Lay profile message.sender
        long userId = response.getAsJsonObject("data").get("user_id").getAsLong();
        long star = transaction.amount / StarConstant.STAR_PRICE_RATE;
        return AoeServices.donateService.donate(userId, star,
                StarConstant.SERVICE_DONATE_CASTER, message.receiverId, "");
    }

    private JsonObject donateGamer(BankTransaction transaction, AoeBankAction message) {
        //Chuyen sao vao tai khoan message.sender
        JsonObject response = starRecharge(transaction, message);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return response;
        }
        //Lay profile message.sender
        long userId = response.getAsJsonObject("data").get("user_id").getAsLong();
        long star = transaction.amount / StarConstant.STAR_PRICE_RATE;
        return AoeServices.donateService.donate(userId, star,
                StarConstant.SERVICE_DONATE_GAMER, message.receiverId, "");
    }

    private JsonObject starRecharge(BankTransaction transaction,
                                    AoeBankAction message) {
        try {
            //Kiem tra user message.sender có ton tai khong
            String username = message.sender;
            JsonObject response = ApiServices.authService.get(username);
            if(!BaseResponse.isSuccessFullMessage(response)){
                //Tao mot tai khoan tuong ung voi mat khau ngau nhien
                String password = StringUtil.generateRandomStringNumberCharacter(8);
                response =  ApiServices.authService.register(username, password, UserConstant.ROLE_USER,
                        UserConstant.STATUS_ACCOUNT_GENERATE);
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
                response =  AoeServices.profileService.getUserProfileByUserId(userId);
                if(!BaseResponse.isSuccessFullMessage(response)){
                    BaseResponse.createFullMessageResponse(22, "create_profile_failed");
                }
            }
            long star_transaction_id = transaction.star_transaction_id;
            if(star_transaction_id == 0){
                long star = transaction.amount / StarConstant.STAR_PRICE_RATE;
                response = AoeServices.starService.exchangeStar(star,
                        StarConstant.SERVICE_STAR_RECHARGE,
                        message.sender, transaction.id);
                if (!BaseResponse.isSuccessFullMessage(response)) {
                    BaseResponse.createFullMessageResponse(23, "exchange_star_failed");
                }
                JsonObject data = response.getAsJsonObject("data");
                star_transaction_id = data.get("id").getAsLong();
                //Cap nhat lai star_transaction_id trong bank_transaction
                BankServices.bankService.updateStarTransactionId(transaction.id, star_transaction_id);
                return BaseResponse.createFullMessageResponse(0,"success", data);
            }else {
                response = AoeServices.starService.getStarTransactionById(star_transaction_id);
                if (!BaseResponse.isSuccessFullMessage(response)) {
                    BaseResponse.createFullMessageResponse(24, "exchange_star_not_found");
                }
                JsonObject data = response.getAsJsonObject("data");
                return BaseResponse.createFullMessageResponse(0, "success", data);
            }
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
