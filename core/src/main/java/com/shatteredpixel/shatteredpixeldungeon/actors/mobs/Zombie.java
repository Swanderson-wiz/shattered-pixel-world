package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;


import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ZombieSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;


public class Zombie extends Mob {

    {
        spriteClass = ZombieSprite.class;

        HP = HT = 10;
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

    private int timesDowned = 0;
    private int lives = Random.NormalIntRange( 0, 4 );
    private boolean recovering = false;

    private static final String TIMES_DOWNED = "times_downed";
    private static final String LIVES = "lives";
    private static final String RECOVERING = "recovering";

    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( TIMES_DOWNED, timesDowned );
        bundle.put( LIVES, lives );
        bundle.put( RECOVERING, recovering );
    }

    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        timesDowned = bundle.getInt( TIMES_DOWNED );
        lives = bundle.getInt( LIVES );
        recovering = bundle.getBoolean( RECOVERING );
    }

    public boolean canRevive() { return timesDowned < lives; }

    public int revivedHT() {
        if (timesDowned <= 0) return HT;
        return Math.max(1, Math.round(HT * (1f - (timesDowned * 0.2f))));
    }

    @Override
    protected boolean act() {
        if (recovering){
            int newHT = revivedHT();
            HP = Math.min(newHT, HP+1);
            if (HP >= newHT){
                recovering = false;
                if (sprite instanceof ZombieSprite) ((ZombieSprite) sprite).endCrumple();
            }
            spend(TICK);
            return true;
        }
        return super.act();
    }

    @Override
    public boolean isAlive() {
        if (HP <= 0 && canRevive()){
            HP = 1;
            timesDowned++;

            if (!recovering) {
                recovering = true;
                if (sprite != null) ((ZombieSprite) sprite).crumple();
            }
        }
        return super.isAlive();
    }

}
