package FirstModXP.Service;

import FirstModXP.FirstModXp;
import FirstModXP.monster.DbGenie;
import basemod.BaseMod;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.monsters.MonsterInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MonsterService {
    public static final Logger logger = LogManager.getLogger(MonsterService.class.getName());


    public static void receiveEditMonster() {
        dbGenieInit();
    }

    private static void dbGenieInit() {
        BaseMod.addMonster(DbGenie.ID, DbGenie.NAME, () -> new DbGenie(0.0f, 0.0f, true));
        BaseMod.addStrongMonsterEncounter(Exordium.ID, new MonsterInfo(DbGenie.NAME, 10.0f));
        logger.info("Added Elite enemy yay");
    }


}
