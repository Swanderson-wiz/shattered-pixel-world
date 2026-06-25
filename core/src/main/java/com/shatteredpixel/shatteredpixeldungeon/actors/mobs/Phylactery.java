package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PhylacterySprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public abstract class Phylactery extends Mob {

    {
        HP = HT = 1;//50;
        
        maxLvl = -2;
        
        properties.add(Property.INORGANIC);
        properties.add(Property.STATIC);
        properties.add(Property.IMMOVABLE);
    }

    public boolean summoning = false;
    public int summoningPos = -1;

    protected boolean firstSummon = true;

    private Lich myLich;
    private int storedLichID = -1;

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    @Override
    public void aggro(Char ch) {
        super.aggro(ch);
        if (myLich != null && myLich.isAlive()
                && Dungeon.level.mobs.contains(myLich)
                && myLich.alignment == alignment){
            myLich.aggro(ch);
        }
    }

    @Override
    public void die(Object cause) {
        if (storedLichID != -1){
            Actor ch = Actor.findById(storedLichID);
            storedLichID = -1;
            if (ch instanceof Lich){
                myLich = (Lich) ch;
            }
        }

        if (myLich != null && myLich.isAlive() && myLich.alignment == alignment){
            myLich.die(null);
        }

        super.die(cause);
    }

    private static final String SUMMONING = "summoning";
    private static final String FIRST_SUMMON = "first_summon";
    private static final String SUMMONING_POS = "summoning_pos";
    private static final String MY_LICH = "my_lich";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( SUMMONING, summoning );
        bundle.put( FIRST_SUMMON, firstSummon );
        if (summoning){
            bundle.put( SUMMONING_POS, summoningPos);
        }
        if (myLich != null){
            bundle.put(MY_LICH, myLich.id() );
        } else if (storedLichID != -1){
            bundle.put(MY_LICH, storedLichID);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        summoning = bundle.getBoolean( SUMMONING );
        if (bundle.contains(FIRST_SUMMON)) firstSummon = bundle.getBoolean(FIRST_SUMMON);
        if (summoning){
            summoningPos = bundle.getInt( SUMMONING_POS );
        }
        if (bundle.contains(MY_LICH)){
            storedLichID = bundle.getInt(MY_LICH);
        }
    }
    
    @Override
    protected boolean act() {
        throwItems();

        sprite.hideAlert();
        sprite.hideLost();


        if (storedLichID != -1){
            Actor ch = Actor.findById(storedLichID);
            storedLichID = -1;
            if (ch instanceof Lich){
                myLich = (Lich) ch;
            }
        }

        if (myLich != null &&
                (!myLich.isAlive()
                        || !Dungeon.level.mobs.contains(myLich)
                        || myLich.alignment != alignment)){
            myLich = null;
        }

        if (summoning){
            summonMinion();
            return true;
        }

        if (!firstSummon && myLich != null && myLich.sprite != null && myLich.isAlive()) {
            spend(TICK);
            return true;
        }
        if (firstSummon) {
            summoningPos = -1;
            summoning = true;
            spend(TICK);
            return true;
        } else if ( myLich == null || myLich.sprite == null || !myLich.isAlive()) {
            summoningPos = -1;
            summoning = true;
            spend(TICK*5);
            return true;
        }

        return true;
    }

    public void summonMinion() {
        //we can summon around blocking terrain, but not through it, except unlocked doors
        boolean[] passable = BArray.not(Dungeon.level.solid, null);
        BArray.or(Dungeon.level.passable, passable, passable);

        for (int c : PathFinder.NEIGHBOURS8){
            if (Actor.findChar(this.pos+c) == null
                    && PathFinder.distance[this.pos+c] != Integer.MAX_VALUE
                    && Dungeon.level.passable[this.pos+c]
                    && fieldOfView[enemy.pos+c]){
                summoningPos = this.pos+c;
            }
        }
        if (Actor.findChar(summoningPos) != null || !Dungeon.level.passable[summoningPos]) {

            int pushPos = pos;
            for (int c : PathFinder.NEIGHBOURS8) {
                if (Actor.findChar(summoningPos + c) == null
                        && Dungeon.level.passable[summoningPos + c]
                        && (Dungeon.level.openSpace[summoningPos + c] || !hasProp(Actor.findChar(summoningPos), Property.LARGE))
                        && Dungeon.level.trueDistance(pos, summoningPos + c) > Dungeon.level.trueDistance(pos, pushPos)) {
                    pushPos = summoningPos + c;
                }
            }

            //push enemy, or wait a turn if there is no valid pushing position
            if (pushPos != pos) {

                Char ch = Actor.findChar(summoningPos);
                //no push if char is immovable, move our skeleton instead
                if (ch == null || Char.hasProp(ch, Property.IMMOVABLE)){
                    summoningPos = pushPos;
                } else {
                    Actor.add(new Pushing(ch, ch.pos, pushPos));

                    ch.pos = pushPos;
                    Dungeon.level.occupyCell(ch);
                }

            } else {

                //attempt to damage the blocker in addition to waiting
                Char blocker = Actor.findChar(summoningPos);
                if (blocker != null && blocker.alignment != alignment){
                    blocker.damage( Random.NormalIntRange(2, 10), new Necromancer.SummoningBlockDamage() );
                    if (blocker == Dungeon.hero && !blocker.isAlive()){
                        Badges.validateDeathFromEnemyMagic();
                        Dungeon.fail(this);
                        GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
                    }
                }

                spend(TICK);
                return;
            }
        }

        summoning = firstSummon = false;

        if (myLich == null || !myLich.isActive()) {
            myLich = lichColor();
            myLich.pos = summoningPos;
            GameScene.add(myLich);
            Dungeon.level.occupyCell(myLich);

            for (Buff b : buffs()){
                if (b.revivePersists) {
                    Buff.affect(myLich, b.getClass());
                }
            }
        }
    }

    protected abstract Lich lichColor();

    public static class GreenPhylactery extends Phylactery {
        {
            spriteClass = PhylacterySprite.Green.class;
        }

        @Override
        protected Lich lichColor() {
            return new Lich.GreenLich();
        }
    }

    public static class BluePhylactery extends Phylactery {
        {
            spriteClass = PhylacterySprite.Blue.class;
        }

        @Override
        protected Lich lichColor() {
            return new Lich.BlueLich();
        }
    }

    public static class PurplePhylactery extends Phylactery {
        {
            spriteClass = PhylacterySprite.Purple.class;
        }

        @Override
        protected Lich lichColor() {
            return new Lich.PurpleLich();
        }
    }

    public static Class<? extends Phylactery> random(){
        float roll = Random.Float();
        if (roll < 0.3f){
            return Phylactery.GreenPhylactery.class;
        } else if (roll < 0.7f){
            return Phylactery.BluePhylactery.class;
        } else {
            return Phylactery.PurplePhylactery.class;
        }
    }
}
