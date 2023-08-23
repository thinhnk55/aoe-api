package vn.vietdefi.bank.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BankAccountState;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.random.RandomUtil;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class BankService implements IBankService {
    @Override
    public JsonObject createBankAccount(int bankCode, String accountOwner, String accountNumber, long bankDetailId) {
        try {
            JsonObject response = getBankAccountByBankCodeAndAccountNumber(bankCode, accountNumber);
            if (BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject data = new JsonObject();
            data.addProperty("bank_code", bankCode);
            data.addProperty("account_number", accountNumber);
            data.addProperty("account_owner", accountOwner);
            data.addProperty("bank_detail_id", bankDetailId);
            data.addProperty("state", BankAccountState.WAIT_TO_WORK);
            bridge.insertObjectToDB("bank_account", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject getBankAccountByBankCodeAndAccountNumber(int bankCode, String accountNumber) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_account WHERE bank_code = ? AND account_number = ?";
            JsonObject data = bridge.queryOne(query, bankCode, accountNumber);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(1, "bank_account_not_found");
            } else {
                return BaseResponse.createFullMessageResponse(0, "success", data);
            }
        } catch (Exception e) {
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getWorkingBankAccount() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_account WHERE state = ?";
            JsonArray data = bridge.query(query, BankAccountState.WORKING);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getOneWorkingBankAccount() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_account WHERE state = ?";
            JsonArray array = bridge.query(query, BankAccountState.WORKING);
            if(array.size() > 0) {
                int random = RandomUtil.nextInt(array.size());
                JsonObject data = array.get(random).getAsJsonObject();
                return BaseResponse.createFullMessageResponse(0, "success", data);
            }else{
                return BaseResponse.createFullMessageResponse(10, "no_working_bank");
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateBankAccountState(long id, int state) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE bank_account SET state = ? WHERE id = ?";
            bridge.update(query, state, id);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }


    @Override
    public JsonObject getAccountById(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_account WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(10, "bank_account_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listBankAccountByState(int state, long page, long recordPerPage) {
        try {
            long offset = (page - 1) * recordPerPage;
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_account WHERE state = ? LIMIT ? OFFSET ?";
            JsonArray array = bridge.query(query, state, recordPerPage, offset);
            JsonObject data = new JsonObject();
            data.add("banks", array);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject listBankAccount(long page, long recordPerPage) {
        try {
            long offset = (page - 1) * recordPerPage;
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_account LIMIT ? OFFSET ?";
            JsonArray array = bridge.query(query, recordPerPage, offset);
            JsonObject data = new JsonObject();
            data.add("banks", array);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject login(int bankCode, String username, String password) {
        try {
            switch (bankCode) {
                case BankCode.TIMO:
                    return loginTimo(username, password);

            }
            return BaseResponse.createFullMessageResponse(10, "bank_un_support");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private JsonObject loginTimo(String username, String password) {
        JsonObject response = BankServices.timoService.loginTimo(username, password);
        if(BaseResponse.isSuccessFullMessage(response)){
            JsonObject timo = response.getAsJsonObject("data");
            response = getBankAccountFromTimo(timo);
            if(BaseResponse.isSuccessFullMessage(response)){
                JsonObject bank = response.getAsJsonObject("data");
                if(bank.get("state").getAsInt() == BankAccountState.DISABLE){
                    waitToWork(bank.get("id").getAsInt());
                }
                return response;
            }else{
                return BaseResponse.createFullMessageResponse(11, "failure");
            }
        }else{
            if(response.get("error").getAsInt() == 10) {
                JsonObject timo = response.getAsJsonObject("data");
                long timoId = timo.get("id").getAsLong();
                JsonObject data = new JsonObject();
                data.addProperty("bank_code", BankCode.TIMO);
                data.addProperty("id", timoId);
                return BaseResponse.createFullMessageResponse(12, "require_otp", data);
            }else{
                return BaseResponse.createFullMessageResponse(11, "failure");
            }
        }
    }

    @Override
    public JsonObject commitOTP(int bankCode, long id, String otp) {
        try {
            switch (bankCode) {
                case BankCode.TIMO:
                    return commitTimo(id, otp);
            }
            return BaseResponse.createFullMessageResponse(10, "bank_un_support");
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private JsonObject commitTimo(long id, String otp) {
        JsonObject response = BankServices.timoService.commitTimo(id, otp);
        if(BaseResponse.isSuccessFullMessage(response)){
            JsonObject timo = response.getAsJsonObject("data");
            response = getBankAccountFromTimo(timo);
            if(BaseResponse.isSuccessFullMessage(response)){
                JsonObject bank = response.getAsJsonObject("data");
                if(bank.get("state").getAsInt() == BankAccountState.DISABLE){
                    waitToWork(bank.get("id").getAsInt());
                }
                return response;
            }else{
                return BaseResponse.createFullMessageResponse(11, "failure");
            }
        }else{
            return BaseResponse.createFullMessageResponse(11, "failure");
        }
    }
    private JsonObject createBankAccountFromTimo(JsonObject timo){
        long timoId = timo.get("id").getAsLong();
        JsonObject response = BankServices.timoService.getBankAccountInfo(timo);
        if(BaseResponse.isSuccessFullMessage(response)){
            JsonObject bank = response.getAsJsonObject("data");
            String accountNumber = bank.get("accountNumber").getAsString();
            String accountOwner = bank.get("fullName").getAsString();
            response = createBankAccount(BankCode.TIMO, accountOwner,
                    accountNumber, timo.get("id").getAsLong());
            if(BaseResponse.isSuccessFullMessage(response)) {
                long bank_account_id = response.getAsJsonObject("data")
                        .get("id").getAsLong();
                long bank_detail_id = response.getAsJsonObject("data")
                        .get("bank_detail_id").getAsLong();
                if(bank_detail_id != timoId){
                    updateBankDetailId(bank_account_id, timoId);
                }
                BankServices.timoService.updateBankAccountId(timoId, bank_account_id);
            }
            return response;
        }else{
            return response;
        }
    }

    private JsonObject getBankAccountFromTimo(JsonObject timo) {
        long bank_account_id = timo.get("bank_account_id").getAsLong();
        if(bank_account_id == 0){
            return createBankAccountFromTimo(timo);
        }else{
            JsonObject response = getAccountById(bank_account_id);
            if(BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }else{
                return createBankAccountFromTimo(timo);
            }
        }
    }

    private JsonObject updateBankDetailId(long id, long detail_id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE bank_account SET bank_detail_id = ? WHERE id = ?";
            bridge.update(query, detail_id, id);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject waitToWork(long id) {
        JsonObject response = updateBankAccountState(id, BankAccountState.WAIT_TO_WORK);
        return response;
    }

    public JsonObject startWorking(long id) {
        JsonObject response = updateBankAccountState(id, BankAccountState.WORKING);
        return response;
    }

    public JsonObject disable(long id) {
        JsonObject response = updateBankAccountState(id, BankAccountState.DISABLE);
        return response;
    }
}
