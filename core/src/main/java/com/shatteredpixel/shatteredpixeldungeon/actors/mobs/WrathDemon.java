package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WrathDemonSprite;
import com.watabou.utils.Random;

public class WrathDemon extends Mob {

    {
        spriteClass = WrathDemonSprite.class;

        HP = HT = 1;//for now
        defenseSkill = 1;//

        EXP = 1;
        maxLvl = 1;

    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 0, 1 );
    }

    @Override
    public int attackSkill( Char target ) {
        return 10;
    }
}
