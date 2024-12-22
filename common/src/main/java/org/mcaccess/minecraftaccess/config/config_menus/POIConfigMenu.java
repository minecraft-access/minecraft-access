package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.POIBlocksConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.POIEntitiesConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.POILockingConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.POIMarkingConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import java.util.function.Function;

@SuppressWarnings("DataFlowIssue")
public class POIConfigMenu extends BaseScreen {
    public POIConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        ButtonWidget poiBlocksButton = this.buildButtonWidget("minecraft_access.gui.poi_config_menu.button.poi_blocks_button",
                (button) -> this.client.setScreen(new POIBlocksConfigMenu("poi_blocks_config_menu", this)));
        this.addDrawableChild(poiBlocksButton);

        ButtonWidget poiEntitiesButton = this.buildButtonWidget("minecraft_access.gui.poi_config_menu.button.poi_entities_button",
                (button) -> this.client.setScreen(new POIEntitiesConfigMenu("poi_entities_config_menu", this)));
        this.addDrawableChild(poiEntitiesButton);

        ButtonWidget poiLockingButton = this.buildButtonWidget("minecraft_access.gui.poi_config_menu.button.poi_locking_button",
                (button) -> this.client.setScreen(new POILockingConfigMenu("poi_locking_config_menu", this)));
        this.addDrawableChild(poiLockingButton);

        ButtonWidget poiMarkingButton = this.buildButtonWidget("minecraft_access.gui.poi_config_menu.button.poi_marking_button",
                (button) -> this.client.setScreen(new POIMarkingConfigMenu("poi_marking_config_menu", this)));
        this.addDrawableChild(poiMarkingButton);
    }
}

@SuppressWarnings("DataFlowIssue")
class POIBlocksConfigMenu extends BaseScreen {
    public POIBlocksConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        POIBlocksConfigMap initMap = POIBlocksConfigMap.getInstance();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    POIBlocksConfigMap map = POIBlocksConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addDrawableChild(featureToggleButton);

        ButtonWidget detectFluidBlocksButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isDetectFluidBlocks() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_blocks_config_menu.button.detect_fluid_blocks_button")
                ),
                (button) -> {
                    POIBlocksConfigMap map = POIBlocksConfigMap.getInstance();
                    map.setDetectFluidBlocks(!map.isDetectFluidBlocks());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isDetectFluidBlocks() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_blocks_config_menu.button.detect_fluid_blocks_button")
                    )));
                });
        this.addDrawableChild(detectFluidBlocksButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> POIBlocksConfigMap.getInstance().getRange(),
                (v) -> POIBlocksConfigMap.getInstance().setRange(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget rangeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.range", initMap.getRange()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c1, this)));
        this.addDrawableChild(rangeButton);

        ButtonWidget playSoundButton = this.buildButtonWidget("minecraft_access.gui.common.button.play_sound_toggle_button." + (initMap.isPlaySound() ? "enabled" : "disabled"),
                (button) -> {
                    POIBlocksConfigMap map = POIBlocksConfigMap.getInstance();
                    map.setPlaySound(!map.isPlaySound());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.play_sound_toggle_button." + (map.isPlaySound() ? "enabled" : "disabled"))));
                });
        this.addDrawableChild(playSoundButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> POIBlocksConfigMap.getInstance().getVolume(),
                (v) -> POIBlocksConfigMap.getInstance().setVolume(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget volumeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.volume", initMap.getVolume()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c2, this)));
        this.addDrawableChild(volumeButton);

        ButtonWidget playSoundForOtherBlocksButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isPlaySoundForOtherBlocks() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_blocks_config_menu.button.play_sound_for_other_blocks_button")
                ),
                (button) -> {
                    POIBlocksConfigMap map = POIBlocksConfigMap.getInstance();
                    map.setPlaySoundForOtherBlocks(!map.isPlaySoundForOtherBlocks());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isPlaySoundForOtherBlocks() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_blocks_config_menu.button.play_sound_for_other_blocks_button")
                    )));
                },
                true);
        this.addDrawableChild(playSoundForOtherBlocksButton);

        ValueEntryMenu.ValueConfig c3 = new ValueEntryMenu.ValueConfig(() -> POIBlocksConfigMap.getInstance().getDelay(),
                (v) -> POIBlocksConfigMap.getInstance().setDelay(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget delayButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.delay", initMap.getDelay()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c3, this)));
        this.addDrawableChild(delayButton);
    }
}

@SuppressWarnings("DataFlowIssue")
class POIEntitiesConfigMenu extends BaseScreen {
    public POIEntitiesConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        POIEntitiesConfigMap initMap = POIEntitiesConfigMap.getInstance();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    POIEntitiesConfigMap map = POIEntitiesConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addDrawableChild(featureToggleButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> POIEntitiesConfigMap.getInstance().getRange(),
                (v) -> POIEntitiesConfigMap.getInstance().setRange(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget rangeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.range", initMap.getRange()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c1, this)));
        this.addDrawableChild(rangeButton);

        ButtonWidget playSoundButton = this.buildButtonWidget("minecraft_access.gui.common.button.play_sound_toggle_button." + (initMap.isPlaySound() ? "enabled" : "disabled"),
                (button) -> {
                    POIEntitiesConfigMap map = POIEntitiesConfigMap.getInstance();
                    map.setPlaySound(!map.isPlaySound());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.play_sound_toggle_button." + (map.isPlaySound() ? "enabled" : "disabled"))));
                });
        this.addDrawableChild(playSoundButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> POIEntitiesConfigMap.getInstance().getVolume(),
                (v) -> POIEntitiesConfigMap.getInstance().setVolume(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget volumeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.volume", initMap.getVolume()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c2, this)));
        this.addDrawableChild(volumeButton);

        ValueEntryMenu.ValueConfig c3 = new ValueEntryMenu.ValueConfig(() -> POIEntitiesConfigMap.getInstance().getDelay(),
                (v) -> POIEntitiesConfigMap.getInstance().setDelay(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget delayButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.delay", initMap.getDelay()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c3, this)));
        this.addDrawableChild(delayButton);
    }
}

@SuppressWarnings("DataFlowIssue")
class POILockingConfigMenu extends BaseScreen {
    public POILockingConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        POILockingConfigMap initMap = POILockingConfigMap.getInstance();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    POILockingConfigMap map = POILockingConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addDrawableChild(featureToggleButton);

        ButtonWidget lockOnBlocksButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isLockOnBlocks() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.lock_on_blocks_button")
                ),
                (button) -> {
                    POILockingConfigMap map = POILockingConfigMap.getInstance();
                    map.setLockOnBlocks(!map.isLockOnBlocks());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isLockOnBlocks() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.lock_on_blocks_button")
                    )));
                });
        this.addDrawableChild(lockOnBlocksButton);

