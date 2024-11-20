package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import java.util.regex.Pattern;

@Getter
@Setter
public class OtherConfigsMap {

    @Setter
    private static OtherConfigsMap instance;

    private static final String DEFAULT_COMMAND_SUGGESTION_FORMAT = "%dx%d %s";
    private static final Pattern TWO_NUMBER_IN_FORMAT = Pattern.compile("(.*%d.*){2}");
    private static final Pattern ONE_STRING_IN_FORMAT = Pattern.compile(".*%s.*");

    @SerializedName("Enable Biome Indicator")
    private boolean biomeIndicatorEnabled;
    @SerializedName("Enable XP Indicator")
    private boolean xpIndicatorEnabled;
    @SerializedName("Enable Facing Direction")
    private boolean facingDirectionEnabled;
    @SerializedName("Enable Health n Hunger")
    private boolean healthNHungerEnabled;
    @SerializedName("Enable Position Narrator")
    private boolean positionNarratorEnabled;
    @SerializedName("Command Suggestion Narrator Format")
    private String commandSuggestionNarratorFormat;
    @SerializedName("Use 12 Hour Time Format")
    private boolean use12HourTimeFormat;
    @SerializedName("Speak Action Bar Messages")
    private boolean actionBarEnabled;
    @SerializedName("Only Speak Action Bar Updates")
    private boolean onlySpeakActionBarUpdates = false;
    @SerializedName("Speak Harvest Of Fishing")
    private boolean fishingHarvestEnabled;
    @SerializedName("Report Held Items Count When Changed")
    private boolean reportHeldItemsCountWhenChanged = true;
    @SerializedName("Enable Menu Fix")
    private boolean menuFixEnabled;
    @SerializedName("Debug Mode")
    private boolean debugMode;

    /**
     * The maximum time interval between two keystrokes in multiple click operations like "double-click", in milliseconds.
     * According to the UX QA, 500ms is quite a good default time, but I still want to extend it a bit.
     * <a href="https://ux.stackexchange.com/questions/40364/what-is-the-expected-timeframe-of-a-double-click"></a>
     * <p>
     * Can be translated into config item if someone needs it, but not now.
     */
    @SerializedName("Multiple Click Speed (in milliseconds)")
    private int multipleClickSpeedInMilliseconds;

    private OtherConfigsMap() {
    }

    public static OtherConfigsMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public String getCommandSuggestionNarratorFormat() {
        if (!checkSuggestionNarrationFormatIsValid(this.commandSuggestionNarratorFormat)) {
            this.commandSuggestionNarratorFormat = DEFAULT_COMMAND_SUGGESTION_FORMAT;
        }
        return this.commandSuggestionNarratorFormat;
    }

    public void setCommandSuggestionNarratorFormat(String commandSuggestionNarratorFormat) {
        this.commandSuggestionNarratorFormat = checkSuggestionNarrationFormatIsValid(commandSuggestionNarratorFormat) ?
                commandSuggestionNarratorFormat : DEFAULT_COMMAND_SUGGESTION_FORMAT;
    }

    private static boolean checkSuggestionNarrationFormatIsValid(String format) {
        if (Strings.isBlank(format)) return false;
        return TWO_NUMBER_IN_FORMAT.matcher(format).matches() && ONE_STRING_IN_FORMAT.matcher(format).matches();
    }

    public static OtherConfigsMap buildDefault() {
        OtherConfigsMap defaultOtherConfigsMap = new OtherConfigsMap();
        defaultOtherConfigsMap.setBiomeIndicatorEnabled(true);
        defaultOtherConfigsMap.setXpIndicatorEnabled(true);
        defaultOtherConfigsMap.setFacingDirectionEnabled(true);
        defaultOtherConfigsMap.setHealthNHungerEnabled(true);
        defaultOtherConfigsMap.setPositionNarratorEnabled(true);
        defaultOtherConfigsMap.setCommandSuggestionNarratorFormat(DEFAULT_COMMAND_SUGGESTION_FORMAT);
        defaultOtherConfigsMap.setUse12HourTimeFormat(false);
        defaultOtherConfigsMap.setActionBarEnabled(true);
        defaultOtherConfigsMap.onlySpeakActionBarUpdates = false;
        defaultOtherConfigsMap.setFishingHarvestEnabled(true);
        defaultOtherConfigsMap.reportHeldItemsCountWhenChanged = true;
        defaultOtherConfigsMap.setMenuFixEnabled(true);
        defaultOtherConfigsMap.setDebugMode(false);
        defaultOtherConfigsMap.setMultipleClickSpeedInMilliseconds(750);

        setInstance(defaultOtherConfigsMap);
        return defaultOtherConfigsMap;
    }
}
