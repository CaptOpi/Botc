package opi.botc.roles;

import java.util.HashMap;
import java.util.Map;

public class RoleManager {
    private Map<String, Role> town = new HashMap<>();
    private Map<String, Role> outsider = new HashMap<>();
    private Map<String, Role> minion = new HashMap<>();
    private Map<String, Role> demon = new HashMap<>();
    private Map<String, Role> traveller = new HashMap<>();
    private Map<String, Role> fabled = new HashMap<>();

    public void addRole(Role role) {
        switch ((RoleType) role.getRoleType()) {
            case Townsfolk:
                town.put(role.getName(), role);
                break;
            case Outsider:
                outsider.put(role.getName(), role);
                break;
            case Minion:
                minion.put(role.getName(), role);
                break;
            case Demon:
                demon.put(role.getName(), role);
                break;
            case Traveller:
                traveller.put(role.getName(), role);
                break;
            case Fabled:
                fabled.put(role.getName(), role);
                break;
        }
    }
    public void removeRole(Role role) {
        switch ((RoleType) role.getRoleType()) {
            case Townsfolk:
                town.remove(role.getName());
                break;
            case Outsider:
                outsider.remove(role.getName());
                break;
            case Minion:
                minion.remove(role.getName());
                break;
            case Demon:
                demon.remove(role.getName());
                break;
            case Traveller:
                traveller.remove(role.getName());
                break;
            case Fabled:
                fabled.remove(role.getName());
                break;
        }
    }
    public Map<String, Role> getTown() {
        return town;
    }
    public Map<String, Role> getOutsider() {
        return outsider;
    }
    public Map<String, Role> getMinion() {
        return minion;
    }
    public Map<String, Role> getDemon() {
        return demon;
    }
    public Map<String, Role> getTraveller() {
        return traveller;
    }
    public Map<String, Role> getFabled() {
        return fabled;
    }
}
