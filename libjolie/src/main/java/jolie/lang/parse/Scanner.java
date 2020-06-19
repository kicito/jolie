/*
 * Copyright (C) 2006-2020 Fabrizio Montesi <famontesi@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package jolie.lang.parse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import jolie.lang.NativeType;

/**
 * Scanner implementation for the Jolie language parser.
 *
 * @author Fabrizio Montesi
 *
 */
public class Scanner {
	// @formatter:off
	/** Token types */
	public enum TokenType {
		EOF,				///< End Of File
		ID,					///< [a-z][a-zA-Z0-9]*
		COMMA,				///< ,
		DOT,				///< .
		INT,				///< [0-9]+
		TRUE,				///< true
		FALSE,				///< false
		LONG,				///< [0-9]+(l|L)
		DOUBLE,				///< [0-9]*"."[0-9]+(e|E)[0-9]+
		LPAREN,				///< (
		RPAREN,				///< )
		LSQUARE,			///< [
		RSQUARE,			///< ]
		LCURLY,				///< {
		RCURLY,				///< }
		//DOLLAR,			///< $
		STRING,				///< "[[:graph:]]*"
		INCREMENT,			///< ++
		MINUS,				///< The minus sign -
		ASTERISK,			///< *
		DIVIDE,				///< /
		ASSIGN,				///< =
		PLUS,				///< +
		ADD_ASSIGN,			///< +=
		MINUS_ASSIGN,		///< -=
		MULTIPLY_ASSIGN,	///< *=
		DIVIDE_ASSIGN,		///< %=
		SEQUENCE,			///< ;
		IF,					///< if
		ELSE,				///< else
		LANGLE,				///< <
		RANGLE,				///< >
		AT,					///< @
		LINKIN,				///< linkIn
		LINKOUT,			///< linkOut
		INSTANCE_OF,		///< instanceof
		EQUAL,				///< ==
		AND,				///< &&
		OR,					///< ||
		PARALLEL,			///< |
		NOT,				///< !
		CARET,				///< ^
		COLON,				///< :
		OP_OW,				///< OneWay
		OP_RR,				///< RequestResponse
		DEFINE, 			///< define
		MAJOR_OR_EQUAL,		///< >=
		MINOR_OR_EQUAL,		///< <=
		NOT_EQUAL,			///< !=
		NULL_PROCESS,		///< nullProcess
		WHILE,				///< while
		EXECUTION,			///< execution
		THROW,				///< throw
		DOCUMENTATION_FORWARD,
		DOCUMENTATION_BACKWARD,
		INSTALL,				///< install
		SCOPE,				///< scope
		SPAWN,				///< spawn
		THIS,				///< this
		COMPENSATE,			///< comp
		EXIT,				///< exit
		INCLUDE,			///< include
		CONSTANTS,			///< constants
		POINTS_TO,			///< ->
		QUESTION_MARK,		///< ?
		ARROW,				///< =>
		DEEP_COPY_LEFT,		///< <<
		DEEP_COPY_WITH_LINKS_LEFT,	///< <<-
		RUN,				///< run
		UNDEF,				///< undef
		HASH,				///< #
		PERCENT_SIGN,		///< %
		FOR,				///< for
		FOREACH,			///< foreach
		WITH,				///< with
		DECREMENT,			///< --
		IS_STRING,			///< is_string
		IS_INT,				///< is_int
		IS_DOUBLE,			///< is_double
		IS_BOOL,			///< is_bool
		IS_LONG,			///< is_long
		IS_DEFINED,			///< is_defined
		CAST_INT,			///< int
		CAST_STRING,		///< string
		CAST_DOUBLE,		///< double
		CAST_BOOL,			///< bool
		CAST_LONG,			///< long
		SYNCHRONIZED,		///< synchronized
		THROWS,				///< throws
		CURRENT_HANDLER,	///< cH
		INIT,				///< init
		PROVIDE,			///< provide
		NEWLINE,			///< a newline token
		ERROR				///< Scanner error
	}
	// @formatter:off
	
	/*
	 * Map of unreserved keywords,
	 * which can be considered as IDs in certain places (e.g. variables).
	 */
	private static final Map< String, TokenType > unreservedKeywords = new HashMap<>();
	
