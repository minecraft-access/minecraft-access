package org.mcaccess.minecraftaccess.config.config_menus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.RCPartialSpeakingConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.RCRelativePositionSoundCueConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.ReadCrosshairConfigMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;

import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("DataFlowIssue")
public class ReadCrosshairConfigMenu extends BaseScreen {
    public ReadCrosshairConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        ReadCrosshairConfigMap initMap = ReadCrosshairConfigMap.getInstance();

        Button featureToggleButton = this.buildButtonWidget(featureToggleButtonMessage(initMap.isEnabled()),
                (button) -> {
                    ReadCrosshairConfigMap map = ReadCrosshairConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(Component.nullToEmpty(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addRenderableWidget(featureToggleButton);

        Function<Boolean, String> useJadeText = featureToggleButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.use_jade_button");
        Button useJadeButton = this.buildButtonWidget(
                useJadeText.apply(initMap.isUseJade()),
                (button) -> {
                    ReadCrosshairConfigMap map = ReadCrosshairConfigMap.getInstance();
                    map.setUseJade(!map.isUseJade());
                    button.setMessage(Component.nullToEmpty(useJadeText.apply(map.isUseJade())));
                    Config.getInstance().writeJSON();
                });
        try {
            Class.forName("snownee.jade.overlay.WailaTickHandler");
        } catch (ClassNotFoundException e) {
            useJadeButton.active = false;
            useJadeButton.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.read_crosshair_config_menu.button.use_jade_button.unavailable")));
        }
        addRenderableWidget(useJadeButton);

        Function<Boolean, String> speakBlockSidesText = featureToggleButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.speak_block_sides_button");
        Button speakBlockSidesButton = this.buildButtonWidget(
                speakBlockSidesText.apply(initMap.isSpeakSide()),
                (button) -> {
                    ReadCrosshairConfigMap map = ReadCrosshairConfigMap.getInstance();
                    map.setSpeakSide(!map.isSpeakSide());
                    button.setMessage(Component.nullToEmpty(speakBlockSidesText.apply(map.isSpeakSide())));
                    Config.getInstance().writeJSON();
                });
        this.addRenderableWidget(speakBlockSidesButton);

        Function<Boolean, String> speakAdditionalEntityPosesText = featureToggleButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.speak_additional_entity_poses_button");
        Button speakAdditionalEntityPosesButton = this.buildButtonWidget(
                speakAdditionalEntityPosesText.apply(initMap.isSpeakAdditionalEntityPoses()),
                (button) -> {
                    ReadCrosshairConfigMap map = ReadCrosshairConfigMap.getInstance();
                    map.setSpeakAdditionalEntityPoses(!map.isSpeakAdditionalEntityPoses());
                    button.setMessage(Component.nullToEmpty(speakAdditionalEntityPosesText.apply(map.isSpeakAdditionalEntityPoses())));
                    Config.getInstance().writeJSON();
                });
        this.addRenderableWidget(speakAdditionalEntityPosesButton);

        Function<Boolean, String> disableConsecutiveBlocksText = featureToggleButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.disable_speaking_consecutive_blocks_button");
        Button disableConsecutiveBlocksButton = this.buildButtonWidget(
                disableConsecutiveBlocksText.apply(initMap.isDisableSpeakingConsecutiveBlocks()),
                (button) -> {
                    ReadCrosshairConfigMap map = ReadCrosshairConfigMap.getInstance();
                    map.setDisableSpeakingConsecutiveBlocks(!map.isDisableSpeakingConsecutiveBlocks());
                    button.setMessage(Component.nullToEmpty(disableConsecutiveBlocksText.apply(map.isDisableSpeakingConsecutiveBlocks())));
                    Config.getInstance().writeJSON();
                },
                true);
        this.addRenderableWidget(disableConsecutiveBlocksButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> ReadCrosshairConfigMap.getInstance().getRepeatSpeakingInterval(),
                (v) -> ReadCrosshairConfigMap.getInstance().setRepeatSpeakingInterval(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        Button repeatSpeakingIntervalButton = this.buildButtonWidget(
                floatValueButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.repeat_speaking_interval_button",
                        initMap.getRepeatSpeakingInterval()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c1, this)),
                true);
        this.addRenderableWidget(repeatSpeakingIntervalButton);

        Button rcSoundMenuButton = this.buildButtonWidget("minecraft_access.gui.read_crosshair_config_menu.button.relative_position_sound_cue_menu_button",
                (button) -> this.minecraft.setScreen(new RCRelativePositionSoundCueConfigMenu("relative_position_sound_cue_menu", this)));
        this.addRenderableWidget(rcSoundMenuButton);

        Button rcPartialSpeakingMenuButton = this.buildButtonWidget("minecraft_access.gui.read_crosshair_config_menu.button.partial_speaking_menu_button",
                (button) -> this.minecraft.setScreen(new RCPartialSpeakingConfigMenu("rc_partial_speaking_menu", this)));
        this.addRenderableWidget(rcPartialSpeakingMenuButton);
    }
}

class RCPartialSpeakingConfigMenu extends BaseScreen {

    public RCPartialSpeakingConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        RCPartialSpeakingConfigMap initMap = RCPartialSpeakingConfigMap.getInstance();

        Button featureToggleButton = this.buildButtonWidget(featureToggleButtonMessage(initMap.isEnabled()),
                (button) -> {
                    RCPartialSpeakingConfigMap map = RCPartialSpeakingConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(Component.nullToEmpty(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addRenderableWidget(featureToggleButton);

        Function<Boolean, String> partialSpeakingWhitelistModeText = featureToggleButtonMessageWith("minecraft_access.gui.rc_partial_speaking_menu.button.partial_speaking_whitelist_mode_button");
        Button partialSpeakingWhitelistModeButton = this.buildButtonWidget(
                partialSpeakingWhitelistModeText.apply(initMap.isPartialSpeakingWhitelistMode()),
                (button) -> {
                    RCPartialSpeakingConfigMap map = RCPartialSpeakingConfigMap.getInstance();
                    map.setPartialSpeakingWhitelistMode(!map.isPartialSpeakingWhitelistMode());
                    button.setMessage(Component.nullToEmpty(partialSpeakingWhitelistModeText.apply(map.isPartialSpeakingWhitelistMode())));
                    Config.getInstance().writeJSON();
                });
        this.addRenderableWidget(partialSpeakingWhitelistModeButton);

        Function<Boolean, String> partialSpeakingFuzzyModeText = featureToggleButtonMessageWith("minecraft_access.gui.rc_partial_speaking_menu.button.partial_speaking_fuzzy_mode_button");
        Button partialSpeakingFuzzyModeButton = this.buildButtonWidget(
                partialSpeakingFuzzyModeText.apply(initMap.isPartialSpeakingFuzzyMode()),
                (button) -> {
                    RCPartialSpeakingConfigMap map = RCPartialSpeakingConfigMap.getInstance();
                    map.setPartialSpeakingFuzzyMode(!map.isPartialSpeakingFuzzyMode());
                    button.setMessage(Component.nullToEmpty(partialSpeakingFuzzyModeText.apply(map.isPartialSpeakingFuzzyMode())));
                    Config.getInstance().writeJSON();
                });
        this.addRenderableWidget(partialSpeakingFuzzyModeButton);
    }
}

class RCRelativePositionSoundCueConfigMenu extends BaseScreen {

    public RCRelativePositionSoundCueConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();
        RCRelativePositionSoundCueConfigMap initMap = RCRelativePositionSoundCueConfigMap.getInstance();

        Button featureToggleButton = this.buildButtonWidget(featureToggleButtonMessage(initMap.isEnabled()),
                (button) -> {
                    RCRelativePositionSoundCueConfigMap map = RCRelativePositionSoundCueConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(Component.nullToEmpty(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addRenderableWidget(featureToggleButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> RCRelativePositionSoundCueConfigMap.getInstance().getMinSoundVolume(),
                (v) -> RCRelativePositionSoundCueConfigMap.getInstance().setMinSoundVolume(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        Button minVolumeButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.min_volume", initMap.getMinSoundVolume()),
                (button) -> Objects.requireNonNull(minecraft).setScreen(new ValueEntryMenu(c1, this)));
        this.addRenderableWidget(minVolumeButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> RCRelativePositionSoundCueConfigMap.getInstance().getMaxSoundVolume(),
                (v) -> RCRelativePositionSoundCueConfigMap.getInstance().setMaxSoundVolume(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        Button maxVolumeButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.max_volume", initMap.getMaxSoundVolume()),
                (button) -> Objects.requireNonNull(minecraft).setScreen(new ValueEntryMenu(c2, this)));
        this.addRenderableWidget(maxVolumeButton);
    }
}