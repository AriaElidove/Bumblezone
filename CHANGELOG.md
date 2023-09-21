### **(V.7.0.4 Changes) (1.20.1 Minecraft)**

##### Dimension:
Made bee spawning system in Bumblezone now not remove noAi set bees.


### **(V.7.0.3 Changes) (1.20.1 Minecraft)**

##### Dimension:
Made bee spawning system in Bumblezone now respect the DoMobSpawning gamerule.

##### Blocks:
Made Honeycomb Brood Blocks now respect the DoMobSpawning gamerule.

##### Entities:
Made angry Bee Queen now respect the DoMobSpawning gamerule.

##### Arenas:
Renamed `the_bumblezone:essence/life_arena/normal_enemy` to `the_bumblezone:essence/radiance_arena/normal_enemy` as that was a typo.

Renamed `the_bumblezone:essence/radiance_cannot_repair` to `the_bumblezone:essence/radiance/cannot_repair` for consistency.

Renamed `the_bumblezone:essence/calming_drowned_bonus_held_item` to `the_bumblezone:essence/calming_arena/drowned_bonus_held_item` for consistency.


### **(V.7.0.2 Changes) (1.20.1 Minecraft)**

##### Mod Compat:
(Fabric/Quilt): Fixed crash when Restricted Portals is not on. Forgot to check if mod is present before trying to use their code.


### **(V.7.0.1 Changes) (1.20.1 Minecraft)**

##### Entities:
Added NoAI argument to Sentry Watcher so its behavior can be turned off when map building.

Added missing lang for Sentry Watcher name.

Fixed NoAI Bee Queen still holding wanted item.

##### Blocks:
Added new backup textures for Essence Blocks! Textures by CrispyTwig. These textures show up when using shaders because shaders disables the normal renderer the blocks have.

##### Mod Compat:
Removed bad text shadow in Bumblezone screens in recipe viewers.


### **(V.7.0.0 Changes) (1.20.1 Minecraft)**

##### Major:
Updated to 1.20.1!

#### *\*Additions*

##### Biomes:
Added Floral Meadow biome full of vanilla flowers and cherry trees! Rootmins can spawn naturally in this biome!
 Giant Cherry Trees spawn in this biome as well.

Added Howling Constructs biome filled with tons of mysteriously floating discs.
 A good biome to practice parkouring and has some small loot!

##### Dimension:
Fishing in Bumblezone dimension now has custom loot!

##### Structures:
Added Subway structure that is a massive tunnel system with flowing air that bees uses to travel around quickly! 
 Lots of loots scattered throughout from all the travelers that has gone through this subway. 
 Can you fight against the airflow to explore everything?

Added Ancient Hoops structure which are many old pillars of wax that are decaying. Some pillars still have a hoop
 on top that mysteriously pulls air flow through. Grab a Bumblebee Chestplate and start flying from pillar to pillar!

Added Ancient Shrine structure that resembles a giant flower filled with pollen! Though the pollen looks suspicious...
 Maybe try some archaeology in this structure?

Added 6 Sempiternal Sanctum structures! A giant structure with mazes and danger that is best done after consuming Essence of the Bees item.
 In the heart is an Essence Block you touch to start an essence event. Beat the difficult events for new end game items!
 By default, these events can be repeated after winning. Set repeatableEssenceEvents to false in config to disable being able to repeat the event after winning.

Added Honey Slime Ranch. A small Shepherd Villager trapped in Bumblezone that is surviving off of Honey Slimes.

##### Blocks:
Added Ancient Wax blocks! Very high blast resistance, uncraftable, and will give Slowness, Weakness, and Mining Fatigue to 
 anyone who has not consumed Essence of the Bees before and are standing on said Ancient Wax blocks. You can use shears to 
 convert between 3 different patterns for these blocks. Only found in certain structures. Can be crafted into stairs or slabs.
 Stonecutter has more efficient recipe for making stairs/slabs.

