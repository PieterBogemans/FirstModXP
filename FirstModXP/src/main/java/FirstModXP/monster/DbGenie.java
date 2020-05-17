package FirstModXP.monster;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.megacrit.cardcrawl.actions.AbstractGameAction.AttackEffect;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.AbstractMonster.EnemyType;
import com.megacrit.cardcrawl.monsters.AbstractMonster.Intent;
import com.megacrit.cardcrawl.powers.AngerPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;

public class DbGenie extends AbstractMonster {
    public static final String ID = "GremlinNob";
    private static final MonsterStrings monsterStrings;
    public static final String NAME;
    public static final String[] MOVES;
    public static final String[] DIALOG;
    private static final int HP_MIN = 82;
    private static final int HP_MAX = 86;
    private static final int A_2_HP_MIN = 85;
    private static final int A_2_HP_MAX = 90;
    private static final int BASH_DMG = 6;
    private static final int RUSH_DMG = 14;
    private static final int A_2_BASH_DMG = 8;
    private static final int A_2_RUSH_DMG = 16;
    private static final int DEBUFF_AMT = 2;
    private int bashDmg;
    private int rushDmg;
    private static final byte BULL_RUSH = 1;
    private static final byte SKULL_BASH = 2;
    private static final byte BELLOW = 3;
    private static final int ANGRY_LEVEL = 2;
    private boolean usedBellow;
    private boolean canVuln;

    public DbGenie(float x, float y) {
        this(x, y, true);
    }

    public DbGenie(float x, float y, boolean setVuln) {
        super(NAME, "DB Genie", 86, -70.0F, -10.0F, 270.0F, 380.0F, (String)null, x, y);
        this.usedBellow = false;
        this.intentOffsetX = -30.0F * Settings.scale;
        this.type = EnemyType.ELITE;
        this.dialogX = -60.0F * Settings.scale;
        this.dialogY = 50.0F * Settings.scale;
        this.canVuln = setVuln;
        if (AbstractDungeon.ascensionLevel >= 8) {
            this.setHp(85, 90);
        } else {
            this.setHp(82, 86);
        }

        if (AbstractDungeon.ascensionLevel >= 3) {
            this.bashDmg = 8;
            this.rushDmg = 16;
        } else {
            this.bashDmg = 6;
            this.rushDmg = 14;
        }

        this.damage.add(new DamageInfo(this, this.rushDmg));
        this.damage.add(new DamageInfo(this, this.bashDmg));
        this.loadAnimation("images/monsters/theBottom/nobGremlin/skeleton.atlas", "images/monsters/theBottom/nobGremlin/skeleton.json", 1.0F);
        TrackEntry e = this.state.setAnimation(0, "animation", true);
        e.setTime(e.getEndTime() * MathUtils.random());
    }

    public void takeTurn() {
        switch(this.nextMove) {
            case 1:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(0), AttackEffect.BLUNT_HEAVY));
                break;
            case 2:
                AbstractDungeon.actionManager.addToBottom(new AnimateSlowAttackAction(this));
                AbstractDungeon.actionManager.addToBottom(new DamageAction(AbstractDungeon.player, (DamageInfo)this.damage.get(1), AttackEffect.BLUNT_HEAVY));
                if (this.canVuln) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 2, true), 2));
                }
                break;
            case 3:
                this.playSfx();
                AbstractDungeon.actionManager.addToBottom(new TalkAction(this, DIALOG[0], 1.0F, 3.0F));
                if (AbstractDungeon.ascensionLevel >= 18) {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new AngerPower(this, 3), 3));
                } else {
                    AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new AngerPower(this, 2), 2));
                }
        }

        AbstractDungeon.actionManager.addToBottom(new RollMoveAction(this));
    }

    private void playSfx() {
        int roll = MathUtils.random(2);
        if (roll == 0) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINNOB_1A"));
        } else if (roll == 1) {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINNOB_1B"));
        } else {
            AbstractDungeon.actionManager.addToBottom(new SFXAction("VO_GREMLINNOB_1C"));
        }

    }

    protected void getMove(int num) {
        if (!this.usedBellow) {
            this.usedBellow = true;
            this.setMove((byte)3, Intent.BUFF);
        } else {
            if (AbstractDungeon.ascensionLevel >= 18) {
                if (!this.lastMove((byte)2) && !this.lastMoveBefore((byte)2)) {
                    if (this.canVuln) {
                        this.setMove(MOVES[0], (byte)2, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
                    } else {
                        this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                    }

                    return;
                }

                if (this.lastTwoMoves((byte)1)) {
                    if (this.canVuln) {
                        this.setMove(MOVES[0], (byte)2, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
                    } else {
                        this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                    }
                } else {
                    this.setMove((byte)1, Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
                }
            } else {
                if (num < 33) {
                    if (this.canVuln) {
                        this.setMove(MOVES[0], (byte)2, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
                    } else {
                        this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                    }

                    return;
                }

                if (this.lastTwoMoves((byte)1)) {
                    if (this.canVuln) {
                        this.setMove(MOVES[0], (byte)2, Intent.ATTACK_DEBUFF, ((DamageInfo)this.damage.get(1)).base);
                    } else {
                        this.setMove((byte)2, Intent.ATTACK, ((DamageInfo)this.damage.get(1)).base);
                    }
                } else {
                    this.setMove((byte)1, Intent.ATTACK, ((DamageInfo)this.damage.get(0)).base);
                }
            }

        }
    }

    static {
        monsterStrings = CardCrawlGame.languagePack.getMonsterStrings("GremlinNob");
        NAME = monsterStrings.NAME;
        MOVES = monsterStrings.MOVES;
        DIALOG = monsterStrings.DIALOG;
    }
}
