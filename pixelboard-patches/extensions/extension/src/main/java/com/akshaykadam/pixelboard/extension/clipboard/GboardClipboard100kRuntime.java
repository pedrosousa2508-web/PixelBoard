package com.akshaykadam.pixelboard.extension.clipboard;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Narrow, fail-closed override for Gboard's text clipboard history item length.
 * The original flag result is retained if the Gboard implementation differs.
 *
 * <p>Does not read, log, save, transmit, or retain clipboard contents.
 */
@SuppressWarnings("unused")
public final class GboardClipboard100kRuntime {
    private static final ConcurrentHashMap<Class<?>, Field> FLAG_NAME_FIELDS =
            new ConcurrentHashMap<Class<?>, Field>();

    private GboardClipboard100kRuntime() {
    }

    /** Called after the existing stock flag getter, sharing its verified hook chain. */
    public static Object applyOverriddenFlagValue(Object receiver, Object stockResult) {
        if (receiver == null || !(stockResult instanceof Long)) {
            return stockResult;
        }
        try {
            Class<?> receiverClass = receiver.getClass();
            Field nameField = FLAG_NAME_FIELDS.get(receiverClass);
            if (nameField == null) {
                Field candidate = receiverClass.getDeclaredField("a");
                if (candidate.getType() != String.class) {
                    return stockResult;
                }
                candidate.setAccessible(true);
                Field old = FLAG_NAME_FIELDS.putIfAbsent(receiverClass, candidate);
                nameField = old == null ? candidate : old;
            }
            return GboardClipboard100kPolicy.maybeOverride(
                    (String) nameField.get(receiver), stockResult);
        } catch (Throwable ignored) {
            // Never interrupt typing on incompatible/changed Gboard bytecode.
            return stockResult;
        }
    }
}
