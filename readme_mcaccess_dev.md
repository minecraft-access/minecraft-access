# MC Access - Development Additions (by RanksemC)

This is an experimental development branch of **MC Access**, the accessibility mod for Minecraft Java Edition created by [PlayAbility](https://github.com/PlayAbilityTeam/MC-Access).

The purpose of this branch is to extend the client-side accessibility systems with additional environmental awareness features, while keeping full compatibility with multiplayer and without using server-side logic or invasive mixins.

---

## 🔧 Additions & Features

### 🌦 Weather Detector
- Narrates current weather conditions.
- Fully functional and stable.
- Example: “It’s raining.” or “Clear skies.”

### 🌅 Day/Night Detector
- Detects time of day and narrates transitions between day and night.
- 90% complete, pending fine-tuning for smoother narration timing.

### 🏘 Structure Detector (Villages)
- Scans nearby chunks for entities related to villages (Villagers, Cats, Iron Golems, Bells, etc.).
- Runs client-side only, so it doesn’t affect multiplayer performance.
- Experimental but working correctly.

### ⚡ Redstone Analyzer (Very Early Prototype)
- Goal: Identify connected redstone components (dispensers, observers, pistons, etc.).
- Currently in very early stages (around 4% progress).
- Disabled by default to avoid lag spikes.

---

## 🧩 Design Philosophy

- All logic runs **client-side**, for safe multiplayer use.
- Avoids modifying core classes via Mixins whenever possible.
- Compatible with the main MC Access architecture and narrator system.
- Performance-conscious scanning: currently limited to entity-based detection.

---

## 📚 Repository & Contributions

**Main repository:**
👉 [https://github.com/PlayAbilityTeam/MC-Access](https://github.com/PlayAbilityTeam/MC-Access)

**This branch (your fork):**
Experimental “RanksemC” development fork for testing accessibility extensions.

---

## 🧠 Planned Work

- Add `en_us.json` and `es_es.json` translation keys for all new narrations.
- Implement limited block-based structure hints once performance allows.
- Add redstone connection descriptions with directional awareness.
- Optionally integrate toggles in MC Access Settings (Accessibility → Environment).

---

## 🧩 Credits

- Original project by **PlayAbility Team**
- Accessibility extensions and experimental features by **Gamxpro**