	static {
		// Initialise the unreserved keywords map.
		unreservedKeywords.put( "OneWay", TokenType.OP_OW );
		unreservedKeywords.put( "RequestResponse", TokenType.OP_RR );
		unreservedKeywords.put( "linkIn", TokenType.LINKIN );
		unreservedKeywords.put( "linkOut", TokenType.LINKOUT );
		unreservedKeywords.put( "if", TokenType.IF );
		unreservedKeywords.put( "else", TokenType.ELSE );
		unreservedKeywords.put( "include", TokenType.INCLUDE );
		unreservedKeywords.put( "define", TokenType.DEFINE );
		unreservedKeywords.put( "nullProcess", TokenType.NULL_PROCESS );
		unreservedKeywords.put( "while", TokenType.WHILE );
		unreservedKeywords.put( "execution", TokenType.EXECUTION );
		unreservedKeywords.put( "install", TokenType.INSTALL );
		unreservedKeywords.put( "this", TokenType.THIS );
		unreservedKeywords.put( "synchronized", TokenType.SYNCHRONIZED );
		unreservedKeywords.put( "throw", TokenType.THROW );
		unreservedKeywords.put( "scope", TokenType.SCOPE );
		unreservedKeywords.put( "spawn", TokenType.SPAWN );
		unreservedKeywords.put( "comp", TokenType.COMPENSATE );
		unreservedKeywords.put( "exit", TokenType.EXIT );
		unreservedKeywords.put( "constants", TokenType.CONSTANTS );
		unreservedKeywords.put( "undef", TokenType.UNDEF );
		unreservedKeywords.put( "for", TokenType.FOR );
		unreservedKeywords.put( "foreach", TokenType.FOREACH );
		unreservedKeywords.put( "is_defined", TokenType.IS_DEFINED );
		unreservedKeywords.put( "is_string", TokenType.IS_STRING );
		unreservedKeywords.put( "is_int", TokenType.IS_INT );
		unreservedKeywords.put( "is_bool", TokenType.IS_BOOL );
		unreservedKeywords.put( "is_long", TokenType.IS_LONG );
		unreservedKeywords.put( "is_double", TokenType.IS_DOUBLE );
		unreservedKeywords.put( "instanceof", TokenType.INSTANCE_OF );
		unreservedKeywords.put( NativeType.INT.id(), TokenType.CAST_INT );
		unreservedKeywords.put( NativeType.STRING.id(), TokenType.CAST_STRING );
		unreservedKeywords.put( NativeType.BOOL.id(), TokenType.CAST_BOOL );
		unreservedKeywords.put( NativeType.DOUBLE.id(), TokenType.CAST_DOUBLE );
		unreservedKeywords.put( NativeType.LONG.id(), TokenType.CAST_LONG );
		unreservedKeywords.put( "throws", TokenType.THROWS );
		unreservedKeywords.put( "cH", TokenType.CURRENT_HANDLER );
		unreservedKeywords.put( "init", TokenType.INIT );
		unreservedKeywords.put( "with", TokenType.WITH );
		unreservedKeywords.put( "true", TokenType.TRUE );
		unreservedKeywords.put( "false", TokenType.FALSE );
		unreservedKeywords.put( "provide", TokenType.PROVIDE );
	}

	/**
	 * This class represents an input token read by the Scanner class.
	 *
	 * @see Scanner
	 * @author Fabrizio Montesi
	 * @version 1.0
	 *
	 */
	public static class Token
	{
		private final TokenType type;
		private final String content;
		private final boolean isUnreservedKeyword;

		/**
		 * Constructor. The content of the token will be set to "".
		 *
		 * @param type the type of this token
		 */
		public Token( TokenType type )
		{
			this.type = type;
			this.content = "";
			this.isUnreservedKeyword = false;
		}

		/**
		 * Constructor.
		 *
		 * @param type the type of this token
		 * @param content the content of this token
		 */
		public Token( TokenType type, String content )
		{
			this.type = type;
			this.content = content;
			this.isUnreservedKeyword = false;
		}

		/**
		 * Constructor.
		 *
		 * @param type the type of this token
		 * @param content the content of this token
		 * @param isUnreservedKeyword specifies whether this token is an unreserved keyword
		 */
		public Token( TokenType type, String content, boolean isUnreservedKeyword )
		{
			this.type = type;
			this.content = content;
			this.isUnreservedKeyword = isUnreservedKeyword;
		}

