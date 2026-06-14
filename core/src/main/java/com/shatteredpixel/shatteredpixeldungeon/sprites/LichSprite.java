package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.watabou.utils.Callback;




public class LichSprite extends MobSprite {

    protected Animation charging;
    protected Emitter summoningParticles;
    protected Emitter summoningBones;
    protected abstract int texOffset();
    protected int BoltType;

    public LichSprite() {
        super();

        int c= texOffset();

        texture( Assets.Sprites.LICH );
        TextureFilm frames = new TextureFilm( texture, 16, 16 );

        idle = new Animation( 1, true );
        idle.frames( frames, c+0, c+0, c+0, c+1, c+0, c+0, c+0, c+0, c+1 );

        run = new Animation( 8, true );
        run.frames( frames, c+0, c+0, c+0, c+2, c+3, c+4 );
    }
}
