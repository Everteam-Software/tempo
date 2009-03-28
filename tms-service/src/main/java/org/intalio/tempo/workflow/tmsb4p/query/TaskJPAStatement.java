package org.intalio.tempo.workflow.tmsb4p.query;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.intalio.tempo.workflow.taskb4p.OrganizationalEntity;
import org.intalio.tempo.workflow.tmsb4p.parser.ASTArray;
import org.intalio.tempo.workflow.tmsb4p.parser.ASTConstant;
import org.intalio.tempo.workflow.tmsb4p.parser.ASTFunNode;
import org.intalio.tempo.workflow.tmsb4p.parser.ASTVarNode;
import org.intalio.tempo.workflow.tmsb4p.parser.Node;
import org.intalio.tempo.workflow.tmsb4p.parser.ParseException;
import org.intalio.tempo.workflow.tmsb4p.parser.Parser;
import org.intalio.tempo.workflow.tmsb4p.parser.ParserConstants;
import org.intalio.tempo.workflow.tmsb4p.parser.ParserTreeConstants;
import org.intalio.tempo.workflow.tmsb4p.server.dao.GenericRoleType;

public class TaskJPAStatement {
    private static final String TASK_ALIAS = "t";

    // for the attachment info
    private static final String ATT_ALIAS = "a";
    private static final String ATT_FROM_CLAUSE = "in (t.attachments) a";

    // for user and role info
    // for the task stake holders
    private static final String STAKE_HOLDERS_ALIAS = "th";
    private static final String STAKE_HOLDERS_FROM_CLAUSE = "in (t.taskStakeholders.principals) th";
    // for the potential owners
    private static final String POTENTIAL_OWNER_ALIAS = "tp";
    private static final String POTENTIAL_OWNER_FROM_CLAUSE = "in (t.potentialOwners.principals) tp";
    // for the business administrator
    private static final String BUSINESS_ADMIN_ALIAS = "tb";
    private static final String BUSINESS_ADMIN_FROM_CLAUSE = "in (t.businessAdministrators.principals) tb";
    // for the notification recipient
    private static final String NOTIFY_RECIPIENT_ALIAS = "tr";
    private static final String NOTIFY_RECIPIENT_FROM_CLAUSE = "in (t.notificationRecipients.principals) tr";

    private static final String SELECT_ALL_CLAUSE = "*";
    private static final String ORDER_BY_ASC = "asc";
    private static final String ORDER_BY_DESC = "desc";

    // place holder for the user query info in where clause
    private static final String USER_ID_PLACE_HOLDER = "${user_id_holder}";
    private static final String GROUP_ID_PLACE_HOLDER = "${group_id_holder}";
    private static final String ROLE_PLACE_HOLDER = "${role_holder}";

    // function node for the user info in where clause
    private ASTFunNode m_userIdNode = null;
    private ASTFunNode m_groupIdNode = null;
    private ASTFunNode m_rolesNode = null;

    private String m_selectClause = null;
    private String m_whereClause = null;
    private String m_orderbyClause = null;

    private SQLStatement m_statement = new SQLStatement();

    public TaskJPAStatement(String sqlClause) throws SQLClauseException {
        parseSQLClause(sqlClause);
    }

    public TaskJPAStatement(String selectClause, String whereClause, String orderbyClause) {
        this.m_selectClause = selectClause;
        this.m_whereClause = whereClause;
        this.m_orderbyClause = orderbyClause;
    }

