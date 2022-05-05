package io.github.invvk.mony.tests.utils;

import io.github.invvk.mony.utils.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class TestUtils {

    @ParameterizedTest
    @DisplayName("Check Time Stamp Validator")
    @ValueSource(strings = {"0:00", "12:0", "0:0", "23:43", "1:1"})
    public void checkTimeStampValidator(String stamp) {
        Assertions.assertTrue(Utils.validateTimeStamp(stamp));
    }

}
