/**
 * 
 */
package com.propn.golf.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thunder.Hsu
 * 
 */
public class View {

    public static final String redirect = "redirect";
    public static final String jsp = "jsp";
    public static final String html = "html";
    public static final String freeMarker = "FreeMarker";

    private String kind;
    private String path;
    private Map<String, Object> model = new HashMap<String, Object>();

    public View(String kind, String path, Map<String, Object> model) {
        this.kind = kind;
        this.path = path;
        if (null != model) {
            this.model = model;
        }
    }

    public String getPath() {
        return path;
    }

    public String getKind() {
        return kind;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void addAttribute(String key, Object value) {
        model.put(key, value);
    }
}