    /*
     * public String getJPAClause() throws InvalidAttributeException {
     * StringBuffer result = new StringBuffer(); // for the select clause
     * result.append("select "); if (selectClauses == null) { // default to all
     * the data result.append(this.alias); } else { for (int i = 0; i <
     * this.selectClauses.size(); i++) { String clause = selectClauses.get(i);
     * if (converter != null) { clause = converter.convertAttribute(clause); }
     * if (i == 0) { result.append(clause); } else { result.append(", " +
     * clause); } } } // for the from clause
     * result.append(" from ").append(this.className).append(" " + this.alias);
     * // for the where clause if (this.whereClause != null) {
     * result.append(" where " + this.whereClause.toString(converter)); } // for
     * the order by clause if (this.orderByClause != null) {
     * result.append(orderByClause.toString(converter)); } return
     * result.toString(); } public static void main(String[] args) throws
     * Exception { TaskClauseConverter taskConverter = new
     * TaskClauseConverter("t"); TaskJPAStatement statement = new
     * TaskJPAStatement("Task", "t", taskConverter); List<String> selectClause =
     * new ArrayList<String>(); selectClause.add(TaskView.ID);
     * selectClause.add(TaskView.STATUS); selectClause.add(TaskView.PRIORITY);
     * selectClause.add(TaskView.SKIPABLE); selectClause.add("Task." +
     * TaskView.PRIMARY_SEARCH_BY); statement.setSelectClauses(selectClause);
     * SQLClause clause1 = new SQLClause(TaskView.ID, SQLOperators.EQUALS,
     * "id"); SQLClause clause2 = new SQLClause(TaskView.NAME,
     * SQLOperators.GREATER_THAN, "name"); SQLClause clause3 = new
     * SQLClause(TaskView.STATUS, SQLOperators.LESS_THAN, "status"); SQLClause
     * clause4 = new SQLClause(TaskView.PRIORITY, SQLOperators.EQUALS, 1);
     * SQLClause clause5 = new SQLClause(TaskView.SKIPABLE, SQLOperators.EQUALS,
     * true); SQLClause clause6 = new SQLClause(TaskView.PRIMARY_SEARCH_BY,
     * SQLOperators.EQUALS, "PRIMARY_SEARCH_BY"); AndClause andClause1 = new
     * AndClause(); andClause1.addClause(clause1);
     * andClause1.addClause(clause2); andClause1.addClause(clause3); OrClause
     * orClause = new OrClause(); orClause.addClause(clause4);
     * orClause.addClause(clause5); AndClause andClause2 = new AndClause();
     * andClause2.addClause(andClause1); andClause2.addClause(orClause);
     * andClause2.addClause(clause6); statement.setWhereClauses(andClause2);
     * OrderByClause orderByClause = new OrderByClause();
     * orderByClause.addClause(new OrderByOption(TaskView.PRIORITY, "asc"));
     * orderByClause.addClause(new OrderByOption("Task." +
     * TaskView.PRIMARY_SEARCH_BY, "desc"));
     * statement.setOrderByClause(orderByClause);
     * System.out.println(statement.getJPAClause()); Map<Integer, Object>
     * paraValues = statement.getParaValues(); Set keys = paraValues.keySet();
     * for (Iterator iterator = keys.iterator(); iterator.hasNext();) { Object
     * key = iterator.next(); System.out.println(" key: " + key + "  value: " +
     * paraValues.get(key)); } }
     */

    private void convertSelectClause() throws InvalidFieldException {
        String[] clauses = QueryUtil.parseSelectClause(this.m_selectClause);
        if ((clauses.length == 0) && (SELECT_ALL_CLAUSE.equals(clauses[0]))) {
            m_statement.addSelectClause(TASK_ALIAS);
            return;
        }

        for (int i = 0; i < clauses.length; i++) {
            String field = TaskFieldConverter.getFieldForSelectClause(clauses[i]);
            if (field != null) {
                m_statement.addSelectClause(TASK_ALIAS + "." + field);
            }
        }
    }

    private void convertWhereClause() throws ParseException, InvalidFieldException {
        Reader reader = new StringReader(this.m_whereClause);
        Parser parser = new Parser(reader);
        Node topNode = parser.parseStream(reader);

        StringBuffer output = new StringBuffer();
        if (topNode != null) {
            if (ParserTreeConstants.jjtNodeName[ParserTreeConstants.JJTFUNNODE].equals(topNode.getNodeName())) {
                outputNode((ASTFunNode) topNode, output);
            } else {
                // throw exception
                throw new ParseException("Invalid where clause");
            }
        }

        // output the user query information
        outputUserQuerInfo(output);
        this.m_statement.addWhereClause(output.toString());
    }

