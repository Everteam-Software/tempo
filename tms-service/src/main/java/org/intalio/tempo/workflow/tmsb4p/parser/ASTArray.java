/* Generated By:JJTree: Do not edit this line. ASTArray.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.intalio.tempo.workflow.tmsb4p.parser;

public class ASTArray extends SimpleNode {
  public ASTArray(int id) {
    super(id);
  }

  public ASTArray(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws ParseException {
    return visitor.visit(this, data);
  }
  
  public String getNodeName() {
	  return ParserTreeConstants.jjtNodeName[ParserTreeConstants.JJTARRAY];
  }
}
/* JavaCC - OriginalChecksum=684382f497e0e40067039bb65177a1e4 (do not edit this line) */
