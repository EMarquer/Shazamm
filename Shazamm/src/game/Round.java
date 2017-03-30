/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.gui.Console;
import game.gui.log.LogSystem;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darven
 */
public class Round {
    
    private final LinkedList<Turn> turns; /* last turn of the list is the last 
    turn played*/

    private boolean ended;
    
    private short winner; //0 for draw, -1 for player1, 1 for player2
    
//***************************** CONSTRUCTOR ************************************   
    
    /**
     * Create a new round from all parameters
     * @param player1
     * @param player2
     * @param size
     * @param firewallLocation 
     */
    public Round(Player player1, Player player2, int size, int firewallLocation) {
        //init empty list
        this.turns=new LinkedList<>();
        this.ended=false;
        
        //init playerState
        PlayerState playerState1 = new PlayerState(player1, true);
        PlayerState playerState2 = new PlayerState(player2, false);
        
        //init the new turn 
        Bridge bridge =new Bridge(playerState1, playerState2, size, firewallLocation);
        Turn initTurn = new Turn(bridge);
        
        initTurn.startOfRoundActions();
        initTurn.end();
        
        this.turns.add(initTurn);
    }
    
    /**
     * Create a new round from another round, as the round following it
      call endOfTurnDraw()
     * @param round
     */
    public Round(Round round) {
        //init empty list
        this.turns=new LinkedList<>();
        this.ended=false;
        
        //recover initial turn from previous last turn
        Turn initTurn = round.getLastTurn().getNextRoundStarter();
        
        initTurn.startOfRoundActions();
        this.turns.add(initTurn);
    }   
    
//***************************** GETTERS ****************************************
    
    /**
     * @return the turns
     */
    public LinkedList<Turn> getTurns() {
        return turns;
    }
    
    /**
     * @return last turn
     */
    public Turn getLastTurn(){
        return this.turns.getLast();
    }
    
    /**
     * @return 
     *      turn before current turn
     *      if less than two turns (including current turn), return null
     */
    public Turn getSecondLastTurn(){
        if (this.turns.size() < 2){
            return null;
        }else{
            return this.turns.get(this.turns.size()-2);
        }
    }
    
    /**
     * Return the last bridge
     * @return 
     */
    public Bridge getLastBridge(){
        return this.getLastTurn().getBridge();
    }
    
    /**
     * return the last playstate 1
     * @return 
     */
    public PlayerState getLastPlayerState1(){
        return this.getLastBridge().getPlayerState1();
    }
    
    /**
     * return the last playstate 2
     * @return 
     */
    public PlayerState getLastPlayerState2(){
        return this.getLastBridge().getPlayerState2();
    }

    /**
     * @return the winner
     */
    public short getWinner() {
        return winner;
    }
    
    /**
     * @return the ended
     */
    public boolean isEnded() {
        return ended;
    }
    
//******************************************************************************
    
    /**
     * set winner value using Bridge.hasOutOfBridge()
     *      0 for draw, -1 for player1, 1 for player2
     *      -2 for no player out of the bridge
     */
    private void setWinner(){
        this.winner = this.getLastTurn().getBridge().hasOutOfBridge();
    }
    
    /**
     * set ended to false: end the round, necessary to avoid doing extra turns 
     *      or replaying the whole round
     */
    public void end(){
        this.ended=true;
        this.setWinner();
        LogSystem.endRound();
    }
    
    
    /**
     * add turn to the end of the turn list
     * @param turn turn to add
     */
    public void addTurn(Turn turn){
        this.turns.addLast(turn);
    }

    /**
     * play turns untill the end of the round
     * @return 
     *      true if the game has ended (if a player is in the lava)
     */
    public boolean play() {
        if (this.turns.isEmpty()){
            throw new IndexOutOfBoundsException("No initial turn defined !");
        }
        
        //play turns while turn does not end the round
        Turn turn;
        while (!this.isEnded()){
            //get last turn added; if first turn, uses initial turn
            turn = this.getLastTurn();
            
            //play turn
            turn.play(this);
            
            //check if the turn fills round end condition
            if (turn.isRoundEnd()){
                this.end();
            }else{
                
                //if round isn't ended, prepare the next turn
                try {
                    this.turns.add((Turn) turn.clone());
                    Console.println("New Turn");
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(Round.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        /*true if, in the last turn of the round, at least one of the
        players is out of the bridge (game ending condition)*/
        return this.getWinner() != -2;
    }
    
    @Override
    public String toString(){
        String str="";
        for (Turn turn : turns) {
            str+=turn.toString();
            str+="\n";
                    
        }
        return str;
    }
}