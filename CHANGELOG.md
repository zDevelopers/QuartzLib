# QuartzLib Changelog

This document explains how QuartzLib evolved from version to version, starting at 0.0.1 (the version after zLib 0.99), and how to migrate your code if there are breaking changes.

We follow semantic versioning: you can tell if a version contains breaking changes by looking at the evolution of the version number.

Changes marked with :warning: are **breaking changes**.



## QuartzLib 0.1

_Published one day_

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
  `ItemMeta` API and can be used in `ItemStackBuilder` with the new [`withMeta()`](https://zdevelopers.github.io/QuartzLib/fr/zcraft/quartzlib/tools/items/ItemStackBuilder.html#withMeta-java.util.function.Consumer-)
  API.

#### `ItemUtils`

- :warning: We removed the following methods from `ItemUtils`, as they are now directly present in all supported Bukkit versions.
  You can also use their equivalent in `ItemStackBuilder` instead.
  - `hasItemFlag(ItemMeta, String)`
  - `removeItemFlags(ItemMeta, String...)`
  - `hideItemAttributes(ItemMeta)`
  - `hideItemAttributes(ItemStack)`

## QuartzLib 0.0.1

_Published 12 November, 2020_

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

