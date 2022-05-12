package com.gavinsappcreations.upcominggames.utilities

import com.gavinsappcreations.upcominggames.domain.Platform

// We use the Giant Bomb API to get the game data: https://www.giantbomb.com/api/
// TODO: Add API key here
const val GIANT_BOMB_API_KEY = ""

// The size of pages that come in from the database and get added to our PagedList.
const val DATABASE_PAGE_SIZE = 20

// The max number of results per request in a call to the Giant Bomb API.
const val NETWORK_PAGE_SIZE = 100

// This is the amount of time in ms that it takes to animate each chunk of the loading bar.
const val LOADING_PROGRESS_ANIMATION_TIME: Long = 350

// The built-in database was retrieved at this time.
const val ORIGINAL_TIME_DATABASE_RETRIEVED_IN_MILLIS = 1_652_377_726_007

// If the API has no image for a game, this string will be somewhere in the URL.
const val NO_IMG_FILE_NAME = "gb_default-16_9"

// This is the name used to refer to the SharedPreferences file we use throughout the app.
const val SHARED_PREFERENCES_NAME = "prefs"

// These are SharedPreferences keys used for storing filter options
const val KEY_RELEASE_DATE_TYPE = "release_date_type"
const val KEY_CUSTOM_DATE_START = "custom_date_start"
const val KEY_CUSTOM_DATE_END = "custom_date_end"
const val KEY_SORT_DIRECTION = "sort_direction"
const val KEY_PLATFORM_TYPE = "platform_type"
const val KEY_PLATFORM_INDICES = "platform_indices"
const val KEY_TIME_LAST_UPDATED_IN_MILLIS = "last_updated"
const val KEY_RECENT_SEARCHES = "recent_searches"

// This is a SavedStateHandle key used to persist the platform checkbox selections across process death.
const val KEY_SAVED_STATE_PLATFORM_INDICES = "platform_indices"

// These are the fields we use to form our API queries.
enum class ApiField(val field: String) {
    Json("json"),
    Id("id"),
    Guid("guid"),
    Name("name"),
    Image("image"),
    Images("images"),
    Platforms("platforms"),
    OriginalReleaseDate("original_release_date"),
    ExpectedReleaseDay("expected_release_day"),
    ExpectedReleaseMonth("expected_release_month"),
    ExpectedReleaseYear("expected_release_year"),
    ExpectedReleaseQuarter("expected_release_quarter"),
    OriginalGameRating("original_game_rating"),
    Developers("developers"),
    Publishers("publishers"),
    Genres("genres"),
    Deck("deck"),
    DetailUrl("site_detail_url"),
    DateLastUpdated("date_last_updated")
}


/**
 * Every game in the "Game" table has a dateFormat integer. This specifies the level of precision
 * to which the game's release date is known. 
 */
enum class DateFormat(val formatCode: Int) {
    Exact(0),
    Month(1),
    Quarter(2),
    Year(3),
    None(4)
}


/**
 * This is the range of indices in the allKnownPlatforms list that we consider to be in the
 * current generation of platforms.
 */
val currentGenerationPlatformRange = IntRange(0, 14)

/**
 * This is a list of all known platforms as of March 2020, sorted by relevance. As new platforms
 * come out, we should add them to this list wherever we think they belong. If a game is added to the
 * database that isn't found in this list, the app will automatically assign that platform the
 * highest relevance.
 */
