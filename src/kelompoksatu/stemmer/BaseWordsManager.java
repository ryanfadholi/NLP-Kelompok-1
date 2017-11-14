/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kelompoksatu.stemmer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rynfd
 */
public class BaseWordsManager {
    private static String dbUri = "jdbc:sqlite:db/katadasar.sqlite";
    private static String matchQuery = "SELECT COUNT(*) AS result "
            + "FROM (SELECT * FROM tb_katadasar WHERE katadasar LIKE ? )";
    private static Connection kamusBaseWords = null;
    
    private static void initConnection(){
       try {
            
            kamusBaseWords = DriverManager.getConnection(
                    BaseWordsManager.dbUri);
            System.out.println("Connection to embedded SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("Error in establishing connection to SQLite: " 
                    + e.getMessage());
        }
    }

    public static boolean isBaseWord(String word){
        PreparedStatement stmt = null;       
        ResultSet rs = null;
        boolean queryResult = false;
        
        if(BaseWordsManager.kamusBaseWords == null){
            BaseWordsManager.initConnection();
        }
        
        try{
            stmt = kamusBaseWords.prepareStatement(
                BaseWordsManager.matchQuery);
            stmt.setString(1, word);
            rs = stmt.executeQuery();
            
            //get the result
            queryResult = rs.getBoolean("result");
            
            //close connections
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error in matching word in database: " +
                    e.getMessage());
        }
        
        return queryResult;
    }
    
    public static String getFirstMatch(Iterable<String> words){
        for(String word : words){
            if(isBaseWord(word)){
                return word;
            }
        }
        //return null if no words are identified as base word
        return null;
    }
    
    public static String getFirstMatch(Iterable<String> words, String defaultWord){
        String queryResult = getFirstMatch(words);
        
        if(queryResult == null){
            return defaultWord;
        }
        
        return queryResult;
    }
    
    public static ArrayList<Boolean> matchAll(Iterable<String> words){
        ArrayList<Boolean> result = new ArrayList<>();
        
        for(String word : words){
           result.add(isBaseWord(word));
        }
        
        return result;
    }
}
