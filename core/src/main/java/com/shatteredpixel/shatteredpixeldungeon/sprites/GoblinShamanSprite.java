package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class GoblinShamanSprite extends MobSprite{

    public GoblinShamanSprite() {
        super();

        texture( Assets.Sprites.GOBLIN_SHAMAN );

        TextureFilm frames = new TextureFilm( texture, 12, 15 );

        idle = new Animation( 2, true );
        idle.frames( frames, 0, 0, 0, 1, 1);

        run = new Animation( 10, true);
        run.frames( frames, 4, 5, 6, 5);

        attack = new Animation(12, false);
        attack.frames( frames, 2, 3, 0);

        die = new Animation( 12, false);
        die.frames( frames, 7, 8, 9, 10);

        play( idle );
    }
}
