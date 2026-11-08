package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.watabou.noosa.TextureFilm;

public class WrathDemonSprite extends MobSprite {

    public WrathDemonSprite() {
        super();

        texture( Assets.Sprites.WRATH_DEMON );

        TextureFilm frames = new TextureFilm( texture, 12, 16 );

        idle = new Animation( 2, true );
        idle.frames( frames, 0, 0, 0, 1, 0, 0, 1, 1 );

        run = new Animation( 12, true );
        run.frames( frames, 2, 3, 4, 5, 6, 7 );

        attack = new Animation( 12, false );
        attack.frames( frames, 8, 9, 10 );

        die = new Animation( 10, false );
        die.frames( frames, 11 );

        play( idle );

    }

    @Override
    public void die() {
        super.die();
        emitter().burst( ElmoParticle.FACTORY , 10 );
        emitter().burst( ShadowParticle.UP, 8 );
    }
}
