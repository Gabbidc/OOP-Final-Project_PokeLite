package com.pokelite.entities;

public abstract class Monster {
    private String name;
    private Element type;
    private double maxHealth;
    private double currentHealth;
    private double baseDamage;

    // --- NEW: COOLDOWN TRACKER ---
    private int cooldown = 0;
    private final int MAX_COOLDOWN = 3; // Number of turns to wait

    public Monster(String name, Element type, double maxHealth, double baseDamage) {
        this.name = name;
        this.type = type;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.baseDamage = baseDamage;
    }

    // Getters
    public String getName() { return name; }
    public Element getType() { return type; }
    public double getHealth() { return currentHealth; }
    public double getMaxHealth() { return maxHealth; }
    public boolean isAlive() { return currentHealth > 0; }
    public double getBaseDamage() { return baseDamage; }

    public void takeDamage(double amount) {
        this.currentHealth -= amount;
        if (this.currentHealth < 0) this.currentHealth = 0;
    }

    // --- NEW: COOLDOWN METHODS ---
    public int getCooldown() { return cooldown; }

    public boolean isSpecialReady() {
        return cooldown == 0;
    }

    public void triggerCooldown() {
        this.cooldown = MAX_COOLDOWN;
    }

    public void reduceCooldown() {
        if (this.cooldown > 0) {
            this.cooldown--;
        }
    }

    // Reset health to max (used for Next Battle)
    public void resetHealth() {
        this.currentHealth = this.maxHealth;
        this.cooldown = 0;
    }

    // Abstract Methods
    public abstract String getSpecialAttackName();
    public abstract double getSpecialAttackPower();
    public abstract String getArt();

    // New: move system - each Monster exposes 4 moves and can perform them by index
    public abstract String[] getMoveNames();
    public abstract void performMove(int index, Monster enemy);

    public void attack(Monster enemy) {
        double multiplier = getTypeMultiplier(this.type, enemy.getType());
        double damage = this.baseDamage * multiplier;
        enemy.takeDamage(damage);
    }

    protected double getTypeMultiplier(Element attacker, Element defender) {
        if (attacker == Element.FIRE && defender == Element.GRASS) return 2.0;
        if (attacker == Element.FIRE && defender == Element.WATER) return 0.5;
        if (attacker == Element.FIRE && defender == Element.ROCK) return 0.5;

        if (attacker == Element.WATER && defender == Element.FIRE) return 2.0;
        if (attacker == Element.WATER && defender == Element.GRASS) return 0.5;
        if (attacker == Element.WATER && defender == Element.ROCK) return 2.0;
        if (attacker == Element.WATER && defender == Element.LIGHTNING) return 0.5;

        if (attacker == Element.GRASS && defender == Element.WATER) return 2.0;
        if (attacker == Element.GRASS && defender == Element.FIRE) return 0.5;
        if (attacker == Element.GRASS && defender == Element.ROCK) return 2.0;

        if (attacker == Element.ROCK && defender == Element.LIGHTNING) return 2.0;
        if (attacker == Element.ROCK && defender == Element.FIRE) return 2.0;
        if (attacker == Element.ROCK && defender == Element.WATER) return 0.5;
        if (attacker == Element.ROCK && defender == Element.GRASS) return 0.5;

        if (attacker == Element.LIGHTNING && defender == Element.WATER) return 2.0;
        if (attacker == Element.LIGHTNING && defender == Element.ROCK) return 0.5;
        if (attacker == Element.LIGHTNING && defender == Element.GRASS) return 0.5;

        return 1.0;
    }
}