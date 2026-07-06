package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.TrainingDummySprite;

public class TrainingDummy extends Mob{

    {
        spriteClass = TrainingDummySprite.class;

        HP = HT = 9999;
        defenseSkill = 0;

        maxLvl = -2;

        state = PASSIVE;
    }

    @Override
    protected boolean act() {
        //char logic
        if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()) {
            fieldOfView = new boolean[Dungeon.level.length()];
        }
        Dungeon.level.updateFieldOfView(this, fieldOfView);

        throwItems();
        sprite.hideSleep();

        //mob logic
        enemy = chooseEnemy();

        enemySeen = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
        //end of char/mob logic

        spend(TICK);
        return true;
    }
}
