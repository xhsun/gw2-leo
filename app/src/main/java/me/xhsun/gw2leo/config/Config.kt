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