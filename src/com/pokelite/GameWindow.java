package com.pokelite;

import com.pokelite.entities.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWindow extends JFrame {

    // --- UI constants ---
    private static final Font FONT_TITLE = new Font("Monospaced", Font.BOLD, 24);
    private static final Font FONT_UI = new Font("Monospaced", Font.PLAIN, 14);
    private static final Font FONT_LOG = new Font("Monospaced", Font.PLAIN, 12);
    private static final Font FONT_ART = new Font("Monospaced", Font.PLAIN, 14);

    private static final Color CYBER_BG = new Color(12,12,20);
    private static final Color CYBER_CYAN = new Color(0,255,230);
    private static final Color CYBER_PINK = new Color(255,102,204);
    private static final Color CYBER_GRAY = new Color(40,40,50);
    private static final Color DAMAGE_RED = new Color(255,80,80);

    // --- State & components ---
    private PlayerProfile profile;
    private JTextField playerNameField;
    private JButton saveProfileButton, loadProfileButton, historyButton, leaderboardButton, statsButton;
    private JButton achievementsButton;
    private JLabel winsLabel, lossesLabel;
    private JCheckBox survivalCheck;

    private Monster player, enemy;
    private JPanel playerPanel, enemyPanel;
    private JTextArea playerArtArea, enemyArtArea;
    private JProgressBar playerHealthBar, enemyHealthBar;
    private JLabel playerHPText, enemyHPText;
    private JTextPane battleLog;
    private JButton[] moveButtons = new JButton[4];
    private JButton restartButton;
    private JButton nextBattleButton;

    private boolean survivalMode = false;
    private int waveNumber = 1;
    private Random rand;

    public GameWindow() {
        super("PokeLite");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        rand = new Random();
        profile = PlayerProfile.loadFromDisk();
        // ensure arrays
        for (int i=0;i<moveButtons.length;i++) moveButtons[i] = null;
        // show start
        showStartScreen();
    }

    private void showStartScreen() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        getContentPane().setBackground(CYBER_BG);

        JLabel title = new JLabel(">> POKELITE <<", SwingConstants.CENTER);
        title.setFont(new Font("Monospaced", Font.BOLD, 32));
        title.setForeground(CYBER_PINK);
        title.setBackground(Color.BLACK);
        title.setOpaque(true);
        title.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(CYBER_BG);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spacer
        center.add(Box.createVerticalStrut(50));

        JPanel btnCol = new JPanel();
        btnCol.setBackground(CYBER_BG);
        btnCol.setLayout(new BoxLayout(btnCol, BoxLayout.Y_AXIS));
        btnCol.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnContinue = new JButton("Continue?");
        JButton btnNew = new JButton("New Game");
        JButton btnExit = new JButton("Exit");

        btnContinue.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnNew.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExit.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnContinue.setMaximumSize(new Dimension(280, 60));
        btnNew.setMaximumSize(new Dimension(280, 60));
        btnExit.setMaximumSize(new Dimension(280, 60));

        styleCyberButton(btnContinue, CYBER_CYAN);
        styleCyberButton(btnNew, CYBER_PINK);
        styleCyberButton(btnExit, Color.WHITE);

        btnCol.add(btnContinue);
        btnCol.add(Box.createVerticalStrut(20));
        btnCol.add(btnNew);
        btnCol.add(Box.createVerticalStrut(20));
        btnCol.add(btnExit);

        center.add(btnCol);
        center.add(Box.createVerticalStrut(50));

        add(center, BorderLayout.CENTER);

        revalidate();
        repaint();

        btnExit.addActionListener(e -> System.exit(0));

        btnContinue.addActionListener(e -> {
            java.util.List<String> profiles = PlayerProfile.listProfiles();
            if (profiles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No saved profiles. Create a new game.");
                return;
            }
            JList<String> list = new JList<>(profiles.toArray(new String[0]));
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane sp = new JScrollPane(list);
            sp.setPreferredSize(new Dimension(300, 200));
            int res = JOptionPane.showConfirmDialog(this, sp, "Select profile to continue", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                String sel = list.getSelectedValue();
                if (sel == null) { JOptionPane.showMessageDialog(this, "Select a profile."); return; }
                profile = PlayerProfile.loadProfileByName(sel);
                playerNameField = new JTextField(profile.getPlayerName(), 18);
                showSelectionScreen();
            }
        });

        btnNew.addActionListener(e -> {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(CYBER_BG);
            JLabel lbl = new JLabel("Enter new profile name:");
            styleLabel(lbl);
            JTextField name = new JTextField();
            name.setPreferredSize(new Dimension(200, 30));
            name.setFont(FONT_UI);
            name.setBackground(Color.WHITE);
            name.setForeground(Color.BLACK);
            name.setCaretColor(Color.BLACK);
            name.setSelectionColor(new Color(220,220,220));
            name.setBorder(BorderFactory.createLineBorder(CYBER_GRAY));
            name.setOpaque(true);
            p.add(lbl, BorderLayout.NORTH);
            p.add(name, BorderLayout.CENTER);
            int r = JOptionPane.showConfirmDialog(this, p, "New Game", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (r == JOptionPane.OK_OPTION) {
                String nm = name.getText().trim();
                if (nm.isEmpty()) { JOptionPane.showMessageDialog(this, "Name cannot be empty."); return; }
                profile = new PlayerProfile(nm);
                profile.saveToDisk();
                playerNameField = new JTextField(profile.getPlayerName(), 18);
                showSelectionScreen();
            }
        });
        
    }

    // --- STYLING HELPERS (Unchanged mostly) ---
    private void styleCyberButton(JButton btn, Color borderColor) {
        btn.setFont(FONT_UI);
        btn.setBackground(CYBER_BG);
        btn.setForeground(borderColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Border line = BorderFactory.createLineBorder(borderColor, 2);
        Border padding = BorderFactory.createEmptyBorder(10, 20, 10, 20);
        btn.setBorder(BorderFactory.createCompoundBorder(line, padding));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if(btn.isEnabled()) {
                    btn.setBackground(borderColor);
                    btn.setForeground(CYBER_BG);
                }
            }
            public void mouseExited(MouseEvent e) {
                if(btn.isEnabled()) {
                    btn.setBackground(CYBER_BG);
                    btn.setForeground(borderColor);
                }
            }
        });
    }

    private void styleLabel(JLabel lbl) {
        lbl.setFont(FONT_UI);
        lbl.setForeground(CYBER_CYAN);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void styleTextArea(JTextArea area, Font font) {
        area.setEditable(false);
        area.setFont(font);
        area.setBackground(CYBER_BG);
        area.setForeground(CYBER_CYAN);
        area.setMargin(new Insets(10, 10, 10, 10));
    }

    private void styleProgressBar(JProgressBar bar) {
        bar.setForeground(Color.GREEN);
        bar.setBackground(CYBER_GRAY);
        bar.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() { return CYBER_BG; }
            @Override
            protected Color getSelectionForeground() { return CYBER_BG; }
        });
        bar.setBorder(BorderFactory.createLineBorder(CYBER_GRAY, 1));
        bar.setPreferredSize(new Dimension(200, 20));
    }

    // --- SELECTION SCREEN ---
    private void showSelectionScreen() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());
        getContentPane().setBackground(CYBER_BG);

        JLabel title = new JLabel(">> CHOOSE YOUR POKE <<", SwingConstants.CENTER);
        title.setFont(FONT_TITLE);
        title.setForeground(CYBER_PINK);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        add(title, BorderLayout.NORTH);

        // Top small profile panel
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        profilePanel.setBackground(CYBER_BG);

        playerNameField = new JTextField(profile.getPlayerName(), 18);
        playerNameField.setFont(FONT_UI);
        playerNameField.setForeground(CYBER_CYAN);
        playerNameField.setBackground(CYBER_GRAY);
        playerNameField.setEditable(false);

        saveProfileButton = new JButton("Save Profile");
        historyButton = new JButton("History");
        leaderboardButton = new JButton("Leaderboard");
        statsButton = new JButton("Stats");
        achievementsButton = new JButton("Achievements");
        loadProfileButton = new JButton("Switch Profile");

        styleCyberButton(loadProfileButton, CYBER_CYAN);
        styleCyberButton(historyButton, CYBER_PINK);
        styleCyberButton(leaderboardButton, new Color(255, 200, 0));
        styleCyberButton(statsButton, new Color(150, 255, 150));
        styleCyberButton(achievementsButton, new Color(255,230,0));
        // Make achievements outline-only and prominent (yellow border)
        achievementsButton.setFont(new Font("Monospaced", Font.BOLD, 16));
        achievementsButton.setBackground(CYBER_BG);
        achievementsButton.setForeground(new Color(255,230,0));
        achievementsButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,230,0), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        achievementsButton.setPreferredSize(new Dimension(160, 40));
        achievementsButton.setToolTipText("View achievements for the current profile");

        winsLabel = new JLabel("Wins: " + profile.getWins());
        lossesLabel = new JLabel("Losses: " + profile.getLosses());
        styleLabel(winsLabel);
        styleLabel(lossesLabel);

        JLabel nameLabel = new JLabel("Name:");
        styleLabel(nameLabel);

        profilePanel.add(nameLabel);
        profilePanel.add(playerNameField);
        profilePanel.add(loadProfileButton);
        // make achievements button more prominent and earlier
        profilePanel.add(achievementsButton);
        profilePanel.add(winsLabel);
        profilePanel.add(lossesLabel);
        profilePanel.add(historyButton);
        profilePanel.add(leaderboardButton);
        profilePanel.add(statsButton);

        survivalCheck = new JCheckBox("Survival Mode");
        survivalCheck.setBackground(CYBER_BG);
        survivalCheck.setForeground(CYBER_CYAN);
        profilePanel.add(survivalCheck);

        add(profilePanel, BorderLayout.PAGE_START);

        JPanel selectionPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        selectionPanel.setBackground(CYBER_BG);
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 60, 60));

        createSelectBtn(selectionPanel, "BlazeWolf [FIRE]", new BlazeWolf(), new Color(255, 50, 50));
        createSelectBtn(selectionPanel, "AquaTurtle [WATER]", new AquaTurtle(), new Color(50, 100, 255));
        createSelectBtn(selectionPanel, "TerraGolem [ROCK]", new TerraGolem(), new Color(150, 150, 150));
        createSelectBtn(selectionPanel, "SparkFox [VOLT]", new SparkFox(), new Color(255, 200, 0));

        add(selectionPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

        loadProfileButton.addActionListener(e -> {
            java.util.List<String> profiles = PlayerProfile.listProfiles();
            if (profiles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No saved profiles.");
                return;
            }
            JList<String> list = new JList<>(profiles.toArray(new String[0]));
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane sp = new JScrollPane(list);
            sp.setPreferredSize(new Dimension(300, 200));
            int res = JOptionPane.showConfirmDialog(this, sp, "Select profile to load", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res == JOptionPane.OK_OPTION) {
                String sel = list.getSelectedValue();
                if (sel == null) { JOptionPane.showMessageDialog(this, "Select a profile."); return; }
                profile = PlayerProfile.loadProfileByName(sel);
                playerNameField.setText(profile.getPlayerName());
                winsLabel.setText("Wins: " + profile.getWins());
                lossesLabel.setText("Losses: " + profile.getLosses());
                log("Profile loaded: " + profile.getPlayerName(), 0);
            }
        });

        historyButton.addActionListener(e -> showHistoryDialog());
        leaderboardButton.addActionListener(e -> showLeaderboardDialog());
        statsButton.addActionListener(e -> showStatsDialog());
        achievementsButton.addActionListener(e -> {
            java.util.List<String[]> list = Achievements.getStatusForProfile(profile.getPlayerName());
            StringBuilder sb = new StringBuilder();
            for (String[] row : list) {
                sb.append(String.format("%s - %s : %s\n", row[2].equals("UNLOCKED") ? "[UNLOCKED]" : "[LOCKED]", row[0], row[1]));
            }
            JTextArea area = new JTextArea(sb.toString());
            area.setEditable(false);
            area.setFont(FONT_UI);
            area.setBackground(CYBER_BG);
            area.setForeground(CYBER_CYAN);
            JScrollPane sp = new JScrollPane(area);
            sp.setPreferredSize(new Dimension(480, 260));
            JOptionPane.showMessageDialog(this, sp, "Achievements", JOptionPane.PLAIN_MESSAGE);
        });
    }

    private void createSelectBtn(JPanel p, String txt, Monster m, Color c) {
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBackground(CYBER_BG);
        btnPanel.setBorder(BorderFactory.createLineBorder(c, 2));
        
        // Monster art/sprite centered
        JTextArea artArea = new JTextArea();
        styleTextArea(artArea, new Font("Monospaced", Font.PLAIN, 14));
        artArea.setText(m.getArt());
        artArea.setForeground(c);
        
        // Wrap in centered panel
        JPanel artWrapper = new JPanel(new BorderLayout());
        artWrapper.setBackground(CYBER_BG);
        JPanel artCenter = new JPanel(new GridBagLayout());
        artCenter.setBackground(CYBER_BG);
        artCenter.add(artArea);
        artWrapper.add(artCenter, BorderLayout.CENTER);
        artWrapper.setPreferredSize(new Dimension(180, 120));
        
        // Button
        JButton btn = new JButton(txt);
        styleCyberButton(btn, c);
        btn.addActionListener(e -> startGame(m));
        
        btnPanel.add(artWrapper, BorderLayout.CENTER);
        btnPanel.add(btn, BorderLayout.SOUTH);
        p.add(btnPanel);
    }

    private void startGame(Monster selectedMonster) {
        this.player = selectedMonster;
        profile.setLastMonster(selectedMonster.getName());
        this.enemy = generateRandomEnemy();
        // configure survival mode
        this.survivalMode = survivalCheck != null && survivalCheck.isSelected();
        this.waveNumber = 1;
        getContentPane().removeAll();
        initBattleUI();
        revalidate();
        repaint();
        log(">> SYSTEM ONLINE. BATTLE LOG INITIATED.", 0);
    }

    private void startNextWave() {
        this.enemy = generateRandomEnemy();
        // adjust enemy bar maximum
        if (enemyHealthBar != null) {
            enemyHealthBar.setMaximum((int)enemy.getMaxHealth());
            enemyHealthBar.setValue((int)enemy.getHealth());
        }
        if (enemyArtArea != null) enemyArtArea.setText(enemy.getArt());
        // update enemy panel title to reflect new name
        if (enemyPanel != null) {
            enemyPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(CYBER_PINK, 1), "[ TARGET: " + enemy.getName() + " ]",
                    TitledBorder.CENTER, TitledBorder.TOP, FONT_UI, CYBER_PINK));
        }
        if (enemyHPText != null) enemyHPText.setText((int)enemy.getHealth() + " / " + (int)enemy.getMaxHealth());
        restartButton.setEnabled(false);
        for (JButton b : moveButtons) if (b != null) b.setEnabled(true);
        if (nextBattleButton != null) nextBattleButton.setEnabled(false);
        updateStatsDisplay();
        log(">> WAVE " + waveNumber + " BEGINS", 0);
    }

    private Monster generateRandomEnemy() {
        int choice = rand.nextInt(4);
        switch (choice) {
            case 0: return new BlazeWolf();
            case 1: return new AquaTurtle();
            case 2: return new TerraGolem();
            default: return new SparkFox();
        }
    }

    // --- BATTLE UI ---
    private void initBattleUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(CYBER_BG);

        // 1. CENTER: DASHBOARD
        JPanel dashboard = new JPanel(new GridLayout(1, 2, 20, 0));
        dashboard.setBackground(CYBER_BG);
        dashboard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create and store panels
        playerPanel = createUnitPanel(player, true);
        enemyPanel = createUnitPanel(enemy, false);

        dashboard.add(playerPanel);
        dashboard.add(enemyPanel);

        add(dashboard, BorderLayout.CENTER);

        // 2. SOUTH: LOG & CONTROLS
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        bottomContainer.setBackground(CYBER_BG);

        battleLog = new JTextPane();
        battleLog.setEditable(false);
        battleLog.setFont(FONT_LOG);
        battleLog.setBackground(CYBER_BG);
        battleLog.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(battleLog);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(CYBER_PINK, 1),
                " BATTLE_LOG ",
                TitledBorder.LEFT, TitledBorder.TOP, FONT_UI, CYBER_PINK));
        scrollPane.getViewport().setBackground(CYBER_BG);
        scrollPane.setPreferredSize(new Dimension(800, 180));

        bottomContainer.add(scrollPane);

        // CONTROLS
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        controls.setBackground(CYBER_BG);

        // Create move buttons (4 moves)
        String[] moves = player.getMoveNames();
        for (int i = 0; i < 4; i++) {
            String label = (moves != null && i < moves.length) ? moves[i] : ("MOVE " + (i+1));
            moveButtons[i] = new JButton(label);
            styleCyberButton(moveButtons[i], i==1 ? CYBER_PINK : CYBER_CYAN);
            final int idx = i;
            moveButtons[i].addActionListener(e -> playTurn(idx));
            controls.add(moveButtons[i]);
        }

        restartButton = new JButton("CHOOSE YOUR POKE");
        styleCyberButton(restartButton, Color.WHITE);
        restartButton.setEnabled(false);
        restartButton.setVisible(false);
        controls.add(restartButton);

        nextBattleButton = new JButton("NEXT BATTLE");
        styleCyberButton(nextBattleButton, new Color(150,255,150));
        nextBattleButton.setEnabled(false);
        nextBattleButton.setVisible(false);
        controls.add(nextBattleButton);

        JButton quitButton = new JButton("QUIT");
        styleCyberButton(quitButton, CYBER_PINK);
        quitButton.addActionListener(e -> showSelectionScreen());
        controls.add(quitButton);

        bottomContainer.add(controls);
        add(bottomContainer, BorderLayout.SOUTH);

        updateStatsDisplay();

        restartButton.addActionListener(e -> showSelectionScreen());
        nextBattleButton.addActionListener(e -> {
            // Heal player and spawn new enemy for next match
            if (player != null) player.resetHealth();
            this.enemy = generateRandomEnemy();
            if (enemy != null) enemy.resetHealth();
            // Update UI
            if (playerHealthBar != null) {
                playerHealthBar.setMaximum((int)player.getMaxHealth());
                playerHealthBar.setValue((int)player.getHealth());
            }
            if (enemyHealthBar != null) {
                enemyHealthBar.setMaximum((int)enemy.getMaxHealth());
                enemyHealthBar.setValue((int)enemy.getHealth());
            }
            if (playerArtArea != null) playerArtArea.setText(player.getArt());
            if (enemyArtArea != null) enemyArtArea.setText(enemy.getArt());
            // update enemy panel title to reflect new name
            if (enemyPanel != null) {
                enemyPanel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(CYBER_PINK, 1), "[ TARGET: " + enemy.getName() + " ]",
                        TitledBorder.CENTER, TitledBorder.TOP, FONT_UI, CYBER_PINK));
            }
            for (JButton b : moveButtons) if (b != null) b.setEnabled(true);
            restartButton.setEnabled(false);
            nextBattleButton.setEnabled(false);
            updateStatsDisplay();
            log(">> NEXT BATTLE STARTED", 0);
        });
    }

    private JPanel createUnitPanel(Monster m, boolean isPlayer) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CYBER_BG);
        String title = isPlayer ? "[ UNIT: " + m.getName() + " ]" : "[ TARGET: " + m.getName() + " ]";
        Color accent = isPlayer ? CYBER_CYAN : CYBER_PINK;

        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(accent, 1), title,
                TitledBorder.CENTER, TitledBorder.TOP, FONT_UI, accent));

        JTextArea artArea = new JTextArea();
        styleTextArea(artArea, FONT_ART);
        artArea.setText(m.getArt());
        artArea.setForeground(accent);
        panel.add(artArea, BorderLayout.CENTER);

        JPanel stats = new JPanel(new GridLayout(2, 1));
        stats.setBackground(CYBER_BG);

        JProgressBar hpBar = new JProgressBar(0, (int)m.getMaxHealth());
        styleProgressBar(hpBar);

        JLabel hpText = new JLabel("HP: 100%");
        styleLabel(hpText);

        stats.add(hpText);
        stats.add(hpBar);

        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setBackground(CYBER_BG);
        statsWrapper.setBorder(BorderFactory.createEmptyBorder(5, 20, 10, 20));
        statsWrapper.add(stats, BorderLayout.CENTER);

        panel.add(statsWrapper, BorderLayout.SOUTH);

        if (isPlayer) {
            playerArtArea = artArea;
            playerHealthBar = hpBar;
            playerHPText = hpText;
        } else {
            enemyArtArea = artArea;
            enemyHealthBar = hpBar;
            enemyHPText = hpText;
        }

        return panel;
    }

    // --- NEW: IMPACT EFFECT SYSTEM ---
    private void triggerDamageEffect(boolean isPlayerTarget) {
        // Decide which panel to shake
        JPanel targetPanel = isPlayerTarget ? playerPanel : enemyPanel;
        JTextArea targetArt = isPlayerTarget ? playerArtArea : enemyArtArea;
        Color originalColor = isPlayerTarget ? CYBER_CYAN : CYBER_PINK;

        // 1. Flash RED
        targetArt.setForeground(DAMAGE_RED);

        // 2. Shake Animation using Timer
        Timer shakeTimer = new Timer(30, new ActionListener() {
            int shakes = 0;
            Point originalLoc = targetPanel.getLocation();

            @Override
            public void actionPerformed(ActionEvent e) {
                if (shakes < 10) { // Shake for 10 frames (approx 300ms)
                    int xOffset = rand.nextInt(10) - 5; // -5 to +5 pixels
                    int yOffset = rand.nextInt(10) - 5;
                    targetPanel.setLocation(originalLoc.x + xOffset, originalLoc.y + yOffset);
                    shakes++;
                } else {
                    // Reset
                    ((Timer)e.getSource()).stop();
                    targetPanel.setLocation(originalLoc); // Snap back
                    targetArt.setForeground(originalColor); // Reset color
                }
            }
        });
        shakeTimer.start();
    }

    // --- GAME LOGIC ---
    private void playTurn(int actionType) {
        if (!player.isAlive() || !enemy.isAlive()) return;

        // --- PLAYER TURN ---
        String[] pmoves = player.getMoveNames();
        String pmoveName = (pmoves != null && actionType < pmoves.length) ? pmoves[actionType] : "MOVE";
        log("> PLAYER USES: " + pmoveName, 1);
        player.performMove(actionType, enemy);
        if (actionType == 1) player.triggerCooldown();

        // Trigger Enemy Hit Effect
        triggerDamageEffect(false);

        player.reduceCooldown();
        enemy.reduceCooldown();
        updateStatsDisplay();

        if (!enemy.isAlive()) {
            log("*** TARGET NEUTRALIZED ***", 0);
            if (survivalMode) {
                // record one win for this wave
                profile.addWin();
                appendBattleHistory("WIN: " + profile.getPlayerName() + " used " + player.getName() + " vs " + enemy.getName() + " (wave " + waveNumber + ")");
                winsLabel.setText("Wins: " + profile.getWins());
                profile.saveToDisk();
                Leaderboard.addEntry(profile.getPlayerName(), 1, 0);
                waveNumber++;
                startNextWave();
                return;
            } else {
                // non-survival: delegate to endGame to handle recording/persistence
                endGame(true);
                return;
            }
        }

        // --- ENEMY TURN ---
        Timer enemyDelay = new Timer(1000, e -> {
            ((Timer)e.getSource()).stop(); // Run once

            if(!enemy.isAlive()) return;

            int enemyMove = rand.nextInt(4);
            if (enemyMove == 1 && !enemy.isSpecialReady()) {
                // fallback
                enemyMove = rand.nextInt(4);
            }

            String[] emoves = enemy.getMoveNames();
            String emoveName = (emoves != null && enemyMove < emoves.length) ? emoves[enemyMove] : "MOVE";
            if (enemyMove == 1 && enemy.isSpecialReady()) {
                log("DETECTED HOSTILE SPECIAL: " + enemy.getSpecialAttackName() + " <", 2);
                enemy.performMove(enemyMove, player);
                enemy.triggerCooldown();
            } else {
                log("HOSTILE USES: " + emoveName + " <", 2);
                enemy.performMove(enemyMove, player);
            }

            // Trigger Player Hit Effect
            triggerDamageEffect(true);

            updateStatsDisplay();

            if (!player.isAlive()) {
                log("*** CRITICAL FAILURE ***", 0);
                // delegate loss recording to endGame to avoid double-count
                endGame(false);
            }
        });
        enemyDelay.start();
    }

    private void updateStatsDisplay() {
        playerArtArea.setText(player.getArt());
        enemyArtArea.setText(enemy.getArt());
        updateBarColor(playerHealthBar, player);
        updateBarColor(enemyHealthBar, enemy);
        playerHPText.setText((int)player.getHealth() + " / " + (int)player.getMaxHealth());
        enemyHPText.setText((int)enemy.getHealth() + " / " + (int)enemy.getMaxHealth());

        // Update move button labels and cooldown for special (index 1)
        String[] moves = player.getMoveNames();
        for (int i = 0; i < moveButtons.length; i++) {
            if (moveButtons[i] == null) continue;
            String lbl = (moves != null && i < moves.length) ? moves[i] : ("MOVE " + (i+1));
            moveButtons[i].setText(lbl);
            moveButtons[i].setEnabled(true);
            if (i == 1) {
                if (player.isSpecialReady()) {
                    moveButtons[i].setEnabled(true);
                    moveButtons[i].setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(CYBER_PINK, 2),
                            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
                } else {
                    moveButtons[i].setEnabled(false);
                    moveButtons[i].setText("RECHARGING [" + player.getCooldown() + "]");
                    moveButtons[i].setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.GRAY, 2),
                            BorderFactory.createEmptyBorder(10, 20, 10, 20)));
                }
            }
        }
    }

    private void updateBarColor(JProgressBar bar, Monster m) {
        bar.setValue((int)m.getHealth());
        double percent = m.getHealth() / m.getMaxHealth();
        if (percent > 0.5) bar.setForeground(Color.GREEN);
        else if (percent > 0.25) bar.setForeground(Color.YELLOW);
        else bar.setForeground(Color.RED);
    }

    private void log(String message, int type) {
        if (battleLog == null) {
            System.out.println(message);
            return;
        }
        StyledDocument doc = battleLog.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();

        StyleConstants.setForeground(style, Color.WHITE);
        StyleConstants.setAlignment(style, StyleConstants.ALIGN_CENTER);

        if (type == 1) {
            StyleConstants.setForeground(style, CYBER_CYAN);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_LEFT);
        } else if (type == 2) {
            StyleConstants.setForeground(style, CYBER_PINK);
            StyleConstants.setAlignment(style, StyleConstants.ALIGN_RIGHT);
        }

        try {
            int length = doc.getLength();
            doc.insertString(length, message + "\n", style);
            doc.setParagraphAttributes(length, message.length(), style, false);
            battleLog.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void endGame(boolean playerWon) {
        for (JButton b : moveButtons) if (b != null) b.setEnabled(false);
        if(playerWon) {
            // record win and persist
            profile.addWin();
            appendBattleHistory("WIN: " + profile.getPlayerName() + " used " + player.getName() + " vs " + enemy.getName() + " (wave " + waveNumber + ")");
            winsLabel.setText("Wins: " + profile.getWins());
            profile.saveToDisk();
            Leaderboard.addEntry(profile.getPlayerName(), 1, 0);
            JOptionPane.showMessageDialog(this, "CONGRATULATIONS!\n\nWins: " + profile.getWins(), "Victory!", JOptionPane.INFORMATION_MESSAGE);
            if (nextBattleButton != null) {
                nextBattleButton.setVisible(!survivalMode);
                if (!survivalMode) nextBattleButton.setEnabled(true);
            }
        } else {
            // record loss and persist
            profile.addLoss();
            appendBattleHistory("LOSS: " + profile.getPlayerName() + " used " + player.getName() + " vs " + enemy.getName());
            winsLabel.setText("Wins: " + profile.getWins());
            lossesLabel.setText("Losses: " + profile.getLosses());
            profile.saveToDisk();
            Leaderboard.addEntry(profile.getPlayerName(), 0, 1);
            JOptionPane.showMessageDialog(this, "DEFEAT!\n\nYou have been defeated.\nChoose a new Poke.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            showSelectionScreen();
        }
    }

    private void appendBattleHistory(String entry) {
        try (java.io.FileWriter fw = new java.io.FileWriter("battle_history.txt", true)) {
            fw.write(java.time.LocalDateTime.now() + " - " + entry + "\n");
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showHistoryDialog() {
        java.io.File f = new java.io.File("battle_history.txt");
        String content = "";
        if (f.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append("\n");
                content = sb.toString();
            } catch (java.io.IOException ex) { ex.printStackTrace(); }
        } else {
            content = "No history.";
        }

        JTextArea area = new JTextArea(content);
        area.setEditable(false);
        area.setFont(FONT_LOG);
        area.setBackground(CYBER_BG);
        area.setForeground(CYBER_CYAN);

        JTextField filterField = new JTextField(20);
        JButton filterBtn = new JButton("Filter");
        JButton exportBtn = new JButton("Export CSV");

        styleCyberButton(filterBtn, CYBER_CYAN);
        styleCyberButton(exportBtn, Color.WHITE);

        final String allContent = content;
        area.setText(allContent);
        filterBtn.addActionListener(e -> {
            String q = filterField.getText().trim().toLowerCase();
            if (q.isEmpty()) {
                area.setText(allContent);
                return;
            }
            StringBuilder sb2 = new StringBuilder();
            for (String line : allContent.split("\n")) {
                if (line.toLowerCase().contains(q)) sb2.append(line).append("\n");
            }
            area.setText(sb2.toString());
        });

        exportBtn.addActionListener(e -> {
            exportHistoryCSV();
            JOptionPane.showMessageDialog(this, "Exported to battle_history.csv", "Export", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(CYBER_BG);
        top.add(new JLabel("Filter:"));
        top.add(filterField);
        top.add(filterBtn);
        top.add(exportBtn);

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(600, 340));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(top, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Battle History", JOptionPane.PLAIN_MESSAGE);
    }

    private void exportHistoryCSV() {
        java.io.File in = new java.io.File("battle_history.txt");
        java.io.File out = new java.io.File("battle_history.csv");
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(in));
             java.io.FileWriter fw = new java.io.FileWriter(out)) {
            fw.write("timestamp,entry\n");
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" - ", 2);
                String ts = parts.length > 0 ? parts[0].replaceAll(",", "") : "";
                String entry = parts.length > 1 ? parts[1].replaceAll(",", "") : "";
                fw.write(String.format("%s,%s\n", ts, entry));
            }
        } catch (java.io.IOException ex) { ex.printStackTrace(); }
    }

    private void showLeaderboardDialog() {
        List<String[]> rows = Leaderboard.aggregateTop(10);
        StringBuilder sb = new StringBuilder();
        sb.append("Rank | Name | Wins | Losses | Ratio\n");
        int r = 1;
        for (String[] row : rows) {
            sb.append(String.format("%d | %s | %s | %s | %s\n", r++, row[0], row[1], row[2], row[3]));
        }
        if (rows.isEmpty()) sb.append("No entries yet.");

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(FONT_UI);
        area.setBackground(CYBER_BG);
        area.setForeground(CYBER_CYAN);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(this, sp, "Leaderboard", JOptionPane.PLAIN_MESSAGE);
    }

    private void showStatsDialog() {
        java.io.File f = new java.io.File("battle_history.txt");
        int total = 0, wins = 0, losses = 0;
        Map<String,Integer> monsterUsage = new HashMap<>();
        if (f.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f))) {
                String line;
                while ((line = br.readLine()) != null) {
                    total++;
                    if (line.contains("WIN:")) wins++;
                    if (line.contains("LOSS:")) losses++;
                    int idx = line.indexOf(" used ");
                    if (idx >= 0) {
                        String rest = line.substring(idx + 6);
                        int vs = rest.indexOf(" vs ");
                        String monster = vs > 0 ? rest.substring(0, vs).trim() : rest.trim();
                        monsterUsage.put(monster, monsterUsage.getOrDefault(monster, 0) + 1);
                    }
                }
            } catch (java.io.IOException ex) { ex.printStackTrace(); }
        }
        double rate = total == 0 ? 0.0 : (wins * 100.0 / total);
        String favorite = "-";
        int best = 0;
        for (Map.Entry<String,Integer> e : monsterUsage.entrySet()) {
            if (e.getValue() > best) { best = e.getValue(); favorite = e.getKey(); }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Player: ").append(profile.getPlayerName()).append("\n");
        sb.append("Total matches: ").append(total).append("\n");
        sb.append("Wins: ").append(wins).append("\n");
        sb.append("Losses: ").append(losses).append("\n");
        sb.append(String.format("Win rate: %.2f%%\n", rate));
        sb.append("Favorite monster: ").append(favorite).append("\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(FONT_UI);
        area.setBackground(CYBER_BG);
        area.setForeground(CYBER_CYAN);
        JOptionPane.showMessageDialog(this, area, "Player Stats", JOptionPane.INFORMATION_MESSAGE);
    }
}