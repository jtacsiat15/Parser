/**
 * Author: 
 * Homework: #3
 * File: Parser.java
 * 
 * Recursive descent parser implementation for MyPL. The parser
 * requires a lexer. Once a parser is created, the parse() method
 * ensures the given program is syntactically correct. 
 */

import java.util.*;


public class Parser {
    
  private Lexer lexer; 
  private Token currToken = null;
  private boolean debug_flag = false;  // set to false to remove debug comments
  
  /** 
   * Create a new parser over the given lexer.
   */
  public Parser(Lexer lexer) {
    this.lexer = lexer;
  }

  /**
   * Ensures program is syntactically correct. On error, throws a
   * MyPLException.
   */
  public void parse() throws MyPLException
  {
    advance();
    stmts();
    eat(TokenType.EOS, "expecting end of file");
  }


  /* Helper Functions */

  // sets current token to next token in stream
  private void advance() throws MyPLException {
    currToken = lexer.nextToken();
  }

  // checks that current token matches given type and advances,
  // otherwise creates an error with the given error message
  private void eat(TokenType t, String errmsg) throws MyPLException {
    if (currToken.type() == t)
      advance();
    else
      error(errmsg);
  }

  // generates an error message from the given message and throws a
  // corresponding MyPLException
  private void error(String errmsg) throws MyPLException {
    String s = errmsg + " found '" + currToken.lexeme() + "'";
    int row = currToken.row();
    int col = currToken.column();
    throw new MyPLException("Parser", errmsg, row, col);
  }

  // function to print a debug string if the debug_flag is set for
  // helping to diagnose/test the parser
  private void debug(String msg) {
    if (debug_flag)
      System.out.println(msg);
  }

  
  /* Recursive Descent Functions */
  
  // <stmts> ::= <stmt> <stmts> | epsilon
  private void stmts() throws MyPLException {
    debug("<stmts>");
    // TODO
  }


  // <bstmts> ::= <bstmt> <bstmts> | epsilon
  private void bstmts() throws MyPLException {
    debug("<bstmts>");
    // TODO
  }
  

  // TODO: remaining recursive descent functions ...
  
}
