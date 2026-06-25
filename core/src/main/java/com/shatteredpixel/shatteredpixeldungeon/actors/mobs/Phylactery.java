package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.NecromancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PhylacterySprite;
import com.watabou.utils.*;

public abstract class Phylactery extends Mob {

    {
        HP = HT = 1;//50;
        
        maxLvl = -2;
        
        properties.add(Property.INORGANIC);
        properties.add(Property.STATIC);
        properties.add(Property.IMMOVABLE);

        state = PASSIVE;
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
                && myLich.alignment == alignment
                && !firstSummon){
            myLich.aggro(ch);
        }
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
        //char logic
        if (fieldOfView == null || fieldOfView.length != Dungeon.level.length()){
            fieldOfView = new boolean[Dungeon.level.length()];
        }
        Dungeon.level.updateFieldOfView( this, fieldOfView );

        throwItems();

        sprite.hideEmo();

        //mob logic
        enemy = chooseEnemy();

        enemySeen = enemy != null && enemy.isAlive() && fieldOfView[enemy.pos] && enemy.invisible <= 0;
        //end of char/mob logic

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
            summoning = true;
            findPos();
            if (sprite != null && sprite.visible) {
                sprite.zap(summoningPos);
            }
            firstSummon = false;
            return true;
        } else if ( myLich == null || myLich.sprite == null || !myLich.isAlive()) {
            summoning = true;
            findPos();
            if (sprite != null && sprite.visible) {
                sprite.zap(summoningPos);
            }
            spend(5*TICK);
            return true;
        }

        return true;
    }

    public void summonMinion() {
        if (summoningPos != -1) {
            if (myLich == null || !myLich.isActive()) {
                myLich = lichColor();
                myLich.pos = summoningPos;
                GameScene.add(myLich);
                Dungeon.level.occupyCell(myLich);
                for (Buff b : buffs()) {
                    if (b.revivePersists) {
                        Buff.affect(myLich, b.getClass());
                    }
                }
            }
            summoning = firstSummon = false;
        }
        ((PhylacterySprite)sprite).finishSummoning();
    }

    private void findPos() {
        for (int i : PathFinder.NEIGHBOURS8) {
            if (Actor.findChar(pos + i) == null && summoningPos == -1
                    && Dungeon.level.passable[pos + i]
                    && (!Char.hasProp(this, Property.LARGE) || Dungeon.level.openSpace[i])) {
                summoningPos = pos + i;
                break;
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
