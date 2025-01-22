package com.easyads.component.rpc;

import java.util.HashMap;
import java.util.Map;

public class ResponseCodeUtils {
    public static Map<Integer, String> responseErrorCodeName = new HashMap<Integer, String>() {{
        put(200, "OK");
        put(201, "Created");
        put(204, "No Content");
        put(400, "Bad Request");
        put(401, "Unauthorized");
        put(403, "Forbidden");
        put(404, "Not Found");
        put(500, "server error");
    }};

    public static Map<String, Object> setResponseErrorCodeWithMessage(Integer code, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", code);
        body.put("name", responseErrorCodeName.get(code));
        body.put("message", message);
        return body;
    }
}
