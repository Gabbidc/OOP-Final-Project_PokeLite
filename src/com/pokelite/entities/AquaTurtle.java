package com.pokelite.entities;

public class AquaTurtle extends Monster {
    public AquaTurtle() { super("AquaTurtle", Element.WATER, 120, 14); }
    @Override public String getSpecialAttackName() { return "HYDRO PUMP"; }
    @Override public double getSpecialAttackPower() { return 28; }
    @Override public void performMove(int index, Monster enemy) {
        switch(index) {
            case 0:
                attack(enemy);
                break;
            case 1:
                double dmg = getSpecialAttackPower() * getTypeMultiplier(getType(), enemy.getType());
                enemy.takeDamage(dmg);
                break;
            case 2:
                enemy.takeDamage(20 * getTypeMultiplier(getType(), enemy.getType()));
                break;
            case 3:
                enemy.takeDamage(8 * getTypeMultiplier(getType(), enemy.getType()));
                break;
            default:
                attack(enemy);
        }
    }

    @Override
    public String[] getMoveNames() {
        return new String[]{"SHELL TAP", "HYDRO PUMP", "WAVE CRASH", "SPLASH"};
    }

    @Override
    public String getArt() {
        return "    _____\n" +
                "   /  0  \\ [WATER]\n" +
                "  |  [_]  |\n" +
                "   \\_____/\n" +
                "   //   \\\\";
    }
}