package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.*;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class Lich extends Mob{

    {
        HP = HT = 1;
            //120;
        defenseSkill = 14;

        EXP = 14;
        maxLvl = 5;
            //30;

        properties.add(Property.UNDEAD);

        HUNTING = new Hunting();
    }

    public boolean summoning = false;
    public int summoningPos = -1;

    protected boolean firstSummon = true;

    private LichSkeleton currSkeleton = null;
    private int storedSkeletonID = -1;
    private final ArrayList<LichSkeleton> mySkeletons = new ArrayList<>();
    private final ArrayList<Integer> storedSkeletonIDs = new ArrayList<>();
    private final int BLUE_LIMIT = 2;
    private final int PURPLE_LIMIT = 5;


    @Override
    protected boolean act() {
        if (summoning && state != HUNTING){
            summoning = false;
            if (sprite instanceof LichSprite) ((LichSprite) sprite).cancelSummoning();
        }
        return super.act();
    }

    @Override
    public void aggro(Char ch) {
        super.aggro(ch);
        for ( LichSkeleton skeleton : mySkeletons) {
            if (skeleton != null && skeleton.isAlive()
                    && Dungeon.level.mobs.contains(skeleton)
                    && skeleton.alignment == alignment) {
                skeleton.aggro(ch);
            }
        }
    }


    @Override
    public void die(Object cause) {
        for ( LichSkeleton skeleton : mySkeletons ) {
            if (skeleton != null && skeleton.isAlive() && skeleton.alignment == alignment) {
                skeleton.die( null );
            }
        }
        if (currSkeleton != null && currSkeleton.isAlive() && currSkeleton.alignment == alignment){
            currSkeleton.die(null);
        }

        super.die(cause);
    }

    @Override
    protected boolean canAttack(Char enemy) { return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos; }

    protected boolean doAttack(Char enemy ) {
        if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
            sprite.zap(enemy.pos);
            return false;
        } else {
            zap();
            return true;
        }
    }

    public static class EarthenBolt{}

    protected abstract void debuff( Char enemy );

    private void zap() {
        spend( 1f );

        Invisibility.dispel(this);
        Char enemy = this.enemy;
        if (hit( this, enemy, true )) {

            if (Random.Int( 2 ) == 0) {
                debuff( enemy );
                if (enemy == Dungeon.hero) Sample.INSTANCE.play( Assets.Sounds.DEBUFF );
            }

            int dmg = Random.NormalIntRange( 0,1);
                    //6, 15 );
            dmg = Math.round(dmg * AscensionChallenge.statModifier(this));
            enemy.damage( dmg, new Lich.EarthenBolt() );

            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail( this );
                GLog.n( Messages.get(this, "bolt_kill") );
            }
        } else {
            enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
        }
    }

    private static final String SUMMONING = "summoning";
    private static final String FIRST_SUMMON = "first_summon";
    private static final String SUMMONING_POS = "summoning_pos";
    private static final String MY_SKELETON = "my_skeleton";
    private static final String STORED_SKELETON_IDS = "stored_skeleton_ids";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put( SUMMONING, summoning );
        bundle.put( FIRST_SUMMON, firstSummon );
        if (summoning){
            bundle.put( SUMMONING_POS, summoningPos);
        }
        if (currSkeleton != null){
            bundle.put( MY_SKELETON, currSkeleton.id() );
        } else if (storedSkeletonID != -1){
            bundle.put( MY_SKELETON, storedSkeletonID );
        }
        int[] skeletonIDArr = new int[mySkeletons.size()];
        int i = 0;
        for (LichSkeleton skeleton : mySkeletons){
            if (skeleton != null) {
                skeletonIDArr[i] = skeleton.id();
            }
            i++;
        }
        bundle.put(STORED_SKELETON_IDS, skeletonIDArr);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        summoning = bundle.getBoolean( SUMMONING );
        if (bundle.contains(FIRST_SUMMON)) firstSummon = bundle.getBoolean(FIRST_SUMMON);
        if (summoning){
            summoningPos = bundle.getInt( SUMMONING_POS );
        }
        if (bundle.contains( MY_SKELETON )){
            storedSkeletonID = bundle.getInt( MY_SKELETON );
        }
        if (bundle.contains( STORED_SKELETON_IDS)) {
            storedSkeletonIDs.clear();
            for (int i : bundle.getIntArray(STORED_SKELETON_IDS)){
                storedSkeletonIDs.add(i);
            }
        }
    }



    public void onZapComplete(){
        for (LichSkeleton skeleton : mySkeletons) {
            if (currSkeleton == null || skeleton.HP < currSkeleton.HP) {
                currSkeleton = skeleton;
            }
        }
        if (currSkeleton == null || currSkeleton.sprite == null || !currSkeleton.isAlive()){
            return;
        }
        //heal skeleton first, unless it's a swarmling
        if (currSkeleton.HP < currSkeleton.HT && !(currSkeleton instanceof LichSwarmling)) {

            if (sprite.visible || currSkeleton.sprite.visible) {
                sprite.parent.add(new Beam.HealthRay(sprite.center(), currSkeleton.sprite.center()));
                Sample.INSTANCE.play(Assets.Sounds.RAY);
            }

            currSkeleton.HP = Math.min(currSkeleton.HP + currSkeleton.HT / 5, currSkeleton.HT);
            if (currSkeleton.sprite.visible) {
                currSkeleton.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(currSkeleton.HT / 5), FloatingText.HEALING);
            }
            //otherwise give it adrenaline
        } else {
            for (LichSkeleton skeleton : mySkeletons) {
                if (skeleton.buff(Adrenaline.class) == null) {
                    if (sprite.visible || skeleton.sprite.visible) {
                        sprite.parent.add(new Beam.HealthRay(sprite.center(), skeleton.sprite.center()));
                        Sample.INSTANCE.play(Assets.Sounds.RAY);
                    }
                    Buff.affect(skeleton, Adrenaline.class, 3f);
                    break;
                }
            }
        }
        next();
    }


    public void summonMinion(){
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
                    blocker.damage( Random.NormalIntRange(2, 10), new Lich.SummoningBlockDamage() );
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


        if (mySkeletons.size() <= BLUE_LIMIT && isBlue()) {
            currSkeleton = new LichSkeleton();
            createMinion();
        } else if ((mySkeletons.isEmpty() || currSkeleton == null || !currSkeleton.isActive())
                && isGreen()) {
            currSkeleton = new LichAbomination();
            createMinion();
        } else if (mySkeletons.size() <= PURPLE_LIMIT && isPurple()) {
            currSkeleton = new LichSwarmling();
            createMinion();
        } else {
            ScrollOfTeleportation.appear(currSkeleton, summoningPos);
        }

        ((LichSprite)sprite).finishSummoning();
    }

    public void createMinion() {
        currSkeleton.pos = summoningPos;
        GameScene.add(currSkeleton);
        Dungeon.level.occupyCell(currSkeleton);
        mySkeletons.add ( currSkeleton );
        storedSkeletonIDs.add( currSkeleton.id() );


        for (Buff b : buffs()){
            if (b.revivePersists) {
                Buff.affect(currSkeleton, b.getClass());
            }
        }
    }

    public static class SummoningBlockDamage{}

    private class Hunting extends Mob.Hunting{

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;

            if (enemySeen){
                target = enemy.pos;
            }

            if (storedSkeletonID != -1){
                Actor ch = Actor.findById(storedSkeletonID);
                storedSkeletonID = -1;
                if (ch instanceof LichSkeleton){
                    currSkeleton = (LichSkeleton) ch;
                }
            }
            if (!storedSkeletonIDs.isEmpty()) {
                for (int skeletonid : storedSkeletonIDs) {
                    if (skeletonid != -1) {
                        Actor skeleton = Actor.findById(skeletonid);
                        if (skeleton instanceof LichSkeleton) {
                            mySkeletons.add((LichSkeleton) skeleton);
                        }
                    }
                }
                storedSkeletonIDs.clear();
            }


            if (summoning){
                summonMinion();
                return true;
            }


            if (currSkeleton != null &&
                    (!currSkeleton.isAlive()
                            || !Dungeon.level.mobs.contains(currSkeleton)
                            || currSkeleton.alignment != alignment)){
                mySkeletons.remove( currSkeleton );
                currSkeleton = null;
                for ( LichSkeleton skeleton : mySkeletons ) {
                    if (skeleton != null &&
                            (skeleton.isAlive()
                                    && Dungeon.level.mobs.contains(skeleton)
                                    && skeleton.alignment == alignment)){
                        currSkeleton = skeleton;
                        break;
                    }
                }
            }


            //if enemy is seen, and enemy is within range, and we have no skeleton, summon a skeleton!
            if (enemySeen
                    && Dungeon.level.distance(pos, enemy.pos) <= 4
                    && ((mySkeletons.isEmpty() && isGreen())
                    || (mySkeletons.size() <= BLUE_LIMIT && isBlue())
                    || (mySkeletons.size() <= PURPLE_LIMIT && isPurple()))){

                summoningPos = -1;

                //we can summon around blocking terrain, but not through it, except unlocked doors
                boolean[] passable = BArray.not(Dungeon.level.solid, null);
                BArray.or(Dungeon.level.passable, passable, passable);
                PathFinder.buildDistanceMap(pos, passable, Dungeon.level.distance(pos, enemy.pos)+3);

                for (int c : PathFinder.NEIGHBOURS8){
                    if (Actor.findChar(enemy.pos+c) == null
                            && PathFinder.distance[enemy.pos+c] != Integer.MAX_VALUE
                            && Dungeon.level.passable[enemy.pos+c]
                            && (!hasProp(Lich.this, Property.LARGE) || Dungeon.level.openSpace[enemy.pos+c])
                            && fieldOfView[enemy.pos+c]
                            && Dungeon.level.trueDistance(pos, enemy.pos+c) < Dungeon.level.trueDistance(pos, summoningPos)){
                        summoningPos = enemy.pos+c;
                    }
                }

                if (summoningPos != -1){

                    summoning = true;
                    sprite.zap( summoningPos );

                    if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[summoningPos]){
                        Dungeon.hero.interrupt();
                    }

                    spend( firstSummon ? TICK : 2*TICK );
                } else {
                    //wait for a turn
                    spend(TICK);
                }

                return true;
                //otherwise, if enemy is seen, and we have a skeleton...
            } else if (enemySeen
                    && ((!mySkeletons.isEmpty() && isGreen())
                    || (mySkeletons.size() >= BLUE_LIMIT && isBlue())
                    || (mySkeletons.size() >= PURPLE_LIMIT && isPurple()))){

                spend(TICK);

                boolean teleporting = false;
                //teleport our skeleton to the enemy if..
                //we can't see it
                if (!fieldOfView[currSkeleton.pos]){
                    teleporting = true;

                    //it has a relatively long path to reach the hero (e.g. it's blocked in a tunnelway)
                } else if (!currSkeleton.canAttack(enemy)){
                    PathFinder.Path skelePath = Dungeon.findPath(currSkeleton, enemy.pos, Dungeon.level.passable, fieldOfView, true);

                    if (skelePath == null || skelePath.size() > 2*Dungeon.level.distance(pos, enemy.pos)){
                        teleporting = true;
                    }
                }

                if (teleporting){

                    //teleport them to the closest spot next to the enemy that can be seen
                    if (!Dungeon.level.adjacent(currSkeleton.pos, enemy.pos)){
                        int telePos = -1;
                        for (int c : PathFinder.NEIGHBOURS8){
                            if (Actor.findChar(enemy.pos+c) == null
                                    && Dungeon.level.passable[enemy.pos+c]
                                    && fieldOfView[enemy.pos+c]
                                    && (Dungeon.level.openSpace[enemy.pos+c] || !Char.hasProp(currSkeleton, Property.LARGE))
                                    && Dungeon.level.trueDistance(pos, enemy.pos+c) < Dungeon.level.trueDistance(pos, telePos)){
                                telePos = enemy.pos+c;
                            }
                        }

                        if (telePos != -1){

                            if (sprite != null && sprite.visible) {
                                summoning = true;
                                summoningPos = telePos;
                                sprite.zap(telePos);
                                if (Dungeon.level.heroFOV[pos] || Dungeon.level.heroFOV[summoningPos]) {
                                    Dungeon.hero.interrupt();
                                }
                                spend(TICK); //2 ticks total, it can't be the first summon
                            }
                        }
                    }

                    return true;

                } else {

                    //zap skeleton
                    if (currSkeleton.HP < currSkeleton.HT || currSkeleton.buff(Adrenaline.class) == null) {
                        if (sprite != null && sprite.visible){
                            sprite.zap(currSkeleton.pos);
                            return false;
                        } else {
                            onZapComplete();
                        }
                    }

                }

                return true;

                //otherwise, default to regular hunting behaviour
            } else {
                return super.act(enemyInFOV, justAlerted);
            }
        }
    }

    public static class LichSkeleton extends Skeleton {

        {
            state = WANDERING;

            spriteClass = LichSkeletonSprite.class;

            //no loot or exp
            maxLvl = -5;

            //20/25 health to start
            HT = 65;
            HP = 50;
        }

        //When we're done testing, this will boost the skeleton's damage
        //@Override
        // public int damageRoll() { return Random.NormalIntRange( 20, 30 ); }

        @Override
        public float spawningWeight() {
            return 0;
        }

        public static class LichSkeletonSprite extends SkeletonSprite{

            public LichSkeletonSprite(){
                super();
                brightness(0.75f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                brightness(0.75f);
            }
        }

    }

    public static class LichAbomination extends LichSkeleton {

        {
            state = WANDERING;
            spriteClass = LichAbominationSprite.class;

            //no loot or exp
            maxLvl = -5;

            //20/25 health to start
            HT = 120;
            HP = 100;

            properties.add(Property.LARGE);
        }

        @Override
        public String description() { return Messages.get( SkeletalAbomination.class, "desc" ); }
        @Override
        public String name() { return Messages.get( SkeletalAbomination.class, "name"); }

        //When we're done testing, this will boost the skeleton's damage
        //@Override
        // public int damageRoll() { return Random.NormalIntRange( 30, 35 ); }


        public static class LichAbominationSprite extends SkeletalAbominationSprite {

            public LichAbominationSprite(){
                super();
                brightness(0.75f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                brightness(0.75f);
            }
        }

    }

    public static class LichSwarmling extends LichSkeleton {

        {
            state = WANDERING;

            spriteClass = LichSwarmlingSprite.class;

            //no loot or exp
            maxLvl = -5;

            HP = HT = 20;
        }

        //When we're done testing, this will boost the swarmling's damage
        //@Override
        // public int damageRoll() { return Random.NormalIntRange( 15, 20 ); }

        @Override
        public String description() { return Messages.get( SkeletonSwarmling.class, "desc" ); }
        @Override
        public String name() { return Messages.get( SkeletonSwarmling.class, "name"); }

        public static class LichSwarmlingSprite extends SkeletonSwarmlingSprite {

            public LichSwarmlingSprite(){
                super();
                brightness(0.75f);
            }

            @Override
            public void resetColor() {
                super.resetColor();
                brightness(0.75f);
            }
        }

    }

    public static class GreenLich extends Lich {
        {
            spriteClass = LichSprite.Green.class;
        }

        protected void debuff( Char enemy ) { Buff.affect( enemy, Ooze.class).set(Ooze.DURATION ); }
    }

    public static class BlueLich extends Lich {
        {
            spriteClass = LichSprite.Blue.class;
        }
        protected void debuff( Char enemy ) { Buff.prolong( enemy, Vulnerable.class, Vulnerable.DURATION ); }
    }

    public static class PurpleLich extends Lich {
        {
            spriteClass = LichSprite.Purple.class;
        }
        protected void debuff( Char enemy ) {
            Buff.prolong( enemy, Hex.class, Hex.DURATION );
        }
    }

    private boolean isGreen() {
        return spriteClass == LichSprite.Green.class;
    }

    private boolean isBlue() {
        return spriteClass == LichSprite.Blue.class;
    }

    private boolean isPurple() {
        return spriteClass == LichSprite.Purple.class;
    }

    public static Class<? extends Lich> random(){
        float roll = Random.Float();
        if (roll < 0.3f){
            return Lich.GreenLich.class;
        } else if (roll < 0.7f){
            return Lich.BlueLich.class;
        } else {
            return Lich.PurpleLich.class;
        }
    }

}
