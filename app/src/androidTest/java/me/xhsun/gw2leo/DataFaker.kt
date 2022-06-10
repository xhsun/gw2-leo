package me.xhsun.gw2leo

import io.github.serpro69.kfaker.Faker
import me.xhsun.gw2leo.account.Account
import me.xhsun.gw2leo.account.datastore.entity.Character
import me.xhsun.gw2leo.account.http.model.AccountDTO
import me.xhsun.gw2leo.storage.Item
import me.xhsun.gw2leo.storage.MaterialCategory
import me.xhsun.gw2leo.storage.StorageItem
import me.xhsun.gw2leo.storage.datastore.entity.LastModified
import me.xhsun.gw2leo.storage.datastore.entity.MaterialStorage
import me.xhsun.gw2leo.storage.datastore.entity.Storage
import me.xhsun.gw2leo.storage.http.model.*
import java.util.concurrent.TimeUnit

class DataFaker {
    private val faker = Faker()

    fun accountFaker(
        id: String = faker.random.randomString(),
        name: String = faker.random.randomString(),
        api: String = faker.random.nextUUID()
    ): Account {
        return Account(
            id = id,
            name = name,
            api = api
        )
    }

    fun accountDAOFaker(
        id: String = faker.random.randomString(),
        name: String = faker.random.randomString(),
        api: String = faker.random.nextUUID()
    ): me.xhsun.gw2leo.account.datastore.entity.Account {
        return me.xhsun.gw2leo.account.datastore.entity.Account(
            id = id,
            name = name,
            api = api
        )
    }

    fun accountDTOFaker(
        id: String = faker.random.randomString(),
        name: String = faker.random.randomString()
    ): AccountDTO {
        return AccountDTO(
            id = id,
            name = name
        )
    }

    fun characterDAOFaker(
        name: String = faker.random.randomString(),
        accountID: String = faker.random.randomString()
    ): Character {
        return Character(
            name = name,
            accountID = accountID
        )
    }

    fun storageItemFaker(
        sellable: Boolean = true,
        itemID: Int = faker.random.nextInt(5000),
        bindTo: String = faker.random.randomString(),
        binding: String = faker.random.randomString(),
        storageType: String = faker.random.randomString(),
        description: String = faker.random.randomString(),
        isBuy: Boolean = true
    ): StorageItem {
        val i = itemFaker(sellable = sellable, id = itemID, description = description)
        return StorageItem(
            id = faker.random.nextInt(),
            detail = i,
            storageType = storageType,
            count = faker.random.nextInt(1, 10),
            charges = faker.random.nextInt(),
            binding = binding,
            bindTo = bindTo,
            accountID = faker.random.randomString(),
            category = MaterialCategory(
                id = faker.random.nextInt(),
                name = faker.random.randomString()
            ),
            price = if (isBuy) i.buy else i.sell,
            gold = if (isBuy) i.buyGold else i.sellGold,
            silver = if (isBuy) i.buySilver else i.sellSilver,
            copper = if (isBuy) i.buyCopper else i.sellCopper
        )
    }

    fun itemDAOFaker(
        id: Int = faker.random.nextInt(),
        chatLink: String = faker.random.randomString(),
        name: String = faker.random.randomString(),
        icon: String = faker.internet.domain(),
        description: String = faker.random.randomString(),
        rarity: String = faker.random.randomString(),
        level: Int = faker.random.nextInt(0, 80),
        sellable: Boolean = true,
        buy: Int = faker.random.nextInt(),
        buyGold: Int = faker.random.nextInt(),
        buySilver: Int = faker.random.nextInt(),
        buyCopper: Int = faker.random.nextInt(),
        sell: Int = faker.random.nextInt(),
        sellGold: Int = faker.random.nextInt(),
        sellSilver: Int = faker.random.nextInt(),
        sellCopper: Int = faker.random.nextInt(),
        type: String = faker.random.randomString()
    ): me.xhsun.gw2leo.storage.datastore.entity.Item {
        return me.xhsun.gw2leo.storage.datastore.entity.Item(
            id = id,
            chatLink = chatLink,
            name = name,
            icon = icon,
            description = description,
            rarity = rarity,
            level = level,
            sellable = sellable,
            buy = buy,
            buyGold = buyGold,
            buySilver = buySilver,
            buyCopper = buyCopper,
            sell = sell,
            sellGold = sellGold,
            sellSilver = sellSilver,
            sellCopper = sellCopper,
            type = type
        )
    }

    fun itemFaker(
        id: Int = faker.random.nextInt(),
        chatLink: String = faker.random.randomString(),
        name: String = faker.random.randomString(),
        icon: String = faker.internet.domain(),
        description: String = faker.random.randomString(),
        rarity: String = faker.random.randomString(),
        level: Int = faker.random.nextInt(0, 80),
        sellable: Boolean = true,
        buy: Int = faker.random.nextInt(1, 1000),
        buyGold: Int = faker.random.nextInt(),
        buySilver: Int = faker.random.nextInt(),
        buyCopper: Int = faker.random.nextInt(),
        sell: Int = faker.random.nextInt(1, 1000),
        sellGold: Int = faker.random.nextInt(),
        sellSilver: Int = faker.random.nextInt(),
        sellCopper: Int = faker.random.nextInt(),
        type: String = faker.random.randomString()
    ): Item {
        return Item(
            id = id,
            chatLink = chatLink,
            name = name,
            icon = icon,
            description = description,
            rarity = rarity,
            level = level,
            sellable = sellable,
            buy = buy,
            buyGold = buyGold,
            buySilver = buySilver,
            buyCopper = buyCopper,
            sell = sell,
            sellGold = sellGold,
            sellSilver = sellSilver,
            sellCopper = sellCopper,
            type = type
        )
    }

