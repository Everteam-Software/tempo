
/**
 * HumanTaskOperationServicesCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

    package com.intalio.www.ws_ht.api.wsdl;

    /**
     *  HumanTaskOperationServicesCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class HumanTaskOperationServicesCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public HumanTaskOperationServicesCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public HumanTaskOperationServicesCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for activate method
            * override this method for handling normal response from activate operation
            */
           public void receiveResultactivate(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.ActivateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from activate operation
           */
            public void receiveErroractivate(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getTaskDescription method
            * override this method for handling normal response from getTaskDescription operation
            */
           public void receiveResultgetTaskDescription(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetTaskDescriptionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getTaskDescription operation
           */
            public void receiveErrorgetTaskDescription(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for delegate method
            * override this method for handling normal response from delegate operation
            */
           public void receiveResultdelegate(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.DelegateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from delegate operation
           */
            public void receiveErrordelegate(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getComments method
            * override this method for handling normal response from getComments operation
            */
           public void receiveResultgetComments(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetCommentsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getComments operation
           */
            public void receiveErrorgetComments(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for stop method
            * override this method for handling normal response from stop operation
            */
           public void receiveResultstop(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.StopResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from stop operation
           */
            public void receiveErrorstop(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getTaskInfo method
            * override this method for handling normal response from getTaskInfo operation
            */
           public void receiveResultgetTaskInfo(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetTaskInfoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getTaskInfo operation
           */
            public void receiveErrorgetTaskInfo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for addAttachment method
            * override this method for handling normal response from addAttachment operation
            */
           public void receiveResultaddAttachment(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.AddAttachmentResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from addAttachment operation
           */
            public void receiveErroraddAttachment(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for forward method
            * override this method for handling normal response from forward operation
            */
           public void receiveResultforward(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.ForwardResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from forward operation
           */
            public void receiveErrorforward(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAttachments method
            * override this method for handling normal response from getAttachments operation
            */
           public void receiveResultgetAttachments(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetAttachmentsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAttachments operation
           */
            public void receiveErrorgetAttachments(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setGenericHumanRole method
            * override this method for handling normal response from setGenericHumanRole operation
            */
           public void receiveResultsetGenericHumanRole(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.SetGenericHumanRoleResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setGenericHumanRole operation
           */
            public void receiveErrorsetGenericHumanRole(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for fail method
            * override this method for handling normal response from fail operation
            */
           public void receiveResultfail(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.FailResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from fail operation
           */
            public void receiveErrorfail(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setOutput method
            * override this method for handling normal response from setOutput operation
            */
           public void receiveResultsetOutput(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.SetOutputResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setOutput operation
           */
            public void receiveErrorsetOutput(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteOutput method
            * override this method for handling normal response from deleteOutput operation
            */
           public void receiveResultdeleteOutput(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.DeleteOutputResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteOutput operation
           */
            public void receiveErrordeleteOutput(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for claim method
            * override this method for handling normal response from claim operation
            */
           public void receiveResultclaim(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.ClaimResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from claim operation
           */
            public void receiveErrorclaim(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for start method
            * override this method for handling normal response from start operation
            */
           public void receiveResultstart(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.StartResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from start operation
           */
            public void receiveErrorstart(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for query method
            * override this method for handling normal response from query operation
            */
           public void receiveResultquery(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.QueryResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from query operation
           */
            public void receiveErrorquery(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getRenderingTypes method
            * override this method for handling normal response from getRenderingTypes operation
            */
           public void receiveResultgetRenderingTypes(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetRenderingTypesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getRenderingTypes operation
           */
            public void receiveErrorgetRenderingTypes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAttachmentInfos method
            * override this method for handling normal response from getAttachmentInfos operation
            */
           public void receiveResultgetAttachmentInfos(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetAttachmentInfosResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAttachmentInfos operation
           */
            public void receiveErrorgetAttachmentInfos(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for complete method
            * override this method for handling normal response from complete operation
            */
           public void receiveResultcomplete(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.CompleteResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from complete operation
           */
            public void receiveErrorcomplete(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for nominate method
            * override this method for handling normal response from nominate operation
            */
           public void receiveResultnominate(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.NominateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from nominate operation
           */
            public void receiveErrornominate(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getInput method
            * override this method for handling normal response from getInput operation
            */
           public void receiveResultgetInput(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetInputResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getInput operation
           */
            public void receiveErrorgetInput(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setPriority method
            * override this method for handling normal response from setPriority operation
            */
           public void receiveResultsetPriority(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.SetPriorityResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setPriority operation
           */
            public void receiveErrorsetPriority(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for suspendUntil method
            * override this method for handling normal response from suspendUntil operation
            */
           public void receiveResultsuspendUntil(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.SuspendUntilResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from suspendUntil operation
           */
            public void receiveErrorsuspendUntil(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for release method
            * override this method for handling normal response from release operation
            */
           public void receiveResultrelease(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.ReleaseResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from release operation
           */
            public void receiveErrorrelease(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for remove method
            * override this method for handling normal response from remove operation
            */
           public void receiveResultremove(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.RemoveResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from remove operation
           */
            public void receiveErrorremove(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for create method
            * override this method for handling normal response from create operation
            */
           public void receiveResultcreate(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.CreateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from create operation
           */
            public void receiveErrorcreate(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteAttachments method
            * override this method for handling normal response from deleteAttachments operation
            */
           public void receiveResultdeleteAttachments(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.DeleteAttachmentsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteAttachments operation
           */
            public void receiveErrordeleteAttachments(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for skip method
            * override this method for handling normal response from skip operation
            */
           public void receiveResultskip(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.SkipResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from skip operation
           */
            public void receiveErrorskip(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for setFault method
            * override this method for handling normal response from setFault operation
            */
           public void receiveResultsetFault(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.SetFaultResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from setFault operation
           */
            public void receiveErrorsetFault(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getMyTaskAbstracts method
            * override this method for handling normal response from getMyTaskAbstracts operation
            */
           public void receiveResultgetMyTaskAbstracts(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetMyTaskAbstractsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getMyTaskAbstracts operation
           */
            public void receiveErrorgetMyTaskAbstracts(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getRendering method
            * override this method for handling normal response from getRendering operation
            */
           public void receiveResultgetRendering(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetRenderingResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getRendering operation
           */
            public void receiveErrorgetRendering(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getOutput method
            * override this method for handling normal response from getOutput operation
            */
           public void receiveResultgetOutput(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetOutputResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getOutput operation
           */
            public void receiveErrorgetOutput(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteFault method
            * override this method for handling normal response from deleteFault operation
            */
           public void receiveResultdeleteFault(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.DeleteFaultResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteFault operation
           */
            public void receiveErrordeleteFault(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getMyTasks method
            * override this method for handling normal response from getMyTasks operation
            */
           public void receiveResultgetMyTasks(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetMyTasksResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getMyTasks operation
           */
            public void receiveErrorgetMyTasks(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getFault method
            * override this method for handling normal response from getFault operation
            */
           public void receiveResultgetFault(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.GetFaultResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getFault operation
           */
            public void receiveErrorgetFault(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for addComment method
            * override this method for handling normal response from addComment operation
            */
           public void receiveResultaddComment(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.AddCommentResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from addComment operation
           */
            public void receiveErroraddComment(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for resume method
            * override this method for handling normal response from resume operation
            */
           public void receiveResultresume(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.ResumeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from resume operation
           */
            public void receiveErrorresume(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for suspend method
            * override this method for handling normal response from suspend operation
            */
           public void receiveResultsuspend(
                    com.intalio.www.ws_ht.api.wsdl.HumanTaskOperationServicesStub.SuspendResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from suspend operation
           */
            public void receiveErrorsuspend(java.lang.Exception e) {
            }
                


    }
    