    private void outputUserQuerInfo(StringBuffer output) throws InvalidFieldException {
        // if the group or user query is defined , the role query must be
        // defined.
        if ((this.m_groupIdNode != null) || (this.m_userIdNode != null)) {
            if (this.m_rolesNode == null) {
                throw new InvalidFieldException("User role has been defined.");
            }

            // extract the roles function name
            String roleOpName = this.getFunctionName(m_rolesNode);
            if ((!QueryOperator.EQUALS.equals(roleOpName)) && (!QueryOperator.NOT_EQUALS.equals(roleOpName)) && (!QueryOperator.NOT_EQUALS2.equals(roleOpName))
                            && (!QueryOperator.IN.equals(roleOpName))) {
                throw new InvalidFieldException("The role query operator name is invalid.");
            }
            // extract the roles info
            List<String> roles = extractRoles(m_rolesNode, roleOpName);

            if (m_groupIdNode != null) {
                outputRoleQueryInfo(this.m_groupIdNode, OrganizationalEntity.GROUP_ENTITY, roleOpName, roles, output);
            } else if (m_userIdNode != null) {
                outputRoleQueryInfo(this.m_groupIdNode, OrganizationalEntity.USER_ENTITY, roleOpName, roles, output);
            }
        } else {
            // no user id and group query info
            if (this.m_rolesNode != null) {
                // can only be two operator "is null" or "is not null"
                String roleFunName = this.getFunctionName(m_rolesNode);
                if (!QueryOperator.IS_NULL.equals(roleFunName) && !QueryOperator.IS_NOT_NULL.equals(roleFunName)) {
                    throw new InvalidFieldException("The role query operator is invalid.");
                }

                String roleName = (String) getNodeValue(m_rolesNode.jjtGetChild(0));
                outputRoleNullQuery(roleName, roleFunName, output);
            }
        }
    }

    private void outputRoleNullQuery(String roleName, String operatorName, StringBuffer output) {
        if (roleName.equalsIgnoreCase(GenericRoleType.excluded_owners.name())) {
            // TODO: ignore it, should model this role in the task entity?
            return;
        }

        int start = output.indexOf(ROLE_PLACE_HOLDER);
        int end = start + ROLE_PLACE_HOLDER.length();
        String mappingRole = TaskFieldConverter.getRoleMappingField(roleName);
        String realSql = " " + mappingRole + " " + operatorName;

        // replace with the real sql.
        output.replace(start, end, realSql);

    }

    private void outputRoleQueryInfo(Node node, String entityType, String roleOpName, List<String> roles, StringBuffer output) {
        // real value of the users or groups
        // TODO:
        // String values =

    }

    private List<String> extractRoles(ASTFunNode node, String funName) {
        List<String> roles = null;
        Node valueNode = node.jjtGetChild(1);
        if ((QueryOperator.EQUALS.equals(funName)) || (QueryOperator.NOT_EQUALS.equals(funName)) || (QueryOperator.NOT_EQUALS2.equals(funName))) {
            // the roles should be string.
            String values = (String) this.getNodeValue(valueNode);
            roles = QueryUtil.parseString(values, ",");
        } else if (QueryOperator.IN.equals(funName)) {
            // should be one List object
            roles = (List<String>) getNodeValue(valueNode);
        }

        return roles;
    }

    private boolean validateRoleFun(String funName) {
        // the function name can only be "=", "<>", "!=" and "in"
        if ((QueryOperator.EQUALS.equals(funName)) || (QueryOperator.NOT_EQUALS.equals(funName)) || (QueryOperator.NOT_EQUALS2.equals(funName))
                        || (QueryOperator.IN.equals(funName))) {
            return true;
        }

        return false;
    }

