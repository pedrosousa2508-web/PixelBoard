package com.akshaykadam.pixelboard.extension.clipboard;

/**
 * Exact-match policy for the stock Gboard text clipboard character-limit flag.
 *
 * <p>Inspired by Jason Wu's GPLv3 Gboard-patches "Clipboard Custom Character Limit".
 * This never alters other flags, their types, or the Android system clipboard.
 */
final class GboardClipboard100kPolicy {
    static final String TEXT_CLIP_ITEM_CHAR_LIMIT = "text_clip_item_char_limit";
    static final long MAX_CHARACTERS = 100_000L;

    private GboardClipboard100kPolicy() {
    }

    static Object maybeOverride(String flagName, Object stockResult) {
        if (!TEXT_CLIP_ITEM_CHAR_LIMIT.equals(flagName) || !(stockResult instanceof Long)) {
            return stockResult;
        }
        return Long.valueOf(MAX_CHARACTERS);
    }
}
