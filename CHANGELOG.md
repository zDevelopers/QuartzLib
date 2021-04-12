# QuartzLib Changelog

This document explains how QuartzLib evolved from version to version, starting at 0.0.1 (the version after zLib 0.99), and how to migrate your code if there are breaking changes.

We follow semantic versioning: you can tell if a version contains breaking changes by looking at the evolution of the version number.

Changes marked with :warning: are **breaking changes**.

## QuartzLib 0.0.4

_Published on April 12th, 2021_

### Changed

#### Internationalization (i18n)

- Fixed a bug where plural scripts were loaded once per translation instead of once per file.
- Optimised the plural script manager by adding many known scripts. This reduces the likelihood of
  having to start a JavaScript engine, improving performance by several orders of magnitude.

## QuartzLib 0.0.3

_Published on April 11th, 2021_

### Changed

#### `CraftingRecipes`

- :warning: All recipes are now required to provide names, therefore all helper methods to generate recipes take a
  mandatory name argument. The name is automatically namespaced with the plugin's name.
- :warning: All helpers that were consuming the now-deprecated `MaterialData` now consume the more flexible `RecipeChoice` instead.
- All helpers that generate shaped recipes (2x2, 2x2 diagonal, etc.) now return `ShapedRecipe`s explicitely, since there
  is no way those recipes can be anything other than shaped, and hiding this detail is not useful at all.
  
#### Glow effect

This tool was rewritten to register a namespaced enchantment, avoiding future incompatibilities with other plugins
registering new enchants.

- :warning: The glow effect is now a QuartzLib component. You still use glow effect as usual (either using `GlowEffect`
  or through the `ItemStackBuilder`), but you have to load the effect using `loadComponents(GlowEffect.class)` in your
  plugin's `onEnable`.

#### `DualWielding`

This API was added when Bukkit had no support for dual wielding. As there is support now, we cleaned up all of this
and removed some things. We kept some useful methods and moved things to other classes for coherence.

- Added `ItemUtils.consumeItemInOffHand`.
- :warning: Moved `ItemUtils.consumeItem` to `ItemUtils.consumeItemInMainHand`.
- :warning: Moved `ItemUtils.damageItemInHand` to `ItemUtils.damageItem`.
- `ItemUtils.damageItem` now returns `true` if the damaged item was broken.
- :warning: Moved `ItemUtils.breakItemInHand` methods to `InventoryUtils.breakItemInHand`.
- :warning: Moved `DualWielding` to `InventoryUtils.DualWielding`.
- :warning: Moved `DualWieldling` methods to `InventoryUtils`.
- :warning: Removed `DualWieldling.setItemInHand` and `DualWieldling.getItemInHand` (use Bukkit API instead).


#### `GlowEffect`
- :warning: This class is not an enchantment anymore and has been re-implemented. It is now a `QuartzComponent` that
  needs to be enabled at startup in order to prevent items with a Glow effect to be used in a Grindstone.

## QuartzLib 0.0.2

_Published on November 26th, 2020_

### Added

#### `ItemStackBuilder`

