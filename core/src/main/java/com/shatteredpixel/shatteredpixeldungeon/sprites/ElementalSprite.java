package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class ElementalSprite extends MobSprite{

    public ElementalSprite() {
        super();

        texture(Assets.Sprites.ELEMENTAL);

        TextureFilm frames = new TextureFilm( texture, 14, 16 );

        idle = new Animation( 10, true );
        idle.frames( frames, 0, 1, 2 );

        run = new Animation( 15, true );
        run.frames( frames, 3, 4, 5, 6, 7, 8 );

        attack = new Animation( 12, false );
        attack.frames( frames, 9, 10, 11 );

        die = new Animation( 1, false );
        die.frames( frames, 0, 1 );

        play( idle );
    }
}
