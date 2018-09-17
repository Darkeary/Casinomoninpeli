/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.*;
import util.PlayerHand;

/**
 *
 * @author Tuomas
 */
@Entity
@Table(name="statistics")
public class Statistic implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int statisticId;
    
    @OneToMany
    private Set<PlayerHand> gameHands;
    
    @OneToOne
    @JoinColumn(name="winning_hand_id", nullable=false)
    private int winningHandId;
    
    
}
