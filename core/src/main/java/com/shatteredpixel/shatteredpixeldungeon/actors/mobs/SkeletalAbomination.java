package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SkeletalAbominationSprite;
import com.watabou.utils.Random;

public class SkeletalAbomination extends Skeleton {

    {
        spriteClass = SkeletalAbominationSprite.class;
        /*Testing will be required to lock down where exactly
          its stats should be at.
        */

        HP = HT = 60;
        defenseSkill = 16;

        EXP = 10;
        maxLvl = 17;


        properties.add(Property.LARGE);
    }

    @Override
    public int damageRoll() { return Random.NormalIntRange( 15, 25 ); }

    @Override
    public int attackSkill( Char target ) { return 20; }
}
