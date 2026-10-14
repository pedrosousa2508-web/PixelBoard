package com.akshaykadam.pixelboard.extension.clipboard;

import org.junit.Assert;
import org.junit.Test;

public class GboardClipboard100kRuntimeTest {
    private static final class Flag {
        private final String a;

        private Flag(String name) {
            a = name;
        }
    }

    private static final class IncompatibleFlag {
        private final int a = 42;
    }

    @Test
    public void overridesOnlyTheExactLongFlag() {
        Object original = Long.valueOf(20_000L);
        Object actual = GboardClipboard100kRuntime.applyOverriddenFlagValue(
                new Flag("text_clip_item_char_limit"), original);
        Assert.assertEquals(Long.valueOf(100_000L), actual);
        Assert.assertEquals(Long.class, actual.getClass());
    }

    @Test
    public void preservesUnrelatedFlagsAndTypes() {
        Object original = Long.valueOf(20_000L);
        Assert.assertSame(original,
                GboardClipboard100kRuntime.applyOverriddenFlagValue(
                        new Flag("another_clipboard_setting"), original));
        Object integer = Integer.valueOf(20_000);
        Assert.assertSame(integer,
                GboardClipboard100kRuntime.applyOverriddenFlagValue(
                        new Flag("text_clip_item_char_limit"), integer));
        Assert.assertSame(original,
                GboardClipboard100kRuntime.applyOverriddenFlagValue(null, original));
        Assert.assertSame(original,
                GboardClipboard100kRuntime.applyOverriddenFlagValue(
                        new IncompatibleFlag(), original));
    }

    @Test
    public void policyRetainsFullTextAtTheBoundaryInASimulatedStorePaste() {
        // This is a contract test of the limit, NOT an instrumented Gboard storage test.
        String sample = "Música brasileira!\n".repeat(5_000);
        String stored = sample.substring(0, Math.min(
                sample.length(),
                ((Long) GboardClipboard100kPolicy.maybeOverride(
                        "text_clip_item_char_limit", Long.valueOf(20_000L))).intValue()));
        Assert.assertEquals(sample, stored);
        Assert.assertTrue(sample.length() > 20_000);
        Assert.assertTrue(sample.length() <= 100_000);
    }

    @Test
    public void oversizedItemIsBoundedTo100000Utf16CodeUnitsInSimulatedStore() {
        String sample = "a".repeat(100_001);
        int max = ((Long) GboardClipboard100kPolicy.maybeOverride(
                "text_clip_item_char_limit", Long.valueOf(20_000L))).intValue();
        String stored = sample.substring(0, Math.min(sample.length(), max));
        Assert.assertEquals(100_000, stored.length());
        Assert.assertEquals(sample.substring(0, 100_000), stored);
    }
}
