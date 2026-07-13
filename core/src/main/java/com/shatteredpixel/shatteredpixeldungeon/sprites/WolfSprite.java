package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class WolfSprite extends MobSprite {

    public WolfSprite() {
        super();

        texture(Assets.Sprites.WOLF );

        TextureFilm frames = new TextureFilm( texture, 20, 16 );

        idle = new Animation( 2, true );
        idle.frames( frames, 0, 0, 0, 0, 0, 0, 1, 1 );

        run = new Animation( 12, true );
        run.frames( frames, 2, 3, 4, 5 );

        attack = new Animation( 12, false );
        attack.frames( frames, 6, 7, 8);

        die = new Animation( 6, false );
        die.frames( frames, 9, 10, 11 );

        play( idle );
    }
}
