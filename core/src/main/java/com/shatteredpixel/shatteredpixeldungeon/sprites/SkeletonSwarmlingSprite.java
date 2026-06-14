package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.watabou.noosa.TextureFilm;

public class SkeletonSwarmlingSprite extends MobSprite {

    public SkeletonSwarmlingSprite() {
        super();

        texture(Assets.Sprites.SKELE_SWARMLING );

        TextureFilm frames = new TextureFilm( texture, 8, 11);

        idle = new Animation( 12, true );
        idle.frames( frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3 );

        run = new Animation( 15, true );
        run.frames( frames, 4, 5, 6, 7, 8, 9  );

        attack = new Animation( 15, false );
        attack.frames( frames, 10, 11, 12 );

        die = new Animation( 12, false );
        die.frames( frames, 13, 14, 15, 16);

        play( idle );
    }

    @Override
    public void die() {
        super.die();
        if (Dungeon.level.heroFOV[ch.pos]) {
            emitter().burst( Speck.factory( Speck.BONE ), 2 );
        }
    }

    @Override
    public int blood() {
        return 0xFFcccccc;
    }

}
