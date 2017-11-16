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

    private final static char[] GHQ = {'g','h','q'};

    private final static String[] INFLECTION_SUFFIXES = {
        "lah", "kah", "ku", "mu", "nya", "tah", "pun"
    };
    
    private final static String[] INFLECTION_SUFFIXES_PARTICLES = {
        "lah", "kah", "tah", "pun"
    };
    
    private final static String[] INFLECTION_SUFFIXES_POSSESIVE_PRONOUNS = {
       "ku", "mu", "nya"
    };
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
    
    private final static Rule[] RULES = {
        
        //rule 21
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
        //rule 23
        new Rule("23/24",
                (w) -> w.startsWith("per"),
                (w) -> w.replaceFirst("per", "")),
        //rule 28
        new Rule("28", 
                (w) -> w.startsWith("pen") && isVowel(w, 3),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("pen","");
                
                possibleBaseWords.add("n" + initialStem);
                possibleBaseWords.add("t" + initialStem);
                
                return BaseWordsManager.getFirstMatch(possibleBaseWords,
                        w);
                }),
        //rule 30
        new Rule("30", 
                (w) -> w.startsWith("peng") && isVowel(w, 4),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("peng","");

                if(initialStem.startsWith("e")){
                    initialStem = initialStem.replaceFirst("e","");
                }
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
    
    private static boolean isGHQ(char c){
        Stream<Character> GHQStream = IntStream.range(0, GHQ.length).mapToObj(i -> GHQ[i]);
        return GHQStream.anyMatch((s) -> s == c);
    }
    
     private static boolean isGHQ(String word, int pos){
        //Apabila posisi diluar panjang string, return false
        if(word.length() < pos+1){
            return false;
        }
        
        return isGHQ(word.charAt(pos));
    }
     
     private static boolean isParticle(String inflectionSuffix){
         for(String particle : INFLECTION_SUFFIXES_PARTICLES){
             if(inflectionSuffix.equals(particle)){
                 return true;
             }
         }
         return false;
     }
    
    private static String preprocess(String word){
        return word.trim().toLowerCase();
    }
    
    private static String cutSuffix(String word, String suffix){
       return word.substring(0, word.length() - suffix.length());
    }
    
    private static String cutPossesivePronouns(String word){
        
        for(String possesive_pronoun : INFLECTION_SUFFIXES_POSSESIVE_PRONOUNS){
            if(word.endsWith(possesive_pronoun)){
                return cutSuffix(word, possesive_pronoun);
            }
        }
        
        return word;
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
        
        //-----------------------------------------------
        //LANGKAH 2 
        processedQuery = applyStep2(processedQuery);
        //C4 apakah kata sudah berbentuk kata dasar 
        System.out.println("Langkah 2: " + processedQuery);
        
        if(BaseWordsManager.isBaseWord(processedQuery)){
            return processedQuery;
        }
        
        //-----------------------------------------------
        //LANGKAH 3
        processedQuery = applyStep3(processedQuery);
        //Cek apakah kata sudah berbentuk kata dasar 
        if(BaseWordsManager.isBaseWord(processedQuery)){
            return processedQuery;
        }
        
        //-----------------------------------------------
        //LANGKAH 4
        processedQuery = applyStep4(processedQuery);
        //Cek apakah kata sudah berbentuk kata dasar
        if(BaseWordsManager.isBaseWord(processedQuery)){
            return processedQuery;
        }
        
        //Jika tidak ada di kamus, asumsikan query adalah kata dasar.
        return originalQuery;
    }
    
    /*
    Aplikasi aturan kedua
    */
    private static String applyStep2(String word){
       
        String result = word;
        String cut_suffix = null;
        
        for(String suffix : INFLECTION_SUFFIXES){
            if(word.endsWith(suffix)){
                result = cutSuffix(result, suffix);
                cut_suffix = suffix;
            }
        }
        
        //kalau tidak null, artinya ada suffix yang dipotong.
        if(cut_suffix != null){
            if(isParticle(cut_suffix)){
                result = cutPossesivePronouns(result);
            }
        }
        
        return result;
    }
    
     /*
    Aplikasi aturan ketiga
    */
    private static String applyStep3(String word){
        String result;
        
        if(word.endsWith("i")){
            result = cutSuffix(word,"i");
            
            if(BaseWordsManager.isBaseWord(applyStep4(result))){
                return result;
            } 
        }
        
        if(word.endsWith("an")){
            result = cutSuffix(word,"an");
            if(BaseWordsManager.isBaseWord(applyStep4(result))){
                return result;
            }
            
            if(result.endsWith("k")) {
                result = cutSuffix(result, "k");
                
                if(BaseWordsManager.isBaseWord(applyStep4(result))){
                    return result;
                }
            }
            
        }
        
        return word;
    }
    
    private static String applyStep4(String word){
        String result = word;
        String temp = result;
        
        int ruleCount = 0;
        String previousRule = "";
        String currentRule;
        
        while(ruleCount < 3){
            currentRule = previousRule;
            
            //Cek jika kata mengandung pasangan prefix-suffix yang dilarang
            if(containsForbiddenPair(word)){
                return word;
            }
            
            for(Rule rule : RULES){
                if(rule.match(result)){
                    currentRule = rule.definition();
                    System.out.println(rule.definition() + "applied.");
                    temp = rule.apply(result);
                    if(BaseWordsManager.isBaseWord(temp)){
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
                result = temp;
            }
            
            //Cek apakah kata sudah berbentuk kata dasar
            if(BaseWordsManager.isBaseWord(result)){
                break;
            }
            
            ruleCount++;
        }
        
        System.out.println("Rule 4 iterates " + ruleCount + " times.");
        
        return result;
    }
}
