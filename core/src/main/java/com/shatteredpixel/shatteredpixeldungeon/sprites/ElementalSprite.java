package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class ElementalSprite extends MobSprite{

    public ElementalSprite() {
        super();

        texture(Assets.Sprites.ELEMENTAL);

        TextureFilm frames = new TextureFilm( texture, 14,16 );

        idle = new Animation( 1, true );
        idle.frames( frames, 0, 1 );

        run = new Animation( 1, true );
        run.frames( frames, 2, 3, 4, 5, 6, 7 );

        attack = new Animation( 1, false );
        attack.frames( frames, 8, 9, 10);

        die = new Animation( 1, false );
        die.frames( frames, 0, 1 );

        play( idle );
    }
}
