package pokemon.battler;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;

public class battleCalculator {
    public static Random random = new Random();

    public static void battleAndLogs(int pos) throws FileNotFoundException {
        Pokemon self = battleManager.playerTeam[battleManager.currentPokemon], 
            enemy = battleManager.pokemonList[battleManager.enemyPokemon];
        if (self.effect.contains(Status.NORMAL) && self.effect.size()==1) {self.skipTurn=false;}
        if (enemy.effect.contains(Status.NORMAL) && enemy.effect.size()==1) {enemy.skipTurn=false;}
        Move selfMove = self.getMove(pos), enemyMove = enemy.getMove(random.nextInt(4));
        boolean selfPriority = self.effect.contains(Status.PRIORITY);
        boolean enemyPriority = enemy.effect.contains(Status.PRIORITY);
        ArrayList<String> arr = new ArrayList<String>();
        arr.add("Nothing happened...");
        if (enemy.skipTurn && self.skipTurn) {textManager.printLogs(arr);}
        else if (enemy.skipTurn) {textManager.printLogs(attack(self, enemy, selfMove));} 
        else if (self.skipTurn) {textManager.printLogs(attack(enemy, self, enemyMove));} 
        else if (enemyPriority) {
            textManager.printLogs(attack(enemy, self, enemyMove));
            if (!self.skipTurn) {textManager.printLogs(attack(self, enemy, selfMove));}
        } else if (selfPriority) {
            textManager.printLogs(attack(self, enemy, selfMove));
            if (!enemy.skipTurn) {textManager.printLogs(attack(enemy, self, enemyMove));}
        } else if (enemy.spd > self.spd) {
            textManager.printLogs(attack(enemy, self, enemyMove));
            if (!self.skipTurn) {textManager.printLogs(attack(self, enemy, selfMove));}
        } else {
            textManager.printLogs(attack(self, enemy, selfMove));
            if (!enemy.skipTurn) {textManager.printLogs(attack(enemy, self, enemyMove));}
        }
    }

    public static ArrayList<String> attack(Pokemon attacker, Pokemon attacked, Move move) {
        int atk, def;
        ArrayList<String> battleLog = new ArrayList<String>();
        battleLog.add(attacker.name + " used " + move.moveName + ".");
        if (move.moveType != "STA") {
            // move.effect(attacker);
            double STAB = (attacker.typeA == move.moveElement || attacker.typeB == move.moveElement) ? 1.5 : 1.0,
                multi = getAtkMulti(new String[] {attacked.typeA,attacked.typeB}, move.moveElement);
            if (move.moveType == "SPE") {
                atk = attacker.sp_atk;
                def = attacked.sp_def;
            } else {
                atk = attacker.atk;
                def = attacked.def;
            }
            int damage = (int) (((((2.0 /** Level*/ / 5 + 2) * atk * move.dmg / def) / 50) + 2) * STAB * multi * (0.85 + (1.0 - 0.85) * random.nextDouble()));
            if (random.nextDouble()*100-move.acc > 0) {
                damage = 0;
                battleLog.add("It missed!");
            } else {
                if (move.target == moveTarget.EFFonENEMYnATK) {
                    inflictStatus(attacker, attacked, move);
                    battleLog.addAll(applyStatus(attacked, attacker, move));
                } else if (move.target == moveTarget.EFFonSELFnATK) {
                    inflictStatus(attacker, attacker, move);
                    battleLog.addAll(applyStatus(attacker, attacker, move));
                } else if (move.target == moveTarget.ATK) {}
                else {
                    throw new InputMismatchException();
                }
            }
            attacked.hp -= damage;
            if (attacked.hp <= 0) {battleLog.add(fainted(attacked));}
            String color = "";
            if (attacked.hp/attacked.MAX_HP >= 0.50) {color = "green";} 
            else if (attacked.hp/attacked.MAX_HP >= 0.25) {color = "yellow";} 
            else {color = "red";}
            textManager.setHealth(color,(int) attacked.hp, attacked.MAX_HP, 
                battleManager.playerTeam[battleManager.currentPokemon] == attacked);
            if (multi == 0) {battleLog.set(0,battleLog.get(0) + " It wasn't effective...");}
            if (multi == 0.5 || multi == 0.25) {battleLog.set(0,battleLog.get(0) + " It wasn't very effective...");}
            if (multi == 2) {battleLog.set(0,battleLog.get(0) + " It was very effective!");}
            if (multi == 4) {battleLog.set(0,battleLog.get(0) + " It was super effective!");}
        } else {
            if (move.target == moveTarget.EFFonENEMY) {
                inflictStatus(attacker, attacked, move);
                battleLog.addAll(applyStatus(attacked, attacker, move));
            } else if (move.target == moveTarget.EFFonSELF) {
                inflictStatus(attacker, attacker, move);
                battleLog.addAll(applyStatus(attacker, attacker, move));
            } else {
                throw new InputMismatchException();
            }
        }
        return battleLog;
    }
    
