package opi.botc.scripts;

import java.util.ArrayList;
import java.util.List;

import opi.botc.roles.Role;

public class Script {
    private List<Role> townsfolk = new ArrayList<Role>();
    private List<Role> outsiders = new ArrayList<Role>();
    private List<Role> minions = new ArrayList<Role>();
    private List<Role> demons = new ArrayList<Role>();
    private List<Role> travellers = new ArrayList<Role>();
    private List<Role> roles = new ArrayList<Role>();
    
    public Script(List<Role> roles) {
        for (Role role : roles) {
            switch (role.getRoleType()) {
                case Townsfolk:
                    townsfolk.add(role);
                    break;
                case Outsider:
                    outsiders.add(role);
                    break;
                case Minion:
                    minions.add(role);
                    break;
                case Demon:
                    demons.add(role);
                    break;
                case Traveller:
                    travellers.add(role);
                    break;
            }
        }
    this.roles = roles;
    }
    public List<Role> getTownsfolk() {
        return townsfolk;
    }
    public List<Role> getOutsiders() {
        return outsiders;
    }
    public List<Role> getMinions() {
        return minions;
    }
    public List<Role> getDemons() {
        return demons;
    }
    public List<Role> getTravellers() {
        return travellers;
    }
    public List<Role> getRoles() {
        return roles;
    }
}