    private void outputNode(ASTFunNode node, StringBuffer output) throws InvalidFieldException {
        if (isAtomNode(node)) {
            outputAtomNode(node, output);
        } else {
            // at most two children for one function node
            int childNum = node.jjtGetNumChildren();
            if (childNum > 1) {
                output.append("(");
            }
            if (childNum > 0) {
                Node child = node.jjtGetChild(0);
                outputNode((ASTFunNode) child, output);
            }

            if (childNum > 1) {
                output.append(" " + getFunctionName(node) + " ");
                Node child = node.jjtGetChild(1);
                outputNode((ASTFunNode) child, output);
                output.append(")");
            }
        }
    }

    private boolean isAtomNode(Node node) {
        String nodeName = node.getNodeName();

        if (ParserTreeConstants.jjtNodeName[ParserTreeConstants.JJTFUNNODE].equals(nodeName)) {
            String funName = ((ASTFunNode) node).getName();
            if ((ParserConstants.tokenImage[ParserConstants.AND].equals(funName)) || (ParserConstants.tokenImage[ParserConstants.OR].equals(funName))) {
                return false;
            }

            return true;
        }

        return true;
    }

    private String getFunctionName(ASTFunNode node) {
        String name = node.getName();
        if (name.startsWith("\"")) {
            name = name.substring(1);
        }
        if (name.endsWith("\"")) {
            name = name.substring(0, name.length() - 1);
        }

        return name;
    }

    private void outputAtomNode(ASTFunNode node, StringBuffer output) throws InvalidFieldException {
        int number = node.jjtGetNumChildren();

        if (number > 0) {
            // get the parameter name
            String paraName = (String) getNodeValue(node.jjtGetChild(0));
            String funName = getFunctionName(node);
            Object paraValue = null;
            if (number > 1) {
                paraValue = getNodeValue(node.jjtGetChild(1));
            }

            String viewField = TaskFieldConverter.getTaskViewField(paraName);

            ParameterValues paraValues = null;
            String alias = null;
            if ((TaskView.ATTACHMENT_NAME.equals(viewField)) || (TaskView.ATTACHMENT_TYPE.equals(viewField))) {
                // check for the attachment
                m_statement.addFromClause(ATT_FROM_CLAUSE);
                paraValues = TaskFieldConverter.convertAttachmentField(viewField, funName, paraValue);
                alias = ATT_ALIAS;
            } else if (TaskView.USERID.equals(viewField)) {
                this.m_userIdNode = node;
                output.append(USER_ID_PLACE_HOLDER);
            } else if (TaskView.GROUP.equals(viewField)) {
                this.m_groupIdNode = node;
                output.append(GROUP_ID_PLACE_HOLDER);
            } else if (TaskView.GENERIC_HUMAN_ROLE.equals(viewField)) {
                this.m_rolesNode = node;
                output.append(ROLE_PLACE_HOLDER);
            } else {
                paraValues = TaskFieldConverter.convertWhereClause(viewField, funName, paraValue);
                alias = TASK_ALIAS;
            }

            if (paraValues != null) {
                output.append(paraValues.toJPAClause(alias, m_statement.getParaValuesStartIdx()));
                Map<Integer, Object> values = paraValues.getJPAValues();
                m_statement.getParaValues().putAll(values);
            }

        }
    }

    private String getNodeValue(ASTVarNode node) {
        return node.getValue();
    }

    private Object getNodeValue(ASTConstant node) {
        return node.getValue();
    }

