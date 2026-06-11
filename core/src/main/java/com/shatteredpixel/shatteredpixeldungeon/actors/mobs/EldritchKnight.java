/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.EldritchKnightSprite;
import com.watabou.utils.Random;

public class EldritchKnight extends Mob {

    {
        spriteClass = EldritchKnightSprite.class;

        HP = HT = 80;
        defenseSkill = 30;

        EXP = 12;
        maxLvl = 26;

        loot = Generator.Category.ARMOR;
        lootChance = 0.2f;

        HUNTING = new Hunting();
    }

    @Override
    public int damageRoll() { return Random.NormalIntRange(10, 20); }

    @Override
    public int attackSkill( Char target ) { return 30; }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 2);
    }

    @Override
    public int attackProc( Char enemy, int damage ) {
        damage = super.attackProc( enemy, damage );
        if (Random.Int( 2 ) == 0) {
            Buff.prolong( enemy, Hex.class, Hex.DURATION );
            enemy.sprite.burst( 0x5A0F36, 5 );
            if(Random.Int( 3 ) == 0) {
                Buff.prolong( enemy, Vertigo.class, Vertigo.DURATION );
            }
        }

        return damage;
    }
}
