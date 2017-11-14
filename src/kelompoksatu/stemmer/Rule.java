/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kelompoksatu.stemmer;

/**
 *
 * @author rynfd
 */
public class Rule {
    private RuleMatcher rm;
    private RuleStemmer rs;
    private String ruleDefiniton;

    public Rule(String ruleDefiniton, RuleMatcher rm, RuleStemmer rs) {
        this.rm = rm;
        this.rs = rs;
        this.ruleDefiniton = ruleDefiniton;
    }
    
    public String apply(String word){
        return rs.process(word);
    }
    
    public boolean match(String word){
        return rm.match(word);
    }
    
    public String definition(){
        return this.ruleDefiniton;
    }
    
    
}
