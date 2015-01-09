public class Letter {
    
    private char letter;
    private int occurences;
    
    public Letter ( char a ) {
        String temp = "" + a;
        letter = temp.toLowerCase().charAt(0);
        occurences = 1;
    }
    
    public char getLetter ( ) {
        return letter;
    }
    
    public int getOccurences ( ) {
        return occurences;
    }
    
    public void increment ( ) {
        occurences++;
    }
    
    public String toString ( ) {
        return letter + ": " + occurences + "/n";
    }

}