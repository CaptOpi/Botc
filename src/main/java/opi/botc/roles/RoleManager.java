package opi.botc.roles;

import java.util.HashMap;
import java.util.Map;

public class RoleManager {
    private Map<String, Role> townsfolk = new HashMap<>();
    private Map<String, Role> outsiders = new HashMap<>();
    private Map<String, Role> minions = new HashMap<>();
    private Map<String, Role> demons = new HashMap<>();
    private Map<String, Role> travellers = new HashMap<>();
    private Map<String, Role> roles = new HashMap<>();

    public void addRole(Role role) {
        switch ((RoleType) role.getRoleType()) {
            case Townsfolk:
                townsfolk.put(role.getName(), role);
                break;
            case Outsider:
                outsiders.put(role.getName(), role);
                break;
            case Minion:
                minions.put(role.getName(), role);
                break;
            case Demon:
                demons.put(role.getName(), role);
                break;
            case Traveller:
                travellers.put(role.getName(), role);
                break;
        }
    }
    public void addRole(String name, Role role) {
        roles.put(name, role);
    }
    public Role getRole(String name) {
        return roles.get(name);
    }
    public String toString() {
        return roles.toString();
    }
    public void removeRole(Role role) {
        switch ((RoleType) role.getRoleType()) {
            case Townsfolk:
                townsfolk.remove(role.getName());
                break;
            case Outsider:
                outsiders.remove(role.getName());
                break;
            case Minion:
                minions.remove(role.getName());
                break;
            case Demon:
                demons.remove(role.getName());
                break;
            case Traveller:
                travellers.remove(role.getName());
                break;
        }
    }
    public Map<String, Role> getTown() {
        return townsfolk;
    }
    public Map<String, Role> getOutsider() {
        return outsiders;
    }
    public Map<String, Role> getMinion() {
        return minions;
    }
    public Map<String, Role> getDemon() {
        return demons;
    }
    public Map<String, Role> getTraveller() {
        return travellers;
    }
    public Map<String, Role> getRoles() {
        return roles;
    }
}
