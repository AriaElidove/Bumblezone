### **(V.7.6.6 Changes) (1.21 Minecraft)**

##### Misc:
(NeoForge): Update to run on Neo 21.0.40-beta or newer


### **(V.7.6.5 Changes) (1.21 Minecraft)**

##### Fluids:
Improve the interaction between Honey Fluids and Lava.

##### Items:
(NeoForge): Made Crystal Cannon now tell Neo API that its default ammo is Honey Crystal Shards.

#### Misc:
Changed how I detect structures within features. May resolve issues where game gets deadlocked/stuck during worldgen.

Fixed a mixin crash on Fabric


### **(V.7.6.4 Changes) (1.21 Minecraft)**

##### Structures:
Made Purple Sempiternal Sanctum arena's Purple Spikes a bit more visible.

Mute the 24th and later Vexes in Yellow Sempiternal Sanctum's arena event to reduce chances of reaching sound limit.

##### Fluids:
Fixed when using a very high resolution texture pack, that other textures bleed into diagonal Honey Fluid textures as tiny squares.

##### Items:
Fixed Stinger Spear, fired Bee Stinger, and fired Honey Crystal Shards unable to break Decorated Pots properly.

##### Teleporting:
Send more packets when teleporting by Bumblezone. Might fix some sync issues?


### **(V.7.6.3 Changes) (1.21 Minecraft)**

##### Blocks:
Fixed broken Honey Cocoon textures when Anti-Trypophobia resourcepack is enabled.

##### Fluids:
Fixed other textures bleeding into diagonal Honey Fluid textures as tiny squares.

##### Items:
Fixed crash when Sugar Water Bucket is attempted to be used in Nether.

##### Structures:
Added 30 extra seconds to complete Yellow and Red Essence Arena events in Sempiternal Sanctums.

Arena victory rewards are now loot tables! You can add to or change rewards from completing Sempiternal Sanctums!
 `the_bumblezome:gameplay/rewards/red_arena_victory`
 `the_bumblezome:gameplay/rewards/yellow_arena_victory`
 `the_bumblezome:gameplay/rewards/green_arena_victory`
 `the_bumblezome:gameplay/rewards/blue_arena_victory`
 `the_bumblezome:gameplay/rewards/purple_arena_victory`
 `the_bumblezome:gameplay/rewards/white_arena_victory`


### **(V.7.6.2 Changes) (1.21 Minecraft)**

##### Misc:
Fixed crash when starting up server due to classloading issues in packet initialization.

##### Items:
Throwing Pollen Puff at the Bogged mob may spawn Red Mushroom or Brown Mushroom nearby! (If mushroom can be placed at spot)

Throwing Dirt Pellet at Breeze will deal extra damage now!

##### Blocks:
Windy Air and Heavy Air no longer affects Breeze mob.

String Curtains now will block Breeze pathfinding through it.

##### Structures:
Added Breeze and Bogged to the enemies that can spawn in the Red Sempiternal Sanctum's essence arena event!


### **(V.7.6.1 Changes) (1.21 Minecraft)**

##### Advancements:
Halved the requirements for many Queen's Desire advancements to reduce grind.

##### Structures:
Made Sempiternal Sanctums spawn a tiny bit more commonly. Tiny bit.

##### Mod Compat:
Fixed crash with Mekanism.

Fixed crash with JEI, EMI, and REI.


### **(V.7.6.0 Changes) (1.21 Minecraft)**

##### Major:
Ported to 1.21! Please report issues and bugs! There's bound to be some due to the workload to do this port.

##### Entities:
Bee Queen trading JSON files changed a bit with the result section to now allow specifying a result item's components.

##### Blocks:
Crystalline Flower now has a dedicated folder of `data/the_bumblezone/bz_crystalline_flower_data` where you can specify 
 exactly what xp amount a tag of items gives. You can now also disable the default 1xp item consuming too!

Potion Candle now has a dedicated folder of `data/the_bumblezone/bz_potion_candle_data` where you can specify limits
 to effects when attached to Potion Candle! Such as capping the maximum level of an effect, capping burn duration,
 or how long the effect lingers after leaving candle. Much easier now to balance Potion Candle for your modpack!

Potion Candle now is disallowed from having Hero of the Village effect, Bad Omen, Trial Omen, and Raid Omen. 
 Controlled by `the_bumblezone:potion_candle/disallowed_effects` effects tag.
