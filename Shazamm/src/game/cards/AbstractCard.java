/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.cards;

import game.PlayerState;
import game.Round;

/**
 *
 */
public abstract class AbstractCard implements Cloneable, Comparable<AbstractCard> {
    
    protected int id; //from 1 to 14
    
    private boolean belongPlayer1; //true if card belong to player 1

//******************************************************************************    
    
    @Override
    public int compareTo(AbstractCard o) {
        //ascending order of IDs
        return this.getId() - o.getId();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException{
        AbstractCard clone = (AbstractCard) super.clone();
        return clone;
    }
    
    /**
     * specific action, must call generalApply
     * @param round
     *      round to which apply the card
     */
    public abstract void apply(Round round);
    
    /**
     * actions to apply for each
     * @param bridge
     *      bridge to which action will be applied
     * @param card 
     *      card applied
     * @param firstPlayer
     *      true if the first player apply the action
     * @return
     *      true if action can proceed
     */
    protected boolean generalApply(Round round){
        
        if (round.getLastTurn().isMute()){
            return false;
        }
        return true;
    }
    
//***************************** GETTER *****************************************
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the card name used for display
     */
    public String getName() {
        return CardsEnum.CARDS[id-1].getName();
    }

    /**
     * @return the image file name, do not contain extension
     */
    public String getImageName() {
        return CardsEnum.CARDS[id-1].getImageName();
    }

    /**
     * @return the decription of the card usage and effects
     */
    public String getDescription() {
        return CardsEnum.CARDS[id-1].getDescription();
    }

    /**
     * @return the belongPlayer1
     */
    public boolean isBelongPlayer1() {
        return belongPlayer1;
    }
    
    /**
     * return the owner of the card
     * @param round
     * @return
     */
    protected PlayerState getOwnerPLayer(Round round){
        if(this.isBelongPlayer1()){
           return round.getLastPlayerStateOne();
        }
        return round.getLastPlayerStateTwo();
    }
    
    protected PlayerState getNotOwnerPlayer(Round round){
        if(this.isBelongPlayer1()){
           return round.getLastPlayerStateTwo();
        }
        return round.getLastPlayerStateOne();
    }
}
