package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class TrainingDummySprite extends MobSprite {

    public TrainingDummySprite() {
        super();

        texture (Assets.Sprites.TRAINING_DUMMY);

        TextureFilm frames = new TextureFilm( texture, 14, 18);

        idle = new MovieClip.Animation( 4, true );
        idle.frames( frames, 0 );

        run = idle.clone();
        attack = idle.clone();
        die = idle.clone();

        play( idle );
    }

}
