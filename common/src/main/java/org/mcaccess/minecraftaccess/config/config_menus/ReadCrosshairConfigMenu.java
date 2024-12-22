package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.RCPartialSpeakingConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.RCRelativePositionSoundCueConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.ReadCrosshairConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

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

        ButtonWidget featureToggleButton = this.buildButtonWidget(featureToggleButtonMessage(initMap.isEnabled()),
                (button) -> {
                    ReadCrosshairConfigMap map = ReadCrosshairConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(Text.of(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addDrawableChild(featureToggleButton);

        Function<Boolean, String> useJadeText = featureToggleButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.use_jade_button");
        ButtonWidget useJadeButton = this.buildButtonWidget(
                useJadeText.apply(initMap.isUseJade()),
                (button) -> {
                    ReadCrosshairConfigMap map = ReadCrosshairConfigMap.getInstance();
                    map.setUseJade(!map.isUseJade());
                    button.setMessage(Text.of(useJadeText.apply(map.isUseJade())));
                    Config.getInstance().writeJSON();
                });
        try {
            Class.forName("snownee.jade.overlay.WailaTickHandler");
        } catch (ClassNotFoundException e) {
            useJadeButton.active = false;
            useJadeButton.setMessage(Text.of(I18n.translate("minecraft_access.gui.read_crosshair_config_menu.button.use_jade_button.unavailable")));
        }
        addDrawableChild(useJadeButton);

        Function<Boolean, String> speakBlockSidesText = featureToggleButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.speak_block_sides_button");
        ButtonWidget speakBlockSidesButton = this.buildButtonWidget(
                speakBlockSidesText.apply(initMap.isSpeakSide()),
                (button) -> {
                    ReadCrosshairConfigMap map = ReadCrosshairConfigMap.getInstance();
                    map.setSpeakSide(!map.isSpeakSide());
                    button.setMessage(Text.of(speakBlockSidesText.apply(map.isSpeakSide())));
                    Config.getInstance().writeJSON();
                });
        this.addDrawableChild(speakBlockSidesButton);

        Function<Boolean, String> disableConsecutiveBlocksText = featureToggleButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.disable_speaking_consecutive_blocks_button");
        ButtonWidget disableConsecutiveBlocksButton = this.buildButtonWidget(
                disableConsecutiveBlocksText.apply(initMap.isDisableSpeakingConsecutiveBlocks()),
                (button) -> {
                    ReadCrosshairConfigMap map = ReadCrosshairConfigMap.getInstance();
                    map.setDisableSpeakingConsecutiveBlocks(!map.isDisableSpeakingConsecutiveBlocks());
                    button.setMessage(Text.of(disableConsecutiveBlocksText.apply(map.isDisableSpeakingConsecutiveBlocks())));
                    Config.getInstance().writeJSON();
                },
                true);
        this.addDrawableChild(disableConsecutiveBlocksButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> ReadCrosshairConfigMap.getInstance().getRepeatSpeakingInterval(),
                (v) -> ReadCrosshairConfigMap.getInstance().setRepeatSpeakingInterval(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget repeatSpeakingIntervalButton = this.buildButtonWidget(
                floatValueButtonMessageWith("minecraft_access.gui.read_crosshair_config_menu.button.repeat_speaking_interval_button",
                        initMap.getRepeatSpeakingInterval()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c1, this)),
                true);
        this.addDrawableChild(repeatSpeakingIntervalButton);

        ButtonWidget rcSoundMenuButton = this.buildButtonWidget("minecraft_access.gui.read_crosshair_config_menu.button.relative_position_sound_cue_menu_button",
                (button) -> this.client.setScreen(new RCRelativePositionSoundCueConfigMenu("relative_position_sound_cue_menu", this)));
        this.addDrawableChild(rcSoundMenuButton);

        ButtonWidget rcPartialSpeakingMenuButton = this.buildButtonWidget("minecraft_access.gui.read_crosshair_config_menu.button.partial_speaking_menu_button",
                (button) -> this.client.setScreen(new RCPartialSpeakingConfigMenu("rc_partial_speaking_menu", this)));
        this.addDrawableChild(rcPartialSpeakingMenuButton);
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

        ButtonWidget featureToggleButton = this.buildButtonWidget(featureToggleButtonMessage(initMap.isEnabled()),
                (button) -> {
                    RCPartialSpeakingConfigMap map = RCPartialSpeakingConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(Text.of(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addDrawableChild(featureToggleButton);

        Function<Boolean, String> partialSpeakingWhitelistModeText = featureToggleButtonMessageWith("minecraft_access.gui.rc_partial_speaking_menu.button.partial_speaking_whitelist_mode_button");
        ButtonWidget partialSpeakingWhitelistModeButton = this.buildButtonWidget(
                partialSpeakingWhitelistModeText.apply(initMap.isPartialSpeakingWhitelistMode()),
                (button) -> {
                    RCPartialSpeakingConfigMap map = RCPartialSpeakingConfigMap.getInstance();
                    map.setPartialSpeakingWhitelistMode(!map.isPartialSpeakingWhitelistMode());
                    button.setMessage(Text.of(partialSpeakingWhitelistModeText.apply(map.isPartialSpeakingWhitelistMode())));
                    Config.getInstance().writeJSON();
                });
        this.addDrawableChild(partialSpeakingWhitelistModeButton);

        Function<Boolean, String> partialSpeakingFuzzyModeText = featureToggleButtonMessageWith("minecraft_access.gui.rc_partial_speaking_menu.button.partial_speaking_fuzzy_mode_button");
        ButtonWidget partialSpeakingFuzzyModeButton = this.buildButtonWidget(
                partialSpeakingFuzzyModeText.apply(initMap.isPartialSpeakingFuzzyMode()),
                (button) -> {
                    RCPartialSpeakingConfigMap map = RCPartialSpeakingConfigMap.getInstance();
                    map.setPartialSpeakingFuzzyMode(!map.isPartialSpeakingFuzzyMode());
                    button.setMessage(Text.of(partialSpeakingFuzzyModeText.apply(map.isPartialSpeakingFuzzyMode())));
                    Config.getInstance().writeJSON();
                });
        this.addDrawableChild(partialSpeakingFuzzyModeButton);
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

        ButtonWidget featureToggleButton = this.buildButtonWidget(featureToggleButtonMessage(initMap.isEnabled()),
                (button) -> {
                    RCRelativePositionSoundCueConfigMap map = RCRelativePositionSoundCueConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(Text.of(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addDrawableChild(featureToggleButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> RCRelativePositionSoundCueConfigMap.getInstance().getMinSoundVolume(),
                (v) -> RCRelativePositionSoundCueConfigMap.getInstance().setMinSoundVolume(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget minVolumeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.min_volume", initMap.getMinSoundVolume()),
                (button) -> Objects.requireNonNull(this.client).setScreen(new ValueEntryMenu(c1, this)));
        this.addDrawableChild(minVolumeButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> RCRelativePositionSoundCueConfigMap.getInstance().getMaxSoundVolume(),
                (v) -> RCRelativePositionSoundCueConfigMap.getInstance().setMaxSoundVolume(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget maxVolumeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.max_volume", initMap.getMaxSoundVolume()),
                (button) -> Objects.requireNonNull(this.client).setScreen(new ValueEntryMenu(c2, this)));
        this.addDrawableChild(maxVolumeButton);
    }
}