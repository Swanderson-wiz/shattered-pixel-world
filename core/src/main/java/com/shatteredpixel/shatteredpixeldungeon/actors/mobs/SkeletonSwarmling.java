package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletonSwarmlingSprite;
import com.watabou.utils.Random;

public class SkeletonSwarmling extends Skeleton {

    public static final int MIN_EXPLODE = 3;
    public static final int MAX_EXPLODE = 5;

    {
        spriteClass = SkeletonSwarmlingSprite.class;

        HP = HT = 5;
        defenseSkill = 2;

        EXP = 1;
        maxLvl = 8;

        lootChance = 0;
    }

    @Override
    public int damageRoll() { return Random.NormalIntRange( 2, 5 ); }

    @Override
    public void die( Object cause ) {
        die( cause, SkeletonSwarmling.MIN_EXPLODE, SkeletonSwarmling.MAX_EXPLODE );
    }

}
