/* Generated By:JJTree: Do not edit this line. ASTConstant.java Version 4.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY= */
package org.intalio.tempo.workflow.tmsb4p.parser;

public class ASTConstant extends SimpleNode {
	private Object value;
  public ASTConstant(int id) {
    super(id);
  }

  public ASTConstant(Parser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(ParserVisitor visitor, Object data) throws ParseException {
    return visitor.visit(this, data);
  }
  
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String toString() {
		return "Constant: " + value;
	}
  
  public String getNodeName() {
		return ParserTreeConstants.jjtNodeName[ParserTreeConstants.JJTCONSTANT];
	}
}
/* JavaCC - OriginalChecksum=60de49b6d373ff9fd6a3275c28a3cc12 (do not edit this line) */