    private Object getNodeValue(Node node) {
        String nodeName = node.getNodeName();
        if (ParserTreeConstants.jjtNodeName[ParserTreeConstants.JJTARRAY].equals(nodeName)) {
            return getNodeValue((ASTArray) node);
        } else if (ParserTreeConstants.jjtNodeName[ParserTreeConstants.JJTCONSTANT].equals(nodeName)) {
            return getNodeValue((ASTConstant) node);
        } else if (ParserTreeConstants.jjtNodeName[ParserTreeConstants.JJTVARNODE].equals(nodeName)) {
            return getNodeValue((ASTVarNode) node);
        }

        return null;
    }

    private List<String> getNodeValue(ASTArray node) {
        int num = node.jjtGetNumChildren();
        if (num == 0) {
            return null;
        }

        List<String> result = new ArrayList<String>();
        for (int i = 0; i < num; i++) {
            Node childNode = node.jjtGetChild(i);
            result.add((String) getNodeValue(childNode));
        }

        return result;
    }

    // TODO:
    private String convertOrderbyClause() throws InvalidFieldException, SQLClauseException {
        // the orderby clause maybe as
        // "order by Task.Priority asc, Task.Name desc"
        // String[] clauses = QueryUtil.parseOrderbyClause(this.orderbyClause);
        // if ((clauses.length == 0) && (SELECT_ALL_CLAUSE.equals(clauses[0])))
        // {
        // return TASK_ALIAS;
        // }
        //
        // String field = null;
        // String orderbyOption = null;
        // StringBuffer result = new StringBuffer();
        // for (int i = 0; i < clauses.length; i++) {
        // String[] temp = clauses[i].split(" ");
        // if (temp.length > 1) {
        // field = temp[0];
        // orderbyOption = temp[1];
        // }
        //            
        // field = TaskFieldConverter
        // .getFieldForOrderClause(clauses[i]);
        // if (field != null) {
        // if (result.length() > 0) {
        // result.append(",");
        // }
        // result.append(TASK_ALIAS + "." + field);
        //                
        // if (orderbyOption != null) {
        // result.append(" ").append(orderbyOption);
        // }
        // }
        //            
        // field = null;
        // orderbyOption = null;
        // }
        //
        // return result.toString();
        return null;
    }

    /**
     * It is to extract the select, from, where, and order by clause.
     * 
     * @param sqlClause
     */
    private void parseSQLClause(String sqlClause) throws SQLClauseException {
        if ((sqlClause == null) || (sqlClause.length() == 0)) {
            throw new SQLClauseException("SQL clause can't be empty!");
        }

        String newClause = sqlClause.toLowerCase();
        if (!newClause.startsWith("select")) {
            throw new SQLClauseException("No select clause found!");
        }

        if (newClause.indexOf(" from ") <= 0) {
            throw new SQLClauseException("No from clause found!");
        }

        // extract the select clause
        int fromIdx = newClause.indexOf(" from ");
        int whereIdx = newClause.indexOf(" where ");
        int orderbyIdx = newClause.indexOf(" order ");
    }

    public SQLStatement getStatement() throws InvalidFieldException, ParseException, SQLClauseException {
        // convert all clauses
        this.convertSelectClause();
        this.convertWhereClause();
        this.convertOrderbyClause();

        return this.m_statement;
    }

    public static void main(String[] args) throws Exception {
        String selectClause = "select id, tasktype, activationtime, startbyexists";
        String whereClause = "task.id='testingid001' and task.tasktype='all' "
                        + "and startbyexists='true' and skipable='true' and (task.priority = 1 or priority = 2) and createdon='2007-12-12 12:4:45'";
        String orderbyClause = "";

        TaskJPAStatement taskStatement = new TaskJPAStatement(selectClause, whereClause, orderbyClause);
        SQLStatement statement = taskStatement.getStatement();

        System.out.println(statement.toString());

        Map<Integer, Object> paraValues = statement.getParaValues();
        if (paraValues != null) {
            Set<Integer> keys = paraValues.keySet();

            for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
                Object key = iterator.next();
                System.out.println("idx: " + key);
                System.out.println("val: " + paraValues.get(key));
            }
        }
    }
}
