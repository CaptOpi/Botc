package opi.botc;

public enum Colors {
    RED("Red"),
    GREEN("Lime"),
    BLUE("Cyan"),
    YELLOW("Yellow"),
    ORANGE("Orange"),
    PURPLE("Purple"),
    PINK("Pink"),
    WHITE("White");

    private final String displayName;

    Colors(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Colors fromString(String displayName) {
        for (Colors color : Colors.values()) {
            if (color.getDisplayName().equalsIgnoreCase(displayName)) {
                return color;
            }
        }
        return null;
    }

    public static Colors safeFromString(String displayName) {
        Colors color = fromString(displayName);
        if (color == null) {
            System.out.println("Invalid color entered: " + displayName);
        } else {
            System.out.println("Selected color: " + color);
        }
        return color;
    }
}