    public static void inflictStatus(Pokemon assigner, Pokemon assignee, Move move) {
        if (move.effectPer == null) {
            for (Status i : move.effects) {
                assignee.effect.add(i);
            }
        } else {
            for (int i = 0; i < move.effects.length; i++) {
                if (random.nextDouble()<=move.effectPer[i]) {
                    assignee.effect.add(move.effects[i]);
                }
            }
        }
    }

    public static ArrayList<String> applyStatus(Pokemon pokemon, Pokemon caster, Move move) {
        ArrayList<String> effectLogs = new ArrayList<String>();
        int[] statsMod = new int[5];
        ArrayList<Status> persistantEffects = new ArrayList<Status>();
        for (Status i : pokemon.effect) {
            if (i==Status.FAINTED) {persistantEffects.add(Status.FAINTED);}
            if (i == Status.MINUS_ATK) {statsMod[0]--;}
            if (i == Status.PLUS_ATK) {statsMod[0]++;}
            if (i == Status.MINUS_DEF) {statsMod[1]--;}
            if (i == Status.PLUS_DEF) {statsMod[1]++;}
            if (i == Status.MINUS_SP_ATK) {statsMod[2]--;}
            if (i == Status.PLUS_SP_ATK) {statsMod[2]++;}
            if (i == Status.MINUS_SP_DEF) {statsMod[3]--;}
            if (i == Status.PLUS_SP_DEF) {statsMod[3]++;}
            if (i == Status.MINUS_SPD) {statsMod[4]--;}
            if (i == Status.PLUS_SPD) {statsMod[4]++;}
            if (i == Status.FLINCH) {if (caster.spd > pokemon.spd && !pokemon.skipTurn) {
                pokemon.skipTurn = true; 
                persistantEffects.add(Status.NORMAL);
                effectLogs.add(pokemon.name + " flinched and missed its turn!");
            }}
            if (i == Status.FROZEN) {if (!(pokemon.typeA == "ICE"||pokemon.typeB=="ICE")) {
                pokemon.skipTurn = true;
                textManager.changeASCIICondition("FRO", 
                    pokemon == battleManager.pokemonList[battleManager.currentPokemon]);
                effectLogs.add(pokemon.name + " was frozen!");
                persistantEffects.add(Status.FROZEN);
            }}
            if (i == Status.DISABLED) {move.disabled = true; persistantEffects.add(Status.REENABLE);}
            if (i == Status.REENABLE) {move.disabled = false;}
            if (i == Status.NORMAL) {pokemon.skipTurn=false;}
        }
        pokemon.effect = persistantEffects;
        for (int i=0; i < statsMod.length;i++) {
            while (statsMod[i]+pokemon.stageStats[i]>6) {statsMod[i]--;}
            while (statsMod[i]+pokemon.stageStats[i]<-6) {statsMod[i]++;}
            pokemon.stageStats[i] += statsMod[i];
            String log = pokemon.name + "'s " +
                ((i==0) ? "Attack" : (i==1) ? "Defense" : (i==2) ? "Sp. Attack" : (i==3) ? "Sp. Defense" : "Speed") + " " +
                ((statsMod[i]==1) ? "increased." : (statsMod[i]==-1) ? "decreased." : (statsMod[i] > 1) ? "sharply increased!" : "sharply decreased. ");
            if (statsMod[i] != 0) {effectLogs.add(log);}
        }
        pokemon.atk += statsMod[0];
        pokemon.def += statsMod[1];
        pokemon.sp_atk += statsMod[2];
        pokemon.sp_def += statsMod[3];
        pokemon.spd += statsMod[4];
        return effectLogs;
    }