- We added a [`withMeta()`](https://zdevelopers.github.io/QuartzLib/fr/zcraft/quartzlib/tools/items/ItemStackBuilder.html#withMeta-java.util.function.Consumer-)  method to alter the `ItemMeta` of the built item. Use it like this.

  ```java
   final ItemStack skull = new ItemStackBuilder(Material.PLAYER_HEAD)
       .withMeta((SkullMeta s) -> s.setOwner("foo"))
       .item();
  ```

  This method can be called multiple times, and if so, all callbacks will be executed.
  If the meta is not of the right type, the callback will be ignored without error. You can chain multiple `withMeta()` calls; only the ones matching the item type will be called.

- We added a [`withFlags(ItemFlag...)`](https://zdevelopers.github.io/QuartzLib/fr/zcraft/quartzlib/tools/items/ItemStackBuilder.html#withFlags-org.bukkit.inventory.ItemFlag...-)  method to add the given item flags to the built item. As example, to hide enchantments and unbreakable state from an item, use it like this.

  ```java
  final ItemStack item = new ItemStackBuilder(Material.QUARTZ)
      .withFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE)
      .item();
  ```

  To hide _all_ attributes from the item, use `hideAllAttributes()`.
  
#### [`ItemUtils`](https://zdevelopers.github.io/QuartzLib/fr/zcraft/quartzlib/tools/items/ItemUtils.html)

- We added a [`asDye`](https://zdevelopers.github.io/QuartzLib/fr/zcraft/quartzlib/tools/items/ItemUtils.html#asDye-org.bukkit.ChatColor-) method to convert a `ChatColor` to its closest `DyeColor` equivalent.

- We added two `colorize` methods to `ItemUtils` to convert either [a dye](https://zdevelopers.github.io/QuartzLib/fr/zcraft/quartzlib/tools/items/ItemUtils.html#colorize-fr.zcraft.quartzlib.tools.items.ColorableMaterial-org.bukkit.DyeColor-) or [a chat color](https://zdevelopers.github.io/QuartzLib/fr/zcraft/quartzlib/tools/items/ItemUtils.html#colorize-fr.zcraft.quartzlib.tools.items.ColorableMaterial-org.bukkit.ChatColor-) to a colored block dynamically. As example,
  
  ```java
  ItemUtils.colorize(ColorableMaterial.GLAZED_TERRACOTTA, DyeColor.LIME)
  ```
  
  will return `Material.LIME_GLAZED_TERRACOTTA`.

#### Tests

- We now use MockBukkit to greatly increase our tests coverage.
- A sample QuartzLib plugin is now available in the repository. We also use it for tests. Check it out at ` src/test/java/fr/zcraft/ztoaster`.



### Changed

#### `ItemStackBuilder`

- :warning: We renamed the `ItemStackBuilder.hideAttributes()` to `ItemStackBuilder.hideAllAttributes()` for clarity.



### Removed

#### `GuiUtils`

- :warning: We removed the following methods from `GuiUtils`, as they were duplicates of `ItemStackBuilder` ones, or using deprecated APIs. Use `ItemStackBuilder.hideAllAttributes()` instead.
  - `GuiUtils.hideItemAttributes(ItemMeta)`
  - `GuiUtils.hideItemAttributes(ItemStack)`

#### `ItemStackBuilder`

- :warning: We removed the `ItemStackBuilder.dye(DyeColor)` and `ItemStackBuilder.head(String)` methods.
  The first one can be replaced by the right material, as in 1.13+ each dyed version has its own material.
  For the second one, use the new `ItemStackBuilder.withMeta()` method.
- :warning: We removed the `ItemStackBuilder.data(short)` method, and all abilities to process Data Values
  since they are now removed from Minecraft and strongly deprecated from Bukkit.
  Damage for tools are now properly handled by bukkit using the [`Damageable`](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/inventory/meta/Damageable.html)
  `ItemMeta` API and can be used in `ItemStackBuilder` with the new [`withMeta()`](https://dev.zcraft.fr/docs/quartzlib/fr/zcraft/quartzlib/tools/items/ItemStackBuilder.html#withMeta-java.util.function.Consumer-)
  API.

#### `ItemUtils`

- :warning: We removed the following methods from `ItemUtils`, as they are now directly present in all supported Bukkit versions.
  You can also use their equivalent in `ItemStackBuilder` instead.
  - `hasItemFlag(ItemMeta, String)`
  - `removeItemFlags(ItemMeta, String...)`
  - `hideItemAttributes(ItemMeta)`
  - `hideItemAttributes(ItemStack)`

## QuartzLib 0.0.1

_Published on November 12th, 2020_

### Changed

#### Higher base requirements

:warning: QuartzLib now requires Java 8+ (instead of 7+) and Bukkit 1.15+ (instead of 1.8.3+).

#### Moved Maven repository

:warning: You must use our new (and now, stable) repository, at `https://maven.zcraft.fr/QuartzLib`. To do so, put his in your `pom.xml`, instead of the old repository:

```xml
    <repository>
        <id>zdevelopers-quartzlib</id>
        <url>https://maven.zcraft.fr/QuartzLib</url>
    </repository>
```

Also, the artifact ID changed to reflect the new name. You should update the dependency like so:

```xml
    <dependency>
        <groupId>fr.zcraft</groupId>
        <artifactId>quartzlib</artifactId>
        <version>0.0.1</version>
    </dependency>
```

Of course, feel free to update the version if new versions have been released when you read this.

Finally, as the package changed too, you should update your shading settings. Update the `configuration` tag like this:

```xml
                     <artifactSet>
                         <includes>
                             <include>fr.zcraft:quartzlib</include>
                         </includes>
                     </artifactSet>
                     <relocations>
                         <relocation>
                             <pattern>fr.zcraft.quartzlib</pattern>
                             <shadedPattern>YOUR.OWN.PACKAGE.quartzlib</shadedPattern>
                         </relocation>
                     </relocations>
```

…keeping other shading as is, if any.

#### Renamed packages and classes

:warning: zLib is now QuartzLib, so a lot of things were renamed.

- The base package `fr.zcraft.zlib` is now `fr.zcraft.quartzlib`.
- The `ZLib` class is now `QuartzLib`.
- The `ZLibComponent` class is now `QuartzComponent`.
- The `ZPlugin` class is now `QuartzPlugin`.

Just rename these references—the interfaces have remained the same.

### Removed

#### `RawText`

- :warning: Removed `Achievement`-related methods from the raw text component, as these are no longer supported in Minecraft 1.15+.
