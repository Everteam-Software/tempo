//package com.intalio.www.ws_ht.api.wsdl;
//
//import org.apache.axis2.databinding.types.NonNegativeInteger;
//
//import com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GenericHumanRoleE;
//import com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.TGenericHumanRoleE;
//import com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.TOrganizationalEntity;
//import com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.TPeopleAssignmentsE;
//import com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.TUser;
//import com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.TUserlist;
//
//public class TMSServiceTest {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) throws Exception{
//		HumanTaskOperationServicesStub stub = new HumanTaskOperationServicesStub("http://localhost:8080/axis2/services/HumanTaskOperationServices");
//		stub.setParticipantToken("VE9LRU4mJnVzZXI9PWFkbWluJiZpc3N1ZWQ9PTExODA0NzY2NjUzOTMmJnJvbGVzPT1pbnRhbGlvXHByb2Nlc3NhZG1pbmlzdHJhdG9yLGV4YW1wbGVzXGVtcGxveWVlLGludGFsaW9ccHJvY2Vzc21hbmFnZXIsZXhhbXBsZXNcbWFuYWdlciYmZnVsbE5hbWU9PUFkbWluaW5pc3RyYXRvciYmZW1haWw9PWFkbWluQGV4YW1wbGUuY29tJiZub25jZT09NDMxNjAwNTE5NDM5MTk1MDMzMyYmdGltZXN0YW1wPT0xMTgwNDc2NjY1Mzk1JiZkaWdlc3Q9PTVmM1dQdDBXOEp2UlpRM2gyblJ6UkRrenRwTT0mJiYmVE9LRU4");
//		
//		// create
//		System.out.println("============ testing create ==============");
//		HumanTaskOperationServicesStub.Create create50 = new HumanTaskOperationServicesCallbackHandler.Create();
//		create50.setIn("test");
//		HumanTaskOperationServicesStub.THumanTaskContext context = new HumanTaskOperationServicesStub.THumanTaskContext();
//		NonNegativeInteger priority =  new NonNegativeInteger("2");
//		context.setPriority(priority);
//		context.setIsSkipable(false);
//		TPeopleAssignmentsE  p = new TPeopleAssignmentsE();
//		GenericHumanRoleE role = new GenericHumanRoleE();
//		TGenericHumanRoleE ba = new TGenericHumanRoleE();
//		TOrganizationalEntity _ba = new TOrganizationalEntity();
//		TUserlist users = new TUserlist();
//		TUser user = new TUser();
//		user.setTUser("intalio/manager");
//		users.addUser(user);
//		_ba.setUsers(users);
//		ba.setOrganizationalEntity(_ba);
//		role.setBusinessAdministrators(ba);
//		p.addGenericHumanRole(role);
//		context.setPeopleAssignments(p);
//		create50.addHumanTaskContext(context);
//		HumanTaskOperationServicesStub.CreateResponse res = stub.create(create50);
//		System.out.println(res.getOut());
//		System.out.println("============ End of testing create ==============");
//		
//		
//		// get created task id
//		String taskId = res.getOut().substring(0, res.getOut().length()-1);
//		System.out.println("task id: "+ taskId);
//		
//		// activate task
//		System.out.println("============ testing activate ==============");
//		
//	
//		System.out.println("============ End of testing activate ==============");
//
//	}
//
//}
