/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.cards.AbstractCard;
import game.gui.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darven
 */
public class Turn implements Cloneable {
    
    protected Bridge bridge;// TO DO
    
    private boolean ended;
    
    private short winner; //0 for draw, -1 for player1, 1 for player2

//***************************** CONSTRUCTOR ************************************
    
    // TO DO
    public Turn(Bridge bridge) {
        this.bridge = bridge;
        this.mute = false;
        this.ended = false;
        this.winner = 0;
    }
    
//***************************** GETTER *****************************************
    /**
     * @return the bridge
     */
    public Bridge getBridge() {
        return bridge;
    }

    /**
     * @return 0 for draw, -1 for player1, 1 for player2
     */
    public short getWinner() {
        if (this.ended) {
            System.out.println("Warning: checking winner in unended turn");
        }
        return winner;
    }
    
    /**
     * check if this turn fills end of round condition
     * @return
     *      true if this fills round end conditions
     */
    public boolean isRoundEnd(){
        int firewall = this.bridge.getFirewallLocation();
        int player1Position = this.bridge.getPlayerState1().getPosition();
        int player2Position = this.bridge.getPlayerState2().getPosition();
        
        boolean firewallKill = (firewall <= player1Position) ||
                (firewall >= player2Position);
        
        int player1Mana = this.bridge.getPlayerState1().getMana();
        int player2Mana = this.bridge.getPlayerState2().getMana();
        
        boolean manaRounout = player1Mana <= 0 || player2Mana <= 0;
        
        return firewallKill || manaRounout;
    }
    
//***************************** SETTER *****************************************
    
    /**
     * set ended to true
     */
    public void end(){
        this.ended = true;
    }
    
//**************************** MUTISM ******************************************
    
    private boolean mute;

    /**
     * @return the mute
     */
    public boolean isMute() {
        return mute;
    }
    
    /**
     * mute to false
     */
    public void setMute() {
        this.mute = true;
    }

//******************************************************************************
    
    @Override
    public Object clone() throws CloneNotSupportedException{
        Turn clone = (Turn) super.clone();
        clone.bridge = (Bridge) clone.bridge.clone();
        
        //default end and turn victory
        clone.ended = false;
        clone.winner = 0;
        
        return clone;
    }
    
    /**
     * Play turn 
     * get action input and apply it to the turn
     * @return 
     *      turn resulting of actions played
     */
    public Turn play(Round round){
        try {
            //generate next turn
            Turn resultTurn = (Turn) this.clone();
            
            //get current player states
            PlayerState player1 = this.bridge.getPlayerState1();
            PlayerState player2 = this.bridge.getPlayerState2();
            
            //get bet
            player1.getBet();
            player2.getBet();
            
            //collect actions input
            ArrayList<AbstractCard> player1Cards = Console.askCards(player1);
            ArrayList<AbstractCard> player2Cards = Console.askCards(player2);
            
            //discard cards played by each player
            player1.getCardManager().discardAll(player1Cards);
            player2.getCardManager().discardAll(player2Cards);
            
            //merge and sort card lists
            ArrayList<AbstractCard> cards = player1Cards;
            cards.addAll(player2Cards);
            Collections.sort(cards);
                    
            //apply cards' action to the turn
            for (AbstractCard card : cards){
                card.generalApply(round);
            }
            
            //apply bet
            this.applyBets();
            
            return resultTurn;
            
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(Turn.class.getName()).log(Level.SEVERE, null, ex);
            return this;
        }    
    }
    
    /**
     * set isPlayer1Winner to 0 for draw, -1 for player1, 1 for player2
     */
    public void applyBets(){
        //get bets
        int player1bet = this.bridge.getPlayerState1().getBet();
        int player2bet = this.bridge.getPlayerState2().getBet();
        
        //compare bets to determine winner
        if (player1bet < player2bet){
            this.winner = 1;
            
        }else if (player1bet > player2bet){
            this.winner = -1;
            
        }else{
            this.winner = 0;
        }
        
        //end Turn
        this.ended = true;
    }
    
    /**
     * apply end of turn actions:
     *      - each player draws cards (number set in Config.END_OF_ROUND_DRAW)
     */
    public void endOfRoundActions(){
        PlayerState player1, player2;
        player1 = this.bridge.getPlayerState1();
        player2 = this.bridge.getPlayerState2();
        
        //draw set number of cards
        for (int i = 0; i < Config.END_OF_ROUND_DRAW; i++){
            player1.getCardManager().drawCard();
            player2.getCardManager().drawCard();
        }
    }
    
    public Turn getNextRoundStarter(){
        //break if turn not ended
        if (!this.ended){
            System.out.println("Warning: checking winner in unended turn");
            return null;
        }
        
        //recover bridge
        Bridge bridge = this.getBridge();
        
        //init playerState
        PlayerState playerState1 = new PlayerState(bridge.getPlayer1(), true);
        PlayerState playerState2 = new PlayerState(bridge.getPlayer2(), false);
        
        //get previous location of firewall
        int firewallLocation = bridge.getFirewallLocation();
        
        //if any player lost, the firewall takes its position 
        if (this.winner != 0){
            //set firewal to loser's position
            if (this.winner < 0){
                firewallLocation = bridge.getPlayerState2().getPosition();
            }else{
                firewallLocation = bridge.getPlayerState1().getPosition();
            }
            
        //if tie, copy the positions of the firewall and the players
        }else{
            //copy player 1 and 2 positions
            playerState1.setPosition(bridge.getPlayerState1().getPosition());
            playerState2.setPosition(bridge.getPlayerState2().getPosition());
        }
        
        
        //init the new turn's bridge
        Bridge nextBridge = new Bridge(playerState1, playerState2, 
                bridge.getSize()-1, 
                firewallLocation);
        
        Turn initTurn = new Turn(nextBridge);
        
        //if any player lost, the players are replaced 3 tiles from the firewall
        if(this.winner != 0){
            nextBridge.replacePlayers();
        }
        
        //apply end of round actions
        initTurn.endOfRoundActions();
        initTurn.end();
        
        //return turn
        return initTurn;
    } 
}
