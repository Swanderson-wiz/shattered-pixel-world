package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletalAbominationSprite;
import com.watabou.utils.Random;

public class SkeletalAbomination extends Skeleton {

    public static final int MIN_EXPLODE = 12;
    public static final int MAX_EXPLODE = 24;

    {
        spriteClass = SkeletalAbominationSprite.class;
        /*Testing will be required to lock down where exactly
          its stats should be at.
        */

        HP = HT = 60;
        defenseSkill = 16;

        EXP = 10;
        maxLvl = 17;

        lootChance = 0;

        properties.add(Property.LARGE);
    }

    //@Override
    //public int damageRoll() { return Random.NormalIntRange( 15, 25 ); }

    @Override
    public int attackSkill( Char target ) { return 20; }

    @Override
    public void die( Object cause ) {
        die( cause, SkeletalAbomination.MIN_EXPLODE, SkeletalAbomination.MAX_EXPLODE );
    }

}
