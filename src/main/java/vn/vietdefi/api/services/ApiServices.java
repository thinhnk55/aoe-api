package vn.vietdefi.api.services;

import vn.vietdefi.api.services.auth.AuthService;
import vn.vietdefi.api.services.auth.IAuthService;

public class ApiServices {
    public static IAuthService authService = new AuthService();
}
