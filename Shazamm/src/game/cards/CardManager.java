/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.cards;

import java.util.ArrayList;
import java.util.LinkedList;
import static game.Config.HAND_REFILL_SIZE;
import java.util.Collections;
import java.util.Random;
import static game.Config.SHUFFLE_STEPS;

/**
 *
 * @author darven
 */
public class CardManager implements Cloneable {
    //deck, cards available to draw to hand
    private LinkedList<AbstractCard> deck;
    
    //cards discarded from hand
    private LinkedList<AbstractCard> discard;
    
    //player's hand
    private ArrayList<AbstractCard> hand; 
    
    //cards discarded last time discardAll was called
    private LinkedList<AbstractCard> lastDiscard;
    
    //true if this belongs to player 1
    private boolean belongPlayer1; 

//***************************** CONSTRUCTOR ************************************    
    
    /**
     * CONSTRUCTOR
     * use default initialisation of deck (see private void initDeck())
     * @param belongPlayer1
     *      true if this belongs to player 1 (used when applying card's effects)
     */
    public CardManager(boolean belongPlayer1) {
        this.belongPlayer1 = belongPlayer1;
        
        //deck initialisation
        this.deck = new LinkedList<>();
        this.initDeck();
        this.shuffleDeck();
        
        //discard and hand initialisation
        this.lastDiscard = new LinkedList<>();
        this.discard = new LinkedList<>();
        this.hand = new ArrayList<>();
    }
    
//***************************** DECK SHUFFLE ***********************************
    
    /**
     * suffle deck by swaping random cards SHUFFLE_STEPS times
     */
    private void shuffleDeck(){
        for (int i=0; i < SHUFFLE_STEPS; i++) {
            this.swapDeckCards();
        }
    }
    
    /**
     * swap two random cards in deck
     */
    private void swapDeckCards(){
        //generation of two random indexes
        Random random = new Random();
        int firstIndex = random.nextInt(this.deck.size());
        int secondIndex = random.nextInt(this.deck.size());
        
        //swap the cards at the two indexes
        AbstractCard tempCard = this.deck.get(firstIndex);
        this.deck.set(firstIndex, this.deck.get(secondIndex));
        this.deck.set(secondIndex, tempCard);
    }    

//***************************** CARD MANIPULATION ******************************
    
    /**
     * initialize deck with a single exemplary of each card
     */
    private void initDeck() {
        for (int i = 14; i > 0; i--){
            this.deck.add(AbstractCard.create(i, this.belongPlayer1));
        }
    }
    
    /**
     * remove a card from deck to add it to hand
     * @return 
     *      false if no card can be drawn (no card left)
     */
    public boolean drawCard(){
        if (this.deck.isEmpty()){
            return false;
        }else{
            AbstractCard card = this.deck.pollFirst();
            this.hand.add(card);
            return true;
        }
    }
    
    /**
     * refill hand by drawing cards untill hand has the minimam amount of cards
     *      if no cards are left, use discardToDeck() automaticaly
     *      WARNING : HAND_REFILL_SIZE has to be inferior or equal
     *      to the size of the deck
     */
    public void refillHand(){
        while  (this.hand.size() < HAND_REFILL_SIZE){
            //draw cards while hand has less than the minimum amount of cards
            boolean cardsLeft = this.drawCard();
            
            //if no ards are left in the deck
            if (!cardsLeft){
                this.discardToDeck();
            }
        }
    }
    
    /**
     * discard inputed card if found in deck
     * @param card 
     *      card to discard
     */
    private void discardCard(AbstractCard card){
        if(this.hand.remove(card)){
            this.discard.add(card);
            this.lastDiscard.add(card);
        }
    }
    
    /**
     * discard all inputed cards if found in the deck, and set them as last 
     *      discarded array of cards
     * @param cards 
     *      cards to discard
     */
    public void discardAll(ArrayList<AbstractCard> cards){
        this.lastDiscard.clear();
        for (AbstractCard card : cards){
            this.discardCard(card);
        }
    }
    
    /**
     * remove all cards from discard and put them back in deck, then shuffle the
     *      deck
     */
    public void discardToDeck(){
        this.deck.addAll(this.getDiscard());
        this.getDiscard().clear();
        this.shuffleDeck();
    }

//***************************** GETTER *****************************************

    /**
     * @return 
     *      clone of hand
     */
    public ArrayList<AbstractCard> getHand(){
        return this.hand;
    }

    /**
     * @return the belongPlayer1
     */
    public boolean isBelongPlayer1() {
        return belongPlayer1;
    }

    /**
     * @return the discard
     */
    private LinkedList<AbstractCard> getDiscard() {
        return discard;
    }

    /**
     * @return the lastDiscard
     */
    public LinkedList<AbstractCard> getLastDiscard() {
        return this.lastDiscard;
    }
    
//******************************************************************************
    
    /**
     * clone cardManager:
     *      clone hand, deck and discard to avoid synchronisation
     * @return
     *      cloned object
     * @throws CloneNotSupportedException 
     */
    @Override
    public Object clone() throws CloneNotSupportedException{
        CardManager clone = (CardManager) super.clone();
        clone.deck = (LinkedList<AbstractCard>) clone.deck.clone();
        clone.discard = (LinkedList<AbstractCard>) clone.getDiscard().clone();
        clone.hand = (ArrayList<AbstractCard>) clone.hand.clone();
        return clone;
    }
    
    @Override
    public String toString(){
        //print hand content
        String str = "Hand:\n";
        for (AbstractCard abstractCard : hand) {
           str+=abstractCard.getName()+"\n";
        }
        
        //print discard content
        str += "Discard:\n";
        for (AbstractCard abstractCard : discard) {
            str+=abstractCard.getName()+"\n";
        }
        
        //print deck content
        str += "Deck:\n";
        for (AbstractCard abstractCard : deck) {
            str+=abstractCard.getName()+"\n";
        }
        return str;
    }
}
