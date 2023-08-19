package vn.vietdefi.api.services;

import vn.vietdefi.api.services.auth.AuthService;
import vn.vietdefi.api.services.auth.IAuthService;
import vn.vietdefi.api.services.otp.IOTPService;
import vn.vietdefi.api.services.otp.OTPService;

public class ApiServices {
    public static IAuthService authService = new AuthService();
    public static IOTPService otpService = new OTPService();
}
