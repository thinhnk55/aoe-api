package vn.vietdefi.bank.services;

import com.google.gson.JsonObject;
import vn.vietdefi.bank.logic.BalanceTransaction;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.bank.services.timo.ITimoService;
import vn.vietdefi.bank.services.timo.TimoService;
import vn.vietdefi.common.BaseResponse;

import java.util.List;

public class BankService implements IBankService{
    private final ITimoService timoService = new TimoService();
    @Override
    public JsonObject login(JsonObject data) {
        try {
            int bankCode = data.get("bank_code").getAsInt();
            String username = data.get("username").getAsString();
            String password = data.get("password").getAsString();
            switch (bankCode) {
                case BankCode.TIMO:
                    return timoService.loginTimo(username, password);
            }
            return BaseResponse.createFullMessageResponse(10, "bank_un_support");
        } catch (Exception e) {
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject commit(JsonObject data) {
        try {
            int bankCode = data.get("bank_code").getAsInt();
            String otp = data.get("otp").getAsString();
            String token = data.get("token").getAsString();
            switch (bankCode) {
                case BankCode.TIMO:
                    String refNo = data.get("refNo").getAsString();
                    return timoService.commitTimo(token,refNo,otp);
            }
            return BaseResponse.createFullMessageResponse(10, "bank_un_support");
        } catch (Exception e) {
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject createBankAccount(JsonObject data) {
        return null;
    }
    @Override
    public JsonObject getActiveBanks() {
        return null;
    }

    @Override
    public JsonObject updateBankState(long id, int state) {
        return null;
    }
}
