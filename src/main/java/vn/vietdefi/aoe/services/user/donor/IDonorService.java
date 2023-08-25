package vn.vietdefi.aoe.services.user.donor;

import com.google.gson.JsonObject;

public interface IDonorService {
    JsonObject createDonor(JsonObject json);
    JsonObject updateDonor(JsonObject json);
    JsonObject deleteDonor(long id);
    JsonObject getDonor(long id);
    JsonObject getDonors(JsonObject json);

    
}
