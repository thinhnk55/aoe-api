package vn.vietdefi.util.file;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtil {
    public static List<String> readAllLines(String filePath) {
        try{
            StringBuilder path = new StringBuilder();
            path.append(filePath);
            return Files.readAllLines(Paths.get(path.toString()));
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return null;
        }
    }
    public static String getString(String filePath) {
        String path = "";
        File file = new File(path + filePath);
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
        try {
            Reader r = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            reader = new BufferedReader(r);
            String text;
            boolean firstLine = true;
            while (true) {
                text = reader.readLine();
                if (text != null) {
                    if (firstLine) {
                        firstLine = false;
                    } else {
                        contents.append(System.getProperty("line.separator"));
                    }
                    contents.append(text);
                } else {
                    break;
                }
            }
            return contents.toString();
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return null;
        }
    }

    public static JsonObject getJsonObject(String filePath) {
        String data = getString(filePath);
        return GsonUtil.toJsonObject(data);
    }

    public static JsonArray getJsonArray(String filePath){
        String data = getString(filePath);
        return GsonUtil.toJsonArray(data);
    }

    public static void writeStringToFile(String fileName, String data) {
        Path path = Paths.get(fileName);
        try {
            BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            writer.append(data);
            writer.close();
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }
}
