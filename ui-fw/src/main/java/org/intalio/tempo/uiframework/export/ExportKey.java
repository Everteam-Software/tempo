package org.intalio.tempo.uiframework.export;

/**** grids.jps section related to PA fields*/
/**
 * {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_update"/>',
 * name : '_update', width : width*0.015, sortable : false, align: 'center'},
 * {display: '<fmt:message
 * key="com_intalio_bpms_workflow_taskHolder_aircraft"/>', name : '_aircraft',
 * width : width*0.020, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_afn"/>', name :
 * '_afn', width : width*0.025, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_sad"/>', name :
 * '_sad', width : width*0.040, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_sta"/>', name :
 * '_sta', width : width*0.025, sortable : false, align: 'center'}, // {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_eta"/>', name :
 * '_eta', width : width*0.025, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_ata"/>', name :
 * '_ata', width : width*0.035, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_type"/>', name :
 * '_type', width : width*0.025, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_dfn"/>', name :
 * '_dfn', width : width*0.025, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_sdd"/>', name :
 * '_sdd', width : width*0.040, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_std"/>', name :
 * '_std', width : width*0.025, sortable : false, align: 'center'}, // {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_etd"/>', name :
 * '_etd', width : width*0.025, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_atd"/>', name :
 * '_atd', width : width*0.035, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_stand"/>', name :
 * '_stand', width : width*0.020, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_coord"/>', name :
 * '_coord', width : width*0.045, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_mechanics"/>', name :
 * '_mechanics', width : width*0.05, sortable : false, align: 'center'},
 * {display: '<fmt:message
 * key="com_intalio_bpms_workflow_taskHolder_avionics"/>', name : '_avionics',
 * width : width*0.05, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_hil"/>', name :
 * '_hil', width : width*0.040, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_rtr"/>', name :
 * '_rtr', width : width*0.040, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_comments"/>', name :
 * '_comments', width : width*0.040, sortable : false, align: 'center'},
 * {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_state"/>',
 * name : '_state', width : width*0.030, sortable : false, align: 'center'},
 * {display: '<fmt:message
 * key="com_intalio_bpms_workflow_taskHolder_resources"/>', name : '_resources',
 * width : width*0.050, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_start"/>', name :
 * '_start', width : width*0.025, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_end"/>', name :
 * '_end', width : width*0.025, sortable : false, align: 'center'}, {display:
 * '<fmt:message key="com_intalio_bpms_workflow_taskHolder_release"/>', name :
 * '_release', width : width*0.025, sortable : false, align: 'center'},
 * {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_late"/>',
 * name : '_late', width : width*0.025, sortable : false, align: 'center'}
 */
public class ExportKey {

	public enum ExportSITAKey {

		UPDATE("Update"), TAIL_NUMBER("Tail Number"), ARR_FL("Arriving flight"), ARR_SCH_DATE(
				"Scheduled arrival date"), STA("Scheduled arrival time"), ETA_ATA(
				"Actual Arrival"), INS("Inspection type"), DEP_FL(
				"Departure flight"), DEP_SCH_DATE("Scheduled departure date"), STD(
				"Scheduled departure time"), ETD_ATD("Actual departure time"), STAND(
				"Stand"), COORD("Coordinators"), MECHANICS("Mechanics"), AVIONICS(
				"Avionics"), HIL("HIL"), RTR("RTR"), COMMENTS("Comments"), STATE(
				"State"), HELP_REQUEST("Help request"), START("Start time"), END(
				"End time"), RELEASE("Release time"), LATE("Late");
		String keyname;
private ExportSITAKey(String string) {
	keyname=string;
}
		public String ExportSITAKey() {
			return keyname;
		}
	}

	public enum ExportGLobalKey {
		
		CREATION_DATE ("Creation date"),
		DESCRIPTION ("Description"),
		ID ("Task ID"),
		FORM_URL ("Form URL"),
		PRIORITY ("Priority"),
		STATE ("Task status"),
		PROCESS_ID ("Process ID"),
		PROCESS_ENDPOINT("Process Endpoint"),
		INIT_MESSAGE_NS ("Init Message NS"),
		INIT_OPERATION_ACTION ("Init OperationAction"),
		COMPLETE_URL ("Complete URL"),
		DEADLINE ("Deadline");
		
		
		String keyname;
		private ExportGLobalKey(String string) {
			keyname=string ;
		}
		public String ExportGlobalKey() {
			return keyname;
		}
		
	}

}
