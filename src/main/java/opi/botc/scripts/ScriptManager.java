package opi.botc.scripts;

import java.util.HashMap;
import java.util.Map;


public class ScriptManager {
    public static Map<String, Script> scripts = new HashMap<>();

    public static void addScript(String name, Script script) {
        scripts.put(name, script);
    }
    public static Script getScript(String name) {
        return scripts.get(name);
    }
    public static void removeScript(String name) {
        scripts.remove(name);
    }
}