    fun lastModifiedFaker(
        type: String = faker.random.randomString(),
        timeOffset: Long = 1
    ): LastModified {
        return LastModified(
            type = type,
            lastModified = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(timeOffset)
        )
    }

    fun storageDAOFaker(
        sellable: Boolean = true,
        binding: String = faker.random.randomString()
    ): Storage {
        return Storage(
            faker.random.nextInt(),
            faker.random.nextInt(),
            faker.random.randomString(),
            faker.random.nextInt(),
            faker.random.nextInt(),
            binding,
            faker.random.randomString(),
            faker.random.nextInt(),
            faker.random.randomString(),
            faker.random.randomString(),
            faker.random.randomString(),
            faker.random.randomString(),
            faker.random.randomString(),
            faker.random.nextInt(),
            sellable,
            faker.random.randomString(),
            faker.random.nextInt(),
            faker.random.nextInt(),
            faker.random.nextInt(),
            faker.random.nextInt(),
            faker.random.nextInt(),
            faker.random.nextInt(),
            faker.random.nextInt(),
            faker.random.nextInt()
        )
    }

    fun materialStorageDAOFaker(
        id: Int = faker.random.nextInt(),
        itemID: Int = faker.random.nextInt(),
        accountID: String = faker.random.randomString(),
        categoryID: Int = faker.random.nextInt(),
        binding: String = faker.random.randomString(),
        count: Int = faker.random.nextInt(),
        categoryName: String = faker.random.randomString(),
        chatLink: String = faker.random.randomString(),
        name: String = faker.random.randomString(),
        icon: String = faker.random.randomString(),
        type: String = faker.random.randomString(),
        description: String = faker.random.randomString(),
        rarity: String = faker.random.randomString(),
        level: Int = faker.random.nextInt(),
        sellable: Boolean = true,
        buy: Int = faker.random.nextInt(),
        buyGold: Int = faker.random.nextInt(),
        buySilver: Int = faker.random.nextInt(),
        buyCopper: Int = faker.random.nextInt(),
        sell: Int = faker.random.nextInt(),
        sellGold: Int = faker.random.nextInt(),
        sellSilver: Int = faker.random.nextInt(),
        sellCopper: Int = faker.random.nextInt()
    ): MaterialStorage {
        return MaterialStorage(
            id,
            itemID,
            accountID,
            categoryID,
            binding,
            count,
            categoryID.toString(),
            categoryName,
            itemID,
            chatLink,
            name,
            icon,
            type,
            description,
            rarity,
            level,
            sellable,
            buy,
            buyGold,
            buySilver,
            buyCopper,
            sell,
            sellGold,
            sellSilver,
            sellCopper
        )
    }

    fun inventoryDTOFaker(
        id: Int = faker.random.nextInt(5000),
        count: Int = faker.random.nextInt(1, 100)
    ): InventoryDTO {
        return InventoryDTO(
            id,
            count,
            faker.random.nextInt(),
            faker.random.randomString(),
            faker.random.randomString()
        )
    }

    fun bagDTOFaker(
        inventory: List<InventoryDTO?> = listOf(
            this.inventoryDTOFaker(),
            null
        )
    ): BagDTO {
        return BagDTO(
            faker.random.nextInt(),
            faker.random.nextInt(),
            inventory
        )
    }

    fun priceDTOFaker(id: Int = faker.random.nextInt(5000)): PriceDTO {
        return PriceDTO(
            id, faker.random.nextBoolean(),
            UnitPriceDTO(
                faker.random.nextInt(1, 5000),
                faker.random.nextInt()
            ),
            UnitPriceDTO(
                faker.random.nextInt(1, 5000),
                faker.random.nextInt()
            ),
        )
    }

    fun itemDTOFaker(id: Int = faker.random.nextInt(5000)): ItemDTO {
        return ItemDTO(
            id,
            faker.random.randomString(),
            faker.random.randomString(),
            faker.random.randomString(),
            faker.random.randomString(),
            faker.random.randomString(),
            faker.random.randomString(),
            faker.random.nextInt(),
            emptyList(),
            null
        )
    }

    fun materialTypeDTOFaker(id: Int = faker.random.nextInt(5000)): MaterialTypeDTO {
        return MaterialTypeDTO(
            id,
            faker.random.randomString(),
            faker.random.nextInt()
        )
    }

    fun materialDTOFaker(
        id: Int = faker.random.nextInt(5000),
        categoryID: Int = faker.random.nextInt(5000),
        count: Int = faker.random.nextInt(1, 100)
    ): MaterialDTO {
        return MaterialDTO(
            id,
            categoryID,
            faker.random.randomString(),
            count
        )
    }

    fun materialCategoryFaker(): MaterialCategory {
        return MaterialCategory(
            faker.random.nextInt(5000),
            faker.random.randomString()
        )
    }
}