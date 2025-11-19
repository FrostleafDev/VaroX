package de.jozelot.varoX.manager; // Passen Sie das Paket ggf. an

import java.util.List;
import java.util.UUID;

public class Team {
    private int id;
    private String name;
    private int kills;
    private int strikes;
    private boolean alive;
    private List<String> members;

    // Konstruktor, Getter und Setter

    public Team(int id, String name, List<String> members) {
        this.id = id;
        this.name = name;
        this.kills = 0;
        this.strikes = 0;
        this.alive = true;
        this.members = members;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
    }

    public int getStrikes() {
        return strikes;
    }

    public void addStrike() {
        this.strikes++;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public List<String> getMembers() {
        return members;
    }

    public void addMember(String memberName) {
        this.members.add(memberName);
    }

    public void removeMember(String memberName) {
        this.members.removeIf(m -> m.equalsIgnoreCase(memberName));
    }
}