package org.intalio.tempo.workflow.tmsb4p.query;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
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
import org.intalio.tempo.workflow.tmsb4p.query.ParameterValues.SinglePara;
import org.intalio.tempo.workflow.tmsb4p.server.dao.GenericRoleType;

public class TaskJPAStatement {
    private static final String TASK_ALIAS = "t";

    // for the attachment info
    static final String ATT_ALIAS = "a";
    static final String ATT_FROM_CLAUSE = "in (t.attachments) a";

    // for user and role info
    // for the task stake holders
    static final String STAKE_HOLDERS_ALIAS = "th";
    static final String STAKE_HOLDERS_FROM_CLAUSE = "in (t.taskStakeholders.principals) th";
    // for the potential owners
    static final String POTENTIAL_OWNER_ALIAS = "tp";
    static final String POTENTIAL_OWNER_FROM_CLAUSE = "in (t.potentialOwners.principals) tp";
    // for the business administrator
    static final String BUSINESS_ADMIN_ALIAS = "tb";
    static final String BUSINESS_ADMIN_FROM_CLAUSE = "in (t.businessAdministrators.principals) tb";
    // for the notification recipient
    static final String NOTIFY_RECIPIENT_ALIAS = "tr";
    static final String NOTIFY_RECIPIENT_FROM_CLAUSE = "in (t.notificationRecipients.principals) tr";

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

    private boolean m_specifiedRoles = false;

    // additional attributes if has specified the roles information
    private Set<String> m_queryRoles = null;
    private List<String> m_groupOrUsers = null;
    private String m_entityType = null;

    private SQLStatement m_statement = new SQLStatement();

    public TaskJPAStatement(String selectClause, String whereClause, String orderbyClause) {
        this.m_selectClause = selectClause;
        this.m_whereClause = whereClause;
        this.m_orderbyClause = orderbyClause;

        this.m_specifiedRoles = false;

        initialize();
    }

    public TaskJPAStatement(Set<String> roles, List<String> groupOrUsers, String entityType, String whereClause) {
        this.m_queryRoles = roles;
        this.m_groupOrUsers = groupOrUsers;
        this.m_entityType = entityType;
        this.m_whereClause = whereClause;

        this.m_specifiedRoles = true;
        initialize();
    }

    public TaskJPAStatement(Set<String> roles, List<String> groupOrUsers, String entityType, String selectClause, String whereClause) {
        this(roles, groupOrUsers, entityType, whereClause);
        this.m_selectClause = selectClause;
    }
    
    private void initialize() {
        this.m_statement.addFromClause("Task " + TASK_ALIAS);
    }

    private void convertSelectClause() throws InvalidFieldException {
        String[] clauses = QueryUtil.parseSelectClause(this.m_selectClause);
        if ((clauses == null) || (clauses.length == 0)) {
            m_statement.addSelectClause(TASK_ALIAS);
            return;
        }

        if ((clauses.length == 1) && (SELECT_ALL_CLAUSE.equals(clauses[0]))) {
            m_statement.addSelectClause(TASK_ALIAS);
            return;
        }

        for (int i = 0; i < clauses.length; i++) {
            String viewField = TaskFieldConverter.getTaskViewField(clauses[i]);

            if ((TaskView.ATTACHMENT_TYPE.equals(viewField)) || (TaskView.ATTACHMENT_NAME.equals(viewField))) {

                m_statement.addFromClause(TaskJPAStatement.ATT_FROM_CLAUSE);
                m_statement.addSelectClause(TaskJPAStatement.ATT_ALIAS);

            } else {
                String mappingField = TaskFieldConverter.getFieldForSelectClause(clauses[i]);
                if (mappingField != null) {
                    m_statement.addSelectClause(TASK_ALIAS + "." + mappingField);
                }
            }
        }
    }