Added Luminescent Wax Channel blocks, Luminescent Wax Corner blocks, and Luminescent Wax Node blocks! 
 Very high blast resistance, uncraftable, and will give Slowness, Weakness, and Mining Fatigue to anyone who has 
 not consumed Essence of the Bees before and are standing on said Ancient Wax blocks. If the block has a light in it, 
 people who consumed Essence of the Bees before and standing on said blocks will get Speed, Damage Resistance, 
 and Beenergized. The light forms will repel Piglins and Hoglins and also will count towards empowering Enchanting Table 
 like Bookshelves do. You can use shears to change the direction of these blocks. Only found in certain structures.
 The light forms can be crafted into lightless forms.

Windy Air added that will push any entity in whatever direction the air block is blowing! Only found in certain structures.
 Can be found in Creative Menu for builders! Can be controlled by this entity type tag: `the_bumblezone:windy_air/immune_to_push_effect`
 Bee Queen will give you Windy Air for Bumblezone armor or tools as an easy way to dispose of those unstackable items.

Heavy Air added that will pull entities down and remove Levitation, Slow Falling, Jump Boost, and non-creative flying! 
 Has no effect on Bees and Beehemoth. Only found in Sempiternal Sanctums. Can be found in Creative Menu for builders!
 Can be controlled by this entity type tag: `the_bumblezone:heavy_air/immune_to_gravity_effect`

Suspicious Pile of Pollen is added now! You may find these scattered throughout Bumblezone in certain structures. Brush them for loot!
 Also found in Creative Menu for builders, but you'll need to set the items or loot table for the block by command.
 They can also be found throughout the Pollinated Fields biomes with a decent amount of possible loot!

Added 2 new patterns for Carvable Wax! The new patterns are called Music and Grate.

##### Items:
Essence of Life - The reward for beating the Green Sempiternal Sanctum structure! Holding this item in offhand will grow 
 crops and saplings nearby, heal mobs tamed by you, and heal other mobs or players on the same team as you! Also, will cure 
 Poison and Wither effect from the healed mobs and yourself. It has 1000 use before it is depleted and needs 10 minutes to recharge by 
 being in the inventory of a player that had consumed Essence of the Bees in the past. Add more effects to 
 `the_bumblezone:essence/life/cure_effects` mob effects tag for this item to be able to remove said tagged effects from allies/pets!
 Use the following two tags to control what plants can be grown or not with this item. Let me know if a modded plant is not able to be grown:
 
 `the_bumblezone:essence/life/grow_plants`

 `the_bumblezone:essence/life/force_disallowed_grow_plant`

Essence of Radiance - The reward for beating the Yellow Sempiternal Sanctum structure! Holding this item in offhand while being
 in a spot with sky brightness 13 or above (basically in view of the sky) will grant you Regeneration 1, Saturation 1, Speed 1,
 Haste 2, and Damage Resistance 2. It will also slowly heal your armor durability as well over time! It has 4800 use before it is 
 depleted and needs 10 minutes to recharge by being in the inventory of a player that had consumed Essence of the Bees in the past.
 Add or remove effects to give to player by editing `the_bumblezone:essence/radiance/sun_effects` mob effects tag.
 Add armor items to `the_bumblezone:essence/radiance_cannot_repair` item tag if you have armor that should not be repaired.