val allKnownPlatforms = listOf(
    Platform("PC", "PC"),
    Platform("PS5", "PlayStation 5"),
    Platform("XSX", "Xbox Series X"),
    Platform("XONE", "Xbox One"),
    Platform("PS4", "PlayStation 4"),
    Platform("NSW", "Nintendo Switch"),
    Platform("IPHN", "iPhone"),
    Platform("ANDR", "Android"),
    Platform("3DS", "Nintendo 3DS"),
    Platform("APTV", "Apple TV"),
    Platform("3DSE", "3DS eShop"),
    Platform("N3DS", "New 3DS"),
    Platform("MAC", "Mac"),
    Platform("IPAD", "iPad"),
    Platform("STAD", "Stadia"),
    Platform("ARC", "Arcade"),
    Platform("BROW", "Browser"),
    Platform("X360", "Xbox 360"),
    Platform("WiiU", "Wii U"),
    Platform("PS3", "PlayStation 3"),
    Platform("Wii", "Wii"),
    Platform("XBGS", "360 Games Store"),
    Platform("PS3N", "PS3 Network"),
    Platform("VITA", "PS Vita"),
    Platform("PSNV", "PS Vita Network"),
    Platform("WSHP", "Wii Shop"),
    Platform("PSP", "PS Portable"),
    Platform("PSPN", "PSP Network"),
    Platform("DS", "Nintendo DS"),
    Platform("DSI", "DSiWare"),
    Platform("PS2", "PlayStation 2"),
    Platform("GCN", "GameCube"),
    Platform("XBOX", "Xbox"),
    Platform("DC", "Dreamcast"),
    Platform("PS1", "PlayStation"),
    Platform("SAT", "Saturn"),
    Platform("N64", "Nintendo 64"),
    Platform("SNES", "SNES"),
    Platform("GEN", "Genesis"),
    Platform("GBA", "Game Boy Advance"),
    Platform("GBC", "Game Boy Color"),
    Platform("GG", "Game Gear"),
    Platform("NES", "NES"),
    Platform("GB", "Game Boy"),
    Platform("SMS", "Master System"),
    Platform("2600", "Atari 2600"),
    Platform("NEO", "Neo Geo"),
    Platform("3DO", "3DO"),
    Platform("CDI", "CD-i"),
    Platform("JAG", "Jaguar"),
    Platform("SCD", "Sega CD"),
    Platform("32X", "Sega 32X"),
    Platform("NGE", "N-Gage"),
    Platform("C64", "Commodore 64"),
    Platform("MSX", "MSX"),
    Platform("SPEC", "ZX Spectrum"),
    Platform("CVIS", "ColecoVision"),
    Platform("INTV", "Intellivision"),
    Platform("TGCD", "TurboGrafx-CD"),
    Platform("TG16", "TurboGrafx-16"),
    Platform("VBOY", "Virtual Boy"),
    Platform("NGP", "Neo Geo Pocket"),
    Platform("NGPC", "NG Pocket Color"),
    Platform("LIN", "Linux"),
    Platform("JCD", "Jaguar CD"),
    Platform("OUYA", "Ouya"),
    Platform("FIRE", "Amazon Fire TV"),
    Platform("WP", "Windows Phone"),
    Platform("AMI", "Amiga"),
    Platform("LYNX", "Atari Lynx"),
    Platform("CPC", "Amstrad CPC"),
    Platform("APL2", "Apple II"),
    Platform("AST", "Atari ST"),
    Platform("A800", "Atari 8-bit"),
    Platform("VC20", "VIC-20"),
    Platform("A2GS", "Apple IIgs"),
    Platform("CD32", "Amiga CD32"),
    Platform("TI99", "TI-99/4A"),
    Platform("WSC", "WonderSwan Color"),
    Platform("C128", "Commodore 128"),
    Platform("NGCD", "Neo Geo CD"),
    Platform("ODY2", "Odyssey 2"),
    Platform("DRAG", "Dragon 32/64"),
    Platform("CBM", "Commodore PET"),
    Platform("TRS8", "TRS-80"),
    Platform("ZOD", "Zodiac"),
    Platform("WSW", "WonderSwan"),
    Platform("CHNF", "Channel F"),
    Platform("5200", "Atari 5200"),
    Platform("COCO", "TRS-80 CoCo"),
    Platform("7800", "Atari 7800"),
    Platform("IPOD", "iPod"),
    Platform("ODYS", "Odyssey"),
    Platform("PCFX", "PC-FX"),
    Platform("VECT", "Vectrex"),
    Platform("GCOM", "Game.Com"),
    Platform("GIZ", "Gizmondo"),
    Platform("VSML", "V.Smile"),
    Platform("PIN", "Pinball"),
    Platform("NUON", "NUON"),
    Platform("LEAP", "Leapster"),
    Platform("MVIS", "Microvision"),
    Platform("FDS", "Famicom Disk"),
    Platform("LACT", "LaserActive"),
    Platform("AVIS", "Adventure Vision"),
    Platform("X68K", "Sharp X68000"),
    Platform("BS-X", "Satellaview"),
    Platform("A2K1", "Arcadia 2001"),
    Platform("AQUA", "Aquarius"),
    Platform("64DD", "Nintendo 64DD"),
    Platform("PIPN", "Pippin"),
    Platform("RZON", "R-Zone"),
    Platform("HSCN", "HyperScan"),
    Platform("GWAV", "Game Wave"),
    Platform("HALC", "RDI Halcyon"),
    Platform("FMT", "FM Towns"),
    Platform("PC88", "NEC PC-8801"),
    Platform("BBCM", "BBC Micro"),
    Platform("PLTO", "PLATO"),
    Platform("PC98", "NEC PC-9801"),
    Platform("X1", "Sharp X1"),
    Platform("FM7", "FM-7"),
    Platform("6001", "NEC PC-6001"),
    Platform("PICO", "Sega Pico"),
    Platform("SGFX", "SuperGrafx"),
    Platform("BAST", "Bally Astrocade"),
    Platform("ZBO", "Zeebo"),
    Platform("ACRN", "Acorn Archimedes"),
    Platform("LOOP", "Casio Loopy"),
    Platform("PDIA", "Bandai Playdia"),
    Platform("MZ", "Sharp MZ"),
    Platform("RCA2", "RCA Studio II"),
    Platform("XAVX", "XaviXPORT"),
    Platform("GP32", "GamePark 32"),
    Platform("PMIN", "Pokemon mini"),
    Platform("CASV", "Epoch"),
    Platform("SCV", "SCV"),
    Platform("DUCK", "Mega Duck"),
    Platform("SG1K", "Sega SG-1000"),
    Platform("CDTV", "Commodore CDTV"),
    Platform("DIDJ", "Leapfrog Didj"),
    Platform("SVIS", "Supervision"),
    Platform("AMAX", "Action Max"),
    Platform("PV1K", "Casio PV-1000"),
    Platform("C16", "Commodore 16"),
    Platform("ACAN", "Super A'Can"),
    Platform("VIS", "Memorex MD 2500"),
    Platform("HGM", "Game Master"),
    Platform("SMC7", "Sony SMC-777"),
    Platform("COUP", "SAM Coupe"),
    Platform("VMIV", "View-Master"),
    Platform("TF1", "Fuze Tomahawk F1"),
    Platform("TUT", "Tomy Tutor"),
    Platform("GMT", "Gamate"),
    Platform("MBEE", "MicroBee"),
    Platform("VSOC", "VTech Socrates"),
    Platform("ABC", "Luxor ABC80"),
    Platform("ALXA", "Amazon Alexa"),
    Platform("ML1", "Magic Leap One"),
    Platform("BNA", "Beena"),
    Platform("OQST", "Oculus Quest"),
    Platform("PLDT", "Playdate"),
    Platform("EVER", "Evercade"),
    Platform("AMIC", "Amico"),
    Platform("NONE", "No platforms")
)