package com.pokelite.entities;

public class BlazeWolf extends Monster {
    public BlazeWolf() { super("BlazeWolf", Element.FIRE, 100, 18); }
    @Override public String getSpecialAttackName() { return "FLAME BURST"; }
    @Override public double getSpecialAttackPower() { return 30; }
    @Override public void performMove(int index, Monster enemy) {
        switch(index) {
            case 0: // Basic Bite
                attack(enemy);
                break;
            case 1: // Special Flame Burst
                double damage = getSpecialAttackPower() * getTypeMultiplier(getType(), enemy.getType());
                enemy.takeDamage(damage);
                break;
            case 2: // Howl - lighter damage
                enemy.takeDamage(10 * getTypeMultiplier(getType(), enemy.getType()));
                break;
            case 3: // Fire Claw - heavier physical
                enemy.takeDamage(24 * getTypeMultiplier(getType(), enemy.getType()));
                break;
            default:
                attack(enemy);
        }
    }

    @Override
    public String[] getMoveNames() {
        return new String[]{"BITE", "FLAME BURST", "HOWL", "FIRE CLAW"};
    }

    @Override
    public String getArt() {
        return "   /\\_/\\  \n" +
                "  ( o.o ) [FIRE]\n" +
                "   > ^ <  \n" +
                "  /  ~  \\ \n" +
                " |_______|";
    }
}