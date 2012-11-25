/* Generated By:JavaCC: Do not edit this line. SQLParser.java */
public class SQLParser implements SQLParserConstants {

  protected Statement mStmt;

  public static void main(String args[]) throws ParseException {
    SQLParser parser = new SQLParser(System.in);
    parser.SQL();
  }

  public Statement getCurrentStmt() {
    return mStmt;
  }

/** Root production. */
  final public void SQL() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case SHOW:
      ShowStatement();
      break;
    case USE:
      UseStatement();
      break;
    case CREATE:
      CreateStatement();
      break;
    case INSERT:
      InsertStatement();
      break;
    case UPDATE:
      UpdateStatement();
      break;
    case SELECT:
      SelectStatement();
      break;
    case 0:
    case EXIT:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EXIT:
        jj_consume_token(EXIT);
        break;
      case 0:
        jj_consume_token(0);
        break;
      default:
        jj_la1[0] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
                         mStmt = new ExitStatement();
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void ShowStatement() throws ParseException {
  ShowStatement stmt = new ShowStatement();
  Token t;
    t = jj_consume_token(SHOW);
               stmt.addString(t.image);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DATABASES:
      t = jj_consume_token(DATABASES);
                      stmt.setIsDatabase(true); stmt.addString(t.image);
      break;
    case TABLES:
      t = jj_consume_token(TABLES);
                     stmt.setIsDatabase(false); stmt.addString(t.image);
      break;
    default:
      jj_la1[2] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(STMT_DELIMITER);
                     mStmt = stmt;
  }

  final public void UseStatement() throws ParseException {
  UseStatement stmt = new UseStatement();
  Token t;
    t = jj_consume_token(USE);
              stmt.addString(t.image);
    t = jj_consume_token(ALPHANUMERIC);
                       stmt.setDatabase(t.image);
    jj_consume_token(STMT_DELIMITER);
                     mStmt = stmt;
  }

  final public void CreateStatement() throws ParseException {
  Token t;
  CreateStatement stmt = new CreateStatement();
    t = jj_consume_token(CREATE);
                 stmt.addString(t.image);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DATABASE:
      CreateDatabaseStatement(stmt);
      break;
    case TABLE:
      CreateTableStatement(stmt);
      break;
    default:
      jj_la1[3] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    jj_consume_token(STMT_DELIMITER);
                     mStmt = stmt;
  }

  final public void CreateDatabaseStatement(CreateStatement stmt) throws ParseException {
  Token t;
    t = jj_consume_token(DATABASE);
                   stmt.setIsDatabase(true); stmt.addString(t.image);
    t = jj_consume_token(ALPHANUMERIC);
                       stmt.setName(t.image);
  }

