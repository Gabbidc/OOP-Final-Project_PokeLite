package com.pokelite;

import java.io.*;
import java.util.*;

public class Leaderboard {
    private static final String FILE = "leaderboard.csv";

    public static void addEntry(String name, int wins, int losses) {
        try (FileWriter fw = new FileWriter(FILE, true)) {
            fw.append(String.format("%s,%d,%d\n", name.replaceAll(",", ""), wins, losses));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> aggregateTop(int limit) {
        Map<String, int[]> agg = new HashMap<>();
        File f = new File(FILE);
        if (!f.exists()) return Collections.emptyList();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;
                String name = parts[0];
                int w = Integer.parseInt(parts[1]);
                int l = Integer.parseInt(parts[2]);
                agg.putIfAbsent(name, new int[]{0,0});
                agg.get(name)[0] += w;
                agg.get(name)[1] += l;
            }
        } catch (IOException e) { e.printStackTrace(); }

        List<String[]> rows = new ArrayList<>();
        for (Map.Entry<String,int[]> e : agg.entrySet()) {
            int wins = e.getValue()[0];
            int losses = e.getValue()[1];
            double ratio = (wins + losses) == 0 ? 0.0 : (wins * 100.0 / (wins + losses));
            String ratioStr = String.format("%.1f%%", ratio);
            rows.add(new String[]{e.getKey(), Integer.toString(wins), Integer.toString(losses), ratioStr});
        }
        rows.sort((a,b) -> Integer.parseInt(b[1]) - Integer.parseInt(a[1]));
        if (rows.size() > limit) return rows.subList(0, limit);
        return rows;
    }
}
