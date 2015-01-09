import java.util.*;
import java.lang.*;
import java.io.*;
import java.net.*;
 
/* Name of the class has to be "Main" only if the class is public. */
class Hangman
{
 
	String json;
	final String ADDRESS = "http://gallows.hulu.com/play?code=adl040@ucsd.edu";
	String token_url = "";
	final String STATUS = "status";
	final String TOKEN = "token";
	final String GUESSES = "remaining_guesses";
	final String STATE = "state";
	static final String ALIVE = "ALIVE";
	static final String DEAD = "DEAD";
	static final String FREE = "FREE";
	static String status;
	int stat_ind1, stat_ind2;
	String token;
	int tok_ind1, tok_ind2;
	int guesses;
	String state;
	int state_ind1,state_ind2;
	
	int spaces;
	static boolean keep_going;

	File dir = new File("./words/");
	File[] files = dir.listFiles();
	String [] words;
	int [] lettercount = new int[26];
	boolean [] guessed = new boolean[26];
	boolean wordmatch;
 
	public static void main (String[] args) throws java.lang.Exception
	{
		Hangman hang = new Hangman();
		keep_going = true;
		while (keep_going) {
			hang.getNewPerson();
			if ( status.equals(ALIVE) ) {
				do {
					hang.guess();
				} while ( status.equals(ALIVE) );
			}
			else if ( status.equals(DEAD) ) {
				System.out.println ("This person is dead :(");
			}
			else if ( status.equals(FREE) )
				System.out.println ("This person is freed! :)");
			else {}
			hang.promptNext();
		}
	}
 
	public void getNewPerson() {
		/* new person = haven't guessed any letters */
		for ( int i = 0; i < guessed.length; i++ )
			guessed[i] = false;
		try {
			URL oracle = new URL(ADDRESS);
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
			json = in.readLine();
			System.out.println(json);
			// get status
			stat_ind1 = json.indexOf('"', json.indexOf(STATUS)+STATUS.length()+1)+1;
			stat_ind2 = json.indexOf('"', stat_ind1);
			status = json.substring(stat_ind1, stat_ind2);
			// get token
			tok_ind1 = json.indexOf('"', json.indexOf(TOKEN)+TOKEN.length()+1)+1;
			tok_ind2 = json.indexOf('"', tok_ind1);
			token = json.substring(tok_ind1, tok_ind2);
			// get guesses
			guesses = json.charAt(json.indexOf(GUESSES)+GUESSES.length()+3) - '0';
			// get state
			state_ind1 = json.indexOf('"', json.indexOf(STATE)+STATE.length()+1)+1;
			state_ind2 = json.indexOf('"', state_ind1);
			state = json.substring(state_ind1, state_ind2);
			
		} catch (IOException e) {
			System.err.println(e);
		}
	}
 
	public void guess ( ) {
		token_url = ADDRESS + "&token=" + token + "&guess=";
		// count spaces to determine number of words
		spaces = 1;
		for ( int i = 0; i < state.length(); i++ ) {
			if ( state.charAt(i) == ' ' )
				spaces++;
		}
		words = new String [spaces];
		boolean one = false;
		boolean two = false;
		int arr_ind = 0;
		int start = 0;
		for ( int i = 0; i <= state.length(); i++ ) {
			if ( i == state.length() || state.charAt(i) == ' ' ) {
				words[arr_ind] = state.substring(start,i);
				/* is there an empty one-lettered word? */
				if (words[arr_ind].length() == 1 && words[arr_ind].charAt(0)=='_' )
					one = true;
				/* is there an empty two-lettered word? */
				if (words[arr_ind].length() == 2 ) {
					two = true;
					for ( int j = 0; j < 2; j++ ) {
						if ( words[arr_ind].charAt(j)!='_' )
							two = false;
					}
				}
				start = i+1;
				arr_ind++;
			}
		}
		
		boolean madeGuess = false;
		/* if there's a one lettered word, we make our guess with that in consideration */
		if (one == true)
			madeGuess = guessOneLetterWord();
		/* if there's a 2 lettered word, we make our guess with that in consideration */
		if ( two == true && madeGuess == false )
			madeGuess = guessTwoLetterWord();
		
		if (madeGuess == false)
			guessByFrequency();
		
		
		try {
			URL oracle = new URL(token_url);
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
			json = in.readLine();
			System.out.println(json);
			// get status
			stat_ind1 = json.indexOf('"', json.indexOf(STATUS)+STATUS.length()+1)+1;
			stat_ind2 = json.indexOf('"', stat_ind1);
			status = json.substring(stat_ind1, stat_ind2);
			// get guesses
			guesses = json.charAt(json.indexOf(GUESSES)+GUESSES.length()+3) - '0';
			// get state
			state_ind1 = json.indexOf('"', json.indexOf(STATE)+STATE.length()+1)+1;
			state_ind2 = json.indexOf('"', state_ind1);
			state = json.substring(state_ind1, state_ind2);
			
		} catch (IOException e) {
			System.err.println(e);
		}
		
	}
	
