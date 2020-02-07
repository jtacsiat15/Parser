/**
 * Author: Jalen Tacsiat
 * Assign: 2
 *
 * The lexer implementation tokenizes a given input stream. The lexer
 * implements a pull-based model via the nextToken function such that
 * each call to nextToken advances the lexer to the next token (which
 * is returned by nextToken). The file has been completed read when
 * nextToken returns the EOS token. Lexical errors in the source file
 * result in the nextToken function throwing a MyPL Exception.
 */

import java.util.*;

import jdk.nashorn.internal.ir.Symbol;

import java.io.*;


public class Lexer {

  private BufferedReader buffer; // handle to input stream
  private int line;
  private int column;
  
  /** 
   */
  public Lexer(InputStream instream) {
    buffer = new BufferedReader(new InputStreamReader(instream));
    this.line = 1;
    this.column = 0;
  }

  
  /**
   * Returns next character in the stream. Returns -1 if end of file.
   */
  private int read() throws MyPLException {
    try {
      int ch = buffer.read();
      return ch;
    } catch(IOException e) {
      error("read error", line, column + 1);
    }
    return -1;
  }

  
  /** 
   * Returns next character without removing it from the stream.
   */
  private int peek() throws MyPLException {
    int ch = -1;
    try {
      buffer.mark(1);
      ch = read();
      buffer.reset();
    } catch(IOException e) {
      error("read error", line, column + 1);
    }
    return ch;
  }


  /**
   * Print an error message and exit the program.
   */
  private void error(String msg, int line, int column) throws MyPLException {
    throw new MyPLException("Lexer", msg, line, column);
  }


