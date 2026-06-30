package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.sprites.SpectralKnightSprite;

public class SpectralKnight extends Mob {

    {
        spriteClass = SpectralKnightSprite.class;

        HP = HT = 1;//40;
        defenseSkill = 1;//10;

        EXP = 7;
        maxLvl = 14;

        properties.add(Property.UNDEAD);

    }
}