Essence of Knowing - The reward for beating the Purple Sempiternal Sanctum structure! Holding this item in offhand will highlight mobs
 and outline certain block entities near you! Allowing you to see what monster is nearby or hidden chests or suspicious blocks! It will
 also tell you the name of any structure you are in! It has 1200 use before it is depleted and needs 15 minutes to recharge by being
 in the inventory of a player that had consumed Essence of the Bees in the past. This highlighting can be configured on client side 
 by config. Server owners can use the block and entity tags to force things to be highlighted or not regardless of what the client 
 config is set as. (The tags can be used to also correct highlighting issues on other mod's mobs with this item on)
 The entity type tags you can edit are these. Disable tag overrides all:

 `the_bumblezone:essence/knowing/prevent_highlighting`

 `the_bumblezone:essence/knowing/forced_green_highlight`

 `the_bumblezone:essence/knowing/forced_cyan_highlight`

 `the_bumblezone:essence/knowing/forced_purple_highlight`

 `the_bumblezone:essence/knowing/forced_red_highlight`

 `the_bumblezone:essence/knowing/forced_orange_highlight`

 `the_bumblezone:essence/knowing/forced_yellow_highlight`

 `the_bumblezone:essence/knowing/forced_white_highlight`

 The block tags you can edit are these. Disable tag overrides all:

 `the_bumblezone:essence/knowing/block_entity_forced_highlighting`

 `the_bumblezone:essence/knowing/block_entity_prevent_highlighting`

Essence of Calming - The reward for beating the Blue Sempiternal Sanctum structure! Holding this item in offhand will make mobs
 nearby no longer get angry at you! (Let me know if a modded mob still attacks you while this essence is active) The effect is lost
 early if player gets hurt by any mob or attacks any mob. If the player sprints while it is active, the item loses power crazy quickly! No rushing! 
 It has 600 use (seconds) before it is depleted and needs 10 minutes to recharge by being in the inventory of a player that had consumed Essence of the Bees in the past.
 The entity type tag `the_bumblezone:essence/calming/allow_anger_through` can be used to mark entities that should keep staying angry 
 at players with this calming effect active. Note, due to implementation, this tag may not always work but let me know if it fails so
 I can investigate the specific use case.

Essence of Raging - The reward for beating the Red Sempiternal Sanctum structure! Holding this item in offhand will highlight
 4 nearby hostile mobs in red. Killing them will grant you Strength status effect and it will highlight more hostile mobs nearby 
 (never more than 4 at a time) Each highlighted kill makes Strength effect stronger and maxes out at Strength 16 after killing 7 highlighted mobs!
 Making this a powerful boss destroyer weapon! The Strength only lasts 15 seconds between kills. If time runs out or you kill a non-highlighted mob,
 the item goes into 10 second cooldown. Making any kill after maxed Strength will also set the item onto 10 second cooldown. 
 It has 28 use (highlighted kills) before it is depleted and needs 30 minutes to recharge by being in the inventory of a player 
 that had consumed Essence of the Bees in the past. It will set cooldown on all other Essence of Raging that player has as well 
 due to how powerful this item is. Add or remove effects to give to player by editing `the_bumblezone:essence/raging/rage_effects` mob effects tag.

Essence of Continuity - The reward for beating the White Sempiternal Sanctum structure! Holding this item in offhand will make dying
 not kill you and instead teleport you back to your respawn point with all health restored, hunger bar filled, neutral and 
 negative status effects removed, and fire removed. It only has 1 use before it is depleted and needs 40 minutes to recharge 
 by being in the inventory of a player that had consumed Essence of the Bees in the past. It will set cooldown on all other
 Essence of Continuity that player has as well due to how powerful this item is. It even works in hardcore worlds!
 It will also give you a Written Book with details of where you were and how you almost died.

Added Flower Headwear crafted from 8 flower items surrounding a Leather Helmet! This can be dyed with dyes and washed in Cauldron just like Leather armor.
 Wearing this makes bees follow you around thinking you're a giant flower! If you have Wrath of the Hive, this headwear will
 make the timer on that effect decrease much faster and will decrease even faster if wearing 4 or more bee-themed wearables (including this item).
 You will also be able to enter Cell Maze structure without getting Wrath of the Hive when wearing this item!
 Can also be worn in a Curios head slot or Trinkets head/hat slot. Its effects does not stack with existing Flower Headwear in helmet slot.

Added Buzzing Briefcase item! Rare drop from Bee House and Honitel structure's loot. Also, a rare drop from tier 4 
 Bee Queen trades (Honey Block/Honey Buckets mainly as want items). Right click (or hold right click) on a bee to stuff
 it into the briefcase. It can hold up to 14 bees of any kind. Shift right click to open the briefcase UI where you can
 heal bees, put stingers back on, pollinate them, or grow babies up to be an adult. You can release the bees from the briefcase
 by UI or by left clicking on a block. Left clicking on an entity releases the bees and have them attack the entity you left clicked.
 Do shift left click to release all the bees at once from briefcase.

Added Bee Soup that is craftable from Bee Bread, Bowl, Beetroot, Potato, 2 Honey Combs, and 1 Bee Stinger. Drinking this
 soup gives you a long level 2 Beenergized effect and has a chance of inflicting Levitation, Slow Falling, Poison, Paralysis, 
 or Luck status effect on you. You could get multiple of these effects at once if you're unlucky (or lucky if it is good effects)!
 Can be found in some structures's loot!

Added Dirt Pellet item craftable from Coarse Dirt + Rooted Dirt. It can be thrown like a Snowball for 1 point of damage and high knockback.
 However, it will deal 3 points of damage to most flying mobs (modded included)! Controlled by these entity tags to change what will receive bonus damage:
 
 `the_bumblezone:dirt_pellet/extra_damage_dealt_to`

 `the_bumblezone:dirt_pellet/forced_no_extra_damage_dealt_to`

Added Neurotoxin Potions you can brew from Bee Soup! Can be lengthened in time with Redstone ingredient.

Pile of Pollen item form is now available in Creative Menu and can be crafted from 9 Pollen Puff in Crafting Table.
 Silk Touch mining a Pile of Pollen that is layer 8 will drop the Pile of Pollen item instead of the Pollen Puff items.

Added 8 new Banner Patterns to find throughout Bumblezone in certain structures and Suspicious Pile of Pollen!

##### Entities:
Added a new entity called Variant Bee! It is exactly like the vanilla bee in behavior but has a different skin on. This replaces
 the old UUID system Bumblezone had to replace vanilla bee skins. Now that system is gone and the bee variants are now a dedicated
 entity for Bumblezone! 4 new variants were added as well. The variant's types are exposed by a "variant" string tag in their nbt. 
 You can add or remove variants to spawn by editing the variantBeeTypes config entry (Server config takes priority over client's). 
 Be sure to add new bee textures to these files for the new bee variant you added to spawn with the textures it needs!

 `assets/the_bumblezone/textures/entity/bee_variants/<VariantType>/bee.png`

 `assets/the_bumblezone/textures/entity/bee_variants/<VariantType>/bee_angry.png`

 `assets/the_bumblezone/textures/entity/bee_variants/<VariantType>/bee_angry_nectar.png`

 `assets/the_bumblezone/textures/entity/bee_variants/<VariantType>/bee_nectar.png`

Added a new non-living entity called Sentry Watcher that lives in Sempiternal Sanctum and will blow up if taken out of the structure or somehow killed.
 This bee statue will charge at any living non-bee mob in front of it and will break through many blocks up to a certain total explosion resistance of the wall of blocks.
 The damage it deals to all in its way will increase the faster the statue is moving. Wear Bee Armor or Bee Curios/Trinkets will reduce a bit of damage as well.
 Some tags to configure its behavior are:

 `the_bumblezone:sentry_watcher/always_destroy` (block tag)

 `the_bumblezone:sentry_watcher/forced_never_destroy` (block tag)

 `the_bumblezone:sentry_watcher/activates_when_seen` (entity tag)

 `the_bumblezone:sentry_watcher/forced_never_activates_when_seen` (entity tag)

 `the_bumblezone:sentry_watcher/cannot_damage` (entity tag)

 A survival craftable spawn egg is also available where these egg-spawned watchers cannot destroy blocks and can be removed by owner double right clicking on it,
 The watcher will not charge at their owner as well. If spawned from a Dispenser, anyone can double right click remove the entity. 
 These spawn eggs cannot be set into a Spawner unless the player is in Creative Mode.

Rootmins are now added! A mob that disguises itself as a Grass Block with a flower on top that is often found in Floral Meadow biome or in certain structures. 
 It will come out of hiding to shoot a Dirt Pellet at any player or spiders nearby. If player has at least 1 bee armor or trinket/curios on, 
 the Rootmin will not attack and instead, might walk up to the player while being curious. Players can right-click this mob with another flower 
 to swap the flower on its head or give it a name tag! Flowers that Rootmins naturally spawn with and what flower they can receive are controlled by these block tags:

 `the_bumblezone:rootmin/default_flowers`

 `the_bumblezone:rootmin/allowed_flower`

 `the_bumblezone:rootmin/forced_disallowed_flower`

 Rootmins are fearful of Iron Golems, Blazes, Wither, and Warden to the point they will try to run away if too close! Rootmin targeting can be controlled by these entity tags:

 `the_bumblezone:rootmin/forced_do_not_target`

 `the_bumblezone:rootmin/panic_avoid`

 `the_bumblezone:rootmin/targets`

Added Snow Block to Clay Block and its opposite to Bee Queen Trades. 

##### Advancements:
New advancements added! 
 Along with the entire Bumblezone advancement layout completely redone and some annoying/useless advancements nerfed or removed.
 Some advancements now has more info to be more clear of what to do.

Will now display a message to user saying to check out Bumblezone's advancements for progression when they obtain the is_near_beehive advancement.

Added "is_target_tag" field to the_bumblezone:killed_counter trigger for advancements. Now you can make "target_entity" point to a tag.
 As a result, some of the Queen's Desire's advancements now checks entity tags for what counts towards them. These are:

 `the_bumblezone:queens_desire/too_many_legs_spiders`

 `the_bumblezone:queens_desire/fighting_the_swarm_silverfish`

 `the_bumblezone:queens_desire/otherworldly_mites_endermite`

 `the_bumblezone:queens_desire/terror_fangs_cave_spider`

##### Mod Compat:
Integration with Lootr was added by Nooby! Special thanks to him! 
 Worldgen placed Honey Cocoons will have a different texture when Lootr is on and have per-player loot.

Added emissive textures for some entities and blocks. 
 These will take effect if Optifine or Continuity or other rendering mod is on that reads _e Optifine emissive textures.

Better Advancements is now a recommended optional mod to use with Bumblezone so Bumblezone's advancements are easier to read.

Added tag translations for Bumblezone item tags so that EMI mod can display them.

(Forge): Added compat with Trails and Tails Plus mod so Pollen Puff hitting their Skeleton Sniffer spawns Moss Carpet!

(Forge): Added compat with Forbidden Arcanus so their Bee Bucket can be used to revive Empty Honeycomb Brood Blocks.

(Fabric): Added compat with Lost Features so that Pollen Puff hitting their Moobloom can spawn Dandelions.


#### *\*Changes* (From 1.19.4 version)

##### Blocks:
Renamed Incense Candle to Potion Candle to be more clear on how to make it and rolls off tongue better. 
 This impacts registry name as well so it is now `the_bumblezone:potion_candle`

Walking through Pile of Pollen should not trigger Sculk Sensor now. Pile of Pollen will also block Sculk Sensor's detection if between it and the sound source.

String Curtains should properly block Sculk Sensor's detection if between it and the sound source.

Fluids should not show drip animations under Glistering Honey Crystal now.

Snow should now be placeable on Royal Jelly Block. 

Swords are now the efficient tools for breaking Honey Web and Redstone Honey Web blocks.

Axes are now the efficient tools for breaking Beehive Beeswax blocks.

Several Bumblezone's full solid blocks has instruments set for Note Blocks to play when under Note Blocks.

Fixed bug where Crystalline Flower drop loses their xp progress when in item form.

Crystalline Flower can be broken by pistons now and drop itself.

Crystalline Flower now takes slightly more than 100 XP to get to tier 2 so consuming enchanted books cannot be infinitely recycled for infinite Honey Crystal Shards.

Crystalline Flower now will hold items in its consume slot and to-enchant slot even when exiting the UI. These items will drop when flower breaks.

Crystalline Flower now tries to keep the last used enchantment still selected when possible while UI is open.

Glistering Honey Crystal should not suffocate mobs inside itself, and it blocks Redstone power like how Glowstone blocks power.

Any Super Candle can be crafted to another color with 2 dyes now!

Any String Curtain can be crafted to another color with 1 dye now!

Honeycomb Brood Block feeding now can grant you Protection of the Hive even when you have Wrath of the Hive.

Increased duration of Protection of the Hive obtained from Honeycomb Brood Block feeding.

Honeycomb Brood Blocks will always have 100% of giving Protection of the Hive effect if a bee is spawned from the feeding.

For the Honey Residue/Honey Web blocks, I converted the right click washing items into an item tag to support more washing items:
 `the_bumblezone:washing_items`

Fixed Panes, Fences, and Iron Bars being able to connect to back of String Curtains. Not anymore.

Added sound when opening Honey Cocoon

##### Items:
Added Stinger Spear, Bee Cannon, and Crystal Cannon to the `minecraft:tools` tag.

Pollen Puff will not multiply Pitcher Plant when thrown at it as Mojang did not want it to be too easy to multiply (Pitcher Plant cannot be bonemealed)

Pollen Puff will spawn Moss Carpet when it hits a Sniffer!

Essence of the Bees takes less time to consume now.

All color Essence items added plus Essence of the Bees is now fire-resistant and won't be destroyed by lava/fire.

Honey Bee Leggings item texture will now show pollen if the item actually has pollen set in nbt.

Honey Bee Leggings now can collect pollen from Pile of Pollen and Flowers by walking now. Not just running.

##### Fluids:
(Fabric/Quilt): Fixed Honey Fluid and Royal Jelly Fluid not rendering the fluid texture on the far side when looking through the fluid.

(Forge): Fixed unable to swim in Sugar Water Fluid.

(Forge): Fixed Night Vision not applying to Sugar Water Fluid when inside it.

Fixed Sugar Water overlay still showing on Glistering Honey Crystal and other blocks that should not show fluid overlay.

##### Enchantments:
Allow Neurotoxin enchant to go up to level 3.

##### Entities:
Honey Slime is now immune to fall damage like Magma Cubes. The honey protects the Slime!

Fixed dimensions for baby Honey Slimes.

Fixed z-fighting on bottom of Honey Slime and ground.

Fixed Bee Queen able to be leashed when it shouldn't.

Added beehemothFriendlyFire config option, so you can disable accidentally hurting your flying friend! Set to true by default to allow owners to kill their Beehemoth by attacking.

##### Structures:
Hanging Gardens now can spawn Torch Flower, Pitcher Plant, Pink Petal, Cherry Leaves, Cherry Logs, and Cherry Saplings!.

Cell Maze now will always have 1 Throne locating Honey Compass always in its center start room.
 The structure can also now have a Mystery locating Honey Compass

Honey Compasses to Mystery Structures now will try to point to different structures even if you got multiple compasses from the same structure's loot.

Lesser Mystery Honey Compasses are added to several structure's loot where these compasses point to smaller Bumblezone structures.

Added Endermites to several existing structures.

Fixed several structures not properly checking for certain other structures to prevent bad structure fusions.

##### Biomes:
Added Bumblezone biomes to `minecraft:snow_golem_melts` biome tag to make Snow Golems melt in Bumblezone since beehives are roughly 95 degrees!

Added Bumblezone biomes to `minecraft:without_wandering_trader_spawns` biome tag to make extra sure Wandering Traders do not spawn in Bumblezone.

##### Dimension:
Fixed Water, Lava, and Powder Snow fog effect not working when in Bumblezone dimension

Set Portal Cooldown when entering or exiting Bumblezone to prevent portal chaining issues.

##### Effects:
(Forge): Removed Milk as a curative item for Wrath of the Hive. Now many other mod's methods of removing the effect should no longer work.

Protection of the Hive will always remove Wrath of the Hive effect if you managed to get both on together.

##### Advancements:
Renamed VIP Trader advancement to Crazy Trader to reduce confusion.

Fixed Magic The Candling advancement to work with Splash and Lingering Potions as well.


----------

Special thanks to RenRen, Nary, CrispyTwig, Tera, and joshieman for their work to help with this update!