    private static String fainted(Pokemon pokemon) {
        pokemon.skipTurn = true;
        pokemon.effect.add(Status.FAINTED);
        textManager.changeASCIICondition("FAI", 
            pokemon == battleManager.pokemonList[battleManager.currentPokemon]);
        return pokemon.name + " has fainted. ";
        //TODO implement pokemon switch
    }
    private static double getAtkMulti(String[] types, String moveElement) {
        double multi = 1.0;
        if (moveElement == "NOR") {
            for (String i : types) {
                if (i == "ROC" || i == "STE") {multi *= 0.5;}
                if (i == "GHO") {multi = 0;}
            }
        }
        if (moveElement == "FIR") {
            for (String i : types) {
                if (i == "FIR" || i == "WAT" || i == "ROC" || i == "DRA") {multi *= 0.5;}
                if (i == "GRA" || i == "ICE" || i == "BUG" || i == "STE") {multi *= 2;}
            }
        }
        if (moveElement == "WAT") {
            for (String i : types) {
                if (i == "WAT" || i == "GRA" || i == "DRA") {multi *= 0.5;}
                if (i == "FIR" || i == "GRO" || i == "ROC") {multi *= 2;}
            }
        }
        if (moveElement == "ELE") {
            for (String i : types) {
                if (i == "GRO") {multi = 0;}
                if (i == "ELE" || i == "GRA" || i == "DRA") {multi *= 0.5;}
                if (i == "WAT" || i == "FLY") {multi *= 2;}
            }
        }
        if (moveElement == "GRA") {
            for (String i : types) {
                if (i == "FIR" || i == "GRA" || i == "POI" || i == "FLY" || i == "BUG" || i == "DRA" || i == "STE") {
                    multi *= 0.5;
                }
                if (i == "WAT" || i == "GRO" || i == "ROC") {multi *= 2;}
            }
        }
        if (moveElement == "ICE") {
            for (String i : types) {
                if (i == "FIR" || i == "WAT" || i == "ICE" || i == "STE") {multi *= 0.5;}
                if (i == "GRA" || i == "GRO" || i == "FLY" || i == "DRA") {multi *= 2;}
            }
        }
        if (moveElement == "FIG") {
            for (String i : types) {
                if (i == "GHO") {multi = 0;}
                if (i == "POI" || i == "FLY" || i == "PSY" || i == "BUG" || i == "FAI") {multi *= 0.5;}
                if (i == "NOR" || i == "ICE" || i == "ROC" || i == "DAR" || i == "STE") {multi *= 2;}
            }
        }
        if (moveElement == "POI") {
            for (String i : types) {
                if (i == "STE") {multi = 0;}
                if (i == "POI" || i == "GRO" || i == "ROC" || i == "GHO") {multi *= 0.5;}
                if (i == "GRA" || i == "FAI") {multi *= 2;}
            }
        }
        if (moveElement == "GRO") {
            for (String i : types) {
                if (i == "FLY") {multi = 0;}
                if (i == "GRA" || i == "BUG") {multi *= 0.5;}
                if (i == "FIR" || i == "ELE" || i == "POI" || i == "ROC" || i == "STE") {multi *= 2;}
            }
        }
        if (moveElement == "FLY") {
            for (String i : types) {
                if (i == "ELE" || i == "ROC" || i == "STE") {multi *= 0.5;}
                if (i == "GRA" || i == "FIG" || i == "BUG") {multi *= 2;}
            }
        }
        if (moveElement == "PSY") {
            for (String i : types) {
                if (i == "DAR") {multi = 0;}
                if (i == "PSY" || i == "STE") {multi *= 0.5;}
                if (i == "FIG" || i == "POI") {multi *= 2;}
            }
        }
        if (moveElement == "BUG") {
            for (String i : types) {
                if (i == "FIR" || i == "FIG" || i == "POI" || i == "FLY" || i == "GHO" || i == "STE" || i == "FAI") {
                    multi *= 0.5;
                }
                if (i == "GRA" || i == "PSY" || i == "DAR") {multi *= 2;}
            }
        }
        if (moveElement == "ROC") {
            for (String i : types) {
                if (i == "FIG" || i == "GRO" || i == "STE") {multi *= 0.5;}
                if (i == "FIR" || i == "ICE" || i == "FLY" || i == "BUG") {multi *= 2;}
            }
        }
        if (moveElement == "GHO") {
            for (String i : types) {
                if (i == "NOR") {multi = 0;}
                if (i == "DAR") {multi *= 0.5;}
                if (i == "PSY" || i == "GHO") {multi *= 2;}
            }
        }
        if (moveElement == "DRA") {
            for (String i : types) {
                if (i == "FAI") {multi = 0;}
                if (i == "STE") {multi *= 0.5;}
                if (i == "DRA") {multi *= 2;}
            }
        }
        if (moveElement == "DAR") {
            for (String i : types) {
                if (i == "FIG" || i == "DAR" || i == "FAI") {multi *= 0.5;}
                if (i == "PSY" || i == "GHO") {multi *= 2;}
            }
        }
        if (moveElement == "STE") {
            for (String i : types) {
                if (i == "FIR" || i == "WAT" || i == "ELE" || i == "STE") {multi *= 0.5;}
                if (i == "ICE" || i == "ROC" || i == "FAI") {multi *= 2;}
            }
        }
        if (moveElement == "FAI") {
            for (String i : types) {
                if (i == "FIR" || i == "POI" || i == "STE") {multi *= 0.5;}
                if (i == "FIG" || i == "DRA" || i == "DAR") {multi *= 2;}
            }
        }
        return multi;
    }
}
