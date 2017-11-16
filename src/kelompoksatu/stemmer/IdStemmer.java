/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kelompoksatu.stemmer;

import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author rynfd
 */
public class IdStemmer {
    
    private final static char[] CONSONANTS = {'b','c','d','f','g','h','j','k',
        'l','m','n','p','q','r','s','t','v','w','x','y','z'};
    
    private final static char[] VOWELS = {'a','i','u','e','o'};

    private final static PrefixSuffixPair[] FORBIDDEN_PREFIX_SUFFIX_PAIRS = {
        new PrefixSuffixPair("be", "i"),
        new PrefixSuffixPair("di", "an"),
        new PrefixSuffixPair("ke", "i"),
        new PrefixSuffixPair("ke", "kan"),
        new PrefixSuffixPair("me", "an"),
        new PrefixSuffixPair("se", "i"),
        new PrefixSuffixPair("se", "kan"),
        new PrefixSuffixPair("te", "an")
    };
    
//Definisi Rule 21 pada array setara dengan:
//    
//    Rule r = new Rule("21", 
//            new RuleMatcher() {
//                @Override
//                public boolean match(String word) {
//                    if(word.startsWith("per")){
//                        if(isVowel(word.charAt(3))){
//                            return true;
//                        }
//                    }
//                    return false;
//                }},
//            new RuleStemmer() {
//                @Override
//                public String process(String word) {
//                    ArrayList<String> possibleBaseWords = new ArrayList<>();
//                    String initialStem = word.replace("per","");
//                
//                    possibleBaseWords.add(initialStem);
//                    possibleBaseWords.add("r" + initialStem);
//                
//                    return BaseWordsManager.getFirstMatch(possibleBaseWords);
//                }
//            });
    
    private final static Rule[] RULES = {
        //rule 21
            new Rule("21", 
            new RuleMatcher() {
                @Override
                public boolean match(String word) 
                {//peraturan
                    if(word.startsWith("per")){
                        if(isVowel(word.charAt(3))){
                            return true;
                        }
                    }
                    return false;
                }},
            new RuleStemmer() {
                @Override
                public String process(String word) {
                    ArrayList<String> possibleBaseWords = new ArrayList<>();
                    String initialStem = word.replaceFirst("per","");
          
                    possibleBaseWords.add(initialStem);
                    possibleBaseWords.add("r" + initialStem);
                
                    return BaseWordsManager.getFirstMatch(possibleBaseWords, word);
                }
            }),
        new Rule("21", 
                (w) -> w.startsWith("per") && isVowel(w, 3),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("per","");
                
                possibleBaseWords.add("r" + initialStem);
                possibleBaseWords.add(initialStem);
                
                return BaseWordsManager.getFirstMatch(possibleBaseWords,
                        w);
                }),
        
        new Rule("28", 
                (w) -> w.startsWith("pen") && isVowel(w.charAt(3)),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("pen","");
                
                possibleBaseWords.add("n" + initialStem);
                possibleBaseWords.add("t" + initialStem);
                
                return BaseWordsManager.getFirstMatch(possibleBaseWords,
                        w);
                }),
        //rule 23, 24
        //........................
        new Rule("30", 
                (w) -> w.startsWith("peng") && isVowel(w.charAt(4)),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("peng","");
                
                possibleBaseWords.add(initialStem);
                possibleBaseWords.add("k" + initialStem);
                
                return BaseWordsManager.getFirstMatch(possibleBaseWords,
                        w);
                }),
        //rule 31
        new Rule("31", 
                (w) -> w.startsWith("peny") && isVowel(w, 4),
                (w) -> w.replaceFirst("peny","s")),
        //rule 32
        new Rule("32", 
                (w) -> w.startsWith("pel") && isVowel(w, 3),
                (w) -> {
                
                 if(w.equals("pelajar")){
                     return "ajar";
                 }
                    
                return w.replaceFirst("pe","");
                })
    };
    
    private static boolean isConsonant(char c){
        Stream<Character> consonantStream = IntStream.range(0, CONSONANTS.length).mapToObj(i -> CONSONANTS[i]);
        return consonantStream.anyMatch((s) -> s == c);
    }
    
     private static boolean isConsonant(String word, int pos){
        //Apabila posisi diluar panjang string, return false
        if(word.length() < pos+1){
            return false;
        }
        
        return isConsonant(word.charAt(pos));
    }
    
    private static boolean isVowel(char c){
        Stream<Character> vowelStream = IntStream.range(0, VOWELS.length).mapToObj(i -> VOWELS[i]);
        return vowelStream.anyMatch((s) -> s == c);
    }
    
    private static boolean isVowel(String word, int pos){
        //Apabila posisi diluar panjang string, return false
        if(word.length() < pos+1){
            return false;
        }
        
        return isVowel(word.charAt(pos));
    }
    private static String preprocess(String word){
        return word.trim().toLowerCase();
    }
    
     private static boolean containsForbiddenPair(String word){
        String prefix;
        String suffix;
        
        for(PrefixSuffixPair pair : FORBIDDEN_PREFIX_SUFFIX_PAIRS){
            prefix = pair.getPrefix();
            suffix = pair.getSuffix();
            
            if(word.startsWith(prefix) && word.endsWith(suffix)){
                System.out.println("Word contains illegal "
                        + "prefix and suffix pair: "
                        + pair.getPrefix() + "- & -" + pair.getSuffix());       
                return true;
            }
        }
        return false;
    }
    
    public static String stem(String word){
        String originalQuery = preprocess(word);
        
        //Cek apakah kata yang diberikan adalah kata dasar
        if(BaseWordsManager.isBaseWord(originalQuery)){
            System.out.println("Word given is a base word.");
            return originalQuery;
        }
        
        String processedQuery = originalQuery;
        String tempQuery = processedQuery;
        
        int ruleCount = 0;
        String previousRule = "";
        String currentRule;
        
        while(ruleCount < 3){
            
            currentRule = previousRule;
            //Cek jika kata mengandung pasangan prefix-suffix yang dilarang
            if(containsForbiddenPair(originalQuery)){
                return originalQuery;
            }
            
            for(Rule rule : RULES){
                if(rule.match(processedQuery)){
                    currentRule = rule.definition();
                    System.out.println(rule.definition() + "applied.");
                    tempQuery = rule.apply(processedQuery);
                    if(BaseWordsManager.isBaseWord(tempQuery)){
                        break;
                    }
                }
            }

            /*
            Jika aturan sekarang dan sebelumnya sama, maka ada 2 kemungkinan:
            1. Tidak ada aturan yang match sama sekali pada iterasi terakhir
            2. Aturan yang match pada iterasi terakhir sama dengan aturan
               sebelumnya.
            Kedua kemungkinan menghentikan iterasi.
            */
            if(currentRule.equals(previousRule)){
                System.out.println("Process stops because either "
                        + "no rule matches or "
                        + "the same rule are matched twice in a row.");
                break;
            } else {
                previousRule = currentRule;
                processedQuery = tempQuery;
            }
            
            //Cek apakah kata sudah berbentuk kata dasar
            if(BaseWordsManager.isBaseWord(originalQuery)){
                break;
            }
            
            ruleCount++;
        }
        
        System.out.println("Iterated " + ruleCount + " times.");
        
        if(BaseWordsManager.isBaseWord(processedQuery)){
            return processedQuery;
        }
        
        return originalQuery;
    }
}
