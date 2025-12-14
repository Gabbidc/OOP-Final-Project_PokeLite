package com.pokelite;

import java.io.*;
import java.util.*;

public class Achievements {

    public static class Achievement {
        public final String id;
        public final String title;
        public final String desc;
        public Achievement(String id, String title, String desc) { this.id=id; this.title=title; this.desc=desc; }
    }

    private static final List<Achievement> ALL = Arrays.asList(
            new Achievement("first_win", "First Blood", "Win your first match."),
            new Achievement("wins_10", "Getting Serious", "Accumulate 10 total wins."),
            new Achievement("win_streak_5", "Hot Streak", "Win 5 matches in a row."),
            new Achievement("lose_streak_5", "It's Not About How Hard You Get Hit", "Lose 5 matches in a row."),
            new Achievement("survive_3", "Survivor", "Clear at least 3 waves in Survival Mode."),
            new Achievement("plays_20", "Veteran", "Play 20 matches total.")
    );

    public static List<String[]> getStatusForProfile(String profileName) {
        List<String[]> out = new ArrayList<>();
        int wins = 0; int losses = 0;
        PlayerProfile p = null;
        try { p = PlayerProfile.loadProfileByName(profileName); } catch (Exception e) { }
        if (p != null) { wins = p.getWins(); losses = p.getLosses(); }

        int totalMatches = countTotalMatches(profileName);
        StreakInfo streak = computeRecentStreak(profileName);
        boolean survived3 = checkSurvivedWaves(profileName, 3);

        for (Achievement a : ALL) {
            boolean computed = false;
            switch (a.id) {
                case "first_win": computed = wins >= 1; break;
                case "wins_10": computed = wins >= 10; break;
                case "win_streak_5": computed = streak != null && "WIN".equals(streak.type) && streak.length >= 5; break;
                case "lose_streak_5": computed = streak != null && "LOSS".equals(streak.type) && streak.length >= 5; break;
                case "survive_3": computed = survived3; break;
                case "plays_20": computed = totalMatches >= 20; break;
            }
            boolean stored = false;
            if (p != null) {
                try { stored = p.isAchievementUnlocked(a.id); } catch (Exception ex) { stored = false; }
            }
            // If computed unlocked but not yet stored, persist it
            if (computed && p != null && !stored) {
                try { p.saveAchievementFlag(a.id, true); stored = true; } catch (Exception ex) { }
            }
            boolean finalUnlocked = stored || computed;
            out.add(new String[]{a.title, a.desc, finalUnlocked ? "UNLOCKED" : "LOCKED"});
        }
        return out;
    }

    private static int countTotalMatches(String profileName) {
        File f = new File("battle_history.txt");
        if (!f.exists()) return 0;
        int c=0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(profileName)) c++;
            }
        } catch (IOException e) { }
        return c;
    }

    private static class StreakInfo { String type; int length; StreakInfo(String t,int l){type=t;length=l;} }

    private static StreakInfo computeRecentStreak(String profileName) {
        File f = new File("battle_history.txt");
        if (!f.exists()) return new StreakInfo("",0);
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line);
        } catch (IOException e) { }
        // iterate from end
        int streak = 0; String type = null;
        for (int i = lines.size()-1; i >= 0; i--) {
            String L = lines.get(i);
            if (!L.contains(profileName)) break; // stop when encounter other player's record
            if (L.contains("WIN:")) {
                if (type==null) type="WIN";
                if (!"WIN".equals(type)) break;
                streak++;
            } else if (L.contains("LOSS:")) {
                if (type==null) type="LOSS";
                if (!"LOSS".equals(type)) break;
                streak++;
            } else break;
        }
        if (type==null) return new StreakInfo("",0);
        return new StreakInfo(type, streak);
    }

    private static boolean checkSurvivedWaves(String profileName, int target) {
        File f = new File("battle_history.txt");
        if (!f.exists()) return false;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains(profileName)) continue;
                int idx = line.indexOf("(wave ");
                if (idx >= 0) {
                    int end = line.indexOf(')', idx);
                    if (end > idx) {
                        String num = line.substring(idx+6, end).trim();
                        try { int n = Integer.parseInt(num); if (n >= target) return true; } catch (NumberFormatException ex) { }
                    }
                }
            }
        } catch (IOException e) { }
        return false;
    }
}
