package me.xhsun.gw2leo.config

const val BASE_URL = "https://api.guildwars2.com/v2/"
const val TIMEOUT = 30   //In seconds
const val AUTH_HEADER = "Authorization"
const val AUTH_BODY_FORMAT = "Bearer %s"
const val CONTENT_TYPE = "Content-Type"
const val CONTENT_VALUE = "application/json"
const val MAX_RESPONSE_SIZE = 200
const val ID_SEPARATOR = ","
val NO_AUTH_INJECTION = arrayOf("account", "characters")

const val DB_NAME = "gw2_leo_database"

const val BANK_STORAGE_PREFIX = "_BANK"
const val BANK_STORAGE_KEY_FORMAT = "_BANK%s"
const val MATERIAL_STORAGE_PREFIX = "_MATERIAL"
const val MATERIAL_STORAGE_KEY_FORMAT = "_MATERIAL%s"
const val INVENTORY_STORAGE_PREFIX = "_INVENTORY"
const val MIN_REFRESH_RATE_HR = 1

const val SHARED_PREFERENCES_FILE_NAME = "account"
const val ACCOUNT_ID_KEY = "accountID"
const val CHARACTER_LIST_KEY = "characters"
const val STORAGE_ITEM_KEY = "storageItem"
const val ITEM_DIALOG_TAG = "storageItemDialog"

const val ORDER_BY_BUY = "buy"
const val ORDER_BY_SELL = "sell"
const val STORAGE_TYPE_KEY = "storageType"
const val STORAGE_DISPLAY_KEY = "STORAGE_DISPLAY"

const val STORAGE_LIST_COLUMN_WIDTH = 82
const val COLOR_Junk = -0x55554a
const val COLOR_Basic = -0x1000000
const val COLOR_Fine = -0x9b4a0a
const val COLOR_Masterwork = -0x9760c8
const val COLOR_Rare = -0x2ab1
const val COLOR_Exotic = -0x6000
const val COLOR_Ascended = -0xbf7f
const val COLOR_Legendary = -0xb5eb74

const val COIN_GOLD =
    "https://render.guildwars2.com/file/090A980A96D39FD36FBB004903644C6DBEFB1FFB/156904.png"
const val COIN_SILVER =
    "https://render.guildwars2.com/file/E5A2197D78ECE4AE0349C8B3710D033D22DB0DA6/156907.png"
const val COIN_COPPER =
    "https://render.guildwars2.com/file/6CF8F96A3299CFC75D5CC90617C3C70331A1EF0E/156902.png"