        ButtonWidget speakDistanceButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isSpeakDistance() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.speak_distance_button")
                ),
                (button) -> {
                    POILockingConfigMap map = POILockingConfigMap.getInstance();
                    map.setSpeakDistance(!map.isSpeakDistance());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isSpeakDistance() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.speak_distance_button")
                    )));
                },
                true);
        this.addDrawableChild(speakDistanceButton);

        ButtonWidget autoLockEyeOfEnderButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isAutoLockEyeOfEnderEntity() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.auto_lock_eye_of_ender_button")
                ),
                (button) -> {
                    POILockingConfigMap map = POILockingConfigMap.getInstance();
                    map.setAutoLockEyeOfEnderEntity(!map.isAutoLockEyeOfEnderEntity());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isAutoLockEyeOfEnderEntity() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.auto_lock_eye_of_ender_button")
                    )));
                },
                true);
        this.addDrawableChild(autoLockEyeOfEnderButton);

        ButtonWidget unlockingSoundButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isUnlockingSound() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.unlocking_sound_button")
                ),
                (button) -> {
                    POILockingConfigMap map = POILockingConfigMap.getInstance();
                    map.setUnlockingSound(!map.isUnlockingSound());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isUnlockingSound() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.unlocking_sound_button")
                    )));
                });
        this.addDrawableChild(unlockingSoundButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> POILockingConfigMap.getInstance().getDelay(),
                (v) -> POILockingConfigMap.getInstance().setDelay(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget delayButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.delay", initMap.getDelay()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c1, this)));
        this.addDrawableChild(delayButton);

        ButtonWidget aimAssistButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isAimAssistEnabled() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.aim_assist_button")
                ),
                (button) -> {
                    POILockingConfigMap map = POILockingConfigMap.getInstance();
                    map.setAimAssistEnabled(!map.isAimAssistEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isAimAssistEnabled() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.aim_assist_button")
                    )));
                });
        this.addDrawableChild(aimAssistButton);

                ButtonWidget aimAssistAudioCuesButton = this.buildButtonWidget(
                        I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isAimAssistAudioCuesEnabled() ? "enabled" : "disabled"),
                                I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.aim_assist_audio_cues_button")
                        ),
                        (button) -> {
                            POILockingConfigMap map = POILockingConfigMap.getInstance();
                            map.setAimAssistAudioCuesEnabled(!map.isAimAssistAudioCuesEnabled());
                            Config.getInstance().writeJSON();
                            button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isAimAssistAudioCuesEnabled() ? "enabled" : "disabled"),
                                    I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.aim_assist_audio_cues_button")
                            )));
                        });
                this.addDrawableChild(aimAssistAudioCuesButton);

                ValueEntryMenu.ValueConfig aimAssistAudioCuesVolumeConfig = new ValueEntryMenu.ValueConfig(
                        () -> POILockingConfigMap.getInstance().getAimAssistAudioCuesVolume(),
                        (v) -> POILockingConfigMap.getInstance().setAimAssistAudioCuesVolume(Float.parseFloat(v)),
                        ValueEntryMenu.ValueType.FLOAT
                );
                ButtonWidget aimAssistAudioCuesVolumeButton = this.buildButtonWidget(
                        I18n.translate("minecraft_access.gui.poi_locking_config_menu.button.aim_assist_audio_cues_volume", initMap.getAimAssistAudioCuesVolume()),
                        (button) -> this.client.setScreen(new ValueEntryMenu(aimAssistAudioCuesVolumeConfig, this))
                );
                this.addDrawableChild(aimAssistAudioCuesVolumeButton);
    }
}

class POIMarkingConfigMenu extends BaseScreen {
    public POIMarkingConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        POIMarkingConfigMap initMap = POIMarkingConfigMap.getInstance();

        ButtonWidget b1 = this.buildButtonWidget(featureToggleButtonMessage(initMap.isEnabled()),
                (button) -> {
                    POIMarkingConfigMap map = POIMarkingConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(Text.of(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addDrawableChild(b1);

        Function<Boolean, String> t2 = featureToggleButtonMessageWith("minecraft_access.gui.poi_marking_config_menu.button.suppress_other_when_enabled_button");
        ButtonWidget b2 = this.buildButtonWidget(
                t2.apply(initMap.isSuppressOtherWhenEnabled()),
                (button) -> {
                    POIMarkingConfigMap map = POIMarkingConfigMap.getInstance();
                    map.setSuppressOtherWhenEnabled(!map.isSuppressOtherWhenEnabled());
                    button.setMessage(Text.of(t2.apply(map.isSuppressOtherWhenEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addDrawableChild(b2);
    }
}