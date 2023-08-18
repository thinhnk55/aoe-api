package vn.vietdefi.api.services.auth;

import com.google.gson.JsonObject;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;
public class AuthService implements IAuthService {
    @Override
    public JsonObject register(String username, String password, int role, int status) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT id FROM user WHERE username = ?";
            boolean isExisted = bridge.queryExist(query, username);
            if(isExisted){
                return BaseResponse.createFullMessageResponse(10, "user_exist");
            }
            query = "INSERT INTO user (username, password, role, status, create_time, token, token_expired) VALUE (?,?,?,?,?,?,?)";
            String hashedPassword = StringUtil.sha256(password);
            long createTime = System.currentTimeMillis();
            String token = StringUtil.generateRandomStringNumberCharacter(32);
            long tokenExpired = System.currentTimeMillis() + UserConstant.TOKEN_EXPIRED_TIME;
            Long userId = bridge.insertOne(query, username, hashedPassword, role, status, createTime, token, tokenExpired);
            JsonObject data = new JsonObject();
            data.addProperty("id", userId);
            data.addProperty("username", username);
            data.addProperty("role", role);
            data.addProperty("status", status);
            data.addProperty("create_time", createTime);
            data.addProperty("token", token);
            data.addProperty("token_expired", tokenExpired);
            JsonObject response = AoeServices.profileService.getUserProfileByUserId(userId);
            data.add("aoe_profile", response.getAsJsonObject("data"));
            response = AoeServices.starService.getStarWalletByUserId(userId);
            data.add("aoe_star", response.getAsJsonObject("data"));
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    public JsonObject get(long userId) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM user WHERE id = ?";
            JsonObject data = bridge.queryOne(query, userId);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "user_not_exist");
            }
            data.remove("password");
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    public JsonObject get(String username) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM user WHERE username = ?";
            JsonObject data = bridge.queryOne(query, username);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "user_not_exist");
            }
            data.remove("password");
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject login(String username, String password){
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM user WHERE username = ?";
            JsonObject data = bridge.queryOne(query, username);
            if(data == null) {
                return BaseResponse.createFullMessageResponse(10, "invalid_username");
            }
            String storedPassword = data.get("password").getAsString();
            String hashedPassword = StringUtil.sha256(password);
            if(!storedPassword.equals(hashedPassword)){
                return BaseResponse.createFullMessageResponse(11, "invalid_password");
            }
            int status = data.get("status").getAsInt();
            if(status != UserConstant.STATUS_NORMAL){
                return BaseResponse.createFullMessageResponse(12, "account_locked", data);
            }
            long tokenExpired = data.get("token_expired").getAsLong();
            long now = System.currentTimeMillis();
            if(tokenExpired < now) {
                String token = StringUtil.generateRandomStringNumberCharacter(32);
                tokenExpired = now + UserConstant.TOKEN_EXPIRED_TIME;
                query = "UPDATE user SET token = ?, token_expired = ? WHERE id =?";
                bridge.query(query, token, tokenExpired, data.get("id").getAsInt());
                data.addProperty("token", token);
                data.addProperty("token_expired", tokenExpired);
            }
            data.remove("password");
            long userId = data.get("id").getAsLong();
            JsonObject response = AoeServices.profileService.getUserProfileByUserId(userId);
            data.add("aoe_profile", response.getAsJsonObject("data"));
            response = AoeServices.starService.getStarWalletByUserId(userId);
            data.add("aoe_star", response.getAsJsonObject("data"));
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    public JsonObject login(long userId) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM user WHERE id = ?";
            JsonObject data = bridge.queryOne(query, userId);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "user_not_exist");
            }
            long tokenExpired = data.get("token_expired").getAsLong();
            long now = System.currentTimeMillis();
            if(tokenExpired < now) {
                String token = StringUtil.generateRandomStringNumberCharacter(32);
                tokenExpired = now + UserConstant.TOKEN_EXPIRED_TIME;
                query = "UPDATE user SET token = ?, token_expired = ? WHERE id =?";
                bridge.query(query, token, tokenExpired, data.get("id").getAsInt());
                data.addProperty("token", token);
                data.addProperty("token_expired", tokenExpired);
            }
            data.remove("password");
            return BaseResponse.createFullMessageResponse(0, "sucess", data);
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    public JsonObject login(String username) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM user WHERE username = ?";
            JsonObject data = bridge.queryOne(query, username);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "user_not_exist");
            }
            long tokenExpired = data.get("token_expired").getAsLong();
            long now = System.currentTimeMillis();
            if(tokenExpired < now) {
                String token = StringUtil.generateRandomStringNumberCharacter(32);
                tokenExpired = now + UserConstant.TOKEN_EXPIRED_TIME;
                query = "UPDATE user SET token = ?, token_expired = ? WHERE id =?";
                bridge.query(query, token, tokenExpired, data.get("id").getAsInt());
                data.addProperty("token", token);
                data.addProperty("token_expired", tokenExpired);
            }
            data.remove("password");
            return BaseResponse.createFullMessageResponse(0, "sucess", data);
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject logout(long userid)  {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE user SET token=? WHERE id=?";
            String token = StringUtil.generateRandomStringNumberCharacter(32);
            bridge.update(query, token, userid);
            return BaseResponse.createFullMessageResponse(0, "success");
        }catch (Exception e){
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject authorize(long userid, String token) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM user WHERE id = ? AND token = ?";
            JsonObject data = bridge.queryOne(query, userid, token);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "invalid_token");
            }
            int status = data.get("status").getAsInt();
            if(status != UserConstant.STATUS_NORMAL){
                return BaseResponse.createFullMessageResponse(11, "account_locked", data);
            }
            long tokenExpired = data.get("token_expired").getAsLong();
            long now = System.currentTimeMillis();
            if(tokenExpired < now){
                return BaseResponse.createFullMessageResponse(11, "token_expired", data);
            }
            data.remove("password");
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject lock(long userid) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE user SET status=? WHERE id=?";
            int x = bridge.update(query, UserConstant.STATUS_LOCKED, userid);
            if(x == 0){
                return BaseResponse.createFullMessageResponse(10, "lock_failed");
            }
            return get(userid);
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
