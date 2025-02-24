package opi.botc;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.nbt.NbtCompound;
import java.util.Map;
import java.util.UUID;

public class BookBuilder {
    private final Map<UUID, Colors> playerColors;
    private final MinecraftServer server;

    public BookBuilder(Map<UUID, Colors> playerColors, MinecraftServer server) {
        this.playerColors = playerColors;
        this.server = server;
    }

    public String createGiveCommand() {
        StringBuilder contentBuilder = new StringBuilder();
        Colors[] colors = {Colors.RED, Colors.GREEN, Colors.PINK, Colors.BLUE, Colors.PURPLE, Colors.WHITE, Colors.ORANGE, Colors.YELLOW};
        int slot = 1;
        for (Colors color : colors) {
            UUID playerUuid = playerColors.entrySet().stream()
                    .filter(entry -> entry.getValue() == color)
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUuid);
            if (player != null) {
                String playerName = player.getName().getString();
                contentBuilder.append("{\"text\":\"")
                        .append(slot)
                        .append(": \",\"color\":\"")
                        .append(getColorCode(color))
                        .append("\"},\"")
                        .append(playerName)
                        .append("\\\\n\",");
            } else {
                contentBuilder.append("{\"text\":\"")
                        .append(slot)
                        .append(": \",\"color\":\"")
                        .append(getColorCode(color))
                        .append("\"},\"")
                        .append("Empty")
                        .append("\\\\n\",");
            }
            slot++;
        }
        
        String giveCommand = "/give @a written_book[written_book_content={pages:['[[\"\",{\"text\":\"Player Positions\",\"bold\":true,\"underlined\":true},\"\\\\n\\\\n\","
                + contentBuilder.toString()
                + "{\"text\":\"(Note: Players are arranged in a circle so 1 to 8 are also neighbours)\",\"italic\":true,\"color\":\"#828282\"}]]'],title:\"Player Positions\",author:Flame_Burns}]";
        return giveCommand;
    }

    private String getColorCode(Colors color) {
        return switch (color) {
            case RED -> "#FF0000";
            case GREEN -> "#00FF00";
            case PINK -> "#FFC2E5";
            case BLUE -> "#00FFFF";
            case PURPLE -> "#BF40BF";
            case WHITE -> "#E8E8E8";
            case ORANGE -> "#FFD700";
            case YELLOW -> "#E7DB32";
        };
    }
}
