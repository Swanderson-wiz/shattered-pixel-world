package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ZombieSprite;
import com.watabou.utils.Random;

public class Zombie extends Mob {

    {
        spriteClass = ZombieSprite.class;

        HP = HT = 1;
        defenseSkill = 1;

        EXP = 1;
        maxLvl = 1;

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
