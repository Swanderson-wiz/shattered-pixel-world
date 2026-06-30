package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SprigganSprite;
import com.watabou.utils.Random;

public class Spriggan extends Mob {
    {
        spriteClass = SprigganSprite.class;

        HP = HT = 1;
        defenseSkill = 1;

        EXP = 1;
        maxLvl = 1;

        //Need to make them flammable at some point
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