	public boolean guessOneLetterWord() {
		/* one lettered word could be "a" or "i"*/
		boolean a_guessed = false;
		boolean i_guessed = false;
		for ( int j = 0; j < state.length(); j++ ) {
			if ( guessed['a' - 'a'] == true )
				a_guessed = true;
			else if ( guessed['i' - 'a'] == true )
				i_guessed = true;
		}
		if ( a_guessed && i_guessed )
			return false;
		else if ( a_guessed ) {
			formGuess('i');
			return true;
		}
		else if ( i_guessed ) {
			formGuess('a');
			return true;
		}
		int a = 0;
		int i = 0;
		String dict_entry;
		for (File child : files) {
			/* for each file, check all the words that could be in the 
			answer for the number of a's and i's */
			try {
				BufferedReader reader = new BufferedReader(new FileReader(child));
				while( (dict_entry = reader.readLine()) != null ) {					
					for ( int k = 0; k < words.length; k++ ) {
						if ( words[k].length() == dict_entry.length() ) {
							for ( int j = 0; j < dict_entry.length(); j++ ) {
								if ( dict_entry.charAt(j) == 'A' || dict_entry.charAt(j) == 'a' )
									a++;
								if ( dict_entry.charAt(j) == 'I' || dict_entry.charAt(j) == 'i' )
									i++;
							}
						}
					}
				}
			} catch ( IOException e ) {
				System.err.println(e);
				return false;
			}
			
		}
		if ( a > i )
			formGuess('a');
		else if ( a < i ) 
			formGuess('i');
		/* in the case of a tie, the guess is determined randomly */
		else {			
			double rand = Math.random()*2;
			if ( rand < 1.0 )
				formGuess('a');
			else
				formGuess('i');
		}
		System.out.println ("ONE");
		return true;
	}
	
	public boolean guessTwoLetterWord() {
		String dict_entry;
		/* resets the lettercount array */
		for ( int i = 0; i < lettercount.length; i++ ) 
			lettercount[i] = 0;
			
		if ( guessed['a'-'a'] == true && guessed['e'-'a'] == true && guessed['i'-'a'] == true &&
			 guessed['o'-'a'] == true && guessed['u'-'a'] == true && guessed['y'-'a'] == true )
			return false;

		for (File child : files) {
			/* for each file, check all the 2-lettered words that could be in the 
			for vowels */
			try {
				BufferedReader reader = new BufferedReader(new FileReader(child));
				while( (dict_entry = reader.readLine()) != null ) {					
					for ( int k = 0; k < words.length; k++ ) {
						if ( dict_entry.length() == 2) {
							for ( int j = 0; j < dict_entry.length(); j++ ) {
								if ( dict_entry.charAt(j) == 'A' || dict_entry.charAt(j) == 'a' )
									lettercount['a' - 'a']++;
								if ( dict_entry.charAt(j) == 'E' || dict_entry.charAt(j) == 'e' )
									lettercount['e' - 'a']++;
								if ( dict_entry.charAt(j) == 'I' || dict_entry.charAt(j) == 'i' )
									lettercount['i' - 'a']++;
								if ( dict_entry.charAt(j) == 'O' || dict_entry.charAt(j) == 'o' )
									lettercount['o' - 'a']++;
								if ( dict_entry.charAt(j) == 'U' || dict_entry.charAt(j) == 'u' )
									lettercount['u' - 'a']++;
								if ( dict_entry.charAt(j) == 'Y' || dict_entry.charAt(j) == 'y' )
									lettercount['y' - 'a']++;
							}
						}
					}
				}
			} catch ( IOException e ) {
				System.err.println(e);
				return false;
			}
			
		}
		/* don't count the letters that have already been guessed */
		for ( int i = 0; i < guessed.length; i++ ) {
			if ( guessed[i] == true ) {
				lettercount[i] *= -1;
			}
		}
		
		/* find the letter with the highest frequency */
		int ind = -1;
		int max = 0;
		for ( int i = 0; i < lettercount.length; i++ ) {
			if (lettercount[i] > max ) {
				max = lettercount[i];
				ind = i;
			}
			/* pick randomly if there are more than one letter with the same frequency */
			else if ( lettercount[i] == max ) {
				double rand = Math.random()*2;
				if ( rand < 1.0 ) {
					max = lettercount[i];
					ind = i;
				}
				else {}
			}
		}
		formGuess( (char)(ind + 'a'));
		
		System.out.println ("TWO LETTERS");
		return true;
	}
	