    private void convertWhereClause() throws ParseException, InvalidFieldException {
        if ((this.m_whereClause == null) || (this.m_whereClause.trim().length() == 0)) {
            return;
        }
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

        if (!this.m_specifiedRoles) {
            // output the user query information
            outputUserQuerInfo(output);
        }

        if (output.length() > 0) {
            this.m_statement.addWhereClause(output.toString());
        }
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
            Set<String> inclduedRoles = extractIncludedRoles(m_rolesNode, roleOpName);

            if (m_groupIdNode != null) {
                outputRoleQueryInfo(this.m_groupIdNode, OrganizationalEntity.GROUP_ENTITY, inclduedRoles, output);
            }
            if (m_userIdNode != null) {
                outputRoleQueryInfo(this.m_userIdNode, OrganizationalEntity.USER_ENTITY, inclduedRoles, output);
            }

            // for the role place holder, just replace it with (1==1), it won't
            // affect the query
            // TODO: the ideal way is to remove the whole role clause.
            int start = output.indexOf(ROLE_PLACE_HOLDER);
            int end = start + ROLE_PLACE_HOLDER.length();
            output.replace(start, end, "(1=1)");
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

    private void outputRoleQueryInfo(ASTFunNode node, String entityType, Set<String> roles, StringBuffer output) throws InvalidFieldException {
        // one function node should have one child at least
        Node paraNode = node.jjtGetChild(0);
        String paraName = (String) this.getNodeValue(paraNode);

        if (node.jjtGetNumChildren() != 2) {
            throw new InvalidFieldException("No value has been defined for the field: " + paraName);
        }

        Object values = this.getNodeValue(node.jjtGetChild(1));
        List userOrGroupVal = TaskFieldConverter.convertListValue(values);

        // get the user or group info
        String opName = this.getFunctionName(node);
        String jpaCollectionOpName = getJPACollectionOp(opName, paraName);

        int start = 0;
        int end = 0;
        String roleQuery = null;
        if (OrganizationalEntity.GROUP_ENTITY.equals(entityType)) {
            roleQuery = outputGroupRoleQueryInfo(roles, userOrGroupVal, jpaCollectionOpName);

            // replace the group place holder with the role query
            start = output.indexOf(GROUP_ID_PLACE_HOLDER);
            end = start + GROUP_ID_PLACE_HOLDER.length();
            output.replace(start, end, roleQuery);
        } else if (OrganizationalEntity.USER_ENTITY.equals(entityType)) {
            roleQuery = outputUserRoleQueryInfo(roles, userOrGroupVal, jpaCollectionOpName);

            // replace the user place holder with the role query
            start = output.indexOf(USER_ID_PLACE_HOLDER);
            end = start + USER_ID_PLACE_HOLDER.length();
            output.replace(start, end, roleQuery);
        }
    }

    private String outputGroupRoleQueryInfo(Set<String> includedRoles, List<String> groups, String collectionOp) {
        String clauseRelation = null;
        if (QueryOperator.IN.equals(collectionOp)) {
            clauseRelation = "or";
        } else {
            // not in
            clauseRelation = "and";
        }

        List<ParameterValues> roleQueries = new ArrayList<ParameterValues>();
        for (Iterator<String> iterator = includedRoles.iterator(); iterator.hasNext();) {
            String role = iterator.next();

            if (role.equalsIgnoreCase(GenericRoleType.task_initiator.name())) {
                // ignore it, only worked for the user type.
            } else if (role.equalsIgnoreCase(GenericRoleType.task_stakeholders.name())) {
                this.m_statement.addFromClause(STAKE_HOLDERS_FROM_CLAUSE);

                ParameterValues paraValues = new ParameterValues("and");
                // Assemble the the sql like
                // "(th.value in (?1) and t.taskStakeholders.entityType=?2)"
                paraValues.addPara(new SinglePara(STAKE_HOLDERS_ALIAS + ".value", collectionOp, groups));
                paraValues.addPara(new SinglePara(TASK_ALIAS + ".taskStakeholders.entityType", QueryOperator.EQUALS, OrganizationalEntity.GROUP_ENTITY));

                roleQueries.add(paraValues);
            } else if (role.equalsIgnoreCase(GenericRoleType.potential_owners.name())) {
                this.m_statement.addFromClause(POTENTIAL_OWNER_FROM_CLAUSE);

                ParameterValues paraValues = new ParameterValues("and");
                // Assemble the sql like
                // "(tp.value in (?3) and t.potentialOwners.entityType=?4)"
                paraValues.addPara(new SinglePara(POTENTIAL_OWNER_ALIAS + ".value", collectionOp, groups));
                paraValues.addPara(new SinglePara(TASK_ALIAS + ".potentialOwners.entityType", QueryOperator.EQUALS, OrganizationalEntity.GROUP_ENTITY));

                roleQueries.add(paraValues);
            } else if (role.equalsIgnoreCase(GenericRoleType.actual_owner.name())) {
                // ignore it, only worked for the user type.
            } else if (role.equalsIgnoreCase(GenericRoleType.excluded_owners.name())) {
                // ignore it, should model this role in the task entity?
            } else if (role.equalsIgnoreCase(GenericRoleType.business_administrators.name())) {
                this.m_statement.addFromClause(BUSINESS_ADMIN_FROM_CLAUSE);

                ParameterValues paraValues = new ParameterValues("and");
                // Assemble the sql like
                // "(tb.value in (?5) and t.businessAdministrators.entityType=?6)"
                paraValues.addPara(new SinglePara(BUSINESS_ADMIN_ALIAS + ".value", collectionOp, groups));
                paraValues.addPara(new SinglePara(TASK_ALIAS + ".businessAdministrators.entityType", QueryOperator.EQUALS, OrganizationalEntity.GROUP_ENTITY));

                roleQueries.add(paraValues);
            } else if (role.equalsIgnoreCase(GenericRoleType.notification_recipients.name())) {
                this.m_statement.addFromClause(NOTIFY_RECIPIENT_FROM_CLAUSE);

                ParameterValues paraValues = new ParameterValues("and");
                // Assemble the sql like
                // "(tr.value in (?5) and t.businessAdministrators.entityType=?6)"
                paraValues.addPara(new SinglePara(NOTIFY_RECIPIENT_ALIAS + ".value", collectionOp, groups));
                paraValues.addPara(new SinglePara(TASK_ALIAS + ".businessAdministrators.entityType", QueryOperator.EQUALS, OrganizationalEntity.GROUP_ENTITY));

                roleQueries.add(paraValues);
            }
        }

        // output the sql string
        StringBuffer result = new StringBuffer();
        if (!roleQueries.isEmpty()) {
            for (int i = 0; i < roleQueries.size(); i++) {
                ParameterValues paraValues = roleQueries.get(i);

                if (i != 0) {
                    result.append(" ").append(clauseRelation).append(" ");
                }

                // clauses
                result.append(paraValues.toJPAClause(null, m_statement.getParaValuesStartIdx()));

                // values
                Map<String, Object> values = paraValues.getJPAValues();
                m_statement.getParaValues().putAll(values);
            }
        }

        if (result.length() == 0) {
            return "";
        }
        return "(" + result.toString() + ")";
    }

    private String outputUserRoleQueryInfo(Set<String> includedRoles, List<String> users, String collectionOp) {
        String clauseRelation = null;
        if (QueryOperator.IN.equals(collectionOp)) {
            clauseRelation = "or";
        } else {
            // not in
            clauseRelation = "and";
        }

        List<ParameterValues> roleQueries = new ArrayList<ParameterValues>();
        for (Iterator<String> iterator = includedRoles.iterator(); iterator.hasNext();) {
            String role = iterator.next();

            if (role.equalsIgnoreCase(GenericRoleType.task_initiator.name())) {
                ParameterValues paraValues = new ParameterValues();

                // Assemble the sql like "t.taskInitiator=?9"
                paraValues.addPara(new SinglePara(TASK_ALIAS + ".taskInitiator", collectionOp, users));
                roleQueries.add(paraValues);
            } else if (role.equalsIgnoreCase(GenericRoleType.task_stakeholders.name())) {
                this.m_statement.addFromClause(STAKE_HOLDERS_FROM_CLAUSE);

                ParameterValues paraValues = new ParameterValues("and");
                // Assemble the sql like
                // "(th.value in (?1) and t.taskStakeholders.entityType=?2)"
                paraValues.addPara(new SinglePara(STAKE_HOLDERS_ALIAS + ".value", collectionOp, users));
                paraValues.addPara(new SinglePara(TASK_ALIAS + ".taskStakeholders.entityType", QueryOperator.EQUALS, OrganizationalEntity.USER_ENTITY));

                roleQueries.add(paraValues);
            } else if (role.equalsIgnoreCase(GenericRoleType.potential_owners.name())) {
                this.m_statement.addFromClause(POTENTIAL_OWNER_FROM_CLAUSE);

                ParameterValues paraValues = new ParameterValues("and");
                // Assemble the sql like
                // "(tp.value in (?3) and t.potentialOwners.entityType=?4)"
                paraValues.addPara(new SinglePara(POTENTIAL_OWNER_ALIAS + ".value", collectionOp, users));
                paraValues.addPara(new SinglePara(TASK_ALIAS + ".potentialOwners.entityType", QueryOperator.EQUALS, OrganizationalEntity.USER_ENTITY));

                roleQueries.add(paraValues);
            } else if (role.equalsIgnoreCase(GenericRoleType.actual_owner.name())) {
                ParameterValues paraValues = new ParameterValues();

                // Assemble the sql like "t.actualOwner=?9"
                paraValues.addPara(new SinglePara(TASK_ALIAS + ".actualOwner", collectionOp, users));
                roleQueries.add(paraValues);
            } else if (role.equalsIgnoreCase(GenericRoleType.excluded_owners.name())) {
                // ignore it, should model this role in the task entity?
            } else if (role.equalsIgnoreCase(GenericRoleType.business_administrators.name())) {
                this.m_statement.addFromClause(BUSINESS_ADMIN_FROM_CLAUSE);

                ParameterValues paraValues = new ParameterValues("and");
                // Assemble the sql like
                // "(tb.value in (?5) and t.businessAdministrators.entityType=?6)"
                paraValues.addPara(new SinglePara(BUSINESS_ADMIN_ALIAS + ".value", collectionOp, users));
                paraValues.addPara(new SinglePara(TASK_ALIAS + ".businessAdministrators.entityType", QueryOperator.EQUALS, OrganizationalEntity.USER_ENTITY));

                roleQueries.add(paraValues);
            } else if (role.equalsIgnoreCase(GenericRoleType.notification_recipients.name())) {
                this.m_statement.addFromClause(NOTIFY_RECIPIENT_FROM_CLAUSE);

                ParameterValues paraValues = new ParameterValues("and");
                // Assemble the sql like
                // "(tr.value in (?5) and t.businessAdministrators.entityType=?6)"
                paraValues.addPara(new SinglePara(NOTIFY_RECIPIENT_ALIAS + ".value", collectionOp, users));
                paraValues.addPara(new SinglePara(TASK_ALIAS + ".businessAdministrators.entityType", QueryOperator.EQUALS, OrganizationalEntity.USER_ENTITY));

                roleQueries.add(paraValues);
            }
        }

        // output the sql string
        StringBuffer result = new StringBuffer();
        if (!roleQueries.isEmpty()) {
            for (int i = 0; i < roleQueries.size(); i++) {
                ParameterValues paraValues = roleQueries.get(i);

                if (i != 0) {
                    result.append(" ").append(clauseRelation).append(" ");
                }

                // clauses
                result.append(paraValues.toJPAClause(null, m_statement.getParaValuesStartIdx()));

                // values
                Map<String, Object> values = paraValues.getJPAValues();
                m_statement.getParaValues().putAll(values);
            }
        }

        if (result.length() == 0) {
            return "";
        }

        return "(" + result.toString() + ")";
    }

    private String getJPACollectionOp(String opName, String exceptionField) throws InvalidFieldException {
        if (opName == null) {
            throw new InvalidFieldException("The operator name can't be empty for field " + exceptionField);
        }

        if ((QueryOperator.EQUALS.equals(opName)) || (QueryOperator.IN.equalsIgnoreCase(opName))) {
            return QueryOperator.IN;
        } else if ((QueryOperator.NOT_EQUALS.equals(opName)) || (QueryOperator.NOT_EQUALS2.equals(opName)) || (QueryOperator.NOT_IN.equalsIgnoreCase(opName))) {
            return QueryOperator.NOT_IN;
        } else {
            throw new InvalidFieldException("Invalid operator for field " + exceptionField);
        }
    }

    private Set<String> extractIncludedRoles(ASTFunNode node, String opName) {
        List<String> roles = null;
        Node valueNode = node.jjtGetChild(1);

        if ((QueryOperator.EQUALS.equals(opName)) || (QueryOperator.NOT_EQUALS.equals(opName)) || (QueryOperator.NOT_EQUALS2.equals(opName))) {
            // the roles should be string.
            String values = (String) this.getNodeValue(valueNode);
            roles = QueryUtil.parseString(values, ",");
        } else if ((QueryOperator.IN.equals(opName)) || (QueryOperator.NOT_IN.equals(opName))) {
            // should be one List object
            roles = (List<String>) getNodeValue(valueNode);
        }

        // if the operator is "<>", "not in" or "!=", needs to get the included
        // role name
        if ((QueryOperator.NOT_EQUALS.equals(opName)) || (QueryOperator.NOT_EQUALS2.equals(opName)) || (QueryOperator.NOT_IN.equals(opName))) {
            return TaskFieldConverter.getInlcudedRole(roles);
        }

        Set<String> result = new HashSet<String>();
        result.addAll(roles);

        return result;
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
                Map<String, Object> values = paraValues.getJPAValues();
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

    private List<Object> getNodeValue(ASTArray node) {
        int num = node.jjtGetNumChildren();
        if (num == 0) {
            return null;
        }

        List<Object> result = new ArrayList<Object>();
        for (int i = 0; i < num; i++) {
            Node childNode = node.jjtGetChild(i);
            result.add(getNodeValue(childNode));
        }

        return result;
    }

    private String convertOrderbyClause() throws InvalidFieldException, SQLClauseException {
        // the orderby clause maybe as
        // "order by Task.Priority asc, Task.Name desc"
        if (this.m_orderbyClause == null) {
            return null;
        }

        String[] clauses = QueryUtil.parseOrderbyClause(this.m_orderbyClause);
        if ((clauses == null) || (clauses.length == 0)) {
            return null;
        }

        String field = null;
        String orderbyOption = null;
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < clauses.length; i++) {
            String[] temp = clauses[i].split(" ");
            if (temp.length == 0) {
                throw new SQLClauseException("Invalid order by clause: " + this.m_orderbyClause);
            }
            field = temp[0];
            if (temp.length > 1) {
                orderbyOption = temp[1];
            }

            field = TaskFieldConverter.getFieldForOrderClause(field);
            if (field != null) {
                if (result.length() > 0) {
                    result.append(",");
                }
                result.append(TASK_ALIAS + "." + field);

                if (orderbyOption != null) {
                    result.append(" ").append(orderbyOption);
                }
            }

            field = null;
            orderbyOption = null;
        }

        return result.toString();
    }

    public SQLStatement getStatement() throws InvalidFieldException, ParseException, SQLClauseException {
        // convert all clauses
        if (!this.m_statement.isInitialized()) {
            return this.m_statement;
        }
        if (this.m_specifiedRoles) {
            if ((this.m_selectClause == null) || (this.m_selectClause.length() == 0)) {
                this.m_statement.addSelectClause(TASK_ALIAS);
            } else {
                this.convertSelectClause();
            }
            this.convertWhereClause();

            // add the query info about the role query
            String roleQuery = null;
            if (OrganizationalEntity.GROUP_ENTITY.equalsIgnoreCase(this.m_entityType)) {
                roleQuery = outputGroupRoleQueryInfo(this.m_queryRoles, this.m_groupOrUsers, QueryOperator.IN);
            } else if (OrganizationalEntity.USER_ENTITY.equalsIgnoreCase(this.m_entityType)) {
                roleQuery = outputUserRoleQueryInfo(this.m_queryRoles, this.m_groupOrUsers, QueryOperator.IN);
            }
            if ((roleQuery != null) && (roleQuery.length() > 0)) {
                this.m_statement.addWhereClause(roleQuery);
            }

        } else {
            
            this.convertSelectClause();
            this.convertWhereClause();
            this.convertOrderbyClause();
        }
        
        this.m_statement.setInitialized(false);        
        return this.m_statement;
    }

    static class ParaValuesWithAlias {
        private ParameterValues paraValues = null;
        private String jpaAlias = null;

        public ParaValuesWithAlias(ParameterValues paraValues, String alias) {
            this.paraValues = paraValues;
            this.jpaAlias = alias;
        }

        public String getJPAClause(int startParaIdx) {
            return paraValues.toJPAClause(jpaAlias, startParaIdx);
        }
    }

    public static void main(String[] args) throws Exception {
        String selectClause = "select id, tasktype, activationtime, startbyexists";
        String whereClause = "task.id='testingid001' and task.tasktype='all' "
                        + "and startbyexists='true' and skipable='true' and (task.priority = 1 or priority = 2) and createdon='2007-12-12 12:4:45'";
        String orderbyClause = "";

        TaskJPAStatement taskStatement = new TaskJPAStatement(selectClause, whereClause, orderbyClause);
        SQLStatement statement = taskStatement.getStatement();

        System.out.println(statement.toString());

        Map<String, Object> paraValues = statement.getParaValues();
        if (paraValues != null) {
            Set<String> keys = paraValues.keySet();

            for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
                Object key = iterator.next();
                System.out.println("idx: " + key);
                System.out.println("val: " + paraValues.get(key));
            }
        }
    }
}
