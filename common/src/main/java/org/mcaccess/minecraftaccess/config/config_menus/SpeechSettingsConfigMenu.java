package org.mcaccess.minecraftaccess.config.config_menus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.config.config_maps.SpeechSettingsConfigMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;

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
        Button speechRateButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.get("minecraft_access.gui.speech_settings_config_menu.button.speech_rate"),
                        initMap.getSpeechRate()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c1, this)));
        this.addRenderableWidget(speechRateButton);
    }
}