	public void guessByFrequency ( ) {
		String dict_entry;
		/* resets the lettercount array */
		for ( int i = 0; i < lettercount.length; i++ ) 
			lettercount[i] = 0;
		
		/* build letter frequency array */
		for (File child : files) {
			/* for each file, check all the words that could be in the 
			answer for the most frequent character */
			try {
				BufferedReader reader = new BufferedReader(new FileReader(child));
				while ( (dict_entry = reader.readLine()) != null ) {
					dict_entry = dict_entry.toUpperCase();
					for ( int k = 0; k < words.length; k++ ) {
						wordmatch = false;
						/* if a dictionary word matches the length of a hangman word... */
						if ( words[k].length() == dict_entry.length() ) {
							wordmatch = true;
							/* ... and the letters don't conflict with the guessed letters in 
							the hangman word ... */
							for ( int j = 0; j < words[k].length(); j++ ) {
								if ( words[k].charAt(j) >= 'A' && words[k].charAt(j) <= 'Z' ) {
									/* no match */
									if ( dict_entry.charAt(j) != words[k].charAt(j) ) {
										wordmatch = false;
									}	
								}
								else if ( words[k].charAt(j) >= 'a' && words[k].charAt(j) <= 'z' ) {
									/* no match */
									if ( dict_entry.toLowerCase().charAt(j) != words[k].charAt(j) ) {
										wordmatch = false;
									}	
								}
							}
							/* then we use that word for the letter frequency array */
							if ( wordmatch == true ) {
								for ( int j = 0; j < dict_entry.length(); j++ ) {
									if ( words[k].charAt(j) == '_' ) {
										if ( dict_entry.charAt(j) >= 'A' && dict_entry.charAt(j) <= 'Z' )
											lettercount[dict_entry.charAt(j)-'A']++;
										else if ( dict_entry.charAt(j) >= 'a' && dict_entry.charAt(j) <= 'z' )
											lettercount[dict_entry.charAt(j)-'a']++;
									}
									else {}
								}
							}
						}
					}
				}				
			} catch ( IOException e ) {
				System.err.println(e);
				return;
			}
		}
		
		/* don't count the letters that have already been guessed */
		for ( int i = 0; i < guessed.length; i++ ) {
			if ( guessed[i] == true ) {
				lettercount[i] *= -1;
			}
		}
		
		
		/* find the letter with the highest frequency */
		int ind = -1;
		int max = 0;
		for ( int i = 0; i < lettercount.length; i++ ) {
			if (lettercount[i] > max ) {
				max = lettercount[i];
				ind = i;
			}
			/* pick randomly if there are more than one letter with the same frequency */
			else if ( lettercount[i] == max ) {
				double rand = Math.random()*2;
				if ( rand < 1.0 ) {
					max = lettercount[i];
					ind = i;
				}
				else {}
			}
		}
		formGuess( (char)(ind + 'a'));
		System.out.println ("BY FREQENCY");
	}
	
	public void formGuess ( char ch ) {
		token_url = ADDRESS + "&token=" + token + "&guess=";
		token_url += ch;
		guessed[ch-'a'] = true;
		System.out.println ( token_url );
	}
	
	public void promptNext ( ) {
		Scanner in = new Scanner (System.in);
		char yn;
		do {
			System.out.print( "Save another person? (Y/N) " );
			yn = in.nextLine().charAt(0);
		} while ( yn != 'Y' && yn != 'y' && yn != 'N' && yn != 'n' );
		if ( yn == 'Y' || yn == 'y' )
			keep_going = true;
		else
			keep_going = false;
	}
}