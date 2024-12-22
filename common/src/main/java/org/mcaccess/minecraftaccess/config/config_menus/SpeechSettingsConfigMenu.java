package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.SpeechSettingsConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@SuppressWarnings("DataFlowIssue")
public class SpeechSettingsConfigMenu extends BaseScreen {
    public SpeechSettingsConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        SpeechSettingsConfigMap initMap = SpeechSettingsConfigMap.getInstance();

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> SpeechSettingsConfigMap.getInstance().getSpeechRate(),
                (v) -> SpeechSettingsConfigMap.getInstance().setSpeechRate(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget speechRateButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.translate("minecraft_access.gui.speech_settings_config_menu.button.speech_rate"),
                        initMap.getSpeechRate()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c1, this)));
        this.addDrawableChild(speechRateButton);
    }
}
