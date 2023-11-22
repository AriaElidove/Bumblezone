package com.telepathicgrunt.the_bumblezone.modcompat.neoforge;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.configs.BzModCompatibilityConfigs;
import com.telepathicgrunt.the_bumblezone.events.entity.EntitySpawnEvent;
import com.telepathicgrunt.the_bumblezone.modcompat.ModChecker;
import com.telepathicgrunt.the_bumblezone.modcompat.ModCompat;
import com.telepathicgrunt.the_bumblezone.modinit.BzEffects;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import pokecube.api.data.PokedexEntry;
import pokecube.api.entity.pokemob.IPokemob;
import pokecube.api.entity.pokemob.PokemobCaps;
import pokecube.core.PokecubeCore;
import pokecube.core.database.Database;
import pokecube.core.entity.pokemobs.helper.PokemobBase;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PokecubeCompat implements ModCompat {
//    private static final List<PokedexEntry> BABY_POKECUBE_POKEMON_LIST = new ArrayList<>();
//    private static final List<PokedexEntry> POKECUBE_POKEMON_LIST = new ArrayList<>();
//
//    public PokecubeCompat() {
//        BABY_POKECUBE_POKEMON_LIST.add(Database.getEntry("Combee"));
//        BABY_POKECUBE_POKEMON_LIST.add(Database.getEntry("Weedle"));
//        BABY_POKECUBE_POKEMON_LIST.add(Database.getEntry("Cutiefly"));
//
//        POKECUBE_POKEMON_LIST.addAll(BABY_POKECUBE_POKEMON_LIST);
//        POKECUBE_POKEMON_LIST.add(Database.getEntry("Ribombee"));
//        POKECUBE_POKEMON_LIST.add(Database.getEntry("Vespiquen"));
//        POKECUBE_POKEMON_LIST.add(Database.getEntry("Kakuna"));
//        POKECUBE_POKEMON_LIST.add(Database.getEntry("Beedrill"));
//
//        // Keep at end so it is only set to true if no exceptions was thrown during setup
//        ModChecker.pokecubePresent = true;
//    }
//
//    @Override
//    public EnumSet<Type> compatTypes() {
//        return EnumSet.of(Type.SPAWNS, Type.DIMENSION_SPAWN);
//    }
//
//    @Override
//    public boolean onBeeSpawn(EntitySpawnEvent event, boolean isBaby) {
//        if (event.entity().getRandom().nextFloat() >= BzModCompatibilityConfigs.spawnrateOfPokecubeBeePokemon) return false;
//        if (!isBaby) return false;
//
//        List<PokedexEntry> pokemonListToUse = BABY_POKECUBE_POKEMON_LIST;
//        if (pokemonListToUse.size() == 0) {
//            Bumblezone.LOGGER.warn(
//                    "Error! List of POKECUBE_POKEMON_LIST is empty! Cannot spawn their bees. " +
//                    "Please let TelepathicGrunt (The Bumblezone dev) know about this!");
//            return false;
//        }
//
//        Mob entity = event.entity();
//        LevelAccessor world = event.level();
//
//        // Pokecube mobs crash if done in worldgen due to onInitialSpawn not being worldgen safe in their code.
//        if(world instanceof WorldGenRegion) return false;
//
//        // randomly pick a Pokecube mob entry
//        PokedexEntry pokemonDatabase = pokemonListToUse.get(world.getRandom().nextInt(pokemonListToUse.size()));
//        PokedexEntry.SpawnData spawn = pokemonDatabase.getSpawnData();
//        if(spawn == null) return false;
//        Mob pokemon = PokecubeCore.createPokemob(pokemonDatabase, entity.level());
//        if (pokemon == null) return false;
//
//        pokemon.setHealth(pokemon.getMaxHealth());
//        pokemon.setBaby(isBaby);
//
//        pokemon.moveTo(
//                entity.getX(),
//                entity.getY(),
//                entity.getZ(),
//                world.getRandom().nextFloat() * 360.0F,
//                0.0F);
//
//        IPokemob pokemob = PokemobCaps.getPokemobFor(pokemon);
//        pokemob.setExp(0, false);
//
//        pokemon.finalizeSpawn(
//                (ServerLevelAccessor) world,
//                world.getCurrentDifficultyAt(pokemon.blockPosition()),
//                event.spawnType(),
//                null,
//                null);
//
//        world.addFreshEntity(pokemon);
//        return true;
//    }
//
//    @Override
//    public void onEntitySpawnInDimension(Entity entity) {
//        if (BzModCompatibilityConfigs.beePokemonGetsProtectionEffect && entity instanceof PokemobBase pokemobBase) {
//            if (POKECUBE_POKEMON_LIST.contains(pokemobBase.pokemobCap.getPokedexEntry())) {
//                pokemobBase.addEffect(new MobEffectInstance(
//                        BzEffects.PROTECTION_OF_THE_HIVE.get(),
//                        Integer.MAX_VALUE - 5,
//                        0,
//                        true,
//                        false));
//            }
//        }
//    }
}
