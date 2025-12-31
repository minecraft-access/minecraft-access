/**
 * This package contains the public API for Minecraft Access.
 * Anything outside of this package or anything annotated with {@link org.jetbrains.annotations.ApiStatus.Internal}
 * is not part of the public API, may change at any time in any way without notice, and should not be accessed.
 *
 * <h2>Getting Started</h2>
 * <h3>1. Dependency</h3>
 * Minecraft Access is available on Maven Central, which should be available by default.
 * In the below examples, {@code VERSION} should be replaced with the current version of Minecraft Access.
 *
 * <p>Currently, the artifacts published to Maven Central are not able to run Minecraft Access in a development environment.
 * This will likely be changed in a future version.
 * Until then, artifacts from Modrinth Maven can be used for testing.
 *
 * <h4>Using a Version Catalog</h4>
 * <h5>{@code gradle/libs.versions.toml}</h5>
 * <pre>{@code
 * [libraries]
 * minecraftAccess = "org.mcaccess:minecraft-access:VERSION"
 *
 * # Optional: Only needed to run Minecraft Access in the development environment
 * # NeoForge is also available.
 * minecraftAccessFabric = "maven.modrinth:minecraft-access:VERSION+fabric"
 * }</pre>
 *
 * <h5>{@code build.gradle}</h5>
 * <pre>{@code
 * // Optional: Only needed to run Minecraft Access in the development environment
 * repositories {
 *     exclusiveContent {
 *         forRepository {
 *             maven {
 *                 name = 'Modrinth'
 *                 url = 'https://api.modrinth.com/maven'
 *             }
 *         }
 *         filter {
 *             includeGroup 'maven.modrinth'
 *         }
 *     }
 * }
 *
 * dependencies {
 *     // compileOnly should be used instead on NeoForge
 *     modCompileOnly libs.minecraftAccess
 *
 *     // Optional: Enables Minecraft Access in the development environment
 *     // runtimeOnly should be used instead on NeoForge
 *     modRuntimeOnly libs.minecraftAccessFabric
 * }
 * }</pre>
 *
 * <h4>Without a Version Catalog</h4>
 * <h5>{@code build.gradle}</h5>
 * <pre>{@code
 * // Optional: Only needed to run Minecraft Access in the development environment
 * repositories {
 *     exclusiveContent {
 *         forRepository {
 *             maven {
 *                 name = 'Modrinth'
 *                 url = 'https://api.modrinth.com/maven'
 *             }
 *         }
 *         filter {
 *             includeGroup 'maven.modrinth'
 *         }
 *     }
 * }
 *
 * dependencies {
 *     // compileOnly should be used instead on NeoForge
 *     modCompileOnly 'org.mcaccess:minecraft-access:VERSION'
 *
 *     // Optional: Enables Minecraft Access in the development environment
 *     // runtimeOnly should be used instead on NeoForge
 *     modRuntimeOnly 'maven.modrinth:minecraft-access:VERSION+fabric'
 * }
 * }</pre>
 *
 * <h3>2. Creating an Addon</h3>
 * To interact with the API, an {@link org.mcaccess.minecraftaccess.api.MinecraftAccessAddon} must be created.
 * See {@link org.mcaccess.minecraftaccess.api.AddonRegistry} for what can be registered.
 *
 * <h4>{@code src/main/java/com/example/MyAddon.java}</h4>
 * <pre>{@code
 * public class MyAddon implements MinecraftAccessAddon {
 *     @Override
 *     public void init(AddonRegistry registry) {
 *         // ...
 *     }
 * }
 * }</pre>
 *
 * <h3>3. Addon Registration</h3>
 * Addons must be registered either by adding it to {@code fabric.mod.json} on Fabric
 * or by annotating it with {@link org.mcaccess.minecraftaccess.api.MinecraftAccessAddon.NeoForge} on NeoForge.
 *
 * <h4>{@code src/main/resources/fabric.mod.json}</h4>
 * <pre>{@code
 * {
 *     "entrypoints": {
 *         "minecraft_access": [
 *             "com.example.MyAddon"
 *         ]
 *     }
 * }
 * }</pre>
 *
 * <h4>NeoForge</h4>
 * <pre>{@code
 * @MinecraftAccessAddon.NeoForge
 * public class MyAddon implements MinecraftAccessAddon {
 *     // ...
 * }
 * }</pre>
 *
 * @see org.mcaccess.minecraftaccess.api.MinecraftAccessAddon
 * @see org.mcaccess.minecraftaccess.api.AddonRegistry
 * @since 1.12.0
 */
package org.mcaccess.minecraftaccess.api;
