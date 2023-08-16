package vn.vietdefi.api.services.auth;

public class UserConstant {
    public static final long TOKEN_EXPIRED_TIME = 7*24*60*60*1000;
    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_LOCKED = 1;
    public static final int ROLE_USER = 1;
    public static final int ROLE_BOT = 2;
    public static final int ROLE_SUPER_ADMIN = 10;
    public static final int ROLE_ADMIN = 9;
    public static final int ROLE_SUPPORT = 8;
}

