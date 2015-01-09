import java.util.*;
import java.lang.*;
import java.io.*;

public class Decryptor {
    
    String message = "";
    String [] words;
    boolean one_lettered_word;
    
    public static void main (String[] args) {
        Decryptor machine = new Decryptor();
        // machine.promptUser();
        if (machine.getMessageFromFile() == false)
            return;
        machine.makeWordsArray();
        machine.rankLetterCommonality();
    }
    
    public void promptUser ( ) {
        Scanner in = new Scanner (System.in);
        do {
            System.out.print( "/nEnter the message to be decrypted: " );
            message = in.nextLine();
        } while ( message == null );
        System.out.println();
    }
    
    public boolean getMessageFromFile ( ) {
        try {
            File cryptogram = new File ("cryptogram.txt");
            Scanner in = new Scanner(new FileReader("cryptogram.txt"));
            while (in.hasNext()) {
                message += in.nextLine();
            }
            System.out.println ( "/nThe message to be decrypted is: " );
            System.out.println ( message );
        }catch ( IOException e ) {
            System.err.println(e);
            return false;
        }
        return true;
    }
    
    public void makeWordsArray ( ) {
        // count spaces to determine number of words
        int spaces = 1;
		for ( int i = 0; i < message.length(); i++ ) {
			if ( message.charAt(i) == ' ' )
				spaces++;
		}
		words = new String [spaces];
        int arr_ind = 0;
		int start = 0;
		for ( int i = 0; i <= message.length(); i++ ) {
			if ( i == message.length() || message.charAt(i) == ' ' 
                || message.charAt(i) == ',' || message.charAt(i) == '.' ) {
                if (i != start) {
                    words[arr_ind] = message.substring(start,i);
                    /* is there a one-lettered word? */
                    if (words[arr_ind].length() == 1)
                        one_lettered_word = true;
                    arr_ind++;
                }
                start = i+1;
			}            
		}
    }
    
    public void rankLetterCommonality ( ) {
        Letter [] letters = new Letter [message.length()];
        int arr_ind = 0;
        for ( int i = 0; i < message.length(); i++ ) {
            char ch = message.charAt(i);
            // if the array is empty, just insert
            if ( i == 0 ) {
                letters[arr_ind] = new Letter(ch);
                arr_ind++;
            }
            // if not, search through previous entries for a previous occurence
            else {
                for ( int j = 0; j < arr_ind; j++ ) {
                    // if there is a match, then increment occurences
                    if ( letters[j].getLetter() == ch ) {
                        letters[j].increment();
                    }
                    // otherwise insert into the array
                    else {
                        letters[arr_ind] = new Letter(ch);
                        arr_ind++;
                    }
                }
            }
        }
    }
}