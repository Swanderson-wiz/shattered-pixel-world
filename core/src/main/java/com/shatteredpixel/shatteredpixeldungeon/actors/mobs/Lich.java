package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.sprites.LichSprite;
import com.watabou.utils.Random;

public abstract class Lich extends Mob{

    {
        HP = HT = 1;
            //120;
        defenseSkill = 14;

        EXP = 14;
        maxLvl = 5;
            //30;

        properties.add(Property.UNDEAD);

        HUNTING = new Hunting();
    }

    public boolean summoning = false;
    public int summoningPos = -1;

    public static class GreenLich extends Lich {
        {
            spriteClass = LichSprite.Green.class;
        }
    }

    public static class BlueLich extends Lich {
        {
            spriteClass = LichSprite.Blue.class;
        }
    }

    public static class PurpleLich extends Lich {
        {
            spriteClass = LichSprite.Purple.class;
        }
    }

    public static Class<? extends Lich> random(){
        float roll = Random.Float();
        if (roll < 0.3f){
            return Lich.GreenLich.class;
        } else if (roll < 0.7f){
            return Lich.BlueLich.class;
        } else {
            return Lich.PurpleLich.class;
        }
    }
}
