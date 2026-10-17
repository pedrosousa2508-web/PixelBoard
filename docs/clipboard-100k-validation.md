# PixelBoard: 100,000-character clipboard history patch

Status: **candidate, not device-verified**. Do not announce working 100K support
until the tests below pass with a real Gboard 18.4.1 APK.

## Approach

The GPLv3 upstream project `jasonwu1994/Gboard-patches` implements
"Clipboard Custom Character Limit" by overriding the Gboard flag named
`text_clip_item_char_limit`, whose stock result is a `Long`.
PixelBoard now includes an **independent** `Clipboard 100K Character Limit`
patch with a fixed `100_000L` replacement.

The implementation reuses PixelBoard's existing flag-family bytecode composer:
no separate hook is installed in Gboard's clipboard UI or storage path.
The runtime checks for the **exact** flag name and `Long` type and falls back
to the original value on reflection/incompatibility failures.
It never reads, transmits, or persists clipboard contents.

No Gboard 18.4.1 APK was available when this change was authored; although
PixelBoard's existing composer has a target for that version, the presence of
the flag and full persistence/paste behavior remain **unverified**. Additional
limits in Gboard storage, Binder transactions, or target apps may exist.

## Build-time checks

1. Run `cd pixelboard-patches && ./gradlew test`.
2. Run `cd pixelboard-patches && ./gradlew :patches:buildAndroid`.
3. Rebuild the bundle with `./build.sh --rebuild` (the checked-in binary\n   `patches/PixelBoard.mpp` is still the original and does not contain the new patch).\n4. Supply the compatible licensed stock base at `input/gboard.apk`; compile
   and sign the patched APK using the project's established build procedure.
5. Verify that the patcher reports the clipboard patch as applied, with no
   missing classes or changed flag getter fingerprint. If it fails, do **not**
   install the APK. Use the stock keyboard until analyzed.

## Android device acceptance test (Gboard 18.4.1)

Test with *PixelBoard's clipboard history card*, not solely the Android system
paste menu. Never test with sensitive clipboard contents.

For each sample, create a known deterministic UTF-16 string, copy it, open
PixelBoard's clipboard history, tap the saved card and paste into a text field
that can hold at least 110,000 characters, then compare **exact content and
length** against the source (prefer a local editor capable of comparing data).

- 19,999 and 20,000 characters: backwards compatibility.
- 20,001 and 50,000: no truncation at the former boundary.
- 99,999 and 100,000: full, exact copy/history/paste, including line breaks.
- 100,001: should be capped to 100,000 if Gboard uses the flag as expected.
- Include Portuguese accents, emojis, and multiline lyrics. Android
  `String.length` counts UTF-16 code units (many emojis count as two).
- Copy several long clips successively, pin/unpin and reopen history, and
  repeat after restarting the keyboard and rebooting the phone.
- Confirm typed input, Rambler voice typing, AI writing tools, and ordinary
  system copy/paste still work; check memory use and UI responsiveness.

No automated JVM test can certify Gboard's own persistence or on-device
paste behavior; the provided unit tests validate policy, type safety, and a
simulated boundary only.

## Credit

Based on the technique published in Jason Wu's Gboard-patches, GPLv3:
https://github.com/jasonwu1994/Gboard-patches
