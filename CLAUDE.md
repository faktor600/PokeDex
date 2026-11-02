# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

Use `.\gradlew.bat` in PowerShell or `./gradlew` in Bash.

```
./gradlew :app:assembleDebug                 # build the app
./gradlew :app:installDebug                  # install on a connected device
./gradlew lintDebug                          # lint the Android modules
./gradlew test                               # all unit tests (Android and JVM modules)
./gradlew connectedDebugAndroidTest          # instrumented tests (needs a device)
```

The test task depends on the module type:

```
./gradlew :pokemon_details:testDebugUnitTest --tests "com.pokedex.pokemon_details.ExampleUnitTest"   # Android module
./gradlew :api_pokemon:test --tests "com.example.SomeTest"                                           # JVM module
```

The only existing tests are the template `Example*` tests.

## Architecture

Multi-module Compose app, a Pokédex backed by PokeAPI (`https://pokeapi.co/api/v2/`). It uses Dagger (with KSP, no Hilt), Navigation 3, Room 3 (package `androidx.room3`), and DataStore. Dependency versions are in `gradle/libs.versions.toml`, though a few are declared inline in module build files.

### Modules

- `:core`, `:api_pokemon`, `:api_pokemon:dao`, `:core_database` are **plain JVM modules**, so they can't use Android APIs.
- `:compose_core` holds the theme, shared components, and `daggerViewModel`.
- `:data_store` is an Android module that wraps Preferences DataStore.
- `:pokemon_list_feature`, `:pokemon_details`, `:pokedex_welcome`, `:splash_screen` are feature modules. They don't depend on each other.
- `:app` is the only module that sees every feature. It owns the `AppComponent`, the back stack, and the Room database builder.

`:api_pokemon` is split into `domain/` (repository interface, models, use cases) and `data/` (Retrofit API, responses, mappers, `internal` repository implementation). Features call the use cases.

### Dependency injection

ViewModels are created through a map of subcomponent factories, not constructed directly:

1. Each screen has a `@Subcomponent` that implements `ComponentWithViewModel<VM>`, with a `Factory` that extends `ComponentFactory`.
2. A `@Module` in the same file binds that factory `@IntoMap` with `@ClassKey(VM::class)`.
3. `AppComponent` lists that module and exposes the map through `factoriesProvider()`.
4. `App` implements `ComponentFactoryMapOwner` and exposes the map, and composables get their ViewModel with `daggerViewModel()`, which reads the map from the application context.

If a ViewModel is missing from the map, `daggerViewModel()` fails at runtime with "No ... found in map dependencies", not at compile time.

Features that need Pokémon data add `PokemonApiModule` to their subcomponent. It's installed per subcomponent, so `PokemonRepositoryImpl` is not a singleton. Its shared state (in-flight jobs, save scope, semaphore) lives in its `companion object` for that reason.

### Navigation

Each feature's `route/` package has a `@Serializable` `NavKey` route and an `EntryProviderScope<NavKey>` extension that registers the entry and takes navigation callbacks as lambdas. `MainActivityContainer` in `app/.../MainActivity.kt` holds the back stack and connects those callbacks, because features can't reference each other.

### Adding a feature screen

Create the module and add it to `settings.gradle.kts` and `app/build.gradle.kts`. Add the subcomponent and factory module, then register them in `AppComponent`. Register the entry provider in `MainActivityContainer`.

### Data flow

Room is the source of truth, and the UI reads from it:

1. **First run:** the splash screen checks the `isPokemonDataCachedFlow` preference. If it's false, the welcome screen downloads every paginated Pokémon list page into Room as stub rows, then sets the flag.
2. **Stub rows:** a `PokemonEntity` whose `type` is `PokemonTypeEnum.UNKNOWN`'s backend string and whose `json` is null has only its id and name.
3. **List:** `searchForPokemonsByNameOrId` returns a Room `Flow`. Stubs are emitted as `PokemonSearchItem.NotLoaded`, which also starts a background fetch. The saved row makes the Flow re-emit with `LoadedPokemon`.
4. **Details:** `getPokemon` waits for a fetch job tracked per id, then decodes the full response from the raw JSON stored in `PokemonEntity.json`. Fetches are limited to 20 concurrent by a semaphore.

### Screen conventions

Feature packages follow `di/`, `route/`, `screen/`, `state/`, and `viewModel/`. ViewModels expose a `uiState` `StateFlow`, and most also take an `*Intent` through `handleIntent` and emit a `*SideEffect` through a `SharedFlow`. `PokemonDetailsViewModel` uses direct methods such as `onNextClick` instead.

Colors and typography come from `compose_core/theme` (`AppTheme`).
