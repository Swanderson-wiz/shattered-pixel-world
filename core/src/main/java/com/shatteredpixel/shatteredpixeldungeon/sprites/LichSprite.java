package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;


public abstract class LichSprite extends MobSprite {

    protected Animation charging;
    protected Emitter summoningParticles;
    protected Emitter summoningBones;
    protected abstract int texOffset();
    protected int boltType;

    public LichSprite() {
        super();

        int c= texOffset();

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
        charging.frames( frames, c+7, c+8 );

        die = new Animation( 10, false );
        die.frames( frames, c+9, c+10, c+11, c+12 );

        play( idle );
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
