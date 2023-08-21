package vn.vietdefi.api.services.otp;

import com.google.gson.JsonObject;

public interface IOTPService {
    JsonObject generateRandomOTP(long userId,int type);
    JsonObject authorizeRandomOTP(long userId, int otp);
    JsonObject verifyOTP(JsonObject data);
    JsonObject sendMessengerOtp (long userId);
    JsonObject verifyOtpForgotPassword(JsonObject data);
}
