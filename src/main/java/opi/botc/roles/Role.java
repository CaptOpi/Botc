package opi.botc.roles;

public class Role {
    private final String name;
    private final String description;
    private final RoleType roleType;

    public Role(String name, String description, RoleType roleType) {
        this.name = name;
        this.description = description;
        this.roleType = roleType;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public RoleType getRoleType() {
        return roleType;
    }
    
}
