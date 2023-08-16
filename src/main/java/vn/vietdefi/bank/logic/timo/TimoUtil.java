package vn.vietdefi.bank.logic.timo;

import vn.vietdefi.util.string.StringUtil;

public class TimoUtil {
    public static String generateRandomTimoDevice(){
        String uuid = StringUtil.generateUUID();
        String client = ":WEB:WEB:176:WEB:desktop:chrome";
        String deviceId = new StringBuilder(uuid).append(client).toString();
        return deviceId;
    }
}
