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


}
