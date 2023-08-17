package vn.vietdefi.bank.logic;

import com.google.gson.JsonObject;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.common.BaseResponse;

public class BankAccount {
    public long id;
    public int bank_code;
    public String account_number;
    public String account_owner;
    public int state;
    public JsonObject bank_detail;

    public BankAccount(JsonObject json) {
        this.id = json.get("id").getAsLong();
        this.bank_code = json.get("bank_code").getAsInt();
        this.account_number = json.get("bank_name").getAsString();
        this.account_owner = json.get("account_number").getAsString();
        this.state = json.get("state").getAsInt();
        long bank_detail_id = json.get("bank_detail_id").getAsLong();
        if(this.bank_code == BankCode.TIMO){
            JsonObject response = BankServices.timoService.getAccountById(bank_detail_id);
            if(BaseResponse.isSuccessFullMessage(response)){
                this.bank_detail = response.getAsJsonObject("data");
            }
        }
    }
}
