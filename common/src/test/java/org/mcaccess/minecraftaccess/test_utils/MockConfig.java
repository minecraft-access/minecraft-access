package org.mcaccess.minecraftaccess.test_utils;

import org.mcaccess.minecraftaccess.config.Config;

/**
 * Do not access json file to reduce the test suite execution time.
 */
public class MockConfig extends Config {
    @Override
    public void loadConfig() {
        super.resetConfigToDefault();
    }

    @Override
    public void writeJSON() {
        // do nothing
    }
}
