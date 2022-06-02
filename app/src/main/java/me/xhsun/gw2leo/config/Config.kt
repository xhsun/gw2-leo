package me.xhsun.gw2leo.config

const val BASE_URL = "https://api.guildwars2.com/v2/"
const val TIMEOUT = 30   //In seconds
const val AUTH_HEADER = "Authorization"
const val AUTH_BODY_FORMAT = "Bearer %s"
const val CONTENT_TYPE = "Content-Type"
const val CONTENT_VALUE = "application/json"
const val MAX_RESPONSE_SIZE = 190
const val DEFAULT_RESPONSE_SIZE = 60
const val TOTAL_PAGE_HEADER = "X-Page-Total"
const val ID_SEPARATOR = ","
const val STARTING_PAGE_INDEX = 0

const val DB_NAME = "gw2_leo_database"
const val DB_BANK_KEY_FORMAT = "_BANK%s"
const val MATERIAL_STORAGE_KEY = "_MATERIAL"

const val SHARED_PREFERENCES_FILE_NAME = "account"
const val ACCOUNT_ID_KEY = "accountID"

const val ORDER_BY_BUY = "buy"
const val ORDER_BY_SELL = "sell"

const val COIN_GOLD =
    "https://render.guildwars2.com/file/090A980A96D39FD36FBB004903644C6DBEFB1FFB/156904.png"
const val COIN_SILVER =
    "https://render.guildwars2.com/file/E5A2197D78ECE4AE0349C8B3710D033D22DB0DA6/156907.png"
const val COIN_COPPER =
    "https://render.guildwars2.com/file/6CF8F96A3299CFC75D5CC90617C3C70331A1EF0E/156902.png"