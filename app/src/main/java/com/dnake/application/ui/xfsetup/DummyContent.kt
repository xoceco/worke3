package com.dnake.application.ui.xfsetup

import java.util.*

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<DummyItem> = ArrayList()
    var dw=7

    /**
     * A map of sample (dummy) items, by ID.
     */

    private val COUNT = 14

    init {

    }

    private fun addItem(item: DummyItem) {
        ITEMS.add(item)
     }
  private fun makeDetails(position: Int): String {

        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A dummy item representing a piece of content.
     */
    data class DummyItem(val id: String, public var content: Int, val details: String) {
        override fun toString(): String = content.toString();
    }
}