  /**
   */
  public Token nextToken() throws MyPLException {
    // TODO: your job in HW2 is to implement the next token function
    String lexeme = "";
    char Symbol = (char)peek();
    if(Character.isWhitespace((char)peek())){
      if((char)read() == '\n'){
        ++line;
        column = 1;
        //return nextToken();
      }
      else{
        ++column;
      }
      return nextToken();
    }
    if((char)peek() == '#'){
      read();
      ++column;
      while(!((char)read() == '\n'));
      ++line;
      column = 1;
      return nextToken();
    }
    else if (peek() == -1){
      return new Token(TokenType.EOS, "", line, column);
    }
    else if ((char)peek() == ',') {
      read();
      ++column;
      return new Token(TokenType.COMMA, ",", line, column);
    }
    else if ((char)peek() == '.') {
      read();
      ++column;
      return new Token(TokenType.DOT, ".", line, column);
    }
    else if ((char)peek() == '+') {
      read();
      ++column;
      return new Token(TokenType.PLUS, "+", line, column);
    }
    else if ((char)peek() == '-') {
      read();
      ++column;
      return new Token(TokenType.MINUS, "-", line, column);
    }
    else if ((char)peek() == '*') {
      read();
      ++column;
      return new Token(TokenType.MULTIPLY, "*", line, column);
    }
    else if ((char)peek() == '/') {
      read();
      ++column;
      return new Token(TokenType.DIVIDE, "/", line, column);
    }
    else if ((char)peek() == '%') {
      read();
      ++column;
      return new Token(TokenType.MODULO, "%", line, column);
    }
    else if ((char)peek() == '>') {
      read();
      ++column;
      if(peek() == '='){
        read();
        ++column;
        return new Token(TokenType.GREATER_THAN_EQUAL, ">=", line, column - 1);
      }
      else
      return new Token(TokenType.GREATER_THAN, ">", line, column);
    }
    else if ((char)peek() == '<') {
      read();
      ++column;
      if(peek() == '='){
        read();
        ++column;
        return new Token(TokenType.LESS_THAN_EQUAL, "<=", line, column - 1 );
      }
      else
      return new Token(TokenType.LESS_THAN, "<", line, column);
    }
    else if ((char)peek() == '=') {
      read();
      ++column;
      return new Token(TokenType.EQUAL, "=", line, column);
    }
    else if ((char)peek() == '!') {
      read();
      ++column;
      if ((char)peek() == '=') {
        read();
        ++column;
        return new Token(TokenType.NOT_EQUAL, "!=", line, column - 1);
      }
    }
    else if ((char)peek() == '(') {
      read();
      ++column;
      return new Token(TokenType.LPAREN, "(", line, column);
    }
    else if ((char)peek() == ')') {
      read();
      ++column;
      return new Token(TokenType.RPAREN, ")", line, column);
    }
    else if ((char)peek() == ':') {
      read();
      ++column;
      if((char)peek() == '=')
      read();
      ++column;
      return new Token(TokenType.ASSIGN, ":=", line, column - 1);
    }
    else if((char)peek() == '\''){
      read();
      ++column;
      while(peek() != '\''){
        //Symbol = (char)read();
        lexeme += (char)read();
      }
      read();
      ++column;
      return new Token(TokenType.CHAR_VAL, lexeme, line, column - 1);
    }
    else if((char)peek() == '"'){
      read();
      ++column;
      int columnStart = column;
      //int col = column;
      while((char)peek() != '"'){
        //Symbol = (char)read();
        if (peek() == -1){
          String msg = "Bad string need to close string declaration with quotes";
          error(msg, line, column);
        }
        if((char)peek() == '\n'){
          String msg = "found newline within string";
          error(msg, line, column);
        }
        else
          lexeme += (char)read(); 
      }
      read();
      ++column;
      return new Token(TokenType.STRING_VAL, lexeme, line, columnStart);
    }
    else if(Character.isDigit(Symbol)){
      //lexeme += Symbol;
      int columnStart = 0;
      while(Character.isDigit((char)peek())){
        lexeme+=(char)read();
        ++column;
        columnStart = column;
        if(Character.isLetter((char)peek())){
          //++column;
          String msg = "unexpected symbol '" + (char)peek() + "'";
          error(msg, line, column );
        }
        if((char)peek() == '.'){
            lexeme += (char)read();
            if(!Character.isDigit((char)peek())){
              String msg = "missing digit in float '" + lexeme + "'";
              error(msg, line, column);
            }
            else{
            while(Character.isDigit((char)peek())){
              lexeme += (char)read();
            }
            if(Character.isLetter((char)peek())){
              String msg = "invalid character in " + lexeme;
              error(msg, line, column);
            }
            else if((char)peek() == '.'){
              String msg = "Too many decimals in number " + lexeme;
              error(msg, line, column);
            }
          }
            return new Token(TokenType.DOUBLE_VAL, lexeme, line, column);
        }
        else if(!Character.isDigit((char)peek())){  
        if(lexeme.charAt(0) == '0' && lexeme.length() > 1){
          String msg = "leading zero in " + "'" + lexeme + "'";
          error(msg, line, column);
        }
        return new Token(TokenType.INT_VAL, lexeme, line, columnStart);
      }
    }
  }
    //read strings in file
    if(Character.isLetter(Symbol)){
      //System.out.println(lexeme);
      ++column;
      int columnStart = column;
      while(Character.isLetter((char)peek()) || (char)peek() == '_' || Character.isDigit((char)peek())){
        lexeme += (char)read();
        ++column;
      }
      //System.out.println(lexeme);
      if(lexeme.equals("int")){
        return new Token(TokenType.INT_TYPE, lexeme, line, columnStart);
      }
      else if(lexeme.equals("bool")){
        return new Token(TokenType.BOOL_TYPE, lexeme, line, columnStart);
      }
      else if(lexeme.equals("double")){
        return new Token(TokenType.DOUBLE_TYPE, lexeme, line, columnStart);
      }
      else if(lexeme.equals("char")){
        return new Token(TokenType.CHAR_TYPE, lexeme, line, columnStart);
      }
      else if(lexeme.equals("string")){
        return new Token(TokenType.STRING_TYPE, lexeme, line, columnStart);
      }
      else if(lexeme.equals("type")){
        return new Token(TokenType.TYPE, lexeme, line, columnStart);
      }
      else if(lexeme.equals("and")){
        return new Token(TokenType.AND, lexeme, line, columnStart);
      }
      else if(lexeme.equals("or")){
        return new Token(TokenType.OR, lexeme, line, columnStart);
      }
      else if(lexeme.equals("not")){
        return new Token(TokenType.NOT, lexeme, line, columnStart);
      }
      else if(lexeme.equals("neg")){
        return new Token(TokenType.NEG, lexeme, line, columnStart);
      }
      else if(lexeme.equals("while")){
        return new Token(TokenType.WHILE, lexeme, line, columnStart);
      }
      else if(lexeme.equals("for")){
        return new Token(TokenType.FOR, lexeme, line, columnStart);
      }
      else if(lexeme.equals("to")){
        return new Token(TokenType.TO, lexeme, line, columnStart);
      }
      else if(lexeme.equals("do")){
        return new Token(TokenType.DO, lexeme, line, columnStart);
      }
      else if(lexeme.equals("if")){
        return new Token(TokenType.IF, lexeme, line, columnStart);
      }
      else if(lexeme.equals("then")){
        return new Token(TokenType.THEN, lexeme, line, columnStart);
      }
      else if(lexeme.equals("else")){
        return new Token(TokenType.ELSE, lexeme, line, columnStart);
      }
      else if(lexeme.equals("elif")){
        return new Token(TokenType.ELIF, lexeme, line, columnStart);
      }
      else if(lexeme.equals("end")){
        return new Token(TokenType.END, lexeme, line, columnStart);
      }
      else if(lexeme.equals("fun")){
        return new Token(TokenType.FUN, lexeme, line, columnStart);
      }
      else if(lexeme.equals("var")){
        return new Token(TokenType.VAR, lexeme, line, columnStart);
      }
      else if(lexeme.equals("set")){
        return new Token(TokenType.SET, lexeme, line, columnStart);
      }
      else if(lexeme.equals("return")){
        return new Token(TokenType.RETURN, lexeme, line, columnStart);
      }
      else if(lexeme.equals("new")){
        return new Token(TokenType.NEW, lexeme, line, columnStart);
      }
      else if(lexeme.equals("nil")){
        return new Token(TokenType.NIL, lexeme, line, columnStart);
      }
      else {
        return new Token(TokenType.ID, lexeme, line, columnStart);
      }
    }
    return new Token(TokenType.EOS, "", line, column);  
  }
}