		/**
		 * Returns the content of this token.
		 *
		 * @return the content of this token
		 */
		public String content()
		{
			return content;
		}

		/**
		 * Returns the type of this token.
		 *
		 * @return the type of this token
		 */
		public TokenType type()
		{
			return type;
		}

		/**
		 * Returns <code>true</code> if this token can be considered as a valid
		 * value for a constant, <code>false</code> otherwise.
		 * @return <code>true</code> if this token can be considered as a valid
		 * value for a constant, <code>false</code> otherwise
		 */
		public boolean isValidConstant()
		{
			return type == TokenType.STRING
				|| type == TokenType.INT
				|| type == TokenType.ID
				|| type == TokenType.LONG
				|| type == TokenType.TRUE
				|| type == TokenType.FALSE
				|| type == TokenType.DOUBLE;
		}

		/**
		 * Equivalent to <code>is(TokenType.EOF)</code>
		 *
		 * @return <code>true</code> if this token has type <code>TokenType.EOF</code>, false otherwise
		 */
		public boolean isEOF()
		{
			return ( type == TokenType.EOF );
		}

		/**
		 * Returns <code>true</code> if this token has the passed type, <code>false</code> otherwise.
		 *
		 * @param compareType the type to compare the type of this token with
		 * @return <code>true</code> if this token has the passed type, <code>false</code> otherwise
		 */
		public boolean is( TokenType compareType )
		{
			return ( type == compareType );
		}

		/**
		 * Returns <code>true</code> if this token has a different type from the passed one, <code>false</code> otherwise.
		 *
		 * @param compareType the type to compare the type of this token with
		 * @return <code>true</code> if this token has a different type from the passed one, <code>false</code> otherwise
		 */
		public boolean isNot( TokenType compareType )
		{
			return ( type != compareType );
		}

		/**
		 * Returns <code>true</code> if this token has type <code>TokenType.ID</code>
		 * and its content is equal to the passed parameter, <code>false</code> otherwise.
		 * @param keyword the keyword to check the content of this token against
		 * @return <code>true</code> if this token has type <code>TokenType.ID</code>
		 * and its content is equal to the passed parameter, <code>false</code> otherwise
		 */
		public boolean isKeyword( String keyword )
		{
			return ( type == TokenType.ID && content.equals( keyword ) );
		}

		/**
		 * Returns <code>true</code> if this token has type <code>TokenType.ID</code>
		 * or is a token for an unreserved keyword, <code>false</code> otherwise.
		 * @return <code>true</code> if this token has type <code>TokenType.ID</code>
		 * or is a token for an unreserved keyword, <code>false</code> otherwise.
		 */
		public boolean isIdentifier()
		{
			return ( type == TokenType.ID || isUnreservedKeyword );
		}

		/**
		 * This method behaves as {@link #isKeyword(java.lang.String) isKeyword}, except that
		 * it is case insensitive.
		 * @param keyword the keyword to check the content of this token against
		 * @return
		 */
		public boolean isKeywordIgnoreCase( String keyword )
		{
			return ( type == TokenType.ID && content.equalsIgnoreCase( keyword ) );
		}
	}

	private final InputStream stream;		// input stream
	private final InputStreamReader reader;	// data input
	protected char ch;						// current character
	protected int currInt;					// current stream int
	private State state;					// current state
	private int line;						// current line
	private final URI source;				// source name
	private final boolean includeDocumentation;	// include documentation tokens

	/**
	 * Constructor
	 *
	 * @param stream the <code>InputStream</code> to use for input reading
	 * @param source the source URI of the stream
	 * @param charset the character encoding
	 * @param includeDocumentation if true, emit documentation tokens
	 * @throws java.io.IOException if the input reading initialization fails
	 */
	public Scanner( InputStream stream, URI source, String charset, boolean includeDocumentation )
		throws IOException
	{
		this.stream = stream;
		this.reader = charset != null ? new InputStreamReader( stream, charset ) : new InputStreamReader( stream );
		this.source = source;
		this.includeDocumentation = includeDocumentation;
		line = 1;
		readChar();
	}

