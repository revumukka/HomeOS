package com.ansub.tvhome;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Universal Multi-Universe Animated Sky & 300-Scene Saga Theater view (SkyView):
 * Supports 6 interactive franchises: Harry Potter, Naruto Shippuden, Demon Slayer, Star Wars, MCU, and DCU.
 * - Top Animated Sky: Twinkling stars, floating atmospheric elements, and reactive companion sprites.
 * - Bottom Saga Theater Stage: Chronological animated story scenes rendered at up to 120 FPS (dynamic LTPO/VRR
 *   sub-microsecond elapsedRealtimeNanos timing) with zero per-frame allocations.
 */
final class SkyView extends View {
    private static final int STAR_COUNT = 56;
    private static final int CANDLE_COUNT = 18;
    private static final int SPARK_COUNT = 36;
    private static final int GHOST_COUNT = 5;
    private static final int SCENE_COUNT = 80;
    private static final int UNIVERSE_SCENE_COUNT = 44;
    private static final float CHAPTER_SEC = 5.0f; // 80 HP scenes / 44 Universe scenes * 5.0s saga loop

    private static final String[] CHAPTER_TITLES = {
        "I  |  PRIVET DRIVE  |  DUMBLEDORE & THE DELUMINATOR",
        "II  |  SURREY SKIES  |  HAGRID'S FLYING MOTORBIKE",
        "III  |  THE REPTILE HOUSE  |  VANISHING GLASS & BOA CONSTRICTOR",
        "IV  |  NUMBER FOUR  |  THE SWARM OF HOGWARTS LETTERS",
        "V  |  DIAGON ALLEY  |  THE BRICK WALL ARCHWAY OPENING",
        "VI  |  GRINGOTTS BANK  |  THE PLUNGING SUBTERRANEAN CART",
        "VII  |  OLLIVANDERS  |  THE WAND CHOOSES THE WIZARD",
        "VIII  |  PLATFORM 9 3/4  |  THE HOGWARTS EXPRESS",
        "IX  |  THE BLACK LAKE  |  FIRST-YEAR LANTERN BOATS",
        "X  |  THE GREAT HALL  |  THE SORTING HAT CEREMONY",
        "XI  |  FLYING LESSON  |  NEVILLE'S REMEMBRALL DIVE",
        "XII  |  CHARMS CLASS  |  WINGARDIUM LEVIOSA & THE TROLL",
        "XIII  |  QUIDDITCH PITCH  |  THE GOLDEN SNITCH CHASE",
        "XIV  |  THE THIRD FLOOR  |  FLUFFY & THE ENCHANTED HARP",
        "XV  |  UNDERGROUND CHAMBERS  |  DEVIL'S SNARE & LUMOS SOLEM",
        "XVI  |  UNDERGROUND CHAMBERS  |  SWARM OF THE WINGED KEYS",
        "XVII  |  UNDERGROUND CHAMBERS  |  GIANT WIZARD'S CHESS",
        "XVIII  |  MIRROR OF ERISED  |  THE PHILOSOPHER'S STONE",
        "XIX  |  BACK TO HOGWARTS  |  THE FLYING FORD ANGLIA",
        "XX  |  HOGWARTS GROUNDS  |  THE WHOMPING WILLOW ATTACK",
        "XXI  |  GREENHOUSE THREE  |  CRYING BABY MANDRAKES",
        "XXII  |  DUELLING CLUB  |  SERPENSORTIA & PARSELTONGUE",
        "XXIII  |  FORBIDDEN FOREST  |  ARAGOG & ACROMANTULA SWARM",
        "XXIV  |  CHAMBER OF SECRETS  |  SWORD OF GRYFFINDOR & BASILISK",
        "XXV  |  CHAMBER OF SECRETS  |  TOM RIDDLE'S DIARY & FANG",
        "XXVI  |  DOBBY IS FREE  |  THE SOCK & LUCIUS MALFOY",
        "XXVII  |  MAGNOLIA CRESCENT  |  AUNT MARGE FLOATING AWAY",
        "XXVIII  |  MAGICAL TRANSPORT  |  THE TRIPLE-DECKER KNIGHT BUS",
        "XXIX  |  CARE OF MAGICAL CREATURES  |  FLIGHT OF BUCKBEAK",
        "XXX  |  DADA CLASS  |  RIDDIKULUS & THE BOGGART WARDROBE",
        "XXXI  |  SECRET PASSAGE  |  THE MARAUDER'S MAP & LUMOS",
        "XXXII  |  SHRIEKING SHACK  |  PADFOOT THE GRIM & MOONY",
        "XXXIII  |  PRISONER OF AZKABAN  |  EXPECTO PATRONUM STAG",
        "XXXIV  |  THE TIME-TURNER  |  RESCUE OF SIRIUS BLACK",
        "XXXV  |  QUIDDITCH WORLD CUP  |  THE DARK MARK IN THE SKY",
        "XXXVI  |  THE GREAT HALL  |  THE GOBLET OF FIRE BLUE FLAMES",
        "XXXVII  |  TRIWIZARD TOURNAMENT  |  THE HUNGARIAN HORNTAIL",
        "XXXVIII  |  THE YULE BALL  |  CHAMPIONS' WALTZ UNDER ICE",
        "XXXIX  |  THE SECOND TASK  |  MERPEOPLE OF THE BLACK LAKE",
        "XL  |  THE THIRD TASK  |  TRIWIZARD MAZE & GLOWING CUP",
        "XLI  |  LITTLE HANGLETON  |  GRAVEYARD PRIORI INCANTATEM",
        "XLII  |  LONDON SKIES  |  ORDER OF THE PHOENIX BROOM FLIGHT",
        "XLIII  |  FORBIDDEN FOREST  |  LUNA & THE WINGED THESTRALS",
        "XLIV  |  ROOM OF REQUIREMENT  |  DUMBLEDORE'S ARMY & FIREWORKS",
        "XLV  |  HALL OF PROPHECIES  |  THE GLOWING PROPHECY ORBS",
        "XLVI  |  MINISTRY ATRIUM  |  DUMBLEDORE'S WATER SPHERE",
        "XLVII  |  POTIONS CLASS  |  FELIX FELICIS LIQUID LUCK",
        "XLVIII  |  THE CRYSTAL CAVE  |  DUMBLEDORE'S FIRESTORM LASSO",
        "XLIX  |  ASTRONOMY TOWER  |  WANDS RAISED AGAINST THE DARK",
        "L  |  SURREY SKIES  |  BATTLE OF THE SEVEN POTTERS",
        "LI  |  FOREST OF DEAN  |  THE SILVER DOE & FROZEN POND",
        "LII  |  XENOPHILIUS'S HOUSE  |  TALE OF THE THREE BROTHERS",
        "LIII  |  GRINGOTTS BREAKOUT  |  UKRAINIAN IRONBELLY DRAGON",
        "LIV  |  ROOM OF REQUIREMENT  |  FIENDFYRE SERPENTS & BROOMS",
        "LV  |  BATTLE OF HOGWARTS  |  PROTEGO MAXIMA & STONE KNIGHTS",
        "LVI  |  THE DEATHLY HALLOWS  |  HARRY VS. VOLDEMORT FINAL DUEL",
        "LVII  |  GRINGOTTS CAVERN  |  RUNAWAY VAULT CART ROLLERCOASTER",
        "LVIII  |  HONEYDUKES PASSAGE  |  THE ONE-EYED WITCH STATUE",
        "LIX  |  HOGSMEADE BLIZZARD  |  THE SHRIEKING SHACK IN SNOW",
        "LX  |  TRIWIZARD ARRIVAL  |  BEAUXBATONS WINGED CARRIAGE",
        "LXI  |  BLACK LAKE WHIRLPOOL  |  THE DURMSTRANG SPECTRAL SHIP",
        "LXII  |  TWELVE GRIMMAULD PLACE  |  HEADQUARTERS OF THE ORDER",
        "LXIII  |  MINISTRY ATRIUM  |  DUMBLEDORE VS. VOLDEMORT CLASH",
        "LXIV  |  GREAT HALL ESCAPE  |  WEASLEYS' FIREWORK DRAGON",
        "LXV  |  THE BURROW MARSHES  |  RING OF FIRE DEATH EATER AMBUSH",
        "LXVI  |  MALFOY MANOR  |  DOBBY & THE CRYSTAL CHANDELIER",
        "LXVII  |  THE PRINCE'S TALE  |  SNAPE'S PENSIEVE & SILVER DOE",
        "LXVIII  |  NINETEEN YEARS LATER  |  PLATFORM 9 3/4 EPILOGUE",
        "LXIX  |  PRIVET DRIVE BEDROOM  |  UNDER-COVERS LUMOS MAXIMA",
        "LXX  |  HOGWARTS EXPRESS  |  DEMENTOR BOARDING & FROSTED GLASS",
        "LXXI  |  HOGSMEADE VILLAGE  |  CROOKSHANKS & SCABBERS CHASE",
        "LXXII  |  GREAT HALL  |  PARCHMENT NAMES IN GOBLET OF FIRE",
        "LXXIII  |  CHAMPIONS' TENT  |  RITA SKEETER & QUICK-QUOTES QUILL",
        "LXXIV  |  DIAGON ALLEY  |  WEASLEYS' WIZARD WHEEZES EMPORIUM",
        "LXXV  |  SIXTH-FLOOR BATHROOM  |  HARRY VS. DRACO SECTUMSEMPRA",
        "LXXVI  |  GRIMMAULD PLACE  |  KREACHER'S TALE & REGULUS LOCKET",
        "LXXVII  |  GODRIC'S HOLLOW  |  CHRISTMAS EVE GRAVEYARD IN SNOW",
        "LXXVIII  |  SHELL COTTAGE  |  BILL & FLEUR BY THE CRASHING SEA",
        "LXXIX  |  HOGWARTS COURTYARD  |  NEVILLE & THE SWORD OF GRYFFINDOR",
        "LXXX  |  VIADUCT BRIDGE  |  REPARO OF THE HOLLY WAND & ELDER WAND"
    };

    private float scale;
    private Bitmap baseSky;
    private Bitmap glowSprite;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final Path flamePath = new Path();
    private final Path actorPath = new Path();
    private static final RectF storyRect = new RectF();
    private Typeface cinzelFont;
    private Typeface f1Font;

    private static RectF rectF(float l, float t, float r, float b) {
        storyRect.set(l, t, r, b);
        return storyRect;
    }

    // Stars: x, y, radius, speed, phase, isMajor(0/1)
    private final float[] stars = new float[STAR_COUNT * 6];
    // Floating candles: x, baseY, width, height, bobSpeed, bobPhase, flickerPhase
    private final float[] candles = new float[CANDLE_COUNT * 7];
    // Drifting golden sparks: x, y, vx, vy, size, phase
    private final float[] sparks = new float[SPARK_COUNT * 6];
    // Flying Hogwarts Ghosts in top sky: baseX, baseY, speedX, bobSpeed, phase, scale, type(0..4)
    private final float[] ghosts = new float[GHOST_COUNT * 7];

    // Interactive Marauder's Map Footsteps: x, y, cosA, sinA, spawnTime, side(-1/1)
    private static final int FOOTSTEP_MAX = 12;
    private final float[] footsteps = new float[FOOTSTEP_MAX * 6];
    private int footstepHead = 0;
    private float lastFocusX = -1f, lastFocusY = -1f;

    // Interactive Lumos Wand-Cast Spark Burst on focus change: x, y, vx, vy, spawnTime, rgb
    private static final int BURST_COUNT = 14;
    private final float[] burstSparks = new float[BURST_COUNT * 6];

    // Interactive Golden Snitch Companion tracking the currently focused tile
    private float snitchCurX = 240f, snitchCurY = 260f;
    private float snitchTargetX = 240f, snitchTargetY = 260f;

    // Manual scene-skip offset (triggered from Settings -> Next Story Scene)
    private int sceneOffset = 0;
    private boolean sceneLocked = false;
    private int lockedSceneIndex = 0;
    private int lastRenderedSceneIndex = 0;
    private float storySpeedMultiplier = 1.0f;
    // v11.0.0 Living Weather & Sky Mood Engine:
    // 0 = Auto (Follows Live Weather), 1 = Clear Starfield, 2 = Rainstorm, 3 = Winter Snowfall, 4 = Thunderstorm, 5 = Aurora Borealis
    private int weatherMood = 0;
    private int liveWeatherMood = 1;
    // Active Theme Universe: 0 = Harry Potter, 1 = Naruto, 2 = Demon Slayer, 3 = Star Wars, 4 = MCU, 5 = DCU, 6 = Formula 1, 7 = Cyberpunk 2099
    private int universeTheme = 0;

    private static final String[] NARUTO_TITLES = {
        "I  |  HIDDEN LEAF VILLAGE  |  NARUTO ON THE HOKAGE ROCK",
        "II  |  TEAM SEVEN  |  BELL TEST WITH KAKASHI HATAKE",
        "III  |  LAND OF WAVES  |  ZABUZA & HAKU DEMONIC ICE MIRRORS",
        "IV  |  CHUNIN EXAMS  |  ROCK LEE EIGHT GATES VS. GAARA",
        "V  |  MOUNT MYOBOKU  |  NARUTO SUMMONS CHIEF TOAD GAMABUNTA",
        "VI  |  UCHIHA CLAN  |  ITACHI & THE CRIMSON SHARINGAN MOON",
        "VII  |  VALLEY OF THE END  |  RASENGAN VS. CHIDORI CLASH",
        "VIII  |  AKATSUKI PURSUIT  |  DEIDARA'S EXPLOSIVE CLAY DRAGON",
        "IX  |  ASSAULT ON KONOHA  |  SAGE MODE NARUTO VS. PAIN",
        "X  |  FOURTH SHINOBI WAR  |  UCHIHA MADARA & PERFECT SUSANOO",
        "XI  |  KURAMA LINK MODE  |  NINE-TAILED FOX & TAILED BEAST BOMB",
        "XII  |  FINAL VALLEY  |  SIX PATHS NARUTO VS. RINNEGAN SASUKE",
        "XIII  |  FOREST OF DEATH  |  OROCHIMARU & GIANT SERPENT MANDA",
        "XIV  |  CHUNIN PRELIMS  |  NEJI HYUGA EIGHT TRIGRAMS 64 PALMS",
        "XV  |  KONOHA CRUSH  |  THIRD HOKAGE & REAPER DEATH SEAL",
        "XVI  |  TSUNADE SEARCH  |  NARUTO'S FIRST RASENGAN IMPACT",
        "XVII  |  LEGENDARY SANNIN  |  TOAD, SNAKE & SLUG DEADLOCK",
        "XVIII  |  SASUKE RECOVERY  |  SHIKAMARU SHADOW POSSESSION",
        "XIX  |  BONE FOREST  |  GAARA'S GIANT SAND TSUNAMI",
        "XX  |  KAZEKAGE RESCUE  |  SAKURA VS. SASORI'S 100 PUPPETS",
        "XXI  |  TENCHI BRIDGE  |  FOUR-TAILED KYUUBI CLOAK NARUTO",
        "XXII  |  IMMORTALS ARC  |  WIND STYLE: RASENSHURIKEN AWAKENS",
        "XXIII  |  AVENGE ASUMA  |  SHIKAMARU'S CHAKRA BLADE TRAP",
        "XXIV  |  HIDDEN RAIN  |  TALE OF JIRAIYA THE GALLANT",
        "XXV  |  UCHIHA FORTRESS  |  SASUKE'S KIRIN LIGHTNING DRAGON",
        "XXVI  |  CLOUD GORGE  |  KILLER BEE & EIGHT-TAILS GYUKI",
        "XXVII  |  FIVE KAGE SUMMIT  |  SASUKE SUSANOO VS. RAIKAGE",
        "XXVIII  |  NINE-TAILS NIGHT  |  MINATO FLYING RAIJIN LEVEL TWO",
        "XXIX  |  ADAMANTINE CHAINS  |  MINATO & KUSHINA SEAL KURAMA",
        "XXX  |  SHINOBI ALLIANCE  |  UNITED SHINOBI FORCES MARCH",
        "XXXI  |  DESERT BATTLEFIELD  |  MADARA'S TWIN METEORS",
        "XXXII  |  UCHIHA BROTHERS  |  ITACHI & SASUKE DUAL SUSANOO",
        "XXXIII  |  CATACLYSM  |  REVIVAL OF THE TEN-TAILS JUBI",
        "XXXIV  |  EIGHTH GATE OF DEATH  |  MIGHT GUY: NIGHT GUY DRAGON",
        "XXXV  |  KAGUYA DIMENSION  |  TEAM SEVEN SIX PATHS SEALING",
        "XXXVI  |  NEW ERA KONOHA  |  SEVENTH HOKAGE NARUTO UZUMAKI",
        "XXXVII  |  ACADEMY SWING  |  YOUNG NARUTO BENEATH HOKAGE ROCK",
        "XXXVIII  |  LAND OF WAVES BRIDGE  |  NINE-TAILS RED CHAKRA AWAKENING",
        "XXXIX  |  ROOFTOP CLASH  |  NARUTO RASENGAN VS. SASUKE CHIDORI",
        "XL  |  FINAL VALLEY WATERFALL  |  WINGED CURSE MARK SASUKE VS. NARUTO",
        "XLI  |  MOUNT MYOBOKU  |  SAGE MODE JIRAIYA & TWO GREAT SAGE TOADS",
        "XLII  |  DESERT BATTLEFIELD  |  GAARA GRAND SAND MAUSOLEUM SEAL",
        "XLIII  |  FOUNDERS ERA  |  FIRST HOKAGE HASHIRAMA THOUSAND-ARMED BUDDHA",
        "XLIV  |  BARYON MODE  |  NARUTO & KURAMA FINAL CRIMSON FUSION"
    };

    private static final String[] DEMON_SLAYER_TITLES = {
        "I  |  MOUNT KUMOTORI  |  TANJIRO & NEZUKO IN THE SNOW",
        "II  |  MOUNT SAGIRI  |  TANJIRO SLICING THE GIANT BOULDER",
        "III  |  FINAL SELECTION  |  MOUNT FUJIKASANE WISTERIA TREES",
        "IV  |  FIRST FORM  |  ZENITSU THUNDERCLAP AND FLASH",
        "V  |  MOUNT NATAGUMO  |  GIYU TOMIOKA WATER BREATHING",
        "VI  |  HINOKAMI KAGURA  |  DANCE OF THE FIRE GOD & NEZUKO",
        "VII  |  BUTTERFLY MANSION  |  SHINOBU KOCHO INSECT BREATHING",
        "VIII  |  MUGEN TRAIN  |  THE INFINITY STEAM LOCOMOTIVE",
        "IX  |  SET YOUR HEART ABLAZE  |  KYOJURO RENGOKU VS. AKAZA",
        "X  |  ENTERTAINMENT DISTRICT  |  TENGEN UZUI SOUND HASHIRA",
        "XI  |  SWORDSMITH VILLAGE  |  MUICHIRO MIST BREATHING",
        "XII  |  INFINITY CASTLE  |  TANJIRO SUN DRAGON VS. MUZAN",
        "XIII  |  MOUNT SAGIRI  |  SAKONJI UROKODAKI & TENGU MASK",
        "XIV  |  NORTHWEST TOWN  |  TANJIRO WATER WHEEL SECOND FORM",
        "XV  |  ASAKUSA STREETS  |  LADY TAMAYO'S BLOOD DEMON SPELL",
        "XVI  |  TEMARI & ARROWS  |  TANJIRO & NEZUKO VS. SUSAMARU",
        "XVII  |  TSUZUMI MANSION  |  INOSUKE HASHIBIRA BEAST BREATHING",
        "XVIII  |  SPIDER MOUNTAIN  |  ZENITSU SIXFOLD THUNDERCLAP",
        "XIX  |  MOUNT NATAGUMO  |  TANJIRO TENTH FORM CONSTANT FLUX",
        "XX  |  UBUYASHIKI ESTATE  |  THE NINE HASHIRA PILLARS ASSEMBLE",
        "XXI  |  BUTTERFLY GARDEN  |  KANAO TSUYURI FLOWER BREATHING",
        "XXII  |  MUGEN TRAIN ROOF  |  TANJIRO & INOSUKE VS. ENMU",
        "XXIII  |  FLAME BREATHING  |  RENGOKU FIFTH FORM FLAME TIGER",
        "XXIV  |  YOSHIWARA ROOFTOPS  |  ZENITSU GODSPEED THUNDERCLAP",
        "XXV  |  BURNING YOSHIWARA  |  AWAKENED NEZUKO VS. DAKI OBI",
        "XXVI  |  MUSICAL SCORE  |  TENGEN & TANJIRO BEHEAD GYUTARO",
        "XXVII  |  SWORDSMITH VILLAGE  |  HAGANEZUKA FORGING THE KATANA",
        "XXVIII  |  LOVE BREATHING  |  MITSURI KANROJI WHIP-KATANA",
        "XXIX  |  LIGHTNING DRAGON  |  MITSURI VS. ZOHAKUTEN WOOD DRAGONS",
        "XXX  |  MORNING SUNRISE  |  NEZUKO CONQUERS THE SUN",
        "XXXI  |  WATERFALL TRAINING  |  GYOMEI HIMEJIMA STONE HASHIRA",
        "XXXII  |  WIND & SERPENT  |  SANEMI & OBANAI SPARRING DUEL",
        "XXXIII  |  INFINITY CASTLE  |  ZENITSU SEVENTH FORM FLAMING THUNDER GOD",
        "XXXIV  |  UPPER MOON TWO  |  SHINOBU, KANAO & INOSUKE VS. DOMA",
        "XXXV  |  UPPER MOON ONE  |  GYOMEI, SANEMI & MUICHIRO VS. KOKUSHIBO",
        "XXXVI  |  DAWN OVER TOKYO  |  THIRTEENTH FORM SUN BREATHING FINALE",
        "XXXVII  |  DRUM MANSION  |  TANJIRO VS. KYOGAI ROTATING CLAW SLASH",
        "XXXVIII  |  MOUNT NATAGUMO  |  SHINOBU KOCHO WISTERIA MOONLIT DESCENT",
        "XXXIX  |  BUTTERFLY MANSION  |  TOTAL CONCENTRATION GOURD BURST",
        "XL  |  ENTERTAINMENT DISTRICT  |  INOSUKE BEAST BREATHING FANG SLASH",
        "XLI  |  SWORDSMITH VILLAGE  |  GENYA SHINAZUGAWA DOUBLE-BARREL SHOT",
        "XLII  |  INFINITY CASTLE  |  GIYU & TANJIRO VS. AKAZA COMPASS NEEDLE",
        "XLIII  |  INFINITY CASTLE  |  OBANAI IGURO SERPENT BREATHING",
        "XLIV  |  SPRINGTIME EPILOGUE  |  KAMADO DESCENDANTS BENEATH SAKURA"
    };

    private static final String[] STAR_WARS_TITLES = {
        "I  |  TATOOINE HORIZON  |  LUKE SKYWALKER & BINARY SUNSET",
        "II  |  THEED GENERATOR  |  DUEL OF THE FATES VS. DARTH MAUL",
        "III  |  CLONE WARS  |  GENERAL GRIEVOUS FOUR LIGHTSABERS",
        "IV  |  MUSTAFAR LAVA RIVER  |  OBI-WAN VS. ANAKIN DUEL",
        "V  |  KESSEL RUN  |  THE MILLENNIUM FALCON IN HYPERSPACE",
        "VI  |  YAVIN TRENCH RUN  |  X-WING VS. TIE FIGHTER DOGFIGHT",
        "VII  |  BATTLE OF HOTH  |  IMPERIAL AT-AT WALKERS",
        "VIII  |  DAGOBAH SWAMP  |  MASTER YODA & THE FORCE",
        "IX  |  CLOUD CITY BESPIN  |  I AM YOUR FATHER DUEL",
        "X  |  DEATH STAR II  |  LUKE VS. VADER & EMPEROR LIGHTNING",
        "XI  |  THE MANDALORIAN  |  DIN DJARIN DARKSABER & GROGU",
        "XII  |  ROGUE ONE CORRIDOR  |  DARTH VADER RED SABER IGNITION",
        "XIII  |  BOONTA EVE CLASSIC  |  ANAKIN SKYWALKER PODRACER",
        "XIV  |  NABOO HANGAR  |  QUI-GON & OBI-WAN VS. DARTH MAUL",
        "XV  |  KAMINO RAINSTORM  |  OBI-WAN VS. JANGO FETT & SLAVE I",
        "XVI  |  GEONOSIS ARENA  |  MACE WINDU PURPLE LIGHTSABER",
        "XVII  |  GEONOSIS HANGAR  |  MASTER YODA VS. COUNT DOOKU",
        "XVIII  |  BATTLE OVER CORUSCANT  |  ANAKIN & OBI-WAN JEDI INTERCEPTORS",
        "XIX  |  INVISIBLE HAND  |  ANAKIN & OBI-WAN VS. COUNT DOOKU",
        "XX  |  UTAPAU SINKHOLE  |  OBI-WAN KENOBI VS. GENERAL GRIEVOUS",
        "XXI  |  CHANCELLOR'S OFFICE  |  MACE WINDU VS. DARTH SIDIOUS LIGHTNING",
        "XXII  |  GALACTIC SENATE  |  YODA VS. EMPEROR PALPATINE",
        "XXIII  |  FORTRESS INQUISITORIUS  |  OBI-WAN KENOBI VS. DARTH VADER",
        "XXIV  |  SCARIF CITADEL  |  DEATH STAR SUPERLASER IGNITION",
        "XXV  |  DEATH STAR HANGAR  |  BEN KENOBI VS. DARTH VADER",
        "XXVI  |  HOTH ASTEROID FIELD  |  MILLENNIUM FALCON VS. TIE SQUADRON",
        "XXVII  |  GREAT PIT OF CARKOON  |  LUKE SKYWALKER ON JABBA'S SAIL BARGE",
        "XXVIII  |  FOREST MOON OF ENDOR  |  SPEEDER BIKE CHASE THROUGH REDWOODS",
        "XXIX  |  EMPEROR'S THRONE ROOM  |  DARTH VADER REDEMPTION & FORCE LIGHTNING",
        "XXX  |  SIEGE OF MANDALORE  |  AHSOKA TANO WHITE LIGHTSABERS VS. MAUL",
        "XXXI  |  STARKILLER FOREST  |  REY & FINN VS. KYLO REN CROSSGUARD SABER",
        "XXXII  |  SUPREMACY THRONE ROOM  |  REY & KYLO REN BACK-TO-BACK DUEL",
        "XXXIII  |  BATTLE OF CRAIT  |  LUKE SKYWALKER FACES THE FIRST ORDER",
        "XXXIV  |  KEF BIR OCEAN WRECKAGE  |  REY VS. KYLO REN AMID CRASHING WAVES",
        "XXXV  |  IMPERIAL LIGHT CRUISER  |  LUKE SKYWALKER & GROGU RESCUE",
        "XXXVI  |  EXEGOL SITH CITADEL  |  REY ALL THE JEDI VS. PALPATINE",
        "XXXVII  |  JUNDLAND WASTES  |  OBI-WAN & LUKE SKYWALKER LANDSPEEDER",
        "XXXVIII  |  MOS EISLEY CANTINA  |  HAN SOLO & CHEWBACCA BLASTER STANDOFF",
        "XXXIX  |  CLOUD CITY CHAMBER  |  DARTH VADER & BOBA FETT CARBON FREEZE",
        "XL  |  DEATH STAR II CORE  |  MILLENNIUM FALCON MAIN REACTOR STRIKE",
        "XLI  |  ENDOR SANCTUARY MOON  |  FORCE GHOSTS OF ANAKIN, OBI-WAN & YODA",
        "XLII  |  NEVARRO STREETS  |  MANDALORIAN WHISTLING BIRDS & JETPACK",
        "XLIII  |  CORVUS FOREST  |  AHSOKA TANO DUAL WHITE LIGHTSABERS",
        "XLIV  |  ALDANI EYE  |  CASSIAN ANDOR & LUTHEN RAEL HAULCRAFT ESCAPE"
    };

    private static final String[] MCU_TITLES = {
        "I  |  STARK WORKSHOP  |  IRON MAN MARK LXXXV FLIGHT",
        "II  |  BIFROST BRIDGE  |  THOR GOD OF THUNDER & MJOLNIR",
        "III  |  FIRST AVENGER  |  CAPTAIN AMERICA VIBRANIUM SHIELD",
        "IV  |  BATTLE OF NEW YORK  |  THE AVENGERS ASSEMBLE",
        "V  |  SANCTUM SANCTORUM  |  DOCTOR STRANGE ELDRITCH MANDALAS",
        "VI  |  CIVIL WAR  |  CAPTAIN AMERICA VS. IRON MAN",
        "VII  |  QUEENS SKYLINE  |  SPIDER-MAN WEB-SWINGING",
        "VIII  |  WAKANDA FOREVER  |  BLACK PANTHER KINETIC PULSE",
        "IX  |  INFINITY WAR  |  THOR ARRIVES WITH STORMBREAKER",
        "X  |  TITAN SHOWDOWN  |  THANOS & THE SIX INFINITY STONES",
        "XI  |  AVENGERS ENDGAME  |  CAPTAIN AMERICA WIELDS MJOLNIR",
        "XII  |  PORTALS & SNAP  |  ON YOUR LEFT & I AM IRON MAN",
        "XIII  |  AFGHAN CAVE ESCAPE  |  IRON MAN MARK I FLAMETHROWERS",
        "XIV  |  MONACO GRAND PRIX  |  SUITCASE ARMOR VS. WHIPLASH",
        "XV  |  PUENTE ANTIGUO  |  THOR AWAKENS MJOLNIR VS. DESTROYER",
        "XVI  |  GERMAN FOREST  |  IRON MAN & CAP VS. THOR MJOLNIR CLASH",
        "XVII  |  MANHATTAN SKYLINE  |  HULK SMASHES CHITAURI LEVIATHAN",
        "XVIII  |  TRISKELION HELICARRIERS  |  CAPTAIN AMERICA VS. WINTER SOLDIER",
        "XIX  |  XANDAR SHOWDOWN  |  GUARDIANS OF THE GALAXY POWER STONE",
        "XX  |  JOHANNESBURG STREETS  |  IRON MAN HULKBUSTER VS. HULK",
        "XXI  |  SOKOVIA CHURCH  |  AVENGERS 360 STAND VS. ULTRON",
        "XXII  |  LEIPZIG AIRPORT  |  GIANT-MAN VS. IRON MAN & SPIDER-MAN",
        "XXIII  |  DARK DIMENSION  |  DOCTOR STRANGE TIME STONE LOOP",
        "XXIV  |  STATEN ISLAND FERRY  |  SPIDER-MAN & IRON MAN RESCUE",
        "XXV  |  SAKAAR ARENA  |  GLADIATOR THOR VS. INCREDIBLE HULK",
        "XXVI  |  RAINBOW BRIDGE  |  AWAKENED THOR VS. HELA NECROSWORDS",
        "XXVII  |  WARRIOR FALLS  |  T'CHALLA & SHURI IN GOLDEN CITY",
        "XXVIII  |  NEW YORK SANCTUM  |  IRON MAN NANOTECH & STRANGE",
        "XXIX  |  NIDAVELLIR FORGE  |  THOR IGNITES THE DYING STAR",
        "XXX  |  BATTLE OF TITAN  |  DOCTOR STRANGE THOUSAND IMAGES VS. THANOS",
        "XXXI  |  AVENGERS COMPOUND  |  TRINITY VS. WARLORD THANOS",
        "XXXII  |  WESTVIEW HEX  |  WANDA MAXIMOFF THE SCARLET WITCH",
        "XXXIII  |  TA LO VILLAGE  |  SHANG-CHI TEN RINGS & GREAT PROTECTOR",
        "XXXIV  |  STATUE OF LIBERTY  |  THREE SPIDER-MEN SWING IN UNISON",
        "XXXV  |  END OF TIME CITADEL  |  LOKI GOD OF STORIES WORLD TREE",
        "XXXVI  |  FINAL SACRIFICE  |  AND I... AM... IRON MAN SNAP",
        "XXXVII  |  BROOKLYN 1943  |  STEVE ROGERS SUPER-SOLDIER VITA-RAY CHAMBER",
        "XXXVIII  |  STARK TOWER  |  IRON MAN MARK VII VS. LOKI CHITAURI SCEPTER",
        "XXXIX  |  ASGARD RAGNAROK  |  ETERNAL FLAME SURTUR TWILIGHT SWORD",
        "XL  |  VORMIR ECLIPSE  |  RED SKULL & THE SOUL STONE ALTAR",
        "XLI  |  QUANTUM REALM  |  ANT-MAN SUBATOMIC TIME VORTEX",
        "XLII  |  KAMAR-TAJ SIEGE  |  DOCTOR STRANGE VS. THE SCARLET WITCH",
        "XLIII  |  TALOKAN RISING  |  SHURI BLACK PANTHER VS. NAMOR",
        "XLIV  |  MULTIVERSE SAGA  |  WOLVERINE & DEADPOOL PORTAL SHOWDOWN"
    };

    private static final String[] DCU_TITLES = {
        "I  |  GOTHAM CATHEDRAL  |  THE DARK KNIGHT ON GARGOYLE",
        "II  |  GOTHAM STREETS  |  THE BATMOBILE TUMBLER PURSUIT",
        "III  |  METROPOLIS SKIES  |  SUPERMAN MAN OF STEEL FLIGHT",
        "IV  |  KRYPTONIAN MIGHT  |  SUPERMAN HEAT VISION BEAM",
        "V  |  THEMYSCIRA  |  WONDER WOMAN & LASSO OF TRUTH",
        "VI  |  CENTRAL CITY  |  THE FLASH ENTERING THE SPEED FORCE",
        "VII  |  KINGDOM OF ATLANTIS  |  AQUAMAN & TRIDENT OF ATLAN",
        "VIII  |  SECTOR 2814  |  GREEN LANTERN POWER RING CONSTRUCT",
        "IX  |  GOTHAM DOCKS  |  ARMORED BATMAN VS. SUPERMAN",
        "X  |  ARKHAM ASYLUM  |  BATMAN & THE BATWING GLIDE",
        "XI  |  HALL OF JUSTICE  |  THE JUSTICE LEAGUE SEVEN UNITED",
        "XII  |  APOKOLIPS FINALE  |  JUSTICE LEAGUE VS. DARKSEID",
        "XIII  |  DOOM OF KRYPTON  |  KAL-EL STARSHIP LAUNCH FROM KRYPTON",
        "XIV  |  FORTRESS OF SOLITUDE  |  FIRST FLIGHT ABOVE ARCTIC ICE",
        "XV  |  HIMALAYAN GLACIER  |  BRUCE WAYNE VS. RA'S AL GHUL",
        "XVI  |  LOWER FIFTH AVENUE  |  BATPOD VS. THE JOKER SEMI-TRUCK",
        "XVII  |  GOTHAM STOCK EXCHANGE  |  BATMAN VS. BANE IN GOTHAM",
        "XVIII  |  INDIAN OCEAN  |  SUPERMAN DESTROYS THE WORLD ENGINE",
        "XIX  |  METROPOLIS RUINS  |  SUPERMAN VS. GENERAL ZOD AERIAL CLASH",
        "XX  |  NO MAN'S LAND  |  WONDER WOMAN DEFLECTING MORTAR FIRE",
        "XXI  |  BELGIAN AIRFIELD  |  WONDER WOMAN VS. ARES GOD OF WAR",
        "XXII  |  STAR LABS ACCELERATOR  |  BARRY ALLEN STRUCK BY LIGHTNING",
        "XXIII  |  SPEED FORCE TUNNEL  |  THE FLASH VS. REVERSE-FLASH DUEL",
        "XXIV  |  HIDDEN SEA  |  AQUAMAN COMMANDS THE KARATHEN LEVIATHAN",
        "XXV  |  SICILY ROOFTOPS  |  AQUAMAN VS. BLACK MANTA PLASMA BEAM",
        "XXVI  |  PLANET OA CITADEL  |  CENTRAL POWER BATTERY & LANTERN CORPS",
        "XXVII  |  WAR OF LIGHT  |  HAL JORDAN VS. SINESTRO YELLOW RING",
        "XXVIII  |  ROCK OF ETERNITY  |  SHAZAM LIVING LIGHTNING TRANSFORMATION",
        "XXIX  |  KAHNDAQ THRONE  |  BLACK ADAM VS. DR. FATE & HAWKMAN",
        "XXX  |  GOTHAM HARBOR  |  TRINITY VS. DOOMSDAY ENERGY WAVE",
        "XXXI  |  STAR LABS ROOFTOP  |  CYBORG APOKOLIPS MOTHER BOX CANNON",
        "XXXII  |  BLUDHAVEN ROOFTOPS  |  NIGHTWING ELECTRIC ESCRIMA STICKS",
        "XXXIII  |  POZNAN NUCLEAR PLANT  |  THE FLASH AT THE SPEED OF TIME",
        "XXXIV  |  SOLAR ORBIT  |  BLACK SUIT SUPERMAN SOLAR RECHARGE",
        "XXXV  |  NEO-GOTHAM 2039  |  BATMAN BEYOND TERRY MCGINNIS FLIGHT",
        "XXXVI  |  WATCHTOWER ORBIT  |  JUSTICE LEAGUE OVER PLANET EARTH",
        "XXXVII  |  WAYNE MANOR BATCAVE  |  BATMAN AT THE BATCOMPUTER",
        "XXXVIII  |  METROPOLIS DAILY PLANET  |  SUPERMAN CATCHES THE HELICOPTER",
        "XXXIX  |  THEMYSCIRA BEACH  |  WONDER WOMAN AMAZONIAN SHIELD WALL",
        "XL  |  ATLANTIS RING OF FIRE  |  AQUAMAN VS. KING ORM TRIDENT DUEL",
        "XLI  |  GOTHAM FLOOD  |  THE BATMAN RED FLARE WALK IN THE DARK",
        "XLII  |  APOKOLIPS CITADEL  |  SUPERMAN VS. STEPPENWOLF ELECTRO-AXE",
        "XLIII  |  TOWER OF FATE  |  DOCTOR FATE GOLDEN ANKH OF NABU",
        "XLIV  |  KINGDOM COME  |  ELDER SUPERMAN & ARMORED BATMAN"
    };

    private static final String[] F1_TITLES = {
        "I  |  LIGHTS OUT  |  FIVE RED LIGHTS AT BAHRAIN NIGHT GP",
        "II  |  MONACO GRAND PRIX  |  FAIRMONT HAIRPIN & TUNNEL BLAST",
        "III  |  MONZA TEMPLE OF SPEED  |  SCUDERIA FERRARI TIFOSI TROPHY LIFT",
        "IV  |  SPA-FRANCORCHAMPS  |  EAU ROUGE & RAIDILLON FULL-THROTTLE CLIMB",
        "V  |  SILVERSTONE CIRCUIT  |  MAGGOTTS & BECKETTS HIGH-G APEX",
        "VI  |  SUZUKA CIRCUIT  |  130R & FIGURE-EIGHT CROSSOVER DUEL",
        "VII  |  INTERLAGOS BRAZIL  |  AYRTON SENNA BRAZILIAN FLAG & TROPHY LIFT",
        "VIII  |  MARINA BAY SINGAPORE  |  FLOODLIT STREET CIRCUIT TITANIUM SPARKS",
        "IX  |  LAS VEGAS STRIP  |  NEON SPHERE STRAIGHTAWAY DRS OVERTAKE",
        "X  |  RED BULL RING  |  1.8-SECOND WORLD RECORD PIT STOP CREW",
        "XI  |  YAS MARINA ABU DHABI  |  WORLD CHAMPIONSHIP DONUTS & FIREWORKS",
        "XII  |  PODIUM CEREMONY  |  CHAMPAGNE SPRAY & GOLD TROPHY LIFT",
        "XIII  |  MONACO SWIMMING POOL  |  SENNA McLAREN MP4/4 QUALIFYING LAP",
        "XIV  |  SILVERSTONE FINALE  |  HAMILTON UNION JACK & BRITISH GP TROPHY LIFT",
        "XV  |  ZANDVOORT BANKING  |  VERSTAPPEN ORANGE ARMY & TROPHY LIFT",
        "XVI  |  MIAMI AUTODROME  |  LANDO NORRIS PAPAYA MAIDEN WIN TROPHY LIFT",
        "XVII  |  MONZA PARABOLICA  |  CHARLES LECLERC MONZA WINNER TROPHY LIFT",
        "XVIII  |  AUSTIN COTA TURN 1  |  STEEP UPHILL FOUR-WIDE START CHARGE",
        "XIX  |  IMOLA TAMBURELLO  |  SCHUMACHER FERRARI F2004 V10 & TROPHY LIFT",
        "XX  |  MONTREAL QUEBEC  |  WALL OF CHAMPIONS CHICANE BRUSH",
        "XXI  |  BAKU CITY CIRCUIT  |  CASTLE SECTION THREADING THE NEEDLE",
        "XXII  |  JEDDAH CORNICHE  |  RED SEA 330 KM/H NIGHT STREET DUEL",
        "XXIII  |  MELBOURNE ALBERT PARK  |  PIAGGIO LAKESIDE HIGH-SPEED CHICANE",
        "XXIV  |  SUZUKA CHICANE  |  SENNA McLAREN VS. PROST FERRARI SHOWDOWN",
        "XXV  |  DIJON PRENOIS 1979  |  VILLENEUVE FERRARI VS. ARNOUX WHEEL-TO-WHEEL",
        "XXVI  |  SPA KEMMEL STRAIGHT  |  HAKKINEN McLAREN DOUBLE PASS ON SCHUMACHER",
        "XXVII  |  INTERLAGOS 2008  |  LAST-CORNER WORLD TITLE DECIDER IN THE RAIN",
        "XXVIII  |  YAS MARINA 2010  |  SEBASTIAN VETTEL YOUNGEST CHAMPION TROPHY LIFT",
        "XXIX  |  BARCELONA CATALUNYA  |  FERNANDO ALONSO RENAULT R25 HOME TROPHY LIFT",
        "XXX  |  MELBOURNE 2009  |  JENSON BUTTON BRAWN GP FLUORESCENT TROPHY LIFT",
        "XXXI  |  ESTORIL MASTERCLASS  |  SENNA LOTUS 97T BLACK & GOLD WET RACE WIN",
        "XXXII  |  NÜRBURGRING LEGENDS  |  NIKI LAUDA FERRARI & FANGIO SILVER ARROW",
        "XXXIII  |  SILVERSTONE 1992  |  NIGEL MANSELL RED FIVE WILLIAMS FW14B",
        "XXXIV  |  AUSTIN COTA 2018  |  KIMI RÄIKKÖNEN ICEMAN FERRARI TROPHY LIFT",
        "XXXV  |  MONZA 2021 PODIUM  |  McLAREN ONE-TWO FINISH & TROPHY LIFT",
        "XXXVI  |  SAKHIR OUTER LOOP  |  LAST-TO-FIRST NIGHT VICTORY TROPHY LIFT",
        "XXXVII  |  SILVERSTONE CLUB  |  THREE-WIDE WHEEL-TO-WHEEL APEX BATTLE",
        "XXXVIII  |  MARINA BAY FINALE  |  CARLOS SAINZ SMOOTH OPERATOR FERRARI TROPHY LIFT",
        "XXXIX  |  SPA-FRANCORCHAMPS RAIN  |  SCHUMACHER FERRARI ROOSTER-TAIL CHARGE",
        "XL  |  SUZUKA 2005  |  RÄIKKÖNEN LAST-LAP OUTSIDE OVERTAKE AT 130R",
        "XLI  |  MONACO HARBOR  |  LECLERC PRINCE OF MONACO HOME TROPHY LIFT",
        "XLII  |  SAFETY CAR RESTART  |  GREEN FLAG PACK CHARGE & TIRE WEAVING",
        "XLIII  |  PARC FERMÉ VICTORY  |  DRIVER LEAPING ATOP THE HALO WITH TROPHY",
        "XLIV  |  CHAMPIONS GALA  |  CONSTRUCTORS' & DRIVERS' WORLD TROPHY LIFT"
    };

    private static final String[] CYBERPUNK_TITLES = {
        "I  |  CORPO PLAZA  |  ARASAKA TOWER NEON RAIN & TRAUMA TEAM AV",
        "II  |  WATSON KABUKI  |  QUADRA TURBO-R V-TECH MIDNIGHT DRIFT",
        "III  |  AFTERLIFE VIP  |  LEGEND OF NIGHT CITY & JOHNNY SILVERHAND",
        "IV  |  SANDEVISTAN  |  DAVID MARTINEZ EMERALD TIME-DILATION SLASH",
        "V  |  JAPANTOWN BRIDGE  |  YAIBA KUSANAGI AKIRA POWER-SLIDE",
        "VI  |  THE BLACKWALL  |  LUCY & V DEEP CYBERSPACE NETRUNNER DIVE",
        "VII  |  KONPEKI PLAZA  |  THE RELIC BIOCHIP HEIST & FLATHEAD BOT",
        "VIII  |  PACIFICA PIER  |  VOODOO BOYS SUB-NET DATA MATRIX",
        "IX  |  LIZZIE'S BAR  |  JUDY ALVAREZ BRAIN-DANCE HOLOGRAM STUDIO",
        "X  |  BADLANDS HIGHWAY  |  PANAM PALMER & ALDECALDOS BASILISK TANK",
        "XI  |  MEGABUILDING H10  |  LITTLE CHINA HOLOGRAPHIC KOI SKYWAY",
        "XII  |  CYBERPSYCHO SIGHTING  |  MAXTAC MANTIS BLADES INTERCEPT",
        "XIII  |  ARASAKA PARADE  |  DASHI HOLO-DRAGONS & ODA KATANA DUEL",
        "XIV  |  ORBITAL AIR  |  TYCHO LUNAR SHUTTLE LAUNCH OVER MORRO ROCK",
        "XV  |  DOGTOWN STADIUM  |  SOLOMON REED & SONGBIRD BLACKWALL SURGE",
        "XVI  |  TOTENTANZ CLUB  |  MAELSTROM CHROME LASER RAVE",
        "XVII  |  VIKTOR VEKTOR CLINIC  |  KIROSHI OPTICS & GORILLA ARMS UPGRADE",
        "XVIII  |  CHERRY BLOSSOM MARKET  |  WAKAKO OKADA & TYGER CLAWS BIKES",
        "XIX  |  DELAMAIN HQ  |  ROGUE AI COMBAT CAB HIGH-SPEED PURSUIT",
        "XX  |  BIOTECHNICA FLATS  |  SYNTHETIC PROTEIN FARMS & ACID FOG",
        "XXI  |  TRAUMA TEAM PLATINUM  |  AV-4 MEDEVAC GUNSHIP EXTRACTION",
        "XXII  |  SAMURAI REUNION  |  KERRY EURODYNE & SILVERHAND GUITAR SOLO",
        "XXIII  |  REBECCA & PILAR  |  EDGERUNNERS HEAVY SHOTGUN OVERDRIVE",
        "XXIV  |  CYBERSKELETON  |  GRAVITY-CRUSH CONVOY SHOWDOWN",
        "XXV  |  ARASAKA ATRIUM  |  DON'T FEAR THE REAPER SOLO ASSAULT",
        "XXVI  |  ADAM SMASHER  |  BORG TITAN VS. SANDEVISTAN THERMAL KATANA",
        "XXVII  |  MIKOSHI SOULKILLER  |  DIGITAL ENGRAM STARLIGHT BRIDGE",
        "XXVIII  |  MOONLIGHT PROMISE  |  I REALLY WANT TO STAY AT YOUR HOUSE",
        "XXIX  |  HEYWOOD GLEN  |  VALENTINOS LOWRIDER GOLD HYDRAULIC CRUISE",
        "XXX  |  CHARTER HILL  |  RAYFIELD CALIBURN HYPERCAR SONIC BOOM",
        "XXXI  |  NORTHSIDE DOCKS  |  MILITECH MECH & DRONE CONVOY",
        "XXXII  |  SANTO DOMINGO  |  6TH STREET ROOFTOP SNIPER OVERWATCH",
        "XXXIII  |  HEAVY HEARTS PYRAMID  |  MR. HANDS HIGH-STAKES FIXER GIG",
        "XXXIV  |  CYNOSURE BUNKER  |  CERBERUS AI CORE & RED DATA TENDRILS",
        "XXXV  |  THE STAR ENDING  |  PANAM & V CROSSING THE BORDER CANYON",
        "XXXVI  |  THE SUN ENDING  |  CRYSTAL PALACE ZERO-G SPACE HEIST",
        "XXXVII  |  SECTOR NINE  |  MOTOKO KUSANAGI THERMO-OPTIC ROOFTOP DIVE",
        "XXXVIII  |  LOS ANGELES 2049  |  SPINNER POLICE CRUISER OVER SEA WALL",
        "XXXIX  |  NEO-TOKYO HIGHWAY  |  SHOTARO KANEDA RED LASER MOTORBIKE",
        "XL  |  TYRELL PYRAMID  |  TEARS IN RAIN ROOFTOP MONOLOGUE",
        "XLI  |  JIG-JIG STREET  |  NEON KANJI SIGNS & STEAMING RAMEN STALL",
        "XLII  |  MONOWIRE WHIP  |  NETRUNNER THERMAL MONOWIRE ARC SLASH",
        "XLIII  |  RELIC OVERCLOCK  |  JOHNNY & V DUAL MALORIAN PISTOL BLAST",
        "XLIV  |  NIGHT CITY LEGENDS  |  TOP OF ARASAKA TOWER NEON SUNRISE"
    };

    // Cached Ambilight Edge LinearGradients (rebuilt ONLY when RGB or screen dimensions change)
    private LinearGradient topEdgeShader, botEdgeShader, leftEdgeShader, rightEdgeShader;
    private int cachedShaderRgb = -1, cachedShaderW = -1, cachedShaderH = -1;

    private boolean running = false;
    private int paletteIndex = 0;

    // v6.0 Dynamic 120 FPS (60Hz / 90Hz / 120Hz VRR) sub-microsecond frame timing & exponential damping state
    private long lastFrameNanos = 0L;
    private float frameDt = 1f / 60f;

    private static float nowSec() {
        return (SystemClock.elapsedRealtimeNanos() % 3_600_000_000_000L) * 1e-9f;
    }

    private static float smoothstep(float x) {
        float c = Math.max(0f, Math.min(1f, x));
        return c * c * (3f - 2f * c);
    }

    private static float dampFactor(float rate60Hz, float dt) {
        float lambda = -60f * (float) Math.log(Math.max(0.001f, 1f - rate60Hz));
        return 1f - (float) Math.exp(-lambda * dt);
    }

    // Reactive FancyLEDs / Hogwarts House color state
    private float curR = 228f, curG = 58f, curB = 54f;
    private float targetR = 228f, targetG = 58f, targetB = 54f;
    private boolean prismMode = false;
    private boolean edgeGlowEnabled = false;

    // Live FancyLEDs Hub (10.0.0.153 / Tuya UDP 6667) state
    private volatile boolean fancyHubOnline = true;
    private volatile int fancyPulseEnergy = 0;
    private Thread hubListenerThread;

    interface HubStatusListener {
        void onHubStatusChanged(boolean online, String ip);
    }
    private HubStatusListener hubListener;

    interface ScreensaverThemeListener {
        void onScreensaverThemeChanged(int theme);
    }
    private ScreensaverThemeListener screensaverThemeListener;

    // v6.0 Universal Landscape & Foldable Cinema Story Screensaver Mode (triggered after 2 minutes of inactivity):
    // - Story animations grow huge and fill up to the bottom 3/4th of the screen.
    // - Story caption & dots at the bottom keep their standard size.
    // - All 344 story scenes across all 7 Theme Universes are pooled into a single randomized shuffle,
    //   updating both SkyView's universe backdrop and the top-left Clock's franchise typography.
    private boolean screensaverMode = false;
    private float screensaverZoom = 0f;
    private int screensaverOverrideScene = -1;
    private int lastScreensaverSlot = -1;
    private static final int TOTAL_POOL_SCENES = SCENE_COUNT + UNIVERSE_SCENE_COUNT * 7; // 388 total story scenes across 8 themes
    private final int[] shuffledPool = new int[TOTAL_POOL_SCENES];
    private int poolCursor = TOTAL_POOL_SCENES;
    private final java.util.Random poolRng = new java.util.Random();

    SkyView(Context context, float scale, int paletteIndex, Typeface cinzel) {
        super(context);
        this.scale = scale;
        this.paletteIndex = paletteIndex;
        this.cinzelFont = cinzel;
        try {
            this.f1Font = Typeface.createFromAsset(context.getAssets(), "f1.ttf");
        } catch (Throwable ignored) {
            this.f1Font = cinzel;
        }
        initParticles(1920f * scale, 1080f * scale);
        buildGlowSprite();
        startFancyLedsListener();
    }

    void setHubStatusListener(HubStatusListener l) {
        this.hubListener = l;
    }

    void setScreensaverThemeListener(ScreensaverThemeListener l) {
        this.screensaverThemeListener = l;
    }

    private void reshuffleStoryPool() {
        int idx = 0;
        int[] counts = {
            CHAPTER_TITLES.length,
            NARUTO_TITLES.length,
            DEMON_SLAYER_TITLES.length,
            STAR_WARS_TITLES.length,
            MCU_TITLES.length,
            DCU_TITLES.length,
            F1_TITLES.length,
            CYBERPUNK_TITLES.length
        };
        for (int th = 0; th < counts.length; th++) {
            for (int sc = 0; sc < counts[th]; sc++) {
                if (idx < TOTAL_POOL_SCENES) {
                    shuffledPool[idx++] = (th << 8) | sc;
                }
            }
        }
        for (int i = idx - 1; i > 0; i--) {
            int j = poolRng.nextInt(i + 1);
            int tmp = shuffledPool[i];
            shuffledPool[i] = shuffledPool[j];
            shuffledPool[j] = tmp;
        }
        for (int i = 1; i < idx - 1; i++) {
            int prevTheme = (shuffledPool[i - 1] >>> 8) & 0xFF;
            int curTheme = (shuffledPool[i] >>> 8) & 0xFF;
            if (curTheme == prevTheme) {
                for (int k = i + 1; k < idx; k++) {
                    if (((shuffledPool[k] >>> 8) & 0xFF) != prevTheme) {
                        int t = shuffledPool[i];
                        shuffledPool[i] = shuffledPool[k];
                        shuffledPool[k] = t;
                        break;
                    }
                }
            }
        }
        poolCursor = 0;
    }

    private void advanceRandomScreensaverScene(int slot) {
        lastScreensaverSlot = slot;
        if (poolCursor >= TOTAL_POOL_SCENES) {
            reshuffleStoryPool();
        }
        int packed = shuffledPool[poolCursor++];
        int nextTheme = (packed >>> 8) & 0xFF;
        int nextScene = packed & 0xFF;
        screensaverOverrideScene = nextScene;
        if (this.universeTheme != nextTheme) {
            this.universeTheme = nextTheme;
            if (baseSky != null && !baseSky.isRecycled()) {
                baseSky.recycle();
                baseSky = null;
            }
        }
        int[] themeColors = {0xFFFFD54F, 0xFFFF5722, 0xFF00E5FF, 0xFF40C4FF, 0xFFB388FF, 0xFFFFCA28, 0xFFFF1801, 0xFF00F0FF};
        int col = themeColors[Math.max(0, Math.min(themeColors.length - 1, nextTheme))];
        setReactiveColor(col, false);
        if (screensaverThemeListener != null) {
            screensaverThemeListener.onScreensaverThemeChanged(nextTheme);
        }
    }

    void setScreensaverMode(boolean active, int userSavedTheme) {
        if (this.screensaverMode == active) return;
        this.screensaverMode = active;
        if (active) {
            lastScreensaverSlot = -1;
            float rawT = nowSec();
            int slot = (int) ((rawT * 0.5f) / CHAPTER_SEC);
            advanceRandomScreensaverScene(slot);
        } else {
            screensaverOverrideScene = -1;
            lastScreensaverSlot = -1;
            setUniverseTheme(userSavedTheme);
            if (screensaverThemeListener != null) {
                screensaverThemeListener.onScreensaverThemeChanged(userSavedTheme);
            }
        }
        invalidate();
    }

    boolean isScreensaverMode() {
        return screensaverMode;
    }

    void setEdgeGlowEnabled(boolean enabled) {
        this.edgeGlowEnabled = enabled;
        invalidate();
    }

    boolean isEdgeGlowEnabled() {
        return edgeGlowEnabled;
    }

    void setReactiveColor(int rgb, boolean prism) {
        this.prismMode = prism;
        this.targetR = Color.red(rgb);
        this.targetG = Color.green(rgb);
        this.targetB = Color.blue(rgb);
        if (running) postInvalidateOnAnimation();
    }

    /**
     * Interactive Wizarding World Twists triggered when remote focus moves to a tile:
     * 1. Spawns a trail of Marauder's Map golden ink footsteps walking from the previous tile to the new tile.
     * 2. Sends the autonomous winged Golden Snitch companion flying to hover above the focused tile.
     * 3. Emits a 14-particle House-colored Lumos Maxima wand-spark burst around the tile.
     */
    void onTileFocused(float cx, float cy, float topY, int houseCol) {
        float now = nowSec();
        snitchTargetX = cx;
        snitchTargetY = Math.max(px(32f), topY - px(16f));

        if (lastFocusX >= 0f && lastFocusY >= 0f) {
            float dx = cx - lastFocusX, dy = cy - lastFocusY;
            float dist = (float) Math.hypot(dx, dy);
            if (dist > px(24f)) {
                float ux = dx / dist, uy = dy / dist;
                float nx = -uy, ny = ux; // perpendicular for left/right boot offset
                int steps = Math.min(6, Math.max(3, Math.round(dist / px(64f))));
                for (int s = 1; s <= steps; s++) {
                    float frac = s / (float) (steps + 1);
                    float side = (s % 2 == 0) ? 1f : -1f;
                    int b = (footstepHead % FOOTSTEP_MAX) * 6;
                    footstepHead++;
                    footsteps[b] = lastFocusX + dx * frac + nx * side * px(7f);
                    footsteps[b + 1] = lastFocusY + dy * frac + ny * side * px(7f);
                    footsteps[b + 2] = ux;
                    footsteps[b + 3] = uy;
                    footsteps[b + 4] = now + s * 0.08f; // staggered step-by-step appearance
                    footsteps[b + 5] = side;
                }
            }
        } else {
            snitchCurX = cx;
            snitchCurY = snitchTargetY;
        }
        lastFocusX = cx;
        lastFocusY = cy;

        int rgb = houseCol & 0xFFFFFF;
        for (int i = 0; i < BURST_COUNT; i++) {
            int b = i * 6;
            double ang = (i * Math.PI * 2.0 / BURST_COUNT) + (now % 0.5f);
            float spd = px(75f + (i % 3) * 42f);
            burstSparks[b] = cx + (float) Math.cos(ang) * px(28f);
            burstSparks[b + 1] = cy + (float) Math.sin(ang) * px(18f);
            burstSparks[b + 2] = (float) Math.cos(ang) * spd;
            burstSparks[b + 3] = (float) Math.sin(ang) * spd - px(18f);
            burstSparks[b + 4] = now;
            burstSparks[b + 5] = Float.intBitsToFloat(i % 2 == 0 ? 0xFFFFE490 : rgb);
        }
        if (running) postInvalidateOnAnimation();
    }

    private int activeSceneCount() {
        return getSceneTitlesForTheme(universeTheme).length;
    }

    String[] getSceneTitlesForTheme(int theme) {
        if (theme == 1) return NARUTO_TITLES;
        if (theme == 2) return DEMON_SLAYER_TITLES;
        if (theme == 3) return STAR_WARS_TITLES;
        if (theme == 4) return MCU_TITLES;
        if (theme == 5) return DCU_TITLES;
        if (theme == 6) return F1_TITLES;
        if (theme == 7) return CYBERPUNK_TITLES;
        return CHAPTER_TITLES;
    }

    int getCurrentSceneIndex() {
        int c = Math.max(1, activeSceneCount());
        return ((lastRenderedSceneIndex % c) + c) % c;
    }

    void jumpToScene(int idx, boolean lock) {
        int c = Math.max(1, activeSceneCount());
        int normalized = ((idx % c) + c) % c;
        float nowT = ((SystemClock.elapsedRealtimeNanos() % 3_600_000_000_000L) * 1e-9f) * 0.5f * storySpeedMultiplier;
        int baseSlot = ((int) (nowT / CHAPTER_SEC)) % c;
        this.sceneOffset = ((normalized - baseSlot) % c + c) % c;
        this.lockedSceneIndex = normalized;
        this.lastRenderedSceneIndex = normalized;
        this.sceneLocked = lock;
        invalidate();
    }

    boolean isSceneLocked() {
        return sceneLocked;
    }

    void setSceneLocked(boolean locked) {
        if (locked && !this.sceneLocked) {
            this.lockedSceneIndex = getCurrentSceneIndex();
        }
        this.sceneLocked = locked;
        invalidate();
    }

    float getStorySpeedMultiplier() {
        return storySpeedMultiplier;
    }

    void setStorySpeedMultiplier(float mult) {
        this.storySpeedMultiplier = Math.max(0.25f, Math.min(3.0f, mult));
        invalidate();
    }

    int getWeatherMood() {
        return weatherMood;
    }

    void setWeatherMood(int mood) {
        this.weatherMood = Math.max(0, Math.min(5, mood));
        invalidate();
    }

    void setLiveWeatherCondition(String raw) {
        if (raw == null) return;
        String s = raw.toLowerCase(java.util.Locale.ROOT);
        if (s.contains("storm") || s.contains("thunder")) {
            liveWeatherMood = 4;
        } else if (s.contains("snow") || s.contains("blizzard") || s.contains("flurr") || s.contains("ice")) {
            liveWeatherMood = 3;
        } else if (s.contains("rain") || s.contains("drizzle") || s.contains("shower")) {
            liveWeatherMood = 2;
        } else {
            liveWeatherMood = 1;
        }
        invalidate();
    }

    private int resolveActiveScene(float cycleTime, int count) {
        if (screensaverMode && screensaverOverrideScene >= 0) {
            return screensaverOverrideScene % count;
        }
        if (sceneLocked && lockedSceneIndex >= 0) {
            lastRenderedSceneIndex = lockedSceneIndex % count;
            return lastRenderedSceneIndex;
        }
        lastRenderedSceneIndex = (((int) (cycleTime / CHAPTER_SEC) + sceneOffset) % count + count) % count;
        return lastRenderedSceneIndex;
    }

    private float resolveStoryFade(float p) {
        if (sceneLocked && !screensaverMode) return 1.0f;
        return p < 0.08f ? smoothstep(p / 0.08f) : (p > 0.92f ? smoothstep((1f - p) / 0.08f) : 1f);
    }

    void nextStoryScene() {
        if (screensaverMode) {
            advanceRandomScreensaverScene(lastScreensaverSlot);
            invalidate();
            return;
        }
        int c = activeSceneCount();
        if (sceneLocked) {
            lockedSceneIndex = (lockedSceneIndex + 1) % c;
            lastRenderedSceneIndex = lockedSceneIndex;
        } else {
            sceneOffset = (sceneOffset + 1) % c;
        }
        invalidate();
    }

    void prevStoryScene() {
        if (screensaverMode) {
            advanceRandomScreensaverScene(lastScreensaverSlot);
            invalidate();
            return;
        }
        int c = activeSceneCount();
        if (sceneLocked) {
            lockedSceneIndex = (lockedSceneIndex + c - 1) % c;
            lastRenderedSceneIndex = lockedSceneIndex;
        } else {
            sceneOffset = (sceneOffset + c - 1) % c;
        }
        invalidate();
    }

    void setScale(float newScale) {
        if (Math.abs(this.scale - newScale) > 0.005f) {
            this.scale = newScale;
            if (baseSky != null && !baseSky.isRecycled()) {
                baseSky.recycle();
                baseSky = null;
            }
            invalidate();
        }
    }

    void setUniverseTheme(int theme) {
        if (this.universeTheme != theme) {
            this.universeTheme = theme;
            this.sceneOffset = 0;
            if (baseSky != null && !baseSky.isRecycled()) {
                baseSky.recycle();
                baseSky = null;
            }
            invalidate();
        }
    }

    int getUniverseTheme() {
        return universeTheme;
    }

    private void startFancyLedsListener() {
        // Only listen for local FancyLEDs Sync Box UDP broadcasts on Android TV devices
        try {
            android.app.UiModeManager um = (android.app.UiModeManager) getContext().getSystemService(Context.UI_MODE_SERVICE);
            boolean isTv = (um != null && um.getCurrentModeType() == android.content.res.Configuration.UI_MODE_TYPE_TELEVISION)
                    || getContext().getPackageManager().hasSystemFeature(android.content.pm.PackageManager.FEATURE_LEANBACK);
            if (!isTv) return;
        } catch (Throwable ignored) {
            return;
        }
        hubListenerThread = new Thread(() -> {
            DatagramSocket ds = null;
            try {
                ds = new DatagramSocket(null);
                ds.setReuseAddress(true);
                ds.bind(new InetSocketAddress(6667));
                ds.setSoTimeout(4000);
                byte[] buf = new byte[512];
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        DatagramPacket dp = new DatagramPacket(buf, buf.length);
                        ds.receive(dp);
                        String ip = dp.getAddress() != null ? dp.getAddress().getHostAddress() : "local";
                        int len = dp.getLength();
                        fancyHubOnline = true;
                        fancyPulseEnergy = Math.min(100, 45 + (len % 55));
                        if (hubListener != null) post(() -> hubListener.onHubStatusChanged(true, ip));
                    } catch (java.net.SocketTimeoutException ignored) {
                        // Passive UDP listener only: never probe hardcoded private LAN IPs (MASVS-NETWORK)
                    }
                }
            } catch (Throwable ignored) {
            } finally {
                if (ds != null) try { ds.close(); } catch (Throwable ignored) {}
            }
        }, "fancyleds-sync");
        hubListenerThread.setDaemon(true);
        hubListenerThread.start();
    }

    void setPalette(int index) {
        if (this.paletteIndex != index) {
            this.paletteIndex = index;
            if (baseSky != null && !baseSky.isRecycled()) {
                baseSky.recycle();
                baseSky = null;
            }
            invalidate();
        }
    }

    void setRunning(boolean run) {
        this.running = run;
        if (run) postInvalidateOnAnimation();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && running) postInvalidateOnAnimation();
    }

    private float px(float v) {
        return v * scale;
    }

    private void buildGlowSprite() {
        int s = Math.max(32, Math.round(px(96)));
        glowSprite = Bitmap.createBitmap(s, s, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(glowSprite);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        float r = s / 2f;
        p.setShader(new RadialGradient(r, r, r,
                new int[] {0x88FFD56B, 0x38FFA028, 0x00FF8000},
                new float[] {0f, 0.45f, 1f}, Shader.TileMode.CLAMP));
        c.drawCircle(r, r, r, p);
    }

    private void initParticles(float w, float h) {
        long seed = 0x484F475741525453L;
        for (int i = 0; i < STAR_COUNT; i++) {
            int b = i * 6;
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float rx = ((seed >>> 16) & 0xFFFF) / 65535f;
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float ry = ((seed >>> 16) & 0xFFFF) / 65535f;
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float rr = ((seed >>> 16) & 0xFFFF) / 65535f;
            stars[b] = rx * w;
            stars[b + 1] = ry * (h * 0.65f);
            stars[b + 2] = px(1.2f + rr * 2.1f);
            stars[b + 3] = 1.2f + (i % 7) * 0.35f;
            stars[b + 4] = i * 1.7f;
            stars[b + 5] = (i % 8 == 0) ? 1f : 0f;
        }

        boolean isPortrait = h > w;
        float[] cxNorm = isPortrait ? new float[] {
            0.09f, 0.16f, 0.84f, 0.91f, 0.12f, 0.88f, 0.10f, 0.22f, 0.34f,
            0.46f, 0.58f, 0.70f, 0.82f, 0.91f, 0.17f, 0.39f, 0.63f, 0.85f
        } : new float[] {
            0.36f, 0.41f, 0.45f, 0.49f, 0.53f, 0.57f, 0.61f, 0.65f, 0.70f,
            0.39f, 0.47f, 0.55f, 0.63f, 0.74f, 0.79f, 0.84f, 0.89f, 0.94f
        };
        float[] cyNorm = isPortrait ? new float[] {
            0.06f, 0.11f, 0.11f, 0.06f, 0.60f, 0.61f, 0.68f, 0.63f, 0.71f,
            0.65f, 0.72f, 0.64f, 0.69f, 0.66f, 0.75f, 0.77f, 0.75f, 0.73f
        } : new float[] {
            0.07f, 0.16f, 0.06f, 0.18f, 0.08f, 0.17f, 0.07f, 0.19f, 0.14f,
            0.22f, 0.23f, 0.21f, 0.22f, 0.19f, 0.22f, 0.17f, 0.21f, 0.16f
        };
        float[] cScale = {
            0.90f, 0.78f, 0.96f, 0.76f, 1.04f, 0.84f, 0.94f, 0.78f, 0.88f,
            0.72f, 0.70f, 0.82f, 0.74f, 0.86f, 0.74f, 0.88f, 0.72f, 0.78f
        };
        for (int i = 0; i < CANDLE_COUNT; i++) {
            int b = i * 7;
            float sc = cScale[i];
            candles[b] = cxNorm[i] * w;
            candles[b + 1] = cyNorm[i] * h;
            candles[b + 2] = px(7.0f * sc);
            candles[b + 3] = px(42f * sc);
            candles[b + 4] = 1.1f + (i % 5) * 0.22f;
            candles[b + 5] = i * 1.15f;
            candles[b + 6] = i * 2.3f;
        }

        // Initialize 5 Hogwarts House Ghosts gliding among the candles
        float[] gxNorm = isPortrait ? new float[] {0.16f, 0.34f, 0.52f, 0.70f, 0.86f}
                                    : new float[] {0.38f, 0.52f, 0.66f, 0.78f, 0.90f};
        float[] gyNorm = isPortrait ? new float[] {0.64f, 0.70f, 0.62f, 0.72f, 0.66f}
                                    : new float[] {0.11f, 0.18f, 0.09f, 0.20f, 0.14f};
        float[] gSpeedX = {px(24f), -px(20f), px(28f), -px(22f), px(18f)};
        float[] gScale = {1.05f, 0.95f, 1.00f, 1.08f, 0.88f};
        for (int g = 0; g < GHOST_COUNT; g++) {
            int b = g * 7;
            ghosts[b] = gxNorm[g] * w;
            ghosts[b + 1] = gyNorm[g] * h;
            ghosts[b + 2] = gSpeedX[g];
            ghosts[b + 3] = 1.4f + g * 0.35f;
            ghosts[b + 4] = g * 1.9f;
            ghosts[b + 5] = gScale[g];
            ghosts[b + 6] = g; // 0: Nearly Headless Nick, 1: Grey Lady, 2: Fat Friar, 3: Bloody Baron, 4: Myrtle
        }

        for (int i = 0; i < SPARK_COUNT; i++) {
            int b = i * 6;
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float rx = ((seed >>> 16) & 0xFFFF) / 65535f;
            seed = seed * 6364136223846793005L + 1442695040888963407L;
            float ry = ((seed >>> 16) & 0xFFFF) / 65535f;
            sparks[b] = rx * w;
            sparks[b + 1] = ry * h;
            sparks[b + 2] = px((i % 2 == 0 ? 1 : -1) * (3.5f + (i % 4) * 1.8f));
            sparks[b + 3] = -px(6.5f + (i % 5) * 2.5f);
            sparks[b + 4] = px(1.6f + (i % 3) * 1.1f);
            sparks[b + 5] = i * 0.9f;
        }
    }

    // Preserves natural 16:9 architectural proportions of horizon towers/castles on tall portrait phone screens
    private static float skyY(int w, int h, float yFrac) {
        if (h <= w || yFrac < 0.55f) return h * yFrac;
        float groundFrac = 0.900f;
        float refH = w * 0.65f;
        return h * groundFrac + (yFrac - groundFrac) * refH;
    }

    private float getStageScale(int w, int h) {
        if (h > w) {
            return Math.min(2.10f, Math.max(1.75f, (h / (float) Math.max(1, w)) * 1.12f));
        }
        return 1.50f + 0.80f * screensaverZoom;
    }

    // Scales the bottom story stage. In Screensaver Mode (screensaverZoom -> 1.0f) across all device types
    // (TV, Portrait/Landscape Phones, Foldables & Tablets), stories grow huge to take over up to the bottom 3/4th of the screen.
    private void applyStageTransform(Canvas c, int w, int h, float groundY) {
        boolean isNarrowVertical = w < h * 0.75f;
        float z = smoothstep(screensaverZoom);
        if (isNarrowVertical) {
            float stScale = getStageScale(w, h);
            float sx = stScale + 0.35f * z;
            float sy = stScale + 0.95f * z;
            c.scale(sx, sy, w * 0.45f, groundY);
        } else {
            float baseScale = h > w ? Math.min(1.85f, Math.max(1.55f, (h / (float) Math.max(1, w)) * 1.05f)) : 1.50f;
            float sx = baseScale + 0.80f * z;
            float sy = baseScale + 1.05f * z;
            float pivotX = w * (0.45f - 0.04f * z);
            c.scale(sx, sy, pivotX, groundY);
        }
    }

    // Renders a subtle cinematic stage ground-plane reflection & rim glow (Gestalt Figure-Ground & Disney Staging)
    private void drawStageGroundGlow(Canvas c, int w, float groundY, int aBase, int glowRgb) {
        paint.setStyle(Paint.Style.FILL);
        int rgb = glowRgb & 0x00FFFFFF;
        paint.setColor((Math.max(0, aBase / 9) << 24) | rgb);
        c.drawOval(w * 0.24f, groundY - px(10f), w * 0.68f, groundY + px(8f), paint);
        paint.setColor((Math.max(0, aBase / 5) << 24) | rgb);
        c.drawOval(w * 0.31f, groundY - px(4f), w * 0.61f, groundY + px(3.5f), paint);
    }

    private void drawStoryCaptionAndDots(Canvas c, int w, int h, String title, int scene, int count,
                                         int aBase, int textRgb, int activeDotCol, int idleDotCol, float defaultDotStep) {
        if (sceneLocked && !screensaverMode && title != null) {
            title = "\u2605 PINNED  |  " + title;
        }
        drawStageGroundGlow(c, w, h * 0.900f, aBase, activeDotCol);
        boolean isPortrait = h > w;
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        if (cinzelFont != null) paint.setTypeface(cinzelFont);
        paint.setColor((Math.round(aBase * 0.90f) << 24) | (textRgb & 0x00FFFFFF));

        if (!isPortrait) {
            paint.setTextSize(px(15.0f));
            paint.setLetterSpacing(0.18f);
            c.drawText(title != null ? title : "", w * 0.5f, h * 0.976f, paint);
            paint.setLetterSpacing(0f);

            float step = count > 1 ? Math.min(defaultDotStep, (w * 0.74f) / (count - 1)) : defaultDotStep;
            for (int i = 0; i < count; i++) {
                float dx = w * 0.5f + (i - (count - 1) / 2f) * step;
                float dy = h * 0.946f;
                paint.setColor(i == scene ? activeDotCol : idleDotCol);
                c.drawCircle(dx, dy, i == scene ? px(3.0f) : px(1.5f), paint);
            }
        } else {
            // Portrait Phone / Foldable Cover Screen: crisp mobile-scaled 2-line caption & responsive dot bar
            float textSize = Math.max(px(16.0f), w * 0.0255f);
            paint.setTextSize(textSize);
            paint.setLetterSpacing(0.08f);
            String raw = title != null ? title : "";
            int splitIdx = raw.indexOf(" \u2014 ");
            int delimLen = 3;
            if (splitIdx < 0) {
                splitIdx = raw.indexOf(" - ");
                delimLen = 3;
            }
            if (splitIdx < 0) {
                splitIdx = raw.lastIndexOf('|');
                delimLen = 1;
            }
            float maxLineW = w * 0.88f;
            if (splitIdx > 0) {
                String line1 = raw.substring(0, splitIdx).trim();
                String line2 = raw.substring(splitIdx + delimLen).trim();
                float m2 = Math.max(paint.measureText(line1), paint.measureText(line2)) * 1.10f;
                if (m2 > maxLineW && m2 > 0) {
                    paint.setTextSize(Math.max(w * 0.019f, textSize * (maxLineW / m2)));
                }
                c.drawText(line1, w * 0.5f, h * 0.944f, paint);
                c.drawText(line2, w * 0.5f, h * 0.965f, paint);
            } else {
                float m = paint.measureText(raw) * 1.10f;
                if (m > maxLineW && m > 0) {
                    paint.setTextSize(Math.max(w * 0.019f, textSize * (maxLineW / m)));
                }
                c.drawText(raw, w * 0.5f, h * 0.958f, paint);
            }
            paint.setLetterSpacing(0f);

            float maxDotsSpan = w * 0.78f;
            float step = count > 1 ? Math.min(Math.max(defaultDotStep, w * 0.014f), maxDotsSpan / (count - 1)) : 0f;
            float rActive = Math.max(px(3.2f), w * 0.0050f);
            float rIdle = Math.max(px(1.6f), w * 0.0024f);
            float dy = h * 0.918f;
            for (int i = 0; i < count; i++) {
                float dx = w * 0.5f + (i - (count - 1) / 2f) * step;
                paint.setColor(i == scene ? activeDotCol : idleDotCol);
                c.drawCircle(dx, dy, i == scene ? rActive : rIdle, paint);
            }
        }
    }

    private void ensureBaseSky(int w, int h) {
        if (baseSky != null && !baseSky.isRecycled() && baseSky.getWidth() == w && baseSky.getHeight() == h) return;
        try {
            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setDither(true);

            int topCol = 0xFF050814, midCol = 0xFF0A1022, botCol = 0xFF0D0918;
            int glow1 = 0x32F0B848, glow2 = 0x2E3A62D8, glow3 = 0x288848C8;
            if (universeTheme == 1) {
                // Theme 2: NARUTO — Crimson Uchiha & Nine-Tails Chakra Night Sky
                topCol = 0xFF0B0408; midCol = 0xFF1C0910; botCol = 0xFF0A0407;
                glow1 = 0x42FF5A20; glow2 = 0x3CD81828; glow3 = 0x32FF8820;
            } else if (universeTheme == 2) {
                // Theme 3: DEMON SLAYER — Wisteria Violet & Water/Sun Breathing Night Sky
                topCol = 0xFF060512; midCol = 0xFF130B22; botCol = 0xFF070510;
                glow1 = 0x40B858FF; glow2 = 0x3820D8FF; glow3 = 0x34FF5020;
            } else if (universeTheme == 3) {
                // Theme 4: STAR WARS — Deep Galactic Space & Binary Sunset Horizon
                topCol = 0xFF03050C; midCol = 0xFF091224; botCol = 0xFF12080E;
                glow1 = 0x3C2888FF; glow2 = 0x38E82028; glow3 = 0x36FF8828;
            } else if (universeTheme == 4) {
                // Theme 5: MCU — Cosmic Infinity Stone & Stark Arc-Reactor Night Sky
                topCol = 0xFF060414; midCol = 0xFF130B28; botCol = 0xFF0A0614;
                glow1 = 0x40A848FF; glow2 = 0x3828D8FF; glow3 = 0x36FF8820;
            } else if (universeTheme == 5) {
                // Theme 6: DCU — Dark Deco Gotham Storm & Bat-Signal Night Sky
                topCol = 0xFF040710; midCol = 0xFF0B1426; botCol = 0xFF050811;
                glow1 = 0x42FFE068; glow2 = 0x382898FF; glow3 = 0x32FFB828;
            } else if (universeTheme == 6) {
                // Theme 7: FORMULA 1 — Night Grand Prix Stadium Floodlights, Camera Flashes & Telemetry Sky
                topCol = 0xFF04070E; midCol = 0xFF0B1324; botCol = 0xFF070912;
                glow1 = 0x42C8EEFF; glow2 = 0x3800D2BE; glow3 = 0x36FF1801;
            } else if (universeTheme == 7) {
                // Theme 8: CYBERPUNK 2099 — Night City Neon Cyan, Hot Magenta & Cyber-Yellow Smog Sky
                topCol = 0xFF04030E; midCol = 0xFF0D0824; botCol = 0xFF070414;
                glow1 = 0x4800F0FF; glow2 = 0x42FF0055; glow3 = 0x38FCEE09;
            } else if (paletteIndex == 1) {
                topCol = 0xFF16080A; midCol = 0xFF220E12; botCol = 0xFF0E070A;
                glow1 = 0x44FFB840; glow2 = 0x38B82830;
            } else if (paletteIndex == 2) {
                topCol = 0xFF061310; midCol = 0xFF0A1D18; botCol = 0xFF050C0B;
                glow1 = 0x3448D898; glow2 = 0x2E288878;
            }
            p.setShader(new LinearGradient(0, 0, 0, h,
                    new int[] {topCol, midCol, botCol}, new float[] {0f, 0.54f, 1f}, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, p);

            p.setShader(new RadialGradient(w * 0.16f, h * 0.15f, px(860),
                    new int[] {glow1, glow1 & 0x00FFFFFF}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, p);

            p.setShader(new RadialGradient(w * 0.78f, h * 0.28f, px(920),
                    new int[] {glow2, glow2 & 0x00FFFFFF}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, p);

            // Expanded storybook theater glow across the lower half (y = 580..1080)
            p.setShader(new RadialGradient(w * 0.50f, h * 0.82f, px(980),
                    new int[] {universeTheme == 1 ? 0x38FF6020 : 0x34F5C868, glow3 & 0x00FFFFFF}, null, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, w, h, p);
            p.setShader(null);

            if (universeTheme == 1) {
                // NARUTO SKY: Infinite Tsukuyomi / 3-Tomoe Sharingan Crimson Moon + Hokage Rock & Torii Gates
                float mx = w * 0.85f, my = h * 0.16f, mr = px(38);
                p.setShader(new RadialGradient(mx, my, mr * 4.2f,
                        new int[] {0x48FF2028, 0x18900818, 0x00000000}, new float[] {0f, 0.45f, 1f}, Shader.TileMode.CLAMP));
                c.drawCircle(mx, my, mr * 4.2f, p);
                p.setShader(null);
                p.setColor(0x58E81824);
                c.drawCircle(mx, my, mr, p);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(1.8f));
                p.setColor(0x88140406);
                c.drawCircle(mx, my, mr * 0.62f, p);
                c.drawCircle(mx, my, mr * 0.34f, p);
                p.setStyle(Paint.Style.FILL);
                p.setColor(0xAA140406);
                c.drawCircle(mx, my, mr * 0.16f, p);
                for (int tm = 0; tm < 3; tm++) {
                    double ang = tm * Math.PI * 2.0 / 3.0 - Math.PI / 6.0;
                    float tx = mx + (float) Math.cos(ang) * (mr * 0.62f);
                    float ty = my + (float) Math.sin(ang) * (mr * 0.62f);
                    c.drawCircle(tx, ty, px(3.8f), p);
                }

                // Hidden Leaf Village (Konohagakure) & Hokage Rock Horizon Silhouette
                float groundY = h * 0.900f;
                p.setStyle(Paint.Style.FILL);
                p.setColor(0x88090306);
                Path stage = new Path();
                stage.moveTo(0, h);
                stage.lineTo(0, groundY);
                stage.lineTo(w * 0.66f, groundY);
                // Hokage Monument Mountain Cliffside on right (0.68..1.00)
                stage.lineTo(w * 0.70f, skyY(w, h, 0.83f));
                stage.lineTo(w * 0.74f, skyY(w, h, 0.75f)); // First Hokage Peak
                stage.lineTo(w * 0.77f, skyY(w, h, 0.77f)); // Second Hokage Peak
                stage.lineTo(w * 0.81f, skyY(w, h, 0.73f)); // Third Hokage Peak
                stage.lineTo(w * 0.85f, skyY(w, h, 0.71f)); // Fourth Hokage Peak
                stage.lineTo(w * 0.90f, skyY(w, h, 0.74f)); // Fifth/Sixth/Seventh Hokage Cliff
                stage.lineTo(w * 0.95f, skyY(w, h, 0.81f));
                stage.lineTo(w, groundY);
                stage.lineTo(w, h);
                stage.close();
                c.drawPath(stage, p);

                // Torii Gates on left & Hokage Mansion Pagoda on right
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(2.6f));
                p.setColor(0x52FF6830);
                for (int tg = 0; tg < 3; tg++) {
                    float gx = w * (0.06f + tg * 0.08f);
                    c.drawLine(gx, groundY, gx, groundY - px(38f), p);
                    c.drawLine(gx + px(28f), groundY, gx + px(28f), groundY - px(38f), p);
                    c.drawLine(gx - px(8f), groundY - px(38f), gx + px(36f), groundY - px(38f), p);
                    c.drawLine(gx - px(4f), groundY - px(28f), gx + px(32f), groundY - px(28f), p);
                }
                c.drawLine(0, groundY, w, groundY, p);
                // Sculpted Hokage face contour lines on the cliff
                p.setStrokeWidth(px(1.6f));
                p.setColor(0x40FF9858);
                for (int hf = 0; hf < 5; hf++) {
                    float fx = w * (0.73f + hf * 0.042f);
                    float fy = skyY(w, h, 0.78f - (hf % 2) * 0.02f);
                    c.drawLine(fx, fy - px(12f), fx + px(6f), fy - px(4f), p);
                    c.drawLine(fx + px(6f), fy - px(4f), fx + px(2f), fy + px(6f), p);
                }
            } else if (universeTheme == 2) {
                // DEMON SLAYER SKY: Wisteria Moon + Glowing Wisteria Trees (left) & Infinity Castle Pagoda (right)
                float mx = w * 0.85f, my = h * 0.16f, mr = px(36);
                p.setShader(new RadialGradient(mx, my, mr * 4.2f,
                        new int[] {0x42E0A8FF, 0x186830B8, 0x00000000}, new float[] {0f, 0.45f, 1f}, Shader.TileMode.CLAMP));
                c.drawCircle(mx, my, mr * 4.2f, p);
                p.setShader(null);
                p.setColor(0x38F4E0FF);
                c.drawCircle(mx, my, mr, p);

                float groundY = h * 0.900f;
                p.setStyle(Paint.Style.FILL);
                p.setColor(0x8806040D);
                Path stage = new Path();
                stage.moveTo(0, h);
                stage.lineTo(0, groundY);
                stage.lineTo(w * 0.68f, groundY);
                // Multi-tiered Infinity Castle / Butterfly Mansion Pagoda on right (0.70..0.96)
                stage.lineTo(w * 0.72f, skyY(w, h, 0.85f));
                stage.lineTo(w * 0.69f, skyY(w, h, 0.84f)); // Lower eaves left
                stage.lineTo(w * 0.75f, skyY(w, h, 0.79f));
                stage.lineTo(w * 0.71f, skyY(w, h, 0.78f)); // Mid eaves left
                stage.lineTo(w * 0.82f, skyY(w, h, 0.69f)); // Pagoda Spire Peak
                stage.lineTo(w * 0.93f, skyY(w, h, 0.78f)); // Mid eaves right
                stage.lineTo(w * 0.89f, skyY(w, h, 0.79f));
                stage.lineTo(w * 0.95f, skyY(w, h, 0.84f)); // Lower eaves right
                stage.lineTo(w * 0.92f, skyY(w, h, 0.85f));
                stage.lineTo(w, groundY);
                stage.lineTo(w, h);
                stage.close();
                c.drawPath(stage, p);

                // Hanging Glowing Wisteria Blossom Clusters on left (Mount Fujikasane)
                for (int wt = 0; wt < 4; wt++) {
                    float tx = w * (0.06f + wt * 0.07f);
                    p.setStyle(Paint.Style.STROKE);
                    p.setStrokeWidth(px(2.4f));
                    p.setColor(0x553A2048);
                    c.drawLine(tx, groundY, tx, groundY - px(46f), p);
                    p.setStyle(Paint.Style.FILL);
                    p.setColor(0x66C878FF);
                    for (int cl = -2; cl <= 2; cl++) {
                        c.drawOval(tx + cl * px(6f) - px(3f), groundY - px(52f), tx + cl * px(6f) + px(3f), groundY - px(28f - Math.abs(cl) * 4f), p);
                    }
                }
                // Warm Shoji Windows in the Pagoda
                p.setColor(0x78FFAE48);
                for (int pw = 0; pw < 4; pw++) {
                    float wx = w * (0.78f + pw * 0.022f);
                    c.drawRect(wx, skyY(w, h, 0.81f), wx + px(6f), skyY(w, h, 0.84f), p);
                }
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(1.6f));
                p.setColor(0x48B878FF);
                c.drawLine(0, groundY, w, groundY, p);
            } else if (universeTheme == 3) {
                // STAR WARS SKY: The Death Star in Upper Right + Imperial Star Destroyer & Coruscant/Jedi Temple Spires
                float mx = w * 0.85f, my = h * 0.16f, mr = px(40);
                p.setShader(new RadialGradient(mx, my, mr * 3.8f,
                        new int[] {0x3898B8D8, 0x14284878, 0x00000000}, new float[] {0f, 0.45f, 1f}, Shader.TileMode.CLAMP));
                c.drawCircle(mx, my, mr * 3.8f, p);
                p.setShader(null);
                p.setColor(0x48788C9E);
                c.drawCircle(mx, my, mr, p);
                // Death Star equatorial trench & superlaser dish
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(1.8f));
                p.setColor(0x88101824);
                c.drawLine(mx - mr, my + px(3f), mx + mr, my + px(3f), p);
                c.drawCircle(mx - mr * 0.35f, my - mr * 0.32f, mr * 0.28f, p);
                p.setStyle(Paint.Style.FILL);
                p.setColor(0x8838FF68);
                c.drawCircle(mx - mr * 0.35f, my - mr * 0.32f, px(2.5f), p);

                float groundY = h * 0.900f;
                p.setColor(0x8C04060E);
                Path stage = new Path();
                stage.moveTo(0, h);
                stage.lineTo(0, groundY);
                stage.lineTo(w * 0.68f, groundY);
                // Jedi Temple / Imperial Citadel Spires on right (0.70..0.96)
                stage.lineTo(w * 0.72f, skyY(w, h, 0.75f));
                stage.lineTo(w * 0.74f, skyY(w, h, 0.86f));
                stage.lineTo(w * 0.80f, skyY(w, h, 0.86f));
                stage.lineTo(w * 0.82f, skyY(w, h, 0.68f)); // Central Ziggurat Spire
                stage.lineTo(w * 0.84f, skyY(w, h, 0.86f));
                stage.lineTo(w * 0.90f, skyY(w, h, 0.86f));
                stage.lineTo(w * 0.92f, skyY(w, h, 0.75f));
                stage.lineTo(w * 0.94f, groundY);
                stage.lineTo(w, groundY);
                stage.lineTo(w, h);
                stage.close();
                c.drawPath(stage, p);

                // Hovering Wedge-Shaped Imperial Star Destroyer Silhouette over right horizon
                Path isd = new Path();
                isd.moveTo(w * 0.66f, skyY(w, h, 0.66f));
                isd.lineTo(w * 0.80f, skyY(w, h, 0.63f));
                isd.lineTo(w * 0.78f, skyY(w, h, 0.69f));
                isd.close();
                p.setColor(0x661C2838);
                c.drawPath(isd, p);

                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(1.6f));
                p.setColor(0x4848A8FF);
                c.drawLine(0, groundY, w, groundY, p);
            } else if (universeTheme == 4) {
                // MCU SKY: Doctor Strange Sling-Ring Portal / Sanctum Window in Upper Right + Avengers Tower Skyline
                float mx = w * 0.85f, my = h * 0.16f, mr = px(38);
                p.setShader(new RadialGradient(mx, my, mr * 4.0f,
                        new int[] {0x44FF9828, 0x18983010, 0x00000000}, new float[] {0f, 0.45f, 1f}, Shader.TileMode.CLAMP));
                c.drawCircle(mx, my, mr * 4.0f, p);
                p.setShader(null);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(2.4f));
                p.setColor(0x88FFB848);
                c.drawCircle(mx, my, mr, p);
                c.drawRect(mx - mr * 0.68f, my - mr * 0.68f, mx + mr * 0.68f, my + mr * 0.68f, p);

                float groundY = h * 0.900f;
                p.setStyle(Paint.Style.FILL);
                p.setColor(0x8C060510);
                Path stage = new Path();
                stage.moveTo(0, h);
                stage.lineTo(0, groundY);
                stage.lineTo(w * 0.68f, groundY);
                // Avengers / Stark Tower Skyline on right (0.70..0.96)
                stage.lineTo(w * 0.72f, skyY(w, h, 0.79f));
                stage.lineTo(w * 0.76f, skyY(w, h, 0.79f));
                stage.lineTo(w * 0.76f, groundY);
                stage.lineTo(w * 0.79f, groundY);
                stage.lineTo(w * 0.80f, skyY(w, h, 0.67f)); // Slanted Avengers Tower Roof
                stage.lineTo(w * 0.87f, skyY(w, h, 0.72f)); // Stark Landing Pad Ledge
                stage.lineTo(w * 0.84f, skyY(w, h, 0.75f));
                stage.lineTo(w * 0.84f, groundY);
                stage.lineTo(w * 0.89f, skyY(w, h, 0.77f));
                stage.lineTo(w * 0.94f, skyY(w, h, 0.77f));
                stage.lineTo(w * 0.94f, groundY);
                stage.lineTo(w, groundY);
                stage.lineTo(w, h);
                stage.close();
                c.drawPath(stage, p);

                // Glowing Cyan Arc-Reactor ‘A’ Emblem on Avengers Tower
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(2.0f));
                p.setColor(0x8848E8FF);
                c.drawCircle(w * 0.82f, skyY(w, h, 0.73f), px(9f), p);
                c.drawLine(0, groundY, w, groundY, p);
            } else if (universeTheme == 5) {
                // DCU SKY: The Bat-Signal Beam in the Upper-Right Gotham Clouds + Wayne Tower & Daily Planet Skyline
                float mx = w * 0.85f, my = h * 0.16f, mr = px(40);
                // Diagonal searchlight cone from Wayne Tower up to the Bat-Signal
                p.setStyle(Paint.Style.FILL);
                p.setColor(0x18FFE888);
                Path cone = new Path();
                cone.moveTo(w * 0.76f, skyY(w, h, 0.72f));
                cone.lineTo(mx - mr * 0.9f, my + mr * 0.3f);
                cone.lineTo(mx + mr * 0.9f, my - mr * 0.3f);
                cone.close();
                c.drawPath(cone, p);

                p.setShader(new RadialGradient(mx, my, mr * 3.8f,
                        new int[] {0x48FFE888, 0x18886820, 0x00000000}, new float[] {0f, 0.45f, 1f}, Shader.TileMode.CLAMP));
                c.drawCircle(mx, my, mr * 3.8f, p);
                p.setShader(null);
                p.setColor(0x52FFEA98);
                c.drawOval(rectF(mx - mr * 1.15f, my - mr * 0.78f, mx + mr * 1.15f, my + mr * 0.78f), p);
                // Dark Bat-Emblem inside the Bat-Signal spotlight
                p.setColor(0xCC060914);
                Path bat = new Path();
                bat.moveTo(mx, my - mr * 0.38f);
                bat.lineTo(mx + mr * 0.18f, my - mr * 0.18f);
                bat.lineTo(mx + mr * 0.82f, my - mr * 0.28f);
                bat.lineTo(mx + mr * 0.48f, my + mr * 0.25f);
                bat.lineTo(mx, my + mr * 0.45f);
                bat.lineTo(mx - mr * 0.48f, my + mr * 0.25f);
                bat.lineTo(mx - mr * 0.82f, my - mr * 0.28f);
                bat.lineTo(mx - mr * 0.18f, my - mr * 0.18f);
                bat.close();
                c.drawPath(bat, p);

                float groundY = h * 0.900f;
                p.setColor(0x9004060E);
                Path stage = new Path();
                stage.moveTo(0, h);
                stage.lineTo(0, groundY);
                stage.lineTo(w * 0.68f, groundY);
                // Gotham Wayne Tower & Metropolis Daily Planet Skyline on right (0.70..0.96)
                stage.lineTo(w * 0.70f, skyY(w, h, 0.74f));
                stage.lineTo(w * 0.73f, skyY(w, h, 0.67f)); // Wayne Tower Deco Spire
                stage.lineTo(w * 0.76f, skyY(w, h, 0.74f));
                stage.lineTo(w * 0.76f, groundY);
                stage.lineTo(w * 0.81f, skyY(w, h, 0.76f));
                stage.lineTo(w * 0.87f, skyY(w, h, 0.76f)); // Daily Planet Roof
                stage.lineTo(w * 0.87f, groundY);
                stage.lineTo(w * 0.91f, skyY(w, h, 0.79f));
                stage.lineTo(w * 0.95f, skyY(w, h, 0.79f));
                stage.lineTo(w * 0.95f, groundY);
                stage.lineTo(w, groundY);
                stage.lineTo(w, h);
                stage.close();
                c.drawPath(stage, p);

                // Golden Daily Planet Globe atop building at 0.84w
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(1.8f));
                p.setColor(0x88FFE078);
                c.drawCircle(w * 0.84f, skyY(w, h, 0.72f), px(11f), p);
                c.drawLine(w * 0.82f, skyY(w, h, 0.72f), w * 0.86f, skyY(w, h, 0.72f), p);
                c.drawLine(0, groundY, w, groundY, p);
            } else if (universeTheme == 6) {
                // FORMULA 1 SKY: Grand Prix Stadium Floodlight Towers, Overhead Light Banks & Tiered Grandstand Tribune
                float[] towerX = {w * 0.44f, w * 0.62f, w * 0.78f, w * 0.91f};
                float[] towerY = {h * 0.12f, h * 0.09f, h * 0.13f, h * 0.17f};
                for (int tw = 0; tw < towerX.length; tw++) {
                    float tx = towerX[tw], ty = towerY[tw];
                    // Volumetric downward stadium floodlight beam cone
                    p.setStyle(Paint.Style.FILL);
                    p.setShader(new RadialGradient(tx, ty, px(240f),
                            new int[] {0x42E8F8FF, 0x1888D4FF, 0x00000000}, new float[] {0f, 0.45f, 1f}, Shader.TileMode.CLAMP));
                    c.drawCircle(tx, ty, px(240f), p);
                    p.setShader(null);
                    p.setColor(0x14D8F2FF);
                    Path beam = new Path();
                    beam.moveTo(tx - px(26f), ty + px(6f));
                    beam.lineTo(tx + px(26f), ty + px(6f));
                    beam.lineTo(tx + px(110f), ty + px(240f));
                    beam.lineTo(tx - px(140f), ty + px(240f));
                    beam.close();
                    c.drawPath(beam, p);

                    // Cantilever Stadium Floodlight Bank Frame (2x4 LED Halogen Array)
                    p.setColor(0xDD141A26);
                    c.drawRoundRect(tx - px(28f), ty - px(11f), tx + px(28f), ty + px(11f), px(4f), px(4f), p);
                    p.setStyle(Paint.Style.STROKE);
                    p.setStrokeWidth(px(1.6f));
                    p.setColor(0x88587090);
                    c.drawRoundRect(tx - px(28f), ty - px(11f), tx + px(28f), ty + px(11f), px(4f), px(4f), p);
                    p.setStyle(Paint.Style.FILL);
                    for (int row = 0; row < 2; row++) {
                        for (int col = 0; col < 4; col++) {
                            float lx = tx + (col - 1.5f) * px(12f);
                            float ly = ty + (row - 0.5f) * px(8f);
                            p.setColor(0xFF98E4FF);
                            c.drawCircle(lx, ly, px(3.4f), p);
                            p.setColor(0xFFFFFFFF);
                            c.drawCircle(lx, ly, px(1.8f), p);
                        }
                    }
                }

                float groundY = h * 0.900f;
                p.setStyle(Paint.Style.FILL);
                p.setColor(0x92060912);
                Path stage = new Path();
                stage.moveTo(0, h);
                stage.lineTo(0, groundY);
                stage.lineTo(w * 0.66f, groundY);
                // Tiered Grandstand Tribune & Race Control Tower on right (0.66..0.98)
                stage.lineTo(w * 0.69f, skyY(w, h, 0.77f)); // Grandstand lower tier
                stage.lineTo(w * 0.79f, skyY(w, h, 0.72f)); // Grandstand upper canopy roof
                stage.lineTo(w * 0.77f, groundY);
                stage.lineTo(w * 0.81f, groundY);
                stage.lineTo(w * 0.82f, skyY(w, h, 0.66f)); // Race Control & Timing Tower
                stage.lineTo(w * 0.86f, skyY(w, h, 0.68f));
                stage.lineTo(w * 0.85f, groundY);
                stage.lineTo(w * 0.88f, skyY(w, h, 0.74f)); // Second Stadium Grandstand
                stage.lineTo(w * 0.97f, skyY(w, h, 0.71f));
                stage.lineTo(w * 0.96f, groundY);
                stage.lineTo(w, groundY);
                stage.lineTo(w, h);
                stage.close();
                c.drawPath(stage, p);

                // Stadium Floodlight Pylon Trusses rising from the Grandstands + Lit Grandstand Tiers
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(2.0f));
                p.setColor(0x6688B8E8);
                c.drawLine(w * 0.74f, skyY(w, h, 0.74f), w * 0.74f, skyY(w, h, 0.62f), p);
                c.drawLine(w * 0.92f, skyY(w, h, 0.72f), w * 0.92f, skyY(w, h, 0.60f), p);
                p.setStyle(Paint.Style.FILL);
                p.setColor(0xCCF0FAFF);
                c.drawRoundRect(w * 0.71f, skyY(w, h, 0.60f), w * 0.77f, skyY(w, h, 0.63f), px(2f), px(2f), p);
                c.drawRoundRect(w * 0.89f, skyY(w, h, 0.58f), w * 0.95f, skyY(w, h, 0.61f), px(2f), px(2f), p);

                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(1.8f));
                p.setColor(0x88FF1801);
                c.drawLine(0, groundY, w, groundY, p);
            } else if (universeTheme == 7) {
                // CYBERPUNK 2099 SKY: Night City Megabuilding Skyline, Arasaka Holo-Rings & Kiroshi Sky Projections
                float mx = w * 0.85f, my = h * 0.16f, mr = px(40);
                // Holographic Magenta/Cyan Orbital Air & Arasaka Sky Ring in Upper Right
                p.setShader(new RadialGradient(mx, my, mr * 4.2f,
                        new int[] {0x48FF0055, 0x2400F0FF, 0x00000000}, new float[] {0f, 0.48f, 1f}, Shader.TileMode.CLAMP));
                c.drawCircle(mx, my, mr * 4.2f, p);
                p.setShader(null);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(2.4f));
                p.setColor(0xCC00F0FF);
                c.drawCircle(mx, my, mr, p);
                p.setStrokeWidth(px(1.6f));
                p.setColor(0xEEFF0055);
                c.drawCircle(mx, my, mr * 0.68f, p);
                // Arasaka 3-Lobe Corporate Holo-Crest inside the sky ring
                p.setStyle(Paint.Style.FILL);
                p.setColor(0xDDFF003C);
                c.drawCircle(mx, my - mr * 0.25f, px(5.5f), p);
                c.drawCircle(mx - mr * 0.22f, my + mr * 0.16f, px(5.5f), p);
                c.drawCircle(mx + mr * 0.22f, my + mr * 0.16f, px(5.5f), p);

                // Vertical Cyan & Magenta Kiroshi Advertising Sky-Beams rising from Corpo Plaza
                p.setColor(0x1400F0FF);
                c.drawRect(w * 0.72f, skyY(w, h, 0.45f), w * 0.74f, skyY(w, h, 0.90f), p);
                p.setColor(0x14FF0055);
                c.drawRect(w * 0.88f, skyY(w, h, 0.42f), w * 0.90f, skyY(w, h, 0.90f), p);

                float groundY = h * 0.900f;
                p.setColor(0x9405040E);
                Path stage = new Path();
                stage.moveTo(0, h);
                stage.lineTo(0, groundY);
                stage.lineTo(w * 0.65f, groundY);
                // Night City Megabuilding H10 & Arasaka Tower Brutalist Skyline on right (0.65..0.98)
                stage.lineTo(w * 0.68f, skyY(w, h, 0.73f)); // Megabuilding H10 stepped block
                stage.lineTo(w * 0.75f, skyY(w, h, 0.73f));
                stage.lineTo(w * 0.75f, groundY);
                stage.lineTo(w * 0.78f, groundY);
                stage.lineTo(w * 0.79f, skyY(w, h, 0.63f)); // Tall Arasaka Corporate Tower
                stage.lineTo(w * 0.86f, skyY(w, h, 0.65f));
                stage.lineTo(w * 0.86f, groundY);
                stage.lineTo(w * 0.89f, skyY(w, h, 0.71f)); // Konpeki / Biotechnica Tower
                stage.lineTo(w * 0.96f, skyY(w, h, 0.71f));
                stage.lineTo(w * 0.96f, groundY);
                stage.lineTo(w, groundY);
                stage.lineTo(w, h);
                stage.close();
                c.drawPath(stage, p);

                // Neon Cyan, Magenta & Cyber-Yellow Window Strips on the Megabuildings
                int[] neonWinCols = {0xCC00F0FF, 0xCCFF0055, 0xCCFCEE09};
                for (int wy = 0; wy < 6; wy++) {
                    p.setColor(neonWinCols[wy % 3]);
                    float yPos = skyY(w, h, 0.67f + wy * 0.035f);
                    c.drawRect(w * 0.80f, yPos, w * 0.85f, yPos + px(2.4f), p);
                    if (wy >= 2) {
                        c.drawRect(w * 0.695f, yPos, w * 0.735f, yPos + px(2.2f), p);
                        c.drawRect(w * 0.905f, yPos, w * 0.945f, yPos + px(2.2f), p);
                    }
                }

                // Neon Wet-Asphalt Horizon Line
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(px(2.0f));
                p.setColor(0x9900F0FF);
                c.drawLine(0, groundY, w, groundY, p);
            } else {
            // Full Moon in upper right sky
            float mx = w * 0.85f, my = h * 0.16f, mr = px(34);
            p.setShader(new RadialGradient(mx, my, mr * 4.0f,
                    new int[] {0x2AFFFAEA, 0x10D8E4FF, 0x00000000}, new float[] {0f, 0.4f, 1f}, Shader.TileMode.CLAMP));
            c.drawCircle(mx, my, mr * 4.0f, p);
            p.setShader(null);
            p.setColor(0x22FFF8DC);
            c.drawCircle(mx, my, mr, p);

            // Subtle constellation lines
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(px(1.0f));
            p.setColor(0x16FFE8A0);
            float[][] constPts = {
                {0.30f, 0.08f, 0.35f, 0.12f}, {0.35f, 0.12f, 0.42f, 0.10f}, {0.42f, 0.10f, 0.46f, 0.14f},
                {0.56f, 0.07f, 0.61f, 0.11f}, {0.61f, 0.11f, 0.65f, 0.08f}
            };
            for (float[] ln : constPts) {
                c.drawLine(ln[0] * w, ln[1] * h, ln[2] * w, ln[3] * h, p);
            }

            // Expanded Panoramic Bottom Stage Silhouette (y = 690..1080):
            // Glenfinnan Viaduct (left), Black Lake (center), and taller Hogwarts Castle Spires (right)
            p.setStyle(Paint.Style.FILL);
            p.setColor(0x80050711);
            Path stage = new Path();
            float groundY = h * 0.900f;
            stage.moveTo(0, h);
            stage.lineTo(0, groundY);
            stage.lineTo(w * 0.34f, groundY);
            stage.lineTo(w * 0.38f, skyY(w, h, 0.922f));
            stage.lineTo(w * 0.66f, skyY(w, h, 0.922f));
            // Taller Hogwarts Castle silhouette on far right (x = 0.68..0.98)
            stage.lineTo(w * 0.70f, skyY(w, h, 0.86f));
            stage.lineTo(w * 0.715f, skyY(w, h, 0.76f)); // West Turret
            stage.lineTo(w * 0.73f, skyY(w, h, 0.86f));
            stage.lineTo(w * 0.755f, skyY(w, h, 0.85f));
            stage.lineTo(w * 0.772f, skyY(w, h, 0.68f)); // Tall Astronomy Tower Peak
            stage.lineTo(w * 0.79f, skyY(w, h, 0.85f));
            stage.lineTo(w * 0.812f, skyY(w, h, 0.82f));
            stage.lineTo(w * 0.826f, skyY(w, h, 0.73f)); // Grand Staircase Tower
            stage.lineTo(w * 0.84f, skyY(w, h, 0.83f));
            stage.lineTo(w * 0.88f, skyY(w, h, 0.83f));  // Great Hall Crest
            stage.lineTo(w * 0.896f, skyY(w, h, 0.75f)); // Bell Tower
            stage.lineTo(w * 0.912f, skyY(w, h, 0.86f));
            stage.lineTo(w * 0.95f, skyY(w, h, 0.89f));
            stage.lineTo(w, groundY);
            stage.lineTo(w, h);
            stage.close();
            c.drawPath(stage, p);

            // Golden horizon rim line along the stage
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(px(1.5f));
            p.setColor(0x42F5D078);
            c.drawLine(0, groundY, w * 0.34f, groundY, p);
            c.drawLine(w * 0.34f, groundY, w * 0.66f, groundY, p);

            // Glenfinnan Viaduct arches under left track
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(px(2.2f));
            p.setColor(0x36F5D078);
            for (int a = 0; a < 7; a++) {
                float ax = w * (0.035f + a * 0.043f);
                c.drawArc(rectF(ax, groundY, ax + w * 0.035f, groundY + px(52)), 180, 180, false, p);
            }

            // Lit Hogwarts Castle windows
            p.setStyle(Paint.Style.FILL);
            p.setColor(0x74FFD268);
            float[][] winPts = {
                {0.770f, 0.73f}, {0.773f, 0.76f}, {0.770f, 0.79f}, {0.773f, 0.82f},
                {0.824f, 0.77f}, {0.827f, 0.80f},
                {0.846f, 0.85f}, {0.854f, 0.85f}, {0.862f, 0.85f}, {0.870f, 0.85f},
                {0.894f, 0.79f}, {0.894f, 0.82f}
            };
            for (float[] wp : winPts) {
                float wy = skyY(w, h, wp[1]);
                c.drawRoundRect(wp[0] * w, wy, wp[0] * w + px(3.5f), wy + px(7.5f), px(1.5f), px(1.5f), p);
            }
            }

            if (android.os.Build.VERSION.SDK_INT >= 26) {
                Bitmap hw = bm.copy(Bitmap.Config.HARDWARE, false);
                if (hw != null) {
                    bm.recycle();
                    bm = hw;
                }
            }
            baseSky = bm;
        } catch (Throwable ignored) {}
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        ensureBaseSky(w, h);
        if (baseSky != null) {
            canvas.drawBitmap(baseSky, 0, 0, bitmapPaint);
        }

        long nowNanos = SystemClock.elapsedRealtimeNanos();
        if (lastFrameNanos > 0L) {
            float rawDt = (nowNanos - lastFrameNanos) * 1e-9f;
            frameDt = Math.max(0.002f, Math.min(0.05f, rawDt));
        } else {
            frameDt = 1f / 60f;
        }
        lastFrameNanos = nowNanos;
        float t = (nowNanos % 3_600_000_000_000L) * 1e-9f;
        float targetZoom = screensaverMode ? 1f : 0f;
        screensaverZoom += (targetZoom - screensaverZoom) * dampFactor(0.08f, frameDt);
        if (Math.abs(targetZoom - screensaverZoom) < 0.002f) screensaverZoom = targetZoom;
        if (screensaverMode) {
            float storyT = t * 0.5f;
            int slot = (int) (storyT / CHAPTER_SEC);
            if (slot != lastScreensaverSlot) {
                advanceRandomScreensaverScene(slot);
                ensureBaseSky(w, h);
            }
        }

        if (prismMode) {
            targetR = 155f + 95f * (float) Math.sin(t * 1.4f);
            targetG = 145f + 95f * (float) Math.sin(t * 1.4f + 2.09f);
            targetB = 165f + 85f * (float) Math.sin(t * 1.4f + 4.18f);
        }
        float colDamp = dampFactor(0.14f, frameDt);
        curR += (targetR - curR) * colDamp;
        curG += (targetG - curG) * colDamp;
        curB += (targetB - curB) * colDamp;
        if (fancyPulseEnergy > 0) fancyPulseEnergy = Math.max(0, fancyPulseEnergy - 2);

        int ir = Math.min(255, Math.max(0, Math.round(curR)));
        int ig = Math.min(255, Math.max(0, Math.round(curG)));
        int ib = Math.min(255, Math.max(0, Math.round(curB)));
        int rgbOnly = (ir << 16) | (ig << 8) | ib;

        // 0. FancyLEDs Reactive Ambilight Perimeter & Sky Aura (Zero-Allocation Cached Shaders!)
        if (edgeGlowEnabled) {
            float breathe = 0.78f + 0.22f * (float) Math.sin(t * 2.1f) + (fancyPulseEnergy / 280f);
            int edgeAlpha = Math.min(115, Math.max(32, Math.round(64f * breathe)));
            float borderDepth = px(92f);

            if (rgbOnly != cachedShaderRgb || w != cachedShaderW || h != cachedShaderH) {
                cachedShaderRgb = rgbOnly;
                cachedShaderW = w;
                cachedShaderH = h;
                int fullAmb = (0xFF << 24) | rgbOnly;
                topEdgeShader = new LinearGradient(0, 0, 0, borderDepth, fullAmb, 0x00000000, Shader.TileMode.CLAMP);
                botEdgeShader = new LinearGradient(0, h, 0, h - borderDepth, fullAmb, 0x00000000, Shader.TileMode.CLAMP);
                leftEdgeShader = new LinearGradient(0, 0, borderDepth, 0, fullAmb, 0x00000000, Shader.TileMode.CLAMP);
                rightEdgeShader = new LinearGradient(w, 0, w - borderDepth, 0, fullAmb, 0x00000000, Shader.TileMode.CLAMP);
            }
            edgePaint.setAlpha(edgeAlpha);
            edgePaint.setShader(topEdgeShader);
            canvas.drawRect(0, 0, w, borderDepth, edgePaint);
            edgePaint.setShader(botEdgeShader);
            canvas.drawRect(0, h - borderDepth, w, h, edgePaint);
            edgePaint.setShader(leftEdgeShader);
            canvas.drawRect(0, 0, borderDepth, h, edgePaint);
            edgePaint.setShader(rightEdgeShader);
            canvas.drawRect(w - borderDepth, 0, w, h, edgePaint);
            edgePaint.setShader(null);
            edgePaint.setAlpha(255);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.5f));
            int rimAlpha = Math.min(210, Math.max(70, Math.round(135f * breathe)));
            paint.setColor((rimAlpha << 24) | rgbOnly);
            canvas.drawRect(px(1f), px(1f), w - px(1f), h - px(1f), paint);
        }

        // 1. Twinkling ceiling stars / theme-specific atmospheric sky glints (Camera Flashes in F1, Chakra Embers in Naruto, etc.)
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < STAR_COUNT; i++) {
            int b = i * 6;
            float sx = stars[b], sy = stars[b + 1], sr = stars[b + 2];
            if (universeTheme == 6) {
                // FORMULA 1: High-Speed Xenon Camera Flashes popping across the stadium & grandstands
                float flashWave = (float) Math.sin(t * (stars[b + 3] * 2.6f) + stars[b + 4] * 3.7f);
                if (flashWave > 0.72f) {
                    float fl = (flashWave - 0.72f) / 0.28f;
                    int flAlpha = Math.min(255, Math.round(fl * fl * 255f));
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((flAlpha / 3 << 24) | 0x00A8ECFF);
                    canvas.drawCircle(sx, sy, sr * (4.5f + fl * 3.5f), paint);
                    paint.setColor((flAlpha << 24) | 0x00FFFFFF);
                    canvas.drawCircle(sx, sy, sr * (1.4f + fl * 1.2f), paint);
                    paint.setStrokeWidth(px(1.3f));
                    float ray = sr * (4.2f + fl * 5.0f);
                    canvas.drawLine(sx - ray, sy, sx + ray, sy, paint);
                    canvas.drawLine(sx, sy - ray, sx, sy + ray, paint);
                    float dRay = ray * 0.58f;
                    canvas.drawLine(sx - dRay, sy - dRay, sx + dRay, sy + dRay, paint);
                    canvas.drawLine(sx - dRay, sy + dRay, sx + dRay, sy - dRay, paint);
                } else {
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0x28D8ECFF);
                    canvas.drawCircle(sx, sy, sr * 0.75f, paint);
                }
                continue;
            }
            float pulse = 0.55f + 0.45f * (float) Math.sin(t * stars[b + 3] + stars[b + 4]);
            int alpha = Math.min(255, Math.max(30, Math.round(pulse * 220f)));
            int starRgb = 0x00FDF4DC;
            int glintRgb = 0x00FFE8A8;
            if (universeTheme == 1) {
                starRgb = (i % 3 == 0) ? 0x00FF5828 : 0x00FFD098;
                glintRgb = 0x00FF7830;
            } else if (universeTheme == 2) {
                starRgb = (i % 3 == 0) ? 0x00E098FF : (i % 3 == 1 ? 0x0068F0FF : 0x00FFE4B8);
                glintRgb = 0x00D888FF;
            } else if (universeTheme == 3) {
                starRgb = (i % 3 == 0) ? 0x0098E0FF : (i % 3 == 1 ? 0x00FFFFFF : 0x00FFD8A8);
                glintRgb = 0x0068C8FF;
            } else if (universeTheme == 4) {
                int[] mcuStars = {0x0058C8FF, 0x00FFE058, 0x00FF5868, 0x00C868FF, 0x0048FF98, 0x00FF9840};
                starRgb = mcuStars[i % 6];
                glintRgb = starRgb;
            } else if (universeTheme == 5) {
                starRgb = (i % 3 == 0) ? 0x0048FF90 : (i % 3 == 1 ? 0x0078E0FF : 0x00FFE478);
                glintRgb = starRgb;
            } else if (universeTheme == 7) {
                starRgb = (i % 3 == 0) ? 0x0000F0FF : (i % 3 == 1 ? 0x00FF0055 : 0x00FCEE09);
                glintRgb = starRgb;
            }
            paint.setColor((alpha << 24) | starRgb);
            canvas.drawCircle(sx, sy, sr, paint);
            if (stars[b + 5] > 0.5f) {
                paint.setStrokeWidth(px(1.1f));
                int glintAlpha = alpha * 2 / 3;
                paint.setColor((glintAlpha << 24) | glintRgb);
                float len = sr * (2.6f + pulse * 1.4f);
                canvas.drawLine(sx - len, sy, sx + len, sy, paint);
                canvas.drawLine(sx, sy - len, sx, sy + len, paint);
            }
        }

        // 2. Drifting Lumos Magic Dust
        int sparkTint = ((Math.min(255, (ir + 255 * 2) / 3)) << 16)
                | ((Math.min(255, (ig + 216 * 2) / 3)) << 8)
                | (Math.min(255, (ib + 107 * 2) / 3));
        for (int i = 0; i < SPARK_COUNT; i++) {
            int b = i * 6;
            float pxPos = (sparks[b] + t * sparks[b + 2] + (float) Math.sin(t * 0.8f + sparks[b + 5]) * px(18f)) % w;
            if (pxPos < 0) pxPos += w;
            float pyPos = (sparks[b + 1] + t * sparks[b + 3]) % h;
            if (pyPos < 0) pyPos += h;
            float glow = 0.4f + 0.6f * (float) Math.sin(t * 2.2f + sparks[b + 5]);
            int alpha = Math.min(220, Math.max(20, Math.round(glow * 175f)));
            paint.setColor((alpha << 24) | (i % 3 == 0 ? rgbOnly : sparkTint));
            canvas.drawCircle(pxPos, pyPos, sparks[b + 4], paint);
        }

        // 3A. Flying Translucent Hogwarts Ghosts / Theme Sky Flyers gliding among the top lights
        drawFlyingGhosts(canvas, w, h, t);

        // 3B. Theme-Specific Iconic Floating Sky Lights (18 Fixtures)
        float glowHalf = glowSprite != null ? glowSprite.getWidth() / 2f : 0f;
        for (int i = 0; i < CANDLE_COUNT; i++) {
            int b = i * 7;
            float cx = candles[b];
            float bob = (float) Math.sin(t * candles[b + 4] + candles[b + 5]) * px(7.0f);
            float cy = candles[b + 1] + bob;
            float cw = candles[b + 2];
            float ch = candles[b + 3];

            float flicker = 0.88f + 0.12f * (float) Math.sin(t * 6.5f + candles[b + 6])
                    + 0.06f * (float) Math.cos(t * 11.3f + candles[b + 5]);
            float flameTipY = cy - px(11f) * flicker;
            // Only apply the warm Hogwarts candle glowSprite in the Harry Potter theme (universeTheme == 0)
            if (glowSprite != null && universeTheme == 0) {
                bitmapPaint.setAlpha(Math.min(255, Math.max(120, Math.round(190f * flicker))));
                canvas.drawBitmap(glowSprite, cx - glowHalf, cy - px(5f) - glowHalf, bitmapPaint);
            }

            if (universeTheme == 1) {
                // NARUTO Top Sky: Traditional Konoha Festival Chōchin Paper Lanterns with Bamboo Ribs, Uzumaki Crest & Silk Tassel
                float lTop = cy - px(2f);
                float lBot = cy + ch * 0.72f;
                float lMidY = (lTop + lBot) * 0.5f;
                float lRx = cw * 1.35f;
                // Warm Will-of-Fire outer chakra halo
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((0x34 << 24) | 0x00FF5820);
                canvas.drawCircle(cx, lMidY, cw * 3.0f * flicker, paint);
                // Top hanging cord
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(1.2f));
                paint.setColor(0xAAFFE098);
                canvas.drawLine(cx, lTop - px(6f), cx, lTop, paint);
                // Plump Scarlet-Vermilion Chōchin Paper Body + Warm Golden Inner Flame Core
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0xE6E02418);
                canvas.drawOval(rectF(cx - lRx, lTop, cx + lRx, lBot), paint);
                paint.setColor(0xEEFFB838);
                canvas.drawOval(rectF(cx - lRx * 0.62f, lTop + ch * 0.14f, cx + lRx * 0.62f, lBot - ch * 0.14f), paint);
                paint.setColor(0xFFFFF4C8);
                canvas.drawCircle(cx, lMidY, cw * 0.36f * flicker, paint);
                // Curved horizontal bamboo rib lines + central Uzumaki spiral emblem
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(1.0f));
                paint.setColor(0x88480C08);
                canvas.drawLine(cx - lRx * 0.92f, lMidY - ch * 0.14f, cx + lRx * 0.92f, lMidY - ch * 0.14f, paint);
                canvas.drawLine(cx - lRx * 0.92f, lMidY + ch * 0.14f, cx + lRx * 0.92f, lMidY + ch * 0.14f, paint);
                paint.setColor(0xCC98140C);
                canvas.drawCircle(cx, lMidY, cw * 0.34f, paint);
                // Lacquered Black & Gold Wooden Top/Bottom Caps (Ukebachi)
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0xFF1C100C);
                canvas.drawRoundRect(cx - cw * 0.82f, lTop - px(2.2f), cx + cw * 0.82f, lTop + px(2.5f), px(1.5f), px(1.5f), paint);
                canvas.drawRoundRect(cx - cw * 0.82f, lBot - px(2.2f), cx + cw * 0.82f, lBot + px(2.5f), px(1.5f), px(1.5f), paint);
                // Swaying Golden Silk Tassel hanging below the lantern
                float tasselSway = (float) Math.sin(t * 4.2f + i) * px(2.8f);
                paint.setColor(0xFFFFD048);
                canvas.drawCircle(cx, lBot + px(4.0f), px(1.8f), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(1.5f));
                canvas.drawLine(cx, lBot + px(4.0f), cx + tasselSway, lBot + px(13f), paint);
                paint.setStyle(Paint.Style.FILL);
                continue;
            } else if (universeTheme == 2) {
                // DEMON SLAYER Top Sky: Alternating Taisho Roofed Chōchin Lanterns & Cascading Wisteria Blossom Vines
                if (i % 2 == 0) {
                    // Type A: Taisho-Era Roofed Paper Lantern (Asakusa / Butterfly Mansion Style)
                    float lTop = cy;
                    float lBot = cy + ch * 0.70f;
                    float lMidY = (lTop + lBot) * 0.5f;
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x32 << 24) | 0x00D878FF);
                    canvas.drawCircle(cx, lMidY, cw * 2.9f * flicker, paint);
                    // Glowing Washi Paper Body (Violet-Rose outer -> Warm Peach-Gold inner flame)
                    paint.setColor(0xD89838D8);
                    canvas.drawRoundRect(cx - cw * 1.15f, lTop, cx + cw * 1.15f, lBot, cw * 0.65f, cw * 0.65f, paint);
                    paint.setColor(0xEEFFD898);
                    canvas.drawOval(rectF(cx - cw * 0.65f, lTop + ch * 0.14f, cx + cw * 0.65f, lBot - ch * 0.14f), paint);
                    // Horizontal bamboo ribs
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.0f));
                    paint.setColor(0x77301048);
                    canvas.drawLine(cx - cw * 1.08f, lMidY - ch * 0.12f, cx + cw * 1.08f, lMidY - ch * 0.12f, paint);
                    canvas.drawLine(cx - cw * 1.08f, lMidY + ch * 0.12f, cx + cw * 1.08f, lMidY + ch * 0.12f, paint);
                    // Pitched Japanese Pagoda Roof Eaves (Kasagi) on top of the lantern + base cap + tassel
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFF1A1026);
                    flamePath.reset();
                    flamePath.moveTo(cx - cw * 1.55f, lTop + px(2f));
                    flamePath.lineTo(cx, lTop - px(5.5f));
                    flamePath.lineTo(cx + cw * 1.55f, lTop + px(2f));
                    flamePath.close();
                    canvas.drawPath(flamePath, paint);
                    canvas.drawRect(cx - cw * 0.85f, lBot - px(1.5f), cx + cw * 0.85f, lBot + px(2.5f), paint);
                    float tSway = (float) Math.sin(t * 3.8f + i) * px(2.5f);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.4f));
                    paint.setColor(0xFFE098FF);
                    canvas.drawLine(cx, lBot + px(2.5f), cx + tSway, lBot + px(11f), paint);
                    paint.setStyle(Paint.Style.FILL);
                } else {
                    // Type B: Illuminated Cascading Wisteria (Fuji) Blossom Raceme Cluster (3 Hanging Petal Vines)
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x34 << 24) | 0x00C868FF);
                    canvas.drawCircle(cx, cy + ch * 0.42f, cw * 3.0f * flicker, paint);
                    float vineSway = (float) Math.sin(t * 3.2f + i) * px(2.5f);
                    for (int st = -1; st <= 1; st++) {
                        float vx = cx + st * cw * 0.82f;
                        int petals = (st == 0) ? 5 : 4;
                        for (int pIdx = 0; pIdx < petals; pIdx++) {
                            float py = cy + pIdx * (ch * 0.17f);
                            float pRad = cw * (0.48f - pIdx * 0.065f);
                            float pxOff = vineSway * (pIdx * 0.25f);
                            paint.setColor(pIdx < 2 ? 0xEEB858FF : (pIdx < 4 ? 0xEEE898FF : 0xFFF8E0FF));
                            canvas.drawOval(rectF(vx + pxOff - pRad * 1.25f, py - pRad * 0.8f,
                                    vx + pxOff + pRad * 1.25f, py + pRad * 0.8f), paint);
                        }
                    }
                }
                continue;
            } else if (universeTheme == 3) {
                // STAR WARS Top Sky: Ignited Lightsabers, Illuminated Jedi/Sith Holocrons & Marksman-H Training Remotes
                int mode = i % 3;
                if (mode == 0) {
                    // Type A: Floating Ignited Lightsaber (Metallic Hilt + Blazing Plasma Blade & Emitter Cross-Flare)
                    int saberCol = (i % 4 == 0) ? 0xFF28A8FF : (i % 4 == 1 ? 0xFF38FF78 : (i % 4 == 2 ? 0xFFFF2030 : 0xFFC858FF));
                    float tilt = (float) Math.sin(t * 2.2f + i) * px(3.5f);
                    float hiltTopY = cy + ch * 0.48f;
                    float hiltBotY = cy + ch * 0.82f;
                    float bladeTipX = cx + tilt;
                    float bladeTipY = cy - ch * 0.36f;
                    // Outer plasma blade glow
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeCap(Paint.Cap.ROUND);
                    paint.setStrokeWidth(cw * 1.45f * flicker);
                    paint.setColor((0x44 << 24) | (saberCol & 0xFFFFFF));
                    canvas.drawLine(cx, hiltTopY, bladeTipX, bladeTipY, paint);
                    // Mid plasma sheath
                    paint.setStrokeWidth(cw * 0.65f);
                    paint.setColor(saberCol);
                    canvas.drawLine(cx, hiltTopY, bladeTipX, bladeTipY, paint);
                    // White-hot plasma core
                    paint.setStrokeWidth(cw * 0.28f);
                    paint.setColor(0xFFFFFFFF);
                    canvas.drawLine(cx, hiltTopY, bladeTipX, bladeTipY, paint);
                    // Emitter cross-glint
                    paint.setStrokeWidth(px(1.2f));
                    canvas.drawLine(cx - cw * 0.9f, hiltTopY, cx + cw * 0.9f, hiltTopY, paint);
                    // Metallic Silver & Black Ribbed Lightsaber Hilt
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFD0DCE8);
                    canvas.drawRoundRect(cx - cw * 0.28f, hiltTopY, cx + cw * 0.28f, hiltBotY, px(1.5f), px(1.5f), paint);
                    paint.setColor(0xFF18202C);
                    canvas.drawRect(cx - cw * 0.30f, hiltTopY + ch * 0.10f, cx + cw * 0.30f, hiltBotY - ch * 0.08f, paint);
                    paint.setColor(0xFFFFD048); // Gold activation switch stud
                    canvas.drawCircle(cx, hiltTopY + ch * 0.06f, px(1.5f), paint);
                } else if (mode == 1) {
                    // Type B: Illuminated Jedi Cube Holocron & Sith Pyramid Holocron with Gold Lattice Matrix
                    boolean sith = (i % 2 == 1);
                    int holoCol = sith ? 0xFFFF2838 : 0xFF38C8FF;
                    float hy = cy + ch * 0.32f;
                    float hr = cw * 1.15f;
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x34 << 24) | (holoCol & 0xFFFFFF));
                    canvas.drawCircle(cx, hy, cw * 2.8f * flicker, paint);
                    if (sith) {
                        // Sith Pyramid Holocron (Translucent Crimson Tetrahedron + Gold Filigree Trim)
                        flamePath.reset();
                        flamePath.moveTo(cx, hy - hr * 1.1f);
                        flamePath.lineTo(cx + hr * 1.15f, hy + hr * 0.85f);
                        flamePath.lineTo(cx - hr * 1.15f, hy + hr * 0.85f);
                        flamePath.close();
                        paint.setColor(0xCC98101C);
                        canvas.drawPath(flamePath, paint);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(px(1.5f));
                        paint.setColor(0xFFFFD068);
                        canvas.drawPath(flamePath, paint);
                        canvas.drawCircle(cx, hy + hr * 0.22f, hr * 0.45f, paint);
                    } else {
                        // Jedi Cube Holocron (Translucent Cyan Cube + Gold Corner Matrix & Inner Ring)
                        paint.setColor(0xBB1068A8);
                        canvas.drawRoundRect(cx - hr, hy - hr, cx + hr, hy + hr, px(2.5f), px(2.5f), paint);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(px(1.5f));
                        paint.setColor(0xFFFFD878);
                        canvas.drawRoundRect(cx - hr, hy - hr, cx + hr, hy + hr, px(2.5f), px(2.5f), paint);
                        canvas.drawCircle(cx, hy, hr * 0.62f, paint);
                        canvas.drawLine(cx - hr, hy - hr, cx + hr, hy + hr, paint);
                        canvas.drawLine(cx + hr, hy - hr, cx - hr, hy + hr, paint);
                    }
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFFFFFFF);
                    canvas.drawCircle(cx, hy + (sith ? hr * 0.22f : 0f), cw * 0.34f * flicker, paint);
                } else {
                    // Type C: Jedi Marksman-H Training Remote Sphere with Glowing Photoreceptor & Emitter Ports
                    float ry = cy + ch * 0.32f;
                    float rr = cw * 1.05f;
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x2C << 24) | 0x0048C8FF);
                    canvas.drawCircle(cx, ry, cw * 2.5f * flicker, paint);
                    paint.setColor(0xFF8C9EB2);
                    canvas.drawCircle(cx, ry, rr, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.2f));
                    paint.setColor(0xFF243040);
                    canvas.drawLine(cx - rr, ry, cx + rr, ry, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFFF2838); // Glowing red training remote photoreceptor eye
                    canvas.drawCircle(cx - rr * 0.3f, ry - rr * 0.25f, px(2.2f), paint);
                    paint.setColor(0xFF48F0FF);
                    canvas.drawCircle(cx + rr * 0.35f, ry + rr * 0.25f, px(1.8f), paint);
                }
                continue;
            } else if (universeTheme == 4) {
                // MCU Top Sky: Tony Stark Arc Reactors, Tesseract Cubes, Eye of Agamotto & Infinity Containment Orbs
                int mcuType = i % 4;
                float my = cy + ch * 0.32f;
                float mr = cw * 1.20f;
                if (mcuType == 0) {
                    // Type A: Tony Stark Circular Chest Arc Reactor (Palladium Ring, 8 Copper Coils & Blazing Cyan Core)
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x38 << 24) | 0x0038E8FF);
                    canvas.drawCircle(cx, my, cw * 2.9f * flicker, paint);
                    paint.setColor(0xFF1A2434);
                    canvas.drawCircle(cx, my, mr, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(2.0f));
                    paint.setColor(0xFF48F0FF);
                    canvas.drawCircle(cx, my, mr * 0.82f, paint);
                    // 8 segmented copper/gold coil tabs around the Arc Reactor ring
                    paint.setStrokeWidth(px(1.8f));
                    paint.setColor(0xFFFFB848);
                    for (int cl = 0; cl < 8; cl++) {
                        double ang = cl * Math.PI / 4.0 + t * 0.6f;
                        canvas.drawLine(cx + (float) Math.cos(ang) * (mr * 0.62f), my + (float) Math.sin(ang) * (mr * 0.62f),
                                        cx + (float) Math.cos(ang) * (mr * 0.98f), my + (float) Math.sin(ang) * (mr * 0.98f), paint);
                    }
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFFFFFFF);
                    canvas.drawCircle(cx, my, mr * 0.42f * flicker, paint);
                } else if (mcuType == 1) {
                    // Type B: The Tesseract (Glowing 3D Isometric Space Stone Cube)
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x38 << 24) | 0x002898FF);
                    canvas.drawCircle(cx, my, cw * 2.8f * flicker, paint);
                    float cr = mr * 0.85f;
                    paint.setColor(0xBB1468D8);
                    canvas.drawRect(cx - cr, my - cr, cx + cr * 0.7f, my + cr * 0.7f, paint);
                    paint.setColor(0xCC28A8FF);
                    canvas.drawRect(cx - cr * 0.7f, my - cr * 0.7f, cx + cr, my + cr, paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.4f));
                    paint.setColor(0xFFA8F4FF);
                    canvas.drawRect(cx - cr, my - cr, cx + cr * 0.7f, my + cr * 0.7f, paint);
                    canvas.drawRect(cx - cr * 0.7f, my - cr * 0.7f, cx + cr, my + cr, paint);
                    canvas.drawLine(cx - cr, my - cr, cx - cr * 0.7f, my - cr * 0.7f, paint);
                    canvas.drawLine(cx + cr * 0.7f, my + cr * 0.7f, cx + cr, my + cr, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFFFFFFF);
                    canvas.drawCircle(cx, my, cr * 0.42f * flicker, paint);
                } else if (mcuType == 2) {
                    // Type C: Doctor Strange's Eye of Agamotto & Rotating Emerald Time Mandala
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x36 << 24) | 0x0028FF78);
                    canvas.drawCircle(cx, my, cw * 2.8f * flicker, paint);
                    // Golden eye amulet frame
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.8f));
                    paint.setColor(0xFFFFC848);
                    canvas.drawOval(rectF(cx - mr * 1.25f, my - mr * 0.72f, cx + mr * 1.25f, my + mr * 0.72f), paint);
                    // Emerald Time Mandala Ring & Core
                    paint.setStrokeWidth(px(1.3f));
                    paint.setColor(0xFF28FF78);
                    canvas.drawCircle(cx, my, mr * 0.62f, paint);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(cx, my, mr * 0.36f * flicker, paint);
                    paint.setColor(0xFFFFFFFF);
                    canvas.drawCircle(cx, my, mr * 0.18f, paint);
                } else {
                    // Type D: Morag Containment Orb / Scepter Cradle with Faceted Power/Mind/Reality Infinity Gem
                    int gemCol = (i % 3 == 0) ? 0xFFC848FF : (i % 3 == 1 ? 0xFFFFE038 : 0xFFFF2838);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x36 << 24) | (gemCol & 0xFFFFFF));
                    canvas.drawCircle(cx, my, cw * 2.8f * flicker, paint);
                    // Metallic containment cradle arcs on left & right
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.8f));
                    paint.setColor(0xFFD8B868);
                    canvas.drawArc(rectF(cx - mr, my - mr, cx + mr, my + mr), 135, 90, false, paint);
                    canvas.drawArc(rectF(cx - mr, my - mr, cx + mr, my + mr), 315, 90, false, paint);
                    // Faceted Hexagon Infinity Stone + 4-point star flare
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(gemCol);
                    canvas.drawOval(rectF(cx - mr * 0.52f, my - mr * 0.72f, cx + mr * 0.52f, my + mr * 0.72f), paint);
                    paint.setColor(0xFFFFFFFF);
                    canvas.drawCircle(cx, my - mr * 0.12f, mr * 0.24f * flicker, paint);
                }
                continue;
            } else if (universeTheme == 5) {
                // DCU Top Sky: Green Lantern Power Batteries, Fortress Sunstone Crystals & Gotham Bat-Beacons
                int dcType = i % 3;
                float dy = cy + ch * 0.32f;
                float dr = cw * 1.15f;
                if (dcType == 0) {
                    // Type A: Green Lantern Corps Power Battery (Oa Lantern Silhouette + Blazing Emerald Core)
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x38 << 24) | 0x0028FF78);
                    canvas.drawCircle(cx, dy, cw * 2.9f * flicker, paint);
                    // Top & Bottom flared green battery caps + side struts
                    paint.setColor(0xFF148848);
                    canvas.drawRoundRect(cx - dr * 0.85f, dy - dr, cx + dr * 0.85f, dy - dr * 0.62f, px(1.5f), px(1.5f), paint);
                    canvas.drawRoundRect(cx - dr * 0.85f, dy + dr * 0.62f, cx + dr * 0.85f, dy + dr, px(1.5f), px(1.5f), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.6f));
                    paint.setColor(0xFF28FF78);
                    canvas.drawLine(cx - dr * 0.72f, dy - dr * 0.65f, cx - dr * 0.52f, dy + dr * 0.65f, paint);
                    canvas.drawLine(cx + dr * 0.72f, dy - dr * 0.65f, cx + dr * 0.52f, dy + dr * 0.65f, paint);
                    canvas.drawCircle(cx, dy, dr * 0.55f, paint);
                    // Blazing white-emerald willpower chamber core
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFF38FF88);
                    canvas.drawCircle(cx, dy, dr * 0.45f * flicker, paint);
                    paint.setColor(0xFFFFFFFF);
                    canvas.drawCircle(cx, dy, dr * 0.22f, paint);
                } else if (dcType == 1) {
                    // Type B: Fortress of Solitude Kryptonian Sunstone Crystal Cluster (3 Prismatic Shards)
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x34 << 24) | 0x0048D8FF);
                    canvas.drawCircle(cx, dy, cw * 2.8f * flicker, paint);
                    // Left & Right angled crystal spikes
                    paint.setColor(0xBB38B8F0);
                    flamePath.reset();
                    flamePath.moveTo(cx - dr * 0.2f, dy + dr * 0.9f);
                    flamePath.lineTo(cx - dr * 1.05f, dy - dr * 0.25f);
                    flamePath.lineTo(cx - dr * 0.45f, dy - dr * 0.55f);
                    flamePath.lineTo(cx + dr * 0.2f, dy + dr * 0.9f);
                    flamePath.lineTo(cx + dr * 1.05f, dy - dr * 0.25f);
                    flamePath.lineTo(cx + dr * 0.45f, dy - dr * 0.55f);
                    flamePath.close();
                    canvas.drawPath(flamePath, paint);
                    // Central tall hexagonal ice-cyan Sunstone shard
                    paint.setColor(0xEE88F0FF);
                    flamePath.reset();
                    flamePath.moveTo(cx, dy - dr * 1.25f);
                    flamePath.lineTo(cx + dr * 0.48f, dy - dr * 0.45f);
                    flamePath.lineTo(cx + dr * 0.36f, dy + dr * 0.95f);
                    flamePath.lineTo(cx - dr * 0.36f, dy + dr * 0.95f);
                    flamePath.lineTo(cx - dr * 0.48f, dy - dr * 0.45f);
                    flamePath.close();
                    canvas.drawPath(flamePath, paint);
                    paint.setColor(0xFFFFFFFF);
                    canvas.drawCircle(cx, dy - dr * 0.35f, dr * 0.24f * flicker, paint);
                } else {
                    // Type C: Gotham Mini Bat-Searchlight Beacon with Golden Spotlight Lens & Bat Silhouette
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x36 << 24) | 0x00FFE068);
                    canvas.drawCircle(cx, dy, cw * 2.8f * flicker, paint);
                    paint.setColor(0xEEFFE478);
                    canvas.drawOval(rectF(cx - dr * 1.15f, dy - dr * 0.78f, cx + dr * 1.15f, dy + dr * 0.78f), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.4f));
                    paint.setColor(0xFF283244);
                    canvas.drawOval(rectF(cx - dr * 1.15f, dy - dr * 0.78f, cx + dr * 1.15f, dy + dr * 0.78f), paint);
                    // Crisp dark Bat-wings emblem inside the golden searchlight lens
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xEE0A0E1A);
                    flamePath.reset();
                    flamePath.moveTo(cx, dy - dr * 0.32f);
                    flamePath.lineTo(cx + dr * 0.78f, dy - dr * 0.25f);
                    flamePath.lineTo(cx + dr * 0.42f, dy + dr * 0.28f);
                    flamePath.lineTo(cx, dy + dr * 0.42f);
                    flamePath.lineTo(cx - dr * 0.42f, dy + dr * 0.28f);
                    flamePath.lineTo(cx - dr * 0.78f, dy - dr * 0.25f);
                    flamePath.close();
                    canvas.drawPath(flamePath, paint);
                }
                continue;
            } else if (universeTheme == 6) {
                // FORMULA 1 Top Sky: Cantilever Stadium Floodlight Rigs, FIA Digital Light Gantry Pods & Camera Strobe Flashes
                float stY = candles[b + 1];
                int f1Mode = i % 3;
                if (f1Mode == 0) {
                    // Type A: Suspended 2x3 Stadium LED Floodlight Truss with Downward Volumetric Spotlight Cone
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0x18C8EEFF);
                    flamePath.reset();
                    flamePath.moveTo(cx - cw * 1.5f, stY + px(4f));
                    flamePath.lineTo(cx + cw * 1.5f, stY + px(4f));
                    flamePath.lineTo(cx + cw * 4.0f, stY + ch * 1.40f);
                    flamePath.lineTo(cx - cw * 4.0f, stY + ch * 1.40f);
                    flamePath.close();
                    canvas.drawPath(flamePath, paint);

                    paint.setColor(0x2CA8E8FF);
                    canvas.drawCircle(cx, stY + px(3f), cw * 2.8f * flicker, paint);
                    paint.setColor(0xEE141A24);
                    canvas.drawRoundRect(cx - cw * 1.65f, stY - px(4f), cx + cw * 1.65f, stY + px(7f), px(2.5f), px(2.5f), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.2f));
                    paint.setColor(0x886888A8);
                    canvas.drawRoundRect(cx - cw * 1.65f, stY - px(4f), cx + cw * 1.65f, stY + px(7f), px(2.5f), px(2.5f), paint);
                    paint.setStyle(Paint.Style.FILL);
                    for (int rw = 0; rw < 2; rw++) {
                        for (int cl = -1; cl <= 1; cl++) {
                            paint.setColor(0xFFFFFFFF);
                            canvas.drawCircle(cx + cl * cw * 0.92f, stY - px(0.8f) + rw * px(4.2f), cw * 0.30f, paint);
                        }
                    }
                } else if (f1Mode == 1) {
                    // Type B: FIA Digital Trackside Starting / Sector Marshal Light Pod (Twin Vertical Red/Green LEDs)
                    boolean greenFlag = (((int) (t * 0.8f + i)) % 3 == 0);
                    int ledCol = greenFlag ? 0xFF00E676 : 0xFFFF1801;
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x2E << 24) | (ledCol & 0xFFFFFF));
                    canvas.drawCircle(cx, stY + ch * 0.28f, cw * 2.5f * flicker, paint);
                    paint.setColor(0xEE10141E);
                    canvas.drawRoundRect(cx - cw * 0.82f, stY - px(2f), cx + cw * 0.82f, stY + ch * 0.62f, px(3f), px(3f), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.2f));
                    paint.setColor(0x88485870);
                    canvas.drawRoundRect(cx - cw * 0.82f, stY - px(2f), cx + cw * 0.82f, stY + ch * 0.62f, px(3f), px(3f), paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(greenFlag ? 0xFF281818 : 0xFFFF1801);
                    canvas.drawCircle(cx, stY + ch * 0.16f, cw * 0.42f, paint);
                    paint.setColor(greenFlag ? 0xFF00E676 : 0xFF182820);
                    canvas.drawCircle(cx, stY + ch * 0.44f, cw * 0.42f, paint);
                } else {
                    // Type C: Grandstand Telephoto Camera Lens & Strobe Flash Burst
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xCC18202C);
                    canvas.drawRoundRect(cx - cw * 0.85f, stY - cw * 0.55f, cx + cw * 0.85f, stY + cw * 0.55f, px(2f), px(2f), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.3f));
                    paint.setColor(0xFF90B4D8);
                    canvas.drawCircle(cx, stY, cw * 0.42f, paint);
                    float camWave = (float) Math.sin(t * 4.8f + i * 2.1f);
                    if (camWave > 0.35f) {
                        float fl = (camWave - 0.35f) / 0.65f;
                        int fa = Math.min(255, Math.round(fl * fl * 255f));
                        paint.setStyle(Paint.Style.FILL);
                        paint.setColor((fa / 3 << 24) | 0x00B8F2FF);
                        canvas.drawCircle(cx, stY, cw * (2.5f + fl * 2.4f), paint);
                        paint.setColor((fa << 24) | 0x00FFFFFF);
                        canvas.drawCircle(cx, stY, cw * (0.68f + fl * 0.55f), paint);
                        paint.setStrokeWidth(px(1.5f));
                        float rLen = cw * (2.3f + fl * 3.0f);
                        canvas.drawLine(cx - rLen, stY, cx + rLen, stY, paint);
                        canvas.drawLine(cx, stY - rLen, cx, stY + rLen, paint);
                    }
                    paint.setStyle(Paint.Style.FILL);
                }
                continue;
            } else if (universeTheme == 7) {
                // CYBERPUNK 2099 Top Sky: Floating Holographic Neon Signs, Militech Recon Drones & Relic Biochip Shards
                float cy2 = cy + ch * 0.28f;
                int cpMode = i % 3;
                if (cpMode == 0) {
                    // Type A: Floating Vertical Neon Kanji / Cyber-Billboard Frame (Cyan + Magenta Neon Tubes)
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x32 << 24) | 0x00FF0055);
                    canvas.drawCircle(cx, cy2, cw * 2.8f * flicker, paint);
                    paint.setColor(0xDD0A0818);
                    canvas.drawRoundRect(cx - cw * 1.15f, cy2 - ch * 0.36f, cx + cw * 1.15f, cy2 + ch * 0.36f, px(3f), px(3f), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.6f));
                    paint.setColor(0xFF00F0FF);
                    canvas.drawRoundRect(cx - cw * 1.15f, cy2 - ch * 0.36f, cx + cw * 1.15f, cy2 + ch * 0.36f, px(3f), px(3f), paint);
                    // Glowing magenta & yellow stylized Kanji / Cyber glyph strokes inside the neon sign
                    paint.setColor(0xFFFF0055);
                    canvas.drawLine(cx - cw * 0.65f, cy2 - ch * 0.18f, cx + cw * 0.65f, cy2 - ch * 0.18f, paint);
                    canvas.drawLine(cx, cy2 - ch * 0.24f, cx, cy2 + ch * 0.24f, paint);
                    paint.setColor(0xFFFCEE09);
                    canvas.drawLine(cx - cw * 0.55f, cy2 + ch * 0.08f, cx + cw * 0.55f, cy2 + ch * 0.08f, paint);
                    paint.setStyle(Paint.Style.FILL);
                } else if (cpMode == 1) {
                    // Type B: Arasaka / Militech Security Recon Drone with Red Laser Scanner Cone
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0x24FF003C);
                    flamePath.reset();
                    flamePath.moveTo(cx, cy2 + px(3f));
                    flamePath.lineTo(cx + cw * 2.4f, cy2 + ch * 0.85f);
                    flamePath.lineTo(cx - cw * 2.4f, cy2 + ch * 0.85f);
                    flamePath.close();
                    canvas.drawPath(flamePath, paint);
                    paint.setColor(0xFF1A2030);
                    canvas.drawOval(rectF(cx - cw * 1.35f, cy2 - cw * 0.65f, cx + cw * 1.35f, cy2 + cw * 0.65f), paint);
                    paint.setColor(0xFFFF003C);
                    canvas.drawCircle(cx, cy2, cw * 0.42f * flicker, paint);
                    paint.setColor(0xFF00F0FF);
                    canvas.drawCircle(cx - cw * 0.9f, cy2, px(1.5f), paint);
                    canvas.drawCircle(cx + cw * 0.9f, cy2, px(1.5f), paint);
                } else {
                    // Type C: Floating Relic Biochip Shard & Sandevistan Neural Node
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((0x34 << 24) | 0x0000F0FF);
                    canvas.drawCircle(cx, cy2, cw * 2.6f * flicker, paint);
                    paint.setColor(0xEE081828);
                    canvas.drawRoundRect(cx - cw * 0.72f, cy2 - ch * 0.30f, cx + cw * 0.72f, cy2 + ch * 0.30f, px(2.5f), px(2.5f), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.4f));
                    paint.setColor(0xFFFCEE09);
                    canvas.drawRoundRect(cx - cw * 0.72f, cy2 - ch * 0.30f, cx + cw * 0.72f, cy2 + ch * 0.30f, px(2.5f), px(2.5f), paint);
                    paint.setColor(0xFF00F0FF);
                    canvas.drawLine(cx - cw * 0.4f, cy2 - ch * 0.14f, cx + cw * 0.4f, cy2 + ch * 0.14f, paint);
                    canvas.drawLine(cx + cw * 0.4f, cy2 - ch * 0.14f, cx - cw * 0.4f, cy2 + ch * 0.14f, paint);
                    paint.setStyle(Paint.Style.FILL);
                }
                continue;
            }
            // THEME 0 (HARRY POTTER): Sculpted Great Hall Floating Wax Taper Candle with Brass Holder & 3-Layer Flame
            paint.setColor((0x24 << 24) | rgbOnly);
            canvas.drawCircle(cx, cy - px(5f), cw * 2.1f * flicker, paint);

            float left = cx - cw / 2f, right = cx + cw / 2f;
            // Ivory beeswax taper body + shaded lower wax + dripping wax rivulets
            paint.setColor(0xE6F4EBD6);
            canvas.drawRoundRect(left, cy, right, cy + ch, cw * 0.35f, cw * 0.35f, paint);
            paint.setColor(0x66C8A870);
            canvas.drawRoundRect(left, cy + ch * 0.55f, right, cy + ch, cw * 0.35f, cw * 0.35f, paint);
            paint.setColor(0xF0FFF8E8);
            canvas.drawRoundRect(left, cy, left + cw * 0.28f, cy + ch * 0.48f, px(1f), px(1f), paint);
            // Antique Hogwarts Brass Candle-Cup & Drip Saucer at bottom of taper
            paint.setColor(0xFFD8A848);
            canvas.drawRoundRect(left - px(2.0f), cy + ch - px(2.2f), right + px(2.0f), cy + ch + px(1.5f), px(1.5f), px(1.5f), paint);
            canvas.drawCircle(cx, cy + ch + px(2.2f), cw * 0.28f, paint);

            // Charred candle wick
            paint.setColor(0xAA2A2018);
            paint.setStrokeWidth(px(1.2f));
            canvas.drawLine(cx, cy, cx, cy - px(3f), paint);

            // Sculpted swaying teardrop flame (outer amber -> inner white-gold + 4-point star glint)
            float sway = (float) Math.sin(t * 4.2f + candles[b + 6]) * px(2.0f);
            float fw = cw * 0.66f;
            flamePath.reset();
            flamePath.moveTo(cx, cy - px(2f));
            flamePath.quadTo(cx - fw, cy - px(6f), cx + sway, flameTipY);
            flamePath.quadTo(cx + fw, cy - px(6f), cx, cy - px(2f));
            flamePath.close();
            paint.setColor(0xEEFFAE28);
            canvas.drawPath(flamePath, paint);

            paint.setColor(0xFFFFF8D8);
            canvas.drawCircle(cx + sway * 0.3f, cy - px(5.0f), cw * 0.30f, paint);
        }
        bitmapPaint.setAlpha(255);

        // 4. INTERACTIVE WOW TWISTS: Marauder's Map Footsteps, Lumos Wand Burst & Golden Snitch Companion
        drawInteractiveMagic(canvas, t);

        // 4B. LIVING WEATHER & SKY MOOD ENGINE (Rainstorm, Winter Snowfall, Thunderstorm Lightning, Solar Aurora)
        drawWeatherAtmosphere(canvas, w, h, t);

        // 5. EXPANDED BOTTOM STAGE (y = 576..1080): 24 Iconic Harry Potter Scenes
        drawStoryLoop(canvas, w, h, t);

        if (running && isShown() && hasWindowFocus()) {
            postInvalidateOnAnimation();
        }
    }

    /**
     * Renders the 3 interactive Wizarding World Easter eggs with zero per-frame object allocations:
     * 1. Marauder's Map golden ink footprints walking from the previous tile to the newly focused tile.
     * 2. Lumos Maxima House-colored wand-spark burst radiating from the focused tile.
     * 3. Autonomous winged Golden Snitch companion that darts across the screen and hovers over the focused card.
     */
    private void drawInteractiveMagic(Canvas c, float t) {
        paint.setStyle(Paint.Style.FILL);
        // 1. Marauder's Map Footsteps Trail
        for (int i = 0; i < FOOTSTEP_MAX; i++) {
            int b = i * 6;
            float spawn = footsteps[b + 4];
            if (spawn <= 0f) continue;
            float age = t - spawn;
            if (age < 0f || age > 1.85f) continue;
            float life = age < 0.18f ? (age / 0.18f) : (1f - (age - 0.18f) / 1.67f);
            int alpha = Math.min(220, Math.max(0, Math.round(life * 210f)));
            float fx = footsteps[b], fy = footsteps[b + 1];
            float ux = footsteps[b + 2], uy = footsteps[b + 3];
            paint.setColor((alpha << 24) | 0x00F5D078);
            // Sole + heel of boot print oriented along movement vector (ux, uy)
            c.drawCircle(fx + ux * px(3.5f), fy + uy * px(3.5f), px(3.4f), paint);
            c.drawCircle(fx - ux * px(3.5f), fy - uy * px(3.5f), px(2.3f), paint);
        }

        // 2. Lumos Maxima Wand-Spark Burst on focus change
        for (int i = 0; i < BURST_COUNT; i++) {
            int b = i * 6;
            float spawn = burstSparks[b + 4];
            if (spawn <= 0f) continue;
            float age = t - spawn;
            if (age < 0f || age > 0.75f) continue;
            float prog = age / 0.75f;
            int alpha = Math.round((1f - prog) * 245f);
            int rgb = Float.floatToRawIntBits(burstSparks[b + 5]);
            float sx = burstSparks[b] + burstSparks[b + 2] * age;
            float sy = burstSparks[b + 1] + burstSparks[b + 3] * age + 0.5f * px(95f) * age * age;
            paint.setColor((alpha << 24) | (rgb & 0xFFFFFF));
            c.drawCircle(sx, sy, px(3.2f * (1f - prog * 0.4f)), paint);
        }

        // 3. Autonomous Companion hovering above the focused tile (or patrolling the upper sky in Screensaver Mode)
        if (screensaverMode) {
            snitchTargetX = getWidth() * (0.54f + 0.30f * (float) Math.sin(t * 0.42f));
            snitchTargetY = getHeight() * (0.18f + 0.06f * (float) Math.cos(t * 0.68f));
        }
        float snitchDamp = dampFactor(0.15f, frameDt);
        snitchCurX += (snitchTargetX - snitchCurX) * snitchDamp;
        snitchCurY += (snitchTargetY - snitchCurY) * snitchDamp;
        float sx = snitchCurX + (float) Math.cos(t * 5.5f) * px(14f);
        float sy = snitchCurY + (float) Math.sin(t * 7.2f) * px(5f);
        if (universeTheme == 1) {
            // NARUTO Companion: Swirling Rasengan + Spinning 4-Blade Wind Demon Shuriken
            paint.setColor(0x4838B8FF);
            c.drawCircle(sx, sy, px(12f), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.0f));
            paint.setColor(0xEEFF4030);
            for (int bl = 0; bl < 4; bl++) {
                double ang = t * 14f + bl * Math.PI / 2.0;
                c.drawLine(sx, sy, sx + (float) Math.cos(ang) * px(13f), sy + (float) Math.sin(ang) * px(13f), paint);
            }
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFFD8F8FF);
            c.drawCircle(sx, sy, px(4.2f), paint);
            return;
        } else if (universeTheme == 2) {
            // DEMON SLAYER Companion: Glowing Shinobu Wisteria Butterfly
            float flap = (float) Math.abs(Math.sin(t * 24f)) * px(10f);
            paint.setColor(0x48C868FF);
            c.drawCircle(sx, sy, px(12f), paint);
            paint.setColor(0xFFE088FF);
            c.drawOval(sx - px(4f) - flap, sy - px(8f), sx, sy + px(6f), paint);
            c.drawOval(sx, sy - px(8f), sx + px(4f) + flap, sy + px(6f), paint);
            paint.setColor(0xFF68F8FF);
            c.drawCircle(sx, sy, px(2.8f), paint);
            return;
        } else if (universeTheme == 3) {
            // STAR WARS Companion: Twin Crossed Lightsabers / Jedi Training Remote
            paint.setColor(0x4438B8FF);
            c.drawCircle(sx, sy, px(12f), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.4f));
            paint.setColor(0xFF38C8FF);
            c.drawLine(sx - px(10f), sy + px(8f), sx + px(10f), sy - px(8f), paint);
            paint.setColor(0xFFFF2838);
            c.drawLine(sx - px(10f), sy - px(8f), sx + px(10f), sy + px(8f), paint);
            paint.setStyle(Paint.Style.FILL);
            return;
        } else if (universeTheme == 4) {
            // MCU Companion: Glowing Iron Man Arc Reactor & Captain America Shield
            paint.setColor(0x4838E8FF);
            c.drawCircle(sx, sy, px(12f), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.2f));
            paint.setColor(0xFF48F0FF);
            c.drawCircle(sx, sy, px(8.5f), paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFFFFFFFF);
            c.drawCircle(sx, sy, px(4.0f), paint);
            return;
        } else if (universeTheme == 5) {
            // DCU Companion: Golden Batarang & Speed Force Aura
            paint.setColor(0x44FFE066);
            c.drawCircle(sx, sy, px(12f), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.4f));
            paint.setColor(0xFFFFD848);
            c.drawArc(rectF(sx - px(14f), sy - px(6f), sx + px(14f), sy + px(8f)), 200, 140, false, paint);
            paint.setStyle(Paint.Style.FILL);
            return;
        } else if (universeTheme == 6) {
            // FORMULA 1 Companion: Spinning Pirelli Soft Red-Wall F1 Wheel & Glowing Carbon-Ceramic Brake Rotor
            paint.setColor(0x48FF1801);
            c.drawCircle(sx, sy, px(13f), paint);
            paint.setColor(0xFF141820);
            c.drawCircle(sx, sy, px(9.8f), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.2f));
            paint.setColor(0xFFFF1801);
            c.drawArc(rectF(sx - px(7.2f), sy - px(7.2f), sx + px(7.2f), sy + px(7.2f)), (t * 360f) % 360f, 250f, false, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFFFF6D00);
            c.drawCircle(sx, sy, px(3.6f), paint);
            return;
        } else if (universeTheme == 7) {
            // CYBERPUNK 2099 Companion: Hovering Netrunner Cyber-Deck & Twin Cyan/Magenta Kiroshi Optics
            paint.setColor(0x4800F0FF);
            c.drawCircle(sx, sy, px(13f), paint);
            paint.setColor(0xFF101424);
            c.drawRoundRect(sx - px(10f), sy - px(6f), sx + px(10f), sy + px(6f), px(3f), px(3f), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(1.8f));
            paint.setColor(0xFFFCEE09);
            c.drawRoundRect(sx - px(10f), sy - px(6f), sx + px(10f), sy + px(6f), px(3f), px(3f), paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFF00F0FF);
            c.drawCircle(sx - px(4f), sy, px(2.4f), paint);
            paint.setColor(0xFFFF0055);
            c.drawCircle(sx + px(4f), sy, px(2.4f), paint);
            return;
        }
        paint.setColor(0x44FFE066);
        c.drawCircle(sx, sy, px(11f), paint);
        paint.setColor(0xFFFFEA78);
        c.drawCircle(sx, sy, px(4.8f), paint);
        float flap = (float) Math.sin(t * 32f) * px(8f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(1.6f));
        paint.setColor(0xEEFFFFFF);
        c.drawLine(sx - px(4f), sy, sx - px(18f), sy - px(5f) + flap, paint);
        c.drawLine(sx + px(4f), sy, sx + px(18f), sy - px(5f) + flap, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * v11.0.0 Living Weather & Sky Mood Engine:
     * Renders zero-allocation 120 FPS procedural weather particles across the sky & stage:
     * - Mode 2 (Rainstorm) & Mode 4 (Thunderstorm): Diagonal wind-driven rain streaks + wet stage ripples (+ forked sky lightning in Mode 4)
     * - Mode 3 (Winter Snowfall): Gently swaying multi-depth crystalline snowflakes
     * - Mode 5 (Aurora Borealis): Shimmering emerald, cyan & violet northern lights curtains across the upper sky
     */
    private void drawWeatherAtmosphere(Canvas c, int w, int h, float t) {
        int activeMood = (weatherMood == 0) ? liveWeatherMood : weatherMood;
        if (activeMood <= 1) return;

        float groundY = h * 0.900f;
        if (activeMood == 2 || activeMood == 4) {
            // Rainstorm / Thunderstorm: 42 diagonal wind-driven rain streaks + stage puddle ripples
            int rainRgb = (universeTheme == 7) ? 0x0000F0FF : 0x00A8DCFF;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            for (int i = 0; i < 42; i++) {
                float speed = px(580f + (i % 5) * 95f);
                float rx = ((i * 173.7f + w * 1.4f - t * speed * 0.22f) % (w * 1.15f)) - w * 0.08f;
                float ry = ((i * 97.3f + t * speed) % (groundY + px(20f)));
                float len = px(16f + (i % 4) * 5.5f);
                int ra = 65 + (i % 4) * 28;
                paint.setStrokeWidth(px(i % 3 == 0 ? 1.6f : 1.1f));
                paint.setColor((ra << 24) | rainRgb);
                c.drawLine(rx, ry, rx - len * 0.24f, ry + len, paint);
            }
            // Wet stage splash ripples along groundY
            for (int r = 0; r < 10; r++) {
                float rp = ((t * 1.9f + r * 0.17f) % 1.0f);
                float sx = w * (0.14f + ((r * 0.077f) % 0.72f));
                int sa = Math.round((1f - rp) * 110f);
                paint.setStrokeWidth(px(1.2f));
                paint.setColor((sa << 24) | rainRgb);
                float radX = px(4f + rp * 16f), radY = px(1.2f + rp * 3.8f);
                c.drawOval(rectF(sx - radX, groundY - radY, sx + radX, groundY + radY), paint);
            }
            if (activeMood == 4) {
                // Periodic Thunderstorm Lightning Bolt & Sky Sheet Flash
                float flashCycle = (t * 0.55f) % 4.0f;
                if (flashCycle < 0.22f) {
                    float fl = 1f - (flashCycle / 0.22f);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor((Math.round(fl * 42f) << 24) | 0x00D8F0FF);
                    c.drawRect(0, 0, w, h, paint);
                    int boltSlot = ((int) (t * 0.55f / 4.0f)) % 3;
                    float bx = w * (0.28f + boltSlot * 0.22f);
                    drawLightningStreak(c, bx, px(12f), bx + px(28f), h * 0.58f,
                            t, 990 + boltSlot, 0x0068C8FF, 0x00B8F4FF, 0x00FFFFFF, px(3.4f), Math.round(fl * 240f), true);
                }
            }
            paint.setStyle(Paint.Style.FILL);
        } else if (activeMood == 3) {
            // Winter Snowfall: 40 gently swaying multi-depth snowflakes
            paint.setStyle(Paint.Style.FILL);
            for (int i = 0; i < 40; i++) {
                float fallSpd = px(42f + (i % 5) * 16f);
                float sy = (i * 83.5f + t * fallSpd) % (groundY + px(12f));
                float sway = (float) Math.sin(t * (1.2f + (i % 4) * 0.35f) + i * 1.7f) * px(18f);
                float sx = ((i * 149.3f) % w) + sway;
                if (sx < 0) sx += w;
                float sr = px(1.8f + (i % 4) * 1.05f);
                int sa = 110 + (i % 4) * 34;
                paint.setColor((sa << 24) | 0x00F4FAFF);
                c.drawCircle(sx, sy, sr, paint);
                if (i % 5 == 0) {
                    paint.setColor(((sa / 3) << 24) | 0x00A8E4FF);
                    c.drawCircle(sx, sy, sr * 2.2f, paint);
                }
            }
        } else if (activeMood == 5) {
            // Solar Aurora Borealis: Shimmering emerald, cyan & violet northern lights ribbons across the upper sky
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            int[] auroraCols = {0x0000FF9D, 0x0000E5FF, 0x00B388FF};
            for (int band = 0; band < 3; band++) {
                int col = auroraCols[band];
                float baseY = h * (0.11f + band * 0.055f);
                actorPath.reset();
                for (int seg = 0; seg <= 16; seg++) {
                    float frac = seg / 16f;
                    float ax = frac * w;
                    float ay = baseY + (float) Math.sin(frac * Math.PI * 3.0 + t * (0.9f + band * 0.25f) + band * 1.4f) * px(22f);
                    if (seg == 0) actorPath.moveTo(ax, ay);
                    else actorPath.lineTo(ax, ay);
                }
                int aa = Math.round(36f + 16f * (float) Math.sin(t * 1.4f + band));
                paint.setStrokeWidth(px(26f - band * 4f));
                paint.setColor((aa << 24) | col);
                c.drawPath(actorPath, paint);
                paint.setStrokeWidth(px(6f));
                paint.setColor(((aa * 3 / 2) << 24) | col);
                c.drawPath(actorPath, paint);
            }
            paint.setStyle(Paint.Style.FILL);
        }
    }

    /**
     * Draws 5 pearlescent translucent Hogwarts House Ghosts (Nearly Headless Nick, The Grey Lady,
     * The Fat Friar, The Bloody Baron, and Moaning Myrtle) gliding among the floating Great Hall candles.
     */
    private void drawFlyingGhosts(Canvas c, int w, int h, float t) {
        float minX = w * 0.33f, spanX = w * 0.62f;
        for (int g = 0; g < GHOST_COUNT; g++) {
            int b = g * 7;
            float speedX = ghosts[b + 2];
            float rawX = (ghosts[b] - minX + t * speedX) % spanX;
            if (rawX < 0) rawX += spanX;
            float gx = minX + rawX;
            float gy = ghosts[b + 1] + (float) Math.sin(t * ghosts[b + 3] + ghosts[b + 4]) * px(14f);
            float sc = ghosts[b + 5];
            int type = Math.round(ghosts[b + 6]);
            float dir = speedX >= 0 ? 1f : -1f;
            if (universeTheme == 1) {
                // NARUTO Top Sky: Drifting Akatsuki Red Clouds, Itachi's Sharingan Crows & Paper-Bomb Kunai
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0x98C81824);
                c.drawCircle(gx - px(10f * sc), gy, px(10f * sc), paint);
                c.drawCircle(gx + px(2f * sc), gy - px(5f * sc), px(12f * sc), paint);
                c.drawCircle(gx + px(14f * sc), gy + px(1f * sc), px(9f * sc), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(1.5f * sc));
                paint.setColor(0xCCFFE8E8);
                c.drawArc(rectF(gx - px(18f * sc), gy - px(14f * sc), gx + px(22f * sc), gy + px(10f * sc)), 195, 150, false, paint);
                drawBird(c, gx + dir * px(26f * sc), gy - px(8f * sc), t * 11f + g, px(14f * sc), 0xDD18080C);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0xFFFF1828); // Glowing red Sharingan eye on Itachi's crow
                c.drawCircle(gx + dir * px(28f * sc), gy - px(9f * sc), px(1.6f * sc), paint);
                continue;
            } else if (universeTheme == 2) {
                // DEMON SLAYER Top Sky: Kasugai Crows & 4-Winged Glowing Wisteria Butterflies (Kocho Shinobu)
                drawBird(c, gx, gy, t * 10f + g, px(18f * sc), 0xDD1A1024);
                float bfX = gx - dir * px(24f * sc);
                float bfY = gy - px(2f * sc);
                float bfFlap = (float) Math.abs(Math.sin(t * 20f + g)) * px(8.5f * sc);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0x38D878FF);
                c.drawCircle(bfX, bfY, px(13f * sc), paint);
                // 4 sculpted butterfly wings (2 upper violet-pink wings + 2 lower lavender wings)
                paint.setColor(0xE0E088FF);
                c.drawOval(rectF(bfX - bfFlap - px(2f * sc), bfY - px(7f * sc), bfX, bfY + px(1f * sc)), paint);
                c.drawOval(rectF(bfX, bfY - px(7f * sc), bfX + bfFlap + px(2f * sc), bfY + px(1f * sc)), paint);
                paint.setColor(0xCCF8C8FF);
                c.drawOval(rectF(bfX - bfFlap * 0.75f - px(1.5f * sc), bfY, bfX, bfY + px(6.5f * sc)), paint);
                c.drawOval(rectF(bfX, bfY, bfX + bfFlap * 0.75f + px(1.5f * sc), bfY + px(6.5f * sc)), paint);
                paint.setColor(0xFFFFFFFF);
                c.drawCircle(bfX, bfY, px(1.8f * sc), paint);
                continue;
            } else if (universeTheme == 3) {
                // STAR WARS Top Sky: Dogfighting X-Wing Starfighters & TIE Fighters with Glowing Ion Engines & Laser Bolts
                if (g % 2 == 0) {
                    // Imperial TIE Fighter: Twin hexagonal solar panels + central ball cockpit + dual green laser bolts
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(2.2f * sc));
                    paint.setColor(0xFFD0DCE8);
                    c.drawLine(gx - px(11f * sc), gy - px(12f * sc), gx - px(11f * sc), gy + px(12f * sc), paint);
                    c.drawLine(gx + px(11f * sc), gy - px(12f * sc), gx + px(11f * sc), gy + px(12f * sc), paint);
                    c.drawLine(gx - px(11f * sc), gy, gx + px(11f * sc), gy, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFF98ACC0);
                    c.drawCircle(gx, gy, px(5.2f * sc), paint);
                    paint.setColor(0xFF1A2432);
                    c.drawCircle(gx, gy, px(2.8f * sc), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(2.0f * sc));
                    paint.setColor(0xFF38FF68);
                    c.drawLine(gx + dir * px(15f * sc), gy - px(2.5f * sc), gx + dir * px(36f * sc), gy - px(2.5f * sc), paint);
                    c.drawLine(gx + dir * px(15f * sc), gy + px(2.5f * sc), gx + dir * px(36f * sc), gy + px(2.5f * sc), paint);
                } else {
                    // Rebel X-Wing Starfighter: S-Foils in attack position + glowing pink ion engines + red laser bolts
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(2.0f * sc));
                    paint.setColor(0xFFE4ECF8);
                    c.drawLine(gx - px(13f * sc), gy - px(8f * sc), gx + px(13f * sc), gy + px(8f * sc), paint);
                    c.drawLine(gx - px(13f * sc), gy + px(8f * sc), gx + px(13f * sc), gy - px(8f * sc), paint);
                    c.drawLine(gx - dir * px(12f * sc), gy, gx + dir * px(16f * sc), gy, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFFF4878); // Glowing X-Wing ion engine nacelles
                    c.drawCircle(gx - dir * px(6f * sc), gy - px(4f * sc), px(2.2f * sc), paint);
                    c.drawCircle(gx - dir * px(6f * sc), gy + px(4f * sc), px(2.2f * sc), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(0xFFFF2838);
                    c.drawLine(gx + dir * px(18f * sc), gy - px(6f * sc), gx + dir * px(38f * sc), gy - px(6f * sc), paint);
                    c.drawLine(gx + dir * px(18f * sc), gy + px(6f * sc), gx + dir * px(38f * sc), gy + px(6f * sc), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                continue;
            } else if (universeTheme == 4) {
                // MCU Top Sky: Thor's Mjolnir Hammer with Lightning Wake & Stark Iron Legion Repulsor Drones
                if (g % 2 == 0) {
                    // Thor's Mjolnir Hammer: Chamfered Uru metal head, leather grip, wrist strap & crackling lightning trail
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.8f * sc));
                    paint.setColor(0xCC58E8FF);
                    c.drawLine(gx - dir * px(14f * sc), gy, gx - dir * px(36f * sc), gy + (float) Math.sin(t * 22f + g) * px(5f * sc), paint);
                    paint.setStrokeWidth(px(2.6f * sc));
                    paint.setColor(0xFFB87840); // Leather-wrapped handle
                    c.drawLine(gx, gy + px(5f * sc), gx - dir * px(15f * sc), gy + px(14f * sc), paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFD0DCE8); // Chamfered Uru Hammerhead
                    c.drawRoundRect(gx - px(10f * sc), gy - px(6.5f * sc), gx + px(10f * sc), gy + px(6.5f * sc), px(2.5f * sc), px(2.5f * sc), paint);
                    paint.setColor(0xFF48F0FF); // Glowing Asgardian rune core
                    c.drawCircle(gx, gy, px(2.6f * sc), paint);
                } else {
                    // Stark Iron Man Repulsor Drone: Hot-Rod Red & Gold Fuselage + Blazing Cyan Arc Reactor & Jet Wake
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(2.4f * sc));
                    paint.setColor(0xAA48F0FF);
                    c.drawLine(gx - dir * px(10f * sc), gy, gx - dir * px(32f * sc), gy, paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFE02028);
                    c.drawOval(rectF(gx - px(12f * sc), gy - px(5.5f * sc), gx + px(12f * sc), gy + px(5.5f * sc)), paint);
                    paint.setColor(0xFFFFC848);
                    c.drawCircle(gx + dir * px(5f * sc), gy, px(3.5f * sc), paint);
                    paint.setColor(0xFFFFFFFF);
                    c.drawCircle(gx, gy, px(2.8f * sc), paint);
                }
                continue;
            } else if (universeTheme == 5) {
                // DCU Top Sky: Gotham Batwing Jets with Twin Golden Afterburners & Green Lantern Willpower Constructs
                if (g % 2 == 0) {
                    // Gotham Batwing Jet Silhouette + Golden Afterburner Exhaust
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xCCFFE068);
                    c.drawCircle(gx - dir * px(12f * sc), gy, px(3.4f * sc), paint);
                    drawBird(c, gx, gy, t * 14f + g, px(21f * sc), 0xEE101828);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(1.4f * sc));
                    paint.setColor(0xAAFFE068);
                    c.drawArc(rectF(gx - px(14f * sc), gy - px(6f * sc), gx + px(14f * sc), gy + px(6f * sc)), 200, 140, false, paint);
                } else {
                    // Glowing Green Lantern Ring Construct & Golden Batarang
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(2.0f * sc));
                    paint.setColor(0xCC28FF78);
                    c.drawLine(gx - dir * px(8f * sc), gy, gx - dir * px(30f * sc), gy, paint);
                    c.drawCircle(gx, gy, px(6.5f * sc), paint);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(0xFFFFFFFF);
                    c.drawCircle(gx, gy, px(2.8f * sc), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                continue;
            } else if (universeTheme == 6) {
                // FORMULA 1 Top Sky: High-Speed Quadcopter Recording Camera Drones with Gimbal Spotlight & Blinking REC Indicator
                paint.setStyle(Paint.Style.FILL);
                // 1. Translucent downward camera recording spotlight cone from gimbal lens
                paint.setColor(0x1AC8F0FF);
                actorPath.reset();
                actorPath.moveTo(gx, gy + px(5f * sc));
                actorPath.lineTo(gx + dir * px(34f * sc), gy + px(36f * sc));
                actorPath.lineTo(gx + dir * px(6f * sc), gy + px(38f * sc));
                actorPath.close();
                c.drawPath(actorPath, paint);

                // 2. Carbon Quadcopter X-Frame Arms & 4 Motor Pods
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.0f * sc));
                paint.setColor(0xEE2A3446);
                c.drawLine(gx - px(16f * sc), gy - px(4f * sc), gx + px(16f * sc), gy - px(4f * sc), paint);
                c.drawLine(gx - px(11f * sc), gy - px(8f * sc), gx + px(11f * sc), gy + px(2f * sc), paint);
                c.drawLine(gx - px(11f * sc), gy + px(2f * sc), gx + px(11f * sc), gy - px(8f * sc), paint);

                // 3. 4 High-RPM Spinning Propeller Blur Discs at rotor tips
                float propSpan = px(7.5f * sc) * (0.55f + 0.45f * (float) Math.abs(Math.sin(t * 36f + g)));
                paint.setStrokeWidth(px(1.5f * sc));
                paint.setColor(0xBBD8F0FF);
                float[] rx = {gx - px(16f * sc), gx - px(9f * sc), gx + px(9f * sc), gx + px(16f * sc)};
                float[] ry = {gy - px(6.5f * sc), gy - px(9.5f * sc), gy - px(9.5f * sc), gy - px(6.5f * sc)};
                for (int r = 0; r < 4; r++) {
                    c.drawLine(rx[r] - propSpan, ry[r], rx[r] + propSpan, ry[r], paint);
                }

                // 4. Central Carbon Drone Body, Underslung 3-Axis Cinema Gimbal & Telephoto Lens
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0xFF18202C);
                c.drawRoundRect(gx - px(8f * sc), gy - px(5f * sc), gx + px(8f * sc), gy + px(2.5f * sc), px(3f * sc), px(3f * sc), paint);
                // Underslung gimbal camera pod
                paint.setColor(0xFF283244);
                c.drawCircle(gx + dir * px(2f * sc), gy + px(5.5f * sc), px(3.8f * sc), paint);
                paint.setColor(0xFF58E8FF);
                c.drawCircle(gx + dir * px(3.4f * sc), gy + px(6.0f * sc), px(1.8f * sc), paint);

                // 5. Blinking Red "REC ●" Recording Tally Light & Navigation LEDs
                boolean recBlink = (((int) (t * 4.5f + g)) % 2) == 0;
                if (recBlink) {
                    paint.setColor(0x55FF1801);
                    c.drawCircle(gx - dir * px(3.5f * sc), gy - px(1.5f * sc), px(5.5f * sc), paint);
                    paint.setColor(0xFFFF1801);
                    c.drawCircle(gx - dir * px(3.5f * sc), gy - px(1.5f * sc), px(2.2f * sc), paint);
                }
                paint.setColor(0xEE00E676);
                c.drawCircle(gx + dir * px(15f * sc), gy - px(4f * sc), px(1.6f * sc), paint);
                continue;
            } else if (universeTheme == 7) {
                // CYBERPUNK 2099 Top Sky: Trauma Team AV-4 Aerodynes & Blade Runner Spinner Cruisers with Neon Ion Trails
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.2f * sc));
                paint.setColor(g % 2 == 0 ? 0xAA00F0FF : 0xAAFF0055);
                c.drawLine(gx - dir * px(14f * sc), gy, gx - dir * px(42f * sc), gy, paint);
                // Downward scanning headlight cone
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0x1C00F0FF);
                actorPath.reset();
                actorPath.moveTo(gx + dir * px(10f * sc), gy + px(3f * sc));
                actorPath.lineTo(gx + dir * px(36f * sc), gy + px(28f * sc));
                actorPath.lineTo(gx + dir * px(4f * sc), gy + px(28f * sc));
                actorPath.close();
                c.drawPath(actorPath, paint);
                // Sleek armored Trauma Team / Spinner aerodyne hull
                paint.setColor(g % 2 == 0 ? 0xFFF0F4FA : 0xFF182032);
                c.drawRoundRect(gx - px(14f * sc), gy - px(5f * sc), gx + px(14f * sc), gy + px(5f * sc), px(3f * sc), px(3f * sc), paint);
                // Flashing Red/Cyan Trauma Team Emergency Strobe
                boolean strobe = (((int) (t * 6f + g)) % 2) == 0;
                paint.setColor(strobe ? 0xFFFF003C : 0xFF00F0FF);
                c.drawCircle(gx + dir * px(6f * sc), gy - px(5.5f * sc), px(2.4f * sc), paint);
                paint.setColor(0xFFFCEE09);
                c.drawCircle(gx - dir * px(12f * sc), gy, px(2.6f * sc), paint);
                continue;
            }
            float shimmer = 0.72f + 0.28f * (float) Math.sin(t * 3.1f + g * 1.3f);
            int auraAlpha = Math.round(18f * shimmer);
            int bodyAlpha = Math.round(64f * shimmer);
            int coreAlpha = Math.round(112f * shimmer);

            // Soft pearly-cyan ectoplasmic aura (genuinely translucent!)
            paint.setStyle(Paint.Style.FILL);
            paint.setColor((auraAlpha << 24) | 0x0088E4FF);
            c.drawCircle(gx, gy, px(22f * sc), paint);

            // Trailing ectoplasmic wisps behind the ghost
            for (int wisp = 1; wisp <= 3; wisp++) {
                float wx = gx - dir * px(13f * wisp * sc);
                float wy = gy + px(5f * wisp * sc) + (float) Math.sin(t * 5f + wisp + g) * px(4f);
                int wa = Math.max(8, bodyAlpha - wisp * 18);
                paint.setColor((wa << 24) | 0x00B8F2FF);
                c.drawCircle(wx, wy, px((5.5f - wisp * 1.2f) * sc), paint);
            }

            // Sculpted billowing spectral robe with scalloped multi-fold hem & flowing sleeves
            float wave = (float) Math.sin(t * 6.5f + g) * px(5.5f * sc);
            paint.setColor((bodyAlpha << 24) | 0x00C8F4FF);
            actorPath.reset();
            actorPath.moveTo(gx + dir * px(5f * sc), gy - px(8f * sc));
            actorPath.cubicTo(
                    gx + dir * px(14f * sc), gy + px(2f * sc),
                    gx + dir * px(4f * sc), gy + px(16f * sc),
                    gx - dir * px(8f * sc), gy + px(23f * sc) + wave * 0.4f);
            actorPath.quadTo(gx - dir * px(16f * sc), gy + px(17f * sc), gx - dir * px(23f * sc), gy + px(25f * sc) + wave);
            actorPath.quadTo(gx - dir * px(29f * sc), gy + px(18f * sc), gx - dir * px(36f * sc), gy + px(21f * sc) - wave);
            actorPath.cubicTo(
                    gx - dir * px(24f * sc), gy + px(9f * sc),
                    gx - dir * px(14f * sc), gy - px(2f * sc),
                    gx - dir * px(5f * sc), gy - px(8f * sc));
            actorPath.close();
            c.drawPath(actorPath, paint);

            // Outstretched translucent spectral arm & draped cuff
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.2f * sc));
            paint.setColor((coreAlpha << 24) | 0x00DCF8FF);
            c.drawLine(gx + dir * px(3f * sc), gy - px(4f * sc), gx + dir * px(14f * sc), gy - px(7f * sc) + wave * 0.25f, paint);
            paint.setStyle(Paint.Style.FILL);

            // Sculpted Ghost Head & Coiffure
            paint.setColor((coreAlpha << 24) | 0x00E8FCFF);
            float headTiltX = (type == 0) ? dir * px(3.8f * (float) Math.sin(t * 2.5f)) : 0f;
            c.drawCircle(gx + headTiltX, gy - px(14f * sc), px(5.8f * sc), paint);

            // Character-specific details (Nick's ruff & plumed hat, Grey Lady's diadem & hair, Baron's chains)
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(1.5f * sc));
            paint.setColor((coreAlpha << 24) | 0x00FFFFFF);
            if (type == 0) {
                // Nearly Headless Nick: Elizabethan ruffled collar + wide-brimmed plumed Tudor hat
                c.drawOval(gx - px(8f * sc), gy - px(9.5f * sc), gx + px(8f * sc), gy - px(5.5f * sc), paint);
                c.drawLine(gx - px(9f * sc) + headTiltX, gy - px(19f * sc), gx + px(9f * sc) + headTiltX, gy - px(19f * sc), paint);
                c.drawLine(gx + headTiltX, gy - px(19f * sc), gx - dir * px(9f * sc) + headTiltX, gy - px(25f * sc), paint);
            } else if (type == 1) {
                // The Grey Lady: Ravenclaw diadem crown & flowing hair waves
                c.drawLine(gx - px(5f * sc), gy - px(19.5f * sc), gx, gy - px(25f * sc), paint);
                c.drawLine(gx, gy - px(25f * sc), gx + px(5f * sc), gy - px(19.5f * sc), paint);
                c.drawLine(gx - dir * px(4f * sc), gy - px(12f * sc), gx - dir * px(15f * sc), gy + px(2f * sc) + wave * 0.3f, paint);
            } else if (type == 3) {
                // The Bloody Baron: trailing spectral chains
                c.drawLine(gx - dir * px(4f * sc), gy + px(2f * sc), gx - dir * px(18f * sc), gy + px(14f * sc) + wave * 0.5f, paint);
                c.drawCircle(gx - dir * px(11f * sc), gy + px(8f * sc) + wave * 0.25f, px(2.2f * sc), paint);
            }
            paint.setStyle(Paint.Style.FILL);
        }
    }

    /**
     * Draws the 24-Scene Harry Potter Saga Loop across the expanded bottom theater stage (y = 576..1080)
     * in a golden-ink & illuminated storybook style (zero per-frame object allocations).
     */
    private void drawStoryLoop(Canvas c, int w, int h, float rawT) {
        // Adjustable animation speed for all bottom story animations
        float t = rawT * 0.5f * storySpeedMultiplier;
        if (universeTheme == 1) {
            drawNarutoStoryLoop(c, w, h, t);
            return;
        } else if (universeTheme == 2) {
            drawDemonSlayerStoryLoop(c, w, h, t);
            return;
        } else if (universeTheme == 3) {
            drawStarWarsStoryLoop(c, w, h, t);
            return;
        } else if (universeTheme == 4) {
            drawMcuStoryLoop(c, w, h, t);
            return;
        } else if (universeTheme == 5) {
            drawDcuStoryLoop(c, w, h, t);
            return;
        } else if (universeTheme == 6) {
            drawF1StoryLoop(c, w, h, t);
            return;
        } else if (universeTheme == 7) {
            drawCyberpunkStoryLoop(c, w, h, t);
            return;
        }
        float totalCycle = CHAPTER_SEC * SCENE_COUNT;
        float cycleTime = t % totalCycle;
        int scene = resolveActiveScene(cycleTime, SCENE_COUNT);
        float p = (cycleTime % CHAPTER_SEC) / CHAPTER_SEC; // 0.0 -> 1.0 progress within current scene
        float fade = resolveStoryFade(p);
        int aBase = Math.min(255, Math.max(0, Math.round(fade * 255f)));

        float groundY = h * 0.900f;
        drawStoryCaptionAndDots(c, w, h, CHAPTER_TITLES[scene], scene, SCENE_COUNT, aBase,
                0x00F5D488, 0xEEFFE082, 0x42F5D078, px(7.8f));

        // Scale the bottom story animations anchored on the stage groundY baseline
        c.save();
        applyStageTransform(c, w, h, groundY);

        switch (scene) {
            case 0: {
                // SCENE 1: Privet Drive — Dumbledore & The Deluminator + McGonagall Cat
                float dumbX = w * 0.34f, lampX = w * 0.62f;
                drawWizardStanding(c, dumbX, groundY - px(68f), t, aBase, 0xFFFFF0C8, true, true, 1.55f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.5f));
                paint.setColor((aBase << 24) | 0x00D8B468);
                c.drawLine(lampX, groundY, lampX, groundY - px(135f), paint);
                paint.setStyle(Paint.Style.FILL);
                c.drawCircle(lampX, groundY - px(140f), px(12f * (1f - p * 0.6f)), paint);
                for (int o = 0; o < 8; o++) {
                    float op = (p * 1.4f + o * 0.12f) % 1.0f;
                    float ox = lampX + (dumbX + px(38f) - lampX) * op;
                    float oy = (groundY - px(140f)) + ((groundY - px(98f)) - (groundY - px(140f))) * op
                            - (float) Math.sin(op * Math.PI) * px(42f);
                    paint.setColor((aBase << 24) | 0x00FFE888);
                    c.drawCircle(ox, oy, px(6.5f * (1f - op * 0.3f)), paint);
                }
                drawCatSilhouette(c, w * 0.22f, groundY - px(18f), aBase);
                break;
            }
            case 1: {
                // SCENE 2: Hagrid's Flying Motorbike across the Surrey Night Sky
                float bikeX = w * (0.18f + p * 0.50f);
                float bikeY = h * 0.72f + (float) Math.sin(t * 4.2f) * px(18f);
                paint.setColor((Math.round(aBase * 0.32f) << 24) | 0x00FFF0A0);
                actorPath.reset();
                actorPath.moveTo(bikeX + px(42f), bikeY);
                actorPath.lineTo(bikeX + px(195f), bikeY - px(38f));
                actorPath.lineTo(bikeX + px(195f), bikeY + px(42f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.8f));
                paint.setColor((aBase << 24) | 0x00FFE082);
                c.drawCircle(bikeX - px(32f), bikeY + px(18f), px(18f), paint);
                c.drawCircle(bikeX + px(34f), bikeY + px(18f), px(18f), paint);
                c.drawLine(bikeX - px(32f), bikeY + px(18f), bikeX + px(12f), bikeY - px(12f), paint);
                c.drawLine(bikeX + px(34f), bikeY + px(18f), bikeX + px(12f), bikeY - px(12f), paint);
                paint.setStyle(Paint.Style.FILL);
                c.drawCircle(bikeX - px(6f), bikeY - px(38f), px(14f), paint);
                c.drawRoundRect(bikeX - px(24f), bikeY - px(24f), bikeX + px(12f), bikeY + px(12f), px(8f), px(8f), paint);
                break;
            }
            case 2: {
                // SCENE 3 (NEW): The Reptile House — Vanishing Glass & Giant Boa Constrictor
                float hx = w * 0.30f;
                drawWizardStanding(c, hx, groundY - px(62f), t, aBase, 0xFFFFE498, false, false, 1.45f);
                // Shimmering vanishing glass enclosure frame
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.5f));
                int glassAlpha = Math.round(aBase * (1f - p * 0.85f));
                paint.setColor((glassAlpha << 24) | 0x0098F0FF);
                c.drawRect(w * 0.44f, groundY - px(130f), w * 0.64f, groundY, paint);
                // Boa Constrictor slithering out of the terrarium & nodding "Brazil, here I come"
                drawSerpent(c, w * (0.52f + p * 0.08f), groundY, t, aBase, 0xFF58A860, 0xFFC8F0A8, 1.25f, true);
                break;
            }
            case 3: {
                // SCENE 4: The Swarm of Hogwarts Letters & Owls at Number Four
                float harryX = w * 0.50f;
                drawWizardStanding(c, harryX, groundY - px(62f), t, aBase, 0xFFFFE498, false, false, 1.45f);
                for (int l = 0; l < 18; l++) {
                    float lp = ((t * 0.65f + l * 0.055f) % 1.0f);
                    float lx = w * 0.18f + lp * (w * 0.52f);
                    float ly = groundY - px(35f) - (float) Math.sin(lp * Math.PI * 2.2f + l) * px(110f) - lp * px(85f);
                    paint.setColor((aBase << 24) | 0x00FFF6E0);
                    c.drawRoundRect(lx - px(12f), ly - px(8f), lx + px(12f), ly + px(8f), px(2f), px(2f), paint);
                    paint.setColor((aBase << 24) | 0x00D82828);
                    c.drawCircle(lx, ly, px(2.8f), paint);
                }
                drawBird(c, w * 0.35f, h * 0.70f, t * 8f, px(28f), (aBase << 24) | 0x00FFF8E8);
                drawBird(c, w * 0.62f, h * 0.68f, t * 9f + 1.2f, px(26f), (aBase << 24) | 0x00FFE098);
                break;
            }
            case 4: {
                // SCENE 5 (NEW): Diagon Alley — Hagrid Tapping the Brick Wall Archway Open
                float hagX = w * 0.30f;
                drawWizardStanding(c, hagX, groundY - px(78f), t, aBase, 0xFFFFD888, true, true, 1.75f);
                drawWizardStanding(c, hagX - px(65f), groundY - px(58f), t + 1f, aBase, 0xFFFFE898, false, false, 1.30f);
                // Expanding golden brick archway parting open
                float archX = w * 0.52f, openR = px(28f + p * 58f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(5.5f));
                paint.setColor((aBase << 24) | 0x00E09A58);
                for (int b = 0; b < 12; b++) {
                    double ang = Math.PI + (b / 11.0) * Math.PI;
                    float bx = archX + (float) Math.cos(ang) * openR;
                    float by = groundY - px(60f) + (float) Math.sin(ang) * openR;
                    c.drawRect(bx - px(9f), by - px(5f), bx + px(9f), by + px(5f), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 5: {
                // SCENE 6 (NEW): Gringotts Wizarding Bank — Plunging Subterranean Mine Cart
                float cartX = w * (0.20f + p * 0.46f);
                float cartY = groundY - px(105f) + p * px(75f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.5f));
                paint.setColor((aBase << 24) | 0x00B89868);
                c.drawLine(w * 0.14f, groundY - px(120f), w * 0.72f, groundY - px(12f), paint);
                // Mine cart & passengers
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00FFE082);
                c.drawRoundRect(cartX - px(32f), cartY - px(24f), cartX + px(32f), cartY, px(5f), px(5f), paint);
                c.drawCircle(cartX - px(18f), cartY + px(5f), px(6f), paint);
                c.drawCircle(cartX + px(18f), cartY + px(5f), px(6f), paint);
                c.drawCircle(cartX - px(10f), cartY - px(32f), px(8f), paint);
                c.drawCircle(cartX + px(10f), cartY - px(30f), px(6.5f), paint);
                break;
            }
            case 6: {
                // SCENE 7: Ollivanders — The Wand Chooses the Wizard
                float hx = w * 0.48f, hy = groundY - px(66f);
                drawWizardStanding(c, hx, hy, t, aBase, 0xFFFFE898, true, false, 1.55f);
                drawWizardStanding(c, hx - px(115f), groundY - px(68f), t + 1f, aBase * 4 / 5, 0xFFD8B878, false, false, 1.50f);
                float wx = hx + px(40f), wy = hy - px(30f);
                for (int r = 0; r < 24; r++) {
                    float ang = t * 5.0f + r * 0.26f;
                    float rad = px(18f + (r * 7.5f));
                    float sx = wx + (float) Math.cos(ang) * rad;
                    float sy = wy + (float) Math.sin(ang) * (rad * 0.65f);
                    paint.setColor((aBase << 24) | (r % 2 == 0 ? 0x00FFE070 : 0x00FF5830));
                    c.drawCircle(sx, sy, px(4.5f), paint);
                }
                break;
            }
            case 7: {
                // SCENE 8: Platform 9 3/4 — The Hogwarts Express Crossing the Viaduct
                drawHogwartsExpressScene(c, w, h, groundY, t, p, aBase);
                break;
            }
            case 8: {
                // SCENE 9: First-Year Lantern Boats Gliding Across the Black Lake
                for (int b = 0; b < 5; b++) {
                    float bx = w * (0.20f + b * 0.085f + p * 0.16f);
                    float by = groundY - px(6f) + (float) Math.sin(t * 3.2f + b) * px(3.5f);
                    paint.setColor((aBase << 24) | 0x00E8C468);
                    actorPath.reset();
                    actorPath.moveTo(bx - px(28f), by - px(8f));
                    actorPath.lineTo(bx + px(28f), by - px(8f));
                    actorPath.lineTo(bx + px(19f), by + px(6f));
                    actorPath.lineTo(bx - px(19f), by + px(6f));
                    actorPath.close();
                    c.drawPath(actorPath, paint);
                    c.drawCircle(bx - px(8f), by - px(15f), px(5.5f), paint);
                    c.drawCircle(bx + px(4f), by - px(15f), px(5.5f), paint);
                    paint.setColor((aBase << 24) | 0x00FFF6C0);
                    c.drawCircle(bx + px(22f), by - px(16f), px(6.0f), paint);
                    paint.setColor((aBase / 3 << 24) | 0x00FFE082);
                    c.drawOval(bx + px(10f), by + px(8f), bx + px(34f), by + px(14f), paint);
                }
                break;
            }
            case 9: {
                // SCENE 10: The Great Hall — The Sorting Hat Ceremony
                float cx = w * 0.48f, sy = groundY - px(45f);
                paint.setColor((aBase << 24) | 0x00FFE498);
                c.drawRect(cx - px(18f), sy, cx + px(18f), sy + px(45f), paint);
                c.drawCircle(cx, sy - px(26f), px(11f), paint);
                float hatY = sy - px(44f) + (float) Math.sin(t * 6f) * px(4f);
                actorPath.reset();
                actorPath.moveTo(cx - px(38f), hatY);
                actorPath.lineTo(cx + px(38f), hatY);
                actorPath.lineTo(cx + px(8f), hatY - px(56f));
                actorPath.lineTo(cx - px(16f), hatY - px(64f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                int[] hCols = {0xFFE83038, 0xFF18C878, 0xFF3282FF, 0xFFFFB020};
                for (int hb = 0; hb < 4; hb++) {
                    float bx = w * (0.26f + hb * 0.145f);
                    float by = h * 0.68f + (float) Math.sin(t * 3f + hb) * px(6f);
                    paint.setColor((aBase * 4 / 5 << 24) | (hCols[hb] & 0xFFFFFF));
                    c.drawRoundRect(bx - px(20f), by, bx + px(20f), by + px(68f), px(4f), px(4f), paint);
                }
                break;
            }
            case 10: {
                // SCENE 11 (NEW): Flying Lesson — Harry's Steep Broom Dive for Neville's Remembrall
                float rx = w * (0.26f + p * 0.36f);
                float ry = h * (0.68f + p * 0.14f);
                drawBroomRider(c, rx, ry, t, aBase, 0xFFE83838, 0xFFFFE082, true, 1.50f);
                // Glowing crimson Remembrall glass sphere ahead of Harry's outstretched hand
                float orbX = rx + px(72f), orbY = ry - px(16f) + (float) Math.sin(t * 8f) * px(8f);
                paint.setColor((aBase / 3 << 24) | 0x00FF2828);
                c.drawCircle(orbX, orbY, px(18f), paint);
                paint.setColor((aBase << 24) | 0x00FF4040);
                c.drawCircle(orbX, orbY, px(8.5f), paint);
                break;
            }
            case 11: {
                // SCENE 12: Wingardium Leviosa Feather & The Mountain Troll
                float hermX = w * 0.30f;
                drawWizardStanding(c, hermX, groundY - px(64f), t, aBase, 0xFFFFE498, true, false, 1.50f);
                float fx = hermX + px(85f), fy = groundY - px(115f) + (float) Math.sin(t * 4f) * px(18f);
                paint.setColor((aBase << 24) | 0x00FFFFFF);
                c.drawOval(fx - px(22f), fy - px(7f), fx + px(22f), fy + px(7f), paint);
                float tx = w * 0.58f;
                paint.setColor((aBase << 24) | 0x00889878);
                c.drawRoundRect(tx - px(38f), groundY - px(120f), tx + px(38f), groundY, px(18f), px(18f), paint);
                c.drawCircle(tx, groundY - px(136f), px(20f), paint);
                float clubY = groundY - px(185f) + (float) Math.sin(t * 7f) * px(10f);
                paint.setColor((aBase << 24) | 0x00D89848);
                c.drawRoundRect(tx - px(32f), clubY - px(10f), tx + px(32f), clubY + px(10f), px(8f), px(8f), paint);
                break;
            }
            case 12: {
                // SCENE 13: Quidditch Pitch — The Golden Snitch Chase
                drawQuidditchScene(c, w, h, groundY, t, p, aBase);
                break;
            }
            case 13: {
                // SCENE 14 (NEW): The Third Floor — Fluffy the Three-Headed Dog & Enchanted Harp
                float fx = w * 0.52f;
                paint.setColor((aBase << 24) | 0x00C89860);
                c.drawRoundRect(fx - px(48f), groundY - px(82f), fx + px(48f), groundY, px(16f), px(16f), paint);
                // 3 ferocious Cerberus heads
                for (int hd = -1; hd <= 1; hd++) {
                    float hx = fx + hd * px(28f);
                    float hy = groundY - px(98f) + (float) Math.sin(t * 4f + hd) * px(6f);
                    c.drawCircle(hx, hy, px(16f), paint);
                    c.drawCircle(hx - px(10f), hy - px(12f), px(6f), paint);
                }
                // Self-playing Golden Harp with floating musical notes
                float harpX = w * 0.30f;
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.5f));
                paint.setColor((aBase << 24) | 0x00FFE082);
                c.drawLine(harpX, groundY, harpX, groundY - px(78f), paint);
                c.drawLine(harpX, groundY, harpX + px(38f), groundY - px(58f), paint);
                c.drawLine(harpX, groundY - px(78f), harpX + px(38f), groundY - px(58f), paint);
                paint.setStyle(Paint.Style.FILL);
                for (int n = 0; n < 5; n++) {
                    float np = ((t * 0.8f + n * 0.2f) % 1.0f);
                    c.drawCircle(harpX + px(35f) + np * px(95f), groundY - px(55f) - np * px(50f), px(4.5f), paint);
                }
                break;
            }
            case 14: {
                // SCENE 15 (NEW): Underground Chambers — Devil's Snare & Hermione's Lumos Solem Sunburst
                float hermX = w * 0.32f;
                drawWizardStanding(c, hermX, groundY - px(64f), t, aBase, 0xFFFFE498, true, false, 1.50f);
                // Blazing Lumos Solem solar flare
                paint.setColor((aBase / 3 << 24) | 0x00FFF088);
                c.drawCircle(hermX + px(48f), groundY - px(95f), px(42f + p * 35f), paint);
                // Writhing Devil's Snare vines recoiling on the right
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(6.0f));
                paint.setColor((aBase << 24) | 0x00286848);
                for (int v = 0; v < 8; v++) {
                    float vx = w * (0.48f + v * 0.03f + p * 0.06f);
                    float sway = (float) Math.sin(t * 6f + v) * px(22f);
                    actorPath.reset();
                    actorPath.moveTo(vx, groundY);
                    actorPath.quadTo(vx - sway, groundY - px(65f), vx + sway, groundY - px(130f));
                    c.drawPath(actorPath, paint);
                }
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 15: {
                // SCENE 16 (NEW): Underground Chambers — Swarm of the Enchanted Winged Keys
                float hx = w * (0.28f + p * 0.30f);
                float hy = h * 0.75f + (float) Math.sin(t * 5f) * px(18f);
                drawBroomRider(c, hx, hy, t, aBase, 0xFFE83838, 0xFFFFE082, true, 1.45f);
                for (int k = 0; k < 20; k++) {
                    float kx = w * 0.22f + ((k * 37 + t * 90f) % (w * 0.52f));
                    float ky = h * 0.65f + (float) Math.sin(t * 6f + k) * px(55f);
                    paint.setColor((aBase << 24) | (k == 0 ? 0x0088F4FF : 0x00FFD870));
                    c.drawCircle(kx, ky, k == 0 ? px(7f) : px(4f), paint);
                    drawBird(c, kx, ky - px(3f), t * 18f + k, k == 0 ? px(16f) : px(9f), (aBase << 24) | 0x00FFFFFF);
                }
                break;
            }
            case 16: {
                // SCENE 17: Underground Chambers — Giant Wizard's Chess Knight
                float kx = w * (0.34f + p * 0.18f);
                paint.setColor((aBase << 24) | 0x00D8C8A8);
                c.drawRect(kx - px(36f), groundY - px(24f), kx + px(36f), groundY, paint);
                actorPath.reset();
                actorPath.moveTo(kx - px(28f), groundY - px(24f));
                actorPath.lineTo(kx - px(18f), groundY - px(138f));
                actorPath.lineTo(kx + px(26f), groundY - px(118f));
                actorPath.lineTo(kx + px(8f), groundY - px(95f));
                actorPath.lineTo(kx + px(28f), groundY - px(24f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(4.5f));
                paint.setColor((aBase << 24) | 0x00FFE890);
                c.drawLine(kx + px(15f), groundY - px(80f), kx + px(85f), groundY - px(145f), paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 17: {
                // SCENE 18 (NEW): The Mirror of Erised & The Glowing Philosopher's Stone
                float mx = w * 0.52f;
                // Ornate Gothic Mirror of Erised Arch
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(5.0f));
                paint.setColor((aBase << 24) | 0x00FFD878);
                c.drawRoundRect(mx - px(38f), groundY - px(155f), mx + px(38f), groundY, px(38f), px(38f), paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase / 3 << 24) | 0x0098E8FF);
                c.drawRoundRect(mx - px(32f), groundY - px(148f), mx + px(32f), groundY - px(6f), px(32f), px(32f), paint);
                // Harry gazing into the Mirror as the crimson Philosopher's Stone glows
                drawWizardStanding(c, mx - px(95f), groundY - px(62f), t, aBase, 0xFFFFE498, false, false, 1.45f);
                paint.setColor((aBase << 24) | 0x00FF2038);
                c.drawCircle(mx - px(58f), groundY - px(85f) + (float) Math.sin(t * 5f) * px(6f), px(8.5f), paint);
                break;
            }
            case 18: {
                // SCENE 19: The Flying Turquoise Ford Anglia Soaring Through the Clouds
                float carX = w * (0.20f + p * 0.52f);
                float carY = h * 0.72f + (float) Math.sin(t * 3.8f) * px(20f);
                paint.setColor((Math.round(aBase * 0.30f) << 24) | 0x00FFF4A8);
                actorPath.reset();
                actorPath.moveTo(carX + px(54f), carY);
                actorPath.lineTo(carX + px(190f), carY - px(32f));
                actorPath.lineTo(carX + px(190f), carY + px(36f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                paint.setColor((aBase << 24) | 0x0048C4D8);
                c.drawRoundRect(carX - px(52f), carY - px(12f), carX + px(54f), carY + px(18f), px(10f), px(10f), paint);
                c.drawRoundRect(carX - px(28f), carY - px(34f), carX + px(26f), carY - px(8f), px(8f), px(8f), paint);
                paint.setColor((aBase << 24) | 0x00FFE898);
                c.drawRect(carX - px(20f), carY - px(28f), carX + px(18f), carY - px(12f), paint);
                c.drawCircle(carX - px(32f), carY + px(20f), px(11f), paint);
                c.drawCircle(carX + px(32f), carY + px(20f), px(11f), paint);
                drawBird(c, carX + px(78f), carY - px(36f), t * 9f, px(24f), (aBase << 24) | 0x00FFF8E8);
                break;
            }
            case 19: {
                // SCENE 20 (NEW): Hogwarts Grounds — The Whomping Willow Thrashing Its Branches
                float wx = w * 0.50f;
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00987448);
                actorPath.reset();
                actorPath.moveTo(wx - px(28f), groundY);
                actorPath.lineTo(wx - px(16f), groundY - px(110f));
                actorPath.lineTo(wx + px(18f), groundY - px(110f));
                actorPath.lineTo(wx + px(30f), groundY);
                actorPath.close();
                c.drawPath(actorPath, paint);
                // 6 whipping gnarled branches
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(5.5f));
                for (int b = 0; b < 6; b++) {
                    float ang = -2.6f + b * 0.52f + (float) Math.sin(t * 7f + b) * 0.35f;
                    float bx = wx + (float) Math.cos(ang) * px(125f);
                    float by = groundY - px(105f) + (float) Math.sin(ang) * px(85f);
                    c.drawLine(wx, groundY - px(105f), bx, by, paint);
                }
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 20: {
                // SCENE 21 (NEW): Greenhouse Three — Crying Baby Mandrakes in Terracotta Pots
                for (int m = 0; m < 4; m++) {
                    float mx = w * (0.28f + m * 0.11f);
                    float pull = (float) Math.abs(Math.sin(t * 5f + m)) * px(28f);
                    paint.setColor((aBase << 24) | 0x00D87848);
                    c.drawRect(mx - px(18f), groundY - px(38f), mx + px(18f), groundY, paint);
                    // Screaming root body & green leaves
                    paint.setColor((aBase << 24) | 0x00C8A468);
                    c.drawCircle(mx, groundY - px(48f) - pull, px(11f), paint);
                    paint.setColor((aBase << 24) | 0x0048C868);
                    c.drawOval(mx - px(16f), groundY - px(74f) - pull, mx + px(16f), groundY - px(58f) - pull, paint);
                }
                break;
            }
            case 21: {
                // SCENE 22 (NEW): Duelling Club — Serpensortia Viper & Harry Speaking Parseltongue
                float hx = w * 0.28f, dracoX = w * 0.64f;
                drawWizardStanding(c, hx, groundY - px(64f), t, aBase, 0xFFFFE498, true, false, 1.50f);
                drawWizardStanding(c, dracoX, groundY - px(64f), t + 1f, aBase, 0xFF98E0B8, false, false, 1.50f);
                drawSerpent(c, w * 0.46f, groundY, t, aBase, 0xFF288858, 0xFF98F0B8, 1.15f, true);
                break;
            }
            case 22: {
                // SCENE 23 (NEW): Forbidden Forest — Aragog & The Acromantula Spider Swarm
                float ax = w * 0.52f, ay = groundY - px(48f);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00685848);
                c.drawOval(ax - px(38f), ay - px(24f), ax + px(38f), ay + px(22f), paint);
                c.drawCircle(ax - px(44f), ay, px(16f), paint);
                // 8 articulated spider legs
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.5f));
                for (int l = -2; l <= 2; l++) {
                    if (l == 0) continue;
                    float legLift = (float) Math.sin(t * 8f + l) * px(10f);
                    c.drawLine(ax + l * px(12f), ay, ax + l * px(28f), ay - px(36f) + legLift, paint);
                    c.drawLine(ax + l * px(28f), ay - px(36f) + legLift, ax + l * px(44f), groundY, paint);
                }
                // Descending web spiders from above
                paint.setStrokeWidth(px(1.5f));
                paint.setColor((aBase * 2 / 3 << 24) | 0x00D8E8F8);
                for (int s = 0; s < 5; s++) {
                    float sx = w * (0.26f + s * 0.08f);
                    float sy = h * 0.66f + (float) Math.abs(Math.sin(t * 3f + s)) * px(75f);
                    c.drawLine(sx, h * 0.62f, sx, sy, paint);
                    c.drawCircle(sx, sy, px(6f), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 23: {
                // SCENE 24: Chamber of Secrets — Harry with Sword of Gryffindor vs Giant Horned Basilisk
                float hx = w * 0.30f, hy = groundY - px(64f);
                drawWizardStanding(c, hx, hy, t, aBase, 0xFFFFE498, false, false, 1.55f);
                float hiltX = hx + px(28f), hiltY = hy - px(24f);
                float tipX = hx + px(84f), tipY = hy - px(92f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.6f));
                paint.setColor((aBase << 24) | 0x00FFD758);
                c.drawLine(hiltX - px(9f), hiltY - px(7f), hiltX + px(11f), hiltY + px(9f), paint);
                paint.setStrokeWidth(px(4.8f));
                paint.setColor((aBase << 24) | 0x00E8F4FF);
                c.drawLine(hiltX + px(2f), hiltY - px(2f), tipX, tipY, paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00FF2438);
                c.drawCircle(hiltX - px(5f), hiltY + px(5f), px(4.2f), paint);
                drawSerpent(c, w * 0.60f, groundY, t, aBase, 0xFF228B58, 0xFF88E8A8, 1.65f, true);
                drawBird(c, w * 0.45f, h * 0.66f, t * 8f, px(36f), (aBase << 24) | 0x00FF5828);
                break;
            }
            case 24: {
                // SCENE 25 (NEW): Chamber of Secrets — Destroying Tom Riddle's Diary with the Basilisk Fang
                float dx = w * 0.46f, dy = groundY - px(16f);
                paint.setColor((aBase << 24) | 0x00FFF0D0);
                c.drawRoundRect(dx - px(42f), dy - px(14f), dx + px(42f), dy, px(4f), px(4f), paint);
                // Curved ivory Basilisk fang plunging down into the diary
                float fangY = dy - px(52f) + (float) Math.abs(Math.sin(t * 5f)) * px(36f);
                actorPath.reset();
                actorPath.moveTo(dx - px(10f), fangY - px(48f));
                actorPath.quadTo(dx + px(14f), fangY - px(20f), dx, fangY);
                actorPath.close();
                c.drawPath(actorPath, paint);
                // Erupting golden-emerald Horcrux light rays
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                for (int r = 0; r < 9; r++) {
                    double ang = -Math.PI * 0.85 + (r / 8.0) * Math.PI * 0.7;
                    paint.setColor((aBase << 24) | (r % 2 == 0 ? 0x0038FF98 : 0x00FFE070));
                    c.drawLine(dx, dy - px(8f), dx + (float) Math.cos(ang) * px(120f * p), dy - px(8f) + (float) Math.sin(ang) * px(120f * p), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 25: {
                // SCENE 26: Dobby Is Free — House-Elf Silhouette, Floating Sock & Shockwave Blasting Lucius Malfoy
                float dobX = w * 0.35f, lucX = w * (0.54f + p * 0.14f);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00FFE498);
                c.drawCircle(dobX, groundY - px(54f), px(8.5f), paint);
                actorPath.reset();
                actorPath.moveTo(dobX - px(6f), groundY - px(56f));
                actorPath.quadTo(dobX - px(24f), groundY - px(64f), dobX - px(28f), groundY - px(50f));
                actorPath.quadTo(dobX - px(16f), groundY - px(48f), dobX - px(5f), groundY - px(51f));
                actorPath.moveTo(dobX + px(6f), groundY - px(56f));
                actorPath.quadTo(dobX + px(20f), groundY - px(65f), dobX + px(24f), groundY - px(52f));
                actorPath.quadTo(dobX + px(14f), groundY - px(48f), dobX + px(5f), groundY - px(51f));
                c.drawPath(actorPath, paint);
                c.drawRect(dobX - px(8f), groundY - px(44f), dobX + px(8f), groundY - px(16f), paint);
                paint.setColor((aBase << 24) | 0x00FFF8DC);
                c.drawRoundRect(dobX + px(16f), groundY - px(74f), dobX + px(26f), groundY - px(58f), px(3f), px(3f), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase << 24) | 0x0098F0FF);
                c.drawCircle(dobX + px(48f) + p * px(120f), groundY - px(45f), px(34f + p * 45f), paint);
                paint.setStyle(Paint.Style.FILL);
                drawWizardStanding(c, lucX, groundY - px(66f), t, aBase, 0xFF98C8B0, false, false, 1.55f);
                break;
            }
            case 26: {
                // SCENE 27 (NEW): Magnolia Crescent — Inflated Aunt Marge Floating into the Night Sky
                drawWizardStanding(c, w * 0.30f, groundY - px(62f), t, aBase, 0xFFFFE498, false, false, 1.45f);
                float mx = w * (0.44f + p * 0.18f);
                float my = groundY - px(75f) - p * px(110f) + (float) Math.sin(t * 4f) * px(10f);
                paint.setColor((aBase << 24) | 0x00E8B898);
                c.drawCircle(mx, my, px(42f), paint);
                c.drawCircle(mx, my - px(46f), px(12f), paint);
                break;
            }
            case 27: {
                // SCENE 28: The Triple-Decker Purple Knight Bus Rocketing Across the Night
                float busX = w * (0.16f + p * 0.56f);
                float squeeze = 1.0f - 0.35f * (float) Math.abs(Math.sin(t * 5f));
                float bw = px(92f * squeeze), bh = px(142f);
                paint.setColor((aBase << 24) | 0x007838C8);
                c.drawRoundRect(busX - bw, groundY - bh, busX + bw, groundY - px(10f), px(10f), px(10f), paint);
                paint.setColor((aBase << 24) | 0x00FFE888);
                for (int deck = 0; deck < 3; deck++) {
                    float wy = groundY - bh + px(14f + deck * 42f);
                    for (int win = -2; win <= 2; win++) {
                        float wx = busX + win * (bw * 0.34f);
                        c.drawRect(wx - px(8f * squeeze), wy, wx + px(8f * squeeze), wy + px(22f), paint);
                    }
                }
                c.drawCircle(busX - bw * 0.65f, groundY - px(8f), px(12f), paint);
                c.drawCircle(busX + bw * 0.65f, groundY - px(8f), px(12f), paint);
                break;
            }
            case 28: {
                // SCENE 29: Flight of Buckbeak the Hippogriff Skimming Over the Black Lake
                float bx = w * (0.24f + p * 0.44f);
                float by = groundY - px(46f) + (float) Math.sin(t * 4.2f) * px(15f);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00D8E8F8);
                actorPath.reset();
                actorPath.moveTo(bx - px(38f), by + px(4f));
                actorPath.quadTo(bx - px(10f), by - px(16f), bx + px(26f), by - px(8f));
                actorPath.quadTo(bx + px(42f), by - px(28f), bx + px(54f), by - px(22f));
                actorPath.lineTo(bx + px(64f), by - px(16f));
                actorPath.lineTo(bx + px(50f), by - px(12f));
                actorPath.quadTo(bx + px(32f), by + px(12f), bx - px(32f), by + px(14f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                float flap = (float) Math.sin(t * 8.5f) * px(44f);
                actorPath.reset();
                actorPath.moveTo(bx - px(4f), by - px(6f));
                actorPath.quadTo(bx - px(28f), by - px(45f) + flap * 0.6f, bx - px(36f), by - px(74f) + flap);
                actorPath.quadTo(bx - px(8f), by - px(52f) + flap * 0.8f, bx + px(24f), by - px(10f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                paint.setColor((aBase << 24) | 0x00FFE498);
                c.drawCircle(bx - px(4f), by - px(26f), px(7.5f), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.6f));
                c.drawLine(bx - px(22f), by - px(22f), bx + px(14f), by - px(24f), paint);
                paint.setColor((aBase * 2 / 3 << 24) | 0x0098E8FF);
                c.drawLine(bx - px(95f), groundY - px(2f), bx + px(35f), groundY - px(2f), paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 29: {
                // SCENE 30 (NEW): DADA Class — Riddikulus & The Rattling Boggart Wardrobe
                drawWizardStanding(c, w * 0.30f, groundY - px(64f), t, aBase, 0xFFFFE498, true, false, 1.50f);
                float wx = w * 0.56f + (float) Math.sin(t * 22f) * px(4f);
                paint.setColor((aBase << 24) | 0x00C89458);
                c.drawRoundRect(wx - px(36f), groundY - px(138f), wx + px(36f), groundY, px(6f), px(6f), paint);
                drawWizardStanding(c, wx - px(58f), groundY - px(66f), t, aBase, 0xFF88D8B0, false, true, 1.50f);
                break;
            }
            case 30: {
                // SCENE 31 (NEW): Secret Hogsmeade Passage — The Marauder's Map & Marching Footprints
                float mapX = w * 0.45f, mapY = groundY - px(75f);
                paint.setColor((aBase << 24) | 0x00F5E0B4);
                c.drawRoundRect(mapX - px(110f), mapY - px(52f), mapX + px(110f), mapY + px(52f), px(6f), px(6f), paint);
                paint.setColor((aBase << 24) | 0x00582818);
                for (int fp = 0; fp < 7; fp++) {
                    float fx = mapX - px(85f) + ((fp * 28f + p * 160f) % 170f) * scale;
                    float fy = mapY + (fp % 2 == 0 ? -px(8f) : px(8f));
                    c.drawOval(fx - px(5f), fy - px(3f), fx + px(5f), fy + px(3f), paint);
                }
                break;
            }
            case 31: {
                // SCENE 32: The Shrieking Shack — Padfoot the Grim & Moony the Werewolf Under the Full Moon
                paint.setColor((aBase / 3 << 24) | 0x00FFF8D8);
                c.drawCircle(w * 0.48f, h * 0.67f, px(48f), paint);
                paint.setColor((aBase * 4 / 5 << 24) | 0x00FFFCE8);
                c.drawCircle(w * 0.48f, h * 0.67f, px(30f), paint);
                // Padfoot the Grim (shaggy black hound with glowing amber eyes)
                drawGrimHound(c, w * 0.36f, groundY, t, aBase, 1.35f);
                // Moony the Werewolf (tall hunched lycanthrope howling toward the full moon)
                float wx = w * 0.58f, wy = groundY - px(68f);
                paint.setColor((aBase << 24) | 0x00687488);
                c.drawOval(wx - px(14f), wy - px(18f), wx + px(14f), wy + px(32f), paint);
                c.drawCircle(wx - px(4f), wy - px(26f), px(10f), paint);
                actorPath.reset();
                actorPath.moveTo(wx - px(10f), wy - px(28f));
                actorPath.lineTo(wx - px(22f), wy - px(34f));
                actorPath.lineTo(wx - px(10f), wy - px(20f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(4.5f));
                c.drawLine(wx - px(8f), wy + px(26f), wx - px(14f), groundY, paint);
                c.drawLine(wx + px(8f), wy + px(26f), wx + px(14f), groundY, paint);
                c.drawLine(wx - px(8f), wy - px(8f), wx - px(26f), wy - px(2f), paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00FFE048);
                c.drawCircle(wx - px(8f), wy - px(28f), px(2.4f), paint);
                break;
            }
            case 32: {
                // SCENE 33: Prisoner of Azkaban — Expecto Patronum Stag vs Dementors
                drawPatronusScene(c, w, h, groundY, t, p, aBase);
                break;
            }
            case 33: {
                // SCENE 34: The Spinning Golden Time-Turner & Rescue of Sirius Black
                float cx = w * 0.48f, cy = h * 0.75f;
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor((aBase << 24) | 0x00FFDF78);
                for (int ring = 0; ring < 3; ring++) {
                    paint.setStrokeWidth(px(3.2f - ring * 0.6f));
                    float rx = px((88f - ring * 22f) * (float) Math.cos(t * (2.2f + ring)));
                    float ry = px(88f - ring * 22f);
                    storyRect.set(cx - Math.abs(rx), cy - ry, cx + Math.abs(rx), cy + ry);
                    c.drawOval(storyRect, paint);
                }
                paint.setStyle(Paint.Style.FILL);
                c.drawCircle(cx, cy, px(16f), paint);
                drawBird(c, w * (0.58f + p * 0.16f), h * 0.72f, t * 7.5f, px(30f), (aBase << 24) | 0x00D8F0FF);
                break;
            }
            case 34: {
                // SCENE 35 (NEW): Quidditch World Cup — The Emerald Dark Mark Skull & Serpent in the Sky
                float sx = w * 0.46f, sy = h * 0.69f;
                paint.setColor((aBase / 3 << 24) | 0x0020FF78);
                c.drawCircle(sx, sy, px(54f), paint);
                paint.setColor((aBase << 24) | 0x0058FF98);
                c.drawCircle(sx, sy - px(8f), px(28f), paint);
                c.drawRect(sx - px(16f), sy + px(10f), sx + px(16f), sy + px(28f), paint);
                paint.setColor((aBase << 24) | 0x000A1810);
                c.drawCircle(sx - px(10f), sy - px(8f), px(6.5f), paint);
                c.drawCircle(sx + px(10f), sy - px(8f), px(6.5f), paint);
                drawSerpent(c, sx + px(18f), groundY, t, aBase, 0xFF28E878, 0xFFA8FFD0, 1.15f, false);
                break;
            }
            case 35: {
                // SCENE 36 (NEW): The Great Hall — The Goblet of Fire Erupting with Azure Flames
                float gx = w * 0.46f;
                paint.setColor((aBase << 24) | 0x00D8B468);
                c.drawRect(gx - px(24f), groundY - px(18f), gx + px(24f), groundY, paint);
                c.drawRect(gx - px(10f), groundY - px(68f), gx + px(10f), groundY - px(18f), paint);
                c.drawRoundRect(gx - px(32f), groundY - px(104f), gx + px(32f), groundY - px(64f), px(12f), px(12f), paint);
                // Dancing azure & magenta flames + flying parchment slip
                for (int f = 0; f < 14; f++) {
                    float fp = ((t * 2.2f + f * 0.07f) % 1.0f);
                    float fx = gx + (float) Math.sin(t * 7f + f) * px(26f * fp);
                    float fy = groundY - px(106f) - fp * px(95f);
                    paint.setColor((Math.round(aBase * (1f - fp)) << 24) | (f % 3 == 0 ? 0x00FF58D8 : 0x0038D8FF));
                    c.drawCircle(fx, fy, px(16f * (1f - fp * 0.5f)), paint);
                }
                break;
            }
            case 36: {
                // SCENE 37: Goblet of Fire — Hungarian Horntail Dragon vs Harry on Firebolt
                drawDragonScene(c, w, h, groundY, t, p, aBase);
                break;
            }
            case 37: {
                // SCENE 38 (NEW): The Yule Ball — Champions' Waltz Under Crystalline Ice Arches
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.0f));
                paint.setColor((aBase * 3 / 4 << 24) | 0x0098F0FF);
                for (int a = 0; a < 3; a++) {
                    float ax = w * (0.30f + a * 0.16f);
                    storyRect.set(ax - px(55f), groundY - px(140f), ax + px(55f), groundY + px(40f));
                    c.drawArc(storyRect, 180, 180, false, paint);
                }
                paint.setStyle(Paint.Style.FILL);
                for (int d = 0; d < 3; d++) {
                    float dx = w * (0.30f + d * 0.16f) + (float) Math.sin(t * 4f + d) * px(14f);
                    drawWizardStanding(c, dx - px(12f), groundY - px(62f), t + d, aBase, 0xFFFFE498, false, false, 1.40f);
                    drawWizardStanding(c, dx + px(12f), groundY - px(60f), t + d + 1f, aBase, 0xFF98E8FF, false, false, 1.38f);
                }
                break;
            }
            case 38: {
                // SCENE 39: The Second Task — Merpeople & Kelp Forest of the Black Lake
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(4.0f));
                for (int k = 0; k < 12; k++) {
                    float kx = w * (0.22f + k * 0.045f);
                    float sway = (float) Math.sin(t * 3.5f + k) * px(18f);
                    paint.setColor((aBase * 3 / 4 << 24) | 0x0020B888);
                    actorPath.reset();
                    actorPath.moveTo(kx, groundY);
                    actorPath.quadTo(kx + sway, groundY - px(65f), kx - sway * 0.5f, groundY - px(130f));
                    c.drawPath(actorPath, paint);
                }
                float mx = w * 0.56f, my = groundY - px(96f) + (float) Math.sin(t * 3.2f) * px(10f);
                float tailSway = (float) Math.cos(t * 4.5f) * px(14f);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x0058D8C0);
                c.drawCircle(mx, my - px(18f), px(8.5f), paint);
                actorPath.reset();
                actorPath.moveTo(mx - px(8f), my - px(8f));
                actorPath.lineTo(mx + px(8f), my - px(8f));
                actorPath.quadTo(mx + px(10f) + tailSway, my + px(24f), mx + tailSway * 1.4f, my + px(52f));
                actorPath.lineTo(mx + tailSway * 1.4f + px(14f), my + px(66f));
                actorPath.lineTo(mx + tailSway * 1.4f, my + px(58f));
                actorPath.lineTo(mx + tailSway * 1.4f - px(14f), my + px(66f));
                actorPath.lineTo(mx + tailSway * 1.4f - px(3f), my + px(52f));
                actorPath.quadTo(mx - px(8f) + tailSway, my + px(24f), mx - px(8f), my - px(8f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                float hx = w * 0.44f, hy = groundY - px(45f) - p * px(88f);
                paint.setColor((aBase << 24) | 0x0088F0FF);
                c.drawCircle(hx, hy - px(16f), px(7.5f), paint);
                c.drawRoundRect(hx - px(6f), hy - px(7f), hx + px(6f), hy + px(22f), px(4f), px(4f), paint);
                break;
            }
            case 39: {
                // SCENE 40 (NEW): The Third Task — The Shifting Triwizard Maze & Glowing Cup Portkey
                float cx = w * 0.46f;
                paint.setColor((aBase << 24) | 0x001E5838);
                c.drawRoundRect(cx - px(160f), groundY - px(125f), cx - px(52f), groundY, px(8f), px(8f), paint);
                c.drawRoundRect(cx + px(52f), groundY - px(125f), cx + px(160f), groundY, px(8f), px(8f), paint);
                // Glowing azure Triwizard Cup levitating in the center corridor
                float cupY = groundY - px(68f) + (float) Math.sin(t * 5f) * px(8f);
                paint.setColor((aBase / 3 << 24) | 0x0048D8FF);
                c.drawCircle(cx, cupY, px(36f), paint);
                paint.setColor((aBase << 24) | 0x00B8F8FF);
                c.drawRoundRect(cx - px(16f), cupY - px(22f), cx + px(16f), cupY + px(14f), px(6f), px(6f), paint);
                break;
            }
            case 40: {
                // SCENE 41 (NEW): Little Hangleton Graveyard — Golden Priori Incantatem Dome
                drawFinalDuelScene(c, w, h, groundY, t, p, aBase);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase * 2 / 3 << 24) | 0x00FFE082);
                storyRect.set(w * 0.22f, groundY - px(190f), w * 0.72f, groundY + px(120f));
                c.drawArc(storyRect, 180, 180, false, paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 41: {
                // SCENE 42 (NEW): Order of the Phoenix — Advance Guard Broom Flight Over the Thames
                for (int b = 0; b < 4; b++) {
                    float bx = w * (0.18f + b * 0.11f + p * 0.22f);
                    float by = h * (0.69f + (b % 2) * 0.05f) + (float) Math.sin(t * 5f + b) * px(12f);
                    drawBroomRider(c, bx, by, t + b, aBase, 0xFFE83838, 0xFFFFE082, false, 1.35f);
                }
                break;
            }
            case 42: {
                // SCENE 43: Forbidden Forest — Luna Lovegood & The Winged Skeletal Thestrals
                drawWizardStanding(c, w * 0.34f, groundY - px(60f), t, aBase, 0xFFFFF0B8, false, false, 1.42f);
                float thx = w * 0.53f, thy = groundY - px(60f);
                drawStagPatronus(c, thx, thy, t, aBase * 4 / 5, 1.28f);
                // Leathery bat-like Thestral wings arching from the shoulders
                float flap = (float) Math.sin(t * 6.5f) * px(22f);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase * 3 / 4 << 24) | 0x00486078);
                actorPath.reset();
                actorPath.moveTo(thx - px(4f), thy - px(10f));
                actorPath.lineTo(thx + px(14f), thy - px(58f) + flap);
                actorPath.lineTo(thx + px(42f), thy - px(36f) + flap * 0.7f);
                actorPath.lineTo(thx + px(18f), thy - px(4f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                break;
            }
            case 43: {
                // SCENE 44: Room of Requirement — Dumbledore's Army & Weasley Fireworks Dragon
                float baseX = w * (0.26f + p * 0.30f);
                for (int m = 0; m < 4; m++) {
                    float bob = (float) Math.abs(Math.sin(t * 5.5f + m)) * px(5f);
                    drawWizardStanding(c, baseX - m * px(62f), groundY - px(62f) - bob, t + m, aBase, 0xFFFFE498, true, false, 1.45f);
                }
                for (int fw = 0; fw < 3; fw++) {
                    float fwx = w * (0.38f + fw * 0.12f);
                    float fwy = h * (0.68f + (fw % 2) * 0.04f);
                    float burst = ((t * 1.8f + fw * 0.33f) % 1.0f);
                    int fa = Math.round(aBase * (1f - burst));
                    paint.setColor((fa << 24) | (fw % 2 == 0 ? 0x00FFD040 : 0x00FF48A8));
                    for (int sp = 0; sp < 10; sp++) {
                        double ang = sp * Math.PI / 5.0;
                        c.drawCircle(fwx + (float) Math.cos(ang) * burst * px(68f),
                                fwy + (float) Math.sin(ang) * burst * px(68f), px(4.0f), paint);
                    }
                }
                break;
            }
            case 44: {
                // SCENE 45 (NEW): Department of Mysteries — Towering Shelves of Glowing Prophecy Orbs
                drawWizardStanding(c, w * 0.46f, groundY - px(64f), t, aBase, 0xFFFFE498, true, false, 1.50f);
                for (int col = 0; col < 6; col++) {
                    float sx = w * (0.24f + col * 0.09f);
                    for (int row = 0; row < 4; row++) {
                        float sy = groundY - px(32f + row * 34f);
                        paint.setColor((aBase << 24) | 0x0078E4FF);
                        c.drawCircle(sx, sy, px(7.5f), paint);
                    }
                }
                break;
            }
            case 45: {
                // SCENE 46 (NEW): Ministry Atrium — Dumbledore Trapping Voldemort in the Swirling Water Sphere
                drawWizardStanding(c, w * 0.28f, groundY - px(68f), t, aBase, 0xFFFFF0C8, true, true, 1.55f);
                float wx = w * 0.56f, wy = groundY - px(72f);
                drawVoldemort(c, wx, groundY - px(66f), t, aBase, 1.45f);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase / 2 << 24) | 0x0048C8FF);
                c.drawCircle(wx, wy, px(64f + 6f * (float) Math.sin(t * 8f)), paint);
                break;
            }
            case 46: {
                // SCENE 47 (NEW): Potions Class — Slughorn's Felix Felicis Liquid Luck Cauldron
                float cx = w * 0.46f;
                drawWizardStanding(c, cx - px(90f), groundY - px(64f), t, aBase, 0xFFFFE498, false, false, 1.50f);
                paint.setColor((aBase << 24) | 0x00383848);
                c.drawOval(cx - px(38f), groundY - px(58f), cx + px(38f), groundY, paint);
                paint.setColor((aBase << 24) | 0x00FFE068);
                for (int b = 0; b < 10; b++) {
                    float bp = ((t * 1.4f + b * 0.1f) % 1.0f);
                    float bx = cx + (float) Math.sin(bp * Math.PI * 3f + b) * px(28f);
                    float by = groundY - px(54f) - bp * px(95f);
                    c.drawCircle(bx, by, px(5.5f * (1f - bp * 0.5f)), paint);
                }
                break;
            }
            case 47: {
                // SCENE 48: Half-Blood Prince — Dumbledore's Firestorm Lasso in the Crystal Cave
                float dumbX = w * 0.48f, dumbY = groundY - px(68f);
                drawWizardStanding(c, dumbX, dumbY, t, aBase, 0xFFFFF0C8, true, true, 1.60f);
                for (int f = 0; f < 28; f++) {
                    float ang = t * 3.8f + f * (float) (Math.PI * 2.0 / 28.0);
                    float rx = px(215f + 35f * (float) Math.sin(t * 2f + f));
                    float ry = px(64f);
                    float fx = dumbX + (float) Math.cos(ang) * rx;
                    float fy = dumbY - px(24f) + (float) Math.sin(ang) * ry;
                    int fa = Math.round(aBase * (0.55f + 0.45f * (float) Math.sin(ang)));
                    paint.setColor((fa << 24) | (f % 3 == 0 ? 0x00FFF498 : 0x00FF6820));
                    c.drawCircle(fx, fy, px(6.5f + (f % 3) * 3.0f), paint);
                }
                break;
            }
            case 48: {
                // SCENE 49 (NEW): Astronomy Tower — Hogwarts Wands Raised Skyward to Dispel the Dark
                for (int wIdx = 0; wIdx < 6; wIdx++) {
                    float wx = w * (0.26f + wIdx * 0.08f);
                    drawWizardStanding(c, wx, groundY - px(62f), t + wIdx, aBase, 0xFFFFE8A0, true, wIdx == 2, 1.45f);
                }
                break;
            }
            case 49: {
                // SCENE 50 (NEW): Battle of the Seven Potters Across the Surrey Skies
                for (int sp = 0; sp < 4; sp++) {
                    float bx = w * (0.20f + sp * 0.12f + p * 0.14f);
                    float by = h * (0.68f + (sp % 2) * 0.06f) + (float) Math.sin(t * 6f + sp) * px(14f);
                    drawBroomRider(c, bx, by, t + sp, aBase, 0xFFE83838, 0xFFFFE082, false, 1.35f);
                }
                break;
            }
            case 50: {
                // SCENE 51 (NEW): Forest of Dean — Snape's Silver Doe Patronus & Sword in the Frozen Pool
                drawWizardStanding(c, w * 0.28f, groundY - px(64f), t, aBase, 0xFFFFE498, true, false, 1.50f);
                drawStagPatronus(c, w * 0.48f, groundY - px(62f), t, aBase, 1.25f);
                paint.setColor((aBase * 2 / 3 << 24) | 0x0088F0FF);
                c.drawOval(w * 0.54f, groundY - px(14f), w * 0.68f, groundY + px(4f), paint);
                break;
            }
            case 51: {
                // SCENE 52: Xenophilius's House — The Tale of the Three Brothers & Deathly Hallows Symbol
                // Arched Stone Bridge over the winding river
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(4.0f));
                paint.setColor((aBase * 3 / 4 << 24) | 0x00C8A868);
                c.drawArc(rectF(w * 0.24f, groundY - px(28f), w * 0.66f, groundY + px(24f)), 180, 180, false, paint);
                paint.setStyle(Paint.Style.FILL);
                for (int br = 0; br < 3; br++) {
                    drawWizardStanding(c, w * (0.28f + br * 0.065f), groundY - px(64f), t + br, aBase, 0xFFFFDF88, true, false, 1.40f);
                }
                drawDementor(c, w * 0.58f, groundY - px(72f), t, aBase, 1.72f);
                // Death's Curved Scythe
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.4f));
                paint.setColor((aBase << 24) | 0x00FFE898);
                c.drawLine(w * 0.54f, groundY - px(18f), w * 0.56f, groundY - px(112f), paint);
                c.drawArc(rectF(w * 0.50f, groundY - px(120f), w * 0.57f, groundY - px(96f)), 180, 135, false, paint);
                // Glowing Deathly Hallows triangle, circle & Elder Wand line overhead
                float hx = w * 0.46f, hy = h * 0.69f;
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase << 24) | 0x00FFE082);
                actorPath.reset();
                actorPath.moveTo(hx, hy - px(32f));
                actorPath.lineTo(hx - px(28f), hy + px(18f));
                actorPath.lineTo(hx + px(28f), hy + px(18f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                c.drawCircle(hx, hy + px(2f), px(15f), paint);
                c.drawLine(hx, hy - px(32f), hx, hy + px(18f), paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 52: {
                // SCENE 53: Gringotts Breakout — The Pale Ukrainian Ironbelly Dragon Soaring Skyward
                float dx = w * (0.38f + p * 0.22f);
                float dy = h * (0.76f - p * 0.08f) + (float) Math.sin(t * 4.5f) * px(12f);
                drawDragonCustom(c, dx, dy, t, aBase, 0xFFD8E8F8, 1.45f);
                for (int f = 0; f < 14; f++) {
                    float fp = ((t * 3.4f + f * 0.07f) % 1.0f);
                    int fa = Math.round(aBase * (1f - fp) * 0.9f);
                    paint.setColor((fa << 24) | (fp < 0.5f ? 0x00FFFFFF : 0x0088D8FF));
                    c.drawCircle(dx - px(62f) - fp * px(180f), dy - fp * px(45f), px(6f + fp * 16f), paint);
                }
                break;
            }
            case 53: {
                // SCENE 54 (NEW): Room of Requirement — Giant Roaring Fiendfyre Fire-Serpent vs Brooms
                drawSerpent(c, w * 0.58f, groundY, t, aBase, 0xFFFF5818, 0xFFFFE868, 1.70f, true);
                drawBroomRider(c, w * (0.28f + p * 0.14f), h * 0.72f, t, aBase, 0xFFE83838, 0xFFFFE082, false, 1.40f);
                drawBroomRider(c, w * (0.20f + p * 0.14f), h * 0.76f, t + 1f, aBase, 0xFF3282FF, 0xFFFFE082, false, 1.35f);
                break;
            }
            case 54: {
                // SCENE 55: Battle of Hogwarts — Protego Maxima Shield Dome & Stone Knights
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(4.5f));
                paint.setColor((aBase * 4 / 5 << 24) | 0x0078E4FF);
                storyRect.set(w * 0.52f, h * 0.62f, w * 0.96f, groundY + px(140f));
                c.drawArc(storyRect, 180, 180, false, paint);
                paint.setStyle(Paint.Style.FILL);
                for (int k = 0; k < 4; k++) {
                    drawWizardStanding(c, w * (0.56f - k * 0.055f), groundY - px(62f), t + k, aBase, 0xFFD8C8A8, true, false, 1.45f);
                }
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                for (int s = 0; s < 6; s++) {
                    float sp = ((t * 2.4f + s * 0.16f) % 1.0f);
                    float sx = w * (0.20f + sp * 0.34f);
                    float sy = h * (0.64f + (s % 3) * 0.05f + sp * 0.08f);
                    paint.setColor((aBase << 24) | 0x0038FF88);
                    c.drawLine(sx - px(28f), sy - px(10f), sx, sy, paint);
                }
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 55: {
                // SCENE 56: The Deathly Hallows — Harry vs. Voldemort Final Priori Incantatem Duel
                drawFinalDuelScene(c, w, h, groundY, t, p, aBase);
                break;
            }
            case 56: {
                // SCENE 57: Gringotts Cavern — Runaway Vault Cart Rollercoaster & Thief's Downfall Waterfall
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase * 3 / 4 << 24) | 0x008898B0);
                c.drawLine(w * 0.24f, groundY - px(78f), w * 0.66f, groundY - px(14f), paint);
                // Thief's Downfall cascading waterfall column
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase / 3 << 24) | 0x0058D8FF);
                c.drawRect(w * 0.50f, h * 0.66f, w * 0.55f, groundY, paint);
                // Hurtling Mine Cart with Harry, Griphook & Hagrid
                float cx = w * (0.28f + p * 0.34f);
                float cy = groundY - px(74f) + p * px(52f);
                paint.setColor((aBase << 24) | 0x0048382C);
                c.drawRoundRect(cx - px(24f), cy - px(18f), cx + px(24f), cy, px(4f), px(4f), paint);
                paint.setColor((aBase << 24) | 0x00FFE082);
                c.drawCircle(cx - px(14f), cy + px(3f), px(5f), paint);
                c.drawCircle(cx + px(14f), cy + px(3f), px(5f), paint);
                drawLightningStreak(c, cx - px(36f), cy + px(3f), cx - px(14f), cy + px(3f),
                        t, 560, 0x00FF7800, 0x00FFE048, 0x00FFFFFF, px(2.4f), aBase, false);
                break;
            }
            case 57: {
                // SCENE 58: Honeydukes Secret Passage — The One-Eyed Witch Statue & Lumos
                paint.setColor((aBase << 24) | 0x00788294);
                c.drawRoundRect(w * 0.54f, groundY - px(92f), w * 0.61f, groundY, px(8f), px(8f), paint);
                drawWizardStanding(c, w * (0.30f + p * 0.18f), groundY - px(58f), t, aBase, 0xFFFFE082, true, true, 1.38f);
                paint.setColor((aBase / 2 << 24) | 0x00FFF8B8);
                c.drawCircle(w * (0.30f + p * 0.18f) + px(28f), groundY - px(68f), px(18f), paint);
                break;
            }
            case 58: {
                // SCENE 59: Hogsmeade Blizzard — The Shrieking Shack in the Snowstorm
                for (int sn = 0; sn < 14; sn++) {
                    float sx = w * (0.26f + ((sn * 0.06f + p * 0.30f) % 0.42f));
                    float sy = h * 0.68f + ((t * 32f + sn * 17f) % px(110f));
                    paint.setColor((aBase * 3 / 4 << 24) | 0x00E8F4FF);
                    c.drawCircle(sx, sy, px(2.4f), paint);
                }
                drawWizardStanding(c, w * 0.36f, groundY - px(58f), t, aBase / 2, 0xFF88E8FF, true, true, 1.36f);
                drawWizardStanding(c, w * 0.56f, groundY - px(58f), t + 1f, aBase, 0xFF38D888, false, false, 1.36f);
                break;
            }
            case 59: {
                // SCENE 60: Triwizard Arrival — Beauxbatons Winged Abraxan Horse Carriage
                float bx = w * (0.28f + p * 0.32f), by = h * 0.75f + (float) Math.sin(t * 4f) * px(10f);
                drawStagPatronus(c, bx + px(54f), by + px(18f), t, aBase, 1.05f);
                drawBird(c, bx + px(52f), by - px(8f), t * 9f, px(28f), (aBase << 24) | 0x00FFE898);
                paint.setColor((aBase << 24) | 0x0078C8FF);
                c.drawRoundRect(bx - px(32f), by - px(16f), bx + px(18f), by + px(18f), px(8f), px(8f), paint);
                paint.setColor((aBase << 24) | 0x00FFE898);
                c.drawRect(bx - px(18f), by - px(8f), bx + px(4f), by + px(4f), paint);
                break;
            }
            case 60: {
                // SCENE 61: Black Lake Whirlpool — The Durmstrang Spectral Galleon Rising
                float sx = w * 0.48f, sy = groundY - px(14f + p * 44f);
                paint.setColor((aBase << 24) | 0x00382C24);
                actorPath.reset();
                actorPath.moveTo(sx - px(48f), sy - px(18f));
                actorPath.lineTo(sx + px(48f), sy - px(18f));
                actorPath.lineTo(sx + px(32f), sy + px(14f));
                actorPath.lineTo(sx - px(32f), sy + px(14f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.8f));
                c.drawLine(sx - px(16f), sy - px(18f), sx - px(16f), sy - px(78f), paint);
                c.drawLine(sx + px(16f), sy - px(18f), sx + px(16f), sy - px(86f), paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 61: {
                // SCENE 62: Number Twelve Grimmauld Place — Headquarters of the Order of the Phoenix
                for (int ord = 0; ord < 4; ord++) {
                    drawWizardStanding(c, w * (0.32f + ord * 0.09f), groundY - px(58f), t + ord * 0.7f,
                            aBase, ord % 2 == 0 ? 0xFFFFE082 : 0xFFFF6848, true, ord == 1, 1.35f);
                }
                break;
            }
            case 62: {
                // SCENE 63: Ministry Atrium — Albus Dumbledore & Fawkes vs. Lord Voldemort
                drawWizardStanding(c, w * 0.34f, groundY - px(60f), t, aBase, 0xFFFFE082, true, false, 1.42f);
                drawBird(c, w * 0.38f, h * 0.73f, t * 10f, px(24f), (aBase << 24) | 0x00FF6820);
                drawVoldemort(c, w * 0.60f, groundY - px(60f), t, aBase, 1.42f);
                drawLightningStreak(c, w * 0.37f, groundY - px(72f), w * 0.48f, groundY - px(74f),
                        t, 621, 0x00FF5800, 0x00FFE048, 0x00FFFFFF, px(3.8f), aBase, true);
                drawLightningStreak(c, w * 0.57f, groundY - px(72f), w * 0.48f, groundY - px(74f),
                        t, 622, 0x0000A848, 0x0038FF78, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            }
            case 63: {
                // SCENE 64: Great Hall Escape — Fred & George Weasley's Firework Dragon
                drawBroomRider(c, w * (0.28f + p * 0.24f), h * 0.75f, t, aBase, 0xFFFF7820, 0xFFFFE082, false, 1.32f);
                drawBroomRider(c, w * (0.36f + p * 0.24f), h * 0.79f, t + 1f, aBase, 0xFFFF7820, 0xFFFFE082, false, 1.32f);
                drawDragonCustom(c, w * 0.56f, h * 0.74f, t, aBase, 0xFFFFB828, 1.35f);
                break;
            }
            case 64: {
                // SCENE 65: The Burrow Marshes — Death Eater Ring of Fire Ambush
                for (int fl = 0; fl < 12; fl++) {
                    float fx = w * (0.28f + fl * 0.035f);
                    float fh = px(18f + 10f * (float) Math.abs(Math.sin(t * 9f + fl)));
                    paint.setColor((aBase * 4 / 5 << 24) | (fl % 2 == 0 ? 0x00FF5818 : 0x00FFD038));
                    c.drawOval(fx - px(8f), groundY - fh, fx + px(8f), groundY, paint);
                }
                drawWizardStanding(c, w * 0.42f, groundY - px(58f), t, aBase, 0xFFFFE082, true, true, 1.36f);
                drawWizardStanding(c, w * 0.52f, groundY - px(58f), t + 0.6f, aBase, 0xFFFF7848, false, false, 1.34f);
                break;
            }
            case 65: {
                // SCENE 66: Malfoy Manor — Dobby Unscrews the Crystal Chandelier
                float chY = h * 0.69f + p * px(58f);
                paint.setColor((aBase << 24) | 0x00A8E8FF);
                c.drawCircle(w * 0.48f, chY, px(16f), paint);
                drawLightningStreak(c, w * 0.48f - px(24f), chY, w * 0.48f + px(24f), chY,
                        t, 651, 0x0038B8FF, 0x00A8F4FF, 0x00FFFFFF, px(2.8f), aBase, true);
                drawWizardStanding(c, w * 0.36f, groundY - px(42f), t, aBase, 0xFFE8D8B8, true, false, 1.05f);
                break;
            }
            case 66: {
                // SCENE 67: The Prince's Tale — Severus Snape's Pensieve Memories & Silver Doe ("Always")
                drawWizardStanding(c, w * 0.36f, groundY - px(58f), t, aBase, 0xFF98B8E8, true, false, 1.38f);
                paint.setColor((aBase << 24) | 0x0088E8FF);
                c.drawOval(w * 0.45f, groundY - px(32f), w * 0.53f, groundY - px(18f), paint);
                drawStagPatronus(c, w * 0.58f, groundY - px(58f), t, aBase, 1.18f);
                break;
            }
            case 67: {
                // SCENE 68: Nineteen Years Later — Platform 9 3/4 Epilogue & Hogwarts Express
                drawHogwartsExpressScene(c, w, h, groundY, t, p, aBase);
                drawWizardStanding(c, w * 0.34f, groundY - px(56f), t, aBase, 0xFFFFE082, true, true, 1.32f);
                drawWizardStanding(c, w * 0.41f, groundY - px(52f), t + 0.5f, aBase, 0xFFFF7848, true, false, 1.24f);
                break;
            }
            case 68: {
                // SCENE 69: Privet Drive Bedroom — Under-Covers Lumos Maxima Practice
                float bx = w * 0.46f, by = groundY - px(18f);
                float flash = Math.max(0f, (float) Math.sin(t * 7.5f));
                paint.setColor((Math.round(aBase * 0.45f * flash) << 24) | 0x00FFF8B8);
                c.drawCircle(bx, by - px(28f), px(82f * flash), paint);
                paint.setColor((aBase << 24) | 0x00384868);
                c.drawArc(rectF(bx - px(62f), by - px(68f), bx + px(62f), by + px(18f)), 180, 180, true, paint);
                paint.setColor((Math.round(aBase * (0.35f + 0.65f * flash)) << 24) | 0x00FFFFFF);
                c.drawCircle(bx + px(10f), by - px(32f), px(14f + 12f * flash), paint);
                break;
            }
            case 69: {
                // SCENE 70: Hogwarts Express — Dementor Boarding & Frosted Compartment Glass
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase << 24) | 0x0088D8FF);
                c.drawRoundRect(w * 0.38f, groundY - px(108f), w * 0.54f, groundY - px(18f), px(6f), px(6f), paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((Math.round(aBase * p * 0.55f) << 24) | 0x00B8F0FF);
                c.drawRoundRect(w * 0.385f, groundY - px(104f), w * 0.535f, groundY - px(22f), px(5f), px(5f), paint);
                drawWizardStanding(c, w * 0.31f, groundY - px(58f), t, aBase, 0xFFFFE082, true, false, 1.36f);
                drawDementor(c, w * (0.62f - p * 0.06f), groundY - px(68f), t, aBase, 1.55f);
                break;
            }
            case 70: {
                // SCENE 71: Hogsmeade Village — Crookshanks Chasing Scabbers Toward the Shrieking Shack
                float rx = w * (0.32f + p * 0.28f);
                drawCatSilhouette(c, rx - px(38f), groundY - px(14f), aBase);
                paint.setColor((aBase << 24) | 0x00B89880);
                c.drawOval(rx, groundY - px(10f), rx + px(12f), groundY - px(3f), paint);
                drawWizardStanding(c, w * 0.28f, groundY - px(58f), t, aBase, 0xFFFF7848, false, true, 1.36f);
                break;
            }
            case 71: {
                // SCENE 72: Great Hall — Flaming Parchment Names Erupting from the Goblet of Fire
                float gx = w * 0.48f;
                paint.setColor((aBase << 24) | 0x00D8B468);
                c.drawRect(gx - px(16f), groundY - px(64f), gx + px(16f), groundY, paint);
                c.drawRoundRect(gx - px(26f), groundY - px(94f), gx + px(26f), groundY - px(60f), px(8f), px(8f), paint);
                drawLightningStreak(c, gx, groundY - px(94f), gx - px(46f), h * 0.69f,
                        t, 710, 0x00FF2898, 0x00FF88D8, 0x00FFFFFF, px(3.8f), aBase, true);
                paint.setColor((aBase << 24) | 0x00FFF4D0);
                c.drawRoundRect(gx - px(54f), h * 0.68f, gx - px(34f), h * 0.71f, px(2f), px(2f), paint);
                drawWizardStanding(c, w * 0.32f, groundY - px(60f), t, aBase, 0xFFFFF0C8, false, true, 1.42f);
                break;
            }
            case 72: {
                // SCENE 73: Champions' Tent — Rita Skeeter & the Acid-Green Quick-Quotes Quill
                drawWizardStanding(c, w * 0.35f, groundY - px(58f), t, aBase, 0xFFFFE082, false, false, 1.36f);
                drawWizardStanding(c, w * 0.58f, groundY - px(58f), t + 0.8f, aBase, 0xFF58FF78, false, false, 1.36f);
                float qx = w * 0.46f + (float) Math.sin(t * 14f) * px(12f);
                float qy = groundY - px(68f);
                paint.setColor((aBase << 24) | 0x00FFF6D8);
                c.drawRoundRect(w * 0.43f, qy, w * 0.49f, qy + px(24f), px(2f), px(2f), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.8f));
                paint.setColor((aBase << 24) | 0x0038FF68);
                c.drawLine(qx, qy + px(4f), qx + px(14f), qy - px(24f), paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            }
            case 73: {
                // SCENE 74: Diagon Alley — Weasleys' Wizard Wheezes Joke Shop & Fireworks
                drawWizardStanding(c, w * 0.38f, groundY - px(58f), t, aBase, 0xFFFF7820, true, false, 1.36f);
                drawWizardStanding(c, w * 0.54f, groundY - px(58f), t + 0.6f, aBase, 0xFFFF7820, true, false, 1.36f);
                for (int sp = 0; sp < 8; sp++) {
                    double ang = t * 4f + sp * Math.PI * 0.25;
                    float r = px(28f + p * 36f);
                    paint.setColor((aBase << 24) | (sp % 2 == 0 ? 0x00FFD838 : 0x00FF48A8));
                    c.drawCircle(w * 0.46f + (float) Math.cos(ang) * r, h * 0.72f + (float) Math.sin(ang) * r * 0.6f, px(4.0f), paint);
                }
                break;
            }
            case 74: {
                // SCENE 75: Sixth-Floor Bathroom — Harry vs. Draco Malfoy Sectumsempra Duel
                drawWizardStanding(c, w * 0.34f, groundY - px(58f), t, aBase, 0xFFFFE082, true, false, 1.38f);
                drawWizardStanding(c, w * 0.58f, groundY - px(58f), t + 0.7f, aBase, 0xFFA8D8E8, true, false, 1.38f);
                drawLightningStreak(c, w * 0.38f, groundY - px(68f), w * 0.55f, groundY - px(68f),
                        t, 740, 0x0058C8FF, 0x00C8F4FF, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            }
            case 75: {
                // SCENE 76: Grimmauld Place — Kreacher's Tale & Regulus Black's Locket Horcrux
                drawWizardStanding(c, w * 0.35f, groundY - px(58f), t, aBase, 0xFFFFE082, false, false, 1.36f);
                drawWizardStanding(c, w * 0.56f, groundY - px(42f), t + 0.5f, aBase, 0xFFD8C8B0, false, false, 1.02f);
                float lx = w * 0.46f, ly = groundY - px(66f) + (float) Math.sin(t * 5f) * px(6f);
                paint.setColor((aBase / 3 << 24) | 0x0028FF78);
                c.drawCircle(lx, ly, px(22f), paint);
                paint.setColor((aBase << 24) | 0x00FFD848);
                c.drawOval(lx - px(8f), ly - px(11f), lx + px(8f), ly + px(11f), paint);
                break;
            }
            case 76: {
                // SCENE 77: Godric's Hollow — Christmas Eve Graveyard & Conjured Wreath in the Snow
                paint.setColor((aBase << 24) | 0x0098A8B8);
                c.drawRoundRect(w * 0.46f, groundY - px(68f), w * 0.52f, groundY, px(6f), px(6f), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.6f));
                paint.setColor((aBase << 24) | 0x0038C868);
                c.drawCircle(w * 0.49f, groundY - px(38f), px(12f), paint);
                paint.setStyle(Paint.Style.FILL);
                drawWizardStanding(c, w * 0.34f, groundY - px(58f), t, aBase, 0xFFFFE082, true, true, 1.36f);
                drawWizardStanding(c, w * 0.39f, groundY - px(56f), t + 0.4f, aBase, 0xFFFFB8C8, false, false, 1.32f);
                break;
            }
            case 77: {
                // SCENE 78: Shell Cottage — Bill & Fleur by the Crashing Tinworth Sea Waves
                paint.setColor((aBase * 3 / 5 << 24) | 0x0038B8FF);
                c.drawArc(rectF(w * 0.24f, groundY - px(48f), w * 0.42f, groundY + px(16f)), 180, 180, true, paint);
                c.drawArc(rectF(w * 0.38f, groundY - px(56f), w * 0.56f, groundY + px(16f)), 180, 180, true, paint);
                drawWizardStanding(c, w * 0.54f, groundY - px(58f), t, aBase, 0xFFFFE082, false, true, 1.35f);
                drawWizardStanding(c, w * 0.61f, groundY - px(56f), t + 0.5f, aBase, 0xFFB8E8FF, false, false, 1.32f);
                break;
            }
            case 78: {
                // SCENE 79: Hogwarts Courtyard — Neville Longbottom & the Sword of Gryffindor vs. Nagini
                drawWizardStanding(c, w * 0.36f, groundY - px(58f), t, aBase, 0xFFFFE082, false, false, 1.38f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase << 24) | 0x00FFFFFF);
                c.drawLine(w * 0.38f, groundY - px(64f), w * 0.46f, groundY - px(96f), paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00FF2438);
                c.drawCircle(w * 0.38f, groundY - px(64f), px(4.5f), paint);
                drawSerpent(c, w * 0.56f, groundY, t, aBase, 0xFF38A860, 0xFFA8F0C0, 1.35f, true);
                break;
            }
            default: {
                // SCENE 80: Viaduct Bridge — Harry Repairs the Phoenix-Feather Holly Wand with the Elder Wand
                drawWizardStanding(c, w * 0.42f, groundY - px(60f), t, aBase, 0xFFFFE082, true, false, 1.42f);
                drawLightningStreak(c, w * 0.45f, groundY - px(70f), w * 0.54f, groundY - px(70f),
                        t, 799, 0x00FF8800, 0x00FFE858, 0x00FFFFFF, px(3.8f), aBase, true);
                for (int sp = 0; sp < 10; sp++) {
                    double ang = t * 6f + sp * Math.PI * 0.2;
                    float r = px(10f + (sp % 3) * 12f);
                    paint.setColor((aBase << 24) | 0x00FFF098);
                    c.drawCircle(w * 0.54f + (float) Math.cos(ang) * r, groundY - px(70f) + (float) Math.sin(ang) * r, px(3.0f), paint);
                }
                break;
            }
        }
        c.restore();
    }

    private void drawNarutoStoryLoop(Canvas c, int w, int h, float t) {
        int count = NARUTO_TITLES.length;
        float totalCycle = CHAPTER_SEC * count;
        float cycleTime = t % totalCycle;
        int scene = resolveActiveScene(cycleTime, count);
        float p = (cycleTime % CHAPTER_SEC) / CHAPTER_SEC;
        float fade = resolveStoryFade(p);
        int aBase = Math.min(255, Math.max(0, Math.round(fade * 255f)));
        float groundY = h * 0.900f;
        drawStoryCaptionAndDots(c, w, h, NARUTO_TITLES[scene], scene, count, aBase,
                0x00FF9848, 0xEEFF6828, 0x42FF9848, px(10.5f));

        c.save();
        applyStageTransform(c, w, h, groundY);
        switch (scene) {
            case 0: // Naruto on Hokage Rock overlooking Konoha
                drawNarutoUzumaki(c, w * 0.46f, groundY - px(58f), t, aBase, 0, 1.35f);
                break;
            case 1: // Team 7 Bell Test with Kakashi Hatake
                drawNarutoUzumaki(c, w * 0.30f, groundY - px(56f), t, aBase, 0, 1.28f);
                drawSasukeUchiha(c, w * 0.44f, groundY - px(56f), t + 0.8f, aBase, false, 1.28f);
                drawSasukeUchiha(c, w * 0.60f, groundY - px(58f), t + 1.5f, aBase, true, 1.35f);
                break;
            case 2: // Zabuza & Haku Demonic Ice Mirrors
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.6f));
                paint.setColor((aBase * 3 / 4 << 24) | 0x0088E8FF);
                for (int m = 0; m < 5; m++) {
                    float ma = -0.8f + m * 0.4f;
                    float mx = w * 0.48f + (float) Math.sin(ma) * px(120f);
                    float my = groundY - px(78f) - (float) Math.cos(ma) * px(36f);
                    c.drawRect(mx - px(15f), my - px(26f), mx + px(15f), my + px(26f), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                drawNarutoUzumaki(c, w * 0.40f, groundY - px(56f), t, aBase, 0, 1.28f);
                drawSasukeUchiha(c, w * 0.56f, groundY - px(56f), t + 1f, aBase, true, 1.28f);
                break;
            case 3: // Rock Lee Eight Gates vs. Gaara Sand Gourd & Shield
                drawNarutoUzumaki(c, w * (0.30f + p * 0.10f), groundY - px(62f), t * 2f, aBase, 1, 1.32f);
                paint.setColor((aBase / 3 << 24) | 0x0038FF88);
                c.drawCircle(w * (0.30f + p * 0.10f), groundY - px(62f), px(38f), paint);
                paint.setColor((aBase * 4 / 5 << 24) | 0x00D8A860);
                c.drawArc(rectF(w * 0.50f, groundY - px(108f), w * 0.64f, groundY + px(16f)), 180, 180, true, paint);
                c.drawOval(w * 0.59f, groundY - px(82f), w * 0.63f, groundY - px(42f), paint);
                drawSasukeUchiha(c, w * 0.58f, groundY - px(56f), t, aBase, true, 1.28f);
                break;
            case 4: // Naruto Summons Chief Toad Gamabunta
                drawGamabuntaToad(c, w * 0.50f, groundY, t, aBase, 1.28f);
                drawNarutoUzumaki(c, w * 0.50f, groundY - px(136f), t, aBase, 0, 1.06f);
                break;
            case 5: // Itachi Uchiha in Akatsuki Cloak under Crimson Moon + Flock of Crows
                drawAkatsukiMember(c, w * 0.48f, groundY - px(58f), t, aBase, true, 1.38f);
                for (int cr = 0; cr < 7; cr++) {
                    float cp = (p + cr * 0.14f) % 1.0f;
                    drawBird(c, w * (0.28f + cp * 0.40f), groundY - px(38f + cr * 14f), t * 12f + cr, px(15f), (aBase << 24) | 0x00FF2830);
                }
                break;
            case 6: // Valley of the End — Rasengan vs. Chidori Clash
            case 11: { // Final Valley — Six Paths Naruto (Truth-Seeking Orbs) vs. Rinnegan Sasuke
                float nx = w * (0.32f + p * 0.08f), sx = w * (0.60f - p * 0.08f);
                float cy = groundY - px(58f);
                drawNarutoUzumaki(c, nx, cy, t, aBase, scene == 11 ? 2 : 0, 1.36f);
                drawSasukeUchiha(c, sx, cy, t + 1.2f, aBase, true, 1.36f);
                float clashX = (nx + sx) * 0.5f, clashY = cy - px(14f);
                paint.setColor((aBase / 3 << 24) | 0x001898FF);
                c.drawCircle(clashX, clashY, px(36f + 8f * (float) Math.sin(t * 18f)), paint);
                paint.setColor((aBase * 3 / 4 << 24) | 0x0048D8FF);
                c.drawCircle(clashX, clashY, px(22f + 5f * (float) Math.cos(t * 22f)), paint);
                paint.setColor((aBase << 24) | 0x00FFFFFF);
                c.drawCircle(clashX, clashY, px(11f), paint);
                for (int b = 0; b < 6; b++) {
                    double ang = t * 5f + b * Math.PI / 3.0;
                    float ex = clashX + (float) Math.cos(ang) * px(54f);
                    float ey = clashY + (float) Math.sin(ang) * px(44f);
                    drawLightningStreak(c, clashX, clashY, ex, ey, t, b * 17 + 3,
                            0x001888FF, 0x0068E8FF, 0x00FFFFFF, px(2.6f), aBase, true);
                }
                break;
            }
            case 7: // Deidara's Explosive Clay Dragon + Akatsuki Rider
                drawDragonCustom(c, w * (0.58f - p * 0.18f), h * 0.77f, t, aBase, 0xFFF0ECE0, 1.25f);
                drawAkatsukiMember(c, w * (0.58f - p * 0.18f), h * 0.74f, t, aBase, true, 0.95f);
                break;
            case 8: // Sage Mode Naruto vs. Pain (Chibaku Tensei)
                drawNarutoUzumaki(c, w * 0.34f, groundY - px(58f), t, aBase, 1, 1.36f);
                drawAkatsukiMember(c, w * 0.60f, groundY - px(72f) + (float) Math.sin(t * 4f) * px(10f), t, aBase, true, 1.36f);
                paint.setColor((aBase << 24) | 0x00181024);
                c.drawCircle(w * 0.47f, h * 0.73f, px(20f), paint);
                break;
            case 9: // Uchiha Madara & Armored Tengu Perfect Susanoo Avatar
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.5f));
                paint.setColor((aBase * 4 / 5 << 24) | 0x003898FF);
                c.drawArc(rectF(w * 0.41f, groundY - px(135f), w * 0.59f, groundY), 180, 180, false, paint);
                for (int rb = 0; rb < 3; rb++) {
                    float ry = groundY - px(32f + rb * 22f);
                    c.drawArc(rectF(w * 0.43f, ry - px(10f), w * 0.57f, ry + px(10f)), 180, 180, false, paint);
                }
                c.drawLine(w * 0.57f, groundY - px(75f), w * 0.69f, groundY - px(128f), paint);
                c.drawLine(w * 0.43f, groundY - px(75f), w * 0.31f, groundY - px(128f), paint);
                paint.setStyle(Paint.Style.FILL);
                drawSasukeUchiha(c, w * 0.50f, groundY - px(58f), t, aBase, false, 1.32f);
                break;
            case 10: // Kurama Nine-Tailed Fox & Six Paths Naruto
                drawKuramaFox(c, w * 0.54f, groundY, t, aBase, 1.25f);
                drawNarutoUzumaki(c, w * 0.32f, groundY - px(58f), t, aBase, 2, 1.32f);
                break;
            case 12: // Forest of Death — Orochimaru & Giant Serpent Manda vs. Naruto & Sasuke
                drawSerpent(c, w * 0.58f, groundY, t, aBase, 0xFF683898, 0xFFFFD848, 1.45f, true);
                drawNarutoUzumaki(c, w * 0.31f, groundY - px(56f), t, aBase, 0, 1.28f);
                drawSasukeUchiha(c, w * 0.41f, groundY - px(56f), t + 0.8f, aBase, false, 1.28f);
                break;
            case 13: // Chunin Prelims — Neji Hyuga Eight Trigrams Sixty-Four Palms
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.4f));
                paint.setColor((aBase * 4 / 5 << 24) | 0x0048E8FF);
                c.drawOval(w * 0.32f, groundY - px(18f), w * 0.62f, groundY + px(12f), paint);
                c.drawOval(w * 0.37f, groundY - px(12f), w * 0.57f, groundY + px(6f), paint);
                paint.setStyle(Paint.Style.FILL);
                drawNinjaSilhouette(c, w * 0.38f, groundY - px(58f), t * 2f, aBase, 0xFFE8E4D8, 0xFF48E8FF, false, true, false, 0xFF48E8FF, 1.34f);
                drawNinjaSilhouette(c, w * 0.55f, groundY - px(58f), t * 2f + 1f, aBase, 0xFF384878, 0xFF48E8FF, false, false, false, 0xFF48E8FF, 1.30f);
                break;
            case 14: // Konoha Crush — Third Hokage Hiruzen & Reaper Death Seal Shinigami
                // Spectral Purple Shinigami Reaper hovering behind Hiruzen with horned mask, white hair & tantō blade
                paint.setColor((aBase / 3 << 24) | 0x00B858FF);
                c.drawOval(w * 0.27f, groundY - px(142f), w * 0.41f, groundY - px(38f), paint);
                paint.setColor((aBase * 3 / 4 << 24) | 0x00E8E0FF);
                c.drawCircle(w * 0.34f, groundY - px(114f), px(14f), paint);
                paint.setColor((aBase << 24) | 0x00FF2848);
                c.drawCircle(w * 0.33f, groundY - px(126f), px(3.5f), paint);
                c.drawCircle(w * 0.35f, groundY - px(126f), px(3.5f), paint);
                drawLightningStreak(c, w * 0.34f, groundY - px(98f), w * 0.56f, groundY - px(68f),
                        t, 914, 0x009838FF, 0x00E098FF, 0x00FFFFFF, px(3.2f), aBase, false);
                drawNinjaSilhouette(c, w * 0.36f, groundY - px(58f), t, aBase, 0xFF282834, 0xFFFFE082, true, false, false, 0, 1.35f);
                drawSasukeUchiha(c, w * 0.58f, groundY - px(58f), t + 1f, aBase, true, 1.35f);
                break;
            case 15: // Tsunade Search — Naruto's First Complete Rasengan Impact vs. Kabuto
                float rNx = w * (0.32f + p * 0.14f);
                drawNarutoUzumaki(c, rNx, groundY - px(58f), t, aBase, 0, 1.36f);
                paint.setColor((aBase / 2 << 24) | 0x0038C8FF);
                c.drawCircle(rNx + px(28f), groundY - px(66f), px(22f + 5f * (float) Math.sin(t * 20f)), paint);
                drawNinjaSilhouette(c, w * 0.56f, groundY - px(58f), t + 0.6f, aBase, 0xFF483868, 0xFF68FF98, true, false, false, 0xFF68FF98, 1.32f);
                break;
            case 16: // Three-Way Deadlock — Gamabunta Toad (Jiraiya), Katsuyu Slug (Tsunade) & Manda Snake (Orochimaru)
                drawGamabuntaToad(c, w * 0.29f, groundY, t, aBase, 1.15f);
                drawNinjaSilhouette(c, w * 0.29f, groundY - px(120f), t, aBase, 0xFFD82828, 0xFFF4F4F8, true, true, false, 0, 0.92f);
                drawKatsuyuSlug(c, w * 0.46f, groundY, t, aBase, 1.15f);
                drawNinjaSilhouette(c, w * 0.46f, groundY - px(116f), t + 0.6f, aBase, 0xFF388848, 0xFFFFE078, false, false, false, 0xFF58F8D8, 0.90f);
                drawSerpent(c, w * 0.63f, groundY, t, aBase, 0xFF7838A8, 0xFFFFE048, 1.25f, true);
                drawNinjaSilhouette(c, w * 0.64f, groundY - px(128f), t + 1.2f, aBase, 0xFFE8E4F0, 0xFF8848B8, false, true, false, 0, 0.90f);
                break;
            case 17: // Sasuke Recovery — Shikamaru Shadow Possession Jutsu
                drawNinjaSilhouette(c, w * 0.34f, groundY - px(58f), t, aBase, 0xFF385838, 0xFF181820, true, false, false, 0, 1.34f);
                drawNinjaSilhouette(c, w * 0.58f, groundY - px(58f), t, aBase, 0xFF883848, 0xFFFF88A8, true, false, false, 0, 1.32f);
                drawLightningStreak(c, w * 0.34f, groundY - px(4f), w * 0.58f, groundY - px(4f),
                        t, 920, 0x00381858, 0x00101018, 0x00583878, px(4.2f), aBase, true);
                break;
            case 18: // Bone Forest — Gaara's Giant Sand Tsunami
                paint.setColor((aBase * 4 / 5 << 24) | 0x00D8A860);
                c.drawArc(rectF(w * 0.28f, groundY - px(98f), w * 0.52f, groundY + px(24f)), 180, 180, true, paint);
                c.drawArc(rectF(w * 0.46f, groundY - px(112f), w * 0.68f, groundY + px(24f)), 180, 180, true, paint);
                drawNinjaSilhouette(c, w * 0.38f, h * 0.74f, t, aBase, 0xFF982830, 0xFFD8A860, true, false, false, 0, 1.28f);
                break;
            case 19: // Kazekage Rescue — Sakura & Chiyo vs. Sasori's 100 Puppets
                drawNinjaSilhouette(c, w * 0.34f, groundY - px(58f), t * 1.5f, aBase, 0xFFE84878, 0xFFFFB8D0, false, false, false, 0xFFFF6898, 1.34f);
                for (int pup = 0; pup < 4; pup++) {
                    float pxP = w * (0.48f + pup * 0.055f);
                    float pyP = groundY - px(64f + (pup % 2) * 22f);
                    drawLightningStreak(c, w * 0.66f, groundY - px(78f), pxP, pyP,
                            t, 930 + pup, 0x0038B8FF, 0x0088E8FF, 0x00FFFFFF, px(1.6f), aBase * 3 / 4, false);
                    drawAkatsukiMember(c, pxP, pyP, t + pup, aBase, true, 1.05f);
                }
                break;
            case 20: // Tenchi Bridge — Four-Tailed Kyuubi Cloak Naruto vs. Orochimaru
                paint.setColor((aBase / 2 << 24) | 0x00FF1828);
                c.drawCircle(w * 0.36f, groundY - px(58f), px(38f), paint);
                drawNarutoUzumaki(c, w * 0.36f, groundY - px(58f), t * 2f, aBase, 1, 1.38f);
                drawLightningStreak(c, w * 0.40f, groundY - px(68f), w * 0.62f, groundY - px(64f),
                        t, 940, 0x00FF0020, 0x00380818, 0x00FF8898, px(4.8f), aBase, true);
                break;
            case 21: // Immortals Arc — Wind Style: Rasenshuriken Awakens
                drawNarutoUzumaki(c, w * 0.42f, groundY - px(58f), t, aBase, 1, 1.38f);
                float rsX = w * 0.42f + px(24f), rsY = groundY - px(96f);
                paint.setColor((aBase / 2 << 24) | 0x0058E8FF);
                c.drawOval(rsX - px(38f), rsY - px(12f), rsX + px(38f), rsY + px(12f), paint);
                c.drawOval(rsX - px(12f), rsY - px(38f), rsX + px(12f), rsY + px(38f), paint);
                paint.setColor((aBase << 24) | 0x00FFFFFF);
                c.drawCircle(rsX, rsY, px(11f), paint);
                break;
            case 22: // Avenge Asuma — Shikamaru's Chakra Blade Trap vs. Hidan
                drawNinjaSilhouette(c, w * 0.38f, groundY - px(58f), t, aBase, 0xFF385838, 0xFF58E8FF, true, false, false, 0xFF58E8FF, 1.35f);
                drawAkatsukiMember(c, w * 0.58f, groundY - px(58f), t + 1f, aBase, true, 1.35f);
                drawLightningStreak(c, w * 0.42f, groundY - px(64f), w * 0.58f, groundY - px(58f),
                        t, 950, 0x00FF4800, 0x00FFD838, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 23: // Hidden Rain Village — Tale of Jiraiya the Gallant vs. Six Paths of Pain
                drawNarutoUzumaki(c, w * 0.34f, groundY - px(60f), t, aBase, 1, 1.44f);
                for (int pn = 0; pn < 3; pn++) {
                    drawAkatsukiMember(c, w * (0.50f + pn * 0.065f), groundY - px(58f), t + pn, aBase, true, 1.24f);
                }
                break;
            case 24: // Uchiha Fortress — Sasuke's Kirin Lightning Dragon vs. Itachi Susanoo
                drawSasukeUchiha(c, w * 0.35f, groundY - px(58f), t, aBase, false, 1.35f);
                drawAkatsukiMember(c, w * 0.58f, groundY - px(58f), t, aBase, true, 1.35f);
                drawDragonCustom(c, w * 0.48f, h * 0.72f, t, aBase, 0xFF48E8FF, 1.28f);
                drawLightningStreak(c, w * 0.48f, h * 0.65f, w * 0.58f, groundY - px(58f),
                        t, 960, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(4.6f), aBase, true);
                break;
            case 25: // Cloud Gorge — Killer Bee & Eight-Tails Gyuki Acrobatics
                drawNinjaSilhouette(c, w * 0.36f, groundY - px(58f), t * 2f, aBase, 0xFFD82828, 0xFFFFFFFF, true, true, false, 0xFF58E8FF, 1.36f);
                drawSasukeUchiha(c, w * 0.58f, groundY - px(58f), t * 2f + 1f, aBase, true, 1.34f);
                drawLightningStreak(c, w * 0.38f, groundY - px(66f), w * 0.56f, groundY - px(66f),
                        t, 970, 0x000088FF, 0x0068F4FF, 0x00FFFFFF, px(3.6f), aBase, true);
                break;
            case 26: // Five Kage Summit — Sasuke Purple Susanoo vs. Fourth Raikage
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.5f));
                paint.setColor((aBase * 4 / 5 << 24) | 0x00B848FF);
                c.drawArc(rectF(w * 0.30f, groundY - px(120f), w * 0.46f, groundY), 180, 180, false, paint);
                paint.setStyle(Paint.Style.FILL);
                drawSasukeUchiha(c, w * 0.38f, groundY - px(58f), t, aBase, false, 1.34f);
                drawNinjaSilhouette(c, w * 0.58f, groundY - px(60f), t * 2f, aBase, 0xFFD8A838, 0xFF58F0FF, true, false, false, 0xFF58F0FF, 1.40f);
                drawLightningStreak(c, w * 0.58f, h * 0.70f, w * 0.58f, groundY,
                        t, 980, 0x000088FF, 0x0068F4FF, 0x00FFFFFF, px(3.6f), aBase, true);
                break;
            case 27: // Nine-Tails Night — Fourth Hokage Minato Flying Raijin Level Two vs. Obito
                drawAkatsukiMember(c, w * 0.52f, groundY - px(56f), t, aBase, true, 1.34f);
                drawNarutoUzumaki(c, w * 0.50f, groundY - px(104f), t * 2f, aBase, 2, 1.34f);
                drawLightningStreak(c, w * 0.32f, groundY - px(68f), w * 0.50f, groundY - px(104f),
                        t, 990, 0x00FF9800, 0x00FFE838, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 28: // Adamantine Sealing Chains — Minato & Kushina Seal Kurama
                drawKuramaFox(c, w * 0.54f, groundY, t, aBase, 1.25f);
                for (int ch = 0; ch < 4; ch++) {
                    drawLightningStreak(c, w * 0.32f, groundY - px(48f), w * (0.46f + ch * 0.05f), groundY - px(88f - ch * 14f),
                            t, 1000 + ch, 0x00FF8800, 0x00FFE048, 0x00FFFFFF, px(2.8f), aBase, false);
                }
                drawNarutoUzumaki(c, w * 0.32f, groundY - px(58f), t, aBase, 1, 1.32f);
                break;
            case 29: // Shinobi Alliance — United Shinobi Forces March
                for (int sh = 0; sh < 5; sh++) {
                    drawNarutoUzumaki(c, w * (0.28f + sh * 0.085f), groundY - px(56f), t + sh * 0.5f, aBase, sh % 3, 1.24f);
                }
                break;
            case 30: // Desert Battlefield — Uchiha Madara Summons Twin Meteors (Tengai Shinsei)
                paint.setColor((aBase << 24) | 0x0058483C);
                c.drawCircle(w * 0.46f, h * 0.70f + p * px(28f), px(34f), paint);
                c.drawCircle(w * 0.54f, h * 0.65f + p * px(28f), px(28f), paint);
                drawSasukeUchiha(c, w * 0.36f, groundY - px(58f), t, aBase, false, 1.34f);
                break;
            case 31: // Uchiha Brothers — Itachi & Sasuke Dual Susanoo vs. Sage Kabuto
                drawAkatsukiMember(c, w * 0.36f, groundY - px(58f), t, aBase, false, 1.35f);
                drawSasukeUchiha(c, w * 0.48f, groundY - px(58f), t + 0.6f, aBase, false, 1.35f);
                drawLightningStreak(c, w * 0.36f, groundY - px(74f), w * 0.64f, groundY - px(64f),
                        t, 1010, 0x00FF2800, 0x00FF9828, 0x00FFFFFF, px(3.6f), aBase, true);
                drawLightningStreak(c, w * 0.48f, groundY - px(74f), w * 0.64f, groundY - px(64f),
                        t, 1011, 0x007818FF, 0x00C868FF, 0x00FFFFFF, px(3.6f), aBase, true);
                break;
            case 32: // Cataclysm — Revival of the Ten-Tails Jubi & Obito
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeWidth(px(5.2f));
                paint.setColor((aBase * 4 / 5 << 24) | 0x00786458);
                for (int tl = 0; tl < 10; tl++) {
                    double ta = -Math.PI * 0.88 + tl * (Math.PI * 0.76 / 9.0);
                    float tx2 = w * 0.55f + (float) Math.cos(ta) * px(88f);
                    float ty2 = groundY - px(72f) + (float) Math.sin(ta) * px(68f) + (float) Math.sin(t * 4f + tl) * px(6f);
                    c.drawLine(w * 0.54f, groundY - px(56f), tx2, ty2, paint);
                }
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00685850);
                c.drawOval(w * 0.45f, groundY - px(118f), w * 0.63f, groundY, paint);
                // Ten-Tails Concentric Crimson Rinne-Sharingan Eye
                paint.setColor((aBase << 24) | 0x00FF1828);
                c.drawCircle(w * 0.54f, groundY - px(84f), px(13f), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(1.5f));
                paint.setColor((aBase << 24) | 0x00101018);
                c.drawCircle(w * 0.54f, groundY - px(84f), px(8.5f), paint);
                c.drawCircle(w * 0.54f, groundY - px(84f), px(4.5f), paint);
                paint.setStyle(Paint.Style.FILL);
                c.drawCircle(w * 0.54f, groundY - px(84f), px(2.2f), paint);
                drawNarutoUzumaki(c, w * 0.32f, groundY - px(58f), t, aBase, 2, 1.34f);
                break;
            case 33: // Eighth Gate of Death — Might Guy: Night Guy Crimson Dragon Kick vs. Madara
                float guyX = w * (0.28f + p * 0.28f);
                drawDragonCustom(c, guyX + px(18f), groundY - px(64f), t * 2f, aBase, 0xFFFF1828, 1.32f);
                drawNarutoUzumaki(c, guyX, groundY - px(58f), t * 2.5f, aBase, 1, 1.36f);
                drawLightningStreak(c, w * 0.26f, groundY - px(58f), guyX + px(24f), groundY - px(58f),
                        t, 1020, 0x00FF0020, 0x00FF6828, 0x00FFFFFF, px(4.2f), aBase, true);
                break;
            case 34: // Kaguya Dimension — Team Seven Six Paths Chibaku Tensei Sealing
                drawNarutoUzumaki(c, w * 0.32f, groundY - px(58f), t, aBase, 2, 1.34f);
                drawSasukeUchiha(c, w * 0.60f, groundY - px(58f), t + 0.5f, aBase, true, 1.34f);
                drawNinjaSilhouette(c, w * 0.46f, groundY - px(108f), t + 1f, aBase, 0xFFE84878, 0xFFFFFFFF, false, false, false, 0, 1.25f);
                drawAkatsukiMember(c, w * 0.46f, groundY - px(58f), t, aBase, false, 1.38f);
                break;
            case 35: // Scene 36: New Era Konoha — Seventh Hokage Naruto Uzumaki & Shadow Hokage Sasuke
                drawNarutoUzumaki(c, w * 0.40f, groundY - px(58f), t, aBase, 2, 1.38f);
                drawSasukeUchiha(c, w * 0.54f, groundY - px(58f), t + 0.8f, aBase, false, 1.36f);
                break;
            case 36: // Scene 37: Academy Swing — Young Naruto Beneath the Hokage Monument
                paint.setColor((aBase << 24) | 0x004A3424);
                c.drawRect(w * 0.34f, groundY - px(112f), w * 0.37f, groundY, paint);
                c.drawRect(w * 0.32f, groundY - px(112f), w * 0.46f, groundY - px(102f), paint);
                float swA = (float) Math.sin(t * 2.4f) * px(10f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(1.8f));
                paint.setColor((aBase << 24) | 0x00C8A878);
                c.drawLine(w * 0.41f, groundY - px(102f), w * 0.41f + swA, groundY - px(42f), paint);
                c.drawLine(w * 0.44f, groundY - px(102f), w * 0.44f + swA, groundY - px(42f), paint);
                paint.setStyle(Paint.Style.FILL);
                drawNarutoUzumaki(c, w * 0.425f + swA, groundY - px(52f), t * 0.5f, aBase, 0, 1.24f);
                break;
            case 37: // Scene 38: Land of Waves Bridge — Naruto Nine-Tails Red Chakra Awakening
                paint.setColor((aBase / 3 << 24) | 0x00FF2418);
                c.drawCircle(w * 0.45f, groundY - px(58f), px(52f + 8f * (float) Math.sin(t * 14f)), paint);
                for (int ct = 0; ct < 4; ct++) {
                    double ca = t * 4f + ct * Math.PI * 0.5;
                    drawLightningStreak(c, w * 0.45f, groundY - px(58f),
                            w * 0.45f + (float) Math.cos(ca) * px(58f),
                            groundY - px(58f) + (float) Math.sin(ca) * px(46f),
                            t, 1037 + ct, 0x00FF1800, 0x00FF6828, 0x00FFFFFF, px(3.4f), aBase, true);
                }
                drawNarutoUzumaki(c, w * 0.45f, groundY - px(58f), t * 1.8f, aBase, 0, 1.38f);
                break;
            case 38: // Scene 39: Rooftop Clash — Naruto Rasengan vs. Sasuke Chidori Water Tanks
                paint.setColor((aBase << 24) | 0x0058687C);
                c.drawRoundRect(w * 0.26f, groundY - px(96f), w * 0.35f, groundY - px(18f), px(6f), px(6f), paint);
                c.drawRoundRect(w * 0.59f, groundY - px(96f), w * 0.68f, groundY - px(18f), px(6f), px(6f), paint);
                drawNarutoUzumaki(c, w * (0.36f + p * 0.08f), groundY - px(58f), t, aBase, 0, 1.32f);
                drawSasukeUchiha(c, w * (0.58f - p * 0.08f), groundY - px(58f), t + 0.8f, aBase, true, 1.32f);
                drawLightningStreak(c, w * 0.35f, groundY - px(64f), w * 0.59f, groundY - px(64f),
                        t, 1038, 0x000088FF, 0x0068F4FF, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 39: // Scene 40: Curse Mark Two — Winged Sasuke Chidori Lament at Final Valley
                paint.setColor((aBase * 4 / 5 << 24) | 0x00382C48);
                c.drawOval(w * 0.48f, groundY - px(108f), w * 0.58f, groundY - px(54f), paint);
                c.drawOval(w * 0.56f, groundY - px(108f), w * 0.66f, groundY - px(54f), paint);
                drawSasukeUchiha(c, w * 0.56f, groundY - px(58f), t, aBase, true, 1.36f);
                drawNarutoUzumaki(c, w * 0.35f, groundY - px(58f), t + 0.6f, aBase, 1, 1.36f);
                drawLightningStreak(c, w * 0.54f, groundY - px(68f), w * 0.38f, groundY - px(68f),
                        t, 1039, 0x00381868, 0x009858FF, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 40: // Scene 41: Tale of Jiraiya — Sage Mode Jiraiya & Two Great Sage Toads
                drawGamabuntaToad(c, w * 0.46f, groundY, t, aBase, 1.24f);
                drawNinjaSilhouette(c, w * 0.46f, groundY - px(132f), t, aBase, 0xFFD82828, 0xFFF8F8FF, true, true, false, 0xFFFF6818, 1.12f);
                paint.setColor((aBase << 24) | 0x0048A858);
                c.drawCircle(w * 0.43f, groundY - px(152f), px(5.5f), paint);
                c.drawCircle(w * 0.49f, groundY - px(152f), px(5.5f), paint);
                break;
            case 41: // Scene 42: Kage Summit — Gaara Sand Pyramid Sealing & Shukaku Claw
                paint.setColor((aBase * 4 / 5 << 24) | 0x00D8A860);
                actorPath.reset();
                actorPath.moveTo(w * 0.55f, groundY - px(112f));
                actorPath.lineTo(w * 0.66f, groundY);
                actorPath.lineTo(w * 0.44f, groundY);
                actorPath.close();
                c.drawPath(actorPath, paint);
                drawNinjaSilhouette(c, w * 0.34f, groundY - px(58f), t, aBase, 0xFF982430, 0xFFD8A860, true, false, false, 0, 1.35f);
                break;
            case 42: // Scene 43: Shinobi War — Reanimated Hokage Hashirama Thousand-Armed Buddha
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase * 3 / 4 << 24) | 0x00C88848);
                for (int arm = 0; arm < 14; arm++) {
                    double aa = -Math.PI * 0.92 + arm * (Math.PI * 0.84 / 13.0);
                    c.drawLine(w * 0.48f, groundY - px(68f),
                            w * 0.48f + (float) Math.cos(aa) * px(84f),
                            groundY - px(68f) + (float) Math.sin(aa) * px(68f), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                drawNinjaSilhouette(c, w * 0.48f, groundY - px(58f), t, aBase, 0xFFB82828, 0xFF281C18, true, true, false, 0, 1.36f);
                break;
            default: // Scene 44: Baryon Mode — Naruto & Kurama Final Crimson Nuclear Fusion
                paint.setColor((aBase / 3 << 24) | 0x00FF2838);
                c.drawCircle(w * 0.46f, groundY - px(64f), px(62f), paint);
                for (int tail = 0; tail < 9; tail++) {
                    double ta = -Math.PI * 0.9 + tail * (Math.PI * 0.8 / 8.0);
                    drawLightningStreak(c, w * 0.46f, groundY - px(56f),
                            w * 0.46f + (float) Math.cos(ta) * px(78f),
                            groundY - px(56f) + (float) Math.sin(ta) * px(62f),
                            t, 1044 + tail, 0x00FF1828, 0x00FFA828, 0x00FFFFFF, px(3.4f), aBase, false);
                }
                drawNarutoUzumaki(c, w * 0.46f, groundY - px(58f), t * 1.6f, aBase, 2, 1.42f);
                break;
        }
        c.restore();
    }

    /**
     * Unmistakable Naruto Uzumaki silhouette:
     * - Iconic spiky yellow Minato/Naruto hair crown + metallic forehead protector + 2 long fluttering ribbons
     * - Mode 0: Classic Orange/Black Jacket + Swirling Blue Rasengan
     * - Mode 1: Sage Mode Crimson Flame Cloak + Giant Summoning Scroll strapped horizontally across back + Rasenshuriken
     * - Mode 2: Six Paths / Kurama Chakra Mode Golden Cloak + 6 floating black Truth-Seeking Orbs (Gudodama) halo!
     */
    private void drawNarutoUzumaki(Canvas c, float x, float y, float t, int alpha, int mode, float s) {
        float sway = (float) Math.sin(t * 6.0f) * px(5.0f * s);
        float stride = (float) Math.sin(t * 6.5f) * px(5.0f * s);
        int coatCol = mode == 2 ? 0xFFFFB828 : (mode == 1 ? 0xFFD82820 : 0xFFFF7818);

        // Chakra aura
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((Math.max(0, alpha / 5) << 24) | (mode == 2 ? 0x00FFE048 : 0x00FF7818));
        c.drawCircle(x, y + px(4f * s), px(30f * s), paint);

        // Mode 2: 6 Floating Black Truth-Seeking Orbs (Gudodama) in a circle behind Naruto's back
        if (mode == 2) {
            paint.setColor((alpha << 24) | 0x0014101C);
            for (int orb = 0; orb < 6; orb++) {
                double oa = -Math.PI * 0.85 + orb * Math.PI * 0.34;
                float ox = x + (float) Math.cos(oa) * px(24f * s);
                float oy = y - px(6f * s) + (float) Math.sin(oa) * px(24f * s);
                c.drawCircle(ox, oy, px(3.6f * s), paint);
            }
        }

        // Mode 1: Giant Toad Summoning Scroll strapped horizontally behind waist
        if (mode == 1) {
            paint.setColor((alpha << 24) | 0x00C83028);
            c.drawRoundRect(x - px(18f * s), y + px(2f * s), x + px(14f * s), y + px(12f * s), px(4f * s), px(4f * s), paint);
            paint.setColor((alpha << 24) | 0x00FFF0D0);
            c.drawCircle(x - px(17f * s), y + px(7f * s), px(4.5f * s), paint);
        }

        // Articulated shinobi legs, calf wraps & ninja sandals
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(3.4f * s));
        paint.setColor((alpha << 24) | (coatCol & 0xFFFFFF));
        c.drawLine(x - px(5f * s), y + px(16f * s), x - px(11f * s) - stride * 0.5f, y + px(38f * s), paint);
        c.drawLine(x + px(5f * s), y + px(16f * s), x + px(12f * s) + stride * 0.5f, y + px(38f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFF4D8);
        c.drawLine(x - px(9f * s) - stride * 0.4f, y + px(30f * s), x - px(11f * s) - stride * 0.5f, y + px(36f * s), paint);
        c.drawLine(x + px(10f * s) + stride * 0.4f, y + px(30f * s), x + px(12f * s) + stride * 0.5f, y + px(36f * s), paint);

        // Naruto Jacket / Sage Cloak with high collar
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | (coatCol & 0xFFFFFF));
        actorPath.reset();
        actorPath.moveTo(x - px(8f * s), y - px(11f * s));
        actorPath.lineTo(x + px(8f * s), y - px(11f * s));
        actorPath.lineTo(x + px(13f * s), y + px(26f * s));
        actorPath.lineTo(x - px(15f * s) + sway, y + px(28f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Black shoulder yoke
        paint.setColor((alpha << 24) | 0x001A1C24);
        c.drawRect(x - px(7f * s), y - px(9f * s), x + px(7f * s), y - px(1f * s), paint);

        // Head & Iconic Spiky Yellow Naruto Hair + Long Fluttering Headband Tails
        paint.setColor((alpha << 24) | 0x00FFE048);
        float hx = x + px(1f * s), hy = y - px(17f * s);
        c.drawCircle(hx, hy, px(6.2f * s), paint);
        actorPath.reset();
        actorPath.moveTo(hx - px(6f * s), hy - px(2f * s));
        actorPath.lineTo(hx - px(13f * s), hy - px(6f * s));
        actorPath.lineTo(hx - px(6f * s), hy - px(6f * s));
        actorPath.lineTo(hx - px(10f * s), hy - px(13f * s));
        actorPath.lineTo(hx - px(2f * s), hy - px(8f * s));
        actorPath.lineTo(hx + px(2f * s), hy - px(14f * s));
        actorPath.lineTo(hx + px(5f * s), hy - px(7f * s));
        actorPath.lineTo(hx + px(11f * s), hy - px(9f * s));
        actorPath.lineTo(hx + px(6f * s), hy - px(2f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // Silver Konoha Forehead Protector Plate & 2 Long Streaming Headband Ties
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.4f * s));
        paint.setColor((alpha << 24) | 0x001C2230);
        c.drawLine(hx - px(5f * s), hy - px(2.5f * s), hx - px(24f * s), hy - px(6f * s) + sway, paint);
        c.drawLine(hx - px(5f * s), hy - px(0.5f * s), hx - px(21f * s), hy + px(3f * s) - sway * 0.6f, paint);
        paint.setColor((alpha << 24) | 0x00D8E4F0);
        c.drawLine(hx - px(4f * s), hy - px(2.5f * s), hx + px(6f * s), hy - px(2.5f * s), paint);

        // Outstretched Arm & Swirling Rasengan / 4-Blade Rasenshuriken
        paint.setStrokeWidth(px(2.8f * s));
        paint.setColor((alpha << 24) | (coatCol & 0xFFFFFF));
        float handX = x + px(19f * s), handY = y - px(9f * s);
        c.drawLine(x + px(6f * s), y - px(6f * s), handX, handY, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha / 2 << 24) | 0x0038C8FF);
        c.drawCircle(handX + px(5f * s), handY - px(3f * s), px(12f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(handX + px(5f * s), handY - px(3f * s), px(5.5f * s), paint);
        if (mode >= 1) {
            // Rasenshuriken 4 Wind Blades spinning around the core
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(1.8f * s));
            paint.setColor((alpha << 24) | 0x00B8F4FF);
            for (int bl = 0; bl < 4; bl++) {
                double ba = t * 14f + bl * Math.PI / 2.0;
                c.drawLine(handX + px(5f * s), handY - px(3f * s),
                        handX + px(5f * s) + (float) Math.cos(ba) * px(18f * s),
                        handY - px(3f * s) + (float) Math.sin(ba) * px(18f * s), paint);
            }
            paint.setStyle(Paint.Style.FILL);
        }
    }

    /**
     * Unmistakable Sasuke Uchiha silhouette:
     * - Iconic ducktail Uchiha hair spikes at the back of the head + glowing crimson Sharingan eye
     * - Open-collar Uchiha tunic + thick purple Shimenawa rope belt bow at the waist
     * - Straight Kusanagi Chokuto sword + crackling multi-forked electric blue Chidori lightning!
     */
    private void drawSasukeUchiha(Canvas c, float x, float y, float t, int alpha, boolean facingLeft, float s) {
        float dir = facingLeft ? -1f : 1f;
        float stride = (float) Math.sin(t * 6.5f) * px(5.0f * s);

        // Legs
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(3.2f * s));
        paint.setColor((alpha << 24) | 0x00242C4C);
        c.drawLine(x - px(5f * s), y + px(16f * s), x - px(10f * s) - stride * 0.5f, y + px(38f * s), paint);
        c.drawLine(x + px(5f * s), y + px(16f * s), x + px(10f * s) + stride * 0.5f, y + px(38f * s), paint);

        // Torso & Purple Orochimaru Rope Belt (Shimenawa)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00303C68);
        c.drawRoundRect(x - px(8f * s), y - px(10f * s), x + px(8f * s), y + px(22f * s), px(3f * s), px(3f * s), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(3.5f * s));
        paint.setColor((alpha << 24) | 0x009868D8);
        c.drawLine(x - px(10f * s), y + px(8f * s), x + px(10f * s), y + px(8f * s), paint);
        // Kusanagi Chokuto Sword extending from hip
        paint.setStrokeWidth(px(2.2f * s));
        paint.setColor((alpha << 24) | 0x00E0ECF8);
        c.drawLine(x, y + px(8f * s), x - dir * px(28f * s), y + px(20f * s), paint);

        // Head & Ducktail Uchiha Hair Spikes
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00181C2C);
        float hx = x, hy = y - px(17f * s);
        c.drawCircle(hx, hy, px(6.2f * s), paint);
        actorPath.reset();
        actorPath.moveTo(hx, hy - px(6f * s));
        actorPath.lineTo(hx - dir * px(15f * s), hy - px(9f * s)); // Upper ducktail spike
        actorPath.lineTo(hx - dir * px(8f * s), hy - px(3f * s));
        actorPath.lineTo(hx - dir * px(16f * s), hy - px(1f * s)); // Mid ducktail spike
        actorPath.lineTo(hx - dir * px(7f * s), hy + px(2f * s));
        actorPath.lineTo(hx - dir * px(13f * s), hy + px(5f * s)); // Lower ducktail spike
        actorPath.lineTo(hx, hy + px(4f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Glowing Crimson Sharingan Eye
        paint.setColor((alpha << 24) | 0x00FF1828);
        c.drawCircle(hx + dir * px(3.2f * s), hy - px(1f * s), px(1.6f * s), paint);

        // Outstretched Arm & Crackling Multi-Forked Chidori Lightning
        float handX = x + dir * px(18f * s), handY = y - px(8f * s);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha / 3 << 24) | 0x002898FF);
        c.drawCircle(handX, handY, px(16f * s), paint);
        for (int b = 0; b < 5; b++) {
            double ba = ((int) (t * 20f) * 0.7f) + b * Math.PI * 0.4;
            float ex = handX + (float) Math.cos(ba) * px(24f * s);
            float ey = handY + (float) Math.sin(ba) * px(22f * s);
            drawLightningStreak(c, handX, handY, ex, ey, t, b * 23 + 7,
                    0x001888FF, 0x0068E8FF, 0x00FFFFFF, px(2.2f * s), alpha, true);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(handX, handY, px(5.2f * s), paint);
    }

    /**
     * Unmistakable Akatsuki Member (Itachi / Pain / Deidara):
     * Floor-length high-collar black Akatsuki cloak with red inner lining & 3 red Akatsuki clouds!
     */
    private void drawAkatsukiMember(Canvas c, float x, float y, float t, int alpha, boolean facingLeft, float s) {
        float dir = facingLeft ? -1f : 1f;
        float sway = (float) Math.sin(t * 5f) * px(5f * s);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x0014121A);
        actorPath.reset();
        actorPath.moveTo(x - px(8f * s), y - px(14f * s)); // High stiff Akatsuki collar
        actorPath.lineTo(x + px(8f * s), y - px(14f * s));
        actorPath.lineTo(x + px(16f * s) + sway * 0.4f, y + px(36f * s));
        actorPath.lineTo(x - px(16f * s) + sway, y + px(36f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Red Akatsuki clouds on the black cloak
        paint.setColor((alpha << 24) | 0x00E51C23);
        c.drawOval(x - px(7f * s), y + px(2f * s), x + px(4f * s), y + px(9f * s), paint);
        c.drawOval(x - px(4f * s), y + px(18f * s), x + px(8f * s), y + px(25f * s), paint);
        // Head & Glowing Crimson Sharingan Eye
        paint.setColor((alpha << 24) | 0x001C1824);
        c.drawCircle(x, y - px(18f * s), px(5.8f * s), paint);
        paint.setColor((alpha << 24) | 0x00FF1828);
        c.drawCircle(x + dir * px(3f * s), y - px(19f * s), px(1.6f * s), paint);
    }

    /**
     * High-finesse Shinobi / Ninja silhouette fallback
     */
    private void drawNinjaSilhouette(Canvas c, float x, float y, float t, int alpha,
                                     int cloakRgb, int accentRgb, boolean spikyHair,
                                     boolean headbandTail, boolean akatsukiCloak, int jutsuRgb, float s) {
        float sway = (float) Math.sin(t * 6.0f) * px(5.0f * s);
        float stride = (float) Math.sin(t * 6.5f) * px(5.0f * s);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((Math.max(0, alpha / 6) << 24) | (accentRgb & 0xFFFFFF));
        c.drawCircle(x, y + px(4f * s), px(28f * s), paint);

        // Striding shinobi legs & sandals
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(3.2f * s));
        paint.setColor((alpha << 24) | (cloakRgb & 0xFFFFFF));
        c.drawLine(x - px(5f * s), y + px(16f * s), x - px(10f * s) - stride * 0.5f, y + px(40f * s), paint);
        c.drawLine(x + px(5f * s), y + px(16f * s), x + px(11f * s) + stride * 0.5f, y + px(40f * s), paint);

        // Billowing Shinobi / Hokage / Akatsuki Cloak
        paint.setStyle(Paint.Style.FILL);
        actorPath.reset();
        actorPath.moveTo(x - px(7f * s), y - px(10f * s));
        actorPath.lineTo(x + px(8f * s), y - px(10f * s));
        actorPath.cubicTo(x + px(12f * s), y + px(6f * s), x + px(15f * s), y + px(22f * s), x + px(16f * s), y + px(34f * s));
        actorPath.quadTo(x, y + px(29f * s), x - px(18f * s) + sway, y + px(35f * s));
        actorPath.cubicTo(x - px(14f * s) + sway * 0.5f, y + px(16f * s), x - px(10f * s), y, x - px(7f * s), y - px(10f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        if (akatsukiCloak) {
            paint.setColor((alpha << 24) | 0x00E82028);
            c.drawCircle(x - px(3f * s), y + px(10f * s), px(4.2f * s), paint);
            c.drawCircle(x + px(2f * s), y + px(22f * s), px(3.8f * s), paint);
        }

        // Head & Spiky Ninja Hair
        paint.setColor((alpha << 24) | (accentRgb & 0xFFFFFF));
        float hx = x + px(1f * s), hy = y - px(17f * s);
        c.drawCircle(hx, hy, px(6.0f * s), paint);
        if (spikyHair) {
            actorPath.reset();
            actorPath.moveTo(hx - px(6f * s), hy - px(2f * s));
            actorPath.lineTo(hx - px(12f * s), hy - px(6f * s));
            actorPath.lineTo(hx - px(5f * s), hy - px(6f * s));
            actorPath.lineTo(hx - px(9f * s), hy - px(12f * s));
            actorPath.lineTo(hx - px(1f * s), hy - px(8f * s));
            actorPath.lineTo(hx + px(3f * s), hy - px(13f * s));
            actorPath.lineTo(hx + px(5f * s), hy - px(6f * s));
            actorPath.lineTo(hx + px(10f * s), hy - px(8f * s));
            actorPath.lineTo(hx + px(6f * s), hy - px(2f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
        }

        // Fluttering Konoha Forehead Protector Ribbon
        if (headbandTail) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.2f * s));
            c.drawLine(hx - px(5f * s), hy - px(2f * s), hx - px(22f * s), hy - px(5f * s) + sway, paint);
            c.drawLine(hx - px(5f * s), hy, hx - px(19f * s), hy + px(3f * s) - sway * 0.6f, paint);
        }

        // Outstretched Arm & Rasengan / Chidori Sphere
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.6f * s));
        float handX = x + px(19f * s), handY = y - px(9f * s);
        c.drawLine(x + px(6f * s), y - px(6f * s), handX, handY, paint);
        paint.setStyle(Paint.Style.FILL);
        if (jutsuRgb != 0) {
            paint.setColor((alpha / 2 << 24) | (jutsuRgb & 0xFFFFFF));
            c.drawCircle(handX + px(4f * s), handY - px(2f * s), px(11f * s), paint);
            paint.setColor((alpha << 24) | 0x00FFFFFF);
            c.drawCircle(handX + px(4f * s), handY - px(2f * s), px(5.0f * s), paint);
        }
    }

    /**
     * High-finesse Kurama (Nine-Tailed Demon Fox) with 9 undulating chakra tails, long fox ears & Tailed Beast Bomb.
     */
    private void drawKuramaFox(Canvas c, float x, float groundY, float t, int alpha, float s) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(4.0f * s));
        paint.setColor((alpha * 4 / 5 << 24) | 0x00FF6818);
        // 9 Undulating Chakra Tails fanning behind Kurama
        for (int tl = 0; tl < 9; tl++) {
            float ang = -0.35f + tl * 0.16f;
            float wave = (float) Math.sin(t * 5.5f + tl * 0.7f) * px(14f * s);
            actorPath.reset();
            actorPath.moveTo(x + px(18f * s), groundY - px(32f * s));
            actorPath.quadTo(
                    x + px(55f * s) + (float) Math.cos(ang) * px(45f * s),
                    groundY - px(75f * s) - tl * px(8f * s) + wave,
                    x + px(88f * s) + tl * px(6f * s),
                    groundY - px(95f * s) - tl * px(10f * s) - wave);
            c.drawPath(actorPath, paint);
        }
        // Kurama Muscular Fox Torso, Snout & Pointed Fox Ears
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00FF7820);
        actorPath.reset();
        actorPath.moveTo(x + px(24f * s), groundY);
        actorPath.lineTo(x - px(28f * s), groundY);
        actorPath.lineTo(x - px(20f * s), groundY - px(52f * s));
        actorPath.lineTo(x - px(44f * s), groundY - px(58f * s)); // Snout
        actorPath.lineTo(x - px(26f * s), groundY - px(72f * s));
        actorPath.lineTo(x - px(12f * s), groundY - px(96f * s)); // Long fox ear
        actorPath.lineTo(x - px(6f * s), groundY - px(72f * s));
        actorPath.quadTo(x + px(18f * s), groundY - px(56f * s), x + px(24f * s), groundY);
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Tailed Beast Bomb (Bijuudama) in front of Kurama's jaws
        paint.setColor((alpha << 24) | 0x00281038);
        c.drawCircle(x - px(56f * s), groundY - px(58f * s), px(14f * s), paint);
        paint.setColor((alpha * 3 / 4 << 24) | 0x00FF3868);
        c.drawCircle(x - px(56f * s), groundY - px(58f * s), px(7f * s), paint);
    }

    /**
     * Show-accurate Chief Toad Gamabunta (Mount Myoboku):
     * - Crouching muscular crimson-rust amphibian body with bent hind legs & webbed feet
     * - Inflating cream vocal sac & belly with crimson shoulder markings
     * - Signature blue happi coat vest with white collar trim
     * - Giant steel Dosu tanto blade held at his side
     * - Protruding golden amphibian slit eyes, left-eye battle scar, and smoking Kiseru pipe with animated smoke rings
     */
    private void drawGamabuntaToad(Canvas c, float x, float groundY, float t, int alpha, float s) {
        float breathe = (float) Math.sin(t * 3.8f) * px(3.5f * s);
        float sac = Math.max(0f, (float) Math.sin(t * 3.8f)) * px(4.5f * s);

        // 1. Giant Dosu Tanto Sword projecting diagonally behind right shoulder
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(5.2f * s));
        paint.setColor((alpha << 24) | 0x00DCE8F4);
        c.drawLine(x + px(18f * s), groundY - px(44f * s) + breathe, x + px(54f * s), groundY - px(106f * s) + breathe, paint);
        paint.setStrokeWidth(px(6.5f * s));
        paint.setColor((alpha << 24) | 0x00281C2C);
        c.drawLine(x + px(8f * s), groundY - px(26f * s) + breathe, x + px(20f * s), groundY - px(47f * s) + breathe, paint);
        paint.setStrokeWidth(px(2.4f * s));
        paint.setColor((alpha << 24) | 0x00FFD54F);
        c.drawCircle(x + px(19f * s), groundY - px(46f * s) + breathe, px(4.5f * s), paint);

        // 2. Muscular crouching hind legs & webbed feet on groundY
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x009E321E);
        c.drawOval(x - px(48f * s), groundY - px(38f * s) + breathe * 0.5f, x - px(18f * s), groundY, paint);
        c.drawOval(x + px(18f * s), groundY - px(38f * s) + breathe * 0.5f, x + px(48f * s), groundY, paint);
        // Webbed toes
        c.drawOval(x - px(52f * s), groundY - px(9f * s), x - px(20f * s), groundY + px(2f * s), paint);
        c.drawOval(x + px(20f * s), groundY - px(9f * s), x + px(52f * s), groundY + px(2f * s), paint);

        // 3. Broad Crimson-Rust Amphibian Torso & Head Dome
        paint.setColor((alpha << 24) | 0x00B84028);
        c.drawOval(x - px(38f * s), groundY - px(84f * s) + breathe, x + px(38f * s), groundY - px(2f * s), paint);
        c.drawOval(x - px(32f * s), groundY - px(96f * s) + breathe, x + px(32f * s), groundY - px(54f * s) + breathe, paint);

        // 4. Cream-White Belly & Inflating Vocal Throat Sac with Crimson Markings
        paint.setColor((alpha << 24) | 0x00F2E4C8);
        c.drawOval(x - px(24f * s) - sac * 0.4f, groundY - px(68f * s) + breathe,
                x + px(24f * s) + sac * 0.4f, groundY - px(6f * s), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.2f * s));
        paint.setColor((alpha * 4 / 5 << 24) | 0x00781C14);
        c.drawArc(rectF(x - px(20f * s), groundY - px(56f * s) + breathe, x - px(4f * s), groundY - px(36f * s) + breathe), 200, 140, false, paint);
        c.drawArc(rectF(x + px(4f * s), groundY - px(56f * s) + breathe, x + px(20f * s), groundY - px(36f * s) + breathe), 200, 140, false, paint);

        // 5. Blue Happi Coat Vest draped over left & right shoulders
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00244684);
        actorPath.reset();
        actorPath.moveTo(x - px(36f * s), groundY - px(18f * s));
        actorPath.lineTo(x - px(38f * s), groundY - px(66f * s) + breathe);
        actorPath.lineTo(x - px(16f * s), groundY - px(74f * s) + breathe);
        actorPath.lineTo(x - px(18f * s), groundY - px(16f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        actorPath.reset();
        actorPath.moveTo(x + px(36f * s), groundY - px(18f * s));
        actorPath.lineTo(x + px(38f * s), groundY - px(66f * s) + breathe);
        actorPath.lineTo(x + px(16f * s), groundY - px(74f * s) + breathe);
        actorPath.lineTo(x + px(18f * s), groundY - px(16f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // White Happi Lapel Trim & Front Toad Forelimbs
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.0f * s));
        paint.setColor((alpha << 24) | 0x00E8F0F8);
        c.drawLine(x - px(16f * s), groundY - px(74f * s) + breathe, x - px(18f * s), groundY - px(16f * s), paint);
        c.drawLine(x + px(16f * s), groundY - px(74f * s) + breathe, x + px(18f * s), groundY - px(16f * s), paint);

        // 6. Protruding Golden Amphibian Eyes, Horizontal Slit Pupils & Left-Eye Battle Scar
        float eyeY = groundY - px(88f * s) + breathe;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00B84028);
        c.drawCircle(x - px(16f * s), eyeY, px(9.5f * s), paint);
        c.drawCircle(x + px(16f * s), eyeY, px(9.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFE048);
        c.drawCircle(x - px(16f * s), eyeY, px(6.5f * s), paint);
        c.drawCircle(x + px(16f * s), eyeY, px(6.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00140806);
        c.drawRect(x - px(20f * s), eyeY - px(1.2f * s), x - px(12f * s), eyeY + px(1.2f * s), paint);
        c.drawRect(x + px(12f * s), eyeY - px(1.2f * s), x + px(20f * s), eyeY + px(1.2f * s), paint);
        // Left-eye scar
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(1.8f * s));
        paint.setColor((alpha << 24) | 0x0058140E);
        c.drawLine(x + px(14f * s), eyeY - px(10f * s), x + px(18f * s), eyeY + px(8f * s), paint);

        // 7. Wide Toad Mouth & Smoking Kiseru Pipe with Animated Rising Smoke Puffs
        float mouthY = groundY - px(72f * s) + breathe;
        paint.setStrokeWidth(px(2.4f * s));
        paint.setColor((alpha << 24) | 0x0038100A);
        c.drawLine(x - px(20f * s), mouthY, x + px(20f * s), mouthY, paint);
        // Brass & Bamboo Kiseru Pipe
        paint.setStrokeWidth(px(3.0f * s));
        paint.setColor((alpha << 24) | 0x00D8B058);
        float pipeTipX = x + px(42f * s), pipeTipY = mouthY - px(10f * s);
        c.drawLine(x + px(12f * s), mouthY, pipeTipX, pipeTipY, paint);
        paint.setStyle(Paint.Style.FILL);
        c.drawCircle(pipeTipX, pipeTipY - px(2f * s), px(3.8f * s), paint);
        // Animated rising smoke rings from Gamabunta's pipe
        for (int sm = 0; sm < 3; sm++) {
            float sp = ((t * 0.9f + sm * 0.33f) % 1.0f);
            float sx = pipeTipX + px(6f * s) + (float) Math.sin(t * 3f + sm) * px(5f * s) + sp * px(10f * s);
            float sy = pipeTipY - px(8f * s) - sp * px(34f * s);
            int sa = Math.max(0, Math.round(alpha * (1f - sp) * 0.65f));
            paint.setColor((sa << 24) | 0x00DCE6F0);
            c.drawCircle(sx, sy, px((3.5f + sp * 5.5f) * s), paint);
        }
    }

    /**
     * Show-accurate Lady Katsuyu (Giant Slug of Shikkotsu Forest):
     * - Undulating pearlescent ivory-white segmented body with ruffled mantle base
     * - Signature cobalt-blue dorsal & lateral stripes
     * - Animated swaying upper optical eyestalk tentacles & lower sensory feelers
     * - Rising cyan-emerald healing chakra motes
     */
    private void drawKatsuyuSlug(Canvas c, float x, float groundY, float t, int alpha, float s) {
        float sway = (float) Math.sin(t * 4.0f) * px(5.5f * s);
        float bob = (float) Math.cos(t * 3.2f) * px(3.0f * s);

        // 1. Wide Ruffled Slug Foot Base & Undulating Rearing Body Column
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00DCEEFF);
        c.drawOval(x - px(36f * s), groundY - px(22f * s), x + px(36f * s), groundY + px(2f * s), paint);

        paint.setColor((alpha << 24) | 0x00F2F9FF);
        actorPath.reset();
        actorPath.moveTo(x - px(32f * s), groundY - px(4f * s));
        actorPath.cubicTo(
                x - px(28f * s) - sway * 0.4f, groundY - px(36f * s),
                x - px(22f * s) + sway * 0.6f, groundY - px(64f * s),
                x - px(14f * s) + sway, groundY - px(84f * s) + bob);
        actorPath.quadTo(x + sway, groundY - px(94f * s) + bob, x + px(14f * s) + sway, groundY - px(84f * s) + bob);
        actorPath.cubicTo(
                x + px(22f * s) + sway * 0.6f, groundY - px(64f * s),
                x + px(28f * s) - sway * 0.4f, groundY - px(36f * s),
                x + px(32f * s), groundY - px(4f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 2. Signature Cobalt-Blue Dorsal & Lateral Stripes (Anime-Accurate Katsuyu Markings)
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(3.2f * s));
        paint.setColor((alpha * 4 / 5 << 24) | 0x002888E8);
        for (int st = -1; st <= 1; st++) {
            actorPath.reset();
            actorPath.moveTo(x + st * px(18f * s), groundY - px(8f * s));
            actorPath.cubicTo(
                    x + st * px(15f * s) - sway * 0.3f, groundY - px(36f * s),
                    x + st * px(12f * s) + sway * 0.5f, groundY - px(62f * s),
                    x + st * px(8f * s) + sway, groundY - px(82f * s) + bob);
            c.drawPath(actorPath, paint);
        }

        // Soft segmented body rings
        paint.setStrokeWidth(px(1.5f * s));
        paint.setColor((alpha / 2 << 24) | 0x0098C8F0);
        for (int rg = 1; rg <= 3; rg++) {
            float ry = groundY - px(rg * 20f * s) + bob * 0.4f;
            float rw = px((28f - rg * 3.5f) * s);
            c.drawArc(rectF(x - rw + sway * 0.3f * rg, ry - px(5f * s), x + rw + sway * 0.3f * rg, ry + px(5f * s)), 15, 150, false, paint);
        }

        // 3. Animated Swaying Upper Optical Tentacles (Eyestalks) & Lower Feelers
        float headX = x + sway, headY = groundY - px(84f * s) + bob;
        float feelerSwayL = (float) Math.sin(t * 5.2f) * px(5f * s);
        float feelerSwayR = (float) Math.cos(t * 5.2f + 0.8f) * px(5f * s);
        paint.setStrokeWidth(px(3.0f * s));
        paint.setColor((alpha << 24) | 0x00F2F9FF);
        // Upper optical tentacles
        float tipLX = headX - px(16f * s) + feelerSwayL, tipLY = headY - px(22f * s);
        float tipRX = headX + px(16f * s) + feelerSwayR, tipRY = headY - px(22f * s);
        c.drawLine(headX - px(8f * s), headY - px(2f * s), tipLX, tipLY, paint);
        c.drawLine(headX + px(8f * s), headY - px(2f * s), tipRX, tipRY, paint);
        // Lower sensory feelers
        paint.setStrokeWidth(px(2.0f * s));
        c.drawLine(headX - px(10f * s), headY + px(4f * s), headX - px(20f * s) - feelerSwayL * 0.5f, headY - px(4f * s), paint);
        c.drawLine(headX + px(10f * s), headY + px(4f * s), headX + px(20f * s) - feelerSwayR * 0.5f, headY - px(4f * s), paint);

        // Rounded optical bulbs at the tips of the upper tentacles
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00F2F9FF);
        c.drawCircle(tipLX, tipLY, px(4.0f * s), paint);
        c.drawCircle(tipRX, tipRY, px(4.0f * s), paint);
        paint.setColor((alpha << 24) | 0x002888E8);
        c.drawCircle(tipLX, tipLY, px(2.0f * s), paint);
        c.drawCircle(tipRX, tipRY, px(2.0f * s), paint);

        // 4. Shikkotsu Healing Chakra Motes rising around Katsuyu
        for (int m = 0; m < 4; m++) {
            float mp = ((t * 0.8f + m * 0.25f) % 1.0f);
            float mx = x + (m % 2 == 0 ? -1 : 1) * px((18f + m * 6f) * s) + (float) Math.sin(t * 3f + m) * px(4f * s);
            float my = groundY - px(16f * s) - mp * px(72f * s);
            int ma = Math.max(0, Math.round(alpha * (1f - mp) * 0.75f));
            paint.setColor((ma << 24) | 0x0058F8D8);
            c.drawCircle(mx, my, px(2.8f * s), paint);
        }
    }

    /**
     * High-Voltage Jagged, Multi-Layered, Branching Lightning Streak Engine (v6.0 120Hz Sub-Frame Plasma):
     * Renders a true crackling lightning bolt with sharp zig-zag vertices, sub-frame smoothstep interpolation
     * across 120Hz frames, 4-layer volumetric plasma glow, endpoint corona flares, and forked secondary branches.
     */
    private void drawLightningStreak(Canvas c, float x1, float y1, float x2, float y2,
                                     float t, int seed, int outerGlowRgb, int midBoltRgb, int coreRgb,
                                     float widthPx, int alpha, boolean forkBranches) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float len = (float) Math.hypot(dx, dy);
        if (len < 1f || alpha <= 4) return;
        float nx = -dy / len;
        float ny = dx / len;
        float maxJitter = Math.min(px(22f), len * 0.16f);
        float rawTick = t * 26f;
        int tick0 = ((int) rawTick) + seed * 19;
        int tick1 = tick0 + 1;
        float blend = smoothstep(rawTick - (int) rawTick);
        int segs = 12;

        // Build main jagged zig-zag bolt path in actorPath with 120Hz sub-frame plasma interpolation
        actorPath.reset();
        actorPath.moveTo(x1, y1);
        for (int i = 1; i < segs; i++) {
            float f = i / (float) segs;
            float env = (float) Math.sin(f * Math.PI);
            int h0 = (tick0 * 37 + i * 73 + seed * 131) & 0xFF;
            int h1 = (tick1 * 37 + i * 73 + seed * 131) & 0xFF;
            float hLerp = h0 + (h1 - h0) * blend;
            float microWave = (float) Math.sin(t * 78f + i * 1.9f + seed) * 0.14f;
            float zig = ((i % 2 == 0) ? 1f : -1f) * (0.40f + (hLerp / 255f) * 0.85f + microWave);
            float off = zig * maxJitter * (0.35f + 0.65f * env);
            actorPath.lineTo(x1 + dx * f + nx * off, y1 + dy * f + ny * off);
        }
        actorPath.lineTo(x2, y2);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.MITER);

        // Pass 0: Atmospheric wide outer corona halo
        paint.setStrokeWidth(widthPx * 5.2f);
        paint.setColor((Math.max(0, alpha / 8) << 24) | (outerGlowRgb & 0xFFFFFF));
        c.drawPath(actorPath, paint);

        // Pass 1: Wide outer electric plasma bloom
        paint.setStrokeWidth(widthPx * 3.2f);
        paint.setColor((Math.max(0, alpha * 2 / 7) << 24) | (outerGlowRgb & 0xFFFFFF));
        c.drawPath(actorPath, paint);

        // Pass 2: High-voltage saturated mid bolt
        paint.setStrokeWidth(widthPx * 1.65f);
        paint.setColor((Math.max(0, alpha * 4 / 5) << 24) | (midBoltRgb & 0xFFFFFF));
        c.drawPath(actorPath, paint);

        // Pass 3: Razor-sharp blinding white-hot core
        paint.setStrokeWidth(widthPx * 0.68f);
        paint.setColor((alpha << 24) | (coreRgb & 0xFFFFFF));
        c.drawPath(actorPath, paint);

        // Forked secondary lightning branches shooting off the main trunk
        if (forkBranches) {
            for (int b = 1; b <= 3; b++) {
                int idx = b * 3;
                float f = idx / (float) segs;
                int h0 = (tick0 * 37 + idx * 73 + seed * 131) & 0xFF;
                int h1 = (tick1 * 37 + idx * 73 + seed * 131) & 0xFF;
                float hLerp = h0 + (h1 - h0) * blend;
                float zig = ((idx % 2 == 0) ? 1f : -1f) * (0.40f + (hLerp / 255f) * 0.85f);
                float off = zig * maxJitter * 0.8f;
                float bx0 = x1 + dx * f + nx * off;
                float by0 = y1 + dy * f + ny * off;
                float side = (b % 2 == 0) ? 1f : -1f;
                float bLen = Math.min(px(34f), len * 0.25f);
                float bx1 = bx0 + (dx / len) * bLen * 0.55f + nx * side * bLen * 0.48f;
                float by1 = by0 + (dy / len) * bLen * 0.55f + ny * side * bLen * 0.48f;
                float bx2 = bx1 + (dx / len) * bLen * 0.50f + nx * side * bLen * 0.22f;
                float by2 = by1 + (dy / len) * bLen * 0.50f + ny * side * bLen * 0.22f;

                paint.setStrokeWidth(widthPx * 1.15f);
                paint.setColor((Math.max(0, alpha * 3 / 4) << 24) | (midBoltRgb & 0xFFFFFF));
                c.drawLine(bx0, by0, bx1, by1, paint);
                c.drawLine(bx1, by1, bx2, by2, paint);
                paint.setStrokeWidth(widthPx * 0.45f);
                paint.setColor((alpha << 24) | (coreRgb & 0xFFFFFF));
                c.drawLine(bx0, by0, bx1, by1, paint);
            }
        }
        paint.setStyle(Paint.Style.FILL);
        // Endpoint plasma contact flare
        paint.setColor((Math.max(0, alpha / 3) << 24) | (midBoltRgb & 0xFFFFFF));
        c.drawCircle(x2, y2, widthPx * 2.2f, paint);
        paint.setColor((Math.max(0, alpha * 3 / 4) << 24) | (coreRgb & 0xFFFFFF));
        c.drawCircle(x2, y2, widthPx * 0.95f, paint);
    }

    /**
     * Detailed 4-Winged Glowing Butterfly for Shinobu Kocho Insect Breathing
     */
    private void drawGlowingButterfly(Canvas c, float x, float y, float t, float size, int wingRgb, int alpha) {
        float flap = 0.35f + 0.65f * (float) Math.abs(Math.sin(t * 14f + x * 0.05f));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha / 3 << 24) | (wingRgb & 0xFFFFFF));
        c.drawCircle(x, y, size * 1.4f, paint);
        paint.setColor((alpha << 24) | (wingRgb & 0xFFFFFF));
        // Upper forewings
        c.drawOval(x - size * flap, y - size * 0.85f, x, y + size * 0.1f, paint);
        c.drawOval(x, y - size * 0.85f, x + size * flap, y + size * 0.1f, paint);
        // Lower hindwings
        c.drawOval(x - size * 0.75f * flap, y - size * 0.1f, x, y + size * 0.75f, paint);
        c.drawOval(x, y - size * 0.1f, x + size * 0.75f * flap, y + size * 0.75f, paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(x, y, size * 0.22f, paint);
    }

    private void drawDemonSlayerStoryLoop(Canvas c, int w, int h, float t) {
        int count = DEMON_SLAYER_TITLES.length;
        float totalCycle = CHAPTER_SEC * count;
        float cycleTime = t % totalCycle;
        int scene = resolveActiveScene(cycleTime, count);
        float p = (cycleTime % CHAPTER_SEC) / CHAPTER_SEC;
        float fade = resolveStoryFade(p);
        int aBase = Math.min(255, Math.max(0, Math.round(fade * 255f)));
        float groundY = h * 0.900f;
        drawStoryCaptionAndDots(c, w, h, DEMON_SLAYER_TITLES[scene], scene, count, aBase,
                0x00D898FF, 0xEE48E8FF, 0x42D898FF, px(10.5f));

        c.save();
        applyStageTransform(c, w, h, groundY);
        switch (scene) {
            case 0: // Tanjiro & Nezuko in the Snow
                for (int sn = 0; sn < 10; sn++) {
                    float sx = w * (0.28f + ((sn * 0.07f + p * 0.25f) % 0.42f));
                    float sy = groundY - px(110f) + ((t * 28f + sn * 19f) % px(105f));
                    paint.setColor((aBase * 2 / 3 << 24) | 0x00E8F4FF);
                    c.drawCircle(sx, sy, px(2.2f), paint);
                }
                drawTanjiroKamado(c, w * 0.40f, groundY - px(58f), t, aBase, false, 1.32f);
                drawNezukoKamado(c, w * 0.54f, groundY - px(52f), t + 1f, aBase, 1.24f);
                break;
            case 1: // Tanjiro Slicing the Giant Boulder
                // Sculpted Granite Boulder with Split Halves & Sabito Fox Mask
                paint.setColor((aBase << 24) | 0x00485264);
                c.drawOval(w * 0.51f, groundY - px(84f), w * 0.62f, groundY, paint);
                drawLightningStreak(c, w * 0.49f, groundY - px(92f), w * 0.64f, groundY - px(2f),
                        t, 11, 0x001888FF, 0x0058F0FF, 0x00FFFFFF, px(3.0f), aBase, false);
                drawTanjiroKamado(c, w * 0.36f, groundY - px(58f), t, aBase, false, 1.34f);
                break;
            case 2: // Final Selection — Mount Fujikasane Wisteria Trees
                drawTanjiroKamado(c, w * (0.30f + p * 0.28f), groundY - px(58f), t, aBase, false, 1.32f);
                for (int pet = 0; pet < 14; pet++) {
                    float pxP = w * (0.24f + ((p + pet * 0.07f) % 1.0f) * 0.46f);
                    float pyP = h * 0.72f + (pet % 4) * px(24f) + (float) Math.sin(t * 4f + pet) * px(5f);
                    paint.setColor((aBase * 4 / 5 << 24) | (pet % 2 == 0 ? 0x00D878FF : 0x00F0B8FF));
                    c.drawOval(pxP - px(4f), pyP - px(2.2f), pxP + px(4f), pyP + px(2.2f), paint);
                }
                break;
            case 3: // Zenitsu Thunderclap and Flash (First Form — Godspeed Lightning Streak!)
                float startX = w * 0.25f;
                float zx = w * (0.28f + p * 0.36f);
                float zy = groundY - px(42f);
                // 1. Sonic Boom Launch Shockwave Ring at kickoff point
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase * 3 / 4 << 24) | 0x00FFE848);
                c.drawOval(startX - px(10f), groundY - px(74f), startX + px(14f), groundY - px(4f), paint);
                paint.setStrokeWidth(px(1.6f));
                paint.setColor((aBase << 24) | 0x00FFFFFF);
                c.drawOval(startX - px(6f), groundY - px(64f), startX + px(10f), groundY - px(12f), paint);
                paint.setStyle(Paint.Style.FILL);
                // 2. High-Speed Golden Afterimage Ghosts of Zenitsu along the dash path
                for (int gh = 1; gh <= 3; gh++) {
                    float gf = gh / 4.0f;
                    float gx = startX + (zx - startX) * gf;
                    drawZenitsuAgatsuma(c, gx, groundY - px(56f), t * 2f - gh * 0.2f, aBase * gh / 6, 1.28f);
                }
                // 3. Three Jagged, Multi-Branched High-Voltage Thunderbolts (Golden + Electric Cyan)
                drawLightningStreak(c, startX, zy - px(6f), zx + px(24f), zy - px(10f),
                        t, 101, 0x00FF8800, 0x00FFE828, 0x00FFFFFF, px(4.6f), aBase, true);
                drawLightningStreak(c, startX + px(12f), zy + px(8f), zx + px(14f), zy + px(4f),
                        t, 202, 0x000078FF, 0x0048E8FF, 0x00FFFFFF, px(3.2f), aBase, true);
                drawLightningStreak(c, startX + px(24f), zy - px(22f), zx + px(30f), zy - px(20f),
                        t, 303, 0x00FFA000, 0x00FFF068, 0x00FFFFFF, px(2.6f), aBase * 4 / 5, true);
                // 4. Zenitsu in Crouched Iaijutsu Unsheathing Slash
                drawZenitsuAgatsuma(c, zx, groundY - px(56f), t * 2f, aBase, 1.35f);
                break;
            case 4: // Giyu Tomioka Eleventh Form: Dead Calm Water Breathing
                drawSlayerSilhouette(c, w * 0.46f, groundY - px(58f), t, aBase, 0xFF982438, 0xFF38D8FF, 0xFF20B8FF, 1.35f);
                paint.setStyle(Paint.Style.STROKE);
                for (int r = 1; r <= 4; r++) {
                    paint.setStrokeWidth(px(2.4f));
                    paint.setColor((aBase * (5 - r) / 5 << 24) | 0x0038D8FF);
                    c.drawOval(w * 0.46f - px(32f * r), groundY - px(8f * r), w * 0.46f + px(32f * r), groundY + px(8f * r), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                break;
            case 5: // Hinokami Kagura Dance of the Fire God & Nezuko Blood Demon Art
            case 11: { // Infinity Castle — Tanjiro Sun Dragon vs. Muzan
                float tx = w * 0.32f;
                drawTanjiroKamado(c, tx, groundY - px(58f), t, aBase, true, 1.35f);
                drawDragonCustom(c, w * (0.46f + p * 0.12f), h * 0.76f, t, aBase, 0xFFFF5018, 1.18f);
                drawLightningStreak(c, tx + px(18f), groundY - px(64f), w * (0.46f + p * 0.12f), h * 0.76f,
                        t, 404, 0x00FF2800, 0x00FF9820, 0x00FFFFAA, px(3.4f), aBase, true);
                if (scene == 11) {
                    drawMuzanKibutsuji(c, w * 0.64f, groundY - px(58f), t, aBase, 1.34f);
                } else {
                    drawNezukoKamado(c, w * 0.62f, groundY - px(52f), t + 0.8f, aBase, 1.25f);
                }
                break;
            }
            case 6: // Shinobu Kocho Insect Breathing Butterfly Dance
                drawSlayerSilhouette(c, w * 0.46f, groundY - px(66f) + (float) Math.sin(t * 5f) * px(12f), t, aBase, 0xFFC868FF, 0xFF68F8D8, 0xFFD878FF, 1.32f);
                for (int bf = 0; bf < 8; bf++) {
                    float bx = w * 0.46f + (float) Math.cos(t * 2.5f + bf * 0.8f) * px(68f);
                    float by = groundY - px(64f) + (float) Math.sin(t * 3.5f + bf * 0.9f) * px(34f);
                    drawGlowingButterfly(c, bx, by, t + bf, px(7.5f), bf % 2 == 0 ? 0x00D878FF : 0x0058F8E0, aBase);
                }
                break;
            case 7: // Mugen Train — Infinity Steam Locomotive
                drawHogwartsExpressScene(c, w, h, groundY, t, p, aBase);
                drawRengokuFlameHashira(c, w * (0.24f + p * 0.36f), groundY - px(68f), t, aBase, 1.18f);
                break;
            case 8: // Set Your Heart Ablaze — Kyojuro Rengoku vs. Akaza
                drawRengokuFlameHashira(c, w * 0.35f, groundY - px(58f), t, aBase, 1.36f);
                drawNinjaSilhouette(c, w * 0.60f, groundY - px(58f), t + 1f, aBase, 0xFF283868, 0xFFFF3878, true, false, false, 0xFF38C8FF, 1.34f);
                // Akaza's 12-Point Glowing Cyan Destructive Death: Compass Needle Snowflake
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.0f));
                paint.setColor((aBase * 4 / 5 << 24) | 0x0038D8FF);
                c.drawOval(w * 0.60f - px(44f), groundY - px(14f), w * 0.60f + px(44f), groundY + px(10f), paint);
                for (int sp = 0; sp < 6; sp++) {
                    double sa = sp * Math.PI / 6.0;
                    float dxS = (float) Math.cos(sa) * px(44f);
                    float dyS = (float) Math.sin(sa) * px(12f);
                    c.drawLine(w * 0.60f - dxS, groundY - px(2f) - dyS, w * 0.60f + dxS, groundY - px(2f) + dyS, paint);
                }
                paint.setStyle(Paint.Style.FILL);
                // Clash between Rengoku's Ninth Form Flame & Akaza's Shockwave
                drawLightningStreak(c, w * 0.38f, groundY - px(58f), w * 0.56f, groundY - px(58f),
                        t, 505, 0x00FF2400, 0x00FFB820, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 9: // Entertainment District — Tengen Uzui vs. Gyutaro
                drawSlayerSilhouette(c, w * 0.36f, groundY - px(58f), t * 1.5f, aBase, 0xFF384868, 0xFFFFB830, 0xFFFF8820, 1.35f);
                drawSlayerSilhouette(c, w * 0.58f, groundY - px(58f), t * 1.5f + 1f, aBase, 0xFF284838, 0xFFFF2848, 0xFFD81838, 1.32f);
                drawLightningStreak(c, w * 0.39f, groundY - px(64f), w * 0.55f, groundY - px(64f),
                        t, 606, 0x00FF6800, 0x00FFE038, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 10: // Swordsmith Village — Muichiro Mist Breathing
                for (int m = 0; m < 6; m++) {
                    paint.setColor((aBase / 4 << 24) | 0x0088E8F8);
                    c.drawOval(w * (0.28f + m * 0.06f), groundY - px(46f), w * (0.38f + m * 0.06f), groundY - px(10f), paint);
                }
                drawSlayerSilhouette(c, w * 0.48f, groundY - px(58f), t, aBase, 0xFF286878, 0xFF98F8FF, 0xFF78E8FF, 1.34f);
                break;
            case 12: // Mount Sagiri — Sakonji Urokodaki & Crimson Tengu Mask Training
                drawSlayerSilhouette(c, w * 0.35f, groundY - px(58f), t, aBase, 0xFF2888C8, 0xFF58D8FF, 0xFF28A8FF, 1.34f);
                paint.setColor((aBase << 24) | 0x00E82428);
                c.drawCircle(w * 0.35f + px(3f), groundY - px(94f), px(8.5f), paint);
                drawTanjiroKamado(c, w * 0.56f, groundY - px(58f), t + 0.8f, aBase, false, 1.30f);
                break;
            case 13: // Northwest Town — Tanjiro Second Form: Water Wheel
                float wwX = w * (0.34f + p * 0.24f), wwY = groundY - px(74f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(7.5f));
                paint.setColor((aBase * 3 / 4 << 24) | 0x001898FF);
                c.drawCircle(wwX, wwY, px(42f), paint);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase << 24) | 0x00FFFFFF);
                c.drawCircle(wwX, wwY, px(39f), paint);
                paint.setStyle(Paint.Style.FILL);
                drawTanjiroKamado(c, wwX, wwY + px(14f), t * 2f, aBase, false, 1.28f);
                break;
            case 14: // Asakusa Streets — Lady Tamayo's Blood Demon Spell
                drawSlayerSilhouette(c, w * 0.42f, groundY - px(58f), t, aBase, 0xFF682888, 0xFFFF58A8, 0xFFD848FF, 1.32f);
                for (int fl = 0; fl < 9; fl++) {
                    float fx = w * (0.30f + fl * 0.042f);
                    float fy = groundY - px(48f) - (float) Math.sin(t * 3.5f + fl) * px(26f);
                    drawGlowingButterfly(c, fx, fy, t + fl, px(6.8f), fl % 2 == 0 ? 0x00FF3878 : 0x00B848FF, aBase);
                }
                break;
            case 15: // Temari & Arrows — Tanjiro & Nezuko vs. Susamaru & Yahaba
                drawTanjiroKamado(c, w * 0.33f, groundY - px(58f), t, aBase, false, 1.30f);
                drawNezukoKamado(c, w * 0.45f, groundY - px(52f), t + 0.6f, aBase, 1.24f);
                drawSlayerSilhouette(c, w * 0.61f, groundY - px(58f), t + 1.2f, aBase, 0xFFFF7828, 0xFFFF2848, 0xFFFF3858, 1.30f);
                float temX = w * (0.38f + (float) Math.abs(Math.sin(t * 5f)) * 0.20f);
                float temY = groundY - px(62f) - (float) Math.abs(Math.cos(t * 5f)) * px(24f);
                paint.setColor((aBase << 24) | 0x00FF2858);
                c.drawCircle(temX, temY, px(9f), paint);
                drawLightningStreak(c, w * 0.58f, groundY - px(68f), temX, temY,
                        t, 615, 0x00FF1838, 0x00FF6888, 0x00FFFFFF, px(2.6f), aBase, false);
                break;
            case 16: // Tsuzumi Mansion — Inosuke Hashibira Beast Breathing Dual Serrated Blades
                drawSlayerSilhouette(c, w * 0.46f, groundY - px(58f), t * 1.4f, aBase, 0xFF385888, 0xFF88C8FF, 0xFF58A8FF, 1.36f);
                // Boar Mask Snout & Tusks
                paint.setColor((aBase << 24) | 0x00A89088);
                c.drawCircle(w * 0.46f + px(3f), groundY - px(93f), px(9f), paint);
                drawLightningStreak(c, w * 0.34f, groundY - px(96f), w * 0.58f, groundY - px(32f),
                        t, 616, 0x002878FF, 0x0088D8FF, 0x00FFFFFF, px(3.4f), aBase, true);
                drawLightningStreak(c, w * 0.34f, groundY - px(32f), w * 0.58f, groundY - px(96f),
                        t, 617, 0x002878FF, 0x0088D8FF, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 17: // Spider Mountain — Zenitsu Sixfold Thunderclap and Flash
                float[] zxPts = {0.26f, 0.58f, 0.30f, 0.62f, 0.34f, 0.52f};
                float[] zyPts = {0.88f, 0.82f, 0.76f, 0.72f, 0.68f, 0.78f};
                for (int s = 0; s < 5; s++) {
                    drawLightningStreak(c, w * zxPts[s], h * zyPts[s], w * zxPts[s + 1], h * zyPts[s + 1],
                            t, 620 + s, 0x00FF9800, 0x00FFE838, 0x00FFFFFF, px(3.4f), aBase, true);
                }
                drawZenitsuAgatsuma(c, w * 0.52f, h * 0.78f - px(18f), t * 2f, aBase, 1.34f);
                break;
            case 18: // Mount Natagumo — Tanjiro Tenth Form: Constant Flux Water Dragon vs. Rui
                drawTanjiroKamado(c, w * 0.32f, groundY - px(58f), t, aBase, false, 1.34f);
                drawDragonCustom(c, w * (0.45f + p * 0.12f), h * 0.76f, t, aBase, 0xFF28B8FF, 1.22f);
                drawSlayerSilhouette(c, w * 0.63f, groundY - px(54f), t + 1f, aBase, 0xFFE8EEF8, 0xFFFF2438, 0xFFFF2438, 1.24f);
                drawLightningStreak(c, w * 0.63f, groundY - px(64f), w * 0.46f, h * 0.76f,
                        t, 630, 0x00D80024, 0x00FF3858, 0x00FFFFFF, px(2.6f), aBase, false);
                break;
            case 19: // Ubuyashiki Estate — The Nine Hashira Pillars Assemble
                drawRengokuFlameHashira(c, w * 0.28f, groundY - px(56f), t, aBase, 1.20f);
                drawSlayerSilhouette(c, w * 0.37f, groundY - px(56f), t + 0.3f, aBase, 0xFF982438, 0xFF38D8FF, 0xFF28B8FF, 1.20f);
                drawSlayerSilhouette(c, w * 0.46f, groundY - px(56f), t + 0.6f, aBase, 0xFFC868FF, 0xFF68F8D8, 0xFFD878FF, 1.18f);
                drawSlayerSilhouette(c, w * 0.55f, groundY - px(56f), t + 0.9f, aBase, 0xFF384868, 0xFFFFB830, 0xFFFF8820, 1.22f);
                drawSlayerSilhouette(c, w * 0.64f, groundY - px(60f), t + 1.2f, aBase, 0xFF586848, 0xFFD8E0E8, 0xFFA8C888, 1.28f);
                break;
            case 20: // Butterfly Garden — Kanao Tsuyuri Flower Breathing
                drawSlayerSilhouette(c, w * 0.45f, groundY - px(58f), t, aBase, 0xFFF4ECF8, 0xFFFF78B8, 0xFFFF58A8, 1.32f);
                for (int bf = 0; bf < 10; bf++) {
                    float bx = w * (0.28f + bf * 0.038f);
                    float by = groundY - px(42f + (bf % 3) * 22f) + (float) Math.sin(t * 4f + bf) * px(10f);
                    drawGlowingButterfly(c, bx, by, t + bf, px(7.2f), bf % 2 == 0 ? 0x00FF78B8 : 0x0068F8E0, aBase);
                }
                break;
            case 21: // Mugen Train Roof — Tanjiro & Inosuke vs. Enmu
                drawHogwartsExpressScene(c, w, h, groundY, t, p, aBase);
                drawTanjiroKamado(c, w * 0.36f, groundY - px(68f), t, aBase, true, 1.18f);
                drawSlayerSilhouette(c, w * 0.50f, groundY - px(68f), t + 0.7f, aBase, 0xFF385888, 0xFF88C8FF, 0xFF58A8FF, 1.18f);
                break;
            case 22: // Flame Breathing — Rengoku Fifth Form: Flame Tiger
                drawRengokuFlameHashira(c, w * 0.32f, groundY - px(58f), t, aBase, 1.36f);
                drawDragonCustom(c, w * (0.48f + p * 0.12f), groundY - px(64f), t * 1.5f, aBase, 0xFFFF6818, 1.26f);
                drawLightningStreak(c, w * 0.36f, groundY - px(64f), w * (0.52f + p * 0.12f), groundY - px(64f),
                        t, 640, 0x00FF2800, 0x00FFC828, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 23: // Yoshiwara Rooftops — Zenitsu Godspeed Thunderclap & Flash vs. Daki
                float gzx = w * (0.28f + p * 0.34f);
                drawLightningStreak(c, w * 0.25f, groundY - px(58f), gzx + px(22f), groundY - px(58f),
                        t, 645, 0x00FF8800, 0x00FFE838, 0x00FFFFFF, px(4.6f), aBase, true);
                drawLightningStreak(c, w * 0.25f, groundY - px(44f), gzx + px(18f), groundY - px(44f),
                        t, 646, 0x00FF2888, 0x00FF78C8, 0x00FFFFFF, px(3.2f), aBase, true);
                drawZenitsuAgatsuma(c, gzx, groundY - px(56f), t * 2.4f, aBase, 1.35f);
                break;
            case 24: // Burning Yoshiwara — Awakened Nezuko Exploding Blood vs. Daki
                drawNezukoKamado(c, w * 0.36f, groundY - px(54f), t, aBase, 1.34f);
                drawSlayerSilhouette(c, w * 0.58f, groundY - px(58f), t + 1f, aBase, 0xFFD82878, 0xFFFF68B8, 0xFFFF3898, 1.32f);
                drawLightningStreak(c, w * 0.39f, groundY - px(64f), w * 0.56f, groundY - px(64f),
                        t, 650, 0x00FF1868, 0x00FF78C8, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 25: // Musical Score Technique — Tengen Uzui & Tanjiro Behead Gyutaro
                drawSlayerSilhouette(c, w * 0.31f, groundY - px(58f), t * 1.6f, aBase, 0xFF384868, 0xFFFFB830, 0xFFFF8820, 1.32f);
                drawTanjiroKamado(c, w * 0.44f, groundY - px(58f), t * 1.6f, aBase, true, 1.32f);
                drawSlayerSilhouette(c, w * 0.60f, groundY - px(58f), t * 1.6f + 1f, aBase, 0xFF284838, 0xFFFF1838, 0xFFD80028, 1.34f);
                drawLightningStreak(c, w * 0.35f, groundY - px(64f), w * 0.58f, groundY - px(64f),
                        t, 655, 0x00FF4800, 0x00FFD838, 0x00FFFFFF, px(4.2f), aBase, true);
                drawLightningStreak(c, w * 0.60f, groundY - px(64f), w * 0.44f, groundY - px(52f),
                        t, 656, 0x00B80018, 0x00FF2848, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 26: // Swordsmith Village — Hotaru Haganezuka Forging the Ancestral Katana
                drawSlayerSilhouette(c, w * 0.44f, groundY - px(58f), t * 1.8f, aBase, 0xFF883828, 0xFFFFB838, 0xFFFF6818, 1.34f);
                paint.setColor((aBase << 24) | 0x0048505C);
                c.drawRect(w * 0.49f, groundY - px(32f), w * 0.57f, groundY, paint);
                for (int sp = 0; sp < 4; sp++) {
                    double sa = -Math.PI * 0.8 + sp * 0.45;
                    drawLightningStreak(c, w * 0.53f, groundY - px(32f),
                            w * 0.53f + (float) Math.cos(sa) * px(52f),
                            groundY - px(32f) + (float) Math.sin(sa) * px(52f),
                            t, 660 + sp, 0x00FF4800, 0x00FFE048, 0x00FFFFFF, px(2.4f), aBase, false);
                }
                break;
            case 27: // Love Breathing — Mitsuri Kanroji Whip-Katana Ribbon Slash
                drawSlayerSilhouette(c, w * 0.42f, groundY - px(58f), t, aBase, 0xFFF8F0F4, 0xFFFF58A8, 0xFFFF3898, 1.34f);
                drawLightningStreak(c, w * 0.45f, groundY - px(68f), w * 0.62f, groundY - px(94f),
                        t, 670, 0x00FF1878, 0x00FF78C8, 0x00FFFFFF, px(3.6f), aBase, true);
                drawLightningStreak(c, w * 0.62f, groundY - px(94f), w * 0.54f, groundY - px(34f),
                        t, 671, 0x00FF1878, 0x00FF78C8, 0x00FFFFFF, px(3.6f), aBase, true);
                break;
            case 28: // Lightning & Wood Dragons — Mitsuri vs. Upper Moon Four Zohakuten
                drawSlayerSilhouette(c, w * 0.34f, groundY - px(58f), t, aBase, 0xFFF8F0F4, 0xFFFF58A8, 0xFFFF3898, 1.34f);
                drawDragonCustom(c, w * 0.56f, h * 0.75f, t, aBase, 0xFF986838, 1.24f);
                drawLightningStreak(c, w * 0.56f, h * 0.72f, w * 0.38f, groundY - px(58f),
                        t, 675, 0x00FF8800, 0x00FFE838, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 29: // Morning Sunrise — Nezuko Conquers the Sun
                paint.setColor((aBase / 3 << 24) | 0x00FFD858);
                c.drawCircle(w * 0.56f, h * 0.76f, px(54f), paint);
                paint.setColor((aBase * 4 / 5 << 24) | 0x00FFF098);
                c.drawCircle(w * 0.56f, h * 0.76f, px(28f), paint);
                drawTanjiroKamado(c, w * 0.38f, groundY - px(58f), t, aBase, false, 1.32f);
                drawNezukoKamado(c, w * 0.49f, groundY - px(52f), t + 0.5f, aBase, 1.26f);
                break;
            case 30: // Waterfall Training — Gyomei Himejima Stone Hashira Flail & Axe
                paint.setColor((aBase / 3 << 24) | 0x0058C8FF);
                c.drawRect(w * 0.28f, h * 0.68f, w * 0.38f, groundY, paint);
                drawSlayerSilhouette(c, w * 0.48f, groundY - px(62f), t, aBase, 0xFF586848, 0xFFD8E0E8, 0xFFA8C888, 1.42f);
                drawLightningStreak(c, w * 0.51f, groundY - px(74f), w * 0.64f, groundY - px(46f),
                        t, 680, 0x00788898, 0x00D8E4F0, 0x00FFFFFF, px(3.2f), aBase, false);
                paint.setColor((aBase << 24) | 0x0098A8B8);
                c.drawCircle(w * 0.64f, groundY - px(46f), px(11f), paint);
                break;
            case 31: // Wind & Serpent — Sanemi Shinazugawa & Obanai Iguro Sparring Duel
                drawSlayerSilhouette(c, w * 0.35f, groundY - px(58f), t * 1.4f, aBase, 0xFFF0F4F8, 0xFF38FF88, 0xFF28E878, 1.34f);
                drawSlayerSilhouette(c, w * 0.58f, groundY - px(58f), t * 1.4f + 1f, aBase, 0xFF282C38, 0xFFC868FF, 0xFFA848FF, 1.32f);
                drawLightningStreak(c, w * 0.38f, groundY - px(64f), w * 0.55f, groundY - px(64f),
                        t, 685, 0x0018C868, 0x0068FF98, 0x00FFFFFF, px(3.6f), aBase, true);
                break;
            case 32: // Infinity Castle — Zenitsu Seventh Form: Flaming Thunder God Dragon vs. Kaigaku
                drawZenitsuAgatsuma(c, w * 0.32f, groundY - px(56f), t * 2.2f, aBase, 1.36f);
                drawDragonCustom(c, w * (0.46f + p * 0.12f), h * 0.75f, t * 1.6f, aBase, 0xFFFFD828, 1.28f);
                drawLightningStreak(c, w * 0.32f, groundY - px(58f), w * 0.62f, groundY - px(62f),
                        t, 690, 0x000088FF, 0x00FFE838, 0x00FFFFFF, px(4.6f), aBase, true);
                drawSlayerSilhouette(c, w * 0.62f, groundY - px(58f), t + 1f, aBase, 0xFF181C28, 0xFF3868FF, 0xFF182848, 1.34f);
                break;
            case 33: // Upper Moon Two — Shinobu, Kanao & Inosuke vs. Doma Crystalline Ice Lotus
                drawSlayerSilhouette(c, w * 0.31f, groundY - px(56f), t, aBase, 0xFFC868FF, 0xFF68F8D8, 0xFFD878FF, 1.26f);
                drawSlayerSilhouette(c, w * 0.42f, groundY - px(56f), t + 0.5f, aBase, 0xFFF4ECF8, 0xFFFF78B8, 0xFFFF58A8, 1.26f);
                drawSlayerSilhouette(c, w * 0.60f, groundY - px(58f), t + 1.0f, aBase, 0xFFB81828, 0xFFFFD848, 0xFF88E8FF, 1.36f);
                drawLightningStreak(c, w * 0.58f, groundY - px(68f), w * 0.44f, groundY - px(62f),
                        t, 692, 0x0038B8FF, 0x00A8F4FF, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 34: // Upper Moon One — Gyomei, Sanemi & Muichiro vs. Six-Eyed Kokushibo
                drawSlayerSilhouette(c, w * 0.30f, groundY - px(62f), t, aBase, 0xFF586848, 0xFFD8E0E8, 0xFFA8C888, 1.36f);
                drawSlayerSilhouette(c, w * 0.41f, groundY - px(58f), t + 0.5f, aBase, 0xFFF0F4F8, 0xFF38FF88, 0xFF28E878, 1.28f);
                drawSlayerSilhouette(c, w * 0.60f, groundY - px(60f), t + 1.0f, aBase, 0xFF682478, 0xFFFF3868, 0xFFFFC848, 1.38f);
                drawLightningStreak(c, w * 0.58f, groundY - px(70f), w * 0.38f, groundY - px(62f),
                        t, 695, 0x00D81868, 0x00FFD848, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 35: // Scene 36: Dawn Over Tokyo — Thirteenth Form Sun Breathing Finale vs. Muzan
                paint.setColor((aBase / 3 << 24) | 0x00FF9828);
                c.drawCircle(w * 0.48f, groundY - px(20f), px(78f), paint);
                drawTanjiroKamado(c, w * 0.35f, groundY - px(58f), t * 1.5f, aBase, true, 1.36f);
                drawDragonCustom(c, w * 0.48f, h * 0.74f, t * 1.5f, aBase, 0xFFFF5818, 1.26f);
                drawMuzanKibutsuji(c, w * 0.62f, groundY - px(58f), t, aBase, 1.38f);
                drawLightningStreak(c, w * 0.38f, groundY - px(64f), w * 0.60f, groundY - px(64f),
                        t, 699, 0x00FF2400, 0x00FFE038, 0x00FFFFFF, px(4.6f), aBase, true);
                break;
            case 36: // Scene 37: Drum Mansion — Kyogai Rotating Claw Slash vs. Tanjiro
                drawTanjiroKamado(c, w * 0.34f, groundY - px(58f), t * 1.4f, aBase, false, 1.34f);
                drawSlayerSilhouette(c, w * 0.59f, groundY - px(58f), t + 0.8f, aBase, 0xFF582838, 0xFFFF3848, 0xFFFF2438, 1.36f);
                for (int cl = -1; cl <= 1; cl++) {
                    drawLightningStreak(c, w * 0.57f, groundY - px(64f + cl * 10f), w * 0.38f, groundY - px(64f + cl * 14f),
                            t, 701 + cl, 0x00D81828, 0x00FF6878, 0x00FFFFFF, px(2.8f), aBase, false);
                }
                break;
            case 37: // Scene 38: Mount Natagumo — Shinobu Kocho Wisteria Moonlit Descent
                drawSlayerSilhouette(c, w * 0.46f, groundY - px(88f) + (float) Math.sin(t * 4f) * px(10f), t, aBase, 0xFFC868FF, 0xFF68F8D8, 0xFFD878FF, 1.36f);
                for (int bf = 0; bf < 10; bf++) {
                    float bx = w * (0.30f + bf * 0.036f);
                    float by = groundY - px(48f + (bf % 3) * 24f) + (float) Math.cos(t * 4f + bf) * px(8f);
                    drawGlowingButterfly(c, bx, by, t + bf, px(7.5f), bf % 2 == 0 ? 0x00D878FF : 0x0068F8E0, aBase);
                }
                break;
            case 38: // Scene 39: Rehabilitation Mansion — Tanjiro Total Concentration Gourd Burst
                drawTanjiroKamado(c, w * 0.38f, groundY - px(58f), t, aBase, false, 1.35f);
                paint.setColor((aBase << 24) | 0x00D8B880);
                c.drawOval(w * 0.50f, groundY - px(72f), w * 0.58f, groundY - px(24f), paint);
                drawLightningStreak(c, w * 0.54f, groundY - px(48f), w * 0.64f, groundY - px(78f),
                        t, 704, 0x002898FF, 0x0088E8FF, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 39: // Scene 40: Entertainment District — Inosuke Beast Breathing Dislocated Slash
                drawSlayerSilhouette(c, w * (0.32f + p * 0.24f), groundY - px(58f), t * 1.8f, aBase, 0xFF385888, 0xFF88C8FF, 0xFF58A8FF, 1.36f);
                drawLightningStreak(c, w * 0.28f, groundY - px(74f), w * 0.62f, groundY - px(54f),
                        t, 705, 0x001878FF, 0x0078D8FF, 0x00FFFFFF, px(3.8f), aBase, true);
                drawLightningStreak(c, w * 0.28f, groundY - px(54f), w * 0.62f, groundY - px(74f),
                        t, 706, 0x001878FF, 0x0078D8FF, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 40: // Scene 41: Swordsmith Village — Genya Shinazugawa Double-Barrel Demon Shot
                drawSlayerSilhouette(c, w * 0.35f, groundY - px(58f), t, aBase, 0xFF482858, 0xFFFF6838, 0xFFFF3820, 1.35f);
                drawLightningStreak(c, w * 0.39f, groundY - px(68f), w * 0.64f, groundY - px(64f),
                        t, 707, 0x00FF2800, 0x00FFA828, 0x00FFFFFF, px(4.2f), aBase, true);
                break;
            case 41: // Scene 42: Infinity Castle — Giyu & Tanjiro vs. Akaza Compass Needle
                drawSlayerSilhouette(c, w * 0.29f, groundY - px(58f), t, aBase, 0xFF982438, 0xFF38D8FF, 0xFF28B8FF, 1.32f);
                drawTanjiroKamado(c, w * 0.41f, groundY - px(58f), t + 0.4f, aBase, true, 1.34f);
                drawNinjaSilhouette(c, w * 0.60f, groundY - px(58f), t + 0.9f, aBase, 0xFF283868, 0xFFFF3878, true, false, false, 0xFF38C8FF, 1.36f);
                drawLightningStreak(c, w * 0.44f, groundY - px(64f), w * 0.58f, groundY - px(64f),
                        t, 708, 0x00FF3800, 0x0038D8FF, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 42: // Scene 43: Infinity Castle — Obanai Iguro Serpent Breathing & Kaburamaru
                drawSlayerSilhouette(c, w * 0.36f, groundY - px(58f), t * 1.5f, aBase, 0xFF282C38, 0xFFC868FF, 0xFFA848FF, 1.35f);
                drawSerpent(c, w * 0.52f, groundY, t * 1.4f, aBase, 0xFFF0F4FC, 0xFFFF2848, 1.24f, false);
                break;
            default: // Scene 44: Springtime Epilogue — Kamado Descendants Beneath Cherry Blossoms
                drawTanjiroKamado(c, w * 0.40f, groundY - px(58f), t * 0.8f, aBase, false, 1.32f);
                drawNezukoKamado(c, w * 0.52f, groundY - px(52f), t * 0.8f + 0.6f, aBase, 1.26f);
                for (int pet = 0; pet < 14; pet++) {
                    float pxP = w * (0.26f + ((p + pet * 0.07f) % 1.0f) * 0.44f);
                    float pyP = h * 0.72f + (pet % 4) * px(22f) + (float) Math.sin(t * 3.5f + pet) * px(6f);
                    paint.setColor((aBase * 4 / 5 << 24) | 0x00FFB8D8);
                    c.drawOval(pxP - px(4.5f), pyP - px(2.5f), pxP + px(4.5f), pyP + px(2.5f), paint);
                }
                break;
        }
        c.restore();
    }

    /**
     * Bespoke Show-Accurate Tanjiro Kamado Renderer:
     * - Green & Black Checkered Haori coat with visible grid squares
     * - Wooden Nezuko Box strapped on back with iron bands
     * - Burgundy spiky hair + forehead scar + dangling Hanafuda rising-sun earring
     * - Black Nichirin Katana unleashing Water Breathing waves or Hinokami Kagura Sun flames
     */
    private void drawTanjiroKamado(Canvas c, float x, float y, float t, int alpha, boolean sunBreathing, float s) {
        float stride = (float) Math.sin(t * 7f) * px(5.5f * s);
        float capeWave = (float) Math.sin(t * 8.5f) * px(7f * s);

        // 1. Wooden Nezuko Box on Tanjiro's back
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00684024);
        c.drawRoundRect(x - px(16f * s), y - px(24f * s), x - px(4f * s), y + px(2f * s), px(2f * s), px(2f * s), paint);
        paint.setColor((alpha << 24) | 0x00241C18);
        c.drawRect(x - px(16f * s), y - px(18f * s), x - px(4f * s), y - px(15f * s), paint);
        c.drawRect(x - px(16f * s), y - px(6f * s), x - px(4f * s), y - px(3f * s), paint);

        // 2. Dark Corps Hakama pants & white Kyahan leg wraps
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(4.8f * s));
        paint.setColor((alpha << 24) | 0x001C2028);
        c.drawLine(x - px(3f * s), y + px(4f * s), x - px(11f * s) - stride, y + px(22f * s), paint);
        c.drawLine(x + px(4f * s), y + px(4f * s), x + px(12f * s) + stride, y + px(22f * s), paint);
        paint.setStrokeWidth(px(3.6f * s));
        paint.setColor((alpha << 24) | 0x00F0F4F8);
        c.drawLine(x - px(11f * s) - stride, y + px(22f * s), x - px(13f * s) - stride, y + px(34f * s), paint);
        c.drawLine(x + px(12f * s) + stride, y + px(22f * s), x + px(14f * s) + stride, y + px(34f * s), paint);

        // 3. Billowing Green & Black Checkered Haori Coat
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x0018A878);
        actorPath.reset();
        actorPath.moveTo(x - px(7f * s), y - px(18f * s));
        actorPath.lineTo(x + px(8f * s), y - px(18f * s));
        actorPath.lineTo(x + px(11f * s), y + px(14f * s));
        actorPath.lineTo(x - px(24f * s) - capeWave, y + px(18f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Black Checkered Squares on Haori
        paint.setColor((alpha << 24) | 0x00101814);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if ((row + col) % 2 == 0) {
                    float sx = x - px((14f - col * 6.5f) * s) - row * capeWave * 0.2f;
                    float sy = y - px((14f - row * 8.5f) * s);
                    c.drawRect(sx, sy, sx + px(5.5f * s), sy + px(6.5f * s), paint);
                }
            }
        }

        // 4. Head, Burgundy Spiky Hair & Hanafuda Rising-Sun Earring
        float headX = x + px(2f * s), headY = y - px(26f * s);
        paint.setColor((alpha << 24) | 0x00F4D4B8);
        c.drawCircle(headX, headY, px(6.8f * s), paint);
        paint.setColor((alpha << 24) | 0x00781824);
        actorPath.reset();
        actorPath.moveTo(headX - px(7f * s), headY - px(2f * s));
        actorPath.lineTo(headX - px(12f * s), headY - px(9f * s));
        actorPath.lineTo(headX - px(4f * s), headY - px(8f * s));
        actorPath.lineTo(headX - px(5f * s), headY - px(15f * s));
        actorPath.lineTo(headX + px(2f * s), headY - px(10f * s));
        actorPath.lineTo(headX + px(7f * s), headY - px(14f * s));
        actorPath.lineTo(headX + px(8f * s), headY - px(4f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Dangling White & Red Hanafuda Earring
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawRect(headX - px(4.5f * s), headY + px(2f * s), headX - px(1.5f * s), headY + px(8.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00E82028);
        c.drawCircle(headX - px(3.0f * s), headY + px(4.5f * s), px(1.4f * s), paint);

        // 5. Black Nichirin Katana & Water / Sun Breathing Swirl
        float handX = x + px(16f * s), handY = y - px(8f * s);
        float tipX = x + px(44f * s), tipY = y - px(28f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(4.0f * s));
        paint.setColor((alpha << 24) | 0x0018A878);
        c.drawLine(x + px(4f * s), y - px(14f * s), handX, handY, paint);
        paint.setStrokeWidth(px(2.8f * s));
        paint.setColor((alpha << 24) | 0x00181C24);
        c.drawLine(handX, handY, tipX, tipY, paint);
        // Breathing Elemental Vortex
        paint.setStrokeWidth(px(5.5f * s));
        paint.setColor((alpha * 4 / 5 << 24) | (sunBreathing ? 0x00FF4818 : 0x0028B8FF));
        storyRect.set(x - px(10f * s), y - px(44f * s), x + px(52f * s), y + px(20f * s));
        c.drawArc(storyRect, -80f + (float) Math.sin(t * 7f) * 16f, 135f, false, paint);
        paint.setStrokeWidth(px(2.2f * s));
        paint.setColor((alpha << 24) | (sunBreathing ? 0x00FFE048 : 0x00FFFFFF));
        c.drawArc(storyRect, -70f + (float) Math.sin(t * 7f) * 16f, 110f, false, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Bespoke Show-Accurate Nezuko Kamado Renderer:
     * - Long black hair with flame-orange tips
     * - Green Bamboo Muzzle across mouth
     * - Pink Asanoha Kimono + Red-and-White Obi + Pink Blood Demon Art Flames
     */
    private void drawNezukoKamado(Canvas c, float x, float y, float t, int alpha, float s) {
        float wave = (float) Math.sin(t * 7.5f) * px(6f * s);
        paint.setStyle(Paint.Style.FILL);
        // Long Flowing Hair (Black with Orange Tips)
        paint.setColor((alpha << 24) | 0x0018141C);
        actorPath.reset();
        actorPath.moveTo(x - px(4f * s), y - px(26f * s));
        actorPath.lineTo(x - px(22f * s) - wave, y + px(6f * s));
        actorPath.lineTo(x - px(8f * s) - wave, y + px(10f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        paint.setColor((alpha << 24) | 0x00FF6820);
        c.drawCircle(x - px(16f * s) - wave, y + px(7f * s), px(5.5f * s), paint);

        // Pink Kimono & Dark Haori
        paint.setColor((alpha << 24) | 0x00FF78B0);
        actorPath.reset();
        actorPath.moveTo(x - px(6f * s), y - px(16f * s));
        actorPath.lineTo(x + px(6f * s), y - px(16f * s));
        actorPath.lineTo(x + px(10f * s), y + px(28f * s));
        actorPath.lineTo(x - px(10f * s), y + px(28f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Red & White Checkered Obi Sash
        paint.setColor((alpha << 24) | 0x00E82438);
        c.drawRect(x - px(7f * s), y - px(4f * s), x + px(7f * s), y + px(3f * s), paint);

        // Head & Green Bamboo Muzzle
        float headX = x + px(1f * s), headY = y - px(24f * s);
        paint.setColor((alpha << 24) | 0x00F8D8C0);
        c.drawCircle(headX, headY, px(6.4f * s), paint);
        paint.setColor((alpha << 24) | 0x0048C868);
        c.drawRoundRect(headX - px(2f * s), headY + px(0.5f * s), headX + px(7.5f * s), headY + px(4.2f * s), px(1.5f * s), px(1.5f * s), paint);

        // Pink Blood Demon Art Flames
        paint.setColor((alpha * 3 / 4 << 24) | 0x00FF3888);
        c.drawCircle(x + px(16f * s), y - px(8f * s) + wave * 0.5f, px(7.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFB8E0);
        c.drawCircle(x + px(16f * s), y - px(8f * s) + wave * 0.5f, px(3.8f * s), paint);
    }

    /**
     * Bespoke Show-Accurate Zenitsu Agatsuma Renderer:
     * - Golden-Yellow Haori with White Triangle pattern
     * - Blonde spiky hair with orange tips
     * - Low Crouching Iaijutsu Thunderclap & Flash stance with Golden-Blue Lightning
     */
    private void drawZenitsuAgatsuma(Canvas c, float x, float y, float t, int alpha, float s) {
        float stride = (float) Math.sin(t * 9f) * px(6f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(4.8f * s));
        paint.setColor((alpha << 24) | 0x001C2028);
        c.drawLine(x - px(4f * s), y + px(6f * s), x - px(16f * s) - stride, y + px(32f * s), paint);
        c.drawLine(x + px(5f * s), y + px(6f * s), x + px(16f * s) + stride, y + px(32f * s), paint);
        // White & gold triangle-patterned Kyahan shin wraps
        paint.setStrokeWidth(px(3.6f * s));
        paint.setColor((alpha << 24) | 0x00FFF8E0);
        c.drawLine(x - px(12f * s) - stride * 0.7f, y + px(22f * s), x - px(16f * s) - stride, y + px(32f * s), paint);
        c.drawLine(x + px(12f * s) + stride * 0.7f, y + px(22f * s), x + px(16f * s) + stride, y + px(32f * s), paint);

        // Golden-Yellow Haori Coat with Crisp White Triangles
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00FFC818);
        actorPath.reset();
        actorPath.moveTo(x - px(5f * s), y - px(16f * s));
        actorPath.lineTo(x + px(10f * s), y - px(16f * s));
        actorPath.lineTo(x + px(13f * s), y + px(14f * s));
        actorPath.lineTo(x - px(26f * s), y + px(16f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        for (int tr = 0; tr < 3; tr++) {
            float tx = x - px((15f - tr * 7.5f) * s);
            float ty = y + px((6f - (tr % 2) * 5f) * s);
            actorPath.reset();
            actorPath.moveTo(tx, ty - px(3.0f * s));
            actorPath.lineTo(tx + px(2.8f * s), ty + px(2.4f * s));
            actorPath.lineTo(tx - px(2.8f * s), ty + px(2.4f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
        }

        // White Scabbard (Saya) at left hip
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.4f * s));
        paint.setColor((alpha << 24) | 0x00F4F6FA);
        c.drawLine(x - px(22f * s), y + px(12f * s), x + px(4f * s), y + px(2f * s), paint);

        // Head & Blonde Spiky Bowl-Cut Hair with Orange Tips
        paint.setStyle(Paint.Style.FILL);
        float headX = x + px(6f * s), headY = y - px(23f * s);
        paint.setColor((alpha << 24) | 0x00F4D4B8);
        c.drawCircle(headX, headY, px(6.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFE038);
        c.drawArc(rectF(headX - px(8.5f * s), headY - px(10.5f * s), headX + px(8.5f * s), headY + px(2f * s)), 155, 225, true, paint);
        paint.setColor((alpha << 24) | 0x00FF7818);
        c.drawArc(rectF(headX - px(8.5f * s), headY - px(5f * s), headX + px(8.5f * s), headY + px(2f * s)), 165, 205, false, paint);

        // Unsheathed Lightning Nichirin Blade Slash + Crackling Thunderbolt Arc
        float handX = x + px(12f * s), handY = y - px(6f * s);
        float tipX = x + px(50f * s), tipY = y - px(20f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(5.2f * s));
        paint.setColor((alpha * 2 / 3 << 24) | 0x00FFE838);
        storyRect.set(x - px(6f * s), y - px(38f * s), x + px(54f * s), y + px(18f * s));
        c.drawArc(storyRect, -65f, 110f, false, paint);
        paint.setStrokeWidth(px(2.6f * s));
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawLine(handX, handY, tipX, tipY, paint);
        drawLightningStreak(c, handX - px(6f * s), handY + px(4f * s), tipX + px(6f * s), tipY - px(4f * s),
                t, 77, 0x000088FF, 0x00FFE838, 0x00FFFFFF, px(2.2f * s), alpha, false);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Bespoke Show-Accurate Kyojuro Rengoku (Flame Hashira) Renderer:
     * - White Flame Cloak with Crimson & Gold Scalloped Flame Hem
     * - Voluminous Golden & Red Flame Hair
     * - Blazing Ninth Form Rengoku Flame Vortex
     */
    private void drawRengokuFlameHashira(Canvas c, float x, float y, float t, int alpha, float s) {
        float wave = (float) Math.sin(t * 8f) * px(7f * s);
        // White Flame Cape with Red & Gold Flame Border
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00F8F4EC);
        actorPath.reset();
        actorPath.moveTo(x - px(6f * s), y - px(18f * s));
        actorPath.lineTo(x + px(8f * s), y - px(18f * s));
        actorPath.lineTo(x + px(10f * s), y + px(18f * s));
        actorPath.lineTo(x - px(28f * s) - wave, y + px(22f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        paint.setColor((alpha << 24) | 0x00FF2818);
        for (int fl = 0; fl < 4; fl++) {
            c.drawCircle(x - px((24f - fl * 8f) * s) - wave * 0.5f, y + px(19f * s), px(4.2f * s), paint);
        }
        // Draw Slayer Body, Hair & Flame Katana
        drawSlayerSilhouette(c, x, y, t, alpha, 0xFFFF5818, 0xFFFFE048, 0xFFFF3818, s);
        // Wild Golden & Red Flame Hair Crown
        float headX = x + px(2f * s), headY = y - px(28f * s);
        paint.setColor((alpha << 24) | 0x00FFD828);
        c.drawCircle(headX - px(3f * s), headY - px(4f * s), px(6.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00FF2418);
        c.drawCircle(headX - px(6f * s), headY - px(7f * s), px(4.0f * s), paint);
    }

    /**
     * High-finesse Demon Slayer Corps Swordsman silhouette:
     * Articulated swordsman stance, billowing Haori coat, Nichirin katana blade, and Elemental Breathing arc!
     */
    private void drawSlayerSilhouette(Canvas c, float x, float y, float t, int alpha,
                                      int haoriRgb, int bladeRgb, int breathRgb, float s) {
        drawNinjaSilhouette(c, x, y, t, alpha, haoriRgb, bladeRgb, true, false, false, 0, s);
        float handX = x + px(19f * s), handY = y - px(9f * s);
        float tipX = x + px(44f * s), tipY = y - px(28f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.4f * s));
        paint.setColor((alpha << 24) | (bladeRgb & 0xFFFFFF));
        c.drawLine(handX, handY, tipX, tipY, paint);
        if (breathRgb != 0) {
            paint.setStrokeWidth(px(5.2f * s));
            paint.setColor((alpha * 3 / 4 << 24) | (breathRgb & 0xFFFFFF));
            storyRect.set(x - px(12f * s), y - px(42f * s), x + px(52f * s), y + px(22f * s));
            c.drawArc(storyRect, -75f + (float) Math.sin(t * 8f) * 18f, 125f, false, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Bespoke Show-Accurate Muzan Kibutsuji (Demon King — Final Combat Form) Renderer:
     * - Flowing shock-white hair crown & glowing plum-red cat-slit demon eyes
     * - Crimson-and-obsidian biokinetic flesh armor torso
     * - 9 whipping crimson-black bone-bladed back tentacles thrashing across the stage
     */
    private void drawMuzanKibutsuji(Canvas c, float x, float y, float t, int alpha, float s) {
        // 1. 9 Whipping Crimson-Black Biokinetic Back Tentacles
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        for (int w = 0; w < 9; w++) {
            double ang = -Math.PI * 0.85 + w * (Math.PI * 0.85 / 8.0) + Math.sin(t * 6.5f + w * 0.9f) * 0.22;
            float len = px((42f + (w % 3) * 12f) * s);
            float mx = x + (float) Math.cos(ang) * len * 0.55f;
            float my = y - px(10f * s) + (float) Math.sin(ang) * len * 0.45f;
            float tx = x + (float) Math.cos(ang + 0.25) * len;
            float ty = y - px(10f * s) + (float) Math.sin(ang + 0.25) * len;
            paint.setStrokeWidth(px(3.4f * s));
            paint.setColor((alpha * 4 / 5 << 24) | 0x00B81428);
            c.drawLine(x, y - px(10f * s), mx, my, paint);
            paint.setStrokeWidth(px(2.2f * s));
            paint.setColor((alpha << 24) | 0x00FF2848);
            c.drawLine(mx, my, tx, ty, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor((alpha << 24) | 0x00F4ECF0);
            c.drawCircle(tx, ty, px(2.0f * s), paint);
            paint.setStyle(Paint.Style.STROKE);
        }
        // 2. Crimson & Obsidian Biokinetic Torso & Legs
        paint.setStrokeWidth(px(5.0f * s));
        paint.setColor((alpha << 24) | 0x0018121C);
        float stride = (float) Math.sin(t * 5.5f) * px(4f * s);
        c.drawLine(x - px(4f * s), y + px(6f * s), x - px(9f * s) - stride, y + px(34f * s), paint);
        c.drawLine(x + px(4f * s), y + px(6f * s), x + px(9f * s) + stride, y + px(34f * s), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00781224);
        c.drawRoundRect(x - px(8.5f * s), y - px(19f * s), x + px(8.5f * s), y + px(8f * s), px(3f * s), px(3f * s), paint);
        // 3. Pale Demon Head, Flowing White Hair & Glowing Plum-Red Eyes
        float headX = x - px(1f * s), headY = y - px(26.5f * s);
        paint.setColor((alpha << 24) | 0x00ECE8F4);
        c.drawCircle(headX, headY, px(6.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00F8F8FF);
        c.drawArc(rectF(headX - px(8.5f * s), headY - px(9.5f * s), headX + px(8.5f * s), headY + px(4f * s)), 150, 240, true, paint);
        paint.setColor((alpha << 24) | 0x00FF1838);
        c.drawCircle(headX - px(2.5f * s), headY - px(1.0f * s), px(1.8f * s), paint);
    }

    private void drawStarWarsStoryLoop(Canvas c, int w, int h, float t) {
        int count = STAR_WARS_TITLES.length;
        float totalCycle = CHAPTER_SEC * count;
        float cycleTime = t % totalCycle;
        int scene = resolveActiveScene(cycleTime, count);
        float p = (cycleTime % CHAPTER_SEC) / CHAPTER_SEC;
        float fade = resolveStoryFade(p);
        int aBase = Math.min(255, Math.max(0, Math.round(fade * 255f)));
        float groundY = h * 0.900f;
        drawStoryCaptionAndDots(c, w, h, STAR_WARS_TITLES[scene], scene, count, aBase,
                0x0068C8FF, 0xEE38B8FF, 0x4268C8FF, px(10.5f));

        c.save();
        applyStageTransform(c, w, h, groundY);
        switch (scene) {
            case 0: // Tatooine — Luke Skywalker & Twin Binary Suns
                paint.setColor((aBase * 3 / 4 << 24) | 0x00FFB838);
                c.drawCircle(w * 0.54f, h * 0.73f, px(20f), paint);
                paint.setColor((aBase * 3 / 4 << 24) | 0x00FF5820);
                c.drawCircle(w * 0.60f, h * 0.77f, px(15f), paint);
                drawJediMaster(c, w * 0.36f, groundY - px(58f), t, aBase, 0xFFD8C8A8, 0xFF38B8FF, false, 1.34f);
                break;
            case 1: // Duel of the Fates — Qui-Gon & Obi-Wan vs. Darth Maul Double-Bladed Saber
                drawJediMaster(c, w * 0.34f, groundY - px(58f), t, aBase, 0xFFC8B898, 0xFF38FF88, false, 1.32f);
                drawDarthMaul(c, w * 0.58f, groundY - px(58f), t + 1f, aBase, true, 1.35f);
                drawLightningStreak(c, w * 0.43f, groundY - px(74f), w * 0.49f, groundY - px(74f),
                        t, 901, 0x00FF1824, 0x0038FF88, 0x00FFFFFF, px(3.2f), aBase, true);
                break;
            case 2: // Clone Wars — General Grievous 4 Lightsabers vs. Obi-Wan
                drawJediMaster(c, w * 0.34f, groundY - px(58f), t, aBase, 0xFFD8C8A8, 0xFF38B8FF, false, 1.32f);
                drawJediSith(c, w * 0.58f, groundY - px(60f), t * 1.6f, aBase, 0xFFD0D8E0, 0xFF38FF88, true, true, 1.36f);
                break;
            case 3: // Mustafar — Obi-Wan vs. Anakin High Ground Duel
            case 8: // Cloud City Bespin — Luke vs. Darth Vader
            case 9: { // Death Star II — Return of the Jedi Throne Room Duel
                float jx = w * (0.35f + p * 0.05f), sx = w * (0.58f - p * 0.05f);
                drawJediMaster(c, jx, groundY - px(58f), t, aBase, scene == 9 ? 0xFF242830 : 0xFFD8C8A8, scene == 9 ? 0xFF38FF88 : 0xFF38B8FF, false, 1.34f);
                drawDarthVader(c, sx, groundY - px(60f), t + 0.8f, aBase, true, 1.38f);
                float cx = (jx + sx) * 0.5f, cy = groundY - px(74f);
                drawLightningStreak(c, cx - px(24f), cy - px(18f), cx + px(24f), cy + px(18f),
                        t, 911, 0x00FF1824, 0x0058D8FF, 0x00FFFFFF, px(3.0f), aBase, true);
                paint.setColor((aBase << 24) | 0x00FFFFFF);
                c.drawCircle(cx, cy, px(9f + 3.5f * (float) Math.sin(t * 20f)), paint);
                break;
            }
            case 4: // Millennium Falcon Jumping to Hyperspace
                float fx = w * (0.28f + p * 0.36f), fy = h * 0.77f;
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.2f));
                paint.setColor((aBase << 24) | 0x00A8E8FF);
                for (int st = 0; st < 8; st++) {
                    float sy = h * (0.70f + st * 0.018f);
                    c.drawLine(fx - px(130f), sy, fx - px(22f), sy, paint);
                }
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00C8D4E0);
                c.drawCircle(fx, fy, px(24f), paint);
                c.drawRect(fx, fy - px(8f), fx + px(32f), fy + px(8f), paint);
                paint.setColor((aBase << 24) | 0x0048D8FF);
                c.drawArc(rectF(fx - px(28f), fy - px(18f), fx - px(12f), fy + px(18f)), 90, 180, false, paint);
                break;
            case 5: // Yavin Trench Run — X-Wing vs. Darth Vader TIE Advanced
                float xwX = w * (0.30f + p * 0.30f), xwY = h * 0.77f + (float) Math.sin(t * 5f) * px(10f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.0f));
                paint.setColor((aBase << 24) | 0x00E8F0F8);
                c.drawLine(xwX - px(22f), xwY - px(12f), xwX + px(22f), xwY + px(12f), paint);
                c.drawLine(xwX - px(22f), xwY + px(12f), xwX + px(22f), xwY - px(12f), paint);
                paint.setColor((aBase << 24) | 0x00FF3838);
                c.drawLine(xwX + px(24f), xwY, xwX + px(95f), xwY, paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            case 6: // Battle of Hoth — Imperial AT-AT Walker & Snowspeeder
                float atX = w * (0.56f - p * 0.12f);
                paint.setColor((aBase << 24) | 0x008898A8);
                c.drawRoundRect(atX - px(36f), groundY - px(98f), atX + px(36f), groundY - px(56f), px(6f), px(6f), paint);
                c.drawRoundRect(atX - px(58f), groundY - px(90f), atX - px(36f), groundY - px(68f), px(4f), px(4f), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(4.5f));
                float walk = (float) Math.sin(t * 4f) * px(8f);
                c.drawLine(atX - px(24f), groundY - px(56f), atX - px(28f) + walk, groundY, paint);
                c.drawLine(atX - px(10f), groundY - px(56f), atX - px(6f) - walk, groundY, paint);
                c.drawLine(atX + px(14f), groundY - px(56f), atX + px(10f) + walk, groundY, paint);
                c.drawLine(atX + px(26f), groundY - px(56f), atX + px(30f) - walk, groundY, paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            case 7: // Dagobah Swamp — Yoda Lifting the X-Wing with the Force
                drawYodaMaster(c, w * 0.35f, groundY - px(40f), t, aBase, 1.25f);
                float liftY = groundY - px(32f + p * 56f);
                paint.setColor((aBase << 24) | 0x00C8D8E8);
                c.drawRoundRect(w * 0.50f, liftY - px(8f), w * 0.62f, liftY + px(8f), px(3f), px(3f), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.6f));
                c.drawLine(w * 0.52f, liftY - px(14f), w * 0.60f, liftY + px(14f), paint);
                c.drawLine(w * 0.52f, liftY + px(14f), w * 0.60f, liftY - px(14f), paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            case 10: // The Mandalorian — Din Djarin Darksaber & Grogu in Hover Pram
                drawMandalorianAndGrogu(c, w * 0.42f, groundY - px(58f), t, aBase, 1.35f);
                break;
            case 11: // Rogue One — Darth Vader Red Lightsaber Ignition in Dark Corridor
                drawDarthVader(c, w * 0.48f, groundY - px(60f), t, aBase, false, 1.42f);
                break;
            case 12: // Boonta Eve Classic — Anakin Skywalker Podracer Twin Turbine Engines
                float podX = w * (0.28f + p * 0.34f), podY = groundY - px(46f) + (float) Math.sin(t * 7f) * px(6f);
                paint.setColor((aBase << 24) | 0x00FFB828);
                c.drawRoundRect(podX + px(24f), podY - px(14f), podX + px(54f), podY - px(4f), px(4f), px(4f), paint);
                c.drawRoundRect(podX + px(24f), podY + px(4f), podX + px(54f), podY + px(14f), px(4f), px(4f), paint);
                paint.setColor((aBase << 24) | 0x003898FF);
                c.drawRoundRect(podX - px(32f), podY - px(7f), podX - px(8f), podY + px(7f), px(4f), px(4f), paint);
                drawLightningStreak(c, podX + px(24f), podY - px(9f), podX + px(24f), podY + px(9f),
                        t, 920, 0x00B828FF, 0x00FF58E8, 0x00FFFFFF, px(3.2f), aBase, false);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(1.8f));
                paint.setColor((aBase << 24) | 0x0098B0C8);
                c.drawLine(podX - px(8f), podY - px(3f), podX + px(24f), podY - px(9f), paint);
                c.drawLine(podX - px(8f), podY + px(3f), podX + px(24f), podY + px(9f), paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            case 13: // Naboo Hangar — Qui-Gon Jinn & Obi-Wan Kenobi vs. Darth Maul
                drawJediMaster(c, w * 0.29f, groundY - px(58f), t, aBase, 0xFFC8B898, 0xFF38FF88, false, 1.30f);
                drawJediMaster(c, w * 0.41f, groundY - px(58f), t + 0.5f, aBase, 0xFFD8C8A8, 0xFF38B8FF, false, 1.30f);
                drawDarthMaul(c, w * 0.60f, groundY - px(58f), t + 1.0f, aBase, true, 1.36f);
                drawLightningStreak(c, w * 0.46f, groundY - px(74f), w * 0.56f, groundY - px(74f),
                        t, 922, 0x00FF1824, 0x0038B8FF, 0x00FFFFFF, px(3.2f), aBase, true);
                break;
            case 14: // Kamino Rainstorm — Obi-Wan Kenobi vs. Jango Fett Jetpack Duel
                drawJediMaster(c, w * 0.34f, groundY - px(58f), t, aBase, 0xFFD8C8A8, 0xFF38B8FF, false, 1.34f);
                float jfX = w * 0.58f, jfY = groundY - px(86f) + (float) Math.sin(t * 5f) * px(10f);
                drawJediSith(c, jfX, jfY, t, aBase, 0xFF3868A8, 0xFFFF8820, true, false, 1.30f);
                drawLightningStreak(c, jfX - px(14f), jfY - px(8f), w * 0.39f, groundY - px(70f),
                        t, 924, 0x00FF2800, 0x00FFA828, 0x00FFFFFF, px(3.0f), aBase, false);
                break;
            case 15: // Geonosis Arena — Mace Windu Amethyst Purple Lightsaber
                drawJediMaster(c, w * 0.36f, groundY - px(58f), t, aBase, 0xFFD8C8A8, 0xFFB838FF, false, 1.36f);
                drawJediMaster(c, w * 0.52f, groundY - px(58f), t + 0.6f, aBase, 0xFFC8B898, 0xFF38FF88, false, 1.32f);
                drawLightningStreak(c, w * 0.66f, groundY - px(68f), w * 0.42f, groundY - px(74f),
                        t, 926, 0x00FF1828, 0x00B838FF, 0x00FFFFFF, px(3.2f), aBase, true);
                break;
            case 16: // Geonosis Hangar — Grand Master Yoda vs. Count Dooku Force Lightning
                drawYodaMaster(c, w * 0.35f, groundY - px(42f), t * 1.8f, aBase, 1.30f);
                drawJediSith(c, w * 0.58f, groundY - px(58f), t + 0.8f, aBase, 0xFF382824, 0xFFFF1824, true, false, 1.36f);
                drawLightningStreak(c, w * 0.56f, groundY - px(70f), w * 0.38f, groundY - px(48f),
                        t, 928, 0x000068FF, 0x0058E8FF, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 17: // Battle Over Coruscant — Anakin & Obi-Wan Eta-2 Jedi Interceptors
                for (int sp = 0; sp < 2; sp++) {
                    float sx = w * (0.28f + p * 0.30f + sp * 0.08f);
                    float sy = h * (0.74f + sp * 0.06f) + (float) Math.sin(t * 5f + sp) * px(8f);
                    paint.setColor((aBase << 24) | (sp == 0 ? 0x00FFD828 : 0x00C82828));
                    c.drawOval(sx - px(18f), sy - px(7f), sx + px(18f), sy + px(7f), paint);
                    drawLightningStreak(c, sx + px(18f), sy, sx + px(82f), sy,
                            t, 930 + sp, 0x0000B848, 0x0048FF88, 0x00FFFFFF, px(2.6f), aBase, false);
                }
                break;
            case 18: // Invisible Hand — Anakin & Obi-Wan vs. Count Dooku
                drawJediMaster(c, w * 0.31f, groundY - px(58f), t, aBase, 0xFF282C38, 0xFF38B8FF, false, 1.32f);
                drawJediMaster(c, w * 0.43f, groundY - px(58f), t + 0.4f, aBase, 0xFFD8C8A8, 0xFF38B8FF, false, 1.32f);
                drawJediSith(c, w * 0.59f, groundY - px(58f), t + 0.9f, aBase, 0xFF382824, 0xFFFF1824, true, false, 1.36f);
                break;
            case 19: // Utapau Sinkhole — Obi-Wan Kenobi vs. General Grievous
                drawJediMaster(c, w * 0.35f, groundY - px(58f), t * 1.4f, aBase, 0xFFD8C8A8, 0xFF38B8FF, false, 1.35f);
                drawJediSith(c, w * 0.57f, groundY - px(60f), t * 1.8f, aBase, 0xFFD8E0E8, 0xFF38FF88, true, true, 1.38f);
                drawLightningStreak(c, w * 0.41f, groundY - px(72f), w * 0.51f, groundY - px(72f),
                        t, 934, 0x000088FF, 0x0038FF88, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 20: // Chancellor's Office — Mace Windu Purple Saber Deflecting Darth Sidious Sith Lightning
                drawJediMaster(c, w * 0.35f, groundY - px(58f), t, aBase, 0xFFD8C8A8, 0xFFB838FF, false, 1.36f);
                drawJediSith(c, w * 0.59f, groundY - px(58f), t + 1f, aBase, 0xFF181422, 0xFFFF1824, true, false, 1.36f);
                drawLightningStreak(c, w * 0.57f, groundY - px(68f), w * 0.42f, groundY - px(74f),
                        t, 936, 0x003868FF, 0x0088E8FF, 0x00FFFFFF, px(4.2f), aBase, true);
                drawLightningStreak(c, w * 0.57f, groundY - px(62f), w * 0.42f, groundY - px(68f),
                        t, 937, 0x006828FF, 0x00C878FF, 0x00FFFFFF, px(3.6f), aBase, true);
                break;
            case 21: // Galactic Senate — Grand Master Yoda vs. Emperor Palpatine
                paint.setColor((aBase << 24) | 0x00485468);
                c.drawOval(w * 0.30f, groundY - px(46f), w * 0.42f, groundY - px(28f), paint);
                c.drawOval(w * 0.52f, groundY - px(62f), w * 0.64f, groundY - px(44f), paint);
                drawYodaMaster(c, w * 0.36f, groundY - px(64f), t, aBase, 1.28f);
                drawJediSith(c, w * 0.58f, groundY - px(82f), t + 1f, aBase, 0xFF181422, 0xFFFF1824, true, false, 1.30f);
                drawLightningStreak(c, w * 0.56f, groundY - px(88f), w * 0.38f, groundY - px(68f),
                        t, 938, 0x002878FF, 0x0078F0FF, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 22: // Fortress Inquisitorius — Obi-Wan Kenobi vs. Darth Vader Boulder Barrage
                drawJediMaster(c, w * 0.34f, groundY - px(58f), t, aBase, 0xFFD8C8A8, 0xFF38B8FF, false, 1.35f);
                drawDarthVader(c, w * 0.59f, groundY - px(60f), t + 0.8f, aBase, true, 1.38f);
                for (int rk = 0; rk < 4; rk++) {
                    float rx = w * (0.38f + rk * 0.045f);
                    float ry = groundY - px(92f) + (float) Math.sin(t * 4f + rk) * px(10f);
                    paint.setColor((aBase << 24) | 0x00687488);
                    c.drawCircle(rx, ry, px(8.5f), paint);
                }
                break;
            case 23: // Scarif Citadel — Death Star Emerald Superlaser Beam Ignition
                paint.setColor((aBase << 24) | 0x00606C7C);
                c.drawCircle(w * 0.35f, h * 0.75f, px(42f), paint);
                paint.setColor((aBase << 24) | 0x00384454);
                c.drawCircle(w * 0.37f, h * 0.73f, px(13f), paint);
                drawLightningStreak(c, w * 0.37f, h * 0.73f, w * 0.64f, groundY - px(18f),
                        t, 940, 0x0000B838, 0x0058FF78, 0x00FFFFFF, px(4.6f), aBase, true);
                break;
            case 24: // Death Star Hangar — Old Ben Kenobi vs. Darth Vader
                drawJediMaster(c, w * 0.36f, groundY - px(58f), t, aBase, 0xFFD8C8A8, 0xFF38B8FF, false, 1.34f);
                drawDarthVader(c, w * 0.57f, groundY - px(60f), t + 0.6f, aBase, true, 1.38f);
                drawLightningStreak(c, w * 0.42f, groundY - px(74f), w * 0.51f, groundY - px(74f),
                        t, 942, 0x00FF1824, 0x0038B8FF, 0x00FFFFFF, px(3.2f), aBase, true);
                break;
            case 25: // Hoth Asteroid Field — Millennium Falcon Evading TIE Squadron
                float mfX = w * (0.30f + p * 0.28f), mfY = h * 0.76f + (float) Math.sin(t * 5f) * px(12f);
                paint.setColor((aBase << 24) | 0x00C8D4E0);
                c.drawCircle(mfX, mfY, px(22f), paint);
                paint.setColor((aBase << 24) | 0x0048D8FF);
                c.drawArc(rectF(mfX - px(26f), mfY - px(16f), mfX - px(10f), mfY + px(16f)), 90, 180, false, paint);
                for (int as = 0; as < 3; as++) {
                    paint.setColor((aBase << 24) | 0x00685850);
                    c.drawCircle(w * (0.28f + as * 0.16f), h * (0.71f + (as % 2) * 0.09f), px(14f), paint);
                }
                break;
            case 26: // Great Pit of Carkoon — Luke Skywalker Green Lightsaber on Jabba's Sail Barge
                paint.setColor((aBase << 24) | 0x00885830);
                c.drawRoundRect(w * 0.28f, groundY - px(28f), w * 0.62f, groundY - px(8f), px(6f), px(6f), paint);
                drawJediMaster(c, w * 0.45f, groundY - px(68f), t, aBase, 0xFF1C2028, 0xFF38FF78, false, 1.34f);
                break;
            case 27: // Forest Moon of Endor — Speeder Bike Chase Through Giant Redwoods
                float sbX = w * (0.28f + p * 0.34f), sbY = groundY - px(38f) + (float) Math.sin(t * 7f) * px(5f);
                paint.setColor((aBase << 24) | 0x00886038);
                c.drawRect(sbX - px(28f), sbY - px(4f), sbX + px(32f), sbY + px(4f), paint);
                drawJediMaster(c, sbX - px(4f), sbY - px(24f), t, aBase, 0xFF486838, 0xFF38FF78, false, 1.15f);
                drawLightningStreak(c, sbX + px(32f), sbY, sbX + px(92f), sbY,
                        t, 944, 0x00FF1824, 0x00FF5838, 0x00FFFFFF, px(2.8f), aBase, false);
                break;
            case 28: // Emperor's Throne Room — Darth Vader Lifting Palpatine Amid Sith Lightning
                drawDarthVader(c, w * 0.45f, groundY - px(60f), t, aBase, false, 1.38f);
                drawJediSith(c, w * 0.48f, groundY - px(96f), t * 2f, aBase, 0xFF181422, 0xFF38B8FF, true, false, 1.24f);
                drawLightningStreak(c, w * 0.48f, groundY - px(96f), w * 0.32f, groundY - px(24f),
                        t, 946, 0x000068FF, 0x0068E8FF, 0x00FFFFFF, px(4.2f), aBase, true);
                drawLightningStreak(c, w * 0.48f, groundY - px(96f), w * 0.64f, groundY - px(24f),
                        t, 947, 0x000068FF, 0x0068E8FF, 0x00FFFFFF, px(4.2f), aBase, true);
                break;
            case 29: // Siege of Mandalore — Ahsoka Tano Dual White Lightsabers vs. Darth Maul
                drawJediSith(c, w * 0.35f, groundY - px(58f), t * 1.4f, aBase, 0xFF385878, 0xFFFFFFFF, false, true, 1.34f);
                drawDarthMaul(c, w * 0.58f, groundY - px(58f), t * 1.4f + 0.8f, aBase, true, 1.35f);
                drawLightningStreak(c, w * 0.41f, groundY - px(72f), w * 0.52f, groundY - px(72f),
                        t, 950, 0x00FF1824, 0x0098E8FF, 0x00FFFFFF, px(3.6f), aBase, true);
                break;
            case 30: // Starkiller Forest — Rey vs. Kylo Ren Crossguard Lightsaber in the Snow
                drawJediMaster(c, w * 0.35f, groundY - px(58f), t, aBase, 0xFFD8D0C0, 0xFF38B8FF, false, 1.34f);
                drawJediSith(c, w * 0.58f, groundY - px(58f), t + 0.8f, aBase, 0xFF14161E, 0xFFFF1824, true, false, 1.36f);
                drawLightningStreak(c, w * 0.41f, groundY - px(72f), w * 0.52f, groundY - px(72f),
                        t, 952, 0x00FF1824, 0x0038B8FF, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 31: // Supremacy Throne Room — Rey & Kylo Ren Back-to-Back vs. Praetorian Guards
                drawJediMaster(c, w * 0.42f, groundY - px(58f), t, aBase, 0xFF787068, 0xFF38B8FF, true, 1.34f);
                drawJediSith(c, w * 0.50f, groundY - px(58f), t + 0.5f, aBase, 0xFF14161E, 0xFFFF1824, false, false, 1.36f);
                break;
            case 32: // Battle of Crait — Luke Skywalker Facing AT-M6 Walkers Across Red Salt Flats
                paint.setColor((aBase * 3 / 4 << 24) | 0x00D81828);
                c.drawRect(w * 0.25f, groundY - px(8f), w * 0.65f, groundY, paint);
                drawJediMaster(c, w * 0.35f, groundY - px(58f), t, aBase, 0xFF282422, 0xFF38B8FF, false, 1.34f);
                drawLightningStreak(c, w * 0.64f, groundY - px(88f), w * 0.38f, groundY - px(4f),
                        t, 954, 0x00FF1824, 0x00FF6838, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 33: // Kef Bir Ocean Wreckage — Rey vs. Kylo Ren Amid Crashing Waves
                paint.setColor((aBase * 3 / 5 << 24) | 0x002878B8);
                c.drawArc(rectF(w * 0.28f, groundY - px(78f), w * 0.64f, groundY + px(24f)), 180, 180, true, paint);
                drawJediMaster(c, w * 0.36f, groundY - px(58f), t, aBase, 0xFFE8E4DC, 0xFF38B8FF, false, 1.34f);
                drawJediSith(c, w * 0.57f, groundY - px(58f), t + 0.7f, aBase, 0xFF14161E, 0xFFFF1824, true, false, 1.36f);
                break;
            case 34: // Imperial Light Cruiser — Luke Skywalker Green Saber & Mandalorian & Grogu
                drawJediMaster(c, w * 0.34f, groundY - px(58f), t, aBase, 0xFF181C24, 0xFF38FF78, false, 1.35f);
                drawMandalorianAndGrogu(c, w * 0.50f, groundY - px(58f), t + 0.6f, aBase, 1.28f);
                break;
            case 35: // Scene 36: Exegol Sith Citadel — Rey Dual Sabers ("I Am All The Jedi") vs. Palpatine
                drawJediMaster(c, w * 0.35f, groundY - px(58f), t, aBase, 0xFFE8E4DC, 0xFF38B8FF, false, 1.36f);
                drawJediSith(c, w * 0.37f, groundY - px(58f), t, aBase, 0xFFE8E4DC, 0xFF38FF78, false, false, 1.30f);
                drawJediSith(c, w * 0.60f, groundY - px(58f), t + 1f, aBase, 0xFF181422, 0xFFFF1824, true, false, 1.36f);
                drawLightningStreak(c, w * 0.58f, groundY - px(68f), w * 0.41f, groundY - px(68f),
                        t, 960, 0x001878FF, 0x0078F0FF, 0x00FFFFFF, px(4.6f), aBase, true);
                break;
            case 36: // Scene 37: Jundland Wastes — Obi-Wan Kenobi & Luke Skywalker Landspeeder
                float lsX = w * (0.30f + p * 0.28f), lsY = groundY - px(28f) + (float) Math.sin(t * 5f) * px(3f);
                paint.setColor((aBase << 24) | 0x00C86848);
                c.drawRoundRect(lsX - px(34f), lsY - px(10f), lsX + px(34f), lsY + px(6f), px(6f), px(6f), paint);
                drawJediMaster(c, lsX - px(14f), groundY - px(58f), t, aBase, 0xFFC8B898, 0xFF38B8FF, false, 1.32f);
                break;
            case 37: // Scene 38: Mos Eisley Cantina — Han Solo & Chewbacca Aboard the Falcon
                drawJediMaster(c, w * 0.36f, groundY - px(58f), t, aBase, 0xFF282C38, 0xFFFF3828, false, 1.34f);
                drawJediMaster(c, w * 0.48f, groundY - px(64f), t + 0.5f, aBase, 0xFF684428, 0xFFFF3828, false, 1.46f);
                drawLightningStreak(c, w * 0.40f, groundY - px(68f), w * 0.64f, groundY - px(68f),
                        t, 962, 0x00FF1824, 0x00FF6838, 0x00FFFFFF, px(3.2f), aBase, false);
                break;
            case 38: // Scene 39: Cloud City Carbon Freeze — Darth Vader & Boba Fett
                paint.setColor((aBase / 3 << 24) | 0x00FF6820);
                c.drawRect(w * 0.42f, groundY - px(110f), w * 0.52f, groundY, paint);
                drawDarthVader(c, w * 0.34f, groundY - px(60f), t, aBase, false, 1.38f);
                drawMandalorianAndGrogu(c, w * 0.58f, groundY - px(58f), t + 0.7f, aBase, 1.32f);
                break;
            case 39: // Scene 40: Battle of Endor — Millennium Falcon Inside Death Star II Reactor
                float rcX = w * (0.28f + p * 0.32f), rcY = h * 0.76f;
                paint.setColor((aBase << 24) | 0x00385068);
                c.drawRect(w * 0.46f, groundY - px(120f), w * 0.54f, groundY, paint);
                drawLightningStreak(c, rcX + px(24f), rcY, w * 0.50f, rcY,
                        t, 964, 0x00FF2800, 0x00FFB838, 0x00FFFFFF, px(3.8f), aBase, true);
                paint.setColor((aBase << 24) | 0x00C8D4E0);
                c.drawCircle(rcX, rcY, px(22f), paint);
                break;
            case 40: // Scene 41: Force Ghosts — Anakin, Obi-Wan & Yoda Beneath Endor Stars
                int fgA = aBase * 3 / 4;
                drawYodaMaster(c, w * 0.34f, groundY - px(42f), t, fgA, 1.26f);
                drawJediMaster(c, w * 0.46f, groundY - px(58f), t + 0.5f, fgA, 0xFF68C8FF, 0xFF68E8FF, false, 1.34f);
                drawJediMaster(c, w * 0.58f, groundY - px(58f), t + 1.0f, fgA, 0xFF68C8FF, 0xFF68E8FF, false, 1.34f);
                break;
            case 41: // Scene 42: Nevarro Streets — Mandalorian Whistling Birds & Jetpack Salvo
                drawMandalorianAndGrogu(c, w * 0.38f, groundY - px(76f) + (float) Math.sin(t * 5f) * px(8f), t, aBase, 1.35f);
                for (int wb = 0; wb < 5; wb++) {
                    drawLightningStreak(c, w * 0.40f, groundY - px(84f),
                            w * (0.54f + wb * 0.03f), groundY - px(52f + (wb % 2) * 28f),
                            t, 966 + wb, 0x003898FF, 0x0098E8FF, 0x00FFFFFF, px(2.4f), aBase, false);
                }
                break;
            case 42: // Scene 43: Corvus Forest — Ahsoka Tano Dual White Sabers vs. Magistrate
                drawJediSith(c, w * 0.36f, groundY - px(58f), t * 1.4f, aBase, 0xFF384860, 0xFFFFFFFF, false, true, 1.35f);
                drawJediSith(c, w * 0.58f, groundY - px(58f), t * 1.4f + 0.7f, aBase, 0xFF681824, 0xFFD8E4F0, true, false, 1.35f);
                drawLightningStreak(c, w * 0.42f, groundY - px(68f), w * 0.54f, groundY - px(68f),
                        t, 972, 0x0088C8FF, 0x00FFFFFF, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            default: // Scene 44: Ferrix Rising — Cassian Andor & Luthen Rael Haulcraft Escape
                float hcX = w * (0.28f + p * 0.34f), hcY = h * 0.76f;
                paint.setColor((aBase << 24) | 0x0098A8B8);
                c.drawOval(hcX - px(24f), hcY - px(8f), hcX + px(24f), hcY + px(8f), paint);
                drawLightningStreak(c, hcX - px(82f), hcY - px(14f), hcX + px(82f), hcY + px(14f),
                        t, 974, 0x00FF1824, 0x00FF6848, 0x00FFFFFF, px(3.6f), aBase, false);
                break;
        }
        c.restore();
    }

    /**
     * Bespoke Film-Accurate Darth Vader Renderer:
     * - Flared dome helmet, triangular breath mask grille, armored pauldrons
     * - Illuminated chest life-support control box (red, green, blue buttons)
     * - Sweeping floor-length black Sith cape & blazing crimson Lightsaber
     */
    private void drawDarthVader(Canvas c, float x, float y, float t, int alpha, boolean facingLeft, float s) {
        float dir = facingLeft ? -1f : 1f;
        float stride = (float) Math.sin(t * 5.5f) * px(4.5f * s);
        float capeWave = (float) Math.sin(t * 6.5f) * px(6.5f * s);

        // 1. Sweeping Floor-Length Black Sith Cape
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x000E1018);
        actorPath.reset();
        actorPath.moveTo(x - dir * px(8f * s), y - px(20f * s));
        actorPath.lineTo(x + dir * px(8f * s), y - px(20f * s));
        actorPath.lineTo(x + dir * px(11f * s), y + px(34f * s));
        actorPath.lineTo(x - dir * px(26f * s) - dir * capeWave, y + px(35f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 2. Armored Sith Boots & Legs
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(5.2f * s));
        paint.setColor((alpha << 24) | 0x001C202C);
        c.drawLine(x - dir * px(4f * s), y + px(6f * s), x - dir * px(10f * s) - stride, y + px(34f * s), paint);
        c.drawLine(x + dir * px(4f * s), y + px(6f * s), x + dir * px(10f * s) + stride, y + px(34f * s), paint);

        // 3. Armored Torso, Silver Shoulder Pauldrons & Chest Life-Support Box
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00181C26);
        c.drawRoundRect(x - px(9f * s), y - px(20f * s), x + px(9f * s), y + px(8f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | 0x00343C4C);
        c.drawOval(x - px(12f * s), y - px(21f * s), x + px(12f * s), y - px(14f * s), paint);
        // Chest Control Box with Red/Blue/Green Status LEDs
        paint.setColor((alpha << 24) | 0x00282E3A);
        c.drawRect(x - px(4.5f * s), y - px(13f * s), x + px(4.5f * s), y - px(4f * s), paint);
        paint.setColor((alpha << 24) | 0x00FF2830);
        c.drawRect(x - px(3.2f * s), y - px(11.5f * s), x - px(1.0f * s), y - px(9.2f * s), paint);
        paint.setColor((alpha << 24) | 0x0038A8FF);
        c.drawRect(x + px(1.0f * s), y - px(11.5f * s), x + px(3.2f * s), y - px(9.2f * s), paint);
        paint.setColor((alpha << 24) | 0x0038FF78);
        c.drawRect(x - px(3.2f * s), y - px(7.5f * s), x + px(3.2f * s), y - px(5.5f * s), paint);

        // 4. Iconic Flared Darth Vader Helmet Dome & Triangular Vocoder Mask
        float headX = x + dir * px(1f * s), headY = y - px(28f * s);
        paint.setColor((alpha << 24) | 0x00141822);
        actorPath.reset();
        actorPath.moveTo(headX, headY - px(10f * s));
        actorPath.lineTo(headX + px(11f * s), headY + px(4f * s));
        actorPath.lineTo(headX - px(11f * s), headY + px(4f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        c.drawArc(rectF(headX - px(9f * s), headY - px(10f * s), headX + px(9f * s), headY + px(4f * s)), 180, 180, true, paint);
        // Glossy triangular breath grille & eye lenses
        paint.setColor((alpha << 24) | 0x00404858);
        c.drawCircle(headX - px(3.2f * s), headY - px(2.5f * s), px(1.8f * s), paint);
        c.drawCircle(headX + px(3.2f * s), headY - px(2.5f * s), px(1.8f * s), paint);
        actorPath.reset();
        actorPath.moveTo(headX, headY - px(1f * s));
        actorPath.lineTo(headX + px(4.5f * s), headY + px(5f * s));
        actorPath.lineTo(headX - px(4.5f * s), headY + px(5f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 5. Blazing Crimson Sith Lightsaber
        float handX = x + dir * px(16f * s), handY = y - px(10f * s);
        float tipX = handX + dir * px(36f * s), tipY = handY - px(28f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(7.5f * s));
        paint.setColor((alpha / 2 << 24) | 0x00FF1820);
        c.drawLine(handX, handY, tipX, tipY, paint);
        paint.setStrokeWidth(px(3.0f * s));
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawLine(handX, handY, tipX, tipY, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Bespoke Film-Accurate Jedi Master (Luke Skywalker / Obi-Wan Kenobi / Qui-Gon Jinn) Renderer
     */
    private void drawJediMaster(Canvas c, float x, float y, float t, int alpha,
                                int robeRgb, int saberRgb, boolean facingLeft, float s) {
        float dir = facingLeft ? -1f : 1f;
        float stride = (float) Math.sin(t * 6.5f) * px(5f * s);
        float robeWave = (float) Math.sin(t * 7.5f) * px(6f * s);

        // Flowing Jedi Tunic & Robe
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | (robeRgb & 0xFFFFFF));
        actorPath.reset();
        actorPath.moveTo(x - px(7f * s), y - px(18f * s));
        actorPath.lineTo(x + px(7f * s), y - px(18f * s));
        actorPath.lineTo(x + dir * px(12f * s), y + px(22f * s));
        actorPath.lineTo(x - dir * px(20f * s) - dir * robeWave, y + px(24f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Brown Jedi Utility Belt & Boots
        paint.setColor((alpha << 24) | 0x00583820);
        c.drawRect(x - px(7.5f * s), y - px(2f * s), x + px(7.5f * s), y + px(2.5f * s), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(4.5f * s));
        c.drawLine(x - dir * px(4f * s), y + px(14f * s), x - dir * px(10f * s) - stride, y + px(34f * s), paint);
        c.drawLine(x + dir * px(4f * s), y + px(14f * s), x + dir * px(10f * s) + stride, y + px(34f * s), paint);

        // Head & Hair
        paint.setStyle(Paint.Style.FILL);
        float headX = x + dir * px(1.5f * s), headY = y - px(26f * s);
        paint.setColor((alpha << 24) | 0x00F0D0B4);
        c.drawCircle(headX, headY, px(6.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00987040);
        c.drawArc(rectF(headX - px(7f * s), headY - px(8f * s), headX + px(7f * s), headY + px(2f * s)), 160, 210, true, paint);

        // Ignited Jedi Lightsaber
        float handX = x + dir * px(16f * s), handY = y - px(10f * s);
        float tipX = handX + dir * px(36f * s), tipY = handY - px(28f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(7.0f * s));
        paint.setColor((alpha / 2 << 24) | (saberRgb & 0xFFFFFF));
        c.drawLine(handX, handY, tipX, tipY, paint);
        paint.setStrokeWidth(px(2.8f * s));
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawLine(handX, handY, tipX, tipY, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Bespoke Darth Maul Renderer with Red/Black Zabrak Face, Crown of Horns & Double-Bladed Lightsaber Staff!
     */
    private void drawDarthMaul(Canvas c, float x, float y, float t, int alpha, boolean facingLeft, float s) {
        drawJediSith(c, x, y, t, alpha, 0xFF18141C, 0xFFFF1824, facingLeft, true, s);
        float headX = x + (facingLeft ? -1f : 1f) * px(2f * s), headY = y - px(27f * s);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00E02028);
        c.drawCircle(headX, headY, px(6.5f * s), paint);
        // Crown of Zabrak Horns
        paint.setColor((alpha << 24) | 0x00E8D8B8);
        for (int h = -2; h <= 2; h++) {
            c.drawCircle(headX + h * px(2.5f * s), headY - px(7.2f * s), px(1.6f * s), paint);
        }
    }

    /**
     * Bespoke Grand Master Yoda Renderer with Long Pointed Green Ears, Jedi Robe & Force Aura!
     */
    private void drawYodaMaster(Canvas c, float x, float y, float t, int alpha, float s) {
        paint.setStyle(Paint.Style.FILL);
        // Cream Jedi Robe over small stature
        paint.setColor((alpha << 24) | 0x00D8CCA8);
        c.drawRoundRect(x - px(8f * s), y - px(8f * s), x + px(8f * s), y + px(16f * s), px(4f * s), px(4f * s), paint);
        // Green Head & Iconic Long Pointed Ears
        float headY = y - px(14f * s);
        paint.setColor((alpha << 24) | 0x0068C858);
        c.drawCircle(x, headY, px(6.2f * s), paint);
        actorPath.reset();
        actorPath.moveTo(x - px(5f * s), headY - px(2f * s));
        actorPath.lineTo(x - px(17f * s), headY - px(5f * s));
        actorPath.lineTo(x - px(5f * s), headY + px(2f * s));
        actorPath.moveTo(x + px(5f * s), headY - px(2f * s));
        actorPath.lineTo(x + px(17f * s), headY - px(5f * s));
        actorPath.lineTo(x + px(5f * s), headY + px(2f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Outstretched Force Hand Glow
        paint.setColor((alpha / 2 << 24) | 0x0068E8FF);
        c.drawCircle(x + px(14f * s), y - px(4f * s), px(6f * s), paint);
    }

    /**
     * Bespoke Mandalorian (Din Djarin) with Silver Beskar T-Visor Helmet, Darksaber & Grogu in Hover Pram!
     */
    private void drawMandalorianAndGrogu(Canvas c, float x, float y, float t, int alpha, float s) {
        // 1. Din Djarin Body & Cape
        drawJediSith(c, x, y, t, alpha, 0xFF484C54, 0xFFFFFFFF, false, false, s);
        // Silver Beskar Chestplate & Shoulder Pauldrons
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00B8C8D8);
        c.drawRoundRect(x - px(7f * s), y - px(18f * s), x + px(7f * s), y - px(4f * s), px(2f * s), px(2f * s), paint);
        // Beskar Helmet with Iconic Black T-Visor
        float headX = x + px(2f * s), headY = y - px(27f * s);
        c.drawRoundRect(headX - px(6.5f * s), headY - px(7.5f * s), headX + px(6.5f * s), headY + px(6.5f * s), px(4f * s), px(4f * s), paint);
        paint.setColor((alpha << 24) | 0x0010141C);
        c.drawRect(headX - px(5f * s), headY - px(3f * s), headX + px(5.5f * s), headY - px(0.5f * s), paint);
        c.drawRect(headX - px(0.5f * s), headY - px(3f * s), headX + px(2.0f * s), headY + px(5.5f * s), paint);
        // Black-Core Darksaber Blade
        float handX = x + px(18f * s), handY = y - px(10f * s);
        float tipX = handX + px(36f * s), tipY = handY - px(28f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.4f * s));
        paint.setColor((alpha << 24) | 0x00101018);
        c.drawLine(handX, handY, tipX, tipY, paint);
        paint.setStyle(Paint.Style.FILL);

        // 2. Grogu (Baby Yoda) Floating in Spherical Hover-Pram
        float gx = x + px(58f * s), gy = y - px(4f * s) + (float) Math.sin(t * 4f) * px(6f * s);
        paint.setColor((alpha << 24) | 0x00DCE4EC);
        c.drawOval(gx - px(14f * s), gy, gx + px(14f * s), gy + px(16f * s), paint);
        paint.setColor((alpha << 24) | 0x0068C868);
        c.drawCircle(gx, gy - px(2f * s), px(5.5f * s), paint);
        // Grogu's Long Pointed Green Ears
        c.drawOval(gx - px(15f * s), gy - px(5f * s), gx - px(4f * s), gy - px(1f * s), paint);
        c.drawOval(gx + px(4f * s), gy - px(5f * s), gx + px(15f * s), gy - px(1f * s), paint);
    }

    /**
     * High-finesse Jedi Knight / Sith Lord silhouette
     * with billowing Jedi/Sith cloak and blazing plasma Lightsaber blade!
     */
    private void drawJediSith(Canvas c, float x, float y, float t, int alpha,
                              int cloakRgb, int saberRgb, boolean facingLeft, boolean doubleBlade, float s) {
        float dir = facingLeft ? -1f : 1f;
        drawNinjaSilhouette(c, x, y, t, alpha, cloakRgb, cloakRgb, false, false, false, 0, s);
        float handX = x + dir * px(18f * s), handY = y - px(10f * s);
        float tipX = handX + dir * px(36f * s), tipY = handY - px(28f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(7.0f * s));
        paint.setColor((alpha / 2 << 24) | (saberRgb & 0xFFFFFF));
        c.drawLine(handX, handY, tipX, tipY, paint);
        if (doubleBlade) {
            c.drawLine(handX, handY, handX - dir * px(32f * s), handY + px(24f * s), paint);
        }
        paint.setStrokeWidth(px(2.8f * s));
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawLine(handX, handY, tipX, tipY, paint);
        if (doubleBlade) {
            c.drawLine(handX, handY, handX - dir * px(32f * s), handY + px(24f * s), paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawMcuStoryLoop(Canvas c, int w, int h, float t) {
        int count = MCU_TITLES.length;
        float totalCycle = CHAPTER_SEC * count;
        float cycleTime = t % totalCycle;
        int scene = resolveActiveScene(cycleTime, count);
        float p = (cycleTime % CHAPTER_SEC) / CHAPTER_SEC;
        float fade = resolveStoryFade(p);
        int aBase = Math.min(255, Math.max(0, Math.round(fade * 255f)));
        float groundY = h * 0.900f;
        drawStoryCaptionAndDots(c, w, h, MCU_TITLES[scene], scene, count, aBase,
                0x00C898FF, 0xEE58E8FF, 0x42C898FF, px(10.5f));

        c.save();
        applyStageTransform(c, w, h, groundY);
        switch (scene) {
            case 0: // Iron Man Mark LXXXV Flight & Repulsor Beam
                float imX = w * (0.28f + p * 0.26f), imY = h * 0.77f + (float) Math.sin(t * 5f) * px(10f);
                drawIronMan(c, imX, imY, t, aBase, false, true, 1.35f);
                drawLightningStreak(c, imX + px(26f), imY - px(14f), imX + px(135f), imY - px(14f),
                        t, 701, 0x000088FF, 0x0048F4FF, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 1: // Thor God of Thunder & Mjolnir / Bifrost Lightning
            case 8: { // Infinity War — Thor Arrives in Wakanda with Stormbreaker
                float tx = w * 0.46f, ty = groundY - px(64f) - (float) Math.abs(Math.sin(t * 4f)) * px(18f);
                // Translucent Bifrost Bridge Column from the Heavens
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase / 5 << 24) | 0x0048E8FF);
                c.drawRect(tx - px(28f), h * 0.65f, tx + px(28f), groundY, paint);
                drawThorOdinson(c, tx, ty, t, aBase, scene == 8, 1.36f);
                // Four Massive Jagged, Forked Sky-to-Ground Thunderbolts!
                drawLightningStreak(c, tx - px(22f), h * 0.65f, tx + px(24f), ty - px(32f),
                        t, 711, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(4.4f), aBase, true);
                drawLightningStreak(c, tx + px(24f), ty - px(32f), tx - px(48f), groundY,
                        t, 712, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(3.8f), aBase, true);
                drawLightningStreak(c, tx + px(24f), ty - px(32f), tx + px(56f), groundY,
                        t, 713, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(3.8f), aBase, true);
                drawLightningStreak(c, tx + px(18f), h * 0.65f, tx + px(4f), groundY,
                        t, 714, 0x002898FF, 0x0098F8FF, 0x00FFFFFF, px(3.2f), aBase * 4 / 5, true);
                break;
            }
            case 2: // Captain America Throwing the Vibranium Shield
                drawCaptainAmerica(c, w * 0.32f, groundY - px(58f), t, aBase, false, false, 1.35f);
                float shX = w * (0.38f + (float) Math.sin(p * Math.PI) * 0.26f);
                float shY = groundY - px(66f) - (float) Math.sin(p * Math.PI * 2f) * px(18f);
                // Shield motion blur trail
                for (int tr = 1; tr <= 3; tr++) {
                    drawVibraniumShield(c, shX - tr * px(12f), shY + tr * px(2f), px(13f), aBase * (4 - tr) / 8);
                }
                drawVibraniumShield(c, shX, shY, px(15f), aBase);
                break;
            case 3: // The Avengers Assemble (Captain America, Iron Man, Thor)
                drawCaptainAmerica(c, w * 0.33f, groundY - px(58f), t, aBase, true, false, 1.30f);
                drawIronMan(c, w * 0.46f, groundY - px(62f), t + 0.6f, aBase, false, false, 1.34f);
                drawThorOdinson(c, w * 0.59f, groundY - px(58f), t + 1.2f, aBase, true, 1.32f);
                drawLightningStreak(c, w * 0.62f, h * 0.68f, w * 0.62f, groundY - px(88f),
                        t, 721, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(3.2f), aBase, true);
                break;
            case 4: // Doctor Strange Eldritch Tao Mandalas & Cloak of Levitation
                float dsX = w * 0.46f, dsY = groundY - px(64f) + (float) Math.sin(t * 4f) * px(9f);
                drawDoctorStrange(c, dsX, dsY, t, aBase, 1.36f);
                break;
            case 5: // Civil War — Captain America Shield vs. Iron Man Repulsor Clash
                float capX = w * 0.35f, ironX = w * 0.58f, cy = groundY - px(58f);
                drawCaptainAmerica(c, capX, cy, t, aBase, true, false, 1.34f);
                drawIronMan(c, ironX, cy, t + 1f, aBase, true, true, 1.34f);
                float clashX = (capX + ironX) * 0.46f, clashY = cy - px(10f);
                drawLightningStreak(c, ironX - px(24f), clashY, clashX, clashY,
                        t, 731, 0x000088FF, 0x0058F8FF, 0x00FFFFFF, px(4.2f), aBase, true);
                drawLightningStreak(c, clashX, clashY, clashX - px(28f), clashY - px(38f),
                        t, 732, 0x00FF8800, 0x00FFE048, 0x00FFFFFF, px(2.8f), aBase, true);
                drawLightningStreak(c, clashX, clashY, clashX + px(24f), clashY + px(34f),
                        t, 733, 0x000088FF, 0x0068F4FF, 0x00FFFFFF, px(2.8f), aBase, true);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase << 24) | 0x00FFFFFF);
                c.drawCircle(clashX, clashY, px(12f + 4f * (float) Math.sin(t * 20f)), paint);
                break;
            case 6: // Spider-Man Web-Swinging across Queens
                float spX = w * (0.28f + p * 0.36f);
                float spY = h * 0.76f + (float) Math.cos(p * Math.PI * 2f) * px(24f);
                drawSpiderMan(c, spX, spY, groundY, t, aBase, 1.34f);
                break;
            case 7: // Wakanda Forever — Black Panther Vibranium Kinetic Pulse
                drawBlackPanther(c, w * 0.46f, groundY - px(58f), t, aBase, 1.36f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.8f));
                paint.setColor((aBase * 3 / 4 << 24) | 0x00B858FF);
                c.drawCircle(w * 0.46f, groundY - px(58f), px(28f + p * 68f), paint);
                paint.setStyle(Paint.Style.FILL);
                for (int k = 0; k < 4; k++) {
                    double ka = t * 3f + k * Math.PI * 0.5;
                    drawLightningStreak(c, w * 0.46f, groundY - px(58f),
                            w * 0.46f + (float) Math.cos(ka) * px(52f),
                            groundY - px(58f) + (float) Math.sin(ka) * px(42f),
                            t, 740 + k, 0x007818FF, 0x00C868FF, 0x00FFFFFF, px(2.6f), aBase, true);
                }
                break;
            case 9: // Titan — Thanos & the Six Infinity Stones
                drawThanosTitan(c, w * 0.48f, groundY - px(62f), t, aBase, false, 1.38f);
                int[] stCols = {0xFF2888FF, 0xFFFFE038, 0xFFFF2438, 0xFFB838FF, 0xFF28FF78, 0xFFFF8820};
                for (int s = 0; s < 6; s++) {
                    double a = t * 3f + s * Math.PI / 3.0;
                    float sx = w * 0.48f + (float) Math.cos(a) * px(52f);
                    float sy = groundY - px(62f) + (float) Math.sin(a) * px(46f);
                    drawLightningStreak(c, w * 0.48f + px(26f), groundY - px(84f), sx, sy,
                            t, 750 + s, stCols[s], stCols[s], 0x00FFFFFF, px(2.2f), aBase * 4 / 5, false);
                    paint.setColor((aBase << 24) | stCols[s]);
                    c.drawCircle(sx, sy, px(5.5f), paint);
                }
                break;
            case 10: // Endgame — Captain America Wields Mjolnir & Shield vs. Thanos
                drawCaptainAmerica(c, w * 0.35f, groundY - px(58f), t, aBase, true, true, 1.34f);
                drawThanosTitan(c, w * 0.59f, groundY - px(62f), t + 1f, aBase, true, 1.36f);
                drawLightningStreak(c, w * 0.33f, h * 0.65f, w * 0.33f, groundY - px(92f),
                        t, 761, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(3.8f), aBase, true);
                drawLightningStreak(c, w * 0.33f, groundY - px(92f), w * 0.56f, groundY - px(68f),
                        t, 762, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 11: // Scene 12: Avengers Endgame Portals ("On Your Left")
                paint.setStyle(Paint.Style.STROKE);
                for (int pt = 0; pt < 3; pt++) {
                    float pxC = w * (0.28f + pt * 0.13f);
                    paint.setStrokeWidth(px(5.5f));
                    paint.setColor((aBase / 3 << 24) | 0x00FF5800);
                    c.drawOval(pxC - px(24f), groundY - px(102f), pxC + px(24f), groundY, paint);
                    paint.setStrokeWidth(px(2.4f));
                    paint.setColor((aBase << 24) | 0x00FFD048);
                    c.drawOval(pxC - px(22f), groundY - px(98f), pxC + px(22f), groundY - px(2f), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                drawCaptainAmerica(c, w * 0.35f, groundY - px(58f), t, aBase, true, true, 1.30f);
                drawIronMan(c, w * 0.48f, groundY - px(60f), t + 0.5f, aBase, false, false, 1.30f);
                drawDoctorStrange(c, w * 0.60f, groundY - px(62f), t + 1.0f, aBase, 1.28f);
                break;
            case 12: // Afghan Cave Escape — Iron Man Mark I Twin Flamethrowers
                drawIronMan(c, w * 0.36f, groundY - px(58f), t, aBase, false, false, 1.38f);
                drawLightningStreak(c, w * 0.40f, groundY - px(68f), w * 0.64f, groundY - px(54f),
                        t, 765, 0x00FF2800, 0x00FFA828, 0x00FFFFAA, px(4.6f), aBase, true);
                drawLightningStreak(c, w * 0.40f, groundY - px(62f), w * 0.62f, groundY - px(82f),
                        t, 766, 0x00FF3800, 0x00FFC838, 0x00FFFFAA, px(4.0f), aBase, true);
                break;
            case 13: // Monaco Grand Prix — Iron Man Mark V Suitcase Armor vs. Whiplash Electric Whips
                drawIronMan(c, w * 0.35f, groundY - px(58f), t, aBase, false, true, 1.34f);
                drawThorOdinson(c, w * 0.59f, groundY - px(58f), t + 1f, aBase, false, 1.32f);
                drawLightningStreak(c, w * 0.57f, groundY - px(68f), w * 0.38f, groundY - px(58f),
                        t, 767, 0x000088FF, 0x0068F4FF, 0x00FFFFFF, px(3.8f), aBase, true);
                drawLightningStreak(c, w * 0.57f, groundY - px(64f), w * 0.42f, groundY - px(84f),
                        t, 768, 0x00FF6800, 0x00FFE048, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 14: // Puente Antiguo — Thor Awakens Mjolnir vs. The Destroyer Armor Thermal Beam
                drawThorOdinson(c, w * 0.35f, groundY - px(62f), t, aBase, false, 1.36f);
                drawThanosTitan(c, w * 0.59f, groundY - px(62f), t + 1f, aBase, true, 1.36f);
                drawLightningStreak(c, w * 0.56f, groundY - px(88f), w * 0.39f, groundY - px(74f),
                        t, 769, 0x00FF2400, 0x00FF9820, 0x00FFFFFF, px(4.2f), aBase, true);
                drawLightningStreak(c, w * 0.35f, h * 0.66f, w * 0.35f, groundY - px(88f),
                        t, 770, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 15: // German Forest — Iron Man & Captain America vs. Thor Mjolnir Shield Shockwave
                drawCaptainAmerica(c, w * 0.36f, groundY - px(58f), t, aBase, true, false, 1.34f);
                drawThorOdinson(c, w * 0.56f, groundY - px(68f), t + 0.5f, aBase, false, 1.36f);
                drawIronMan(c, w * 0.27f, groundY - px(60f), t + 1.0f, aBase, false, false, 1.28f);
                drawLightningStreak(c, w * 0.46f, groundY - px(68f), w * 0.32f, groundY - px(102f),
                        t, 771, 0x000088FF, 0x0078F8FF, 0x00FFFFFF, px(3.8f), aBase, true);
                drawLightningStreak(c, w * 0.46f, groundY - px(68f), w * 0.62f, groundY - px(102f),
                        t, 772, 0x000088FF, 0x0078F8FF, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 16: // Manhattan Skyline — Hulk ("I'm Always Angry") Smashes Chitauri Leviathan
                drawIncredibleHulk(c, w * 0.36f, groundY - px(62f), t, aBase, false, 1.42f);
                drawDragonCustom(c, w * 0.56f, h * 0.75f, t, aBase, 0xFF687890, 1.32f);
                drawLightningStreak(c, w * 0.41f, groundY - px(74f), w * 0.52f, h * 0.75f,
                        t, 773, 0x0038FF68, 0x00A8FF98, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 17: // Triskelion Helicarriers — Captain America vs. Winter Soldier Bionic Arm Duel
                drawCaptainAmerica(c, w * 0.36f, groundY - px(58f), t * 1.4f, aBase, true, false, 1.35f);
                drawBlackPanther(c, w * 0.56f, groundY - px(58f), t * 1.4f + 0.8f, aBase, 1.35f);
                drawLightningStreak(c, w * 0.42f, groundY - px(66f), w * 0.52f, groundY - px(66f),
                        t, 774, 0x00FF6800, 0x00FFE048, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 18: // Xandar Showdown — Guardians of the Galaxy Holding the Violet Power Stone
                drawIronMan(c, w * 0.34f, groundY - px(58f), t, aBase, false, false, 1.28f);
                drawBlackPanther(c, w * 0.45f, groundY - px(58f), t + 0.4f, aBase, 1.28f);
                drawThanosTitan(c, w * 0.57f, groundY - px(60f), t + 0.8f, aBase, true, 1.32f);
                for (int r = 0; r < 4; r++) {
                    double ra = t * 3f + r * Math.PI * 0.5;
                    drawLightningStreak(c, w * 0.45f, groundY - px(68f),
                            w * 0.45f + (float) Math.cos(ra) * px(58f),
                            groundY - px(68f) + (float) Math.sin(ra) * px(44f),
                            t, 775 + r, 0x009818FF, 0x00E068FF, 0x00FFFFFF, px(3.4f), aBase, true);
                }
                break;
            case 19: // Johannesburg Streets — Iron Man Hulkbuster Armor vs. Rampaging Hulk
                drawIronMan(c, w * 0.35f, groundY - px(64f), t, aBase, false, true, 1.50f);
                drawIncredibleHulk(c, w * 0.58f, groundY - px(64f), t + 0.8f, aBase, true, 1.45f);
                drawLightningStreak(c, w * 0.42f, groundY - px(74f), w * 0.53f, groundY - px(74f),
                        t, 780, 0x000088FF, 0x0058F8FF, 0x00FFFFFF, px(4.2f), aBase, true);
                break;
            case 20: // Sokovia Church — Avengers 360-Degree Stand Against Ultron Sentries
                drawCaptainAmerica(c, w * 0.32f, groundY - px(58f), t, aBase, true, false, 1.28f);
                drawIronMan(c, w * 0.45f, groundY - px(62f), t + 0.5f, aBase, false, true, 1.30f);
                drawThorOdinson(c, w * 0.58f, groundY - px(58f), t + 1.0f, aBase, false, 1.30f);
                drawLightningStreak(c, w * 0.48f, groundY - px(74f), w * 0.66f, groundY - px(74f),
                        t, 781, 0x00FF1828, 0x00FF6838, 0x00FFFFFF, px(3.4f), aBase, true);
                break;
            case 21: // Leipzig Airport — Giant-Man vs. Iron Man & Spider-Man Web-Tow
                drawIronMan(c, w * 0.46f, groundY - px(78f), t * 0.7f, aBase, false, false, 1.72f);
                drawSpiderMan(c, w * 0.31f, h * 0.76f, groundY, t, aBase, 1.24f);
                drawIronMan(c, w * 0.62f, h * 0.75f, t + 0.8f, aBase, true, true, 1.24f);
                break;
            case 22: // Dark Dimension — Doctor Strange Green Time Stone Loop ("Dormammu, I've Come to Bargain")
                drawDoctorStrange(c, w * 0.36f, groundY - px(60f), t, aBase, 1.35f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase << 24) | 0x0038FF78);
                c.drawCircle(w * 0.39f, groundY - px(72f), px(24f), paint);
                paint.setStyle(Paint.Style.FILL);
                drawLightningStreak(c, w * 0.62f, h * 0.72f, w * 0.42f, groundY - px(72f),
                        t, 782, 0x009818FF, 0x0038FF78, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 23: // Staten Island Ferry — Spider-Man Holding Split Ferry Hulls & Iron Man Rescue
                drawSpiderMan(c, w * 0.46f, groundY - px(66f), groundY, t, aBase, 1.34f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.4f));
                paint.setColor((aBase << 24) | 0x00FFFFFF);
                for (int wb = -2; wb <= 2; wb++) {
                    c.drawLine(w * 0.46f, groundY - px(66f), w * 0.28f, groundY - px(52f + wb * 12f), paint);
                    c.drawLine(w * 0.46f, groundY - px(66f), w * 0.64f, groundY - px(52f + wb * 12f), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                drawIronMan(c, w * 0.62f, groundY - px(60f), t, aBase, true, false, 1.30f);
                break;
            case 24: // Sakaar Arena — Gladiator Thor Lightning Leap vs. Incredible Hulk
                drawThorOdinson(c, w * 0.36f, groundY - px(74f), t * 1.5f, aBase, false, 1.36f);
                drawIncredibleHulk(c, w * 0.58f, groundY - px(62f), t + 0.8f, aBase, true, 1.44f);
                drawLightningStreak(c, w * 0.36f, h * 0.66f, w * 0.56f, groundY - px(68f),
                        t, 783, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(4.2f), aBase, true);
                break;
            case 25: // Rainbow Bridge — Awakened Thor God of Thunder vs. Hela Necroswords
                drawThorOdinson(c, w * 0.35f, groundY - px(64f), t, aBase, false, 1.36f);
                drawBlackPanther(c, w * 0.59f, groundY - px(58f), t + 1f, aBase, 1.36f);
                drawLightningStreak(c, w * 0.35f, h * 0.65f, w * 0.59f, groundY - px(64f),
                        t, 784, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(4.4f), aBase, true);
                break;
            case 26: // Warrior Falls — T'Challa Black Panther & Shuri Vibranium Gauntlets
                drawBlackPanther(c, w * 0.36f, groundY - px(58f), t, aBase, 1.36f);
                drawBlackPanther(c, w * 0.55f, groundY - px(56f), t + 0.6f, aBase, 1.26f);
                drawLightningStreak(c, w * 0.53f, groundY - px(66f), w * 0.39f, groundY - px(66f),
                        t, 785, 0x007818FF, 0x0048E8FF, 0x00FFFFFF, px(3.6f), aBase, true);
                break;
            case 27: // New York Sanctum — Iron Man Bleeding Edge Nanotech Suit-Up & Doctor Strange
                drawIronMan(c, w * 0.35f, groundY - px(58f), t, aBase, false, true, 1.35f);
                drawDoctorStrange(c, w * 0.54f, groundY - px(60f), t + 0.6f, aBase, 1.34f);
                break;
            case 28: // Nidavellir Forge — Thor Holding Open the Iris of a Dying Neutron Star
                paint.setColor((aBase * 3 / 4 << 24) | 0x00FFB838);
                c.drawCircle(w * 0.30f, h * 0.76f, px(26f), paint);
                drawThorOdinson(c, w * 0.48f, h * 0.76f + px(14f), t, aBase, true, 1.36f);
                drawLightningStreak(c, w * 0.30f, h * 0.76f, w * 0.64f, h * 0.76f,
                        t, 786, 0x00FF6800, 0x00FFE858, 0x00FFFFFF, px(4.8f), aBase, true);
                break;
            case 29: // Battle of Titan — Doctor Strange Images of Ikonn vs. Thanos Four Stones
                for (int im = 0; im < 3; im++) {
                    drawDoctorStrange(c, w * (0.29f + im * 0.06f), groundY - px(60f + im * 8f), t + im * 0.3f, aBase * (im + 2) / 4, 1.26f);
                }
                drawThanosTitan(c, w * 0.59f, groundY - px(62f), t, aBase, true, 1.38f);
                drawLightningStreak(c, w * 0.41f, groundY - px(74f), w * 0.57f, groundY - px(74f),
                        t, 787, 0x00FF5800, 0x00FFD838, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 30: // Avengers Compound — Iron Man, Captain America & Thor vs. Warlord Thanos
                drawCaptainAmerica(c, w * 0.29f, groundY - px(58f), t, aBase, true, false, 1.28f);
                drawIronMan(c, w * 0.39f, groundY - px(60f), t + 0.4f, aBase, false, true, 1.30f);
                drawThorOdinson(c, w * 0.49f, groundY - px(58f), t + 0.8f, aBase, true, 1.30f);
                drawThanosTitan(c, w * 0.62f, groundY - px(62f), t + 1.2f, aBase, true, 1.38f);
                break;
            case 31: // Westview Hex — Wanda Maximoff Transforming into The Scarlet Witch
                drawScarletWitch(c, w * 0.46f, groundY - px(68f) + (float) Math.sin(t * 4f) * px(8f), t, aBase, 1.38f);
                for (int ch = 0; ch < 4; ch++) {
                    double ca = t * 3f + ch * Math.PI * 0.5;
                    drawLightningStreak(c, w * 0.46f, groundY - px(76f),
                            w * 0.46f + (float) Math.cos(ca) * px(58f),
                            groundY - px(76f) + (float) Math.sin(ca) * px(46f),
                            t, 788 + ch, 0x00B80028, 0x00FF2858, 0x00FFFFFF, px(3.6f), aBase, true);
                }
                break;
            case 32: // Ta Lo Village — Shang-Chi Ten Rings & The Great Protector Water Dragon
                drawDoctorStrange(c, w * 0.34f, groundY - px(58f), t, aBase, 1.34f);
                drawDragonCustom(c, w * 0.54f, h * 0.75f, t, aBase, 0xFF48D8FF, 1.28f);
                for (int rg = 0; rg < 5; rg++) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(px(2.4f));
                    paint.setColor((aBase << 24) | 0x00FFD838);
                    c.drawCircle(w * (0.36f + rg * 0.038f), groundY - px(74f) + (float) Math.sin(t * 6f + rg) * px(10f), px(6.5f), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                break;
            case 33: // Statue of Liberty — Three Spider-Men (Peter 1, Peter 2, Peter 3) Swinging in Unison
                drawSpiderMan(c, w * 0.32f, h * 0.77f + (float) Math.sin(t * 5f) * px(10f), groundY, t, aBase, 1.28f);
                drawSpiderMan(c, w * 0.46f, h * 0.74f + (float) Math.cos(t * 5f) * px(10f), groundY, t + 0.6f, aBase, 1.34f);
                drawSpiderMan(c, w * 0.60f, h * 0.77f - (float) Math.sin(t * 5f) * px(10f), groundY, t + 1.2f, aBase, 1.28f);
                break;
            case 34: // End of Time Citadel — Loki God of Stories Holding the Emerald Multiverse World Tree
                drawThorOdinson(c, w * 0.46f, groundY - px(62f), t, aBase, false, 1.36f);
                for (int br = -2; br <= 2; br++) {
                    drawLightningStreak(c, w * 0.46f, groundY - px(68f),
                            w * (0.46f + br * 0.08f), h * 0.68f,
                            t, 792 + br, 0x0000A848, 0x0038FF78, 0x00FFFFFF, px(3.4f), aBase, true);
                }
                break;
            case 35: // Scene 36: Final Sacrifice — "And I... Am... Iron Man" Infinity Snap
                drawIronMan(c, w * 0.46f, groundY - px(58f), t, aBase, false, false, 1.42f);
                int[] snapCols = {0xFF2888FF, 0xFFFFE038, 0xFFFF2438, 0xFFB838FF, 0xFF28FF78, 0xFFFF8820};
                for (int s = 0; s < 6; s++) {
                    double sa = t * 4f + s * Math.PI / 3.0;
                    drawLightningStreak(c, w * 0.46f + px(22f), groundY - px(82f),
                            w * 0.46f + px(22f) + (float) Math.cos(sa) * px(68f),
                            groundY - px(82f) + (float) Math.sin(sa) * px(58f),
                            t, 795 + s, snapCols[s], snapCols[s], 0x00FFFFFF, px(3.4f), aBase, true);
                }
                break;
            case 36: // Scene 37: Brooklyn 1943 — Steve Rogers Super-Soldier Vita-Ray Chamber
                paint.setColor((aBase / 3 << 24) | 0x0048D8FF);
                c.drawRoundRect(w * 0.41f, groundY - px(108f), w * 0.51f, groundY, px(8f), px(8f), paint);
                drawCaptainAmerica(c, w * 0.46f, groundY - px(58f), t, aBase, false, false, 1.38f);
                drawLightningStreak(c, w * 0.32f, groundY - px(68f), w * 0.46f, groundY - px(68f),
                        t, 801, 0x001888FF, 0x0078F8FF, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 37: // Scene 38: Stark Tower — Loki Chitauri Scepter vs. Iron Man Mark VII
                drawIronMan(c, w * 0.35f, groundY - px(60f), t, aBase, false, true, 1.35f);
                drawThorOdinson(c, w * 0.58f, groundY - px(58f), t + 0.8f, aBase, false, 1.35f);
                drawLightningStreak(c, w * 0.56f, groundY - px(68f), w * 0.38f, groundY - px(68f),
                        t, 802, 0x001888FF, 0x0058F4FF, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 38: // Scene 39: Asgard Vault — Eternal Flame Surtur Twilight Sword Ragnarok
                paint.setColor((aBase * 3 / 4 << 24) | 0x00FF4818);
                c.drawOval(w * 0.46f, groundY - px(132f), w * 0.62f, groundY, paint);
                drawThorOdinson(c, w * 0.33f, groundY - px(58f), t, aBase, false, 1.34f);
                drawLightningStreak(c, w * 0.54f, groundY - px(124f), w * 0.38f, groundY - px(24f),
                        t, 803, 0x00FF2400, 0x00FFB828, 0x00FFFFFF, px(4.8f), aBase, true);
                break;
            case 39: // Scene 40: Vormir Altar — Red Skull & The Golden Soul Stone Eclipse
                paint.setColor((aBase / 2 << 24) | 0x00FF9820);
                c.drawCircle(w * 0.48f, h * 0.72f, px(38f), paint);
                paint.setColor((aBase << 24) | 0x0014101C);
                c.drawCircle(w * 0.48f, h * 0.72f, px(28f), paint);
                drawDoctorStrange(c, w * 0.42f, groundY - px(62f), t, aBase, 1.34f);
                break;
            case 40: // Scene 41: Quantum Realm — Ant-Man & The Wasp Subatomic Time Vortex
                paint.setStyle(Paint.Style.STROKE);
                for (int qr = 1; qr <= 4; qr++) {
                    paint.setStrokeWidth(px(2.4f));
                    paint.setColor((aBase * (5 - qr) / 5 << 24) | (qr % 2 == 0 ? 0x0048E8FF : 0x00D858FF));
                    c.drawCircle(w * 0.48f, groundY - px(64f), px(18f * qr), paint);
                }
                paint.setStyle(Paint.Style.FILL);
                drawIronMan(c, w * 0.42f, groundY - px(60f), t, aBase, false, false, 1.28f);
                break;
            case 41: // Scene 42: Kamar-Taj Siege — Doctor Strange Crimson Bands of Cyttorak
                drawDoctorStrange(c, w * 0.35f, groundY - px(62f), t, aBase, 1.36f);
                drawScarletWitch(c, w * 0.58f, groundY - px(62f), t + 0.7f, aBase, 1.36f);
                drawLightningStreak(c, w * 0.38f, groundY - px(68f), w * 0.56f, groundY - px(68f),
                        t, 806, 0x00FF5800, 0x00FFD038, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 42: // Scene 43: Atlantis Rising — Shuri Black Panther vs. Namor Winged Anklets
                drawBlackPanther(c, w * 0.35f, groundY - px(58f), t, aBase, 1.34f);
                drawThorOdinson(c, w * 0.58f, groundY - px(74f) + (float) Math.sin(t * 5f) * px(10f), t + 0.8f, aBase, false, 1.34f);
                drawLightningStreak(c, w * 0.38f, groundY - px(66f), w * 0.55f, groundY - px(74f),
                        t, 807, 0x008828FF, 0x00D878FF, 0x00FFFFFF, px(3.6f), aBase, true);
                break;
            default: // Scene 44: Multiverse Saga — Wolverine Adamantium Claws & Deadpool Katana
                drawCaptainAmerica(c, w * 0.36f, groundY - px(58f), t * 1.5f, aBase, false, false, 1.35f);
                drawSpiderMan(c, w * 0.56f, groundY - px(58f), groundY, t * 1.5f + 0.6f, aBase, 1.35f);
                drawLightningStreak(c, w * 0.39f, groundY - px(66f), w * 0.53f, groundY - px(66f),
                        t, 808, 0x00FF9800, 0x00FFE858, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
        }
        c.restore();
    }

    /**
     * Bespoke MCU Iron Man (Mark LXXXV) Renderer:
     * - Hot-rod red & gold segmented armor plating
     * - Blazing cyan-white Chest Arc Reactor + glowing cyan slit eyes on gold faceplate
     * - Boot jet thrusters + outstretched Repulsor palm beam
     */
    private void drawIronMan(Canvas c, float x, float y, float t, int alpha,
                             boolean facingLeft, boolean firingRepulsor, float s) {
        float dir = facingLeft ? -1f : 1f;
        float hover = (float) Math.sin(t * 6f) * px(3f * s);
        float cy = y + hover;

        // 1. Boot Jet Thruster Flames
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha * 3 / 4 << 24) | 0x0038E8FF);
        c.drawCircle(x - dir * px(7f * s), cy + px(35f * s), px(5.5f * s), paint);
        c.drawCircle(x + dir * px(7f * s), cy + px(35f * s), px(5.5f * s), paint);

        // 2. Red & Gold Armored Legs
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(5.2f * s));
        paint.setColor((alpha << 24) | 0x00FFC838); // Gold thighs
        c.drawLine(x - dir * px(4f * s), cy + px(6f * s), x - dir * px(6f * s), cy + px(19f * s), paint);
        c.drawLine(x + dir * px(4f * s), cy + px(6f * s), x + dir * px(6f * s), cy + px(19f * s), paint);
        paint.setColor((alpha << 24) | 0x00D81E28); // Red armored boots
        c.drawLine(x - dir * px(6f * s), cy + px(19f * s), x - dir * px(7f * s), cy + px(32f * s), paint);
        c.drawLine(x + dir * px(6f * s), cy + px(19f * s), x + dir * px(7f * s), cy + px(32f * s), paint);

        // 3. Hot-Rod Red Armored Torso, Gold Shoulder Pads & Glowing Cyan Arc Reactor
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00D81E28);
        c.drawRoundRect(x - px(8.5f * s), cy - px(19f * s), x + px(8.5f * s), cy + px(8f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFC838);
        c.drawCircle(x - px(9.5f * s), cy - px(15f * s), px(4.2f * s), paint);
        c.drawCircle(x + px(9.5f * s), cy - px(15f * s), px(4.2f * s), paint);
        // Glowing Cyan Chest Arc Reactor
        paint.setColor((alpha / 2 << 24) | 0x0038F0FF);
        c.drawCircle(x + dir * px(1.5f * s), cy - px(10f * s), px(5.2f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(x + dir * px(1.5f * s), cy - px(10f * s), px(2.6f * s), paint);

        // 4. Iron Man Helmet (Red Dome + Gold Faceplate + Cyan Slit Eyes)
        float headX = x + dir * px(1f * s), headY = cy - px(26.5f * s);
        paint.setColor((alpha << 24) | 0x00D81E28);
        c.drawRoundRect(headX - px(6.5f * s), headY - px(7.5f * s), headX + px(6.5f * s), headY + px(6.5f * s), px(3.5f * s), px(3.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFC838);
        c.drawRoundRect(headX - px(4.8f * s), headY - px(4.5f * s), headX + px(4.8f * s), headY + px(5.5f * s), px(2f * s), px(2f * s), paint);
        paint.setColor((alpha << 24) | 0x0068F8FF);
        c.drawRect(headX - px(3.8f * s), headY - px(2.0f * s), headX - px(0.8f * s), headY - px(0.4f * s), paint);
        c.drawRect(headX + px(0.8f * s), headY - px(2.0f * s), headX + px(3.8f * s), headY - px(0.4f * s), paint);

        // 5. Outstretched Repulsor Arm & Cyan Repulsor Blast
        float handX = x + dir * px(20f * s), handY = cy - px(11f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(4.2f * s));
        paint.setColor((alpha << 24) | 0x00D81E28);
        c.drawLine(x + dir * px(6f * s), cy - px(14f * s), handX, handY, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(handX, handY, px(3.5f * s), paint);
        if (firingRepulsor) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(6.5f * s));
            paint.setColor((alpha / 2 << 24) | 0x0038F0FF);
            c.drawLine(handX, handY, handX + dir * px(75f * s), handY, paint);
            paint.setStrokeWidth(px(2.6f * s));
            paint.setColor((alpha << 24) | 0x00FFFFFF);
            c.drawLine(handX, handY, handX + dir * px(75f * s), handY, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    /**
     * Bespoke MCU Captain America (Steve Rogers) Renderer:
     * - Navy-blue tactical suit, white Chest Star, red & white abdominal stripes
     * - Blue helmet cowl with white 'A' insignia
     * - Concentric Red-White-Blue Vibranium Star Shield + optional Mjolnir hammer
     */
    private void drawCaptainAmerica(Canvas c, float x, float y, float t, int alpha,
                                    boolean holdingShield, boolean holdingMjolnir, float s) {
        float stride = (float) Math.sin(t * 6.5f) * px(5f * s);
        // Navy Legs & Red Avengers Boots
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(5.0f * s));
        paint.setColor((alpha << 24) | 0x001E3E88);
        c.drawLine(x - px(4f * s), y + px(6f * s), x - px(9f * s) - stride, y + px(23f * s), paint);
        c.drawLine(x + px(4f * s), y + px(6f * s), x + px(9f * s) + stride, y + px(23f * s), paint);
        paint.setColor((alpha << 24) | 0x00C81E28);
        c.drawLine(x - px(9f * s) - stride, y + px(23f * s), x - px(10f * s) - stride, y + px(34f * s), paint);
        c.drawLine(x + px(9f * s) + stride, y + px(23f * s), x + px(10f * s) + stride, y + px(34f * s), paint);

        // Navy Torso, Red-and-White Stripes & White Chest Star
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x001E3E88);
        c.drawRoundRect(x - px(8.5f * s), y - px(19f * s), x + px(8.5f * s), y + px(8f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(x + px(1f * s), y - px(11f * s), px(3.2f * s), paint);
        for (int st = -2; st <= 2; st++) {
            paint.setColor((alpha << 24) | (st % 2 == 0 ? 0x00D82028 : 0x00FFFFFF));
            c.drawRect(x + st * px(2.6f * s) - px(1f * s), y - px(3f * s), x + st * px(2.6f * s) + px(1f * s), y + px(5f * s), paint);
        }

        // Helmet Cowl & Skin Jaw
        float headX = x + px(1f * s), headY = y - px(26.5f * s);
        paint.setColor((alpha << 24) | 0x00F4D0B4);
        c.drawCircle(headX, headY, px(6.5f * s), paint);
        paint.setColor((alpha << 24) | 0x001E3E88);
        c.drawArc(rectF(headX - px(7f * s), headY - px(7.5f * s), headX + px(7f * s), headY + px(2f * s)), 170, 200, true, paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(headX + px(1.5f * s), headY - px(3.8f * s), px(1.5f * s), paint);

        // Concentric Vibranium Shield on Forearm
        if (holdingShield) {
            drawVibraniumShield(c, x + px(15f * s), y - px(6f * s), px(12.5f * s), alpha);
        }
        // Worthy Cap Holding Mjolnir Overhead
        if (holdingMjolnir) {
            float mx = x - px(14f * s), my = y - px(28f * s);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.6f * s));
            paint.setColor((alpha << 24) | 0x00684024);
            c.drawLine(x - px(6f * s), y - px(14f * s), mx, my, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor((alpha << 24) | 0x00B8C8D8);
            c.drawRoundRect(mx - px(7f * s), my - px(9f * s), mx + px(7f * s), my - px(1f * s), px(2f * s), px(2f * s), paint);
        }
    }

    private void drawVibraniumShield(Canvas c, float x, float y, float r, int alpha) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00E82028);
        c.drawCircle(x, y, r, paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(x, y, r * 0.72f, paint);
        paint.setColor((alpha << 24) | 0x00E82028);
        c.drawCircle(x, y, r * 0.48f, paint);
        paint.setColor((alpha << 24) | 0x001E50C8);
        c.drawCircle(x, y, r * 0.30f, paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(x, y, r * 0.14f, paint);
    }

    /**
     * Bespoke MCU Thor Odinson (God of Thunder) Renderer:
     * - Crimson Asgardian cape, dark chest armor with 6 silver Asgardian discs
     * - Golden hair + glowing cyan lightning eyes + Mjolnir or Stormbreaker battle-axe
     */
    private void drawThorOdinson(Canvas c, float x, float y, float t, int alpha, boolean stormbreaker, float s) {
        float capeWave = (float) Math.sin(t * 7.5f) * px(7f * s);
        // Crimson Asgardian Cape
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00C81820);
        actorPath.reset();
        actorPath.moveTo(x - px(8f * s), y - px(19f * s));
        actorPath.lineTo(x + px(8f * s), y - px(19f * s));
        actorPath.lineTo(x - px(4f * s), y + px(32f * s));
        actorPath.lineTo(x - px(28f * s) - capeWave, y + px(30f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // Dark Asgardian Armor & 6 Silver Chest Discs
        paint.setColor((alpha << 24) | 0x00202838);
        c.drawRoundRect(x - px(8.5f * s), y - px(19f * s), x + px(8.5f * s), y + px(10f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | 0x00D0DCE8);
        for (int row = 0; row < 3; row++) {
            c.drawCircle(x - px(3.5f * s), y - px((14f - row * 6f) * s), px(1.8f * s), paint);
            c.drawCircle(x + px(3.5f * s), y - px((14f - row * 6f) * s), px(1.8f * s), paint);
        }

        // Head, Golden Hair & Glowing Cyan Lightning Eyes
        float headX = x + px(1f * s), headY = y - px(26.5f * s);
        paint.setColor((alpha << 24) | 0x00F4D0B4);
        c.drawCircle(headX, headY, px(6.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFD848);
        c.drawArc(rectF(headX - px(7.5f * s), headY - px(8.5f * s), headX + px(7.5f * s), headY + px(4f * s)), 140, 230, true, paint);
        paint.setColor((alpha << 24) | 0x0068F8FF);
        c.drawCircle(headX + px(2.2f * s), headY - px(1.0f * s), px(1.6f * s), paint);

        // Raised Stormbreaker Battle-Axe or Mjolnir Hammer
        float wx = x + px(18f * s), wy = y - px(24f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(3.0f * s));
        paint.setColor((alpha << 24) | 0x00784C28);
        c.drawLine(x + px(7f * s), y - px(8f * s), wx, wy, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00C8D8E8);
        if (stormbreaker) {
            c.drawArc(rectF(wx - px(4f * s), wy - px(10f * s), wx + px(12f * s), wy + px(6f * s)), -60, 120, true, paint);
            c.drawRect(wx - px(7f * s), wy - px(5f * s), wx, wy + px(2f * s), paint);
        } else {
            c.drawRoundRect(wx - px(7f * s), wy - px(8f * s), wx + px(7f * s), wy, px(2f * s), px(2f * s), paint);
        }
    }

    /**
     * Bespoke MCU Doctor Strange Renderer:
     * - High-collared Crimson Cloak of Levitation + Blue Kamar-Taj Tunic
     * - Glowing Green Eye of Agamotto + Twin Orange-Gold Eldritch Tao Mandala Shields
     */
    private void drawDoctorStrange(Canvas c, float x, float y, float t, int alpha, float s) {
        float capeWave = (float) Math.sin(t * 6.5f) * px(6.5f * s);
        paint.setStyle(Paint.Style.FILL);
        // Crimson Cloak of Levitation with High Upturned Collar
        paint.setColor((alpha << 24) | 0x00D81E28);
        actorPath.reset();
        actorPath.moveTo(x - px(11f * s), y - px(29f * s));
        actorPath.lineTo(x + px(9f * s), y - px(29f * s));
        actorPath.lineTo(x + px(12f * s), y + px(28f * s));
        actorPath.lineTo(x - px(24f * s) - capeWave, y + px(30f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Blue Kamar-Taj Tunic & Green Eye of Agamotto
        paint.setColor((alpha << 24) | 0x001C3058);
        c.drawRoundRect(x - px(7.5f * s), y - px(18f * s), x + px(7.5f * s), y + px(18f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | 0x0038FF78);
        c.drawCircle(x + px(1f * s), y - px(12f * s), px(2.5f * s), paint);
        // Head
        paint.setColor((alpha << 24) | 0x00F4D0B4);
        c.drawCircle(x + px(1f * s), y - px(26f * s), px(6.2f * s), paint);
        // Twin Eldritch Tao Mandala Shields
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.2f * s));
        paint.setColor((alpha << 24) | 0x00FFA828);
        float m1X = x + px(22f * s), m1Y = y - px(10f * s);
        c.drawCircle(m1X, m1Y, px(16f * s), paint);
        c.drawRect(m1X - px(11f * s), m1Y - px(11f * s), m1X + px(11f * s), m1Y + px(11f * s), paint);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Bespoke MCU Spider-Man Acrobatic Web-Swinging Renderer
     */
    private void drawSpiderMan(Canvas c, float x, float y, float groundY, float t, int alpha, float s) {
        // Taut White Web-Line to top of stage
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.2f));
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawLine(x + px(12f * s), groundY - px(135f), x + px(8f * s), y - px(14f * s), paint);

        // Acrobatic crouched blue legs & red torso
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(4.8f * s));
        paint.setColor((alpha << 24) | 0x001C48B8);
        c.drawLine(x - px(4f * s), y + px(4f * s), x - px(14f * s), y + px(14f * s), paint);
        c.drawLine(x + px(4f * s), y + px(4f * s), x + px(14f * s), y + px(12f * s), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00E81C24);
        c.drawRoundRect(x - px(7.5f * s), y - px(16f * s), x + px(7.5f * s), y + px(6f * s), px(4f * s), px(4f * s), paint);
        // Red Mask with Large White Spider-Eyes
        float headX = x + px(2f * s), headY = y - px(23f * s);
        c.drawCircle(headX, headY, px(6.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawOval(headX - px(4.5f * s), headY - px(3f * s), headX - px(0.5f * s), headY + px(1.5f * s), paint);
        c.drawOval(headX + px(0.5f * s), headY - px(3f * s), headX + px(4.5f * s), headY + px(1.5f * s), paint);
    }

    /**
     * Bespoke MCU Black Panther (T'Challa) Renderer with Pointed Panther Ears, Silver Claw Necklace & Violet Kinetic Lines
     */
    private void drawBlackPanther(Canvas c, float x, float y, float t, int alpha, float s) {
        drawNinjaSilhouette(c, x, y, t, alpha, 0xFF14121E, 0xFFA848FF, false, false, false, 0xFFA848FF, s);
        float headX = x + px(2f * s), headY = y - px(27f * s);
        paint.setStyle(Paint.Style.FILL);
        // Pointed Panther Cowl Ears
        paint.setColor((alpha << 24) | 0x0014121E);
        c.drawCircle(headX - px(4.5f * s), headY - px(6.5f * s), px(2.5f * s), paint);
        c.drawCircle(headX + px(4.5f * s), headY - px(6.5f * s), px(2.5f * s), paint);
        // Silver Vibranium Claw Necklace
        paint.setColor((alpha << 24) | 0x00DCE4F0);
        for (int cl = -2; cl <= 2; cl++) {
            c.drawCircle(x + cl * px(2.8f * s), y - px(18f * s), px(1.4f * s), paint);
        }
    }

    /**
     * Bespoke MCU Thanos (The Mad Titan) Renderer:
     * - Massive 1.35x broad-shouldered gold & navy Sanctuary armor + purple Titan head
     * - Raised Golden Infinity Gauntlet blazing with the 6 Infinity Stones!
     */
    private void drawThanosTitan(Canvas c, float x, float y, float t, int alpha, boolean facingLeft, float s) {
        float dir = facingLeft ? -1f : 1f;
        // Massive Armored Legs & Torso
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00242838);
        c.drawRoundRect(x - px(12f * s), y - px(22f * s), x + px(12f * s), y + px(12f * s), px(4f * s), px(4f * s), paint);
        // Golden Titan Shoulder & Chest Armor
        paint.setColor((alpha << 24) | 0x00FFC838);
        c.drawOval(x - px(16f * s), y - px(23f * s), x + px(16f * s), y - px(14f * s), paint);
        c.drawRect(x - px(9f * s), y - px(16f * s), x + px(9f * s), y - px(2f * s), paint);
        // Purple Titan Head & Gold Helmet Crown
        float headX = x + dir * px(1f * s), headY = y - px(30f * s);
        paint.setColor((alpha << 24) | 0x008848A8);
        c.drawRoundRect(headX - px(7.5f * s), headY - px(7.5f * s), headX + px(7.5f * s), headY + px(7.5f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFC838);
        c.drawArc(rectF(headX - px(8f * s), headY - px(8.5f * s), headX + px(8f * s), headY), 180, 180, true, paint);
        // Raised Golden Infinity Gauntlet with Glowing Infinity Stones
        float gx = x + dir * px(20f * s), gy = y - px(18f * s);
        c.drawRoundRect(gx - px(5.5f * s), gy - px(6.5f * s), gx + px(5.5f * s), gy + px(5.5f * s), px(2.5f * s), px(2.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(gx, gy - px(2f * s), px(3.0f * s), paint);
    }

    /**
     * Bespoke MCU Incredible Hulk Renderer:
     * - Colossal Gamma-Green Muscular Frame, Broad Trapezius & Pectoral Deltoids
     * - Iconic Torn Purple Shorts & Bare Gamma-Green Calves
     * - Shaggy Dark Hair, Roaring Brow & Raised Gamma-Charged Smash Fists
     */
    private void drawIncredibleHulk(Canvas c, float x, float y, float t, int alpha, boolean facingLeft, float s) {
        float dir = facingLeft ? -1f : 1f;
        float breathe = (float) Math.sin(t * 5f) * px(2.0f * s);
        // Gamma radiation ground aura
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((Math.max(0, alpha / 5) << 24) | 0x0038FF68);
        c.drawCircle(x, y - px(6f * s), px(34f * s), paint);
        // Bare Gamma-Green Calves & Torn Purple Shorts
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(6.5f * s));
        paint.setColor((alpha << 24) | 0x0038A848);
        c.drawLine(x - dir * px(6f * s), y + px(14f * s), x - dir * px(10f * s), y + px(34f * s), paint);
        c.drawLine(x + dir * px(6f * s), y + px(14f * s), x + dir * px(10f * s), y + px(34f * s), paint);
        paint.setColor((alpha << 24) | 0x00582888); // Torn purple shorts
        c.drawLine(x - dir * px(5f * s), y + px(4f * s), x - dir * px(7f * s), y + px(18f * s), paint);
        c.drawLine(x + dir * px(5f * s), y + px(4f * s), x + dir * px(7f * s), y + px(18f * s), paint);
        // Colossal Gamma-Green Torso & Shoulders
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x0038A848);
        c.drawRoundRect(x - px(13.5f * s), y - px(22f * s) + breathe, x + px(13.5f * s), y + px(8f * s), px(5f * s), px(5f * s), paint);
        c.drawCircle(x - px(14f * s), y - px(16f * s) + breathe, px(6.5f * s), paint);
        c.drawCircle(x + px(14f * s), y - px(16f * s) + breathe, px(6.5f * s), paint);
        // Hulk Head, Dark Unkempt Hair & Glowing Gamma-Green Eyes
        float headX = x + dir * px(1.5f * s), headY = y - px(29.5f * s) + breathe;
        c.drawRoundRect(headX - px(7.5f * s), headY - px(7.5f * s), headX + px(7.5f * s), headY + px(7.5f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | 0x00181C20);
        c.drawArc(rectF(headX - px(8f * s), headY - px(9f * s), headX + px(8f * s), headY), 170, 200, true, paint);
        paint.setColor((alpha << 24) | 0x0078FF88);
        c.drawCircle(headX + dir * px(2.8f * s), headY - px(1.5f * s), px(1.8f * s), paint);
        // Raised Smash Fist
        float fx = x + dir * px(22f * s), fy = y - px(20f * s) + breathe;
        paint.setColor((alpha << 24) | 0x0038A848);
        c.drawCircle(fx, fy, px(6.2f * s), paint);
    }

    /**
     * Bespoke MCU Scarlet Witch (Wanda Maximoff) Renderer:
     * - Sculpted Crimson Tiara Crown with twin horns & flowing auburn hair
     * - Crimson Sokovian Corset Coat & Billowing Cape
     * - Twin Blazing Chaos Magic Hex Spheres in both hands
     */
    private void drawScarletWitch(Canvas c, float x, float y, float t, int alpha, float s) {
        float capeWave = (float) Math.sin(t * 6.5f) * px(6.5f * s);
        paint.setStyle(Paint.Style.FILL);
        // Billowing Dark-Crimson Cape & Skirt
        paint.setColor((alpha << 24) | 0x00881024);
        actorPath.reset();
        actorPath.moveTo(x - px(7f * s), y - px(18f * s));
        actorPath.lineTo(x + px(7f * s), y - px(18f * s));
        actorPath.lineTo(x + px(13f * s), y + px(30f * s));
        actorPath.lineTo(x - px(22f * s) - capeWave, y + px(32f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Scarlet Corset Bodice
        paint.setColor((alpha << 24) | 0x00D81834);
        c.drawRoundRect(x - px(7.0f * s), y - px(18f * s), x + px(7.0f * s), y + px(8f * s), px(3f * s), px(3f * s), paint);
        // Head, Flowing Auburn Hair & Iconic Sculpted Scarlet Witch Crown Tiara
        float headX = x + px(1f * s), headY = y - px(26f * s);
        paint.setColor((alpha << 24) | 0x00983824);
        c.drawOval(headX - px(9.5f * s), headY - px(8f * s), headX + px(8.5f * s), headY + px(12f * s), paint);
        paint.setColor((alpha << 24) | 0x00F4D0B4);
        c.drawCircle(headX, headY, px(6.0f * s), paint);
        // Sculpted M-shaped Crimson Tiara Crown & Glowing Red Chaos Eyes
        paint.setColor((alpha << 24) | 0x00FF1838);
        actorPath.reset();
        actorPath.moveTo(headX - px(5.5f * s), headY - px(3f * s));
        actorPath.lineTo(headX - px(3.5f * s), headY - px(10.5f * s));
        actorPath.lineTo(headX, headY - px(5.5f * s));
        actorPath.lineTo(headX + px(3.5f * s), headY - px(10.5f * s));
        actorPath.lineTo(headX + px(5.5f * s), headY - px(3f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        c.drawCircle(headX + px(2.0f * s), headY - px(1.0f * s), px(1.6f * s), paint);
        // Twin Blazing Chaos Magic Hex Orbs in both hands
        float orbPulse = px((6.5f + 2.0f * (float) Math.sin(t * 14f)) * s);
        paint.setColor((alpha / 2 << 24) | 0x00FF1848);
        c.drawCircle(x - px(16f * s), y - px(8f * s), orbPulse * 1.4f, paint);
        c.drawCircle(x + px(18f * s), y - px(10f * s), orbPulse * 1.4f, paint);
        paint.setColor((alpha << 24) | 0x00FF98B0);
        c.drawCircle(x - px(16f * s), y - px(8f * s), orbPulse * 0.65f, paint);
        c.drawCircle(x + px(18f * s), y - px(10f * s), orbPulse * 0.65f, paint);
    }

    private void drawDcuStoryLoop(Canvas c, int w, int h, float t) {
        int count = DCU_TITLES.length;
        float totalCycle = CHAPTER_SEC * count;
        float cycleTime = t % totalCycle;
        int scene = resolveActiveScene(cycleTime, count);
        float p = (cycleTime % CHAPTER_SEC) / CHAPTER_SEC;
        float fade = resolveStoryFade(p);
        int aBase = Math.min(255, Math.max(0, Math.round(fade * 255f)));
        float groundY = h * 0.900f;
        drawStoryCaptionAndDots(c, w, h, DCU_TITLES[scene], scene, count, aBase,
                0x00FFE082, 0xEEFFE068, 0x4268B8FF, px(10.5f));

        c.save();
        applyStageTransform(c, w, h, groundY);
        switch (scene) {
            case 0: // Gotham Cathedral — The Dark Knight on Gargoyle + Bat-Signal + Flock of Bats
                // Glowing Gotham Bat-Signal Spotlight in the Sky
                paint.setColor((aBase / 5 << 24) | 0x00FFE878);
                actorPath.reset();
                actorPath.moveTo(w * 0.34f, groundY);
                actorPath.lineTo(w * 0.56f, h * 0.70f);
                actorPath.lineTo(w * 0.64f, h * 0.74f);
                actorPath.close();
                c.drawPath(actorPath, paint);
                paint.setColor((aBase * 2 / 3 << 24) | 0x00FFE888);
                c.drawOval(w * 0.55f, h * 0.69f, w * 0.65f, h * 0.75f, paint);
                // Sculpted Gothic Gargoyle Pedestal
                paint.setColor((aBase << 24) | 0x00242C3A);
                c.drawRect(w * 0.43f, groundY - px(38f), w * 0.51f, groundY, paint);
                c.drawOval(w * 0.41f, groundY - px(44f), w * 0.53f, groundY - px(32f), paint);
                drawBatmanDarkKnight(c, w * 0.47f, groundY - px(84f), t, aBase, false, 1.35f);
                for (int b = 0; b < 7; b++) {
                    float bp = (p + b * 0.13f) % 1.0f;
                    drawBird(c, w * (0.30f + bp * 0.36f), groundY - px(55f + b * 14f), t * 14f + b, px(14f), (aBase << 24) | 0x00FFE068);
                }
                break;
            case 1: // Gotham Streets — The Tumbler Batmobile Pursuit with Afterburner Flame
                float bmX = w * (0.28f + p * 0.34f);
                // Roaring Jet Afterburner Plume & Sparks
                drawLightningStreak(c, bmX - px(88f), groundY - px(16f), bmX - px(46f), groundY - px(16f),
                        t, 801, 0x00FF3800, 0x00FFA820, 0x00FFFFFF, px(4.0f), aBase, true);
                paint.setColor((aBase << 24) | 0x001C2434);
                actorPath.reset();
                actorPath.moveTo(bmX - px(48f), groundY - px(10f));
                actorPath.lineTo(bmX - px(38f), groundY - px(30f));
                actorPath.lineTo(bmX + px(14f), groundY - px(32f));
                actorPath.lineTo(bmX + px(50f), groundY - px(14f));
                actorPath.lineTo(bmX + px(48f), groundY - px(6f));
                actorPath.close();
                c.drawPath(actorPath, paint);
                paint.setColor((aBase << 24) | 0x00FFE068);
                c.drawCircle(bmX - px(30f), groundY - px(8f), px(11f), paint);
                c.drawCircle(bmX + px(30f), groundY - px(8f), px(11f), paint);
                paint.setColor((aBase << 24) | 0x0010141C);
                c.drawCircle(bmX - px(30f), groundY - px(8f), px(6f), paint);
                c.drawCircle(bmX + px(30f), groundY - px(8f), px(6f), paint);
                break;
            case 2: // Metropolis Skies — Superman Man of Steel Supersonic Flight
                float supX = w * (0.28f + p * 0.36f), supY = h * 0.77f + (float) Math.sin(t * 4f) * px(10f);
                // Double Conical Sonic Boom Vapor Rings
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.6f));
                paint.setColor((aBase * 2 / 3 << 24) | 0x00DCEEFF);
                c.drawOval(supX - px(28f), supY - px(28f), supX - px(12f), supY + px(28f), paint);
                c.drawOval(supX - px(48f), supY - px(38f), supX - px(28f), supY + px(38f), paint);
                paint.setStyle(Paint.Style.FILL);
                drawSupermanManOfSteel(c, supX, supY, t, aBase, false, false, 1.35f);
                break;
            case 3: // Superman Heat Vision Beam
                float sx = w * 0.34f, sy = groundY - px(60f);
                drawSupermanManOfSteel(c, sx, sy, t, aBase, true, false, 1.36f);
                drawLightningStreak(c, sx + px(5f), sy - px(25f), w * 0.64f, groundY - px(32f),
                        t, 811, 0x00FF0000, 0x00FF6820, 0x00FFFFFF, px(4.4f), aBase, true);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase / 2 << 24) | 0x00FF2424);
                c.drawCircle(w * 0.64f, groundY - px(32f), px(24f), paint);
                paint.setColor((aBase << 24) | 0x00FFFFAA);
                c.drawCircle(w * 0.64f, groundY - px(32f), px(10f), paint);
                break;
            case 4: // Wonder Woman & Golden Lasso of Truth
                float wwX = w * 0.36f, wwY = groundY - px(58f);
                drawWonderWoman(c, wwX, wwY, t, aBase, 1.36f);
                drawLightningStreak(c, wwX + px(14f), wwY - px(10f), wwX + px(118f), wwY - px(18f),
                        t, 821, 0x00FF8800, 0x00FFE038, 0x00FFFFFF, px(3.2f), aBase, false);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase << 24) | 0x00FFE858);
                c.drawOval(wwX + px(95f), wwY - px(36f), wwX + px(142f), wwY + px(2f), paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            case 5: // Central City — The Flash Entering the Speed Force (High-Voltage Lightning Streaks!)
                float startFlX = w * 0.24f;
                float flX = w * (0.28f + p * 0.34f);
                float flY = groundY - px(56f);
                // 1. Swirling Speed Force Dimensional Portal ahead of Barry
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(4.5f));
                paint.setColor((aBase / 2 << 24) | 0x00FF7800);
                c.drawOval(w * 0.64f, groundY - px(96f), w * 0.69f, groundY - px(4f), paint);
                paint.setStrokeWidth(px(2.2f));
                paint.setColor((aBase << 24) | 0x00FFE848);
                c.drawOval(w * 0.645f, groundY - px(90f), w * 0.685f, groundY - px(10f), paint);
                paint.setStyle(Paint.Style.FILL);
                // 2. Three Crimson-Gold Speed Force Afterimage Ghosts of The Flash
                for (int gh = 1; gh <= 3; gh++) {
                    float gf = gh / 4.0f;
                    float gx = startFlX + (flX - startFlX) * gf;
                    drawTheFlash(c, gx, flY, t * 2.5f - gh * 0.25f, aBase * gh / 6, 1.28f);
                }
                // 3. Four Jagged, Multi-Branched Speed Force Lightning Streaks (NO straight lines!)
                drawLightningStreak(c, startFlX, flY - px(18f), flX + px(12f), flY - px(20f),
                        t, 851, 0x00FF5800, 0x00FFE028, 0x00FFFFFF, px(4.4f), aBase, true);
                drawLightningStreak(c, startFlX + px(8f), flY - px(2f), flX + px(16f), flY - px(4f),
                        t, 852, 0x00FF3800, 0x00FFC818, 0x00FFFFFF, px(3.8f), aBase, true);
                drawLightningStreak(c, startFlX + px(14f), flY + px(16f), flX + px(8f), flY + px(14f),
                        t, 853, 0x00FF7800, 0x00FFF058, 0x00FFFFFF, px(3.4f), aBase, true);
                drawLightningStreak(c, flX - px(18f), flY - px(28f), flX + px(28f), flY - px(12f),
                        t, 854, 0x00FF8800, 0x00FFE838, 0x00FFFFFF, px(2.8f), aBase, false);
                // 4. The Flash Sprinting at Superluminal Velocity
                drawTheFlash(c, flX, flY, t * 2.5f, aBase, 1.36f);
                break;
            case 6: // Kingdom of Atlantis — Aquaman & Trident of Atlan Tidal Waves
                // Sculpted Cresting Ocean Tidal Waves
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase * 3 / 5 << 24) | 0x001888D8);
                c.drawArc(rectF(w * 0.28f, groundY - px(82f), w * 0.44f, groundY + px(20f)), 180, 180, true, paint);
                c.drawArc(rectF(w * 0.48f, groundY - px(82f), w * 0.64f, groundY + px(20f)), 180, 180, true, paint);
                drawAquaman(c, w * 0.46f, groundY - px(58f), t, aBase, 1.36f);
                drawLightningStreak(c, w * 0.46f + px(24f), h * 0.68f, w * 0.46f + px(24f), groundY - px(96f),
                        t, 861, 0x00FF8800, 0x00FFE048, 0x00FFFFFF, px(3.2f), aBase, true);
                break;
            case 7: // Sector 2814 — Green Lantern Power Ring Construct
                float glX = w * 0.36f, glY = groundY - px(60f);
                drawGreenLantern(c, glX, glY, t, aBase, 1.35f);
                drawLightningStreak(c, glX + px(18f), glY - px(10f), w * 0.56f, glY - px(20f),
                        t, 871, 0x0000A848, 0x0038FF88, 0x00FFFFFF, px(3.8f), aBase, true);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.2f));
                paint.setColor((aBase << 24) | 0x0038FF88);
                c.drawCircle(w * 0.56f, glY - px(20f), px(22f), paint);
                c.drawRect(w * 0.56f - px(12f), glY - px(36f), w * 0.56f + px(12f), glY - px(4f), paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            case 8: // Gotham Docks — Armored Batman vs. Superman
                drawBatmanDarkKnight(c, w * 0.35f, groundY - px(58f), t, aBase, true, 1.36f);
                drawSupermanManOfSteel(c, w * 0.58f, groundY - px(62f), t + 1f, aBase, true, true, 1.36f);
                drawLightningStreak(c, w * 0.39f, groundY - px(78f), w * 0.47f, groundY - px(74f),
                        t, 881, 0x0000A848, 0x0038FF88, 0x00FFFFFF, px(3.4f), aBase, true);
                drawLightningStreak(c, w * 0.56f, groundY - px(86f), w * 0.47f, groundY - px(74f),
                        t, 882, 0x00FF0000, 0x00FF5820, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 9: // Arkham Asylum — Batwing Glide Under the Moon
                float bwX = w * (0.28f + p * 0.36f), bwY = h * 0.76f + (float) Math.sin(t * 4f) * px(10f);
                drawBird(c, bwX, bwY, 0.4f, px(42f), (aBase << 24) | 0x00FFE068);
                break;
            case 10: // Hall of Justice — The Justice League United (Batman, Superman, Wonder Woman, Flash)
                drawBatmanDarkKnight(c, w * 0.31f, groundY - px(58f), t, aBase, false, 1.28f);
                drawSupermanManOfSteel(c, w * 0.42f, groundY - px(60f), t + 0.5f, aBase, false, false, 1.32f);
                drawWonderWoman(c, w * 0.53f, groundY - px(58f), t + 1.0f, aBase, 1.28f);
                drawTheFlash(c, w * 0.63f, groundY - px(56f), t + 1.5f, aBase, 1.28f);
                break;
            case 11: // Scene 12: Justice League vs. Darkseid Omega Beams
                drawSupermanManOfSteel(c, w * 0.34f, groundY - px(58f), t, aBase, false, false, 1.34f);
                float dkX = w * 0.60f, dkY = groundY - px(62f);
                drawDarkseid(c, dkX, dkY, t, aBase, 1.38f);
                // High-Finesse Multi-Cornered Crimson Apokolips Omega Beams
                drawLightningStreak(c, dkX - px(6f), dkY - px(28f), w * 0.49f, dkY - px(38f),
                        t, 891, 0x00B80018, 0x00FF2428, 0x00FFFFAA, px(3.6f), aBase, false);
                drawLightningStreak(c, w * 0.49f, dkY - px(38f), w * 0.45f, dkY - px(2f),
                        t, 892, 0x00B80018, 0x00FF2428, 0x00FFFFAA, px(3.6f), aBase, false);
                drawLightningStreak(c, w * 0.45f, dkY - px(2f), w * 0.37f, dkY - px(14f),
                        t, 893, 0x00B80018, 0x00FF2428, 0x00FFFFAA, px(3.6f), aBase, true);
                break;
            case 12: // Doom of Krypton — Kal-El Starship Pod Launching from Exploding Red Sun
                paint.setColor((aBase * 3 / 4 << 24) | 0x00FF2818);
                c.drawCircle(w * 0.32f, groundY - px(24f), px(48f), paint);
                float podK = w * (0.34f + p * 0.28f), podY = groundY - px(36f + p * 64f);
                drawLightningStreak(c, w * 0.32f, groundY - px(28f), podK, podY,
                        t, 1201, 0x000088FF, 0x0068F4FF, 0x00FFFFFF, px(3.8f), aBase, true);
                paint.setColor((aBase << 24) | 0x00DCE8F8);
                c.drawOval(podK - px(12f), podY - px(6f), podK + px(12f), podY + px(6f), paint);
                break;
            case 13: // Fortress of Solitude — Superman First Flight Sonic Boom Over Arctic Ice Spires
                for (int sp = 0; sp < 5; sp++) {
                    float ix = w * (0.28f + sp * 0.08f);
                    paint.setColor((aBase * 2 / 3 << 24) | 0x0088D8FF);
                    actorPath.reset();
                    actorPath.moveTo(ix - px(14f), groundY);
                    actorPath.lineTo(ix, groundY - px(48f + (sp % 2) * 24f));
                    actorPath.lineTo(ix + px(14f), groundY);
                    actorPath.close();
                    c.drawPath(actorPath, paint);
                }
                drawSupermanManOfSteel(c, w * (0.32f + p * 0.28f), h * 0.74f, t, aBase, false, false, 1.36f);
                break;
            case 14: // Himalayan Glacier — Bruce Wayne vs. Ra's al Ghul Sword Duel
                drawBatmanDarkKnight(c, w * 0.36f, groundY - px(58f), t * 1.4f, aBase, false, 1.35f);
                drawBatmanDarkKnight(c, w * 0.56f, groundY - px(58f), t * 1.4f + 0.8f, aBase, false, 1.35f);
                drawLightningStreak(c, w * 0.41f, groundY - px(68f), w * 0.51f, groundY - px(68f),
                        t, 1203, 0x00FF7800, 0x00FFE848, 0x00FFFFFF, px(3.2f), aBase, true);
                break;
            case 15: // Lower Fifth Avenue — Batpod vs. The Joker Semi-Truck Flip
                float bpX = w * (0.28f + p * 0.24f);
                paint.setColor((aBase << 24) | 0x00202838);
                c.drawCircle(bpX - px(16f), groundY - px(8f), px(9f), paint);
                c.drawCircle(bpX + px(16f), groundY - px(8f), px(9f), paint);
                drawBatmanDarkKnight(c, bpX, groundY - px(42f), t, aBase, false, 1.15f);
                paint.setColor((aBase << 24) | 0x00483868);
                c.drawRoundRect(w * 0.54f, groundY - px(86f), w * 0.64f, groundY - px(12f), px(4f), px(4f), paint);
                break;
            case 16: // Gotham Stock Exchange — Batman vs. Bane Sewer Showdown
                drawBatmanDarkKnight(c, w * 0.36f, groundY - px(58f), t, aBase, false, 1.36f);
                drawDarkseid(c, w * 0.57f, groundY - px(60f), t + 0.8f, aBase, 1.32f);
                break;
            case 17: // Indian Ocean — Superman Flying Upward Through the World Engine Gravity Beam
                drawLightningStreak(c, w * 0.46f, h * 0.65f, w * 0.46f, groundY,
                        t, 1206, 0x000068FF, 0x0068E8FF, 0x00FFFFFF, px(5.2f), aBase, true);
                drawSupermanManOfSteel(c, w * 0.46f, groundY - px(38f + p * 54f), t, aBase, false, false, 1.36f);
                break;
            case 18: // Metropolis Ruins — Superman vs. General Zod Heat Vision Clash
                drawSupermanManOfSteel(c, w * 0.34f, groundY - px(60f), t, aBase, true, false, 1.36f);
                drawSupermanManOfSteel(c, w * 0.59f, groundY - px(60f), t + 0.8f, aBase, true, true, 1.36f);
                drawLightningStreak(c, w * 0.37f, groundY - px(84f), w * 0.56f, groundY - px(84f),
                        t, 1208, 0x00FF0000, 0x00FF7820, 0x00FFFFFF, px(4.4f), aBase, true);
                break;
            case 19: // No Man's Land — Wonder Woman Bracelets of Submission Deflecting Machine-Gun Fire
                drawWonderWoman(c, w * 0.38f, groundY - px(58f), t, aBase, 1.36f);
                for (int sh = 0; sh < 4; sh++) {
                    drawLightningStreak(c, w * 0.64f, groundY - px(54f + sh * 10f), w * 0.42f, groundY - px(66f),
                            t, 1210 + sh, 0x00FF8800, 0x00FFE848, 0x00FFFFFF, px(2.6f), aBase, false);
                }
                break;
            case 20: // Belgian Airfield — Wonder Woman Godkiller Lightning vs. Ares God of War
                drawWonderWoman(c, w * 0.36f, groundY - px(62f), t, aBase, 1.36f);
                drawDarkseid(c, w * 0.59f, groundY - px(60f), t + 1f, aBase, 1.34f);
                drawLightningStreak(c, w * 0.38f, groundY - px(72f), w * 0.57f, groundY - px(72f),
                        t, 1215, 0x00FF8800, 0x00FFE838, 0x00FFFFFF, px(4.4f), aBase, true);
                break;
            case 21: // STAR Labs Particle Accelerator — Barry Allen Struck by Golden Speed Force Bolt
                drawTheFlash(c, w * 0.46f, groundY - px(56f), t, aBase, 1.36f);
                drawLightningStreak(c, w * 0.46f, h * 0.64f, w * 0.46f, groundY - px(56f),
                        t, 1218, 0x00FF5800, 0x00FFE828, 0x00FFFFFF, px(4.8f), aBase, true);
                break;
            case 22: // Speed Force Tunnel — The Flash (Gold) vs. Reverse-Flash (Crimson) High-Speed Duel
                float rfX = w * (0.30f + p * 0.30f);
                drawLightningStreak(c, w * 0.25f, groundY - px(68f), rfX + px(16f), groundY - px(68f),
                        t, 1220, 0x00FF6800, 0x00FFE838, 0x00FFFFFF, px(4.2f), aBase, true);
                drawLightningStreak(c, w * 0.25f, groundY - px(48f), rfX + px(28f), groundY - px(48f),
                        t, 1221, 0x00B80018, 0x00FF1828, 0x00FFFFFF, px(4.2f), aBase, true);
                drawTheFlash(c, rfX, groundY - px(56f), t * 2.5f, aBase, 1.34f);
                drawTheFlash(c, rfX + px(28f), groundY - px(56f), t * 2.5f + 0.5f, aBase, 1.34f);
                break;
            case 23: // Hidden Sea — Aquaman Commanding the Colossal Karathen Sea Leviathan
                drawAquaman(c, w * 0.35f, groundY - px(58f), t, aBase, 1.36f);
                drawDragonCustom(c, w * 0.55f, h * 0.75f, t, aBase, 0xFF18A8C8, 1.34f);
                break;
            case 24: // Sicily Rooftops — Aquaman Trident Deflecting Black Manta's Crimson Optic Beam
                drawAquaman(c, w * 0.35f, groundY - px(58f), t, aBase, 1.36f);
                drawDarkseid(c, w * 0.59f, groundY - px(58f), t + 0.8f, aBase, 1.28f);
                drawLightningStreak(c, w * 0.57f, groundY - px(82f), w * 0.40f, groundY - px(68f),
                        t, 1225, 0x00D80018, 0x00FF2838, 0x00FFFFFF, px(4.2f), aBase, true);
                break;
            case 25: // Planet Oa Citadel — Central Power Battery & Green Lantern Corps
                paint.setColor((aBase * 3 / 4 << 24) | 0x0000C858);
                c.drawRoundRect(w * 0.44f, groundY - px(96f), w * 0.52f, groundY - px(16f), px(6f), px(6f), paint);
                drawLightningStreak(c, w * 0.48f, groundY - px(96f), w * 0.48f, h * 0.65f,
                        t, 1228, 0x0000A848, 0x0048FF88, 0x00FFFFFF, px(4.6f), aBase, true);
                drawGreenLantern(c, w * 0.34f, groundY - px(64f), t, aBase, 1.34f);
                break;
            case 26: // War of Light — Hal Jordan (Green Ring) vs. Sinestro (Yellow Ring) Clash
                drawGreenLantern(c, w * 0.34f, groundY - px(60f), t, aBase, 1.35f);
                drawGreenLantern(c, w * 0.58f, groundY - px(60f), t + 0.8f, aBase, 1.35f);
                drawLightningStreak(c, w * 0.38f, groundY - px(70f), w * 0.46f, groundY - px(70f),
                        t, 1230, 0x0000A848, 0x0038FF88, 0x00FFFFFF, px(4.0f), aBase, true);
                drawLightningStreak(c, w * 0.56f, groundY - px(70f), w * 0.46f, groundY - px(70f),
                        t, 1231, 0x00FF8800, 0x00FFE828, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 27: // Rock of Eternity — Shazam! Living Lightning Bolt Transformation
                drawSupermanManOfSteel(c, w * 0.46f, groundY - px(58f), t, aBase, false, false, 1.38f);
                drawLightningStreak(c, w * 0.46f, h * 0.64f, w * 0.46f, groundY - px(68f),
                        t, 1235, 0x00FF8800, 0x00FFE838, 0x00FFFFFF, px(5.0f), aBase, true);
                break;
            case 28: // Kahndaq Throne — Black Adam vs. Doctor Fate & Hawkman
                drawSupermanManOfSteel(c, w * 0.35f, groundY - px(60f), t, aBase, false, false, 1.36f);
                drawWonderWoman(c, w * 0.57f, groundY - px(64f), t + 0.8f, aBase, 1.34f);
                drawLightningStreak(c, w * 0.37f, groundY - px(72f), w * 0.55f, groundY - px(72f),
                        t, 1238, 0x00FF8800, 0x00FFE838, 0x00FFFFFF, px(4.2f), aBase, true);
                break;
            case 29: // Gotham Harbor — Batman, Superman & Wonder Woman Trinity vs. Doomsday
                drawBatmanDarkKnight(c, w * 0.28f, groundY - px(58f), t, aBase, true, 1.28f);
                drawSupermanManOfSteel(c, w * 0.39f, groundY - px(60f), t + 0.4f, aBase, true, false, 1.32f);
                drawWonderWoman(c, w * 0.49f, groundY - px(58f), t + 0.8f, aBase, 1.28f);
                drawDarkseid(c, w * 0.62f, groundY - px(62f), t + 1.2f, aBase, 1.40f);
                drawLightningStreak(c, w * 0.41f, groundY - px(84f), w * 0.59f, groundY - px(74f),
                        t, 1240, 0x00FF1800, 0x00FF7828, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 30: // STAR Labs Rooftop — Cyborg Boom Tube & Apokolips Plasma Cannon
                drawBatmanDarkKnight(c, w * 0.38f, groundY - px(58f), t, aBase, true, 1.35f);
                drawLightningStreak(c, w * 0.42f, groundY - px(68f), w * 0.64f, groundY - px(68f),
                        t, 1242, 0x000078FF, 0x0058F4FF, 0x00FFFFFF, px(4.2f), aBase, true);
                break;
            case 31: // Bludhaven Rooftops — Nightwing Dual Electric Blue Escrima Sticks
                drawBatmanDarkKnight(c, w * 0.45f, groundY - px(62f) - (float) Math.abs(Math.sin(t * 5f)) * px(16f), t, aBase, false, 1.34f);
                drawLightningStreak(c, w * 0.35f, groundY - px(74f), w * 0.56f, groundY - px(74f),
                        t, 1245, 0x000088FF, 0x0058F4FF, 0x00FFFFFF, px(3.6f), aBase, true);
                break;
            case 32: // Poznan Nuclear Plant — The Flash Running Faster Than Time (Zack Snyder's Justice League)
                float tfX = w * (0.62f - p * 0.32f);
                for (int gh = 1; gh <= 3; gh++) {
                    drawTheFlash(c, tfX + gh * px(24f), groundY - px(56f), t * 2.5f - gh * 0.2f, aBase * (4 - gh) / 6, 1.28f);
                }
                drawLightningStreak(c, w * 0.64f, groundY - px(64f), tfX - px(14f), groundY - px(64f),
                        t, 1248, 0x000078FF, 0x0068F4FF, 0x00FFFFFF, px(4.6f), aBase, true);
                drawLightningStreak(c, w * 0.64f, groundY - px(48f), tfX - px(10f), groundY - px(48f),
                        t, 1249, 0x000098FF, 0x0098F8FF, 0x00FFFFFF, px(3.6f), aBase, true);
                drawTheFlash(c, tfX, groundY - px(56f), t * 2.5f, aBase, 1.36f);
                break;
            case 33: // Solar Orbit — Black-Suit Superman Recharging in the Golden Sun Above Earth
                paint.setColor((aBase / 3 << 24) | 0x00FFB828);
                c.drawCircle(w * 0.58f, h * 0.72f, px(44f), paint);
                paint.setColor((aBase << 24) | 0x00FFE868);
                c.drawCircle(w * 0.58f, h * 0.72f, px(22f), paint);
                drawSupermanManOfSteel(c, w * 0.42f, h * 0.75f, t, aBase, false, false, 1.38f);
                break;
            case 34: // Neo-Gotham 2039 — Batman Beyond (Terry McGinnis) Crimson Wings & Rocket Boots
                float bbX = w * (0.28f + p * 0.34f), bbY = h * 0.75f + (float) Math.sin(t * 5f) * px(10f);
                drawLightningStreak(c, bbX - px(48f), bbY + px(28f), bbX - px(8f), bbY + px(28f),
                        t, 1252, 0x00FF1828, 0x00FF6838, 0x00FFFFFF, px(3.6f), aBase, false);
                drawBatmanDarkKnight(c, bbX, bbY, t, aBase, false, 1.35f);
                break;
            case 35: // Scene 36: Watchtower Orbit — The Justice League Seven Looking Over Planet Earth
                paint.setColor((aBase / 3 << 24) | 0x002888FF);
                c.drawArc(rectF(w * 0.24f, groundY - px(24f), w * 0.68f, groundY + px(64f)), 180, 180, true, paint);
                drawBatmanDarkKnight(c, w * 0.28f, groundY - px(58f), t, aBase, false, 1.24f);
                drawSupermanManOfSteel(c, w * 0.37f, groundY - px(62f), t + 0.3f, aBase, false, false, 1.28f);
                drawWonderWoman(c, w * 0.46f, groundY - px(58f), t + 0.6f, aBase, 1.24f);
                drawTheFlash(c, w * 0.55f, groundY - px(56f), t + 0.9f, aBase, 1.24f);
                drawAquaman(c, w * 0.63f, groundY - px(58f), t + 1.2f, aBase, 1.24f);
                break;
            case 36: // Scene 37: Wayne Manor Batcave — Batman at the Batcomputer & T-Rex Trophy
                paint.setColor((aBase * 3 / 4 << 24) | 0x002898FF);
                c.drawRoundRect(w * 0.48f, groundY - px(96f), w * 0.62f, groundY - px(42f), px(4f), px(4f), paint);
                drawBatmanDarkKnight(c, w * 0.38f, groundY - px(58f), t, aBase, false, 1.36f);
                break;
            case 37: // Scene 38: Daily Planet Globe — Superman Catching the Falling Helicopter
                paint.setColor((aBase << 24) | 0x00FFD848);
                c.drawCircle(w * 0.32f, groundY - px(24f), px(22f), paint);
                drawSupermanManOfSteel(c, w * 0.48f, h * 0.74f, t, aBase, false, false, 1.36f);
                paint.setColor((aBase << 24) | 0x00DCE4F0);
                c.drawRoundRect(w * 0.44f, h * 0.67f, w * 0.54f, h * 0.71f, px(4f), px(4f), paint);
                break;
            case 38: // Scene 39: Themyscira Beach — Wonder Woman Amazonian Shield Wall
                for (int am = 0; am < 4; am++) {
                    drawWonderWoman(c, w * (0.30f + am * 0.09f), groundY - px(58f), t + am * 0.4f, aBase, 1.28f);
                }
                break;
            case 39: // Scene 40: Atlantis Throne — King Orm & Aquaman Underwater Trident Duel
                drawAquaman(c, w * 0.35f, groundY - px(58f), t * 1.4f, aBase, 1.36f);
                drawAquaman(c, w * 0.58f, groundY - px(58f), t * 1.4f + 0.8f, aBase, 1.36f);
                drawLightningStreak(c, w * 0.40f, groundY - px(68f), w * 0.54f, groundY - px(68f),
                        t, 1260, 0x00FF8800, 0x00FFE838, 0x00FFFFFF, px(3.8f), aBase, true);
                break;
            case 40: // Scene 41: Crime Alley Rain — The Batman Red Flare Walk Through Gotham Flood
                drawBatmanDarkKnight(c, w * (0.34f + p * 0.20f), groundY - px(58f), t, aBase, false, 1.36f);
                float flrX = w * (0.34f + p * 0.20f) + px(22f), flrY = groundY - px(66f);
                paint.setColor((aBase / 2 << 24) | 0x00FF1824);
                c.drawCircle(flrX, flrY, px(32f + 6f * (float) Math.sin(t * 18f)), paint);
                paint.setColor((aBase << 24) | 0x00FFFFFF);
                c.drawCircle(flrX, flrY, px(5.5f), paint);
                break;
            case 41: // Scene 42: Apokolips Citadel — Superman vs. Steppenwolf Electro-Axe
                drawSupermanManOfSteel(c, w * 0.35f, groundY - px(62f), t, aBase, true, false, 1.36f);
                drawDarkseid(c, w * 0.58f, groundY - px(60f), t + 0.8f, aBase, 1.36f);
                drawLightningStreak(c, w * 0.38f, groundY - px(84f), w * 0.56f, groundY - px(72f),
                        t, 1262, 0x00FF1800, 0x00FF7828, 0x00FFFFFF, px(4.0f), aBase, true);
                break;
            case 42: // Scene 43: Tower of Fate — Doctor Fate Golden Ankh Helmet of Nabu
                drawSupermanManOfSteel(c, w * 0.46f, groundY - px(66f) + (float) Math.sin(t * 4f) * px(8f), t, aBase, false, false, 1.36f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(3.6f));
                paint.setColor((aBase << 24) | 0x00FFE048);
                c.drawCircle(w * 0.46f, groundY - px(96f), px(18f), paint);
                c.drawLine(w * 0.46f, groundY - px(78f), w * 0.46f, groundY - px(42f), paint);
                c.drawLine(w * 0.42f, groundY - px(64f), w * 0.50f, groundY - px(64f), paint);
                paint.setStyle(Paint.Style.FILL);
                break;
            default: // Scene 44: Kingdom Come — Elder Superman & Armored Batman Over Metropolis
                drawSupermanManOfSteel(c, w * 0.39f, groundY - px(62f), t, aBase, false, false, 1.38f);
                drawBatmanDarkKnight(c, w * 0.53f, groundY - px(58f), t + 0.6f, aBase, true, 1.38f);
                break;
        }
        c.restore();
    }

    /**
     * Bespoke DCU Batman (The Dark Knight) Renderer:
     * - Tall pointed Bat-ears on cowl + glowing white detective eyes
     * - Scalloped bat-wing cape billowing behind him
     * - Slate-grey Kevlar Bat-suit, golden Bat-symbol oval on chest & golden Utility Belt
     */
    private void drawBatmanDarkKnight(Canvas c, float x, float y, float t, int alpha, boolean armored, float s) {
        float capeWave = (float) Math.sin(t * 7f) * px(7f * s);
        // 1. Scalloped Bat-Wing Cape
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x000E121C);
        actorPath.reset();
        actorPath.moveTo(x - px(8f * s), y - px(19f * s));
        actorPath.lineTo(x + px(8f * s), y - px(19f * s));
        actorPath.lineTo(x + px(12f * s), y + px(30f * s));
        actorPath.lineTo(x - px(4f * s), y + px(24f * s));
        actorPath.lineTo(x - px(14f * s) - capeWave * 0.5f, y + px(32f * s));
        actorPath.lineTo(x - px(22f * s) - capeWave, y + px(24f * s));
        actorPath.lineTo(x - px(32f * s) - capeWave, y + px(31f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 2. Dark Boots & Kevlar Legs
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(5.2f * s));
        paint.setColor((alpha << 24) | (armored ? 0x00404C5E : 0x00262E3E));
        c.drawLine(x - px(4f * s), y + px(6f * s), x - px(9f * s), y + px(34f * s), paint);
        c.drawLine(x + px(4f * s), y + px(6f * s), x + px(9f * s), y + px(34f * s), paint);

        // 3. Armored Bat-Torso, Chest Bat-Symbol & Golden Utility Belt
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | (armored ? 0x0048566A : 0x002C3446));
        c.drawRoundRect(x - px(9f * s), y - px(19f * s), x + px(9f * s), y + px(8f * s), px(3f * s), px(3f * s), paint);
        // Golden Oval Chest Bat-Emblem
        paint.setColor((alpha << 24) | 0x00FFE048);
        c.drawOval(x - px(5.5f * s), y - px(14f * s), x + px(5.5f * s), y - px(8.5f * s), paint);
        paint.setColor((alpha << 24) | 0x0010141C);
        c.drawOval(x - px(4.0f * s), y - px(12.8f * s), x + px(4.0f * s), y - px(9.7f * s), paint);
        // Golden Utility Belt
        paint.setColor((alpha << 24) | 0x00FFD838);
        c.drawRect(x - px(8.8f * s), y + px(2f * s), x + px(8.8f * s), y + px(6f * s), paint);

        // 4. Bat-Cowl with Tall Pointed Bat-Ears & Glowing White Eyes
        float headX = x + px(1f * s), headY = y - px(26.5f * s);
        paint.setColor((alpha << 24) | (armored ? 0x00404C5E : 0x00141822));
        c.drawCircle(headX, headY, px(6.5f * s), paint);
        actorPath.reset();
        actorPath.moveTo(headX - px(6f * s), headY - px(3f * s));
        actorPath.lineTo(headX - px(5.5f * s), headY - px(15.5f * s));
        actorPath.lineTo(headX - px(1.5f * s), headY - px(5f * s));
        actorPath.moveTo(headX + px(1.5f * s), headY - px(5f * s));
        actorPath.lineTo(headX + px(5.5f * s), headY - px(15.5f * s));
        actorPath.lineTo(headX + px(6f * s), headY - px(3f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Glowing White / Cyan Detective Eyes
        paint.setColor((alpha << 24) | (armored ? 0x0078F8FF : 0x00FFFFFF));
        c.drawRect(headX - px(3.8f * s), headY - px(2.0f * s), headX - px(0.8f * s), headY - px(0.3f * s), paint);
        c.drawRect(headX + px(0.8f * s), headY - px(2.0f * s), headX + px(3.8f * s), headY - px(0.3f * s), paint);

        // Green Kryptonite Spear in Armored Mode
        if (armored) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.4f * s));
            paint.setColor((alpha << 24) | 0x0090A0B0);
            c.drawLine(x + px(14f * s), y + px(28f * s), x + px(18f * s), y - px(24f * s), paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor((alpha << 24) | 0x0038FF78);
            c.drawCircle(x + px(18f * s), y - px(26f * s), px(4.5f * s), paint);
        }
    }

    /**
     * Bespoke DCU Superman (Man of Steel) Renderer:
     * - Royal-blue Kryptonian suit, red boots/trunks, sweeping crimson cape
     * - Pentagonal Red & Gold House of El 'S' Shield on chest + Heat Vision eyes
     */
    private void drawSupermanManOfSteel(Canvas c, float x, float y, float t, int alpha,
                                        boolean heatVision, boolean facingLeft, float s) {
        float dir = facingLeft ? -1f : 1f;
        float capeWave = (float) Math.sin(t * 7f) * px(7f * s);
        // 1. Flowing Crimson Cape
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00D81C24);
        actorPath.reset();
        actorPath.moveTo(x - dir * px(8f * s), y - px(19f * s));
        actorPath.lineTo(x + dir * px(8f * s), y - px(19f * s));
        actorPath.lineTo(x - dir * px(4f * s), y + px(32f * s));
        actorPath.lineTo(x - dir * px(28f * s) - dir * capeWave, y + px(30f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 2. Blue Kryptonian Legs & Red Boots
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(5.2f * s));
        paint.setColor((alpha << 24) | 0x001850B8);
        c.drawLine(x - dir * px(4f * s), y + px(6f * s), x - dir * px(8f * s), y + px(22f * s), paint);
        c.drawLine(x + dir * px(4f * s), y + px(6f * s), x + dir * px(8f * s), y + px(22f * s), paint);
        paint.setColor((alpha << 24) | 0x00D81E28);
        c.drawLine(x - dir * px(8f * s), y + px(22f * s), x - dir * px(9f * s), y + px(34f * s), paint);
        c.drawLine(x + dir * px(8f * s), y + px(22f * s), x + dir * px(9f * s), y + px(34f * s), paint);

        // 3. Broad Blue Torso, Red Belt & Pentagonal Gold/Red 'S' Shield
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x001850B8);
        c.drawRoundRect(x - px(9f * s), y - px(19f * s), x + px(9f * s), y + px(8f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | 0x00D81E28);
        c.drawRect(x - px(8.8f * s), y + px(3f * s), x + px(8.8f * s), y + px(7.5f * s), paint);
        // Pentagonal 'S' Diamond Shield on Chest
        paint.setColor((alpha << 24) | 0x00FFE038);
        actorPath.reset();
        actorPath.moveTo(x - px(5.5f * s), y - px(15f * s));
        actorPath.lineTo(x + px(5.5f * s), y - px(15f * s));
        actorPath.lineTo(x + px(6.5f * s), y - px(11f * s));
        actorPath.lineTo(x, y - px(5.5f * s));
        actorPath.lineTo(x - px(6.5f * s), y - px(11f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        paint.setColor((alpha << 24) | 0x00E81E28);
        c.drawCircle(x, y - px(11f * s), px(2.8f * s), paint);

        // 4. Head, Dark Hair & Glowing Crimson Heat Vision Eyes
        float headX = x + dir * px(1f * s), headY = y - px(26.5f * s);
        paint.setColor((alpha << 24) | 0x00F4D0B4);
        c.drawCircle(headX, headY, px(6.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00181C24);
        c.drawArc(rectF(headX - px(7f * s), headY - px(8f * s), headX + px(7f * s), headY + px(1f * s)), 165, 210, true, paint);
        if (heatVision) {
            paint.setColor((alpha << 24) | 0x00FF2424);
            c.drawCircle(headX + dir * px(2.2f * s), headY - px(1.0f * s), px(2.2f * s), paint);
        }
    }

    /**
     * Bespoke DCU Wonder Woman (Diana of Themyscira) Renderer:
     * - Long dark hair, Golden Tiara with Red Star
     * - Crimson corset with Golden Eagle W breastplate & Blue Amazonian battle skirt
     */
    private void drawWonderWoman(Canvas c, float x, float y, float t, int alpha, float s) {
        float stride = (float) Math.sin(t * 6.5f) * px(5f * s);
        // Long Flowing Dark Hair
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x0018141C);
        c.drawOval(x - px(11f * s), y - px(28f * s), x + px(4f * s), y - px(8f * s), paint);

        // Red & Gold Boots
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(4.5f * s));
        paint.setColor((alpha << 24) | 0x00C81E28);
        c.drawLine(x - px(3.5f * s), y + px(8f * s), x - px(8f * s) - stride, y + px(34f * s), paint);
        c.drawLine(x + px(3.5f * s), y + px(8f * s), x + px(8f * s) + stride, y + px(34f * s), paint);

        // Crimson Armor Corset, Golden Eagle W & Blue Gladiator Skirt
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00D81E28);
        c.drawRoundRect(x - px(7.5f * s), y - px(18f * s), x + px(7.5f * s), y + px(2f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFD838);
        c.drawRect(x - px(6.5f * s), y - px(15f * s), x + px(6.5f * s), y - px(11.5f * s), paint);
        paint.setColor((alpha << 24) | 0x001C48A8);
        c.drawRoundRect(x - px(8.5f * s), y - px(1f * s), x + px(8.5f * s), y + px(11f * s), px(2f * s), px(2f * s), paint);

        // Head & Golden Tiara
        float headX = x + px(1f * s), headY = y - px(26f * s);
        paint.setColor((alpha << 24) | 0x00F4D0B4);
        c.drawCircle(headX, headY, px(6.2f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFE048);
        c.drawRect(headX - px(5.5f * s), headY - px(5.5f * s), headX + px(5.5f * s), headY - px(2.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00E82028);
        c.drawCircle(headX + px(1f * s), headY - px(4.0f * s), px(1.5f * s), paint);
    }

    /**
     * Bespoke DCU The Flash (Barry Allen) Sprinting Speedster Renderer:
     * - Dynamic forward-leaning sprinter posture with high-knee drive & pumping elbows
     * - Crimson suit, Golden Boots, Golden Lightning Belt, White Circle Chest Emblem with Jagged Bolt & Golden Ear-Wings
     */
    private void drawTheFlash(Canvas c, float x, float y, float t, int alpha, float s) {
        float stride = (float) Math.sin(t * 9f) * px(8.5f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(4.8f * s));
        // Rear & Front Sprinting Thighs (Crimson)
        paint.setColor((alpha << 24) | 0x00E81C24);
        float k1X = x - px(9f * s) - stride * 0.5f, k1Y = y + px(19f * s);
        float k2X = x + px(11f * s) + stride * 0.6f, k2Y = y + px(16f * s);
        c.drawLine(x - px(2f * s), y + px(6f * s), k1X, k1Y, paint);
        c.drawLine(x + px(3f * s), y + px(6f * s), k2X, k2Y, paint);
        // Golden Speedster Boots (Calves)
        paint.setColor((alpha << 24) | 0x00FFD838);
        c.drawLine(k1X, k1Y, k1X - px(6f * s) - stride * 0.4f, y + px(33f * s), paint);
        c.drawLine(k2X, k2Y, k2X - px(3f * s) + stride * 0.3f, y + px(32f * s), paint);

        // Forward-leaning Crimson Torso & Golden Lightning Belt
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00E81C24);
        actorPath.reset();
        actorPath.moveTo(x - px(5f * s), y - px(18f * s));
        actorPath.lineTo(x + px(10f * s), y - px(18f * s));
        actorPath.lineTo(x + px(6f * s), y + px(7f * s));
        actorPath.lineTo(x - px(8f * s), y + px(7f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Golden V-Shaped Lightning Belt
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.2f * s));
        paint.setColor((alpha << 24) | 0x00FFE038);
        c.drawLine(x - px(7f * s), y + px(3f * s), x + px(1f * s), y + px(6.5f * s), paint);
        c.drawLine(x + px(1f * s), y + px(6.5f * s), x + px(7f * s), y + px(3f * s), paint);

        // White Chest Circle & Jagged Golden Lightning Bolt Emblem
        paint.setStyle(Paint.Style.FILL);
        float cx = x + px(3.0f * s), cy = y - px(10f * s);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(cx, cy, px(4.2f * s), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(1.8f * s));
        paint.setColor((alpha << 24) | 0x00FFC818);
        c.drawLine(cx + px(2.5f * s), cy - px(4.5f * s), cx - px(1.0f * s), cy, paint);
        c.drawLine(cx - px(1.0f * s), cy, cx + px(1.5f * s), cy, paint);
        c.drawLine(cx + px(1.5f * s), cy, cx - px(2.5f * s), cy + px(4.5f * s), paint);

        // Pumping Sprinter Arms
        paint.setStrokeWidth(px(4.0f * s));
        paint.setColor((alpha << 24) | 0x00E81C24);
        c.drawLine(x + px(5f * s), y - px(14f * s), x + px(16f * s) + stride * 0.4f, y - px(6f * s), paint);
        c.drawLine(x - px(3f * s), y - px(14f * s), x - px(14f * s) - stride * 0.4f, y - px(4f * s), paint);

        // Red Cowl, Exposed Chin & Iconic Golden Ear-Wing Lightning Bolts
        paint.setStyle(Paint.Style.FILL);
        float headX = x + px(5.5f * s), headY = y - px(25.0f * s);
        paint.setColor((alpha << 24) | 0x00F4D0B4);
        c.drawCircle(headX, headY, px(6.4f * s), paint);
        paint.setColor((alpha << 24) | 0x00E81C24);
        c.drawArc(rectF(headX - px(6.8f * s), headY - px(7.2f * s), headX + px(6.8f * s), headY + px(3.5f * s)), 145, 235, true, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.4f * s));
        paint.setColor((alpha << 24) | 0x00FFE038);
        c.drawLine(headX - px(1f * s), headY - px(1.5f * s), headX - px(10f * s), headY - px(6.5f * s), paint);
        c.drawLine(headX - px(5f * s), headY - px(4.0f * s), headX - px(12f * s), headY - px(3.0f * s), paint);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Bespoke DCU Aquaman (Arthur Curry) Renderer with Golden Scale-Mail, Green Greaves & 5-Pronged Golden Trident!
     */
    private void drawAquaman(Canvas c, float x, float y, float t, int alpha, float s) {
        // Emerald Green Legs
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(5.0f * s));
        paint.setColor((alpha << 24) | 0x00189858);
        c.drawLine(x - px(4f * s), y + px(6f * s), x - px(9f * s), y + px(34f * s), paint);
        c.drawLine(x + px(4f * s), y + px(6f * s), x + px(9f * s), y + px(34f * s), paint);
        // Golden Scale-Mail Torso
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00FF9818);
        c.drawRoundRect(x - px(8.5f * s), y - px(19f * s), x + px(8.5f * s), y + px(8f * s), px(3f * s), px(3f * s), paint);
        // Head & Flowing Hair/Beard
        float headX = x + px(1f * s), headY = y - px(26.5f * s);
        paint.setColor((alpha << 24) | 0x00F4D0B4);
        c.drawCircle(headX, headY, px(6.4f * s), paint);
        paint.setColor((alpha << 24) | 0x00986830);
        c.drawArc(rectF(headX - px(8f * s), headY - px(8.5f * s), headX + px(7f * s), headY + px(6f * s)), 110, 250, true, paint);
        // 5-Pronged Golden Trident of Atlan
        float tx = x + px(18f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.8f * s));
        paint.setColor((alpha << 24) | 0x00FFE048);
        c.drawLine(tx, y + px(34f * s), tx, y - px(32f * s), paint);
        c.drawLine(tx - px(6f * s), y - px(24f * s), tx + px(6f * s), y - px(24f * s), paint);
        for (int pr = -1; pr <= 1; pr++) {
            c.drawLine(tx + pr * px(5.5f * s), y - px(24f * s), tx + pr * px(5.5f * s), y - px(35f * s), paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Bespoke DCU Green Lantern (Hal Jordan) Renderer with Emerald & Black Suit, Chest Lantern Badge & Glowing Ring
     */
    private void drawGreenLantern(Canvas c, float x, float y, float t, int alpha, float s) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(4.8f * s));
        paint.setColor((alpha << 24) | 0x0014181E);
        c.drawLine(x - px(4f * s), y + px(6f * s), x - px(8f * s), y + px(34f * s), paint);
        c.drawLine(x + px(4f * s), y + px(6f * s), x + px(8f * s), y + px(34f * s), paint);
        // Emerald Green Torso & White/Green Lantern Chest Badge
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x0018B858);
        c.drawRoundRect(x - px(8f * s), y - px(18f * s), x + px(8f * s), y + px(7f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(x + px(1f * s), y - px(10f * s), px(3.8f * s), paint);
        paint.setColor((alpha << 24) | 0x0028FF78);
        c.drawCircle(x + px(1f * s), y - px(10f * s), px(2.2f * s), paint);
        // Head
        paint.setColor((alpha << 24) | 0x00F4D0B4);
        c.drawCircle(x + px(1f * s), y - px(26f * s), px(6.2f * s), paint);
    }

    /**
     * Bespoke DCU Darkseid (Lord of Apokolips) Renderer with Massive Stone-Grey Frame, Omega Chest Emblem & Red Eyes
     */
    private void drawDarkseid(Canvas c, float x, float y, float t, int alpha, float s) {
        paint.setStyle(Paint.Style.FILL);
        // Massive Apokolips Armor Torso
        paint.setColor((alpha << 24) | 0x00283448);
        c.drawRoundRect(x - px(12f * s), y - px(22f * s), x + px(12f * s), y + px(14f * s), px(4f * s), px(4f * s), paint);
        // Glowing Red Omega Symbol on Chest
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.4f * s));
        paint.setColor((alpha << 24) | 0x00FF2828);
        c.drawArc(rectF(x - px(5.5f * s), y - px(15f * s), x + px(5.5f * s), y - px(4f * s)), 140, 260, false, paint);
        // Stone-Grey New God Head & Blazing Crimson Eyes
        paint.setStyle(Paint.Style.FILL);
        float headX = x - px(1f * s), headY = y - px(30f * s);
        paint.setColor((alpha << 24) | 0x00545868);
        c.drawRoundRect(headX - px(7.5f * s), headY - px(7.5f * s), headX + px(7.5f * s), headY + px(7.5f * s), px(2.5f * s), px(2.5f * s), paint);
        paint.setColor((alpha << 24) | 0x00FF2020);
        c.drawCircle(headX - px(2.5f * s), headY - px(1.0f * s), px(2.0f * s), paint);
    }

    /**
     * THEME 7: FORMULA 1 — 44-Scene Grand Prix Story Loop (Iconic Tracks, Team-Accurate F1 Cars & Podium Trophy Lifts)
     */
    private void drawF1StoryLoop(Canvas c, int w, int h, float t) {
        int count = F1_TITLES.length;
        float totalCycle = CHAPTER_SEC * count;
        float cycleTime = t % totalCycle;
        int scene = resolveActiveScene(cycleTime, count);
        float p = (cycleTime % CHAPTER_SEC) / CHAPTER_SEC;
        float fade = resolveStoryFade(p);
        int aBase = Math.min(255, Math.max(0, Math.round(fade * 255f)));
        float groundY = h * 0.900f;

        drawStoryCaptionAndDots(c, w, h, F1_TITLES[scene], scene, count, aBase,
                0x00FFF2E8, 0xEEFF1801, 0x44FF5252, px(7.8f));

        c.save();
        applyStageTransform(c, w, h, groundY);

        // Team Livery Palettes: (Body RGB, Accent RGB, Pirelli Sidewall RGB)
        final int FERRARI_RED = 0x00FF1801, FERRARI_ACC = 0x00FFF200;
        final int REDBULL_NAVY = 0x001B3C98, REDBULL_ACC = 0x00FFD800;
        final int MCLAREN_PAPAYA = 0x00FF8000, MCLAREN_ACC = 0x0018C8FF;
        final int MERC_SILVER = 0x00C8D6E6, MERC_PETRONAS = 0x0000F5D4;
        final int ASTON_GREEN = 0x0000A878, ASTON_LIME = 0x00D4FF00;
        final int MP4_WHITE = 0x00F4F6FA, MP4_RED = 0x00FF1801;
        final int LOTUS_BLACK = 0x001E2028, LOTUS_GOLD = 0x00FFD700;
        final int RENAULT_BLUE = 0x0000A8FF, RENAULT_YELLOW = 0x00FFE600;
        final int WILLIAMS_BLUE = 0x001E68F0, WILLIAMS_GOLD = 0x00FFD000;
        final int BRAWN_WHITE = 0x00F4F8FA, BRAWN_NEON = 0x00D8FF00;

        final int TIRE_SOFT = 0x00FF1801;
        final int TIRE_MED = 0x00FFD800;
        final int TIRE_HARD = 0x00F4F6FA;
        final int TIRE_WET = 0x000098FF;

        // Render the faded Constructor / Team Name watermark in the bottom area behind the cars & track
        drawF1TeamBackdrop(c, w, groundY, aBase, scene);

        switch (scene) {
            case 0: // Scene 1: Lights Out — Five Red Lights at Bahrain Night GP
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                drawF1StartGantry(c, w * 0.45f, groundY - px(88f), p, aBase);
                float launch = p < 0.45f ? 0f : (p - 0.45f) / 0.55f;
                float accel = launch * launch * w * 0.30f;
                drawF1Car(c, w * 0.30f + accel, groundY - px(14f), t * 2.2f, aBase,
                        REDBULL_NAVY, REDBULL_ACC, TIRE_SOFT, false, launch > 0.05f, false, 1.24f);
                drawF1Car(c, w * 0.42f + accel * 1.06f, groundY - px(8f), t * 2.2f + 0.4f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, false, launch > 0.05f, false, 1.28f);
                break;

            case 1: // Scene 2: Monaco Grand Prix — Fairmont Hairpin & Tunnel Blast
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                // Monaco Tunnel Concrete Archway on Left
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(6f));
                paint.setColor((aBase * 3 / 4 << 24) | 0x00606C80);
                c.drawArc(rectF(w * 0.24f, groundY - px(84f), w * 0.38f, groundY + px(12f)), 180, 180, false, paint);
                paint.setStyle(Paint.Style.FILL);
                float monX = w * (0.28f + p * 0.32f);
                drawF1Car(c, monX, groundY - px(10f), t * 2.5f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, true, true, false, 1.32f);
                drawF1Car(c, monX - px(92f), groundY - px(14f), t * 2.5f + 0.5f, aBase,
                        MCLAREN_PAPAYA, MCLAREN_ACC, TIRE_MED, false, true, false, 1.24f);
                break;

            case 2: // Scene 3: Monza Temple of Speed — Scuderia Ferrari Tifosi Podium Trophy Lift
                drawF1PodiumCeremony(c, w * 0.45f, groundY, t, aBase,
                        FERRARI_RED, FERRARI_ACC, MCLAREN_PAPAYA, MERC_PETRONAS, true);
                break;

            case 3: // Scene 4: Spa-Francorchamps — Eau Rouge & Raidillon Full-Throttle Uphill Climb
                // Steep Raidillon elevation hill with Red & Yellow Belgian kerbs
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(5f));
                paint.setColor((aBase << 24) | 0x00FF1801);
                c.drawLine(w * 0.24f, groundY - px(4f), w * 0.66f, groundY - px(52f), paint);
                paint.setColor((aBase << 24) | 0x00FFD800);
                c.drawLine(w * 0.24f, groundY - px(9f), w * 0.66f, groundY - px(57f), paint);
                paint.setStyle(Paint.Style.FILL);
                float spaX = w * (0.28f + p * 0.30f);
                float spaY = groundY - px(12f + p * 38f);
                c.save();
                c.rotate(-6.5f, spaX, spaY);
                drawF1Car(c, spaX, spaY, t * 2.6f, aBase,
                        REDBULL_NAVY, REDBULL_ACC, TIRE_SOFT, true, true, false, 1.30f);
                drawF1Car(c, spaX - px(88f), spaY + px(2f), t * 2.6f + 0.6f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, true, true, false, 1.24f);
                c.restore();
                break;

            case 4: // Scene 5: Silverstone Circuit — Maggotts & Becketts High-G Apex
                drawF1TrackBase(c, w, groundY, t, aBase, 0x001E2430, 0x00FFFFFF, false);
                float silvX = w * (0.26f + p * 0.34f);
                float weave = (float) Math.sin(t * 6.5f) * px(8f);
                drawF1Car(c, silvX, groundY - px(10f) + weave, t * 2.5f, aBase,
                        MERC_SILVER, MERC_PETRONAS, TIRE_HARD, true, true, false, 1.30f);
                drawF1Car(c, silvX - px(86f), groundY - px(12f) - weave, t * 2.5f + 0.5f, aBase,
                        MCLAREN_PAPAYA, MCLAREN_ACC, TIRE_MED, true, true, false, 1.26f);
                break;

            case 5: // Scene 6: Suzuka Circuit — 130R & Figure-Eight Crossover Bridge Duel
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                // Suzuka Figure-8 Overpass Bridge
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase * 4 / 5 << 24) | 0x00263042);
                c.drawRect(w * 0.28f, groundY - px(62f), w * 0.62f, groundY - px(52f), paint);
                c.drawRect(w * 0.33f, groundY - px(52f), w * 0.36f, groundY, paint);
                c.drawRect(w * 0.54f, groundY - px(52f), w * 0.57f, groundY, paint);
                // Upper car crossing the bridge left-to-right, lower car blasting underneath right-to-left
                drawF1Car(c, w * (0.30f + p * 0.28f), groundY - px(60f), t * 2.4f, aBase,
                        REDBULL_NAVY, REDBULL_ACC, TIRE_SOFT, true, true, false, 1.12f);
                drawF1Car(c, w * (0.58f - p * 0.26f), groundY - px(10f), t * 2.4f + 0.7f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, false, true, true, 1.30f);
                break;

            case 6: // Scene 7: Interlagos Brazil — Ayrton Senna Home Trophy Lift & Brazilian Flag
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00009B3A, 0x00FEDF00, false);
                drawF1Car(c, w * 0.34f, groundY - px(10f), t, aBase,
                        MP4_WHITE, MP4_RED, TIRE_MED, false, false, false, 1.26f);
                drawF1DriverTrophyLift(c, w * 0.54f, groundY - px(56f), t, aBase,
                        0x00FF2400, 0x00FEDF00, 0x00FFD700, false, true, 0x00009B3A, 1.42f);
                break;

            case 7: // Scene 8: Marina Bay Singapore — Floodlit Street Circuit Titanium Sparks
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                // Singapore Flyer illuminated wheel in background
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(px(2.2f));
                paint.setColor((aBase * 2 / 3 << 24) | 0x0000D2BE);
                c.drawCircle(w * 0.58f, groundY - px(68f), px(34f), paint);
                paint.setStyle(Paint.Style.FILL);
                float sinX = w * (0.28f + p * 0.32f);
                drawF1Car(c, sinX, groundY - px(10f), t * 2.8f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, true, true, false, 1.34f);
                drawF1Car(c, sinX - px(95f), groundY - px(12f), t * 2.8f + 0.5f, aBase,
                        MERC_SILVER, MERC_PETRONAS, TIRE_MED, true, true, false, 1.26f);
                break;

            case 8: // Scene 9: Las Vegas Strip — Neon Sphere Straightaway DRS Overtake
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                // Glowing Las Vegas Sphere Dome on Horizon
                paint.setStyle(Paint.Style.FILL);
                paint.setColor((aBase / 2 << 24) | 0x00FF1801);
                c.drawArc(rectF(w * 0.40f, groundY - px(88f), w * 0.54f, groundY + px(20f)), 180, 180, true, paint);
                paint.setColor((aBase * 3 / 4 << 24) | 0x00FFD800);
                c.drawArc(rectF(w * 0.42f, groundY - px(72f), w * 0.52f, groundY + px(14f)), 180, 180, true, paint);
                float vegasLead = w * (0.26f + p * 0.36f);
                drawF1Car(c, vegasLead - px(36f), groundY - px(14f), t * 2.6f, aBase,
                        FERRARI_RED, 0x00FFFFFF, TIRE_HARD, false, true, false, 1.24f);
                drawF1Car(c, vegasLead + px(24f), groundY - px(9f), t * 2.8f, aBase,
                        REDBULL_NAVY, REDBULL_ACC, TIRE_MED, true, true, false, 1.32f);
                break;

            case 9: // Scene 10: Red Bull Ring — 1.8-Second World Record Pit Stop Crew
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                drawF1PitStopCrew(c, w * 0.45f, groundY, t, p, aBase, REDBULL_NAVY, REDBULL_ACC);
                break;

            case 10: // Scene 11: Yas Marina Abu Dhabi — World Championship Donuts & Fireworks
                drawF1TrackBase(c, w, groundY, t, aBase, 0x0000D2BE, 0x00FFFFFF, false);
                drawF1DonutCelebration(c, w * 0.45f, groundY, t, aBase, REDBULL_NAVY, REDBULL_ACC);
                break;

            case 11: // Scene 12: Podium Ceremony — Champagne Spray & Gold Trophy Lift
                drawF1PodiumCeremony(c, w * 0.45f, groundY, t, aBase,
                        REDBULL_NAVY, REDBULL_ACC, FERRARI_RED, MCLAREN_PAPAYA, true);
                break;

            case 12: // Scene 13: Monaco Swimming Pool — Senna McLaren MP4/4 Qualifying Lap
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                drawF1Car(c, w * (0.28f + p * 0.34f), groundY - px(10f), t * 2.8f, aBase,
                        MP4_WHITE, MP4_RED, TIRE_MED, false, true, false, 1.36f);
                break;

            case 13: // Scene 14: Silverstone Finale — Hamilton Union Jack & British GP Trophy Lift
                drawF1TrackBase(c, w, groundY, t, aBase, 0x001E2430, 0x00FFFFFF, false);
                drawF1Car(c, w * 0.33f, groundY - px(10f), t, aBase,
                        MERC_SILVER, MERC_PETRONAS, TIRE_SOFT, false, false, false, 1.26f);
                drawF1DriverTrophyLift(c, w * 0.54f, groundY - px(56f), t, aBase,
                        MERC_SILVER, MERC_PETRONAS, 0x00FFD700, true, true, 0x000052B4, 1.42f);
                break;

            case 14: // Scene 15: Zandvoort Banking — Verstappen Orange Army Flares & Trophy Lift
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF6600, 0x00FFFFFF, false);
                // Billowing Dutch Orange Army Flare Smoke Clouds in background
                for (int fl = 0; fl < 4; fl++) {
                    float fx = w * (0.28f + fl * 0.11f) + (float) Math.sin(t * 2f + fl) * px(10f);
                    float fy = groundY - px(54f + (fl % 2) * 18f);
                    paint.setColor((aBase / 3 << 24) | 0x00FF6800);
                    c.drawCircle(fx, fy, px(28f), paint);
                }
                drawF1Car(c, w * 0.33f, groundY - px(10f), t, aBase,
                        REDBULL_NAVY, REDBULL_ACC, TIRE_SOFT, false, false, false, 1.26f);
                drawF1DriverTrophyLift(c, w * 0.54f, groundY - px(56f), t, aBase,
                        REDBULL_NAVY, 0x00FF6800, 0x00FFD700, true, false, 0, 1.42f);
                break;

            case 15: // Scene 16: Miami Autodrome — Lando Norris Papaya Maiden Win Trophy Lift
                drawF1PodiumCeremony(c, w * 0.45f, groundY, t, aBase,
                        MCLAREN_PAPAYA, MCLAREN_ACC, REDBULL_NAVY, FERRARI_RED, true);
                break;

            case 16: // Scene 17: Monza Parabolica — Charles Leclerc Monza Winner Trophy Lift
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00009246, 0x00CE2B37, false);
                drawF1Car(c, w * 0.33f, groundY - px(10f), t, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, false, false, false, 1.28f);
                drawF1DriverTrophyLift(c, w * 0.54f, groundY - px(56f), t, aBase,
                        FERRARI_RED, FERRARI_ACC, 0x00FFD700, true, true, 0x00DC0000, 1.44f);
                break;

            case 17: // Scene 18: Austin COTA Turn 1 — Steep Uphill Four-Wide Start Charge
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00BF0A30, 0x00002868, false);
                float cotaX = w * (0.26f + p * 0.30f);
                drawF1Car(c, cotaX - px(42f), groundY - px(16f), t * 2.5f, aBase,
                        MCLAREN_PAPAYA, MCLAREN_ACC, TIRE_MED, false, true, false, 1.18f);
                drawF1Car(c, cotaX, groundY - px(12f), t * 2.5f + 0.3f, aBase,
                        REDBULL_NAVY, REDBULL_ACC, TIRE_MED, false, true, false, 1.24f);
                drawF1Car(c, cotaX + px(38f), groundY - px(8f), t * 2.5f + 0.6f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, false, true, false, 1.30f);
                break;

            case 18: // Scene 19: Imola Tamburello — Schumacher Ferrari F2004 V10 & Trophy Lift
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00009246, 0x00FFFFFF, false);
                drawF1Car(c, w * (0.26f + p * 0.22f), groundY - px(10f), t * 3.0f, aBase,
                        FERRARI_RED, 0x00FFFFFF, TIRE_SOFT, false, true, false, 1.30f);
                drawF1DriverTrophyLift(c, w * 0.56f, groundY - px(56f), t, aBase,
                        FERRARI_RED, 0x00FFFFFF, 0x00FFD700, true, false, 0, 1.42f);
                break;

            case 19: // Scene 20: Montreal Quebec — Wall of Champions Chicane Brush
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                // "BIENVENUE AU QUÉBEC" Wall of Champions Barrier along track
                paint.setColor((aBase * 4 / 5 << 24) | 0x00E8EDF5);
                c.drawRect(w * 0.26f, groundY - px(36f), w * 0.64f, groundY - px(22f), paint);
                paint.setColor((aBase * 4 / 5 << 24) | 0x000055A4);
                c.drawRect(w * 0.26f, groundY - px(29f), w * 0.64f, groundY - px(25f), paint);
                drawF1Car(c, w * (0.28f + p * 0.34f), groundY - px(10f), t * 2.7f, aBase,
                        ASTON_GREEN, ASTON_LIME, TIRE_SOFT, true, true, false, 1.32f);
                break;

            case 20: // Scene 21: Baku City Circuit — Castle Section Threading the Needle
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                // Baku Old Town Medieval Stone Castle Tower on Left
                paint.setColor((aBase * 4 / 5 << 24) | 0x008C765C);
                c.drawRoundRect(w * 0.27f, groundY - px(88f), w * 0.35f, groundY, px(4f), px(4f), paint);
                drawF1Car(c, w * (0.34f + p * 0.28f), groundY - px(10f), t * 2.5f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, false, true, false, 1.32f);
                break;

            case 21: // Scene 22: Jeddah Corniche — Red Sea 330 km/h Night Street Duel
                drawF1TrackBase(c, w, groundY, t, aBase, 0x0000A86B, 0x00FFFFFF, false);
                float jedX = w * (0.28f + p * 0.32f);
                drawF1Car(c, jedX, groundY - px(10f), t * 2.9f, aBase,
                        REDBULL_NAVY, REDBULL_ACC, TIRE_MED, true, true, false, 1.32f);
                drawF1Car(c, jedX - px(86f), groundY - px(13f), t * 2.9f + 0.4f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, true, true, false, 1.26f);
                break;

            case 22: // Scene 23: Melbourne Albert Park — Lakeside High-Speed Chicane
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FFD800, 0x0000843D, false);
                float melX = w * (0.28f + p * 0.32f);
                drawF1Car(c, melX, groundY - px(10f), t * 2.6f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, true, true, false, 1.32f);
                drawF1Car(c, melX - px(90f), groundY - px(13f), t * 2.6f + 0.5f, aBase,
                        MCLAREN_PAPAYA, MCLAREN_ACC, TIRE_MED, false, true, false, 1.26f);
                break;

            case 23: // Scene 24: Suzuka Chicane — Senna McLaren vs. Prost Ferrari Championship Showdown
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                float spX = w * (0.28f + p * 0.30f);
                drawF1Car(c, spX + px(28f), groundY - px(13f), t * 2.6f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_MED, false, true, false, 1.28f);
                drawF1Car(c, spX - px(24f), groundY - px(9f), t * 2.6f + 0.3f, aBase,
                        MP4_WHITE, MP4_RED, TIRE_SOFT, false, true, false, 1.32f);
                break;

            case 24: // Scene 25: Dijon-Prenois 1979 — Gilles Villeneuve Ferrari vs. Arnoux Wheel-to-Wheel
                drawF1TrackBase(c, w, groundY, t, aBase, 0x000055A4, 0x00EF4135, false);
                float dijX = w * (0.28f + p * 0.30f);
                float rub = (float) Math.sin(t * 9f) * px(6f);
                drawF1Car(c, dijX, groundY - px(13f), t * 2.7f, aBase,
                        RENAULT_YELLOW, 0x00181818, TIRE_MED, false, true, false, 1.26f);
                drawF1Car(c, dijX + rub, groundY - px(8f), t * 2.7f + 0.2f, aBase,
                        FERRARI_RED, 0x00FFFFFF, TIRE_SOFT, false, true, false, 1.32f);
                break;

            case 25: // Scene 26: Spa Kemmel Straight — Häkkinen McLaren Double Pass on Schumacher
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFD800, false);
                float kemX = w * (0.28f + p * 0.30f);
                drawF1Car(c, kemX - px(10f), groundY - px(14f), t * 2.6f, aBase,
                        FERRARI_RED, 0x00FFFFFF, TIRE_SOFT, false, true, false, 1.24f);
                drawF1Car(c, kemX + px(32f), groundY - px(8f), t * 2.9f, aBase,
                        MERC_SILVER, 0x00FF1801, TIRE_MED, true, true, false, 1.32f);
                break;

            case 26: // Scene 27: Interlagos 2008 — "Is That Glock?!" Last-Corner Title Decider in the Rain
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00009B3A, 0x00FEDF00, true);
                float glkX = w * (0.28f + p * 0.32f);
                drawF1Car(c, glkX, groundY - px(10f), t * 2.7f, aBase,
                        MERC_SILVER, 0x00FF1801, TIRE_WET, false, true, false, 1.32f);
                drawF1DriverTrophyLift(c, w * 0.56f, groundY - px(56f), t, aBase,
                        0x00ECEFF4, 0x00FF1801, 0x00FFD700, true, false, 0, 1.38f);
                break;

            case 27: // Scene 28: Yas Marina 2010 — Sebastian Vettel Youngest World Champion Trophy Lift
                drawF1PodiumCeremony(c, w * 0.45f, groundY, t, aBase,
                        REDBULL_NAVY, REDBULL_ACC, MERC_SILVER, FERRARI_RED, true);
                break;

            case 28: // Scene 29: Barcelona Catalunya — Fernando Alonso Renault R25 Home Trophy Lift
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00AA151B, 0x00F1BF00, false);
                drawF1Car(c, w * 0.33f, groundY - px(10f), t, aBase,
                        RENAULT_BLUE, RENAULT_YELLOW, TIRE_SOFT, false, false, false, 1.28f);
                drawF1DriverTrophyLift(c, w * 0.54f, groundY - px(56f), t, aBase,
                        RENAULT_BLUE, RENAULT_YELLOW, 0x00FFD700, true, true, 0x00F1BF00, 1.42f);
                break;

            case 29: // Scene 30: Melbourne 2009 — Jenson Button Brawn GP Fluorescent Trophy Lift
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00D8FF00, 0x00FFFFFF, false);
                drawF1Car(c, w * 0.33f, groundY - px(10f), t, aBase,
                        BRAWN_WHITE, BRAWN_NEON, TIRE_SOFT, false, false, false, 1.28f);
                drawF1DriverTrophyLift(c, w * 0.54f, groundY - px(56f), t, aBase,
                        BRAWN_WHITE, BRAWN_NEON, 0x00FFD700, true, false, 0, 1.42f);
                break;

            case 30: // Scene 31: Estoril Rain Masterclass — Senna Lotus 97T Black & Gold Wet Race Win
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FFD048, 0x00181818, true);
                drawF1Car(c, w * (0.28f + p * 0.30f), groundY - px(10f), t * 2.6f, aBase,
                        LOTUS_BLACK, LOTUS_GOLD, TIRE_WET, false, true, false, 1.32f);
                drawF1DriverTrophyLift(c, w * 0.57f, groundY - px(56f), t, aBase,
                        LOTUS_BLACK, LOTUS_GOLD, 0x00FFD700, false, true, 0x00FFD048, 1.38f);
                break;

            case 31: // Scene 32: Nürburgring Legends — Niki Lauda Ferrari & Fangio Silver Arrow
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                float nurX = w * (0.28f + p * 0.32f);
                drawF1Car(c, nurX, groundY - px(10f), t * 2.5f, aBase,
                        FERRARI_RED, 0x00FFFFFF, TIRE_HARD, false, true, false, 1.30f);
                drawF1Car(c, nurX - px(88f), groundY - px(13f), t * 2.5f + 0.5f, aBase,
                        0x00C8D2DC, MERC_PETRONAS, TIRE_HARD, false, true, false, 1.26f);
                break;

            case 32: // Scene 33: Silverstone 1992 — Nigel Mansell Red Five Williams FW14B
                drawF1TrackBase(c, w, groundY, t, aBase, 0x001858D8, 0x00FFD000, false);
                drawF1Car(c, w * (0.28f + p * 0.28f), groundY - px(10f), t * 2.8f, aBase,
                        WILLIAMS_BLUE, WILLIAMS_GOLD, TIRE_MED, false, true, false, 1.32f);
                drawF1DriverTrophyLift(c, w * 0.56f, groundY - px(56f), t, aBase,
                        WILLIAMS_BLUE, WILLIAMS_GOLD, 0x00FFD700, true, true, 0x00FF1801, 1.40f);
                break;

            case 33: // Scene 34: Austin COTA 2018 — Kimi Räikkönen Iceman Ferrari Trophy Lift
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                drawF1Car(c, w * 0.33f, groundY - px(10f), t, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, false, false, false, 1.28f);
                drawF1DriverTrophyLift(c, w * 0.54f, groundY - px(56f), t, aBase,
                        FERRARI_RED, 0x00FFFFFF, 0x00FFD700, true, false, 0, 1.42f);
                break;

            case 34: // Scene 35: Monza 2021 Podium — McLaren One-Two Finish & Trophy Lift
                drawF1PodiumCeremony(c, w * 0.45f, groundY, t, aBase,
                        MCLAREN_PAPAYA, MCLAREN_ACC, MCLAREN_PAPAYA, MERC_SILVER, true);
                break;

            case 35: // Scene 36: Sakhir Outer Loop — Last-to-First Night Victory Trophy Lift
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF80C0, 0x00FFFFFF, false);
                drawF1Car(c, w * 0.33f, groundY - px(10f), t, aBase,
                        0x00FF66B2, 0x001858D8, TIRE_SOFT, false, false, false, 1.28f);
                drawF1DriverTrophyLift(c, w * 0.54f, groundY - px(56f), t, aBase,
                        0x00FF66B2, 0x00FFFFFF, 0x00FFD700, true, true, 0x00009B3A, 1.42f);
                break;

            case 36: // Scene 37: Silverstone Club — "Through Goes Hamilton!" Three-Wide Apex Battle
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                float clbX = w * (0.28f + p * 0.30f);
                drawF1Car(c, clbX - px(28f), groundY - px(15f), t * 2.6f, aBase,
                        REDBULL_NAVY, REDBULL_ACC, TIRE_SOFT, false, true, false, 1.20f);
                drawF1Car(c, clbX, groundY - px(12f), t * 2.6f + 0.3f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_HARD, false, true, false, 1.24f);
                drawF1Car(c, clbX + px(34f), groundY - px(8f), t * 2.9f + 0.6f, aBase,
                        MERC_SILVER, MERC_PETRONAS, TIRE_HARD, true, true, false, 1.32f);
                break;

            case 37: // Scene 38: Marina Bay Finale — Carlos Sainz Smooth Operator Ferrari Trophy Lift
                drawF1PodiumCeremony(c, w * 0.45f, groundY, t, aBase,
                        FERRARI_RED, FERRARI_ACC, MCLAREN_PAPAYA, MERC_SILVER, true);
                break;

            case 38: // Scene 39: Spa-Francorchamps Rain — Schumacher Ferrari Rooster-Tail Charge
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFD800, true);
                drawF1Car(c, w * (0.28f + p * 0.34f), groundY - px(10f), t * 2.8f, aBase,
                        FERRARI_RED, 0x00FFFFFF, TIRE_WET, false, true, false, 1.34f);
                break;

            case 39: // Scene 40: Suzuka 2005 — Räikkönen Last-Lap Outside Overtake at 130R
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                float suzX = w * (0.28f + p * 0.32f);
                drawF1Car(c, suzX - px(24f), groundY - px(13f), t * 2.6f, aBase,
                        RENAULT_BLUE, RENAULT_YELLOW, TIRE_MED, false, true, false, 1.24f);
                drawF1Car(c, suzX + px(28f), groundY - px(9f), t * 2.9f, aBase,
                        MERC_SILVER, 0x00FF1801, TIRE_SOFT, true, true, false, 1.32f);
                break;

            case 40: // Scene 41: Monaco Harbor — Charles Leclerc Prince of Monaco Home Trophy Lift
                drawF1PodiumCeremony(c, w * 0.45f, groundY, t, aBase,
                        FERRARI_RED, 0x00FFFFFF, MCLAREN_PAPAYA, FERRARI_RED, true);
                break;

            case 41: // Scene 42: Safety Car Restart — Green Flag Pack Charge & Tire Weaving
                drawF1TrackBase(c, w, groundY, t, aBase, 0x0000E676, 0x00FFFFFF, false);
                float scX = w * (0.26f + p * 0.30f);
                float wv = (float) Math.sin(t * 8f) * px(5f);
                drawF1Car(c, scX - px(74f), groundY - px(13f) - wv, t * 2.5f, aBase,
                        MCLAREN_PAPAYA, MCLAREN_ACC, TIRE_SOFT, false, true, false, 1.20f);
                drawF1Car(c, scX, groundY - px(11f) + wv, t * 2.5f + 0.3f, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, false, true, false, 1.25f);
                drawF1Car(c, scX + px(78f), groundY - px(9f) - wv, t * 2.5f + 0.6f, aBase,
                        REDBULL_NAVY, REDBULL_ACC, TIRE_SOFT, false, true, false, 1.30f);
                break;

            case 42: // Scene 43: Parc Fermé Victory — Driver Leaping Atop the Halo with Trophy
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FF1801, 0x00FFFFFF, false);
                drawF1Car(c, w * 0.45f, groundY - px(10f), t, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, false, false, false, 1.38f);
                // Driver standing proudly on top of the F1 car cockpit Halo lifting the golden trophy!
                drawF1DriverTrophyLift(c, w * 0.45f - px(6f), groundY - px(74f), t, aBase,
                        FERRARI_RED, FERRARI_ACC, 0x00FFD700, false, false, 0, 1.26f);
                break;

            default: // Scene 44: Champions Gala — Constructors' & Drivers' World Championship Trophy Lift
                drawF1TrackBase(c, w, groundY, t, aBase, 0x00FFD700, 0x00FF1801, false);
                drawF1Car(c, w * 0.30f, groundY - px(10f), t, aBase,
                        FERRARI_RED, FERRARI_ACC, TIRE_SOFT, false, false, false, 1.18f);
                drawF1Car(c, w * 0.60f, groundY - px(10f), t, aBase,
                        REDBULL_NAVY, REDBULL_ACC, TIRE_SOFT, false, false, true, 1.18f);
                drawF1DriverTrophyLift(c, w * 0.45f, groundY - px(58f), t, aBase,
                        REDBULL_NAVY, REDBULL_ACC, 0x00FFD700, true, false, 0, 1.46f);
                break;
        }
        c.restore();
    }

    /**
     * Draws the 44-Scene CYBERPUNK 2099 (Night City / Edgerunners / Netrunner) Saga Loop across the bottom stage.
     * Features:
     * - Faded Night City District & Faction Watermark in the lower stage (`CORPO PLAZA // ARASAKA`, `EDGERUNNERS // SANDEVISTAN`, etc.)
     * - Neon wet-asphalt reflection grid along groundY
     * - Quadra Turbo-R V-Tech supercar with glowing cyan light-bar & magenta underglow
     * - Yaiba Kusanagi CT-3X neon cyber-bike Akira power-slides & wheelies
     * - Cyber-Samurai / David Martinez / V with Sandevistan emerald-cyan time-dilation clones & thermal katana slash
     * - Trauma Team AV-4 aerial extraction & Blackwall cyberspace data pillars
     */
    private void drawCyberpunkStoryLoop(Canvas c, int w, int h, float t) {
        int count = CYBERPUNK_TITLES.length;
        float totalCycle = CHAPTER_SEC * count;
        float cycleTime = t % totalCycle;
        int scene = resolveActiveScene(cycleTime, count);
        float p = (cycleTime % CHAPTER_SEC) / CHAPTER_SEC;
        float fade = resolveStoryFade(p);
        int aBase = Math.min(255, Math.max(0, Math.round(fade * 255f)));

        float groundY = h * 0.900f;
        drawStoryCaptionAndDots(c, w, h, CYBERPUNK_TITLES[scene], scene, count, aBase,
                0x0000F0FF, 0xEE00F0FF, 0x44FF0055, px(7.8f));

        c.save();
        applyStageTransform(c, w, h, groundY);

        // 1. Faded Night City District & Faction Watermark in the bottom stage
        String[] districts = {
            "CORPO PLAZA // ARASAKA TOWER",
            "WATSON KABUKI // QUADRA V-TECH",
            "AFTERLIFE VIP // SAMURAI LEGENDS",
            "SANDEVISTAN // EDGERUNNERS OVERDRIVE",
            "JAPANTOWN // YAIBA KUSANAGI",
            "PACIFICA // BLACKWALL CYBERSPACE",
            "KONPEKI PLAZA // RELIC BIOCHIP",
            "DOGTOWN // BLACKWALL PROTOCOL"
        };
        String[] subs = {
            "NIGHT CITY SECTOR 01 // HIGH-ALERT TRAUMA TEAM AIRSPACE",
            "TURBO-R V-TECH // NEON WET-ASPHALT MIDNIGHT DRIFT",
            "JOHNNY SILVERHAND & V // NEVER FADE AWAY",
            "DAVID MARTINEZ & LUCY // MILITARY CYBERWARE TIME-DILATION",
            "AKIRA HIGH-G POWER SLIDE // TYGER CLAWS TERRITORY",
            "NETWATCH ICE BREAKER // DEEP NEURAL SUB-NET",
            "ARASAKA COUNTER-INTEL // MILITECH FLATHEAD BOT",
            "SOLOMON REED & SONGBIRD // ORBITAL AIR LAUNCH"
        };
        int[] distCols = {0x00FF0055, 0x0000F0FF, 0x00FCEE09, 0x0000FF9D, 0x00FF003C, 0x0000F0FF, 0x00FCEE09, 0x00FF0055};
        int dIdx = scene % districts.length;
        int dCol = distCols[dIdx];

        Typeface prevTf = paint.getTypeface();
        Paint.Align prevAlign = paint.getTextAlign();
        paint.setTypeface(f1Font != null ? f1Font : (cinzelFont != null ? cinzelFont : Typeface.DEFAULT_BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        float cx = w * 0.45f;
        float maxW = w * 0.46f;
        float wmSize = px(26f);
        paint.setTextSize(wmSize);
        float m = paint.measureText(districts[dIdx]);
        if (m > maxW && m > 0f) paint.setTextSize(Math.max(px(13f), wmSize * (maxW / m)));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((Math.round(aBase * 0.16f) << 24) | dCol);
        c.drawText(districts[dIdx], cx, groundY - px(38f), paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(1.0f));
        paint.setColor((Math.round(aBase * 0.28f) << 24) | 0x0000F0FF);
        c.drawText(districts[dIdx], cx, groundY - px(38f), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(px(8.8f));
        paint.setColor((Math.round(aBase * 0.42f) << 24) | 0x00E0F8FF);
        c.drawText(subs[dIdx], cx, groundY + px(14f), paint);
        paint.setTextAlign(prevAlign);
        paint.setTypeface(prevTf);

        // 2. Neon Wet-Asphalt Cyber Grid along groundY
        float kStart = w * 0.22f, kEnd = w * 0.68f;
        float kW = px(22f);
        float scroll = (t * px(160f)) % (kW * 2f);
        int kIdx = 0;
        for (float kx = kStart - scroll; kx < kEnd; kx += kW, kIdx++) {
            float l = Math.max(kStart, kx);
            float r = Math.min(kEnd, kx + kW);
            if (r > l) {
                int kc = (kIdx % 2 == 0) ? 0x0000F0FF : 0x00FF0055;
                paint.setColor((aBase * 3 / 4 << 24) | kc);
                c.drawRect(l, groundY - px(3.5f), r, groundY, paint);
            }
        }

        // 3. Scene-Specific Choreography (Quadra V-Tech, Yaiba Kusanagi, Sandevistan Edgerunner, Blackwall Netrunner, Trauma Team AV)
        int mode = scene % 5;
        if (mode == 0) {
            // Mode A: Quadra Turbo-R V-Tech Supercar vs. Trauma Team AV-4 Gunship Extraction
            float carX = w * (0.28f + p * 0.32f);
            drawCyberQuadraCar(c, carX, groundY - px(10f), t, aBase, 0x001A2234, 0x0000F0FF, 0x00FF0055, false, 1.32f);
            // Hovering Trauma Team AV-4 overhead with downward searchlight beam
            float avX = w * (0.58f - p * 0.18f);
            float avY = groundY - px(92f) + (float) Math.sin(t * 4f) * px(6f);
            paint.setColor((aBase / 4 << 24) | 0x0000F0FF);
            actorPath.reset();
            actorPath.moveTo(avX - px(18f), avY + px(8f));
            actorPath.lineTo(avX + px(18f), avY + px(8f));
            actorPath.lineTo(avX + px(54f), groundY);
            actorPath.lineTo(avX - px(54f), groundY);
            actorPath.close();
            c.drawPath(actorPath, paint);
            paint.setColor((aBase << 24) | 0x00F0F6FF);
            c.drawRoundRect(avX - px(32f), avY - px(10f), avX + px(32f), avY + px(10f), px(5f), px(5f), paint);
            paint.setColor((aBase << 24) | 0x00FF003C);
            c.drawRect(avX - px(14f), avY - px(4f), avX + px(14f), avY + px(4f), paint);
            c.drawCircle(avX - px(24f), avY + px(10f), px(4.5f), paint);
            c.drawCircle(avX + px(24f), avY + px(10f), px(4.5f), paint);
        } else if (mode == 1) {
            // Mode B: Sandevistan Time-Dilation Overdrive — 5 Emerald/Cyan After-Image Clones + Thermal Katana Slash
            float baseX = w * (0.28f + p * 0.30f);
            int[] sandCols = {0x0000FF66, 0x0000F0FF, 0x0038FF98, 0x0000D8FF, 0x00FCEE09};
            for (int cl = 0; cl < 5; cl++) {
                float cxPos = baseX - (4 - cl) * px(26f);
                int cAlpha = (cl == 4) ? aBase : Math.round(aBase * (0.22f + cl * 0.14f));
                drawCyberEdgerunner(c, cxPos, groundY - px(56f), t + cl * 0.2f, cAlpha, sandCols[cl], cl == 4, false, 1.32f);
            }
            // Opponent Borg / Cyberpsycho on right
            drawCyberEdgerunner(c, w * 0.62f, groundY - px(58f), t, aBase, 0x00FF003C, true, true, 1.42f);
            drawLightningStreak(c, baseX + px(18f), groundY - px(64f), w * 0.60f, groundY - px(64f),
                    t, 700 + scene, 0x0000F0FF, 0x00FCEE09, 0x00FFFFFF, px(3.8f), aBase, true);
        } else if (mode == 2) {
            // Mode C: Yaiba Kusanagi CT-3X Akira Power-Slide & Plasma Light-Ribbon Wake
            float bikeX = w * (0.28f + p * 0.34f);
            // Trailing Akira neon red & cyan tail-light plasma ribbons
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(4.2f));
            paint.setColor((aBase * 3 / 4 << 24) | 0x00FF003C);
            c.drawLine(bikeX - px(95f), groundY - px(16f), bikeX - px(18f), groundY - px(16f), paint);
            paint.setStrokeWidth(px(2.6f));
            paint.setColor((aBase * 3 / 4 << 24) | 0x0000F0FF);
            c.drawLine(bikeX - px(82f), groundY - px(10f), bikeX - px(18f), groundY - px(10f), paint);
            paint.setStyle(Paint.Style.FILL);
            drawCyberKusanagiBike(c, bikeX, groundY - px(10f), t, aBase, 0x00FF003C, 0x0000F0FF, 1.38f);
        } else if (mode == 3) {
            // Mode D: The Blackwall Cyberspace Dive — Vertical Red/Cyan Data Matrix Pillars & Floating Netrunners
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.4f));
            for (int col = 0; col < 12; col++) {
                float bx = w * (0.25f + col * 0.036f);
                float bh = px(55f + 38f * (float) Math.abs(Math.sin(t * 3.2f + col * 0.9f)));
                paint.setColor(((aBase * 2 / 3) << 24) | (col % 3 == 0 ? 0x0000F0FF : 0x00FF003C));
                c.drawLine(bx, groundY, bx, groundY - bh, paint);
            }
            paint.setStyle(Paint.Style.FILL);
            drawCyberEdgerunner(c, w * 0.38f, groundY - px(62f), t, aBase, 0x0000F0FF, false, false, 1.32f);
            drawCyberEdgerunner(c, w * 0.54f, groundY - px(62f), t + 0.5f, aBase, 0x00FF0055, false, true, 1.32f);
        } else {
            // Mode E: High-Speed Midnight Street Duel — Quadra V-Tech vs. Rayfield Caliburn Hypercar
            float c1 = w * (0.25f + p * 0.28f);
            float c2 = c1 + px(135f) + (float) Math.sin(t * 4.5f) * px(18f);
            drawCyberQuadraCar(c, c1, groundY - px(10f), t, aBase, 0x00182030, 0x00FF0055, 0x00FCEE09, false, 1.26f);
            drawCyberQuadraCar(c, c2, groundY - px(10f), t + 0.4f, aBase, 0x00F0F6FF, 0x0000F0FF, 0x00FF003C, false, 1.28f);
        }

        c.restore();
    }

    private void drawCyberQuadraCar(Canvas c, float x, float y, float t, int alpha,
                                     int bodyRgb, int neonRgb, int tailRgb, boolean facingLeft, float s) {
        float dir = facingLeft ? -1f : 1f;
        // 1. Neon Underglow Strip on the Wet Asphalt
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha / 2 << 24) | (neonRgb & 0xFFFFFF));
        c.drawOval(x - px(46f * s), y + px(2f * s), x + px(46f * s), y + px(10f * s), paint);

        // 2. Blue/Magenta Twin Plasma Exhaust Afterburner Flames
        float flameLen = px(18f * s + 8f * s * (float) Math.abs(Math.sin(t * 28f)));
        paint.setColor((alpha << 24) | (tailRgb & 0xFFFFFF));
        c.drawOval(Math.min(x - dir * px(42f * s), x - dir * (px(42f * s) + flameLen)), y - px(9f * s),
                   Math.max(x - dir * px(42f * s), x - dir * (px(42f * s) + flameLen)), y - px(3f * s), paint);

        // 3. Wide Wedge-Shaped Quadra V-Tech / Caliburn Chassis
        paint.setColor((alpha << 24) | (bodyRgb & 0xFFFFFF));
        actorPath.reset();
        actorPath.moveTo(x - dir * px(42f * s), y - px(3f * s));
        actorPath.lineTo(x - dir * px(40f * s), y - px(16f * s));
        actorPath.lineTo(x - dir * px(14f * s), y - px(24f * s)); // Louvered fastback roofline
        actorPath.lineTo(x + dir * px(12f * s), y - px(23f * s));
        actorPath.lineTo(x + dir * px(32f * s), y - px(12f * s));
        actorPath.lineTo(x + dir * px(46f * s), y - px(3f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 4. Glowing Cyan Side Trim & Windshield Visor
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.0f * s));
        paint.setColor((alpha << 24) | (neonRgb & 0xFFFFFF));
        c.drawLine(x - dir * px(38f * s), y - px(11f * s), x + dir * px(42f * s), y - px(9f * s), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | (tailRgb & 0xFFFFFF));
        c.drawCircle(x - dir * px(40f * s), y - px(13f * s), px(3.5f * s), paint);

        // 5. Glowing Cyber-Rims
        float wR = px(9.2f * s);
        for (int wi = 0; wi < 2; wi++) {
            float wx = x + (wi == 0 ? -dir * px(26f * s) : dir * px(28f * s));
            paint.setStyle(Paint.Style.FILL);
            paint.setColor((alpha << 24) | 0x0010141E);
            c.drawCircle(wx, y - px(6f * s), wR, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.2f * s));
            paint.setColor((alpha << 24) | (neonRgb & 0xFFFFFF));
            c.drawCircle(wx, y - px(6f * s), wR * 0.68f, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawCyberKusanagiBike(Canvas c, float x, float y, float t, int alpha, int bodyRgb, int neonRgb, float s) {
        // Low-slung Yaiba Kusanagi / Kaneda Cyber-Motorcycle with glowing hubless neon rims
        float wR = px(10.0f * s);
        float rX = x - px(24f * s), fX = x + px(26f * s), wY = y - px(6.5f * s);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00121622);
        c.drawCircle(rX, wY, wR, paint);
        c.drawCircle(fX, wY, wR, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.4f * s));
        paint.setColor((alpha << 24) | (neonRgb & 0xFFFFFF));
        c.drawCircle(rX, wY, wR * 0.68f, paint);
        c.drawCircle(fX, wY, wR * 0.68f, paint);

        // Fairing & Rider leaning forward
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | (bodyRgb & 0xFFFFFF));
        actorPath.reset();
        actorPath.moveTo(rX - px(6f * s), wY - px(4f * s));
        actorPath.lineTo(x + px(8f * s), wY - px(18f * s));
        actorPath.lineTo(fX + px(8f * s), wY - px(6f * s));
        actorPath.lineTo(rX, wY + px(2f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Edgerunner rider helmet & yellow collar
        paint.setColor((alpha << 24) | 0x00FCEE09);
        c.drawCircle(x - px(2f * s), wY - px(22f * s), px(5.2f * s), paint);
    }

    private void drawCyberEdgerunner(Canvas c, float x, float y, float t, int alpha,
                                     int neonRgb, boolean katanaOut, boolean facingLeft, float s) {
        float dir = facingLeft ? -1f : 1f;
        float stride = (float) Math.sin(t * 7f) * px(5f * s);
        int col = neonRgb & 0xFFFFFF;

        // Neon Aura
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((Math.max(0, alpha / 5) << 24) | col);
        c.drawCircle(x, y + px(4f * s), px(26f * s), paint);

        // Legs
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(3.2f * s));
        paint.setColor((alpha << 24) | 0x001C2436);
        c.drawLine(x - px(4f * s), y + px(14f * s), x - px(10f * s) - stride * 0.5f, y + px(38f * s), paint);
        c.drawLine(x + px(4f * s), y + px(14f * s), x + px(10f * s) + stride * 0.5f, y + px(38f * s), paint);

        // Samurai / Edgerunner High-Collar Jacket
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00182030);
        c.drawRoundRect(x - px(8.5f * s), y - px(12f * s), x + px(8.5f * s), y + px(18f * s), px(3f * s), px(3f * s), paint);
        // Glowing high LED collar & Sandevistan neural spine stripe
        paint.setColor((alpha << 24) | col);
        c.drawRect(x - px(9.5f * s), y - px(15f * s), x + px(9.5f * s), y - px(10f * s), paint);
        c.drawRect(x - px(2f * s), y - px(10f * s), x + px(2f * s), y + px(14f * s), paint);

        // Head & Glowing Cyber-Visor
        paint.setColor((alpha << 24) | 0x00222A3A);
        c.drawCircle(x, y - px(20f * s), px(5.8f * s), paint);
        paint.setColor((alpha << 24) | 0x0000F0FF);
        c.drawRect(x + (facingLeft ? -px(6f * s) : 0f), y - px(22f * s),
                   x + (facingLeft ? 0f : px(6f * s)), y - px(19.5f * s), paint);

        // Thermal Katana / Mantis Blade Slash
        if (katanaOut) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(px(2.8f * s));
            paint.setColor((alpha << 24) | col);
            c.drawLine(x + dir * px(6f * s), y - px(6f * s), x + dir * px(34f * s), y - px(22f * s), paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    /**
     * Renders the faded Constructor / Team Name watermark & telemetry badge in the bottom F1 story area
     * so every scene clearly displays the competing Formula 1 Team(s) in a sleek faded motorsport look.
     */
    private void drawF1TeamBackdrop(Canvas c, int w, float groundY, int alpha, int scene) {
        String teamWatermark;
        String teamSub;
        int teamColor;
        switch (scene) {
            case 0:
                teamWatermark = "RED BULL RACING  ×  SCUDERIA FERRARI";
                teamSub = "CONSTRUCTORS // RB20 #1  ·  SF-24 #16";
                teamColor = 0x00FF1801;
                break;
            case 1:
            case 22:
                teamWatermark = "SCUDERIA FERRARI  ×  McLAREN F1";
                teamSub = "CONSTRUCTORS // MARANELLO SF-24  ·  WOKING MCL38";
                teamColor = 0x00FF2800;
                break;
            case 2:
            case 16:
            case 18:
            case 20:
            case 33:
            case 37:
            case 38:
            case 40:
            case 42:
                teamWatermark = "SCUDERIA FERRARI";
                teamSub = "MARANELLO // CAVALLINO RAMPANTE · SF-24";
                teamColor = 0x00FF1801;
                break;
            case 3:
            case 5:
            case 8:
            case 21:
            case 43:
                teamWatermark = "ORACLE RED BULL  ×  SCUDERIA FERRARI";
                teamSub = "CHAMPIONSHIP RIVALRY // RB20 #1  ·  SF-24 #16";
                teamColor = 0x00FFD800;
                break;
            case 4:
                teamWatermark = "MERCEDES-AMG  ×  McLAREN F1";
                teamSub = "SILVERSTONE // PETRONAS W15 #44  ·  PAPAYA MCL38 #4";
                teamColor = 0x0000F5D4;
                break;
            case 6:
            case 12:
                teamWatermark = "McLAREN HONDA MP4/4";
                teamSub = "WOKING HERITAGE // AYRTON SENNA #12 · TURBO V6";
                teamColor = 0x00FF1801;
                break;
            case 7:
                teamWatermark = "SCUDERIA FERRARI  ×  MERCEDES-AMG";
                teamSub = "NIGHT STREET DUEL // SF-24 #16  ·  W15 #44";
                teamColor = 0x0000F5D4;
                break;
            case 9:
            case 10:
            case 11:
            case 14:
            case 27:
                teamWatermark = "ORACLE RED BULL RACING";
                teamSub = "MILTON KEYNES // WORLD CHAMPIONS · RB20 #1";
                teamColor = 0x00FFD800;
                break;
            case 13:
                teamWatermark = "MERCEDES-AMG PETRONAS";
                teamSub = "BRACKLEY SILVER ARROWS // LEWIS HAMILTON #44";
                teamColor = 0x0000F5D4;
                break;
            case 15:
            case 34:
                teamWatermark = "McLAREN FORMULA 1 TEAM";
                teamSub = "WOKING // PAPAYA RACING · MCL38 #4";
                teamColor = 0x00FF8000;
                break;
            case 17:
            case 41:
                teamWatermark = "McLAREN  ·  RED BULL  ·  FERRARI";
                teamSub = "THREE-TEAM GRAND PRIX SHOWDOWN // #4 · #1 · #16";
                teamColor = 0x00FF8000;
                break;
            case 19:
                teamWatermark = "ASTON MARTIN ARAMCO F1";
                teamSub = "SILVERSTONE // BRITISH RACING GREEN · AMR24 #14";
                teamColor = 0x0000E698;
                break;
            case 23:
                teamWatermark = "McLAREN HONDA  ×  SCUDERIA FERRARI";
                teamSub = "SUZUKA LEGENDS // SENNA #12  ·  PROST #2";
                teamColor = 0x00FF1801;
                break;
            case 24:
                teamWatermark = "SCUDERIA FERRARI  ×  RENAULT F1";
                teamSub = "DIJON 1979 // VILLENEUVE #12  ·  ARNOUX #16";
                teamColor = 0x00FFE600;
                break;
            case 25:
                teamWatermark = "McLAREN MERCEDES  ×  FERRARI";
                teamSub = "SPA KEMMEL // HÄKKINEN MP4/15  ·  SCHUMACHER F1-2000";
                teamColor = 0x00DCE6F2;
                break;
            case 26:
                teamWatermark = "McLAREN MERCEDES";
                teamSub = "INTERLAGOS 2008 // VODAFONE McLAREN MP4-23 #22";
                teamColor = 0x00FF1801;
                break;
            case 28:
                teamWatermark = "RENAULT F1 TEAM";
                teamSub = "ENSTONE & VIRY // FERNANDO ALONSO · R25 V10 #5";
                teamColor = 0x0000A8FF;
                break;
            case 29:
                teamWatermark = "BRAWN GP FORMULA 1";
                teamSub = "BRACKLEY // JENSON BUTTON · BGP 001 #22";
                teamColor = 0x00D8FF00;
                break;
            case 30:
                teamWatermark = "TEAM LOTUS RENAULT 97T";
                teamSub = "HETHEL // BLACK & GOLD JPS · AYRTON SENNA #12";
                teamColor = 0x00FFD700;
                break;
            case 31:
                teamWatermark = "SCUDERIA FERRARI  ×  MERCEDES-BENZ";
                teamSub = "NÜRBURGRING LEGENDS // LAUDA 312T  ·  FANGIO W196";
                teamColor = 0x00FF1801;
                break;
            case 32:
                teamWatermark = "WILLIAMS RENAULT FW14B";
                teamSub = "GROVE // NIGEL MANSELL · RED FIVE #5";
                teamColor = 0x00FFD000;
                break;
            case 35:
                teamWatermark = "RACING POINT BWT F1";
                teamSub = "SAKHIR // SERGIO PÉREZ · RP20 #11";
                teamColor = 0x00FF66B2;
                break;
            case 36:
                teamWatermark = "MERCEDES  ·  FERRARI  ·  RED BULL";
                teamSub = "SILVERSTONE CLUB // #44  ·  #16  ·  #11";
                teamColor = 0x0000F5D4;
                break;
            case 39:
                teamWatermark = "McLAREN MERCEDES  ×  RENAULT F1";
                teamSub = "SUZUKA 130R // RÄIKKÖNEN MP4-20 #9  ·  FISICHELLA R25 #6";
                teamColor = 0x0000A8FF;
                break;
            default:
                teamWatermark = "FORMULA 1 WORLD CHAMPIONSHIP";
                teamSub = "CONSTRUCTORS & DRIVERS GRAND PRIX";
                teamColor = 0x00FF1801;
                break;
        }

        float cx = w * 0.45f;
        float maxW = w * 0.46f;
        Typeface prevTf = paint.getTypeface();
        Paint.Align prevAlign = paint.getTextAlign();
        paint.setTypeface(f1Font != null ? f1Font : (cinzelFont != null ? cinzelFont : Typeface.DEFAULT_BOLD));
        paint.setTextAlign(Paint.Align.CENTER);

        // 1. Large Faded Team Name Watermark in the bottom stage behind the cars (faded motorsport look)
        float wmSize = px(28f);
        paint.setTextSize(wmSize);
        float measured = paint.measureText(teamWatermark);
        if (measured > maxW && measured > 0f) {
            wmSize = Math.max(px(14f), wmSize * (maxW / measured));
            paint.setTextSize(wmSize);
        }
        int fillA = Math.max(0, Math.min(255, Math.round(alpha * 0.16f)));
        int strokeA = Math.max(0, Math.min(255, Math.round(alpha * 0.28f)));
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((fillA << 24) | (teamColor & 0xFFFFFF));
        c.drawText(teamWatermark, cx, groundY - px(36f), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(1.1f));
        paint.setColor((strokeA << 24) | 0x00F4F8FF);
        c.drawText(teamWatermark, cx, groundY - px(36f), paint);

        // 2. Faded Team Name & Chassis Telemetry Strip directly in the lower track apron area
        paint.setStyle(Paint.Style.FILL);
        int subA = Math.max(0, Math.min(255, Math.round(alpha * 0.42f)));
        paint.setTextSize(px(9.2f));
        paint.setColor((subA << 24) | 0x00E4ECF8);
        c.drawText(teamSub, cx, groundY + px(14f), paint);

        // Subtle faded team-colored speed rule flanking the bottom team label
        int lineA = Math.max(0, Math.min(255, Math.round(alpha * 0.30f)));
        paint.setColor((lineA << 24) | (teamColor & 0xFFFFFF));
        c.drawRect(w * 0.25f, groundY + px(3.5f), w * 0.65f, groundY + px(5.2f), paint);

        paint.setTextAlign(prevAlign);
        paint.setTypeface(prevTf);
    }

    /**
     * Renders the scrolling FIA Grand Prix Track Kerbs (Red/White or circuit-themed) and optional wet-weather spray.
     */
    private void drawF1TrackBase(Canvas c, int w, float groundY, float t, int alpha, int kerbCol1, int kerbCol2, boolean wetTrack) {
        paint.setStyle(Paint.Style.FILL);
        float kStart = w * 0.22f, kEnd = w * 0.68f;
        float kW = px(18f);
        float scroll = (t * px(180f)) % (kW * 2f);
        int idx = 0;
        for (float kx = kStart - scroll; kx < kEnd; kx += kW, idx++) {
            float l = Math.max(kStart, kx);
            float r = Math.min(kEnd, kx + kW);
            if (r > l) {
                int kc = (idx % 2 == 0) ? kerbCol1 : kerbCol2;
                paint.setColor((alpha * 4 / 5 << 24) | (kc & 0xFFFFFF));
                c.drawRect(l, groundY - px(4.5f), r, groundY, paint);
            }
        }
        if (wetTrack) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(1.5f));
            paint.setColor((alpha / 2 << 24) | 0x0090D8FF);
            for (int r = 0; r < 10; r++) {
                float rx = w * (0.26f + ((r * 0.043f + t * 0.25f) % 0.40f));
                float ry = groundY - px(70f) + ((r * 19f + t * 140f) % px(65f));
                c.drawLine(rx, ry, rx - px(8f), ry + px(14f), paint);
            }
            paint.setStyle(Paint.Style.FILL);
        }
    }

    /**
     * Renders the 5-Red-Lights FIA Starting Gantry above the starting grid.
     */
    private void drawF1StartGantry(Canvas c, float cx, float gy, float sceneProg, int alpha) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x001A202C);
        c.drawRoundRect(cx - px(74f), gy - px(16f), cx + px(74f), gy + px(14f), px(4f), px(4f), paint);
        boolean lightsOut = sceneProg >= 0.45f;
        int litCount = lightsOut ? 0 : Math.min(5, 1 + (int) (sceneProg / 0.08f));
        for (int i = 0; i < 5; i++) {
            float lx = cx + (i - 2) * px(26f);
            paint.setColor((alpha << 24) | 0x000E121A);
            c.drawRoundRect(lx - px(9f), gy - px(13f), lx + px(9f), gy + px(11f), px(3f), px(3f), paint);
            boolean on = (i < litCount);
            paint.setColor(((on ? alpha : alpha / 4) << 24) | (on ? 0x00FF1801 : 0x00481014));
            c.drawCircle(lx, gy - px(4f), px(4.2f), paint);
            c.drawCircle(lx, gy + px(4.5f), px(4.2f), paint);
            if (on) {
                paint.setColor((alpha / 3 << 24) | 0x00FF1801);
                c.drawCircle(lx, gy, px(13f), paint);
            }
        }
    }

    /**
     * Bespoke Team-Accurate Formula 1 Car Renderer:
     * - Distinct Constructor Liveries & Silhouettes (Ferrari SF-24, Red Bull RB20, McLaren MCL38,
     *   Mercedes W15, Aston Martin AMR24, Classic 1988 McLaren MP4/4, Classic 1985 Lotus 97T,
     *   Renault R25, Williams FW14B, Brawn GP BGP 001)
     * - Faded floating Constructor Team Name + Car Number callout above each car
     * - 18-inch Pirelli P-Zero color-coded tires + glowing carbon-ceramic brake discs + titanium sparks
     */
    private void drawF1Car(Canvas c, float x, float y, float t, int alpha,
                           int bodyRgb, int accentRgb, int tireWallRgb,
                           boolean drsOpen, boolean sparksAndFlames, boolean facingLeft, float s) {
        float dir = facingLeft ? -1f : 1f;
        int bodyCol = bodyRgb & 0xFFFFFF;
        int accCol = accentRgb & 0xFFFFFF;
        int tireCol = tireWallRgb & 0xFFFFFF;

        // Identify Constructor Team from palette so each team gets a genuinely distinct livery & badge
        boolean isFerrari = (bodyCol == 0xFF1801 || bodyCol == 0xDC0000);
        boolean isRedBull = (bodyCol == 0x1B3C98 || bodyCol == 0x0B163E);
        boolean isMcLaren = (bodyCol == 0xFF8000);
        boolean isMercedes = (bodyCol == 0xC8D6E6 || bodyCol == 0x242C36 || bodyCol == 0xC8D2DC);
        boolean isAston = (bodyCol == 0x00A878 || bodyCol == 0x006F62);
        boolean isMp4 = (bodyCol == 0xF4F6FA && accCol == 0xFF1801);
        boolean isLotus = (bodyCol == 0x1E2028 || bodyCol == 0x16161C);
        boolean isRenault = (bodyCol == 0x00A8FF || bodyCol == 0x0090FF || bodyCol == 0xFFE600 || bodyCol == 0xFFE000);
        boolean isWilliams = (bodyCol == 0x1E68F0 || bodyCol == 0x1858D8);
        boolean isBrawn = (bodyCol == 0xF4F8FA && accCol == 0xD8FF00);
        boolean isPink = (bodyCol == 0xFF66B2);
        boolean classicOpenCockpit = isMp4 || isLotus;

        String teamTag = "F1";
        String carNum = "1";
        int badgeRgb = accCol;
        if (isFerrari) { teamTag = "FERRARI"; carNum = "16"; badgeRgb = 0x00FF2800; }
        else if (isRedBull) { teamTag = "RED BULL"; carNum = "1"; badgeRgb = 0x00FFD800; }
        else if (isMcLaren) { teamTag = "McLAREN"; carNum = "4"; badgeRgb = 0x00FF8000; }
        else if (isMercedes) { teamTag = (accCol == 0xFF1801) ? "McLAREN-MERC" : "MERCEDES"; carNum = "44"; badgeRgb = 0x0000F5D4; }
        else if (isAston) { teamTag = "ASTON MARTIN"; carNum = "14"; badgeRgb = 0x0000E698; }
        else if (isMp4) { teamTag = "McLAREN MP4/4"; carNum = "12"; badgeRgb = 0x00FF1801; }
        else if (isLotus) { teamTag = "LOTUS 97T"; carNum = "12"; badgeRgb = 0x00FFD700; }
        else if (isRenault) { teamTag = "RENAULT"; carNum = "5"; badgeRgb = 0x00FFE600; }
        else if (isWilliams) { teamTag = "WILLIAMS"; carNum = "5"; badgeRgb = 0x00FFD000; }
        else if (isBrawn) { teamTag = "BRAWN GP"; carNum = "22"; badgeRgb = 0x00D8FF00; }
        else if (isPink) { teamTag = "RACING POINT"; carNum = "11"; badgeRgb = 0x00FF66B2; }

        // 1. Titanium Underfloor Skid-Block Sparks & Exhaust Flame
        if (sparksAndFlames) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(px(2.0f * s));
            for (int sp = 0; sp < 6; sp++) {
                float prog = ((t * 4.5f + sp * 0.17f) % 1.0f);
                float sx = x - dir * px(28f * s + prog * 54f * s);
                float sy = y + px(2f * s) - (float) Math.abs(Math.sin(t * 16f + sp * 2.1f)) * px(14f * s * prog);
                int spAlpha = Math.round(alpha * (1f - prog));
                paint.setColor((spAlpha << 24) | (sp % 2 == 0 ? 0x00FFE868 : 0x00FF6D00));
                c.drawLine(sx, sy, sx - dir * px(9f * s), sy - px(2f * s), paint);
            }
            // Blinking red FIA ERS rain light at rear diffuser
            paint.setStyle(Paint.Style.FILL);
            if (((int) (t * 12f)) % 2 == 0) {
                paint.setColor((alpha << 24) | 0x00FF1801);
                c.drawCircle(x - dir * px(44f * s), y - px(5f * s), px(3.2f * s), paint);
            }
        }

        // 2. Carbon Venturi Underfloor Plank & Sidepod Skirt
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x0012151E);
        float flL = Math.min(x - dir * px(42f * s), x + dir * px(46f * s));
        float flR = Math.max(x - dir * px(42f * s), x + dir * px(46f * s));
        c.drawRoundRect(flL, y - px(4f * s), flR, y + px(1f * s), px(2f * s), px(2f * s), paint);

        // 3. Sculpted Monocoque Chassis, Engine Cover Shark-Fin, Sidepod & Low Nose Cone
        paint.setColor((alpha << 24) | bodyCol);
        actorPath.reset();
        actorPath.moveTo(x - dir * px(38f * s), y - px(3f * s)); // Rear gearbox
        actorPath.lineTo(x - dir * px(36f * s), y - px(19f * s)); // Rear engine cover
        actorPath.lineTo(x - dir * px(10f * s), y - px(classicOpenCockpit ? 26f * s : 24f * s)); // Roll-hoop airbox peak
        actorPath.lineTo(x - dir * px(4f * s), y - px(14f * s));  // Cockpit opening rear
        actorPath.lineTo(x + dir * px(12f * s), y - px(13f * s)); // Cockpit front cowling
        actorPath.lineTo(x + dir * px(48f * s), y - px(4f * s));  // Sleek low nose tip
        actorPath.lineTo(x + dir * px(44f * s), y - px(1f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 4. Team-Specific Authentic Multi-Color Livery Patterns (so each team looks unmistakable!)
        if (isRedBull) {
            // Oracle Red Bull RB20: Bright Sun-Yellow Nose Cone Tip + Tall Yellow Airbox Roll-Hoop + Charging Red Bull on Engine Cover
            paint.setColor((alpha << 24) | 0x00FFD800);
            actorPath.reset();
            actorPath.moveTo(x + dir * px(32f * s), y - px(8f * s));
            actorPath.lineTo(x + dir * px(48f * s), y - px(4f * s));
            actorPath.lineTo(x + dir * px(44f * s), y - px(1f * s));
            actorPath.lineTo(x + dir * px(30f * s), y - px(2f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
            // Yellow Airbox Crown
            actorPath.reset();
            actorPath.moveTo(x - dir * px(22f * s), y - px(20f * s));
            actorPath.lineTo(x - dir * px(10f * s), y - px(24f * s));
            actorPath.lineTo(x - dir * px(5f * s), y - px(16f * s));
            actorPath.lineTo(x - dir * px(20f * s), y - px(14f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
            // Charging Crimson Bull Silhouette on Engine Cover & Sidepod Red Stripe
            paint.setColor((alpha << 24) | 0x00FF1828);
            c.drawOval(rectF(x - dir * px(24f * s), y - px(19f * s), x - dir * px(8f * s), y - px(12f * s)), paint);
            c.drawRect(Math.min(x - dir * px(28f * s), x + dir * px(26f * s)), y - px(6f * s),
                       Math.max(x - dir * px(28f * s), x + dir * px(26f * s)), y - px(3.5f * s), paint);
        } else if (isFerrari) {
            // Scuderia Ferrari SF-24: White + Giallo Modena Yellow Diagonal Engine-Cover Slash & Scuderia Shield
            paint.setColor((alpha << 24) | 0x00FFFFFF);
            actorPath.reset();
            actorPath.moveTo(x - dir * px(32f * s), y - px(19f * s));
            actorPath.lineTo(x - dir * px(12f * s), y - px(23f * s));
            actorPath.lineTo(x - dir * px(18f * s), y - px(11f * s));
            actorPath.lineTo(x - dir * px(34f * s), y - px(11f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
            paint.setColor((alpha << 24) | 0x00FFF200);
            c.drawLine(x - dir * px(32f * s), y - px(17f * s), x + dir * px(26f * s), y - px(7f * s), paint);
            c.drawCircle(x + dir * px(16f * s), y - px(10f * s), px(2.6f * s), paint); // Giallo Modena Scuderia Shield
        } else if (isMcLaren) {
            // McLaren MCL38: Anthracite Stealth Carbon Sidepod Belly + Electric Cyan Swoosh + Papaya Nose & Engine Cover
            paint.setColor((alpha << 24) | 0x001C222E);
            actorPath.reset();
            actorPath.moveTo(x - dir * px(34f * s), y - px(4f * s));
            actorPath.lineTo(x - dir * px(16f * s), y - px(14f * s));
            actorPath.lineTo(x + dir * px(18f * s), y - px(9f * s));
            actorPath.lineTo(x + dir * px(12f * s), y - px(3f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.2f * s));
            paint.setColor((alpha << 24) | 0x0018C8FF);
            c.drawLine(x - dir * px(28f * s), y - px(9f * s), x + dir * px(24f * s), y - px(6f * s), paint);
            paint.setStyle(Paint.Style.FILL);
        } else if (isMercedes) {
            // Mercedes-AMG W15: Obsidian Carbon Rear Engine Cover + Dual Glowing Petronas Teal Flow Stripes
            paint.setColor((alpha << 24) | 0x00181E28);
            actorPath.reset();
            actorPath.moveTo(x - dir * px(38f * s), y - px(4f * s));
            actorPath.lineTo(x - dir * px(36f * s), y - px(19f * s));
            actorPath.lineTo(x - dir * px(8f * s), y - px(22f * s));
            actorPath.lineTo(x - dir * px(2f * s), y - px(5f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.2f * s));
            paint.setColor((alpha << 24) | accCol);
            c.drawLine(x - dir * px(34f * s), y - px(10f * s), x + dir * px(36f * s), y - px(6f * s), paint);
            c.drawLine(x - dir * px(30f * s), y - px(6.5f * s), x + dir * px(28f * s), y - px(4f * s), paint);
            paint.setStyle(Paint.Style.FILL);
        } else if (isMp4) {
            // 1988 McLaren Honda MP4/4: Marlboro Day-Glo Red Engine Cover & Nose Triangle Chevron over Pure White
            paint.setColor((alpha << 24) | 0x00FF1801);
            actorPath.reset();
            actorPath.moveTo(x - dir * px(36f * s), y - px(19f * s));
            actorPath.lineTo(x - dir * px(10f * s), y - px(26f * s));
            actorPath.lineTo(x - dir * px(6f * s), y - px(13f * s));
            actorPath.lineTo(x - dir * px(36f * s), y - px(11f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
            c.drawRect(Math.min(x + dir * px(16f * s), x + dir * px(36f * s)), y - px(9f * s),
                       Math.max(x + dir * px(16f * s), x + dir * px(36f * s)), y - px(5f * s), paint);
        } else if (isLotus) {
            // 1985 Lotus 97T: Double JPS Gold Pinstriping Outlining the Black Chassis
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(1.8f * s));
            paint.setColor((alpha << 24) | 0x00FFD700);
            c.drawLine(x - dir * px(35f * s), y - px(17f * s), x - dir * px(10f * s), y - px(24f * s), paint);
            c.drawLine(x - dir * px(34f * s), y - px(8f * s), x + dir * px(40f * s), y - px(5f * s), paint);
            paint.setStyle(Paint.Style.FILL);
        } else {
            // Aston Martin / Renault / Williams / Brawn GP: Bold Two-Tone Sidepod & Engine Cover Wedge
            paint.setColor((alpha << 24) | accCol);
            actorPath.reset();
            actorPath.moveTo(x - dir * px(32f * s), y - px(6f * s));
            actorPath.lineTo(x - dir * px(12f * s), y - px(18f * s));
            actorPath.lineTo(x + dir * px(16f * s), y - px(10f * s));
            actorPath.lineTo(x + dir * px(38f * s), y - px(5f * s));
            actorPath.lineTo(x - dir * px(28f * s), y - px(4f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
        }

        // 5. Driver Helmet & Curved Titanium HALO Cockpit Protection Bar (Open Cockpit for 1980s MP4/4 & Lotus 97T!)
        int helmetCol = (isMp4 || isLotus) ? 0x00FFE600 : (isFerrari ? 0x00FF2800 : (isMercedes ? 0x00FFD700 : accCol));
        paint.setColor((alpha << 24) | helmetCol);
        c.drawCircle(x + dir * px(1f * s), y - px(16.5f * s), px(4.6f * s), paint);
        paint.setColor((alpha << 24) | 0x00141822);
        c.drawRect(Math.min(x + dir * px(1f * s), x + dir * px(5.5f * s)), y - px(18f * s),
                   Math.max(x + dir * px(1f * s), x + dir * px(5.5f * s)), y - px(15.5f * s), paint);
        if (!classicOpenCockpit) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.4f * s));
            paint.setColor((alpha << 24) | (isFerrari ? 0x00FF1801 : (isMcLaren ? 0x00FF8000 : 0x00DCE2EC)));
            c.drawLine(x - dir * px(5f * s), y - px(15f * s), x + dir * px(11f * s), y - px(18.5f * s), paint);
            c.drawLine(x + dir * px(11f * s), y - px(18.5f * s), x + dir * px(13f * s), y - px(13f * s), paint);
        }

        // 6. Dual-Plane Rear Wing with Open/Closed DRS Flap + Front Wing Endplates
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | (isFerrari ? 0x00FFFFFF : bodyCol));
        float rwL = Math.min(x - dir * px(43f * s), x - dir * px(34f * s));
        float rwR = Math.max(x - dir * px(43f * s), x - dir * px(34f * s));
        c.drawRoundRect(rwL, y - px(25f * s), rwR, y - px(6f * s), px(2f * s), px(2f * s), paint);
        // DRS Top Flap (tilted open in green/white when DRS is active!)
        paint.setColor((alpha << 24) | (drsOpen ? 0x0000E676 : (isRedBull ? 0x00FF1828 : accCol)));
        c.drawRect(rwL - px(1f * s), y - px(drsOpen ? 28f * s : 25f * s), rwR + px(1f * s), y - px(22f * s), paint);

        // Front multi-element wing
        float fwL = Math.min(x + dir * px(38f * s), x + dir * px(51f * s));
        float fwR = Math.max(x + dir * px(38f * s), x + dir * px(51f * s));
        paint.setColor((alpha << 24) | (isFerrari ? 0x00FFFFFF : accCol));
        c.drawRoundRect(fwL, y - px(6f * s), fwR, y, px(2f * s), px(2f * s), paint);

        // 7. 18-Inch Front & Rear F1 Wheels with Glowing Brake Discs & Pirelli Color-Coded Sidewalls
        float rearWheelX = x - dir * px(26f * s);
        float frontWheelX = x + dir * px(28f * s);
        float wheelY = y - px(6.5f * s);
        float wheelR = px(9.5f * s);
        float spinDeg = (t * 540f) % 360f;

        for (int wIdx = 0; wIdx < 2; wIdx++) {
            float wx = (wIdx == 0) ? rearWheelX : frontWheelX;
            // Black Pirelli tire rubber
            paint.setStyle(Paint.Style.FILL);
            paint.setColor((alpha << 24) | 0x0012151C);
            c.drawCircle(wx, wheelY, wheelR, paint);
            // Glowing orange-red carbon-ceramic brake rotor inside wheel rim
            paint.setColor((alpha * 4 / 5 << 24) | (isLotus ? 0x00FFD700 : 0x00FF5500));
            c.drawCircle(wx, wheelY, wheelR * 0.42f, paint);
            // Spinning Pirelli P-Zero color-coded sidewall ring
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.2f * s));
            paint.setColor((alpha << 24) | tireCol);
            c.drawArc(rectF(wx - wheelR * 0.72f, wheelY - wheelR * 0.72f, wx + wheelR * 0.72f, wheelY + wheelR * 0.72f),
                    spinDeg + wIdx * 90f, 245f, false, paint);
        }
        paint.setStyle(Paint.Style.FILL);

        // 8. Faded Floating Team Name & Driver Number Callout above the Car (so every car is immediately identifiable!)
        Typeface prevTf = paint.getTypeface();
        Paint.Align prevAlign = paint.getTextAlign();
        paint.setTypeface(f1Font != null ? f1Font : Typeface.DEFAULT_BOLD);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(px(7.8f * s));
        int tagAlpha = Math.max(0, Math.min(255, Math.round(alpha * 0.58f)));
        paint.setColor((tagAlpha << 24) | (badgeRgb & 0xFFFFFF));
        c.drawText(teamTag + " #" + carNum, x - dir * px(4f * s), y - px(29.5f * s), paint);
        paint.setTextAlign(prevAlign);
        paint.setTypeface(prevTf);
    }

    /**
     * Bespoke F1 Driver Podium Celebration & Winning Trophy Lift Renderer:
     * - Team-accurate racing overalls with racing stripes & constructor chest badge
     * - Both arms raised high overhead lifting the gleaming Grand Prix Trophy
     * - Optional high-pressure sparkling Podium Champagne spray arc & waving National/Team Flag
     */
    private void drawF1DriverTrophyLift(Canvas c, float x, float y, float t, int alpha,
                                        int suitRgb, int accentRgb, int trophyRgb,
                                        boolean champagneSpray, boolean waveFlag, int flagRgb, float s) {
        int suitCol = suitRgb & 0xFFFFFF;
        int accCol = accentRgb & 0xFFFFFF;
        int trCol = trophyRgb & 0xFFFFFF;
        float bounce = (float) Math.abs(Math.sin(t * 5.5f)) * px(4.5f * s);
        float dy = y - bounce;

        // 1. Optional Waving Team / National Flag behind driver
        if (waveFlag) {
            float wave = (float) Math.sin(t * 7.5f) * px(6f * s);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(2.0f * s));
            paint.setColor((alpha << 24) | 0x00E0E6F0);
            c.drawLine(x + px(8f * s), dy + px(14f * s), x + px(22f * s), dy - px(42f * s), paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor((alpha * 4 / 5 << 24) | (flagRgb & 0xFFFFFF));
            actorPath.reset();
            actorPath.moveTo(x + px(21f * s), dy - px(42f * s));
            actorPath.quadTo(x + px(36f * s), dy - px(46f * s) + wave, x + px(48f * s), dy - px(38f * s) - wave);
            actorPath.lineTo(x + px(45f * s), dy - px(22f * s) - wave);
            actorPath.quadTo(x + px(33f * s), dy - px(28f * s) + wave, x + px(18f * s), dy - px(26f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
        }

        // 2. Racing Suit Legs & Boots
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(5.0f * s));
        paint.setColor((alpha << 24) | suitCol);
        c.drawLine(x - px(4f * s), dy + px(6f * s), x - px(7f * s), dy + px(34f * s), paint);
        c.drawLine(x + px(4f * s), dy + px(6f * s), x + px(7f * s), dy + px(34f * s), paint);

        // 3. Racing Overalls Torso & Team Sponsor Stripe
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | suitCol);
        c.drawRoundRect(x - px(8.5f * s), dy - px(18f * s), x + px(8.5f * s), dy + px(8f * s), px(3f * s), px(3f * s), paint);
        paint.setColor((alpha << 24) | accCol);
        c.drawRect(x - px(6.5f * s), dy - px(13f * s), x + px(6.5f * s), dy - px(8.5f * s), paint);
        c.drawRect(x - px(8.5f * s), dy + px(2f * s), x + px(8.5f * s), dy + px(5.5f * s), paint);

        // 4. Both Arms Raised High in Victory Lifting the Grand Prix Trophy Overhead!
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(4.2f * s));
        paint.setColor((alpha << 24) | suitCol);
        c.drawLine(x - px(7f * s), dy - px(15f * s), x - px(9f * s), dy - px(35f * s), paint);
        c.drawLine(x + px(7f * s), dy - px(15f * s), x + px(9f * s), dy - px(35f * s), paint);

        // 5. Driver Head & Pirelli Podium Winner Cap / Helmet
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00F4D0B4);
        c.drawCircle(x, dy - px(25.5f * s), px(6.0f * s), paint);
        paint.setColor((alpha << 24) | suitCol);
        c.drawArc(rectF(x - px(6.5f * s), dy - px(32f * s), x + px(6.5f * s), dy - px(22f * s)), 180, 180, true, paint);
        paint.setColor((alpha << 24) | 0x00FFD700); // Golden laurels on winner cap visor
        c.drawRect(x - px(6f * s), dy - px(27.5f * s), x + px(7.5f * s), dy - px(25.5f * s), paint);

        // 6. Gleaming Golden Grand Prix Trophy Held High Overhead!
        float tx = x, ty = dy - px(45f * s);
        // Golden Trophy Aura Glow
        paint.setColor((alpha / 3 << 24) | trCol);
        c.drawCircle(tx, ty, px(16f * s), paint);
        // Trophy Pedestal, Stem, Sculpted Cup & Twin Handles
        paint.setColor((alpha << 24) | 0x001E222A);
        c.drawRoundRect(tx - px(5.5f * s), ty + px(7f * s), tx + px(5.5f * s), ty + px(11f * s), px(1.5f * s), px(1.5f * s), paint);
        paint.setColor((alpha << 24) | trCol);
        actorPath.reset();
        actorPath.moveTo(tx - px(8.5f * s), ty - px(9f * s));
        actorPath.lineTo(tx + px(8.5f * s), ty - px(9f * s));
        actorPath.quadTo(tx + px(6f * s), ty + px(3f * s), tx + px(2.2f * s), ty + px(7f * s));
        actorPath.lineTo(tx - px(2.2f * s), ty + px(7f * s));
        actorPath.quadTo(tx - px(6f * s), ty + px(3f * s), tx - px(8.5f * s), ty - px(9f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(1.8f * s));
        c.drawOval(rectF(tx - px(11.5f * s), ty - px(7f * s), tx + px(11.5f * s), ty + px(1f * s)), paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawCircle(tx - px(3f * s), ty - px(4f * s), px(2.0f * s), paint);

        // 7. Sparkling Podium Champagne Spray Arcs!
        if (champagneSpray) {
            for (int sp = 0; sp < 10; sp++) {
                float spProg = ((t * 2.4f + sp * 0.1f) % 1.0f);
                float dirSp = (sp % 2 == 0) ? 1f : -1f;
                float sx = tx + dirSp * spProg * px(52f * s);
                float sy = ty + px(6f * s) - (float) Math.sin(spProg * Math.PI) * px(28f * s) + spProg * spProg * px(22f * s);
                int spA = Math.round(alpha * (1f - spProg * 0.7f));
                paint.setColor((spA << 24) | (sp % 3 == 0 ? 0x00FFE898 : 0x00FFFFFF));
                c.drawCircle(sx, sy, px((2.8f - spProg * 1.2f) * s), paint);
            }
        }
    }

    /**
     * Full 1st / 2nd / 3rd Grand Prix Podium Ceremony with Trophy Lift, Dual Champagne Spray & Falling Confetti.
     */
    private void drawF1PodiumCeremony(Canvas c, float cx, float groundY, float t, int alpha,
                                      int p1Suit, int p1Acc, int p2Suit, int p3Suit, boolean confetti) {
        paint.setStyle(Paint.Style.FILL);
        // 1st (Center, Tallest), 2nd (Left), 3rd (Right) Podium Steps
        paint.setColor((alpha * 4 / 5 << 24) | 0x00181E2C);
        c.drawRoundRect(cx - px(34f), groundY - px(34f), cx + px(34f), groundY, px(4f), px(4f), paint);
        c.drawRoundRect(cx - px(98f), groundY - px(24f), cx - px(36f), groundY, px(4f), px(4f), paint);
        c.drawRoundRect(cx + px(36f), groundY - px(18f), cx + px(98f), groundY, px(4f), px(4f), paint);

        // Gold/Silver/Bronze Top Strips on Podium Steps
        paint.setColor((alpha << 24) | 0x00FFD700);
        c.drawRect(cx - px(34f), groundY - px(34f), cx + px(34f), groundY - px(29f), paint);
        paint.setColor((alpha << 24) | 0x00DCE2EC);
        c.drawRect(cx - px(98f), groundY - px(24f), cx - px(36f), groundY - px(20f), paint);
        paint.setColor((alpha << 24) | 0x00E09048);
        c.drawRect(cx + px(36f), groundY - px(18f), cx + px(98f), groundY - px(14f), paint);

        // P2 (Left) & P3 (Right) Drivers celebrating & spraying champagne toward P1
        drawF1DriverTrophyLift(c, cx - px(67f), groundY - px(58f), t + 0.4f, alpha,
                p2Suit, 0x00FFFFFF, 0x00DCE2EC, true, false, 0, 1.08f);
        drawF1DriverTrophyLift(c, cx + px(67f), groundY - px(52f), t + 0.8f, alpha,
                p3Suit, 0x00FFFFFF, 0x00E09048, true, false, 0, 1.05f);
        // P1 Race Winner on Center Step Lifting the Gold Grand Prix Trophy!
        drawF1DriverTrophyLift(c, cx, groundY - px(72f), t, alpha,
                p1Suit, p1Acc, 0x00FFD700, true, false, 0, 1.28f);

        // Falling Gold & Crimson Victory Confetti
        if (confetti) {
            for (int cf = 0; cf < 14; cf++) {
                float cxPos = cx + (cf - 6.5f) * px(20f) + (float) Math.sin(t * 3f + cf) * px(8f);
                float cyPos = groundY - px(135f) + ((t * 48f + cf * 17f) % px(125f));
                paint.setColor(((alpha * 3 / 4) << 24) | (cf % 2 == 0 ? 0x00FFD700 : 0x00FF1801));
                c.drawRect(cxPos, cyPos, cxPos + px(4.5f), cyPos + px(3.0f), paint);
            }
        }
    }

    /**
     * 1.8-Second World Record Pit Stop Crew with Overhead Pit Rig, Wheel-Gun Sparks & Jack Crew.
     */
    private void drawF1PitStopCrew(Canvas c, float cx, float groundY, float t, float p, int alpha, int bodyCol, int accCol) {
        // Overhead Carbon Pit Gantry Boom & Compressed Air Hoses
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(3.5f));
        paint.setColor((alpha * 4 / 5 << 24) | 0x00384458);
        c.drawLine(cx - px(75f), groundY - px(78f), cx + px(75f), groundY - px(78f), paint);
        c.drawLine(cx - px(32f), groundY - px(78f), cx - px(28f), groundY - px(22f), paint);
        c.drawLine(cx + px(32f), groundY - px(78f), cx + px(28f), groundY - px(22f), paint);
        paint.setStyle(Paint.Style.FILL);

        float carOffset = p < 0.72f ? 0f : ((p - 0.72f) / 0.28f) * px(140f);
        drawF1Car(c, cx + carOffset, groundY - px(11f), t * 3f, alpha,
                bodyCol, accCol, 0x00FF1801, false, p >= 0.72f, false, 1.32f);

        // Pit Crew Mechanics kneeling at front & rear wheels with glowing LED wheel guns
        float[] crewX = {cx - px(62f), cx - px(30f), cx + px(30f), cx + px(62f)};
        for (int m = 0; m < 4; m++) {
            paint.setColor((alpha << 24) | bodyCol);
            c.drawRoundRect(crewX[m] - px(5f), groundY - px(26f), crewX[m] + px(5f), groundY, px(2f), px(2f), paint);
            paint.setColor((alpha << 24) | accCol);
            c.drawCircle(crewX[m], groundY - px(31f), px(4.5f), paint);
            // Flashing green/cyan wheel-gun torque indicator light
            paint.setColor((alpha << 24) | (p < 0.65f ? 0x00FF1801 : 0x0000E676));
            c.drawCircle(crewX[m], groundY - px(14f), px(3.5f), paint);
        }
    }

    /**
     * World Championship Victory Donuts with Billowing Pirelli Tire Smoke & Grand Prix Fireworks.
     */
    private void drawF1DonutCelebration(Canvas c, float cx, float groundY, float t, int alpha, int bodyCol, int accCol) {
        // Billowing White Pirelli Tire Smoke Rings behind the spinning F1 car
        paint.setStyle(Paint.Style.FILL);
        for (int sm = 0; sm < 7; sm++) {
            float sp = ((t * 1.8f + sm * 0.14f) % 1.0f);
            float sx = cx + (float) Math.cos(t * 4f + sm) * px(46f * sp);
            float sy = groundY - px(12f + sp * 42f);
            int sa = Math.round(alpha * (1f - sp) * 0.55f);
            paint.setColor((sa << 24) | 0x00E8EEF8);
            c.drawCircle(sx, sy, px(14f + sp * 22f), paint);
        }
        boolean flip = ((int) (t * 2.2f)) % 2 == 1;
        drawF1Car(c, cx + (float) Math.sin(t * 4.4f) * px(18f), groundY - px(10f), t * 4f, alpha,
                bodyCol, accCol, 0x00FF1801, false, true, flip, 1.32f);

        // Twin Championship Fireworks Bursting in the Sky Above the Circuit
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.0f));
        float[] fwX = {cx - px(72f), cx + px(72f)};
        for (int f = 0; f < 2; f++) {
            float fp = ((t * 1.4f + f * 0.5f) % 1.0f);
            int fa = Math.round(alpha * (1f - fp));
            paint.setColor((fa << 24) | (f == 0 ? 0x00FFD700 : 0x00FF1801));
            for (int r = 0; r < 8; r++) {
                double ang = r * Math.PI / 4.0;
                float r1 = fp * px(12f), r2 = fp * px(34f);
                c.drawLine(fwX[f] + (float) Math.cos(ang) * r1, groundY - px(88f) + (float) Math.sin(ang) * r1,
                           fwX[f] + (float) Math.cos(ang) * r2, groundY - px(88f) + (float) Math.sin(ang) * r2, paint);
            }
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawHogwartsExpressScene(Canvas c, int w, int h, float groundY, float t, float p, int aBase) {
        float trainX = w * (0.05f + p * 0.52f);
        float trainY = groundY - px(38f);

        // Warm golden headlight cone projecting ahead of the locomotive
        paint.setColor((Math.round(aBase * 0.30f) << 24) | 0x00FFE898);
        actorPath.reset();
        actorPath.moveTo(trainX + px(168f), trainY + px(6f));
        actorPath.lineTo(trainX + px(295f), trainY - px(26f));
        actorPath.lineTo(trainX + px(295f), trainY + px(34f));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // Billowing steam puffs rising high into the expanded stage
        for (int s = 0; s < 8; s++) {
            float sp = ((t * 2.1f + s * 0.13f) % 1.0f);
            float sx = trainX + px(152f) - sp * px(120f) - s * px(18f);
            float sy = trainY - px(28f) - sp * px(64f);
            int sa = Math.round(aBase * (1f - sp) * 0.65f);
            paint.setColor((sa << 24) | 0x00FFF2D6);
            c.drawCircle(sx, sy, px(8f + sp * 18f), paint);
        }

        // Scarlet & Gold Locomotive (Olton Hall 5972) — 1.4x larger
        paint.setColor((aBase << 24) | 0x00D8322C);
        c.drawRoundRect(trainX + px(86f), trainY - px(14f), trainX + px(168f), trainY + px(28f), px(6f), px(6f), paint);
        c.drawRoundRect(trainX + px(80f), trainY - px(26f), trainX + px(114f), trainY + px(26f), px(4f), px(4f), paint);
        c.drawRect(trainX + px(146f), trainY - px(34f), trainX + px(158f), trainY - px(10f), paint);
        paint.setColor((aBase << 24) | 0x00FFDF80);
        c.drawRect(trainX + px(88f), trainY - px(18f), trainX + px(104f), trainY - px(4f), paint);
        for (int wh = 0; wh < 3; wh++) {
            c.drawCircle(trainX + px(100f + wh * 24f), trainY + px(30f), px(7.5f), paint);
        }

        // 2 Scarlet Passenger Carriages with glowing golden compartment windows
        for (int car = 0; car < 2; car++) {
            float cx = trainX - car * px(96f);
            paint.setColor((aBase << 24) | 0x00B82420);
            c.drawRoundRect(cx, trainY - px(12f), cx + px(78f), trainY + px(26f), px(5f), px(5f), paint);
            paint.setColor((aBase << 24) | 0x00FFE688);
            for (int wIdx = 0; wIdx < 3; wIdx++) {
                float wx = cx + px(10f + wIdx * 22f);
                c.drawRoundRect(wx, trainY - px(3f), wx + px(14f), trainY + px(10f), px(2f), px(2f), paint);
            }
        }
        drawBird(c, w * (0.18f + p * 0.56f), h * 0.70f + (float) Math.sin(t * 5f) * px(14f), t * 9f, px(26f), (aBase << 24) | 0x00FFF8E8);
    }

    private void drawQuidditchScene(Canvas c, int w, int h, float groundY, float t, float p, int aBase) {
        float snitchX = w * (0.24f + p * 0.52f) + (float) Math.cos(t * 6.5f) * px(34f);
        float snitchY = h * 0.73f + (float) Math.sin(t * 7.8f) * px(28f);

        // Three Tall Golden Quidditch Goal Hoops in center pitch
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(3.0f));
        paint.setColor((aBase * 4 / 5 << 24) | 0x00F5D078);
        for (int g = -1; g <= 1; g++) {
            float gx = w * 0.50f + g * px(46f);
            float gh = g == 0 ? px(136f) : px(108f);
            c.drawLine(gx, groundY, gx, groundY - gh, paint);
            c.drawCircle(gx, groundY - gh - px(18f), px(18f), paint);
        }
        paint.setStyle(Paint.Style.FILL);

        // Golden Snitch with fluttering silver wings & golden aura
        paint.setColor((aBase / 3 << 24) | 0x00FFE066);
        c.drawCircle(snitchX, snitchY, px(18f), paint);
        paint.setColor((aBase << 24) | 0x00FFEA78);
        c.drawCircle(snitchX, snitchY, px(8.5f), paint);
        float wingFlap = (float) Math.sin(t * 28f) * px(15f);
        paint.setStrokeWidth(px(2.4f));
        paint.setColor((aBase << 24) | 0x00FFFFFF);
        c.drawLine(snitchX - px(8f), snitchY, snitchX - px(32f), snitchY - px(10f) + wingFlap, paint);
        c.drawLine(snitchX + px(8f), snitchY, snitchX + px(32f), snitchY - px(10f) + wingFlap, paint);

        // Harry on Nimbus 2000 reaching out for the Snitch
        float hx = snitchX - px(120f);
        float hy = snitchY + (float) Math.sin(t * 5.2f) * px(18f) + px(10f);
        drawBroomRider(c, hx, hy, t, aBase, 0xFFE83838, 0xFFFFE082, true, 1.45f);
        drawBroomRider(c, hx - px(165f), hy + px(30f) + (float) Math.cos(t * 4.5f) * px(14f), t + 1.5f, aBase * 4 / 5, 0xFF20B870, 0xFFC8E8D8, false, 1.40f);
    }

    private void drawPatronusScene(Canvas c, int w, int h, float groundY, float t, float p, int aBase) {
        float harryX = w * 0.25f;
        float harryY = groundY - px(66f);
        drawWizardStanding(c, harryX, harryY, t, aBase, 0xFFFFE498, true, false, 1.55f);

        float stagX = w * (0.38f + p * 0.14f);
        float stagY = groundY - px(74f) + (float) Math.abs(Math.sin(t * 5.5f)) * -px(22f);
        drawStagPatronus(c, stagX, stagY, t, aBase, 1.45f);

        paint.setStyle(Paint.Style.STROKE);
        for (int r = 0; r < 3; r++) {
            float rp = ((t * 1.4f + r * 0.33f) % 1.0f);
            int ra = Math.round(aBase * (1f - rp) * 0.82f);
            paint.setStrokeWidth(px(3.4f * (1f - rp * 0.4f)));
            paint.setColor((ra << 24) | 0x0098ECFF);
            float arcR = px(56f + rp * 235f);
            storyRect.set(harryX - arcR, harryY - arcR, harryX + arcR, harryY + arcR);
            c.drawArc(storyRect, -45, 90, false, paint);
        }
        paint.setStyle(Paint.Style.FILL);

        for (int d = 0; d < 4; d++) {
            float dx = w * (0.60f + d * 0.055f + p * 0.08f);
            float dy = h * (0.71f + (d % 2) * 0.04f) + (float) Math.sin(t * 3.2f + d) * px(14f);
            drawDementor(c, dx, dy, t + d, aBase, 1.45f);
        }
    }

    private void drawDragonScene(Canvas c, int w, int h, float groundY, float t, float p, int aBase) {
        float dragX = w * (0.60f - p * 0.12f);
        float dragY = h * 0.72f + (float) Math.sin(t * 4.0f) * px(16f);
        drawDragonCustom(c, dragX, dragY, t, aBase, 0xFFE09A48, 1.45f);

        for (int f = 0; f < 18; f++) {
            float fp = ((t * 3.2f + f * 0.055f) % 1.0f);
            float fx = dragX - px(62f) - fp * px(245f);
            float fy = dragY + px(8f) + fp * px(34f) + (float) Math.sin(t * 12f + f) * px(12f * fp);
            int fa = Math.round(aBase * (1f - fp) * 0.92f);
            int fcol = fp < 0.38f ? 0xFFFFF4A8 : (fp < 0.72f ? 0xFFFF8820 : 0xFFE02818);
            paint.setColor((fa << 24) | (fcol & 0xFFFFFF));
            c.drawCircle(fx, fy, px(6.5f + fp * 18f), paint);
        }

        float hx = dragX - px(315f) + (float) Math.cos(t * 3.5f) * px(32f);
        float hy = dragY + px(38f) + (float) Math.sin(t * 4.5f) * px(22f);
        drawBroomRider(c, hx, hy, t, aBase, 0xFFE83838, 0xFFFFE082, true, 1.45f);
    }

    private void drawFinalDuelScene(Canvas c, int w, int h, float groundY, float t, float p, int aBase) {
        float harryX = w * 0.25f;
        float voldX = w * 0.70f;
        float duelY = groundY - px(66f);

        drawWizardStanding(c, harryX, duelY, t, aBase, 0xFFFFE498, true, false, 1.55f);
        drawVoldemort(c, voldX, duelY, t, aBase, 1.55f);

        float clashRatio = 0.46f + 0.18f * p + 0.05f * (float) Math.sin(t * 11f);
        float wandLeftX = harryX + px(38f);
        float wandRightX = voldX - px(38f);
        float clashX = wandLeftX + (wandRightX - wandLeftX) * clashRatio;
        float clashY = duelY - px(28f) + (float) Math.sin(t * 16f) * px(5.0f);

        // Harry's Crimson-Gold Expelliarmus Beam
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(11.0f));
        paint.setColor((aBase / 2 << 24) | 0x00FF3828);
        c.drawLine(wandLeftX, duelY - px(28f), clashX, clashY, paint);
        paint.setStrokeWidth(px(4.5f));
        paint.setColor((aBase << 24) | 0x00FFE090);
        c.drawLine(wandLeftX, duelY - px(28f), clashX, clashY, paint);

        // Voldemort's Emerald Avada Kedavra Beam
        paint.setStrokeWidth(px(11.0f));
        paint.setColor((aBase / 2 << 24) | 0x0018E868);
        c.drawLine(wandRightX, duelY - px(28f), clashX, clashY, paint);
        paint.setStrokeWidth(px(4.5f));
        paint.setColor((aBase << 24) | 0x00A8FFD0);
        c.drawLine(wandRightX, duelY - px(28f), clashX, clashY, paint);

        // Blazing Priori Incantatem Core & Flying Sparks at Clash Point
        paint.setStyle(Paint.Style.FILL);
        float coreR = px(22f + 8f * (float) Math.sin(t * 22f));
        paint.setColor((aBase / 2 << 24) | 0x00FFD848);
        c.drawCircle(clashX, clashY, coreR * 1.9f, paint);
        paint.setColor((aBase << 24) | 0x00FFFFFF);
        c.drawCircle(clashX, clashY, coreR * 0.70f, paint);

        for (int sp = 0; sp < 14; sp++) {
            float sAng = t * 8f + sp * 0.448f;
            float sDist = ((t * 82f + sp * 11f) % 68f) * scale;
            float sx = clashX + (float) Math.cos(sAng) * sDist;
            float sy = clashY + (float) Math.sin(sAng) * sDist;
            paint.setColor((aBase << 24) | (sp % 2 == 0 ? 0x00FF6840 : 0x0048FF90));
            c.drawCircle(sx, sy, px(3.2f), paint);
        }
    }

    /**
     * Sculpted seated Tabby Cat (Professor McGonagall Animagus) with arched back, pointed ears & curling tail.
     */
    private void drawCatSilhouette(Canvas c, float x, float y, int alpha) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00FFDF88);
        // Seated cat body with arched haunches
        actorPath.reset();
        actorPath.moveTo(x - px(9f), y + px(12f));
        actorPath.quadTo(x - px(13f), y - px(4f), x - px(5f), y - px(14f));
        actorPath.lineTo(x + px(5f), y - px(14f));
        actorPath.quadTo(x + px(11f), y - px(2f), x + px(8f), y + px(12f));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Cat head & pointed ears
        c.drawCircle(x + px(1f), y - px(19f), px(6.5f), paint);
        actorPath.reset();
        actorPath.moveTo(x - px(4.5f), y - px(22f));
        actorPath.lineTo(x - px(2.5f), y - px(30f));
        actorPath.lineTo(x + px(0.5f), y - px(24f));
        actorPath.moveTo(x + px(1.5f), y - px(24f));
        actorPath.lineTo(x + px(5.0f), y - px(30f));
        actorPath.lineTo(x + px(6.5f), y - px(22f));
        c.drawPath(actorPath, paint);
        // Curling tail
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.8f));
        actorPath.reset();
        actorPath.moveTo(x - px(8f), y + px(10f));
        actorPath.quadTo(x - px(22f), y + px(6f), x - px(18f), y - px(6f));
        c.drawPath(actorPath, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Bespoke Padfoot / The Grim Hound (Sirius Black Animagus) Renderer:
     * Shaggy black hound with muscular chest, snarling wolf-hound snout, pointed ears, glowing amber eyes & tail.
     */
    private void drawGrimHound(Canvas c, float x, float groundY, float t, int alpha, float s) {
        float stride = (float) Math.sin(t * 6.5f) * px(4.5f * s);
        float y = groundY - px(26f * s);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(px(4.2f * s));
        paint.setColor((alpha << 24) | 0x001E2430);
        // 4 Articulated Hound Legs
        c.drawLine(x - px(16f * s), y + px(6f * s), x - px(20f * s) - stride, groundY, paint);
        c.drawLine(x - px(10f * s), y + px(6f * s), x - px(12f * s) + stride * 0.5f, groundY, paint);
        c.drawLine(x + px(12f * s), y + px(6f * s), x + px(14f * s) - stride * 0.5f, groundY, paint);
        c.drawLine(x + px(18f * s), y + px(6f * s), x + px(22f * s) + stride, groundY, paint);
        // Whipping Hound Tail
        c.drawLine(x - px(18f * s), y - px(2f * s), x - px(34f * s), y - px(14f * s) + stride, paint);

        // Shaggy Muscular Hound Torso, Neck, Snout & Ears
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00242C3A);
        c.drawOval(x - px(20f * s), y - px(10f * s), x + px(22f * s), y + px(10f * s), paint);
        c.drawCircle(x + px(22f * s), y - px(14f * s), px(9.5f * s), paint);
        actorPath.reset();
        actorPath.moveTo(x + px(26f * s), y - px(18f * s));
        actorPath.lineTo(x + px(38f * s), y - px(14f * s));
        actorPath.lineTo(x + px(26f * s), y - px(9f * s));
        actorPath.moveTo(x + px(18f * s), y - px(20f * s));
        actorPath.lineTo(x + px(22f * s), y - px(30f * s));
        actorPath.lineTo(x + px(26f * s), y - px(20f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Glowing Gold-Amber Grim Eye
        paint.setColor((alpha << 24) | 0x00FFE048);
        c.drawCircle(x + px(26f * s), y - px(16f * s), px(2.2f * s), paint);
    }

    /**
     * High-finesse Quidditch Seeker on a Nimbus 2000 / Firebolt broomstick:
     * Curved polished shaft, golden ferrule & stirrup, 5-bristle flared tail twigs,
     * leaning rider with bent knee, wind-whipped scalloped cloak, fluttering scarf & tousled hair.
     */
    private void drawBroomRider(Canvas c, float x, float y, float t, int alpha, int cloakRgb, int goldRgb, boolean reaching, float s) {
        float wave = (float) Math.sin(t * 10.5f) * px(4.5f * s);

        // 1. Curved ergonomic Nimbus / Firebolt broom handle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.8f * s));
        paint.setColor((alpha << 24) | (goldRgb & 0xFFFFFF));
        actorPath.reset();
        actorPath.moveTo(x - px(28f * s), y + px(4f * s));
        actorPath.quadTo(x - px(2f * s), y + px(1f * s), x + px(14f * s), y - px(3f * s));
        actorPath.quadTo(x + px(22f * s), y - px(5f * s), x + px(32f * s), y - px(4f * s));
        c.drawPath(actorPath, paint);

        // 2. Flared multi-bristle broom tail with golden binding band
        paint.setStyle(Paint.Style.FILL);
        actorPath.reset();
        actorPath.moveTo(x - px(22f * s), y + px(3f * s));
        actorPath.quadTo(x - px(35f * s), y - px(5f * s), x - px(48f * s), y - px(4f * s) + wave * 0.25f);
        actorPath.lineTo(x - px(43f * s), y + px(2f * s));
        actorPath.lineTo(x - px(50f * s), y + px(5f * s) + wave * 0.25f);
        actorPath.lineTo(x - px(42f * s), y + px(8f * s));
        actorPath.lineTo(x - px(46f * s), y + px(13f * s) + wave * 0.25f);
        actorPath.quadTo(x - px(32f * s), y + px(11f * s), x - px(22f * s), y + px(5f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 3. Rider's bent leg & boot in golden stirrup
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.6f * s));
        c.drawLine(x - px(2f * s), y - px(1f * s), x - px(9f * s), y + px(9f * s), paint);
        c.drawLine(x - px(9f * s), y + px(9f * s), x - px(4f * s), y + px(11f * s), paint);

        // 4. Billowing Quidditch Cloak with scalloped wind-whipped folds
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | (cloakRgb & 0xFFFFFF));
        actorPath.reset();
        actorPath.moveTo(x + px(9f * s), y - px(12f * s));
        actorPath.cubicTo(
                x - px(6f * s), y - px(12f * s),
                x - px(18f * s), y - px(6f * s) + wave,
                x - px(32f * s), y - px(3f * s) + wave);
        actorPath.quadTo(x - px(24f * s), y + px(2f * s), x - px(28f * s), y + px(7f * s) + wave * 0.6f);
        actorPath.quadTo(x - px(12f * s), y + px(5f * s), x + px(11f * s), y - px(2f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 5. Sculpted Seeker Head & wind-swept tousled hair spikes
        paint.setColor((alpha << 24) | (goldRgb & 0xFFFFFF));
        float hx = x + px(11f * s), hy = y - px(17f * s);
        c.drawCircle(hx, hy, px(5.6f * s), paint);
        actorPath.reset();
        actorPath.moveTo(hx - px(2f * s), hy - px(5f * s));
        actorPath.lineTo(hx - px(10f * s), hy - px(7f * s));
        actorPath.lineTo(hx - px(5f * s), hy - px(2f * s));
        actorPath.lineTo(hx - px(11f * s), hy - px(2f * s));
        actorPath.lineTo(hx - px(4f * s), hy + px(2f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 6. Outstretched Seeker Arm reaching for the Golden Snitch
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.4f * s));
        if (reaching) {
            c.drawLine(x + px(9f * s), y - px(10f * s), x + px(20f * s), y - px(13f * s), paint);
            c.drawLine(x + px(20f * s), y - px(13f * s), x + px(33f * s), y - px(17f * s), paint);
        } else {
            c.drawLine(x + px(9f * s), y - px(10f * s), x + px(21f * s), y - px(5f * s), paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * High-finesse "Tale of the Three Brothers" style articulated Wizard Silhouette:
     * - Soft backlight aura rim
     * - Two animated striding legs with knees & heeled wizard boots
     * - Layered open wizard robe with undulating cubic-curve hem & lapel highlight
     * - Streaming two-tailed House scarf
     * - Sculpted head profile with tousled hair locks & glasses glint (or curved pointed Wizard Hat & flowing beard)
     * - Articulated shoulder -> elbow -> forearm -> draped sleeve cuff -> tapered wand with star-cross Lumos flare
     */
    private void drawWizardStanding(Canvas c, float x, float y, float t, int alpha, int rgb, boolean wandGlow, boolean hatAndBeard, float s) {
        int col = rgb & 0xFFFFFF;
        float sway = (float) Math.sin(t * 4.5f) * px(4.5f * s);
        float stride = (float) Math.sin(t * 5.2f) * px(4.2f * s);

        // 1. Subtle golden-amber rim aura behind the silhouette
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((Math.max(0, alpha / 7) << 24) | col);
        c.drawCircle(x, y + px(6f * s), px(26f * s), paint);

        // 2. Articulated Striding Legs & Heeled Wizard Boots
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(3.2f * s));
        paint.setColor((alpha << 24) | col);
        // Back leg (hip -> knee -> boot -> toe)
        float bFootX = x - px(6f * s) - stride * 0.6f, bFootY = y + px(40f * s);
        c.drawLine(x - px(4f * s), y + px(18f * s), x - px(5f * s) - stride * 0.3f, y + px(30f * s), paint);
        c.drawLine(x - px(5f * s) - stride * 0.3f, y + px(30f * s), bFootX, bFootY, paint);
        c.drawLine(bFootX, bFootY, bFootX + px(5.5f * s), bFootY, paint);
        // Front leg (hip -> knee -> boot -> toe)
        float fFootX = x + px(6f * s) + stride * 0.6f, fFootY = y + px(40f * s);
        c.drawLine(x + px(4f * s), y + px(18f * s), x + px(6f * s) + stride * 0.4f, y + px(30f * s), paint);
        c.drawLine(x + px(6f * s) + stride * 0.4f, y + px(30f * s), fFootX, fFootY, paint);
        c.drawLine(fFootX, fFootY, fFootX + px(6.5f * s), fFootY, paint);

        // 3. Sculpted Layered Wizard Robe & Billowing Cloak Hem
        paint.setStyle(Paint.Style.FILL);
        actorPath.reset();
        actorPath.moveTo(x - px(7f * s), y - px(9f * s)); // Left shoulder + high collar
        actorPath.lineTo(x + px(7.5f * s), y - px(9f * s)); // Right shoulder
        actorPath.cubicTo(
                x + px(10f * s), y + px(4f * s),
                x + px(13f * s), y + px(20f * s),
                x + px(14f * s), y + px(34f * s)); // Front robe edge
        // Scalloped 3-fold wind-swept cloak hem
        actorPath.quadTo(x + px(5f * s), y + px(30f * s), x - px(2f * s) + sway * 0.4f, y + px(35f * s));
        actorPath.quadTo(x - px(10f * s) + sway * 0.7f, y + px(31f * s), x - px(20f * s) + sway, y + px(35f * s));
        actorPath.cubicTo(
                x - px(16f * s) + sway * 0.6f, y + px(18f * s),
                x - px(11f * s), y + px(2f * s),
                x - px(7f * s), y - px(9f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 4. Fluttering Two-Tailed Hogwarts Scarf streaming behind the neck
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.4f * s));
        actorPath.reset();
        actorPath.moveTo(x - px(4f * s), y - px(9f * s));
        actorPath.quadTo(x - px(15f * s), y - px(12f * s) - sway * 0.5f, x - px(25f * s), y - px(6f * s) + sway);
        actorPath.moveTo(x - px(5f * s), y - px(7f * s));
        actorPath.quadTo(x - px(14f * s), y - px(5f * s) + sway * 0.4f, x - px(22f * s), y + px(1f * s) - sway * 0.5f);
        c.drawPath(actorPath, paint);

        // 5. Sculpted Head Profile, Hair / Wizard Hat & Beard
        paint.setStyle(Paint.Style.FILL);
        float hx = x + px(0.5f * s), hy = y - px(16.5f * s);
        c.drawCircle(hx, hy, px(6.0f * s), paint);
        // Sculpted nose & chin profile facing right
        actorPath.reset();
        actorPath.moveTo(hx + px(4.5f * s), hy - px(2.5f * s));
        actorPath.lineTo(hx + px(7.8f * s), hy - px(0.5f * s));
        actorPath.lineTo(hx + px(5.2f * s), hy + px(1.5f * s));
        actorPath.lineTo(hx + px(5.8f * s), hy + px(4.2f * s));
        actorPath.lineTo(hx + px(2.0f * s), hy + px(5.2f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        if (hatAndBeard) {
            // Iconic Curved-Brim Pointed Wizard Hat with crooked tip + long flowing Dumbledore beard
            actorPath.reset();
            actorPath.moveTo(hx - px(12f * s), hy - px(3.5f * s));
            actorPath.quadTo(hx, hy - px(6.5f * s), hx + px(12f * s), hy - px(3.0f * s));
            actorPath.lineTo(hx + px(6.5f * s), hy - px(5.5f * s));
            actorPath.quadTo(hx + px(3f * s), hy - px(20f * s), hx - px(6f * s) + sway * 0.3f, hy - px(27f * s));
            actorPath.quadTo(hx - px(5f * s), hy - px(15f * s), hx - px(6.5f * s), hy - px(5.5f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
            // Long flowing silver-gold beard tapering past the waist
            actorPath.reset();
            actorPath.moveTo(hx + px(2f * s), hy + px(4f * s));
            actorPath.quadTo(hx + px(9f * s), hy + px(14f * s), hx + px(5f * s) - sway * 0.3f, hy + px(27f * s));
            actorPath.quadTo(hx - px(1f * s), hy + px(16f * s), hx - px(2f * s), hy + px(5f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
        } else {
            // Tousled Harry Potter hair spikes & subtle round glasses glint
            actorPath.reset();
            actorPath.moveTo(hx - px(5.5f * s), hy - px(2f * s));
            actorPath.lineTo(hx - px(9.5f * s), hy - px(4.5f * s));
            actorPath.lineTo(hx - px(5.0f * s), hy - px(5.0f * s));
            actorPath.lineTo(hx - px(7.5f * s), hy - px(8.5f * s));
            actorPath.lineTo(hx - px(2.0f * s), hy - px(6.5f * s));
            actorPath.lineTo(hx + px(1.5f * s), hy - px(9.5f * s));
            actorPath.lineTo(hx + px(3.5f * s), hy - px(5.8f * s));
            actorPath.lineTo(hx + px(7.2f * s), hy - px(6.8f * s));
            actorPath.lineTo(hx + px(5.0f * s), hy - px(3.0f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
            // Round spectacles glint
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(1.1f * s));
            paint.setColor((alpha << 24) | 0x00FFFFFF);
            c.drawCircle(hx + px(4.2f * s), hy - px(1.2f * s), px(2.1f * s), paint);
        }

        // 6. Articulated Casting Arm, Draped Wizard Sleeve & Tapered Wand
        float elbowX = x + px(11f * s), elbowY = y - px(3f * s);
        float handX = x + px(18f * s), handY = y - px(11f * s);
        float wandTipX = x + px(31f * s), wandTipY = y - px(21f * s);
        // Wide draped wizard sleeve hanging below the elbow
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | col);
        actorPath.reset();
        actorPath.moveTo(x + px(5f * s), y - px(7f * s));
        actorPath.lineTo(elbowX, elbowY);
        actorPath.lineTo(handX, handY);
        actorPath.lineTo(handX - px(2f * s), handY + px(9f * s));
        actorPath.quadTo(elbowX, elbowY + px(8f * s), x + px(5f * s), y + px(2f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);
        // Tapered wand shaft
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.0f * s));
        paint.setColor((alpha << 24) | 0x00FFF6D8);
        c.drawLine(handX, handY, wandTipX, wandTipY, paint);

        // 7. Multi-layered Star-Cross Lumos Spell Flare at Wand Tip
        if (wandGlow) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor((alpha / 3 << 24) | 0x00FFD868);
            c.drawCircle(wandTipX, wandTipY, px(10.5f * s), paint);
            paint.setColor((alpha * 2 / 3 << 24) | 0x00FFEFA8);
            c.drawCircle(wandTipX, wandTipY, px(5.8f * s), paint);
            paint.setColor((alpha << 24) | 0x00FFFFFF);
            c.drawCircle(wandTipX, wandTipY, px(2.8f * s), paint);
            // 4-point star cross rays
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(px(1.3f * s));
            float ray = px(8.5f * s + 2.5f * s * (float) Math.sin(t * 14f));
            c.drawLine(wandTipX - ray, wandTipY, wandTipX + ray, wandTipY, paint);
            c.drawLine(wandTipX, wandTipY - ray, wandTipX, wandTipY + ray, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * High-finesse Lord Voldemort silhouette + Nagini coiled & rearing beside him:
     * - Noseless serpentine skull profile with glowing crimson slit eye
     * - High arched Death Eater collar & tattered smoke-tendril shadow robe
     * - Skeletal arm & bony fingers wielding the bone-white Elder Wand
     * - Nagini the giant Horcrux serpent rearing right behind him
     */
    private void drawVoldemort(Canvas c, float x, float y, float t, int alpha, float s) {
        float sway = (float) Math.cos(t * 5.0f) * px(5.5f * s);

        // 1. Nagini coiled & rearing menacingly behind Voldemort
        drawSerpent(c, x + px(34f * s), y + px(40f * s), t + 0.8f, alpha * 4 / 5, 0xFF286848, 0xFF78C898, s * 0.62f, true);

        // 2. Tattered Dark-Emerald & Obsidian Shadow Cloak with writhing smoke tendrils
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00243C34);
        actorPath.reset();
        actorPath.moveTo(x - px(7f * s), y - px(9f * s));
        actorPath.lineTo(x + px(9f * s), y - px(13f * s)); // High flared dark collar
        actorPath.lineTo(x + px(7f * s), y - px(7f * s));
        actorPath.cubicTo(
                x + px(12f * s), y + px(8f * s),
                x + px(18f * s) + sway * 0.6f, y + px(24f * s),
                x + px(24f * s) + sway, y + px(40f * s));
        // 3 tattered smoke-tendril points along the bottom hem
        actorPath.quadTo(x + px(12f * s) + sway * 0.5f, y + px(31f * s), x + px(4f * s) + sway * 0.3f, y + px(41f * s));
        actorPath.quadTo(x - px(4f * s), y + px(32f * s), x - px(13f * s), y + px(39f * s));
        actorPath.cubicTo(
                x - px(11f * s), y + px(20f * s),
                x - px(9f * s), y + px(4f * s),
                x - px(7f * s), y - px(9f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // Spectral green rim-light on Voldemort's cloak front
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(1.6f * s));
        paint.setColor((alpha * 3 / 4 << 24) | 0x0078D8B0);
        c.drawLine(x - px(7f * s), y - px(9f * s), x - px(13f * s), y + px(38f * s), paint);

        // 3. Sculpted Noseless Serpentine Skull Profile (facing left)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00A4DEC4);
        float hx = x, hy = y - px(16.5f * s);
        c.drawCircle(hx + px(1f * s), hy, px(5.8f * s), paint);
        actorPath.reset();
        actorPath.moveTo(hx - px(3.5f * s), hy - px(4.5f * s));
        actorPath.lineTo(hx - px(6.8f * s), hy + px(0.5f * s)); // Flat noseless nasal bridge
        actorPath.lineTo(hx - px(5.5f * s), hy + px(4.8f * s)); // Gaunt jaw
        actorPath.lineTo(hx + px(1.5f * s), hy + px(5.5f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // Glowing crimson slit eye
        paint.setColor((alpha << 24) | 0x00FF2424);
        c.drawCircle(hx - px(3.2f * s), hy - px(1.2f * s), px(1.5f * s), paint);

        // 4. Extended Skeletal Arm, Draped Sleeve & Bone-White Elder Wand
        float elbowX = x - px(11f * s), elbowY = y - px(4f * s);
        float handX = x - px(18f * s), handY = y - px(12f * s);
        float wandTipX = x - px(32f * s), wandTipY = y - px(21f * s);
        paint.setColor((alpha << 24) | 0x00243C34);
        actorPath.reset();
        actorPath.moveTo(x - px(5f * s), y - px(7f * s));
        actorPath.lineTo(elbowX, elbowY);
        actorPath.lineTo(handX, handY);
        actorPath.lineTo(handX + px(2f * s), handY + px(9f * s));
        actorPath.quadTo(elbowX, elbowY + px(8f * s), x - px(5f * s), y + px(2f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.1f * s));
        paint.setColor((alpha << 24) | 0x00E0FFF0);
        c.drawLine(handX, handY, wandTipX, wandTipY, paint);
        paint.setStyle(Paint.Style.FILL);
        // Elder Wand nodules
        c.drawCircle((handX + wandTipX) * 0.5f, (handY + wandTipY) * 0.5f, px(1.8f * s), paint);
    }

    /**
     * High-finesse anatomical Serpent / Giant Horned Basilisk / Nagini renderer:
     * - Multi-wave S-curved tapering muscular body built from left/right normal offsets (zero allocation)
     * - Lighter ventral belly scales stripe & dorsal king-of-serpents crest spines
     * - Sculpted horned viper skull with wide-open upper & lower jaws
     * - Curved venomous fangs, darting crimson forked tongue & blazing yellow slit eye
     */
    private void drawSerpent(Canvas c, float x, float groundY, float t, int alpha, int bodyRgb, int bellyRgb, float s, boolean facingLeft) {
        float dir = facingLeft ? -1f : 1f;
        float sway = (float) Math.sin(t * 4.5f) * px(12f * s);

        // Spine control points from coiled tail on ground up to rearing neck & head
        float x0 = x - dir * px(52f * s), y0 = groundY - px(4f * s);
        float x1 = x - dir * px(14f * s), y1 = groundY - px(18f * s);
        float x2 = x + dir * px(22f * s) - sway * 0.4f, y2 = groundY - px(46f * s);
        float x3 = x - dir * px(16f * s) + sway * 0.7f, y3 = groundY - px(78f * s);
        float x4 = x + dir * px(8f * s) + sway, y4 = groundY - px(106f * s); // Head/neck pivot

        // 1. Tapering muscular serpent body (filled contour)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | (bodyRgb & 0xFFFFFF));
        actorPath.reset();
        actorPath.moveTo(x0, y0);
        actorPath.cubicTo(x1, y1 - px(7f * s), x2 + dir * px(10f * s), y2 - px(6f * s), x3 - dir * px(7f * s), y3);
        actorPath.quadTo(x4 - dir * px(6f * s), y4 + px(10f * s), x4, y4 - px(5f * s));
        actorPath.quadTo(x4 + dir * px(6f * s), y4 + px(12f * s), x3 + dir * px(8f * s), y3 + px(4f * s));
        actorPath.cubicTo(x2 + dir * px(24f * s), y2 + px(6f * s), x1 + dir * px(12f * s), y1 + px(5f * s), x0, y0);
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 2. Luminous ventral belly scale highlight stripe along the inner curve
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.4f * s));
        paint.setColor((alpha * 4 / 5 << 24) | (bellyRgb & 0xFFFFFF));
        actorPath.reset();
        actorPath.moveTo(x1, y1);
        actorPath.cubicTo(x2 + dir * px(8f * s), y2, x3 - dir * px(4f * s), y3, x4, y4);
        c.drawPath(actorPath, paint);

        // 3. Dorsal Spine Crest / Basilisk back fins along upper neck
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | (bellyRgb & 0xFFFFFF));
        for (int sp = 0; sp < 5; sp++) {
            float frac = sp / 4.0f;
            float sx = x3 + (x4 - x3) * frac - dir * px(6f * s);
            float sy = y3 + (y4 - y3) * frac;
            actorPath.reset();
            actorPath.moveTo(sx, sy + px(3f * s));
            actorPath.lineTo(sx - dir * px(7f * s), sy - px(2f * s));
            actorPath.lineTo(sx, sy - px(4f * s));
            actorPath.close();
            c.drawPath(actorPath, paint);
        }

        // 4. Sculpted Horned Basilisk Skull with Open Upper & Lower Jaws
        paint.setColor((alpha << 24) | (bodyRgb & 0xFFFFFF));
        actorPath.reset();
        // Upper skull & swept-back crown horns
        actorPath.moveTo(x4 - dir * px(4f * s), y4 - px(6f * s));
        actorPath.lineTo(x4 - dir * px(15f * s), y4 - px(14f * s)); // Crown horn tip
        actorPath.lineTo(x4 - dir * px(2f * s), y4 - px(9f * s));
        actorPath.lineTo(x4 + dir * px(18f * s), y4 - px(8f * s)); // Upper snout tip
        actorPath.lineTo(x4 + dir * px(15f * s), y4 - px(2f * s));
        actorPath.lineTo(x4 + dir * px(2f * s), y4 - px(1f * s)); // Jaw hinge
        // Gaping lower jaw
        actorPath.lineTo(x4 + dir * px(15f * s), y4 + px(8f * s)); // Lower jaw tip
        actorPath.lineTo(x4 + dir * px(11f * s), y4 + px(11f * s));
        actorPath.lineTo(x4 - dir * px(4f * s), y4 + px(4f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 5. Needle-Sharp Venomous Fangs & Flicking Crimson Forked Tongue
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(1.8f * s));
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        c.drawLine(x4 + dir * px(13f * s), y4 - px(3f * s), x4 + dir * px(12f * s), y4 + px(2.5f * s), paint);
        c.drawLine(x4 + dir * px(9f * s), y4 - px(2.5f * s), x4 + dir * px(8.5f * s), y4 + px(2.0f * s), paint);

        // Flicking forked tongue
        float tongueFlick = (float) Math.sin(t * 18f) * px(2.5f * s);
        paint.setStrokeWidth(px(1.4f * s));
        paint.setColor((alpha << 24) | 0x00FF3838);
        float tx = x4 + dir * px(24f * s), ty = y4 + px(2f * s) + tongueFlick;
        c.drawLine(x4 + dir * px(10f * s), y4 + px(2f * s), tx, ty, paint);
        c.drawLine(tx, ty, tx + dir * px(5f * s), ty - px(3f * s), paint);
        c.drawLine(tx, ty, tx + dir * px(5f * s), ty + px(3f * s), paint);

        // 6. Blazing Golden Basilisk Eye
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x00FFE820);
        c.drawCircle(x4 + dir * px(5f * s), y4 - px(5.5f * s), px(2.2f * s), paint);
    }

    /**
     * High-finesse Corporal Stag Patronus:
     * - Multi-layered silver-cyan celestial aura
     * - Sculpted proud stag chest, arched neck, tapered head & pointed ears
     * - Majestic 10-point branched royal antlers
     * - 4 articulated two-jointed galloping legs (thigh -> hock/knee -> cannon bone -> hoof)
     */
    private void drawStagPatronus(Canvas c, float x, float y, float t, int alpha, float s) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha / 4 << 24) | 0x0058D4FF);
        c.drawCircle(x, y - px(4f * s), px(38f * s), paint);
        paint.setColor((alpha * 2 / 5 << 24) | 0x0098EEFF);
        c.drawCircle(x + px(4f * s), y - px(6f * s), px(24f * s), paint);

        // Sculpted Stag Torso, Arched Neck & Head
        paint.setColor((alpha << 24) | 0x00E8FCFF);
        actorPath.reset();
        actorPath.moveTo(x - px(19f * s), y + px(6f * s)); // Rump
        actorPath.quadTo(x - px(24f * s), y + px(2f * s), x - px(26f * s), y + px(9f * s)); // Tail
        actorPath.lineTo(x - px(18f * s), y + px(9f * s));
        actorPath.quadTo(x, y + px(11f * s), x + px(16f * s), y + px(8f * s)); // Barrel chest
        actorPath.quadTo(x + px(22f * s), y - px(4f * s), x + px(20f * s), y - px(20f * s)); // Proud arched neck
        actorPath.lineTo(x + px(29f * s), y - px(21f * s)); // Tapered snout
        actorPath.lineTo(x + px(28f * s), y - px(25f * s));
        actorPath.lineTo(x + px(17f * s), y - px(26f * s)); // Crown of head
        actorPath.lineTo(x + px(12f * s), y - px(29f * s)); // Swept ear
        actorPath.lineTo(x + px(14f * s), y - px(23f * s));
        actorPath.quadTo(x + px(11f * s), y - px(10f * s), x + px(8f * s), y - px(4f * s)); // Withers
        actorPath.quadTo(x - px(6f * s), y - px(3f * s), x - px(19f * s), y + px(6f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // Majestic 10-Point Branched Royal Antlers
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.2f * s));
        paint.setColor((alpha << 24) | 0x00FFFFFF);
        // Main antler beams
        c.drawLine(x + px(17f * s), y - px(25f * s), x + px(5f * s), y - px(46f * s), paint);
        c.drawLine(x + px(18f * s), y - px(25f * s), x + px(24f * s), y - px(44f * s), paint);
        // Tines on left beam
        c.drawLine(x + px(13f * s), y - px(32f * s), x + px(19f * s), y - px(39f * s), paint);
        c.drawLine(x + px(9f * s), y - px(39f * s), x + px(1f * s), y - px(42f * s), paint);
        c.drawLine(x + px(7f * s), y - px(42f * s), x + px(11f * s), y - px(49f * s), paint);
        // Tines on right beam
        c.drawLine(x + px(21f * s), y - px(34f * s), x + px(28f * s), y - px(38f * s), paint);
        c.drawLine(x + px(23f * s), y - px(40f * s), x + px(18f * s), y - px(47f * s), paint);

        // 4 Articulated Two-Jointed Galloping Legs
        float stride = (float) Math.sin(t * 10f) * px(8.5f * s);
        paint.setStrokeWidth(px(2.4f * s));
        // Hind leg 1
        float hk1x = x - px(16f * s) - stride * 0.5f, hk1y = y + px(18f * s);
        c.drawLine(x - px(15f * s), y + px(7f * s), hk1x, hk1y, paint);
        c.drawLine(hk1x, hk1y, hk1x - px(5f * s) - stride * 0.5f, y + px(30f * s), paint);
        // Hind leg 2
        float hk2x = x - px(10f * s) + stride * 0.4f, hk2y = y + px(18f * s);
        c.drawLine(x - px(11f * s), y + px(7f * s), hk2x, hk2y, paint);
        c.drawLine(hk2x, hk2y, hk2x + stride * 0.3f, y + px(29f * s), paint);
        // Foreleg 1
        float fk1x = x + px(14f * s) + stride * 0.6f, fk1y = y + px(17f * s);
        c.drawLine(x + px(13f * s), y + px(7f * s), fk1x, fk1y, paint);
        c.drawLine(fk1x, fk1y, fk1x + px(5f * s) + stride * 0.4f, y + px(29f * s), paint);
        // Foreleg 2
        float fk2x = x + px(9f * s) - stride * 0.4f, fk2y = y + px(17f * s);
        c.drawLine(x + px(9f * s), y + px(7f * s), fk2x, fk2y, paint);
        c.drawLine(fk2x, fk2y, fk2x - px(3f * s) - stride * 0.4f, y + px(28f * s), paint);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * High-finesse Azkaban Dementor:
     * - Deep cowled hood with dark abyssal void face
     * - Tattered multi-ribbon spectral cloak streaming in the wind
     * - Extended skeletal hands with bony fingers
     */
    private void drawDementor(Canvas c, float x, float y, float t, int alpha, float s) {
        float billow = (float) Math.sin(t * 4.8f) * px(7.5f * s);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | 0x002C384E);

        // Cowled hood & 5 tattered trailing cloak ribbons
        actorPath.reset();
        actorPath.moveTo(x - px(4f * s), y - px(26f * s)); // Pointed hood peak
        actorPath.quadTo(x + px(8f * s), y - px(22f * s), x + px(11f * s), y - px(8f * s));
        actorPath.cubicTo(
                x + px(15f * s), y + px(6f * s),
                x + px(20f * s) + billow, y + px(20f * s),
                x + px(24f * s) + billow, y + px(35f * s));
        actorPath.quadTo(x + px(14f * s) + billow * 0.6f, y + px(21f * s), x + px(9f * s) + billow * 0.5f, y + px(37f * s));
        actorPath.quadTo(x + px(3f * s), y + px(22f * s), x - px(2f * s) + billow * 0.4f, y + px(34f * s));
        actorPath.quadTo(x - px(7f * s), y + px(20f * s), x - px(13f * s) + billow * 0.7f, y + px(33f * s));
        actorPath.cubicTo(
                x - px(14f * s), y + px(14f * s),
                x - px(12f * s), y - px(10f * s),
                x - px(4f * s), y - px(26f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // Dark void inside the hood
        paint.setColor((alpha << 24) | 0x000E131D);
        c.drawOval(x - px(9f * s), y - px(21f * s), x - px(2f * s), y - px(11f * s), paint);

        // Outstretched grey skeletal hands
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(1.8f * s));
        paint.setColor((alpha << 24) | 0x00788CA8);
        c.drawLine(x - px(8f * s), y - px(8f * s), x - px(20f * s), y - px(11f * s) + billow * 0.2f, paint);
        c.drawLine(x - px(20f * s), y - px(11f * s) + billow * 0.2f, x - px(24f * s), y - px(13f * s), paint);
        c.drawLine(x - px(20f * s), y - px(11f * s) + billow * 0.2f, x - px(24f * s), y - px(9f * s), paint);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * High-finesse Hungarian Horntail / Ukrainian Ironbelly Dragon:
     * - S-curved muscular neck, arched spine & long whipping tail tipped with a spiked club
     * - Swept-back dorsal ridge spikes along neck and tail
     * - Horned dragon skull with open roaring jaws & molten eye
     * - Articulated bat-like wing with wing-thumb claw, 4 radiating finger spars & scalloped membranes
     */
    private void drawDragonCustom(Canvas c, float x, float y, float t, int alpha, int rgb, float s) {
        int col = rgb & 0xFFFFFF;
        float flap = (float) Math.sin(t * 9.0f) * px(28f * s);
        float tailWave = (float) Math.cos(t * 7.0f) * px(12f * s);

        // 1. Sinuous Dragon Body, S-Curved Neck & Whipping Tail
        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha << 24) | col);
        actorPath.reset();
        // Neck -> Back -> Whipping Tail tip -> Belly -> Chest
        actorPath.moveTo(x - px(36f * s), y - px(2f * s)); // Throat
        actorPath.quadTo(x - px(22f * s), y - px(14f * s), x - px(4f * s), y - px(11f * s)); // Shoulder
        actorPath.quadTo(x + px(18f * s), y - px(10f * s), x + px(34f * s), y - px(2f * s)); // Haunches
        actorPath.quadTo(x + px(56f * s), y + px(4f * s), x + px(82f * s), y - px(8f * s) + tailWave); // Tail tip
        // Spiked tail spade
        actorPath.lineTo(x + px(90f * s), y - px(13f * s) + tailWave);
        actorPath.lineTo(x + px(86f * s), y - px(2f * s) + tailWave);
        actorPath.quadTo(x + px(54f * s), y + px(12f * s), x + px(28f * s), y + px(11f * s));
        actorPath.quadTo(x, y + px(16f * s), x - px(22f * s), y + px(9f * s)); // Muscular chest
        actorPath.quadTo(x - px(30f * s), y + px(4f * s), x - px(36f * s), y - px(2f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 2. Sculpted Horned Dragon Skull & Open Roaring Jaws (facing left)
        actorPath.reset();
        actorPath.moveTo(x - px(34f * s), y - px(6f * s));
        actorPath.lineTo(x - px(22f * s), y - px(16f * s)); // Swept cranial horn
        actorPath.lineTo(x - px(37f * s), y - px(9f * s));
        actorPath.lineTo(x - px(54f * s), y - px(5f * s)); // Upper snout tip
        actorPath.lineTo(x - px(40f * s), y - px(1f * s)); // Jaw hinge
        actorPath.lineTo(x - px(52f * s), y + px(7f * s)); // Open lower jaw tip
        actorPath.lineTo(x - px(34f * s), y + px(4f * s));
        actorPath.close();
        c.drawPath(actorPath, paint);

        // 3. Hind Leg & Talons
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(3.0f * s));
        c.drawLine(x + px(16f * s), y + px(10f * s), x + px(10f * s), y + px(22f * s), paint);
        c.drawLine(x + px(10f * s), y + px(22f * s), x + px(4f * s), y + px(25f * s), paint);

        // 4. Bat-Like Ribbed Wing with 4 Finger Spars & Scalloped Webbing
        float wx0 = x - px(8f * s), wy0 = y - px(8f * s); // Wing shoulder
        float wxWrist = x + px(12f * s), wyWrist = y - px(48f * s) + flap; // Wing wrist apex
        float f1x = x - px(4f * s), f1y = y - px(36f * s) + flap * 0.85f;
        float f2x = x + px(22f * s), f2y = y - px(40f * s) + flap * 0.9f;
        float f3x = x + px(38f * s), f3y = y - px(26f * s) + flap * 0.65f;
        float f4x = x + px(24f * s), f4y = y - px(6f * s);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor((alpha * 4 / 5 << 24) | col);
        actorPath.reset();
        actorPath.moveTo(wx0, wy0);
        actorPath.lineTo(f1x, f1y);
        actorPath.lineTo(wxWrist, wyWrist);
        actorPath.quadTo(x + px(16f * s), wyWrist + px(8f * s), f2x, f2y);
        actorPath.quadTo(x + px(28f * s), f2y + px(10f * s), f3x, f3y);
        actorPath.quadTo(x + px(28f * s), f3y + px(12f * s), f4x, f4y);
        actorPath.close();
        c.drawPath(actorPath, paint);

        // Wing bone spars highlight
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.0f * s));
        paint.setColor((alpha << 24) | 0x00FFF0C8);
        c.drawLine(wx0, wy0, wxWrist, wyWrist, paint);
        c.drawLine(wxWrist, wyWrist, f2x, f2y, paint);
        c.drawLine(wxWrist, wyWrist, f3x, f3y, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    /**
     * Sculpted Owl / Phoenix in flight with cambered double-jointed wings, head & fanned tail feathers.
     */
    private void drawBird(Canvas c, float x, float y, float phase, float span, int color) {
        float flap = (float) Math.sin(phase) * (span * 0.48f);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        // Bird head, torso & fanned tail
        c.drawOval(x - span * 0.22f, y - span * 0.10f, x + span * 0.22f, y + span * 0.12f, paint);
        // Cambered wings
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(px(2.6f));
        actorPath.reset();
        actorPath.moveTo(x - span, y - flap);
        actorPath.quadTo(x - span * 0.45f, y - span * 0.28f - flap * 0.3f, x, y);
        actorPath.quadTo(x + span * 0.45f, y - span * 0.28f - flap * 0.3f, x + span, y - flap);
        c.drawPath(actorPath, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    static int themeAccentFor(String key) {
        if (key == null) return 0xFFE83838;
        String k = key.toLowerCase();
        if (k.contains("youtube")) return 0xFFE83038;
        if (k.contains("surfshark")) return 0xFF18C878;
        if (k.contains("oumi") || k.contains("vidhub") || k.contains("hulu")) return 0xFF3282FF;
        if (k.contains("plex")) return 0xFFFFB020;
        if (k.contains("hotstar")) return 0xFFA848FF;
        if (k.contains("apple")) return 0xFF38D4E8;
        if (k.contains("vending")) return 0xFFD88828;
        if (k.contains("hw17") || k.contains("hdmi300004")) return 0xFF28E8D8;
        return 0xFFE8B848;
    }

    static boolean isCustomIconPackApp(String pkg) {
        if (pkg == null) return false;
        String k = pkg.toLowerCase();
        return k.contains("youtube") || k.contains("surfshark")
                || k.contains("oumi") || k.contains("vidhub") || k.contains("hulu")
                || k.contains("plex") || k.contains("hotstar") || k.contains("disney");
    }

    static float getCardContentScale(int w, int h) {
        float base = w / 324f;
        return w < 280 ? Math.max(base, Math.min(w, h) / 205f) : Math.max(0.45f, base);
    }

    static float getMedallionRadius(int w, int h) {
        return getMedallionRadius(w, h, false);
    }

    static float getMedallionRadius(int w, int h, boolean horizontal) {
        if (horizontal) {
            return Math.min(h * 0.28f, w * 0.14f);
        }
        float cs = getCardContentScale(w, h);
        float er = Math.min(24f * cs, h * 0.22f);
        return er * 1.15f;
    }

    static float getMedallionCenterX(int w, int h, boolean horizontal) {
        if (horizontal) {
            float cardScale = Math.max(0.35f, w / 324f);
            float ir = getMedallionRadius(w, h, true);
            return 27f * cardScale + ir;
        }
        return w * 0.5f;
    }

    static float getMedallionCenterY(int w, int h, boolean horizontal) {
        return horizontal ? (h * 0.50f) : (h * 0.34f);
    }

    static Drawable createThemeBanner(Resources res, String pkg, String label,
                                      int w, int h, float scale,
                                      Typeface themeFont, Typeface cinzelFont) {
        return createThemeBanner(res, pkg, label, w, h, scale, themeFont, cinzelFont, 0, null, 0, false);
    }

    static Drawable createThemeBanner(Resources res, String pkg, String label,
                                      int w, int h, float scale,
                                      Typeface themeFont, Typeface cinzelFont, int theme,
                                      Drawable defaultIcon, int slotIndex) {
        return createThemeBanner(res, pkg, label, w, h, scale, themeFont, cinzelFont, theme, defaultIcon, slotIndex, false);
    }

    static Drawable createThemeBanner(Resources res, String pkg, String label,
                                      int w, int h, float scale,
                                      Typeface themeFont, Typeface cinzelFont, int theme,
                                      Drawable defaultIcon, int slotIndex, boolean horizontalLayout) {
        int bw = Math.max(1, w), bh = Math.max(1, h);
        Bitmap bm = Bitmap.createBitmap(bw, bh, Bitmap.Config.ARGB_8888);
        if (res != null) bm.setDensity(res.getDisplayMetrics().densityDpi);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setDither(true);

        float cardScale = Math.max(0.35f, bw / 324f);
        float contentScale = getCardContentScale(bw, bh);
        String k = pkg == null ? "" : pkg.toLowerCase();
        int packSlot = -1;
        if (k.contains("youtube")) packSlot = 0;
        else if (k.contains("surfshark")) packSlot = 1;
        else if (k.contains("oumi") || k.contains("vidhub") || k.contains("hulu")) packSlot = 2;
        else if (k.contains("plex")) packSlot = 3;
        else if (k.contains("hotstar") || k.contains("disney")) packSlot = 4;

        boolean squareBadge = Math.abs(bw - bh) <= 4;
        boolean useCustomPackIcon = (packSlot >= 0) && !squareBadge && !horizontalLayout;
        int styleSlot = (packSlot >= 0) ? packSlot : (Math.abs(slotIndex) % 5);

        int topBg, botBg, glowCol, emblemType;
        String houseSub;
        if (theme == 1) {
            // Theme 2: NARUTO App Banners
            if (styleSlot == 0) {
                topBg = 0xFF2A070C; botBg = 0xFF120306; glowCol = 0x60FF2430;
                houseSub = "UCHIHA CLAN  |  SHARINGAN"; emblemType = 101;
            } else if (styleSlot == 1) {
                topBg = 0xFF0A2416; botBg = 0xFF04100A; glowCol = 0x5828D878;
                houseSub = "HIDDEN LEAF  |  WILL OF FIRE"; emblemType = 102;
            } else if (styleSlot == 2) {
                topBg = 0xFF081A34; botBg = 0xFF040B18; glowCol = 0x5838B8FF;
                houseSub = "SEVENTH HOKAGE  |  RASENGAN"; emblemType = 103;
            } else if (styleSlot == 3) {
                topBg = 0xFF2A1406; botBg = 0xFF120802; glowCol = 0x60FF7820;
                houseSub = "NINE-TAILS  |  KURAMA MODE"; emblemType = 104;
            } else {
                topBg = 0xFF220818; botBg = 0xFF0E040B; glowCol = 0x58E82048;
                houseSub = "AKATSUKI  |  TSUKUYOMI"; emblemType = 105;
            }
        } else if (theme == 2) {
            // Theme 3: DEMON SLAYER App Banners
            if (styleSlot == 0) {
                topBg = 0xFF2A0908; botBg = 0xFF120404; glowCol = 0x60FF4820;
                houseSub = "HINOKAMI KAGURA  |  SUN BREATH"; emblemType = 201;
            } else if (styleSlot == 1) {
                topBg = 0xFF06241E; botBg = 0xFF03100D; glowCol = 0x5820E8B8;
                houseSub = "MIZU NO KOKYU  |  WATER BREATH"; emblemType = 202;
            } else if (styleSlot == 2) {
                topBg = 0xFF081E2C; botBg = 0xFF040C14; glowCol = 0x5848E8FF;
                houseSub = "MUICHIRO  |  MIST HASHIRA"; emblemType = 203;
            } else if (styleSlot == 3) {
                topBg = 0xFF281C06; botBg = 0xFF120C02; glowCol = 0x60FFD828;
                houseSub = "KAMINARI  |  THUNDERCLAP"; emblemType = 204;
            } else {
                topBg = 0xFF1E0A2C; botBg = 0xFF0C0414; glowCol = 0x58C868FF;
                houseSub = "KOCHO SHINOBU  |  WISTERIA"; emblemType = 205;
            }
        } else if (theme == 3) {
            // Theme 4: STAR WARS App Banners
            if (styleSlot == 0) {
                topBg = 0xFF28060A; botBg = 0xFF100305; glowCol = 0x60FF2028;
                houseSub = "GALACTIC EMPIRE  |  DARTH VADER"; emblemType = 301;
            } else if (styleSlot == 1) {
                topBg = 0xFF08221A; botBg = 0xFF040E0A; glowCol = 0x5838E898;
                houseSub = "MANDALORIAN  |  BESKAR CLAN"; emblemType = 302;
            } else if (styleSlot == 2) {
                topBg = 0xFF071A32; botBg = 0xFF030B16; glowCol = 0x5838B8FF;
                houseSub = "JEDI ORDER  |  SKYWALKER"; emblemType = 303;
            } else if (styleSlot == 3) {
                topBg = 0xFF281606; botBg = 0xFF100902; glowCol = 0x60FF9828;
                houseSub = "REBEL ALLIANCE  |  STARBIRD"; emblemType = 304;
            } else {
                topBg = 0xFF1A0A2E; botBg = 0xFF0B0414; glowCol = 0x58B858FF;
                houseSub = "CORUSCANT  |  THE FORCE"; emblemType = 305;
            }
        } else if (theme == 4) {
            // Theme 5: MCU App Banners
            if (styleSlot == 0) {
                topBg = 0xFF2A070A; botBg = 0xFF120305; glowCol = 0x60FF2830;
                houseSub = "STARK TECH  |  MARK LXXXV"; emblemType = 401;
            } else if (styleSlot == 1) {
                topBg = 0xFF062418; botBg = 0xFF03100A; glowCol = 0x5828FF78;
                houseSub = "KAMAR-TAJ  |  TIME STONE"; emblemType = 402;
            } else if (styleSlot == 2) {
                topBg = 0xFF081A32; botBg = 0xFF040B16; glowCol = 0x5848E8FF;
                houseSub = "ASGARDIAN  |  MJOLNIR"; emblemType = 403;
            } else if (styleSlot == 3) {
                topBg = 0xFF281806; botBg = 0xFF100A02; glowCol = 0x60FFB828;
                houseSub = "AVENGERS  |  INFINITY SAGA"; emblemType = 404;
            } else {
                topBg = 0xFF1E082E; botBg = 0xFF0C0414; glowCol = 0x58B848FF;
                houseSub = "WAKANDA  |  VIBRANIUM"; emblemType = 405;
            }
        } else if (theme == 5) {
            // Theme 6: DCU App Banners
            if (styleSlot == 0) {
                topBg = 0xFF28080C; botBg = 0xFF100306; glowCol = 0x60FF2830;
                houseSub = "HOUSE OF EL  |  METROPOLIS"; emblemType = 501;
            } else if (styleSlot == 1) {
                topBg = 0xFF062416; botBg = 0xFF031009; glowCol = 0x5828FF78;
                houseSub = "SECTOR 2814  |  GREEN LANTERN"; emblemType = 502;
            } else if (styleSlot == 2) {
                topBg = 0xFF08162C; botBg = 0xFF040914; glowCol = 0x58FFE068;
                houseSub = "THE DARK KNIGHT  |  GOTHAM"; emblemType = 503;
            } else if (styleSlot == 3) {
                topBg = 0xFF281806; botBg = 0xFF100A02; glowCol = 0x60FFD838;
                houseSub = "THEMYSCIRA  |  WONDER WOMAN"; emblemType = 504;
            } else {
                topBg = 0xFF140C2C; botBg = 0xFF080514; glowCol = 0x5848A8FF;
                houseSub = "JUSTICE LEAGUE  |  WATCHTOWER"; emblemType = 505;
            }
        } else if (theme == 6) {
            // Theme 7: FORMULA 1 Constructor App Banners (Ferrari, Red Bull Racing, McLaren, Mercedes-AMG, Aston Martin)
            if (styleSlot == 0) {
                topBg = 0xFF300608; botBg = 0xFF140203; glowCol = 0x64FF1801;
                houseSub = "SCUDERIA FERRARI  |  MARANELLO"; emblemType = 601;
            } else if (styleSlot == 1) {
                topBg = 0xFF07102E; botBg = 0xFF030614; glowCol = 0x60FF1E38;
                houseSub = "RED BULL RACING  |  CHAMPIONS"; emblemType = 602;
            } else if (styleSlot == 2) {
                topBg = 0xFF2E1404; botBg = 0xFF140802; glowCol = 0x64FF8000;
                houseSub = "McLAREN F1  |  PAPAYA SPEED"; emblemType = 603;
            } else if (styleSlot == 3) {
                topBg = 0xFF052422; botBg = 0xFF02100F; glowCol = 0x6000D2BE;
                houseSub = "MERCEDES-AMG  |  SILVER ARROW"; emblemType = 604;
            } else {
                topBg = 0xFF06241A; botBg = 0xFF02100B; glowCol = 0x6000A86B;
                houseSub = "ASTON MARTIN  |  GRAND PRIX"; emblemType = 605;
            }
        } else if (theme == 7) {
            // Theme 8: CYBERPUNK 2099 App Banners (Arasaka Corp, Edgerunners Sandevistan, Netrunner Blackwall, Trauma Team, Samurai Afterlife)
            if (styleSlot == 0) {
                topBg = 0xFF2C0512; botBg = 0xFF120208; glowCol = 0x64FF0055;
                houseSub = "ARASAKA CORP  |  NIGHT CITY"; emblemType = 701;
            } else if (styleSlot == 1) {
                topBg = 0xFF052822; botBg = 0xFF02100E; glowCol = 0x6000FF9D;
                houseSub = "EDGERUNNERS  |  SANDEVISTAN"; emblemType = 702;
            } else if (styleSlot == 2) {
                topBg = 0xFF061E30; botBg = 0xFF030B14; glowCol = 0x6400F0FF;
                houseSub = "NETRUNNER  |  THE BLACKWALL"; emblemType = 703;
            } else if (styleSlot == 3) {
                topBg = 0xFF2A2204; botBg = 0xFF120E02; glowCol = 0x64FCEE09;
                houseSub = "QUADRA V-TECH  |  AFTERLIFE"; emblemType = 704;
            } else {
                topBg = 0xFF1C082E; botBg = 0xFF0B0314; glowCol = 0x60B848FF;
                houseSub = "TRAUMA TEAM  |  PLATINUM AV"; emblemType = 705;
            }
        } else if (styleSlot == 0) {
            topBg = 0xFF28090E; botBg = 0xFF110407; glowCol = 0x58E83038;
            houseSub = "GRYFFINDOR  |  PENSIEVE";
            emblemType = 1;
        } else if (styleSlot == 1) {
            topBg = 0xFF07221B; botBg = 0xFF040F0C; glowCol = 0x5418C878;
            houseSub = "SLYTHERIN  |  PROTEGO";
            emblemType = 2;
        } else if (styleSlot == 2) {
            topBg = 0xFF091830; botBg = 0xFF050B16; glowCol = 0x563282FF;
            houseSub = "RAVENCLAW  |  CHAMBER";
            emblemType = k.contains("hulu") ? 6 : 3;
        } else if (styleSlot == 3) {
            topBg = 0xFF241806; botBg = 0xFF100A03; glowCol = 0x58FFB020;
            houseSub = "HUFFLEPUFF  |  THEATER";
            emblemType = 4;
        } else {
            topBg = 0xFF180C2C; botBg = 0xFF0A0514; glowCol = 0x58A848FF;
            houseSub = "ASTRONOMY  |  LUMOS";
            emblemType = 5;
        }

        p.setShader(new LinearGradient(0, 0, 0, bh, topBg, botBg, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, bw, bh, p);

        p.setShader(new RadialGradient(bw * 0.5f, squareBadge ? bh * 0.5f : bh * 0.36f, bw * 0.62f,
                new int[] {glowCol, glowCol & 0x00FFFFFF}, null, Shader.TileMode.CLAMP));
        c.drawRect(0, 0, bw, bh, p);
        p.setShader(null);

        if (squareBadge) {
            float pad = Math.max(3f, bw * 0.055f);
            float r = bw * 0.24f;
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1.5f, bw * 0.018f));
            p.setColor(0x66F5D078);
            c.drawRoundRect(new RectF(pad, pad, bw - pad, bh - pad), r, r, p);
        } else {
            float pad = 11f * cardScale;
            float r = 18f * cardScale;
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1.4f * cardScale);
            p.setColor(0x52F5D078);
            c.drawRoundRect(new RectF(pad, pad, bw - pad, bh - pad), r, r, p);

            float pad2 = 15f * cardScale;
            p.setStrokeWidth(0.8f * cardScale);
            p.setColor(0x2CF5D078);
            c.drawRoundRect(new RectF(pad2, pad2, bw - pad2, bh - pad2), r * 0.8f, r * 0.8f, p);

            p.setStyle(Paint.Style.FILL);
            p.setColor(0x88FFE082);
            float[][] corners = {
                {pad + 6f * cardScale, pad + 6f * cardScale},
                {bw - pad - 6f * cardScale, pad + 6f * cardScale},
                {pad + 6f * cardScale, bh - pad - 6f * cardScale},
                {bw - pad - 6f * cardScale, bh - pad - 6f * cardScale}
            };
            for (float[] cn : corners) c.drawCircle(cn[0], cn[1], 2.2f * cardScale, p);
        }

        float cx = horizontalLayout ? getMedallionCenterX(bw, bh, true) : (bw * 0.5f);
        float cy = squareBadge ? (bh * 0.5f) : (horizontalLayout ? getMedallionCenterY(bw, bh, true) : (bh * 0.34f));
        float er = squareBadge ? (bw * 0.30f)
                : (horizontalLayout ? (getMedallionRadius(bw, bh, true) / 1.15f) : Math.min(24f * contentScale, bh * 0.22f));

        p.setShader(new RadialGradient(cx, cy, er * (squareBadge ? 1.45f : (horizontalLayout ? 1.65f : 1.9f)),
                new int[] {0x48FFD878, 0x00FFD878}, null, Shader.TileMode.CLAMP));
        c.drawCircle(cx, cy, er * (squareBadge ? 1.45f : (horizontalLayout ? 1.65f : 1.9f)), p);
        p.setShader(null);

        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(squareBadge ? Math.max(2f, bw * 0.024f) : (2.2f * contentScale));
        p.setColor(0xFFFFE498);

        if (!useCustomPackIcon && defaultIcon != null) {
            // Fallback from the 5-app custom icon pack to the device's default application icon.
            // MainActivity.addTile also overlays a hardware-accelerated circular ImageView inside this medallion.
            float ir = er * 1.15f;
            p.setStyle(Paint.Style.FILL);
            p.setColor(0x8808060C);
            c.drawCircle(cx, cy, ir, p);
            try {
                c.save();
                Path clip = new Path();
                clip.addCircle(cx, cy, ir, Path.Direction.CW);
                c.clipPath(clip);
                if (defaultIcon instanceof BitmapDrawable) {
                    Bitmap ib = ((BitmapDrawable) defaultIcon).getBitmap();
                    if (ib != null) {
                        if (android.os.Build.VERSION.SDK_INT >= 26 && ib.getConfig() == Bitmap.Config.HARDWARE) {
                            Bitmap sw = ib.copy(Bitmap.Config.ARGB_8888, false);
                            if (sw != null) ib = sw;
                        }
                        c.drawBitmap(ib, null, new RectF(cx - ir, cy - ir, cx + ir, cy + ir), p);
                    }
                } else {
                    defaultIcon.setBounds(Math.round(cx - ir), Math.round(cy - ir), Math.round(cx + ir), Math.round(cy + ir));
                    defaultIcon.draw(c);
                }
                c.restore();
            } catch (Throwable ignored) {
                try { c.restore(); } catch (Throwable ignored2) {}
            }
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1.8f * contentScale);
            p.setColor(0xCCFFE498);
            c.drawCircle(cx, cy, ir + 1.4f * contentScale, p);
        } else if (emblemType >= 101 && emblemType <= 105) {
            // NARUTO Custom Vector Emblems: Sharingan, Konoha Leaf, Rasengan Shuriken, Uzumaki Spiral, Akatsuki Cloud
            if (emblemType == 101) { // Sharingan Eye
                c.drawCircle(cx, cy, er * 0.95f, p);
                c.drawCircle(cx, cy, er * 0.56f, p);
                p.setStyle(Paint.Style.FILL);
                c.drawCircle(cx, cy, er * 0.22f, p);
                for (int tm = 0; tm < 3; tm++) {
                    double ang = tm * Math.PI * 2.0 / 3.0 - Math.PI / 2.0;
                    c.drawCircle(cx + (float) Math.cos(ang) * er * 0.56f, cy + (float) Math.sin(ang) * er * 0.56f, er * 0.14f, p);
                }
            } else if (emblemType == 102) { // Konoha Hidden Leaf Crest
                c.drawArc(rectF(cx - er * 0.65f, cy - er * 0.65f, cx + er * 0.65f, cy + er * 0.65f), 0, 270, false, p);
                c.drawLine(cx - er * 0.65f, cy, cx - er * 1.15f, cy, p);
                c.drawLine(cx, cy - er * 0.65f, cx + er * 1.05f, cy - er * 0.10f, p);
                c.drawLine(cx + er * 1.05f, cy - er * 0.10f, cx, cy + er * 0.65f, p);
            } else if (emblemType == 103) { // 4-Blade Fuma Shuriken & Rasengan
                for (int bl = 0; bl < 4; bl++) {
                    double a = bl * Math.PI / 2.0 + Math.PI / 4.0;
                    c.drawLine(cx, cy, cx + (float) Math.cos(a) * er * 1.08f, cy + (float) Math.sin(a) * er * 1.08f, p);
                }
                c.drawCircle(cx, cy, er * 0.42f, p);
            } else if (emblemType == 104) { // Uzumaki Spiral & Nine-Tails Seal
                c.drawCircle(cx, cy, er * 0.95f, p);
                c.drawCircle(cx, cy, er * 0.58f, p);
                c.drawCircle(cx, cy, er * 0.25f, p);
            } else { // Akatsuki Cloud
                c.drawCircle(cx - er * 0.50f, cy + er * 0.10f, er * 0.45f, p);
                c.drawCircle(cx + er * 0.05f, cy - er * 0.15f, er * 0.55f, p);
                c.drawCircle(cx + er * 0.60f, cy + er * 0.10f, er * 0.42f, p);
            }
        } else if (emblemType >= 201 && emblemType <= 205) {
            // DEMON SLAYER Custom Emblems: Sun Halo Earring, Checkered Water Wheel, Mist Tsuba, Thunder Bolt, Wisteria Butterfly
            if (emblemType == 201) { // Rising Sun Hanafuda Crest
                c.drawCircle(cx, cy, er * 0.92f, p);
                for (int ry = 0; ry < 8; ry++) {
                    double a = ry * Math.PI / 4.0;
                    c.drawLine(cx + (float) Math.cos(a) * er * 0.35f, cy + (float) Math.sin(a) * er * 0.35f,
                            cx + (float) Math.cos(a) * er * 0.90f, cy + (float) Math.sin(a) * er * 0.90f, p);
                }
                p.setStyle(Paint.Style.FILL);
                c.drawCircle(cx, cy, er * 0.35f, p);
            } else if (emblemType == 202) { // Tanjiro Checkered Haori & Water Wave
                c.drawRect(cx - er * 0.80f, cy - er * 0.80f, cx + er * 0.80f, cy + er * 0.80f, p);
                c.drawLine(cx, cy - er * 0.80f, cx, cy + er * 0.80f, p);
                c.drawLine(cx - er * 0.80f, cy, cx + er * 0.80f, cy, p);
            } else if (emblemType == 203) { // Nichirin Tsuba Sword Guard
                c.drawRect(cx - er * 0.78f, cy - er * 0.78f, cx + er * 0.78f, cy + er * 0.78f, p);
                c.drawCircle(cx, cy, er * 0.35f, p);
            } else if (emblemType == 204) { // Zenitsu Thunderclap Bolt & Triangle
                Path zb = new Path();
                zb.moveTo(cx + er * 0.25f, cy - er * 0.95f);
                zb.lineTo(cx - er * 0.35f, cy + er * 0.05f);
                zb.lineTo(cx + er * 0.15f, cy + er * 0.05f);
                zb.lineTo(cx - er * 0.25f, cy + er * 0.95f);
                c.drawPath(zb, p);
            } else { // Shinobu Butterfly Crest
                c.drawOval(new RectF(cx - er * 0.95f, cy - er * 0.75f, cx, cy + er * 0.55f), p);
                c.drawOval(new RectF(cx, cy - er * 0.75f, cx + er * 0.95f, cy + er * 0.55f), p);
            }
        } else if (emblemType >= 301 && emblemType <= 305) {
            // STAR WARS Custom Emblems: Imperial Cog, Mandalorian T-Visor, Jedi Winged Saber, Rebel Starbird, Death Star
            if (emblemType == 301) { // Galactic Empire 6-Spoke Cog
                c.drawCircle(cx, cy, er * 0.92f, p);
                c.drawCircle(cx, cy, er * 0.42f, p);
                for (int sp = 0; sp < 6; sp++) {
                    double a = sp * Math.PI / 3.0;
                    c.drawLine(cx + (float) Math.cos(a) * er * 0.42f, cy + (float) Math.sin(a) * er * 0.42f,
                            cx + (float) Math.cos(a) * er * 0.92f, cy + (float) Math.sin(a) * er * 0.92f, p);
                }
            } else if (emblemType == 302) { // Mandalorian Beskar Helmet T-Visor
                c.drawRoundRect(new RectF(cx - er * 0.75f, cy - er * 0.88f, cx + er * 0.75f, cy + er * 0.88f), er * 0.45f, er * 0.45f, p);
                c.drawLine(cx - er * 0.55f, cy - er * 0.20f, cx + er * 0.55f, cy - er * 0.20f, p);
                c.drawLine(cx, cy - er * 0.20f, cx, cy + er * 0.72f, p);
            } else if (emblemType == 303) { // Jedi Order Crest & Lightsaber
                c.drawLine(cx, cy - er * 1.05f, cx, cy + er * 0.95f, p);
                c.drawArc(rectF(cx - er * 0.95f, cy - er * 0.65f, cx + er * 0.95f, cy + er * 0.85f), 190, 160, false, p);
            } else if (emblemType == 304) { // Rebel Alliance Starbird
                c.drawArc(rectF(cx - er * 0.92f, cy - er * 0.85f, cx + er * 0.92f, cy + er * 0.92f), 340, 220, false, p);
                c.drawLine(cx, cy - er * 0.95f, cx, cy + er * 0.85f, p);
            } else { // Death Star Battle Station
                c.drawCircle(cx, cy, er * 0.92f, p);
                c.drawLine(cx - er * 0.92f, cy, cx + er * 0.92f, cy, p);
                c.drawCircle(cx - er * 0.32f, cy - er * 0.32f, er * 0.26f, p);
            }
        } else if (emblemType >= 401 && emblemType <= 405) {
            // MCU Custom Emblems: Iron Man Arc Reactor, Eye of Agamotto, Thor Mjolnir, Avengers 'A', Cap Shield
            if (emblemType == 401) { // Iron Man Arc Reactor Triangle in Circle
                c.drawCircle(cx, cy, er * 0.92f, p);
                Path ar = new Path();
                ar.moveTo(cx - er * 0.58f, cy - er * 0.38f);
                ar.lineTo(cx + er * 0.58f, cy - er * 0.38f);
                ar.lineTo(cx, cy + er * 0.68f);
                ar.close();
                c.drawPath(ar, p);
            } else if (emblemType == 402) { // Eye of Agamotto (Time Stone)
                c.drawOval(new RectF(cx - er * 0.98f, cy - er * 0.55f, cx + er * 0.98f, cy + er * 0.55f), p);
                c.drawCircle(cx, cy, er * 0.36f, p);
            } else if (emblemType == 403) { // Thor's Mjolnir Hammer
                c.drawRoundRect(new RectF(cx - er * 0.72f, cy - er * 0.72f, cx + er * 0.72f, cy - er * 0.08f), er * 0.15f, er * 0.15f, p);
                c.drawLine(cx, cy - er * 0.08f, cx, cy + er * 0.88f, p);
            } else if (emblemType == 404) { // Avengers 'A' Logo
                c.drawCircle(cx, cy, er * 0.92f, p);
                c.drawLine(cx + er * 0.12f, cy - er * 0.85f, cx - er * 0.58f, cy + er * 0.75f, p);
                c.drawLine(cx + er * 0.12f, cy - er * 0.85f, cx + er * 0.48f, cy + er * 0.75f, p);
                c.drawLine(cx - er * 0.35f, cy + er * 0.15f, cx + er * 0.52f, cy + er * 0.15f, p);
            } else { // Captain America Shield / Vibranium Crest
                c.drawCircle(cx, cy, er * 0.92f, p);
                c.drawCircle(cx, cy, er * 0.60f, p);
                c.drawCircle(cx, cy, er * 0.28f, p);
            }
        } else if (emblemType >= 501 && emblemType <= 505) {
            // DCU Custom Emblems: Superman Diamond Shield, Green Lantern Symbol, Gotham Bat-Emblem, Wonder Woman 'WW', Flash/JL Crest
            if (emblemType == 501) { // Superman Pentagonal Diamond Shield
                Path sh = new Path();
                sh.moveTo(cx - er * 0.62f, cy - er * 0.78f);
                sh.lineTo(cx + er * 0.62f, cy - er * 0.78f);
                sh.lineTo(cx + er * 0.98f, cy - er * 0.20f);
                sh.lineTo(cx, cy + er * 0.92f);
                sh.lineTo(cx - er * 0.98f, cy - er * 0.20f);
                sh.close();
                c.drawPath(sh, p);
                c.drawArc(rectF(cx - er * 0.45f, cy - er * 0.45f, cx + er * 0.45f, cy + er * 0.25f), 45, 240, false, p);
            } else if (emblemType == 502) { // Green Lantern Corps Symbol
                c.drawCircle(cx, cy, er * 0.56f, p);
                c.drawLine(cx - er * 0.78f, cy - er * 0.72f, cx + er * 0.78f, cy - er * 0.72f, p);
                c.drawLine(cx - er * 0.78f, cy + er * 0.72f, cx + er * 0.78f, cy + er * 0.72f, p);
            } else if (emblemType == 503) { // Batman Bat-Signal Oval & Wings
                c.drawOval(new RectF(cx - er * 1.15f, cy - er * 0.68f, cx + er * 1.15f, cy + er * 0.68f), p);
                Path bt = new Path();
                bt.moveTo(cx, cy - er * 0.35f);
                bt.lineTo(cx + er * 0.75f, cy - er * 0.22f);
                bt.lineTo(cx, cy + er * 0.42f);
                bt.lineTo(cx - er * 0.75f, cy - er * 0.22f);
                bt.close();
                c.drawPath(bt, p);
            } else if (emblemType == 504) { // Wonder Woman Double-W Crest
                Path ww = new Path();
                ww.moveTo(cx - er * 0.95f, cy - er * 0.35f);
                ww.lineTo(cx - er * 0.45f, cy + er * 0.55f);
                ww.lineTo(cx, cy - er * 0.15f);
                ww.lineTo(cx + er * 0.45f, cy + er * 0.55f);
                ww.lineTo(cx + er * 0.95f, cy - er * 0.35f);
                c.drawPath(ww, p);
            } else { // Justice League Shield & Flash Lightning
                c.drawRoundRect(new RectF(cx - er * 0.78f, cy - er * 0.85f, cx + er * 0.78f, cy + er * 0.85f), er * 0.35f, er * 0.35f, p);
                c.drawLine(cx + er * 0.20f, cy - er * 0.65f, cx - er * 0.25f, cy + er * 0.05f, p);
                c.drawLine(cx - er * 0.25f, cy + er * 0.05f, cx + er * 0.20f, cy + er * 0.05f, p);
                c.drawLine(cx + er * 0.20f, cy + er * 0.05f, cx - er * 0.20f, cy + er * 0.65f, p);
            }
        } else if (emblemType >= 601 && emblemType <= 605) {
            // FORMULA 1 Custom Constructor Emblems: Scuderia Ferrari Shield, Red Bull Charging Sun, McLaren Speedmark, Mercedes 3-Pointed Star, Grand Prix Trophy
            if (emblemType == 601) { // Scuderia Ferrari Shield & Prancing Horse Silhouette
                Path sf = new Path();
                sf.moveTo(cx - er * 0.72f, cy - er * 0.82f);
                sf.lineTo(cx + er * 0.72f, cy - er * 0.82f);
                sf.quadTo(cx + er * 0.72f, cy + er * 0.45f, cx, cy + er * 0.94f);
                sf.quadTo(cx - er * 0.72f, cy + er * 0.45f, cx - er * 0.72f, cy - er * 0.82f);
                sf.close();
                c.drawPath(sf, p);
                c.drawLine(cx - er * 0.15f, cy + er * 0.48f, cx + er * 0.22f, cy - er * 0.42f, p);
                c.drawLine(cx + er * 0.22f, cy - er * 0.42f, cx + er * 0.42f, cy - er * 0.20f, p);
            } else if (emblemType == 602) { // Red Bull Racing Sun Disc & Charging Bull Horns
                c.drawCircle(cx, cy, er * 0.56f, p);
                c.drawLine(cx - er * 0.98f, cy + er * 0.18f, cx - er * 0.25f, cy - er * 0.18f, p);
                c.drawLine(cx + er * 0.98f, cy + er * 0.18f, cx + er * 0.25f, cy - er * 0.18f, p);
            } else if (emblemType == 603) { // McLaren Papaya Speedmark Swoosh
                Path mc = new Path();
                mc.moveTo(cx - er * 0.92f, cy + er * 0.35f);
                mc.quadTo(cx + er * 0.15f, cy - er * 0.85f, cx + er * 0.92f, cy - er * 0.35f);
                mc.quadTo(cx + er * 0.20f, cy - er * 0.10f, cx - er * 0.25f, cy + er * 0.55f);
                mc.close();
                c.drawPath(mc, p);
            } else if (emblemType == 604) { // Mercedes-AMG Three-Pointed Star in Ring
                c.drawCircle(cx, cy, er * 0.92f, p);
                c.drawLine(cx, cy, cx, cy - er * 0.88f, p);
                c.drawLine(cx, cy, cx - er * 0.76f, cy + er * 0.44f, p);
                c.drawLine(cx, cy, cx + er * 0.76f, cy + er * 0.44f, p);
            } else { // Grand Prix Winner Trophy & Aston Wings
                c.drawLine(cx - er * 0.95f, cy - er * 0.15f, cx + er * 0.95f, cy - er * 0.15f, p);
                c.drawRoundRect(new RectF(cx - er * 0.45f, cy - er * 0.75f, cx + er * 0.45f, cy + er * 0.25f), er * 0.20f, er * 0.20f, p);
                c.drawLine(cx, cy + er * 0.25f, cx, cy + er * 0.82f, p);
                c.drawLine(cx - er * 0.42f, cy + er * 0.82f, cx + er * 0.42f, cy + er * 0.82f, p);
            }
        } else if (emblemType >= 701 && emblemType <= 705) {
            // CYBERPUNK 2099 Custom Emblems: Arasaka 3-Lobe Crest, Sandevistan Spine, Blackwall Matrix, Samurai Visor, Trauma Cross
            if (emblemType == 701) { // Arasaka 3-Ring Corporate Crest
                c.drawCircle(cx, cy - er * 0.42f, er * 0.32f, p);
                c.drawCircle(cx - er * 0.38f, cy + er * 0.28f, er * 0.32f, p);
                c.drawCircle(cx + er * 0.38f, cy + er * 0.28f, er * 0.32f, p);
                c.drawLine(cx, cy, cx, cy - er * 0.10f, p);
            } else if (emblemType == 702) { // Sandevistan Cyberware Neural Spine
                c.drawLine(cx, cy - er * 0.88f, cx, cy + er * 0.88f, p);
                for (int nd = -2; nd <= 2; nd++) {
                    float ny = cy + nd * er * 0.34f;
                    c.drawLine(cx - er * 0.52f, ny, cx + er * 0.52f, ny - er * 0.12f, p);
                }
            } else if (emblemType == 703) { // Netrunner Blackwall Hexagon & Data Nodes
                c.drawRect(cx - er * 0.72f, cy - er * 0.72f, cx + er * 0.72f, cy + er * 0.72f, p);
                c.drawLine(cx - er * 0.72f, cy - er * 0.72f, cx + er * 0.72f, cy + er * 0.72f, p);
                c.drawCircle(cx, cy, er * 0.32f, p);
            } else if (emblemType == 704) { // Samurai Jacket Collar & Cyber-Visor
                c.drawLine(cx - er * 0.85f, cy - er * 0.20f, cx + er * 0.85f, cy - er * 0.20f, p);
                c.drawLine(cx - er * 0.55f, cy - er * 0.65f, cx, cy + er * 0.75f, p);
                c.drawLine(cx + er * 0.55f, cy - er * 0.65f, cx, cy + er * 0.75f, p);
            } else { // Trauma Team Platinum Medical Cross & Shield
                c.drawCircle(cx, cy, er * 0.88f, p);
                c.drawLine(cx - er * 0.52f, cy, cx + er * 0.52f, cy, p);
                c.drawLine(cx, cy - er * 0.52f, cx, cy + er * 0.52f, p);
            }
        } else if (emblemType == 1) {
            Path tri = new Path();
            tri.moveTo(cx, cy - er);
            tri.lineTo(cx - er * 1.05f, cy + er * 0.82f);
            tri.lineTo(cx + er * 1.05f, cy + er * 0.82f);
            tri.close();
            c.drawPath(tri, p);
            c.drawCircle(cx, cy + er * 0.22f, er * 0.56f, p);
            c.drawLine(cx, cy - er, cx, cy + er * 0.82f, p);
            p.setStyle(Paint.Style.FILL);
            Path play = new Path();
            play.moveTo(cx - er * 0.20f, cy + er * 0.02f);
            play.lineTo(cx - er * 0.20f, cy + er * 0.42f);
            play.lineTo(cx + er * 0.26f, cy + er * 0.22f);
            play.close();
            c.drawPath(play, p);
        } else if (emblemType == 2) {
            Path sh = new Path();
            sh.moveTo(cx, cy - er);
            sh.lineTo(cx + er * 0.88f, cy - er * 0.55f);
            sh.quadTo(cx + er * 0.88f, cy + er * 0.45f, cx, cy + er * 0.98f);
            sh.quadTo(cx - er * 0.88f, cy + er * 0.45f, cx - er * 0.88f, cy - er * 0.55f);
            sh.close();
            c.drawPath(sh, p);
            Path sr = new Path();
            sr.moveTo(cx + er * 0.25f, cy - er * 0.42f);
            sr.lineTo(cx - er * 0.22f, cy - er * 0.05f);
            sr.lineTo(cx + er * 0.22f, cy + er * 0.08f);
            sr.lineTo(cx - er * 0.25f, cy + er * 0.45f);
            c.drawPath(sr, p);
        } else if (emblemType == 3) {
            c.drawCircle(cx, cy, er * 0.95f, p);
            p.setStrokeWidth(1.4f * scale);
            c.drawOval(new RectF(cx - er * 1.25f, cy - er * 0.45f, cx + er * 1.25f, cy + er * 0.45f), p);
            c.drawCircle(cx, cy, er * 0.52f, p);
            p.setStyle(Paint.Style.FILL);
            c.drawCircle(cx, cy, 3.8f * scale, p);
        } else if (emblemType == 4) {
            Path dm = new Path();
            dm.moveTo(cx, cy - er * 1.02f);
            dm.lineTo(cx + er * 1.02f, cy);
            dm.lineTo(cx, cy + er * 1.02f);
            dm.lineTo(cx - er * 1.02f, cy);
            dm.close();
            c.drawPath(dm, p);
            Path chv = new Path();
            chv.moveTo(cx - er * 0.22f, cy - er * 0.42f);
            chv.lineTo(cx + er * 0.28f, cy);
            chv.lineTo(cx - er * 0.22f, cy + er * 0.42f);
            p.setStrokeWidth(3.2f * scale);
            c.drawPath(chv, p);
        } else if (emblemType == 5) {
            p.setStrokeWidth(1.4f * scale);
            c.drawCircle(cx, cy, er * 0.78f, p);
            p.setStyle(Paint.Style.FILL);
            Path st = new Path();
            for (int i = 0; i < 8; i++) {
                double ang = i * Math.PI / 4.0 - Math.PI / 2.0;
                float rad = (i % 2 == 0) ? er * 1.08f : er * 0.28f;
                float sx = cx + (float) Math.cos(ang) * rad;
                float sy = cy + (float) Math.sin(ang) * rad;
                if (i == 0) st.moveTo(sx, sy);
                else st.lineTo(sx, sy);
            }
            st.close();
            c.drawPath(st, p);
        } else {
            c.drawCircle(cx, cy, er * 0.48f, p);
            Path wL = new Path();
            wL.moveTo(cx - er * 0.48f, cy - er * 0.08f);
            wL.quadTo(cx - er * 1.15f, cy - er * 0.65f, cx - er * 1.48f, cy - er * 0.15f);
            wL.quadTo(cx - er * 1.05f, cy + er * 0.22f, cx - er * 0.48f, cy + er * 0.10f);
            c.drawPath(wL, p);
            Path wR = new Path();
            wR.moveTo(cx + er * 0.48f, cy - er * 0.08f);
            wR.quadTo(cx + er * 1.15f, cy - er * 0.65f, cx + er * 1.48f, cy - er * 0.15f);
            wR.quadTo(cx + er * 1.05f, cy + er * 0.22f, cx + er * 0.48f, cy + er * 0.10f);
            c.drawPath(wR, p);
        }

        if (!squareBadge) {
            p.setStyle(Paint.Style.FILL);
            if (horizontalLayout) {
                // Clean Horizontal Left-to-Right Layout: Icon on the Left, App Name + Subtitle on the Right
                float ir = getMedallionRadius(bw, bh, true);
                float textLeft = cx + ir + Math.max(12f * cardScale, bw * 0.045f);
                float textRight = bw - 20f * cardScale;
                float maxTextW = Math.max(40f, textRight - textLeft);

                p.setTextAlign(Paint.Align.LEFT);
                p.setLetterSpacing(0f);
                if (themeFont != null) p.setTypeface(themeFont);
                float titleSize = Math.min(bh * 0.30f, (theme == 0 ? 40f : (theme == 2 ? 30f : 26f)) * contentScale);
                p.setTextSize(titleSize);
                if (label != null && maxTextW > 0) {
                    float measured = p.measureText(label);
                    if (measured > maxTextW) p.setTextSize(Math.max(12f * cardScale, titleSize * (maxTextW / measured)));
                }
                p.setColor(0xFFFFF4D2);
                p.setShadowLayer(10f * cardScale, 0, 2f * cardScale, 0xCC000000);
                c.drawText(label != null ? label : "", textLeft, bh * 0.50f, p);

                if (cinzelFont != null) p.setTypeface(cinzelFont);
                String subText = houseSub;
                float subSize = Math.min(bh * 0.115f, 11f * contentScale);
                p.setTextSize(subSize);
                p.setLetterSpacing(0.12f);
                if (subText != null && maxTextW > 0) {
                    float subMeasured = p.measureText(subText) * 1.15f;
                    if (subMeasured > maxTextW) p.setTextSize(Math.max(8.5f * cardScale, subSize * (maxTextW / subMeasured)));
                }
                p.setColor(0xB8F5D488);
                p.setShadowLayer(4f * cardScale, 0, 1f * cardScale, 0xAA000000);
                c.drawText(subText != null ? subText : "", textLeft, bh * 0.76f, p);
            } else {
                p.setTextAlign(Paint.Align.CENTER);
                if (themeFont != null) p.setTypeface(themeFont);
                float titleSize = (theme == 0 ? 44f : (theme == 2 ? 32f : 28f)) * contentScale;
                p.setTextSize(titleSize);
                float maxTextW = bw - 34f * cardScale;
                if (label != null && maxTextW > 0) {
                    float measured = p.measureText(label);
                    if (measured > maxTextW) p.setTextSize(Math.max(13f * cardScale, titleSize * (maxTextW / measured)));
                }
                p.setColor(0xFFFFF4D2);
                p.setShadowLayer(10f * cardScale, 0, 2f * cardScale, 0xCC000000);
                c.drawText(label != null ? label : "", cx, bh * 0.74f, p);

                if (cinzelFont != null) p.setTypeface(cinzelFont);
                String subText = houseSub;
                if (bw < 260 && subText != null) {
                    int pipe = subText.indexOf('|');
                    if (pipe > 0) subText = subText.substring(0, pipe).trim();
                }
                float subSize = 11.5f * contentScale;
                p.setTextSize(subSize);
                p.setLetterSpacing(bw < 260 ? 0.10f : 0.18f);
                if (subText != null && maxTextW > 0) {
                    float subMeasured = p.measureText(subText) * (bw < 260 ? 1.12f : 1.22f);
                    if (subMeasured > maxTextW) p.setTextSize(Math.max(9f * cardScale, subSize * (maxTextW / subMeasured)));
                }
                p.setColor(0xB8F5D488);
                p.setShadowLayer(4f * cardScale, 0, 1f * cardScale, 0xAA000000);
                c.drawText(subText != null ? subText : "", cx, bh * 0.88f, p);
            }
        }

        // Keep bm as ARGB_8888 (never copy to Bitmap.Config.HARDWARE) so ImageView renders it on all OEMs (including Samsung One UI).
        return new BitmapDrawable(res, bm);
    }
}
