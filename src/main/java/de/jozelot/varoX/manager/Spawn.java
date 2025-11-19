package de.jozelot.varoX.manager;

import org.bukkit.Location;
import org.bukkit.World;

public class Spawn {
    private int id;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Spawn(int id, double[] positionData) {
        this.id = id;
        this.x = positionData[0];
        this.y = positionData[1];
        this.z = positionData[2];
        this.yaw = (float) positionData[3];
        this.pitch = (float) positionData[4];
    }

    public double[] getPositionData() {
        double[] postionData = {x,y,z,yaw,pitch};
        return postionData;
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public int getId() {
        return id;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}