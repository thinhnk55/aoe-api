package vn.vietdefi.aoe.services.user.donor;

import com.google.gson.JsonObject;

public interface IDonorService {
    JsonObject createDonor(JsonObject json);
    JsonObject updateDonor(JsonObject json);
    JsonObject getDonorByUserId(long id);
    JsonObject getListDonor(long page, long recordPerPage);
    JsonObject deleteDonorByPhone(String phone);
}
