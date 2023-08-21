package vn.vietdefi.util.sql;

import com.google.gson.*;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SQLJavaBridge {
    public HikariClient sqlClient;

    public SQLJavaBridge(HikariClient sqlClient) {
        this.sqlClient = sqlClient;
    }

    public Integer queryInteger(String query, Object... params) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        Integer value = null;
        try {
            connection = sqlClient.getConnection();
            st = buildQueryStatement(connection, query, params);
            rs = st.executeQuery();
            if (rs.next()) {
                value = rs.getInt(1);
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                if (i != params.length - 1) {
                    sb.append(params[i]).append(",");
                } else {
                    sb.append(params[i]);
                }
            }
            DebugLogger.error("query error {} params {}", query, sb.toString());
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return value;
    }

    public Long queryLong(String query, Object... params) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        Long value = null;
        try {
            connection = sqlClient.getConnection();
            st = buildQueryStatement(connection, query, params);
            rs = st.executeQuery();
            if (rs.next()) {
                value = rs.getLong(1);
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                if (i != params.length - 1) {
                    sb.append(params[i]).append(",");
                } else {
                    sb.append(params[i]);
                }
            }
            DebugLogger.error("query error {} params {}", query, sb.toString());
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return value;
    }

    public Double queryDouble(String query, Object... params) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        Double value = null;
        try {
            connection = sqlClient.getConnection();
            st = buildQueryStatement(connection, query, params);
            rs = st.executeQuery();
            if (rs.next()) {
                value = rs.getDouble(1);
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                if (i != params.length - 1) {
                    sb.append(params[i]).append(",");
                } else {
                    sb.append(params[i]);
                }
            }
            DebugLogger.error("query error {} params {}", query, sb.toString());
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return value;
    }

    public String queryString(String query, Object... params) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        String value = null;
        try {
            connection = sqlClient.getConnection();
            st = buildQueryStatement(connection, query, params);
            rs = st.executeQuery();
            if (rs.next()) {
                value = rs.getString(1);
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                if (i != params.length - 1) {
                    sb.append(params[i]).append(",");
                } else {
                    sb.append(params[i]);
                }
            }
            DebugLogger.error("query error {} params {}", query, sb.toString());
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return value;
    }

    public boolean queryExist(String query, Object... params) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        boolean exist = false;
        try {
            connection = sqlClient.getConnection();
            st = buildQueryStatement(connection, query, params);
            rs = st.executeQuery();
            exist = rs.next();
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                if (i != params.length - 1) {
                    sb.append(params[i]).append(",");
                } else {
                    sb.append(params[i]);
                }
            }
            DebugLogger.error("query error {} params {}", query, sb.toString());
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return exist;
    }

    public int queryAmmount(String query, Object... params) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        int count = 0;
        try {
            connection = sqlClient.getConnection();
            st = buildQueryStatement(connection, query, params);
            rs = st.executeQuery();
            while (rs.next()) {
                count++;
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                if (i != params.length - 1) {
                    sb.append(params[i]).append(",");
                } else {
                    sb.append(params[i]);
                }
            }
            DebugLogger.error("query error {} params {}", query, sb.toString());
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return count;
    }

    public JsonObject queryOne(String query, Object... params) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        JsonObject json = null;
        try {
            connection = sqlClient.getConnection();
            st = buildQueryStatement(connection, query, params);
            rs = st.executeQuery();
            json = GsonUtil.toJsonObject(rs);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                if (i != params.length - 1) {
                    sb.append(params[i]).append(",");
                } else {
                    sb.append(params[i]);
                }
            }
            DebugLogger.error("query error {} params {}", query, sb.toString());
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return json;
    }

    public JsonArray query(String query, Object... params) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        JsonArray array = new JsonArray();
        try {
            connection = sqlClient.getConnection();
            st = buildQueryStatement(connection, query, params);
            rs = st.executeQuery();
            array = GsonUtil.toJsonArray(rs);
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                if (i != params.length - 1) {
                    sb.append(params[i]).append(",");
                } else {
                    sb.append(params[i]);
                }
            }
            DebugLogger.error("query error {} params {}", query, sb.toString());
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return array;
    }

    public int update(String query, Object... params) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        try {
            connection = sqlClient.getConnection();
            st = buildQueryStatement(connection, query, params);
            int x = st.executeUpdate();
            return x;
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                if (i != params.length - 1) {
                    sb.append(params[i]).append(",");
                } else {
                    sb.append(params[i]);
                }
            }
            DebugLogger.error("update error {} params {}", query, sb.toString());
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return 0;
    }



    public PreparedStatement buildQueryStatement(Connection connection, String query, Object... params) throws SQLException {
        PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        int count = 1;
        for (int i = 0; i < params.length; i++) {
            addParamsByValue(st, count++, params[i], params[i].getClass());
        }
        return st;
    }

    public static void addParamsByValue(PreparedStatement st, int index, Object value, Type type) throws SQLException {
        if (type == String.class) {
            st.setString(index, (String) value);
        } else if (type == Integer.class || type == int.class) {
            st.setInt(index, (Integer) value);
        } else if (type == Long.class || type == long.class) {
            st.setLong(index, (Long) value);
        } else if (type == Float.class || type == float.class) {
            st.setFloat(index, (Float) value);
        } else if (type == Double.class || type == double.class) {
            st.setDouble(index, (Double) value);
        } else if (type == Integer[].class || type == int[].class) {
            st.setString(index, GsonUtil.toJsonString(value));
        } else if (type == Long[].class || type == long[].class) {
            st.setString(index, GsonUtil.toJsonString(value));
        } else if (type == Double[].class || type == double[].class) {
            st.setString(index, GsonUtil.toJsonString(value));
        } else if (type == JsonObject.class || type == JsonArray.class) {
            st.setString(index, value.toString());
        }
    }


    public int updateObjectToDb(String table, String primaryKey, JsonObject json) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        try {
            connection = sqlClient.getConnection();
            st = buildUpdateStatement(connection, table, primaryKey, json);
            int x = st.executeUpdate();
            return x;
        } catch (Exception e) {
            DebugLogger.error("updateObjectToDb {}, {}", table, json);
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return 0;
    }

    public int updateObjectToDb(String table, JsonObject json) throws Exception {
        return updateObjectToDb(table, "id", json);
    }

    public void insertObjectToDB(String table, String primaryKey, JsonObject json) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        try {
            connection = sqlClient.getConnection();
            st = buildInsertStatement(connection, table, json);
            st.executeUpdate();
            rs = st.getGeneratedKeys();
            if (rs.next()) {
                String insert_key = new StringBuilder("insert_").append(primaryKey).toString();
                long id = rs.getLong(insert_key);
                json.addProperty(primaryKey, id);
            }
        } catch (Exception e) {
            DebugLogger.error("insertObjectToDB {}, {}", table, json);
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
    }

    public void insertObjectToDB(String table, JsonObject json) throws Exception {
        insertObjectToDB(table, "id", json);
    }


    public PreparedStatement buildInsertStatement(Connection connection, String table, JsonObject json) throws Exception {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(table).append(" (");
        Set<Map.Entry<String, JsonElement>> fields = json.entrySet();
        Iterator<Map.Entry<String, JsonElement>> iterator = fields.iterator();
        int size = fields.size();
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (int i = 0; i < size; i++) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            String key = entry.getKey();
            sb1.append(key);
            if (i == size - 1) {
                sb1.append(") VALUES (");
                sb2.append("?)");
            } else {
                sb1.append(",");
                sb2.append("?,");
            }
        }
        sb.append(sb1).append(sb2);
        String query = sb.toString();
        PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        iterator = fields.iterator();
        for (int i = 1; i <= size; i++) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            addParamsByField(st, i, entry.getValue());
        }
        return st;
    }

    public void insertManyObjectToDB(String table, JsonArray array) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        try {
            connection = sqlClient.getConnection();
            connection.setAutoCommit(false);
            st = buildInsertManyStatement(connection, table, array);
            int[] updateCounts = st.executeBatch();
            connection.commit();
        } catch (Exception e) {
            DebugLogger.error("insertObjectToDB {}, {}", table, array);
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
    }

    public PreparedStatement buildInsertManyStatement(Connection connection, String table, JsonArray array) throws Exception {
        if (array.size() > 0) {
            JsonObject json = array.get(0).getAsJsonObject();
            StringBuilder sb = new StringBuilder("INSERT INTO ");
            sb.append(table).append(" (");
            Set<Map.Entry<String, JsonElement>> fields = json.entrySet();
            Iterator<Map.Entry<String, JsonElement>> iterator = fields.iterator();
            int size = fields.size();
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();
            for (int i = 0; i < size; i++) {
                Map.Entry<String, JsonElement> entry = iterator.next();
                String key = entry.getKey();
                sb1.append(key);
                if (i == size - 1) {
                    sb1.append(") VALUES (");
                    sb2.append("?)");
                } else {
                    sb1.append(",");
                    sb2.append("?,");
                }
            }
            sb.append(sb1).append(sb2);
            String query = sb.toString();
            PreparedStatement st = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            for (int j = 0; j < array.size(); j++) {
                JsonObject object = array.get(j).getAsJsonObject();
                fields = object.entrySet();

                iterator = fields.iterator();
                for (int i = 1; i <= size; i++) {
                    Map.Entry<String, JsonElement> entry = iterator.next();
                    addParamsByField(st, i, entry.getValue());
                }
                st.addBatch();
            }
            return st;
        }
        return null;
    }

    public PreparedStatement buildUpdateStatement(Connection connection, String table, String primaryField, JsonObject json) throws SQLException, IllegalAccessException {
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(table).append(" SET ");
        Set<Map.Entry<String, JsonElement>> fields = json.entrySet();
        Iterator<Map.Entry<String, JsonElement>> iterator = fields.iterator();
        int size = fields.size();
        for (int i = 0; i < size; i++) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            String key = entry.getKey();
            if (!key.equals(primaryField)) {
                sb.append(key).append(" = ? ");
            }
            if (i != size - 1 && !key.equals(primaryField)) {
                sb.append(", ");
            }
        }
        sb.append("WHERE ").append(primaryField).append("= ?");
        String query = sb.toString();
        PreparedStatement st = connection.prepareStatement(query);
        int count = 1;
        JsonElement idValue = null;
        iterator = fields.iterator();
        for (int i = 0; i < size; i++) {
            Map.Entry<String, JsonElement> entry = iterator.next();
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if (!key.equals(primaryField)) {
                addParamsByField(st, count++, value);
            } else {
                idValue = value;
            }
        }

        addParamsByField(st, count, idValue);
        return st;
    }

    public static void addParamsByField(PreparedStatement st, int index, JsonElement value) throws IllegalAccessException, SQLException {
        if (value.isJsonNull()) {
            st.setObject(index, null);
        } else if (value.isJsonPrimitive()) {
            JsonPrimitive primitive = value.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                st.setBoolean(index, value.getAsBoolean());
            } else if (primitive.isNumber()) {
                st.setDouble(index, value.getAsDouble());
            } else if (primitive.isString()) {
                st.setString(index, value.getAsString());
            }
        } else {
            st.setString(index, value.toString());
        }
    }
    public Long insertOne(String query, Object... params) throws Exception {
        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement st = null;
        Exception exception = null;
        try {
            connection = sqlClient.getConnection();
            st = buildQueryStatement(connection, query, params);
            int x = st.executeUpdate();
            if(x == 1){
                ResultSet generatedKeys = st.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long insertedId = generatedKeys.getLong(1);
                    return insertedId;
                }
            }
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < params.length; i++) {
                if (i != params.length - 1) {
                    sb.append(params[i]).append(",");
                } else {
                    sb.append(params[i]);
                }
            }
            DebugLogger.error("update error {} params {}", query, sb.toString());
            exception = e;
        } finally {
            sqlClient.releaseConnection(connection, st, rs);
            if (exception != null) {
                throw exception;
            }
        }
        return -1L;
    }
}