	/**
	 * Constructor for a scanner that does not return documentation tokens.
	 *
	 * @param stream the <code>InputStream</code> to use for input reading
	 * @param source the source URI of the stream
	 * @param charset the character encoding
	 * @throws java.io.IOException if the input reading initialization fails
	 */
	public Scanner( InputStream stream, URI source, String charset ) throws IOException
	{
		this( stream, source, charset, false );
	}

	public boolean includeDocumentation()
	{
		return includeDocumentation;
	}

	private final StringBuilder tokenBuilder = new StringBuilder( 64 );

	private void resetTokenBuilder()
	{
		tokenBuilder.setLength( 0 );
	}

	public String readLine()
		throws IOException
	{
		resetTokenBuilder();
		readChar();
		while( !isNewLineChar( ch ) ) {
			tokenBuilder.append( ch );
			readChar();
		}
		return tokenBuilder.toString();
	}

	public InputStream inputStream()
	{
		return stream;
	}

	/**
	 * Returns character encoding
	 *
	 * @return character encoding
	 */
	public String charset()
	{
		return reader.getEncoding();
	}

	/**
	 * Returns the current line the scanner is reading.
	 *
	 * @return the current line the scanner is reading.
	 */
	public int line()
	{
		return line;
	}

	/**
	 * Returns the source URI the scanner is reading.
	 *
	 * @return the source URI the scanner is reading
	 */
	public URI source()
	{
		return source;
	}

	/**
	 * Eats all separators (whitespace) until the next input.
	 *
	 * @throws IOException
	 */
	public void eatSeparators()
		throws IOException
	{
		while( isSeparator( ch ) ) {
			readChar();
		}
	}

	public void eatSeparatorsUntilEOF()
		throws IOException
	{
		while( isSeparator( ch ) && stream.available() > 0 ) {
			readChar();
		}
	}

	/**
	 * Checks whether a character is a separator (whitespace).
	 *
	 * @param c the character to check as a whitespace
	 * @return <code>true</code> if <code>c</code> is a separator (whitespace)
	 */
	public static boolean isSeparator( char c )
	{
		return isNewLineChar( c ) || c == '\t' || c == ' ';
	}

	/**
	 * Checks whether a character is a horizontal separator (whitespace).
	 *
	 * @param c the character to check
	 * @return <code>true</code> if <code>c</code> is a horizontal separator (whitespace)
	 */
	private static boolean isHorizontalWhitespace( char c )
	{
		return c == '\t' || c == ' ';
	}

	/**
	 * Checks whether a character is an overflow character.
	 *
	 * @param c the character to check
	 * @return <code>true</code> if <code>c</code> is an overflow character
	 */
	private static boolean isOverflowChar( char c )
	{
		return ( (int) c >= Character.MAX_VALUE );
	}

	/**
	 * Checks whether a character is a newline character.
	 *
	 * @param c the character to check
	 * @return <code>true</code> if <code>c</code> is a newline character
	 */
	public static boolean isNewLineChar( char c )
	{
		return ( c == '\n' || c == '\r' );
	}

	/**
	 * Reads the next character and loads it into the scanner local state.
	 *
	 * @throws IOException if the source cannot be read
	 */
	public final void readChar()
		throws IOException
	{
		currInt = reader.read();

		ch = (char) currInt;

		if ( ch == '\n' ) {
			line++;
		}
	}

	/**
	 * Returns the current character in the scanner local state.
	 *
	 * @return the current character in the scanner local state
	 */
	public char currentCharacter()
	{
		return ch;
	}
	
	private static enum State
	{
		FIRST_CHARACTER,
		ID,
		INT_or_LONG_or_DOUBLE,
		STRING,
		PLUS_or_CHOICE,
		MULTIPLY_or_MULTIPLY_ASSIGN,
		ASSIGN_or_EQUAL,
		PARALLEL_or_LOGIC_OR,
		LOGIC_AND,
		LANGLE_or_MINOR_OR_EQUAL_or_DEEP_COPY_LEFT_or_DEEP_COPY_WITH_LINKS_LEFT,
		DEEP_COPY_WITH_LINKS_LEFT_or_DEEP_COPY_LEFT,
		RANGLE_or_MINOR_OR_EQUAL,
		NOT_or_NOT_EQUAL,
		DIVIDE_or_BEGIN_COMMENT_or_LINE_COMMENT,
		WAITING_FOR_END_COMMENT,
		MINUS_or_NUMBER_or_POINTS_TO,
		LINE_COMMENT,
		DOT,
		REAL,
		SCIENTIFIC_NOTATION_FIRST_AFTER_E,
		SCIENTIFIC_NOTATION_FIRST_EXP_DIGIT,
		SCIENTIFIC_NOTATION_SECOND_TO_END_DIGITS,
		DOCUMENTATION_FORWARD_BLOCK,
		DOCUMENTATION_FORWARD_INLINE,
		DOCUMENTATION_BACKWARD_BLOCK,
		DOCUMENTATION_BACKWARD_INLINE
	}

