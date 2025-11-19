package de.jozelot.varoX.manager;

public class User {
    private String name;
    private boolean alive;
    private int kills;
    private int disconnects;
    private int strikes;
    private long lastPlayedSession;

    public User(String name) {
        this.name = name;
        this.alive = true;
        this.kills = 0;
        this.disconnects = 0;
        this.strikes = 0;
        this.lastPlayedSession = System.currentTimeMillis();
    }

    private User() {

    }

    public String getName() {
        return name;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getKills() {
        return kills;
    }

    public int getDisconnects() {
        return disconnects;
    }

    public int getStrikes() {
        return strikes;
    }

    public long getLastPlayedSession() {
        return lastPlayedSession;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void addKill() {
        this.kills++;
    }

    public void addDisconnect() {
        this.disconnects++;
    }

    public void addStrike() {
        this.strikes++;
    }

    public void setLastPlayedSession(long lastPlayedSession) {
        this.lastPlayedSession = lastPlayedSession;
    }
}