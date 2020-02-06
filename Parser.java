/**
 * Author: Jalen Tacsiat
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
    if(currToken.type() != TokenType.EOS){
      debug("<stmts>");
      stmt();
      stmts();
    }
  }


  // <bstmts> ::= <bstmt> <bstmts> | epsilon
  private void bstmts() throws MyPLException {
    //System.out.println(currToken.type());
    if(isBstmt(currToken.type())){
      debug("<bstmts>");
      bstmt();
      bstmts();
    }
  }
  
  // TODO: remaining recursive descent functions ...

  private void stmt() throws MyPLException{
    debug("<stmt>");
    if(currToken.type() == TokenType.TYPE){
      tdecl();
    }
    else if(currToken.type() == TokenType.FUN){
      fdecl();
    }
    else{
      bstmt();
    }
  }

  private void bstmt() throws MyPLException{
    debug("<bstmt>");
    if(currToken.type() == TokenType.VAR){
      //advance();
      vdecl();
    }
    else if(currToken.type() == TokenType.SET){
      //advance();
      assign();
    }
    else if(currToken.type() ==  TokenType.IF){
      //advance();
      cond();
    }
    else if(currToken.type() == TokenType.WHILE){
      //advance();
      tokenWhile();
    }
    else if(currToken.type() == TokenType.FOR){
      //advance();
      tokenFor();
    }
    else if(currToken.type() == TokenType.RETURN){
      //advance();
      exit();
    }
    else{
      expr();
    }
  }

  private void tdecl() throws MyPLException{
    debug("<tdecl>");
    eat(TokenType.TYPE, "expecting type" );
    eat(TokenType.ID, "expecting ID" );
    vdecls();
    eat(TokenType.END, "expecting end");
  }

  private void vdecls() throws MyPLException{
    debug("<vdecls>");
    if(currToken.type() == TokenType.VAR){
      vdecl();
      vdecls();
    }
  }

  private void fdecl() throws MyPLException{
    debug("<fdecl>");
    eat(TokenType.FUN, "expected word: FUN");
    if(isType(currToken.type())){
      dtype();
    }
    else{
      eat(TokenType.NIL, "expecting nil");
    }
    //System.out.println("after checks");
    eat(TokenType.ID, "expected ID");
    //System.out.println("after checks");
    eat(TokenType.LPAREN, "expected left parentheses");
    params();
    eat(TokenType.RPAREN, "expected right parentheses");
    bstmts();
    eat(TokenType.END, "expected END token");
  }
  private void params() throws MyPLException{
    if(isType(currToken.type())){
      //System.out.println("type in params " + currToken.type());
      debug("<params>");
      dtype();
      eat(TokenType.ID, "expected ID");
      //System.out.println(currToken.type());
      while (currToken.type() == TokenType.COMMA){
        advance();
        dtype();
        eat(TokenType.ID, "expected id");
      }
    }
  }
  private void dtype() throws MyPLException{
    //System.out.println(currToken.type());
    debug("<dtype>");
    if(currToken.type() == TokenType.INT_TYPE){
      advance();
      //eat(TokenType.INT_TYPE, "expected int type");
    }
    else if(currToken.type() == TokenType.DOUBLE_TYPE){
      advance();
      //eat(TokenType.DOUBLE_TYPE, "expected double type");
    }
    else if(currToken.type() == TokenType.BOOL_TYPE){
      advance();
      //eat(TokenType.BOOL_TYPE, "expected bool type");
    }
    else if(currToken.type() == TokenType.CHAR_TYPE){
      advance();
      //eat(TokenType.CHAR_TYPE, "expected char type");
    }
    else if(currToken.type() == TokenType.STRING_TYPE){
      advance();
      //eat(TokenType.STRING_TYPE, "expected type");
    }
    else if(currToken.type() == TokenType.ID){
      advance();
      //eat(TokenType.ID, "expected ID");
    }
  }
  private void exit() throws MyPLException{
    debug("<exit>");
    eat(TokenType.RETURN, "expected 'return'");
    if(currToken.type() !=TokenType.END){
      expr();
    }
    //debug("in exit");
  }

  private void vdecl() throws MyPLException{
    debug("<vdecl>");
    eat(TokenType.VAR, "Expected VAR");
    if(currToken.type() == TokenType.ID){
      advance();
      if(currToken.type() == TokenType.ID){
        advance();
        //eat(TokenType.ASSIGN, "expecting ':='");
      }
    }
    else{
      dtype();
      eat(TokenType.ID, "Expected ID");
    }
    eat(TokenType.ASSIGN, "Expected Assignment statement");
    expr();
    //error("end of vdecl");
  }

  private void assign() throws MyPLException{
    debug("<assign>");
    eat(TokenType.SET, "expected set");
    lvalue();
    eat(TokenType.ASSIGN, "exepected assignment statement");
    expr();
  }
  
  private void lvalue() throws MyPLException{
    debug("<lvalue>");
    eat(TokenType.ID, "expected ID");
    while(currToken.type() == TokenType.DOT){
      advance();
      eat(TokenType.ID, "expected ID");
    }
  }
  private void cond() throws MyPLException{
    debug("<cond>");
    eat(TokenType.IF, "expected 'if'");
    expr();
    eat(TokenType.THEN, "expected 'then'");
    bstmts();
    condt();
    debug("in cond");
    //System.out.println(currToken.type());
    eat(TokenType.END, "expected 'end'");
  }

  private void condt() throws MyPLException{
    if(currToken.type() == TokenType.ELIF){
      debug("<condt>");
      advance();
      //eat(TokenType.ELIF, "expected 'elif'");
      expr();
      eat(TokenType.THEN, "expected 'then'");
      bstmts();
      condt();
    }
    else if(currToken.type() == TokenType.ELSE){
      advance();
      bstmts();
    }
  }
  private void tokenWhile() throws MyPLException{
    debug("<tokenwhile>");
    eat(TokenType.WHILE, "expected 'while'");
    expr();
    eat(TokenType.DO, "expected 'do'");
    bstmts();
    debug("in while");
    eat(TokenType.END, "expected 'end'");
  }
  private void tokenFor() throws MyPLException{
    debug("<for>");
    eat(TokenType.FOR, "expected 'for'");
    eat(TokenType.ID, "expected 'id'");
    eat(TokenType.ASSIGN, "expected ':=' for assignment");
    expr();
    eat(TokenType.TO, "expected 'to'");
    expr();
    eat(TokenType.DO, "expected 'do'");
    bstmts();
    debug("in token for");
    eat(TokenType.END, "expected 'end'");
  }
  //when checking if statements advance
  private void expr() throws MyPLException{
    debug("<expr>");
    if(currToken.type() == TokenType.NOT){
      advance();
      expr();
    }
    else if(currToken.type() == TokenType.LPAREN){
      advance();
      expr();
      eat(TokenType.RPAREN, "expected ')' to close parentheses");
    }
    else{
      rvalue();
    }
    if(isOperator(currToken.type())){
      advance();
      expr();
    }
  }
  private void operator() throws MyPLException{
    debug("<operator>");
    if(currToken.type() == TokenType.PLUS){
      advance();
      //eat(TokenType.PLUS, "expected '+'");
    }
    if(currToken.type() == TokenType.MINUS){
      advance();
      //eat(TokenType.MINUS, "expected '-'");
    }
    if(currToken.type() == TokenType.DIVIDE){
      advance();
      //eat(TokenType.DIVIDE, "expected '/'");
    }
    if(currToken.type() == TokenType.MULTIPLY){
      advance();
      //eat(TokenType.MULTIPLY, "expected '*'");
    }
    if(currToken.type() == TokenType.MODULO){
      advance();
      //eat(TokenType.MODULO, "expected '%'");
    }
    if(currToken.type() == TokenType.AND){
      advance();
      //eat(TokenType.AND, "expected 'and'");
    }
    if(currToken.type() == TokenType.OR){
      advance();
      //eat(TokenType.OR, "expected 'or'");
    }
    if(currToken.type() == TokenType.EQUAL){
      advance();
      //eat(TokenType.EQUAL, "expected '='");
    }
    if(currToken.type() == TokenType.LESS_THAN){
      advance();
      //eat(TokenType.LESS_THAN, "expected '<'");
    }
    if(currToken.type() == TokenType.GREATER_THAN){
      advance();
      //eat(TokenType.GREATER_THAN, "expected '>'");
    }
    if(currToken.type() == TokenType.LESS_THAN_EQUAL){
      advance();
      //eat(TokenType.LESS_THAN_EQUAL, "expected '<='");
    }
    if(currToken.type() == TokenType.GREATER_THAN_EQUAL){
      advance();
      //eat(TokenType.GREATER_THAN_EQUAL, "expected >=");
    }
    if(currToken.type() == TokenType.NOT_EQUAL){
      advance();
      //eat(TokenType.NOT_EQUAL, "expected '!='");
    }

  }
  private void rvalue() throws MyPLException{
    debug("<rvalue>");
    if(currToken.type() == TokenType.NIL){
      advance();
    }
    else if(currToken.type() == TokenType.NEW){
      advance();
      eat(TokenType.ID, "expected ID");
    }
    else if(currToken.type() ==  TokenType.NEG){
      advance();
      expr();
    }
    else if(currToken.type() == TokenType.ID){
      idrval();
    }
    else{
      pval();
    }

  }
  private void pval() throws MyPLException{
    debug("<pval>");
    if(currToken.type() == TokenType.INT_VAL){
      advance();
      //eat(TokenType.INT_VAL, "expected integer value");
    }
    if(currToken.type() == TokenType.DOUBLE_VAL){
      advance();
      //eat(TokenType.DOUBLE_VAL, "expected double value");
    }
    if(currToken.type() == TokenType.BOOL_VAL){
      advance();
      //eat(TokenType.BOOL_VAL, "expected boolean value");
    }
    if(currToken.type() == TokenType.CHAR_VAL){
      advance();
      //eat(TokenType.CHAR_VAL, "expected char value");
    }
    if(currToken.type() == TokenType.STRING_VAL){
      advance();
      //eat(TokenType.STRING_VAL, "expected string value");
    }
    debug("in pval");

  }
  private void idrval() throws MyPLException{
    debug("<idrval>");
    //debug("before id eat");
    eat(TokenType.ID, "expecting ID");
    //debug("after id eat");
    if(currToken.type() == TokenType.LPAREN){
      advance();
      //debug("in lparen");
      exprlist();
      eat(TokenType.RPAREN, "expecting ')'");
    }
    else{
    while(currToken.type() == TokenType.DOT){
      advance();
      eat(TokenType.ID, "expecting ID");
    }
  }
  }
  private void exprlist() throws MyPLException{
    debug("<exprlist>");
    if(isExpr(currToken.type())){
      expr();
      while(currToken.type() == TokenType.COMMA){
        advance();
        expr();
      }
    }
  }
  public boolean isType(TokenType t){
    Set <TokenType> s = new HashSet<TokenType>();
    s.add(TokenType.INT_TYPE);
    s.add(TokenType.DOUBLE_TYPE);
    s.add(TokenType.BOOL_TYPE);
    s.add(TokenType.CHAR_TYPE);
    s.add(TokenType.STRING_TYPE);
    s.add(TokenType.ID);
    return s.contains(t);

  }
  public boolean isOperator(TokenType t){
    Set <TokenType> s = new HashSet<TokenType>();
    s.add(TokenType.PLUS);
    s.add(TokenType.MINUS);
    s.add(TokenType.DIVIDE);
    s.add(TokenType.MULTIPLY);
    s.add(TokenType.MODULO);
    s.add(TokenType.AND);
    s.add(TokenType.OR );
    s.add(TokenType.EQUAL);
    s.add(TokenType.LESS_THAN);
    s.add(TokenType.GREATER_THAN);
    s.add(TokenType.LESS_THAN_EQUAL);
    s.add(TokenType.GREATER_THAN_EQUAL );
    s.add(TokenType.NOT_EQUAL);       
    return s.contains(t);
  }
  public boolean isExpr(TokenType t){
    Set <TokenType> s = new HashSet<TokenType>();
    s.add(TokenType.NIL);
    s.add(TokenType.NEW);
    s.add(TokenType.NEG);
    s.add(TokenType.INT_VAL);
    s.add(TokenType.DOUBLE_VAL);
    s.add(TokenType.BOOL_VAL);
    s.add(TokenType.CHAR_VAL);
    s.add(TokenType.STRING_VAL);
    s.add(TokenType.ID);
    s.add(TokenType.LPAREN);
    return s.contains(t);

  }
  public boolean isBstmt(TokenType t){
    Set <TokenType> s = new HashSet<TokenType>();
    s.add(TokenType.VAR);
    s.add(TokenType.SET);
    s.add(TokenType.IF);
    s.add(TokenType.WHILE);
    s.add(TokenType.FOR);
    s.add(TokenType.RETURN);
    s.add(TokenType.NOT);
    s.add(TokenType.ASSIGN);
    s.add(TokenType.IF);
    s.add(TokenType.WHILE);
    s.add(TokenType.FOR);
    s.add(TokenType.NIL);
    s.add(TokenType.NEW);
    s.add(TokenType.NEG);
    s.add(TokenType.INT_VAL);
    s.add(TokenType.DOUBLE_VAL);
    s.add(TokenType.BOOL_VAL);
    s.add(TokenType.CHAR_VAL);
    s.add(TokenType.STRING_VAL);
    s.add(TokenType.ID);
    s.add(TokenType.LPAREN);
    return s.contains(t);
  }
}
