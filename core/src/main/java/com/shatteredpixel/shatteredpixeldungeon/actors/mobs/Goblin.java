package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GoblinSprite;
import com.watabou.utils.Random;

public class Goblin extends Mob {

    {
        spriteClass = GoblinSprite.class;

        HP = HT = 12;
        defenseSkill = 2;

        EXP = 1;
        maxLvl = 7;

        loot = Gold.class;
        lootChance = 0.34f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 5);
    }

    @Override
    public int attackSkill( Char target ) {
        return 6;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange( 0, 2 );
    }
}
