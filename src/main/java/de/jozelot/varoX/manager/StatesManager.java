package de.jozelot.varoX.manager;

import de.jozelot.varoX.VaroX;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class StatesManager {

    private final FileManager fileManager;
    private final VaroX plugin;

    private final File gameStateFile;

    public StatesManager(FileManager fileManager, VaroX plugin) {
        this.fileManager = fileManager;
        this.gameStateFile = fileManager.getGameStateFile();
        this.plugin = plugin;
    }

    public int getGameState() {
        Integer gameState = fileManager.load(gameStateFile, "game_state", Integer.class);
        return gameState != null ? gameState : 0;
    }

    public void setGameState(int gameState) {
        fileManager.save(gameStateFile, "game_state", gameState);
        plugin.startPermanentDay();
    }

    public void saveWorldBorderState(int phaseNumber, double targetSize, long durationSeconds, long shrinkStartTimeMillis) {
        fileManager.save(gameStateFile, "border_active_phase", phaseNumber);
        fileManager.save(gameStateFile, "border_target_size", targetSize);
        fileManager.save(gameStateFile, "border_duration_seconds", durationSeconds);
        fileManager.save(gameStateFile, "border_start_time_ms", shrinkStartTimeMillis);
    }

    public void saveWorldBorderDelayState(int phaseNumber, long plannedDelaySeconds, long delayStartTimeMillis) {
        fileManager.save(gameStateFile, "border_active_phase", phaseNumber);
        fileManager.save(gameStateFile, "border_delay_duration_seconds", plannedDelaySeconds);
        fileManager.save(gameStateFile, "border_delay_start_time_ms", delayStartTimeMillis);

        fileManager.save(gameStateFile, "border_target_size", null);
        fileManager.save(gameStateFile, "border_duration_seconds", null);
        fileManager.save(gameStateFile, "border_start_time_ms", null);
    }

    public void saveWorldBorderShrinkState(int phaseNumber, double targetSize, long durationSeconds, long shrinkStartTimeMillis) {
        fileManager.save(gameStateFile, "border_active_phase", phaseNumber);
        fileManager.save(gameStateFile, "border_target_size", targetSize);
        fileManager.save(gameStateFile, "border_duration_seconds", durationSeconds);
        fileManager.save(gameStateFile, "border_start_time_ms", shrinkStartTimeMillis);

        fileManager.save(gameStateFile, "border_delay_duration_seconds", null);
        fileManager.save(gameStateFile, "border_delay_start_time_ms", null);
    }

    public void clearWorldBorderState() {
        fileManager.save(gameStateFile, "border_active_phase", null);
        fileManager.save(gameStateFile, "border_target_size", null);
        fileManager.save(gameStateFile, "border_duration_seconds", null);
        fileManager.save(gameStateFile, "border_start_time_ms", null);
        fileManager.save(gameStateFile, "border_delay_duration_seconds", null);
        fileManager.save(gameStateFile, "border_delay_start_time_ms", null);
    }

    public int getActiveBorderPhase() {
        Integer phase = fileManager.load(gameStateFile, "border_active_phase", Integer.class);
        return phase != null ? phase : 0;
    }

    public double getBorderTargetSize() {
        Double size = fileManager.load(gameStateFile, "border_target_size", Double.class);
        return size != null ? size : 0.0;
    }

    public long getBorderDurationSeconds() {
        Long duration = fileManager.load(gameStateFile, "border_duration_seconds", Long.class);
        return duration != null ? duration : 0;
    }

    public long getBorderStartTimeMillis() {
        Long time = fileManager.load(gameStateFile, "border_start_time_ms", Long.class);
        return time != null ? time : 0;
    }

    public long getBorderDelayDurationSeconds() {
        Long duration = fileManager.load(gameStateFile, "border_delay_duration_seconds", Long.class);
        return duration != null ? duration : 0;
    }

    public long getBorderDelayStartTimeMillis() {
        Long time = fileManager.load(gameStateFile, "border_delay_start_time_ms", Long.class);
        return time != null ? time : 0;
    }

}
