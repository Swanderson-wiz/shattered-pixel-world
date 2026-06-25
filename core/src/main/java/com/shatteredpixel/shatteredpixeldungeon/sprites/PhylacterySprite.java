package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Phylactery;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public abstract class PhylacterySprite extends MobSprite {

    protected abstract int texOffset();
    private Emitter summoningBones;

    public PhylacterySprite() {
        super();

        int c = texOffset();

        texture(Assets.Sprites.PHYLACTERY);
        TextureFilm frames = new TextureFilm( texture, 10, 16);

        idle = new Animation( 5, true );
        idle.frames( frames, 0+c, 1+c, 2+c, 3+c, 4+c );

        zap = new Animation( 8, true );
        zap.frames( frames, 5+c, 6+c );

        run = idle.clone();

        attack = idle.clone();

        die = new Animation( 10, false );
        die.frames( frames, 7+c, 8+c, 9+c, 10+c );

        play( idle );
    }

    @Override
    public void update() {
        super.update();
        if (summoningBones != null && ((Phylactery) ch).summoningPos != -1){
            summoningBones.visible = Dungeon.level.heroFOV[((Phylactery) ch).summoningPos];
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

    @Override
    public void zap(int cell) {
        play(zap);
        if (ch instanceof Phylactery && ((Phylactery) ch).summoning){
            if (summoningBones != null){
                summoningBones.on = false;
            }
            summoningBones = CellEmitter.get(((Phylactery) ch).summoningPos);
            summoningBones.pour(Speck.factory(Speck.RATTLE), 0.2f);
            summoningBones.visible = Dungeon.level.heroFOV[((Phylactery) ch).summoningPos];
            if (visible || summoningBones.visible ) Sample.INSTANCE.play( Assets.Sounds.CHARGEUP, 1f, 0.8f );
        }
    }

    @Override
    public int blood() {
        return 0xA0A048;
    }

    public static class Green extends PhylacterySprite {
        @Override
        protected int texOffset() { return 0; }
    }
    public static class Blue extends PhylacterySprite {
        @Override
        protected int texOffset() { return 11; }
    }
    public static class Purple extends PhylacterySprite {
        @Override
        protected int texOffset() { return 22; }
    }
}
