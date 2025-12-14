# ⚡ PokeLite: Cyberpunk Edition

A retro-futuristic, turn-based monster battle simulator built with **Java** and **Swing**.

![Game Status](https://img.shields.io/badge/Status-Playable-brightgreen)
![Java](https://img.shields.io/badge/Language-Java-orange)

---

## 🎮 Gameplay

**PokeLite** is a turn-based battle system where players:

- Select a monster (**BlazeWolf, AquaTurtle, TerraGolem, SparkFox**) from the selection screen.
- Fight randomly generated opponents in **1v1 combat**.
- Choose from **3 standard attacks** and **1 powerful special attack** with cooldown.
- Manage cooldowns strategically to maximize damage.
- Play **Survival Mode** to battle consecutive enemy waves.
- Continue battling after victories or return to monster selection.
- Track win/loss history and leaderboard rankings.

---

## ✨ Features

### Profiles & Persistence
- Multiple player profiles stored in the `profiles/` directory.
- Each profile saved as:
  - `profiles/<name>.properties`
  - `profiles/<name>.achievements.properties`
- Persistent achievements per profile.

### Gameplay Enhancements
- Monsters use a **4-move API** with decision-making logic.
- One special attack per monster with cooldown mechanics.
- **Survival Mode** for continuous battles.
- **Next Battle** option after winning.

### UI / UX Improvements
- Redesigned start screen (Continue / New Game / Exit).
- Improved selection screen with:
  - Centered ASCII sprites
  - Larger buttons
  - Themed styling
- Visual damage effects:
  - Screen shake
  - Red flash on hit

### Battle History & Leaderboard
- Battle history logged to:
  - `battle_history.txt`
  - Exportable `battle_history.csv`
- Leaderboard tracks per-match deltas and aggregates results correctly.

### Achievements
- Persistent achievement system.
- Tracked achievements include:
  - First win
  - Win streaks
  - Survival waves completed
  - Total games played

---

## ▶️ How to Run

### Option 1: Using an IDE
1. Open the project in **IntelliJ**, **Eclipse**, or **VS Code**.
2. Run:src/com/pokelite/main.java

### Option 2: Command Line

#### Compile
```bash
javac -d bin src/com/pokelite/*.java src/com/pokelite/entities/*.java

java -cp bin com.pokelite.main

# Compile
javac -d bin src\com\pokelite\*.java src\com\pokelite\entities\*.java

# Run
java -cp bin com.pokelite.main

Requirements:
Java 8 or later (JDK 21+ recommended)

📁 File Structure
battle_history.txt            # Append-only match log
battle_history.csv            # Exported battle history
leaderboard.csv               # Per-match leaderboard records
profiles/                     # Player profiles & achievements
 ├─ <name>.properties
 └─ <name>.achievements.properties

src/com/pokelite/
 ├─ GameWindow.java           # Main GUI and game loop
 ├─ PlayerProfile.java        # Profile persistence
 ├─ Leaderboard.java          # Leaderboard aggregation
 ├─ Achievements.java         # Achievement logic
 └─ entities/                 # Monster implementations
