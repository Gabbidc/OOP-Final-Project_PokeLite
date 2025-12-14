package com.pokelite.entities;

public class SparkFox extends Monster {
    public SparkFox() { super("SparkFox", Element.LIGHTNING, 105, 16); }
    @Override public String getSpecialAttackName() { return "THUNDERBOLT"; }
    @Override public double getSpecialAttackPower() { return 32; }
    @Override public void performMove(int index, Monster enemy) {
        switch(index) {
            case 0:
                attack(enemy);
                break;
            case 1:
                double damage = getSpecialAttackPower() * getTypeMultiplier(getType(), enemy.getType());
                enemy.takeDamage(damage);
                break;
            case 2:
                enemy.takeDamage(18 * getTypeMultiplier(getType(), enemy.getType()));
                break;
            case 3:
                enemy.takeDamage(22 * getTypeMultiplier(getType(), enemy.getType()));
                break;
            default:
                attack(enemy);
        }
    }

    @Override
    public String[] getMoveNames() {
        return new String[]{"SHOCK", "THUNDERBOLT", "SPARK JUMP", "VOLT CLAW"};
    }

    @Override
    public String getArt() {
        return "    /\\ /\\\n" +
                "   ( >.< ) [ZAP!]\n" +
                "    )   ( //\n" +
                "   (  _  )//\n" +
                "    \"\" \"\"";
    }
}