	/**
	 * Consumes characters from the source text and returns its corresponding token.
	 *
	 * @return the token corresponding to the consumed characters
	 * @throws IOException if not enough characters can be read from the source
	 */
	public Token getToken()
		throws IOException
	{
		boolean keepRun = true;
		state = State.FIRST_CHARACTER;
        
		while ( currInt != -1 && isHorizontalWhitespace( ch ) ) {
			readChar();
		}

		if ( currInt == -1 ) {
			return new Token( TokenType.EOF );
		}

		boolean stopOneChar = false;
		Token retval = null;
		resetTokenBuilder();

		while( keepRun ) {
			if ( currInt == -1 && retval == null ) {
				keepRun = false; // We *need* a token at this point
			}
			switch( state ) {
				/* When considering multi-characters tokens (states > 1),
				 * remember to read another character in case of a
				 * specific character (==) check.
				 */

				case FIRST_CHARACTER: // First character
					if ( Character.isLetter( ch ) || ch == '_' ) {
						state = State.ID;
					} else if ( Character.isDigit( ch ) ) {
						state = State.INT_or_LONG_or_DOUBLE;
					} else if ( ch == '"' ) {
						state = State.STRING;
					} else if ( ch == '+' ) {
						state = State.PLUS_or_CHOICE;
					} else if ( ch == '*' ) {
						state = State.MULTIPLY_or_MULTIPLY_ASSIGN;
					} else if ( ch == '=' ) {
						state = State.ASSIGN_or_EQUAL;
					} else if ( ch == '|' ) {
						state = State.PARALLEL_or_LOGIC_OR;
					} else if ( ch == '&' ) {
						state = State.LOGIC_AND;
					} else if ( ch == '<' ) {
						state = State.LANGLE_or_MINOR_OR_EQUAL_or_DEEP_COPY_LEFT_or_DEEP_COPY_WITH_LINKS_LEFT;
					} else if ( ch == '>' ) {
						state = State.RANGLE_or_MINOR_OR_EQUAL;
					} else if ( ch == '!' ) {
						state = State.NOT_or_NOT_EQUAL;
					} else if ( ch == '/' ) {
						state = State.DIVIDE_or_BEGIN_COMMENT_or_LINE_COMMENT;
					} else if ( ch == '-' ) {
						state = State.MINUS_or_NUMBER_or_POINTS_TO;
					} else if ( ch == '.' ) { // DOT or REAL
						state = State.DOT;
					} else { // ONE CHARACTER TOKEN
						if ( ch == '(' ) {
							retval = new Token( TokenType.LPAREN );
						} else if ( ch == ')' ) {
							retval = new Token( TokenType.RPAREN );
						} else if ( ch == '[' ) {
							retval = new Token( TokenType.LSQUARE );
						} else if ( ch == ']' ) {
							retval = new Token( TokenType.RSQUARE );
						} else if ( ch == '{' ) {
							retval = new Token( TokenType.LCURLY );
						} else if ( ch == '}' ) {
							retval = new Token( TokenType.RCURLY );
						} else if ( ch == '@' ) {
							retval = new Token( TokenType.AT );
						} else if ( ch == ':' ) {
							retval = new Token( TokenType.COLON );
						} else if ( ch == ',' ) {
							retval = new Token( TokenType.COMMA );
						} else if ( ch == ';' ) {
							retval = new Token( TokenType.SEQUENCE );
						} else if ( ch == '%' ) {
							retval = new Token( TokenType.PERCENT_SIGN );
						} else if ( ch == '#' ) {
							retval = new Token( TokenType.HASH );
						} else if ( ch == '^' ) {
							retval = new Token( TokenType.CARET );
						} else if ( ch == '?' ) {
							retval = new Token( TokenType.QUESTION_MARK );
						} else if ( isNewLineChar( ch ) ) {
							retval = new Token( TokenType.NEWLINE );
						}

						readChar();
					}

					break;
				case ID:  // ID (or unreserved keyword)
					if ( !Character.isLetterOrDigit( ch ) && ch != '_' ) {
						String str = tokenBuilder.toString();
						TokenType tt = unreservedKeywords.get( str );
						if ( tt != null ) {
							// It is an unreserved keyword
							retval = new Token( tt, str, true );
						} else {
							// It is a normal ID, not corresponding to any keyword
							retval = new Token( TokenType.ID, str );
						}
					}
					break;
				case INT_or_LONG_or_DOUBLE: // INT (or LONG, or DOUBLE)
					if ( ch == 'e' || ch == 'E' ) {
						state = State.SCIENTIFIC_NOTATION_FIRST_EXP_DIGIT;
					} else if ( !Character.isDigit( ch ) && ch != '.' ) {
						if ( ch == 'l' || ch == 'L' ) {
							retval = new Token( TokenType.LONG, tokenBuilder.toString() );
							readChar();
						} else {
							retval = new Token( TokenType.INT, tokenBuilder.toString() );
						}
					} else if ( ch == '.' ) {
						tokenBuilder.append( ch );
						readChar();
						if ( !Character.isDigit( ch ) ) {
							retval = new Token( TokenType.ERROR, tokenBuilder.toString() );
						} else {
							state = State.REAL; // recognized a DOUBLE
						}
					}
					break;
				case STRING:  // STRING
					if ( ch == '"' ) {
						retval = new Token( TokenType.STRING, tokenBuilder.toString().substring( 1 ) );
						readChar();
					} else if ( ch == '\\' ) { // Parse special characters
						readChar();
						if ( ch == '\\' ) {
							tokenBuilder.append( '\\' );
						} else if ( ch == 'n' ) {
							tokenBuilder.append( '\n' );
						} else if ( ch == 't' ) {
							tokenBuilder.append( '\t' );
						} else if ( ch == 'r' ) {
							tokenBuilder.append( '\r' );
						} else if ( ch == '"' ) {
							tokenBuilder.append( '"' );
						} else if ( ch == 'u' ) {
							tokenBuilder.append( 'u' );
						} else {
							throw new IOException( "malformed string: bad \\ usage" );
						}

						stopOneChar = true;
						readChar();
					}
					break;
				case PLUS_or_CHOICE:  // PLUS OR CHOICE
					if ( ch == '=' ) {
						retval = new Token( TokenType.ADD_ASSIGN );
						readChar();
					} else if ( ch == '+' ) {
						retval = new Token( TokenType.INCREMENT );
						readChar();
					} else {
						retval = new Token( TokenType.PLUS );
					}
					break;
				case MULTIPLY_or_MULTIPLY_ASSIGN: // MULTIPLY or MULTIPLY_ASSIGN
					if ( ch == '=' ) {
						retval = new Token( TokenType.MULTIPLY_ASSIGN );
						readChar();
					} else {
						retval = new Token( TokenType.ASTERISK, "*" );
					}
					break;
				case ASSIGN_or_EQUAL: // ASSIGN OR EQUAL
					if ( ch == '=' ) {
						retval = new Token( TokenType.EQUAL );
						readChar();
					} else if ( ch == '>' ) {
						retval = new Token( TokenType.ARROW );
						readChar();
					} else {
						retval = new Token( TokenType.ASSIGN );
					}
					break;
				case PARALLEL_or_LOGIC_OR:  // PARALLEL OR LOGICAL OR
					if ( ch == '|' ) {
						retval = new Token( TokenType.OR );
						readChar();
					} else {
						retval = new Token( TokenType.PARALLEL );
					}
					break;
				case LOGIC_AND: // LOGICAL AND
					if ( ch == '&' ) {
						retval = new Token( TokenType.AND );
						readChar();
					}
					break;
				case LANGLE_or_MINOR_OR_EQUAL_or_DEEP_COPY_LEFT_or_DEEP_COPY_WITH_LINKS_LEFT: // LANGLE OR MINOR_OR_EQUAL OR DEEP_COPY_LEFT
					if ( ch == '=' ) {
						retval = new Token( TokenType.MINOR_OR_EQUAL );
						readChar();
					} else if ( ch == '<' ) {
						state = State.DEEP_COPY_WITH_LINKS_LEFT_or_DEEP_COPY_LEFT;
					} else {
						retval = new Token( TokenType.LANGLE );
					}
					break;
				case RANGLE_or_MINOR_OR_EQUAL: // RANGLE OR MINOR_OR_EQUAL
					if ( ch == '=' ) {
						retval = new Token( TokenType.MAJOR_OR_EQUAL );
						readChar();
					} else {
						retval = new Token( TokenType.RANGLE );
					}
					break;
				case NOT_or_NOT_EQUAL: // NOT OR NOT_EQUAL
					if ( ch == '=' ) {
						retval = new Token( TokenType.NOT_EQUAL );
						readChar();
					} else {
						retval = new Token( TokenType.NOT );
					}
					break;
				case DIVIDE_or_BEGIN_COMMENT_or_LINE_COMMENT: // DIVIDE OR BEGIN_COMMENT OR LINE_COMMENT
					if ( ch == '*' ) { // BEGIN COMMENT
						readChar();
						stopOneChar = true;

						if ( includeDocumentation && ch == '*' ) { // BEGIN DOCUMENTATION
							readChar();

							if ( ch == '!' ) { // Ignore old documentation symbol
								readChar();
							}

							resetTokenBuilder();
							state = State.DOCUMENTATION_FORWARD_BLOCK;
						} else if ( includeDocumentation && ch == '<' ) {
							readChar();
							resetTokenBuilder();
							state = State.DOCUMENTATION_BACKWARD_BLOCK;
						} else {
							state = State.WAITING_FOR_END_COMMENT;
						}
					} else if ( ch == '/' ) {
						readChar();
						stopOneChar = true;
						if ( includeDocumentation && ch == '/' ) {
							readChar();
							resetTokenBuilder();
							state = State.DOCUMENTATION_FORWARD_INLINE;
						} else if ( includeDocumentation && ch == '<' ) {
							readChar();
							resetTokenBuilder();
							state = State.DOCUMENTATION_BACKWARD_INLINE; // BACKWARD DOCUMENTATION COMMENT
						} else {
							state = State.LINE_COMMENT; // NORMAL LINE COMMENT
						}
					} else if ( ch == '=' ) {
						retval = new Token( TokenType.DIVIDE_ASSIGN );
						readChar();
					} else {
						retval = new Token( TokenType.DIVIDE );
					}
					break;
				case WAITING_FOR_END_COMMENT: // WAITING FOR END_COMMENT
					if ( ch == '*' ) {
						readChar();
						stopOneChar = true;
						if ( ch == '/' ) {
							readChar();
							retval = new Token( TokenType.NEWLINE );
						}
					}
					break;
				case MINUS_or_NUMBER_or_POINTS_TO: // MINUS OR (negative) NUMBER OR POINTS_TO
					if ( Character.isDigit( ch ) ) {
						state = State.INT_or_LONG_or_DOUBLE;
					} else if ( ch == '-' ) {
						retval = new Token( TokenType.DECREMENT );
						readChar();
					} else if ( ch == '>' ) {
						retval = new Token( TokenType.POINTS_TO );
						readChar();
					} else if ( ch == '=' ) {
						retval = new Token( TokenType.MINUS_ASSIGN );
						readChar();
					} else if ( ch == '.' ) {
						tokenBuilder.append( ch );
						readChar();
						if ( !Character.isDigit( ch ) ) {
							retval = new Token( TokenType.ERROR, "-." );
						} else {
							state = State.REAL;
						}
					} else {
						retval = new Token( TokenType.MINUS );
					}
					break;
				case LINE_COMMENT: // LINE_COMMENT: waiting for end of line
					if ( isNewLineChar( ch ) || isOverflowChar( ch ) ) {
						readChar();
						retval = new Token( TokenType.NEWLINE );
					}
					break;
				case DOT: // DOT
					if ( !Character.isDigit( ch ) ) {
						retval = new Token( TokenType.DOT );
					} else {
						state = State.REAL; // It's a REAL
					}
					break;
				case REAL: // REAL "."[0-9]+ 
					if ( ch == 'E' || ch == 'e' ) {
						state = State.SCIENTIFIC_NOTATION_FIRST_AFTER_E;
					} else if ( !Character.isDigit( ch ) ) {
						retval = new Token( TokenType.DOUBLE, tokenBuilder.toString() );
					}
					break;
				case SCIENTIFIC_NOTATION_FIRST_AFTER_E: // Scientific notation, first char after 'E'
					if ( ch == '-' || ch == '+' ) {
						state = State.SCIENTIFIC_NOTATION_FIRST_EXP_DIGIT;
					} else if ( Character.isDigit( ch ) ) {
						state = State.SCIENTIFIC_NOTATION_SECOND_TO_END_DIGITS;
					} else {
						retval = new Token( TokenType.ERROR );
					}
					break;
				case SCIENTIFIC_NOTATION_FIRST_EXP_DIGIT: // Scientific notation, first exp. digit
					if ( !Character.isDigit( ch ) ) {
						retval = new Token( TokenType.ERROR );
					} else {
						state = State.SCIENTIFIC_NOTATION_SECOND_TO_END_DIGITS;
					}
					break;
				case SCIENTIFIC_NOTATION_SECOND_TO_END_DIGITS: // Scientific notation: from second digit to end
					if ( !Character.isDigit( ch ) ) {
						retval = new Token( TokenType.DOUBLE, tokenBuilder.toString() );
					}
					break;
				case DOCUMENTATION_FORWARD_BLOCK: // Documentation comment
					if ( ch == '*' ) {
						readChar();
						stopOneChar = true;
						if ( ch == '/' ) {
							readChar();
							if ( !includeDocumentation ) {
								resetTokenBuilder();
								state = State.FIRST_CHARACTER;
							} else {
								retval = new Token( TokenType.DOCUMENTATION_FORWARD, tokenBuilder.toString() );
							}
						}
					}
					break;
				case DOCUMENTATION_FORWARD_INLINE:
					if ( ch == '\r' ) { // IGNORE CARRIGE RETURN (WINDOWS) NEWLINES
						readChar();
					}
					if ( ch == '\n' || isOverflowChar( ch ) ) {
						if ( !includeDocumentation ) {
							readChar();
							resetTokenBuilder();
							state = State.FIRST_CHARACTER;
						} else { // RETURN THE DOCUMENTATION COMMENT
							retval = new Token( TokenType.DOCUMENTATION_FORWARD, tokenBuilder.toString() );
						}
					}
					break;
				case DOCUMENTATION_BACKWARD_BLOCK:
					if ( ch == '*' ) {
						readChar();
						stopOneChar = true;
						if ( ch == '/' ) {
							readChar();
							if ( includeDocumentation ) {
								retval = new Token( TokenType.DOCUMENTATION_BACKWARD, tokenBuilder.toString() );
							} else {
								resetTokenBuilder();
								state = State.FIRST_CHARACTER;
							}
						}
					}
					break;
				case DOCUMENTATION_BACKWARD_INLINE:
					if ( isNewLineChar( ch ) || isOverflowChar( ch ) ) {
						if ( includeDocumentation ) {
							retval = new Token( TokenType.DOCUMENTATION_BACKWARD, tokenBuilder.toString() );
						} else {
							resetTokenBuilder();
							state = State.FIRST_CHARACTER;
						}
					}
					break;
				case DEEP_COPY_WITH_LINKS_LEFT_or_DEEP_COPY_LEFT: // << or <<-
					readChar();
					if ( ch == '-' ) {
						retval = new Token( TokenType.DEEP_COPY_WITH_LINKS_LEFT );
						readChar();
					} else {
						retval = new Token( TokenType.DEEP_COPY_LEFT );
					}
					break;
				default:
					retval = new Token( TokenType.ERROR, tokenBuilder.toString() );
					break;
			}

			if ( retval == null ) {
				if ( stopOneChar ) {
					stopOneChar = false;
				} else {
					tokenBuilder.append( ch );
					readChar();
				}
			} else {
				keepRun = false; // Ok, we are done.
			}
		}

		if ( retval == null ) {
			retval = new Token( TokenType.ERROR );
		}

		return retval;
	}
}
