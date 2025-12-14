package com.pokelite.entities;

public class TerraGolem extends Monster {
    public TerraGolem() { super("TerraGolem", Element.ROCK, 150, 12); }
    @Override public String getSpecialAttackName() { return "ROCK SMASH"; }
    @Override public double getSpecialAttackPower() { return 22; }
    @Override public void performMove(int index, Monster enemy) {
        switch(index) {
            case 0:
                attack(enemy);
                break;
            case 1:
                enemy.takeDamage(getSpecialAttackPower());
                break;
            case 2:
                enemy.takeDamage(16);
                break;
            case 3:
                enemy.takeDamage(28);
                break;
            default:
                attack(enemy);
        }
    }

    @Override
    public String[] getMoveNames() {
        return new String[]{"PUNCH", "ROCK SMASH", "ROLL", "EARTH CRUSH"};
    }

    @Override
    public String getArt() {
        return "   [_____]\n" +
                "   | o o | [ROCK]\n" +
                "  /|  _  |\\\n" +
                " / |_____| \\\n" +
                "   /     \\ ";
    }
}