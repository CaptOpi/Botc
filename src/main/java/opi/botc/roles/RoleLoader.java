package opi.botc.roles;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import opi.botc.BloodOfTheClocktower;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.logging.Logger;

public class RoleLoader {

    private static final Gson gson = new Gson();
    private static final Identifier ROLES_JSON = Identifier.of(BloodOfTheClocktower.MOD_ID, "role/roles.json");
    public static Logger LOGGER = Logger.getLogger(BloodOfTheClocktower.MOD_ID);

    public static void loadRoles(RoleManager roleManager) {
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        try {
            Optional<Resource> optionalResource = resourceManager.getResource(ROLES_JSON);
            if (optionalResource.isPresent()) {
                Resource resource = optionalResource.get();
                InputStream inputStream = resource.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

                JsonObject rolesJson = gson.fromJson(reader, JsonObject.class);

                loadRoleCategory(rolesJson, "townsfolk", RoleType.Townsfolk, roleManager);
                loadRoleCategory(rolesJson, "outsiders", RoleType.Outsider, roleManager);
                loadRoleCategory(rolesJson, "minions", RoleType.Minion, roleManager);
                loadRoleCategory(rolesJson, "demons", RoleType.Demon, roleManager);
                loadRoleCategory(rolesJson, "travellers", RoleType.Traveller, roleManager);
            } else {
                LOGGER.severe("Failed to load roles.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load roles.");
        }
    }

    private static void loadRoleCategory(JsonObject rolesJson, String categoryName, RoleType roleType, RoleManager roleManager) {
        JsonArray categoryArray = rolesJson.getAsJsonArray(categoryName);

        for (int i = 0; i < categoryArray.size(); i++) {
            JsonObject roleJson = categoryArray.get(i).getAsJsonObject();
            String name = roleJson.get("name").getAsString();
            String description = roleJson.get("description").getAsString();

            Role role = new Role(name, description, roleType);
            roleManager.addRole(role);
            roleManager.addRole(name, role);
        }
    }
}
