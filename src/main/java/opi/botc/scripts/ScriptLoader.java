package opi.botc.scripts;

import com.google.gson.Gson;
import net.minecraft.util.Identifier;
import opi.botc.BloodOfTheClocktower;

import java.util.logging.Logger;

public class ScriptLoader {

    private static final Gson gson = new Gson();
    private static final Identifier ROLES_JSON = Identifier.of(BloodOfTheClocktower.MOD_ID, "scripts/scripts.json");
    public static Logger LOGGER = Logger.getLogger(BloodOfTheClocktower.MOD_ID);

}
