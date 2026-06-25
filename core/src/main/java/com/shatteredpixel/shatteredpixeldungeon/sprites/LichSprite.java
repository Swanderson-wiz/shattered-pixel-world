package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Lich;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;


public abstract class LichSprite extends MobSprite {

    protected Animation charging;
    protected Emitter summoningBones;
    protected abstract int texOffset();
    protected int boltType;

    public LichSprite() {
        super();

        int c = texOffset();

        texture( Assets.Sprites.LICH );
        TextureFilm frames = new TextureFilm( texture, 16, 16 );

        idle = new Animation( 1, true );
        idle.frames( frames, c+0, c+0, c+0, c+1, c+0, c+0, c+0, c+0, c+1 );

        run = new Animation( 8, true );
        run.frames( frames, c+0, c+0, c+0, c+2, c+3, c+4 );

        attack = new Animation( 10, false );
        attack.frames( frames, c+5, c+6, c+7, c+8 );

        zap = attack.clone();

        charging = new Animation( 5, true );
        charging.frames( frames, c+8, c+9 );

        die = new Animation( 10, false );
        die.frames( frames, c+10, c+11, c+12, c+13 );

        play( idle );
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (ch instanceof Lich && ((Lich) ch).summoning){
            zap(((Lich) ch).summoningPos);
        }
    }

    @Override
    public void update() {
        super.update();
        if (summoningBones != null && ((Lich) ch).summoningPos != -1){
            summoningBones.visible = Dungeon.level.heroFOV[((Lich) ch).summoningPos];
        }
    }

    @Override
    public void die() {
        super.die();
        if (summoningBones != null){
            summoningBones.on = false;
            summoningBones = null;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (summoningBones != null){
            summoningBones.on = false;
            summoningBones = null;
        }
    }

    public void cancelSummoning(){
        if (summoningBones != null){
            summoningBones.on = false;
            summoningBones = null;
        }
    }

    public void finishSummoning(){
        if (summoningBones != null) {
            if (summoningBones.visible) {
                Sample.INSTANCE.play(Assets.Sounds.BONES);
                summoningBones.burst(Speck.factory(Speck.RATTLE), 5);
            } else {
                summoningBones.on = false;
            }
            summoningBones = null;
        }
        idle();
    }

    public void charge(){
        play(charging);
    }


    @Override
    public void zap(int cell) {
        super.zap(cell);
        if (ch instanceof Lich && ((Lich) ch).summoning){
            if (summoningBones != null){
                summoningBones.on = false;
            }
            summoningBones = CellEmitter.get(((Lich) ch).summoningPos);
            summoningBones.pour(Speck.factory(Speck.RATTLE), 0.2f);
            summoningBones.visible = Dungeon.level.heroFOV[((Lich) ch).summoningPos];
            if (visible || summoningBones.visible ) Sample.INSTANCE.play( Assets.Sounds.CHARGEUP, 1f, 0.8f );
        } else if (ch instanceof Lich && !((Lich) ch).weakestExists()) {
            MagicMissile.boltFromChar( parent,
                    boltType,
                    this,
                    cell,
                    new Callback() {
                        @Override
                        public void call() {
                            ((Lich)ch).onZapComplete();
                        }
                    } );
            Sample.INSTANCE.play( Assets.Sounds.ZAP );
        }
    }

    @Override
    public void onComplete(Animation anim) {
        super.onComplete(anim);
        if (anim == zap){
            if (ch instanceof Lich){
                if (((Lich) ch).summoning){
                    charge();
                } else if (((Lich) ch).weakestExists()) {
                    ((Lich)ch).onZapComplete();
                    idle();
                } else {
                    idle();
                }
            } else {
                idle();
            }
        }
    }

    public static class Green extends LichSprite {
        {
            boltType = MagicMissile.BOLT_GREEN;
        }

        @Override
        protected int texOffset() { return 0; }
    }

    public static class Blue extends LichSprite {
        {
            boltType = MagicMissile.BOLT_BLUE;
        }

        @Override
        protected int texOffset() { return 16; }
    }

    public static class Purple extends LichSprite {
        {
            boltType = MagicMissile.BOLT_PURPLE;
        }

        @Override
        protected int texOffset() { return 32; }
    }

}
