package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public abstract class PhylacterySprite extends MobSprite {

    protected abstract int texOffset();
    private Animation activeIdle;

    public PhylacterySprite() {
        super();

        int c = texOffset();

        texture(Assets.Sprites.PHYLACTERY);
        TextureFilm frames = new TextureFilm( texture, 10, 16);

        idle = new Animation( 1, true );
        idle.frames( frames, 0+c, 1+c, 2+c, 3+c, 4+c );

        activeIdle = new Animation( 1, false );
        activeIdle.frames( frames, 5+c, 6+c );

        run = idle.clone();

        attack = idle.clone();

        die = new Animation( 10, false );
        die.frames( frames, 7+c, 8+c, 9+c, 10+c );

        play( idle );
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
        protected int texOffset() { return 10; }
    }
    public static class Purple extends PhylacterySprite {
        @Override
        protected int texOffset() { return 20; }
    }
}
