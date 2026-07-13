package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WolfSprite;
import com.watabou.utils.Random;

public class Wolf extends Mob {

    {
        spriteClass = WolfSprite.class;

        HP = HT = 1;//for now
        defenseSkill = 1;//
        baseSpeed = 2f;

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