  final public void CreateTableStatement(CreateStatement stmt) throws ParseException {
  Token t;
    t = jj_consume_token(TABLE);
                stmt.setIsDatabase(false); stmt.addString(t.image);
    t = jj_consume_token(ALPHANUMERIC);
                       stmt.setName(t.image);
    t = jj_consume_token(BRACKET_OPEN);
                       stmt.addString(t.image);
    FieldDefinition(stmt);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case FIELD_DELIMITER:
        ;
        break;
      default:
        jj_la1[4] = jj_gen;
        break label_1;
      }
      t = jj_consume_token(FIELD_DELIMITER);
                            stmt.addString(t.image);
      FieldDefinition(stmt);
    }
    t = jj_consume_token(BRACKET_CLOSE);
                        stmt.addString(t.image);
  }

  final public void FieldDefinition(CreateStatement stmt) throws ParseException {
  Token t;
  FieldDefinition field = new FieldDefinition();
    t = jj_consume_token(ALPHANUMERIC);
                       field.setName(t.image);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case TYPE_INTEGER:
      t = jj_consume_token(TYPE_INTEGER);
                         field.setType(FieldDefinition.FIELD_TYPE_INTEGER);
      break;
    case TYPE_STRING:
      t = jj_consume_token(TYPE_STRING);
                          field.setType(FieldDefinition.FIELD_TYPE_STRING);
      break;
    case TYPE_DOUBLE:
      t = jj_consume_token(TYPE_DOUBLE);
                          field.setType(FieldDefinition.FIELD_TYPE_DOUBLE);
      break;
    default:
      jj_la1[5] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      stmt.addField(field);
  }

  final public void InsertStatement() throws ParseException {
  Token t;
  InsertStatement stmt = new InsertStatement();
    t = jj_consume_token(INSERT);
                 stmt.addString(t.image);
    t = jj_consume_token(INTO);
               stmt.addString(t.image);
    t = jj_consume_token(ALPHANUMERIC);
                       stmt.setName(t.image);
    t = jj_consume_token(VALUES);
                 stmt.addString(t.image);
    t = jj_consume_token(BRACKET_OPEN);
                       stmt.addString(t.image);
    ValueDefinition(stmt);
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case FIELD_DELIMITER:
        ;
        break;
      default:
        jj_la1[6] = jj_gen;
        break label_2;
      }
      t = jj_consume_token(FIELD_DELIMITER);
                            stmt.addString(t.image);
      ValueDefinition(stmt);
    }
    t = jj_consume_token(BRACKET_CLOSE);
                        stmt.addString(t.image);
    jj_consume_token(STMT_DELIMITER);
                     mStmt = stmt;
  }

  final public void ValueDefinition(InsertStatement stmt) throws ParseException {
  Token t;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
      t = jj_consume_token(INTEGER);
                    stmt.addValue(new Integer(Integer.parseInt(t.image)));
      break;
    case DOUBLE:
      t = jj_consume_token(DOUBLE);
                     stmt.addValue(new Double(Double.parseDouble(t.image)));
      break;
    case STRING_DELIMITER:
      t = StringValue();
                          stmt.addValue(t.image);
      break;
    default:
      jj_la1[7] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void UpdateStatement() throws ParseException {
  Token t;
  UpdateStatement stmt = new UpdateStatement();
    t = jj_consume_token(UPDATE);
                 stmt.addString(t.image);
    t = jj_consume_token(ALPHANUMERIC);
                       stmt.setName(t.image);
    SetClause(stmt);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WHERE:
      WhereClause(stmt);
      break;
    default:
      jj_la1[8] = jj_gen;
      ;
    }
    jj_consume_token(STMT_DELIMITER);
                     mStmt = stmt;
  }

  final public void SetClause(UpdateStatement stmt) throws ParseException {
  Token t;
    t = jj_consume_token(SET);
              stmt.addString(t.image);
    t = jj_consume_token(ALPHANUMERIC);
                       stmt.setField(t.image);
    t = jj_consume_token(COMP_EQ);
                  stmt.addString(t.image);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
      t = jj_consume_token(INTEGER);
                    stmt.setValue(new Integer(Integer.parseInt(t.image)));
      break;
    case DOUBLE:
      t = jj_consume_token(DOUBLE);
                     stmt.setValue(new Double(Double.parseDouble(t.image)));
      break;
    case STRING_DELIMITER:
      t = StringValue();
                          stmt.setValue(t.image);
      break;
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void SelectStatement() throws ParseException {
  SelectStatement stmt = new SelectStatement();
    SelectClause(stmt);
    FromClause(stmt);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case WHERE:
      WhereClause(stmt);
      break;
    default:
      jj_la1[10] = jj_gen;
      ;
    }
    jj_consume_token(STMT_DELIMITER);
                     mStmt = stmt;
  }

  final public void SelectClause(SelectStatement stmt) throws ParseException {
  Token t;
    t = jj_consume_token(SELECT);
                 stmt.addString(t.image);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case STAR:
      t = jj_consume_token(STAR);
                 stmt.addSelect(t.image);
      break;
    case ALPHANUMERIC:
      t = jj_consume_token(ALPHANUMERIC);
                           stmt.addSelect(t.image);
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case FIELD_DELIMITER:
          ;
          break;
        default:
          jj_la1[11] = jj_gen;
          break label_3;
        }
        t = jj_consume_token(FIELD_DELIMITER);
                              stmt.addString(t.image);
        t = jj_consume_token(ALPHANUMERIC);
                           stmt.addSelect(t.image);
      }
      break;
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void FromClause(SelectStatement stmt) throws ParseException {
  Token t;
    t = jj_consume_token(FROM);
               stmt.addString(t.image);
    t = jj_consume_token(ALPHANUMERIC);
                       stmt.addFrom(t.image);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case FIELD_DELIMITER:
        ;
        break;
      default:
        jj_la1[13] = jj_gen;
        break label_4;
      }
      t = jj_consume_token(FIELD_DELIMITER);
                            stmt.addString(t.image);
      t = jj_consume_token(ALPHANUMERIC);
                         stmt.addFrom(t.image);
    }
  }

  final public void WhereClause(WhereStatement stmt) throws ParseException {
  Token t;
    t = jj_consume_token(WHERE);
                stmt.addString(t.image);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case ALPHANUMERIC:
      WhereItem(stmt);
      break;
    case BRACKET_OPEN:
      WhereItemNestable(stmt);
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case AND:
          ;
          break;
        default:
          jj_la1[14] = jj_gen;
          break label_5;
        }
        t = jj_consume_token(AND);
                    stmt.addString(t.image);
        WhereItemNestable(stmt);
      }
      break;
    default:
      jj_la1[15] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void WhereItem(WhereStatement stmt) throws ParseException {
  Token t;
  WhereClause where = new WhereClause();
    t = jj_consume_token(ALPHANUMERIC);
                       where.setField(t.image);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case COMP_EQ:
      t = jj_consume_token(COMP_EQ);
                    where.setCompType(WhereClause.COMP_EQ);
      break;
    case COMP_GT:
      t = jj_consume_token(COMP_GT);
                      where.setCompType(WhereClause.COMP_GT);
      break;
    case COMP_GE:
      t = jj_consume_token(COMP_GE);
                      where.setCompType(WhereClause.COMP_GE);
      break;
    case COMP_LT:
      t = jj_consume_token(COMP_LT);
                      where.setCompType(WhereClause.COMP_LT);
      break;
    case COMP_LE:
      t = jj_consume_token(COMP_LE);
                      where.setCompType(WhereClause.COMP_LE);
      break;
    default:
      jj_la1[16] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case INTEGER:
      t = jj_consume_token(INTEGER);
                    where.setValue(new Integer(Integer.parseInt(t.image)));
      break;
    case DOUBLE:
      t = jj_consume_token(DOUBLE);
                     where.setValue(new Double(Double.parseDouble(t.image)));
      break;
    case STRING_DELIMITER:
      t = StringValue();
                          where.setValue(t.image);
      break;
    default:
      jj_la1[17] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    stmt.addWhere(where);
  }

  final public void WhereItemNestable(WhereStatement stmt) throws ParseException {
  Token t;
    t = jj_consume_token(BRACKET_OPEN);
                       stmt.addString(t.image);
    WhereItem(stmt);
    t = jj_consume_token(BRACKET_CLOSE);
                        stmt.addString(t.image);
  }

/* === SHARED === */
  final public Token StringValue() throws ParseException {
  Token t;
    jj_consume_token(STRING_DELIMITER);
    t = jj_consume_token(ALPHANUMERIC);
    jj_consume_token(STRING_DELIMITER);
    {if (true) return t;}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public SQLParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[18];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x1001,0x1fc1,0x280000,0x500000,0x40000000,0x3800000,0x40000000,0x20000000,0x4000,0x20000000,0x4000,0x40000000,0x8000000,0x40000000,0x40000,0x0,0x80000000,0x20000000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x0,0x0,0x0,0x0,0x0,0xc0,0x0,0xc0,0x0,0x0,0x100,0x0,0x0,0x110,0xf,0xc0,};
   }

  /** Constructor with InputStream. */
  public SQLParser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public SQLParser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new SQLParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public SQLParser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new SQLParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public SQLParser(SQLParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(SQLParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 18; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[41];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 18; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 41; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
