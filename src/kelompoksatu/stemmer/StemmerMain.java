/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kelompoksatu.stemmer;

import java.util.Scanner;
/**
 *
 * @author rynfd
 */
public class StemmerMain {
    public static void main(String[] args) {
        String query;
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter String: ");
        query = sc.next();
        
        System.out.println("Entered String: " + query);
        System.out.println("Stemmed String: " + IdStemmer.stem(query));
    }
}
