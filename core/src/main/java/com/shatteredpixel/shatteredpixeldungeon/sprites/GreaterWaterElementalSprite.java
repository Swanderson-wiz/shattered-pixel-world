package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class GreaterWaterElementalSprite extends MobSprite {

    public GreaterWaterElementalSprite() {
        super();

        texture(Assets.Sprites.GREATER_WATER);

        TextureFilm frames = new TextureFilm( texture, 14, 16 );

        idle = new Animation( 5, true );
        idle.frames( frames, 0, 1, 2, 3 );

        run = new Animation( 6, true );
        run.frames( frames, 4, 4, 5 );

        attack = new Animation( 12, false );
        attack.frames( frames, 6, 7, 8 );

        die = new Animation( 12, false );
        die.frames( frames, 9, 10, 11, 12, 13 );

        play( idle );
    }
}
