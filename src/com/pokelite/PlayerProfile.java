package com.pokelite;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PlayerProfile {
    private String playerName = "PLAYER";
    private String lastMonster = "";
    private int wins = 0;
    private int losses = 0;

    private String fileName = null; // path to properties file

    private static final String LEGACY_FILE = "player_profile.properties";
    private static final String PROFILES_DIR = "profiles";

    public PlayerProfile() {}

    public PlayerProfile(String name) {
        this.playerName = name;
        this.fileName = PROFILES_DIR + File.separator + name + ".properties";
    }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; if (fileName==null) fileName = PROFILES_DIR + File.separator + name + ".properties"; }

    public String getLastMonster() { return lastMonster; }
    public void setLastMonster(String m) { this.lastMonster = m; }

    public int getWins() { return wins; }
    public int getLosses() { return losses; }

    public void addWin() { wins++; }
    public void addLoss() { losses++; }

    // Save to associated profile file (or to legacy file if not set)
    public void saveToDisk() {
        try {
            File dir = new File(PROFILES_DIR);
            if (!dir.exists()) dir.mkdirs();
            String outFile = (fileName != null) ? fileName : LEGACY_FILE;
            try (OutputStream out = new FileOutputStream(outFile)) {
                Properties p = new Properties();
                p.setProperty("name", playerName == null ? "" : playerName);
                p.setProperty("lastMonster", lastMonster == null ? "" : lastMonster);
                p.setProperty("wins", Integer.toString(wins));
                p.setProperty("losses", Integer.toString(losses));
                p.store(out, "ElementalClash Player Profile");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Achievements persistence per-profile
    private String achievementFilePath() {
        if (fileName != null) {
            // replace .properties with .achievements.properties
            if (fileName.endsWith(".properties")) return fileName.substring(0, fileName.length() - ".properties".length()) + ".achievements.properties";
            return fileName + ".achievements.properties";
        }
        return PROFILES_DIR + File.separator + playerName + ".achievements.properties";
    }

    public java.util.Properties loadAchievements() {
        java.util.Properties props = new java.util.Properties();
        File f = new File(achievementFilePath());
        if (!f.exists()) return props;
        try (java.io.InputStream in = new java.io.FileInputStream(f)) {
            props.load(in);
        } catch (java.io.IOException e) { e.printStackTrace(); }
        return props;
    }

    public void saveAchievementFlag(String id, boolean unlocked) {
        try {
            File dir = new File(PROFILES_DIR);
            if (!dir.exists()) dir.mkdirs();
            File f = new File(achievementFilePath());
            java.util.Properties props = loadAchievements();
            props.setProperty(id, Boolean.toString(unlocked));
            try (java.io.OutputStream out = new java.io.FileOutputStream(f)) {
                props.store(out, "Achievements for " + playerName);
            }
        } catch (java.io.IOException e) { e.printStackTrace(); }
    }

    public boolean isAchievementUnlocked(String id) {
        java.util.Properties props = loadAchievements();
        return Boolean.parseBoolean(props.getProperty(id, "false"));
    }

    public static PlayerProfile loadFromDisk() {
        // load legacy first if exists
        File legacy = new File(LEGACY_FILE);
        if (legacy.exists()) {
            PlayerProfile p = loadFromFile(LEGACY_FILE);
            syncStatsFromLeaderboard(p);
            return p;
        }
        // otherwise return empty profile
        return new PlayerProfile();
    }

    public static PlayerProfile loadFromFile(String path) {
        PlayerProfile profile = new PlayerProfile();
        profile.fileName = path;
        File f = new File(path);
        if (!f.exists()) return profile;
        try (InputStream in = new FileInputStream(f)) {
            Properties p = new Properties();
            p.load(in);
            profile.playerName = p.getProperty("name", profile.playerName);
            profile.lastMonster = p.getProperty("lastMonster", profile.lastMonster);
            profile.wins = Integer.parseInt(p.getProperty("wins", "0"));
            profile.losses = Integer.parseInt(p.getProperty("losses", "0"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profile;
    }

    public static List<String> listProfiles() {
        List<String> out = new ArrayList<>();
        File dir = new File(PROFILES_DIR);
        if (!dir.exists()) dir.mkdirs();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isFile() && f.getName().endsWith(".properties")) {
                    String name = f.getName().substring(0, f.getName().length() - ".properties".length());
                    out.add(name);
                }
            }
        }
        // include legacy file as 'default' if present
        File legacy = new File(LEGACY_FILE);
        if (legacy.exists()) out.add("default");
        return out;
    }

    public static PlayerProfile loadProfileByName(String name) {
        if (name == null || name.isEmpty()) return new PlayerProfile();
        if (name.equals("default")) return loadFromFile(LEGACY_FILE);
        PlayerProfile profile = loadFromFile(PROFILES_DIR + File.separator + name + ".properties");
        // Sync wins/losses from leaderboard CSV to ensure accuracy
        syncStatsFromLeaderboard(profile);
        return profile;
    }

    private static void syncStatsFromLeaderboard(PlayerProfile profile) {
        // Read leaderboard CSV and recalculate wins/losses for this player
        File leaderboardFile = new File("leaderboard.csv");
        if (!leaderboardFile.exists()) return;
        int totalWins = 0;
        int totalLosses = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(leaderboardFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                String recordName = parts[0];
                if (recordName.equals(profile.playerName)) {
                    totalWins += Integer.parseInt(parts[1]);
                    totalLosses += Integer.parseInt(parts[2]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        profile.wins = totalWins;
        profile.losses = totalLosses;
    }
}
