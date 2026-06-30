package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpectralKnightSprite;
import com.watabou.utils.Random;

public class SpectralKnight extends Mob {

    {
        spriteClass = SpectralKnightSprite.class;

        HP = HT = 40;
        defenseSkill = 10;

        EXP = 7;
        maxLvl = 14;

        properties.add(Property.UNDEAD);

    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 0, 1 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 1;
    }

}
