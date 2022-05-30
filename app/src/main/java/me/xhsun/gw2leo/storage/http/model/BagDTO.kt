package me.xhsun.gw2leo.storage.http.model

/**
 * Contains one object structure per bag in the character's inventory
 */
data class BagDTO(
    val id: Int,
    val size: Int,
    /**
     * Contains one object structure per item, object is null if no item is in the given bag slot.
     */
    val inventory: List<InventoryDTO?>
)
