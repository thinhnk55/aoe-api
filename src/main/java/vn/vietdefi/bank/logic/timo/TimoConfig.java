package vn.vietdefi.bank.logic.timo;

public class TimoConfig {

    public static final String xTimoDevice = "367268af89462456da6c98b49ee98a56:WEB:WEB:176:WEB:desktop:chrome";
    public static final String URL_LOGIN = "https://app2.timo.vn/login";
    public static final String URL_NOTIFICATION_VN = "https://app2.timo.vn/notification/vn";
    public static final String URL_NOTIFICATION_CHECK = "https://app2.timo.vn/user/notification/check";
    public static final String URL_TRANSACTION_DETAIL = "https://app2.timo.vn/user/account/transaction/receipt";
    public static final String URL_TRANSACTION_LIST = "https://app2.timo.vn/user/account/transaction/list";
    /**
     * body
     * {action: "R", id: "ALL"}
     * post
     */
    public static final String URL_NOTIFICATION_UPDATE = "https://app2.timo.vn/notification/update"; //update notification had been read
    public static final String URL_LOGIN_COMMIT = "https://app2.timo.vn/login/commit";
    public static final String URL_FAST_TRANSFER_GET_INFO = "https://app2.timo.vn/user/fastTransfer/getInfo";
    public static final String URL_CARD_GAME = "https://app2.timo.vn/user/smartLink/cardName";
    public static final String URL_TRANSFER_ACCOUNT = "https://app2.timo.vn/user/txn/transfer/account";
    public static final String commit = "https://app2.timo.vn/user/txn/commit";
    public static final String CHECK_TYPE = "timo";
    public static final String ACCOUNT_TYPE = "1025";
    public static final String FORMAT = "list";
    public static final int INDEX = 0;
    public static final int OFFSET = -1;
}
