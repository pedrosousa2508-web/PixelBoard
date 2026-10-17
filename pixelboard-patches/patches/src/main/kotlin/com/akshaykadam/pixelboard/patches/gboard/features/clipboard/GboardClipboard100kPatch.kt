package com.akshaykadam.pixelboard.patches.gboard.features.clipboard

import com.akshaykadam.pixelboard.patches.gboard.shared.GboardFlagFamilyFeature
import com.akshaykadam.pixelboard.patches.gboard.shared.gboardFlagFamilyFeaturePatch

/**
 * Shares the existing verified stock flag getter hook with voice and writing tools.
 * The runtime only changes the Long result for text_clip_item_char_limit.
 *
 * Original technique: Jason Wu's GPLv3 Gboard-patches Clipboard Custom Character Limit.
 */
internal val gboardClipboard100kFlagValuePatch = gboardFlagFamilyFeaturePatch(
    description = "Increase Gboard history's text clip item limit to 100,000 characters.",
    feature = GboardFlagFamilyFeature.CLIPBOARD_100K,
)
