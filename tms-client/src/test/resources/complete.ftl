<b4p:completeTaskRequest xmlns:b4p="http://www.intalio.com/bpms/workflow/ib4p_20051115">
<b4p:taskMetaData><b4p:taskId>${taskId}</b4p:taskId></b4p:taskMetaData>
<b4p:participantToken>${token}</b4p:participantToken>
<b4p:user>${user}</b4p:user>
<b4p:taskOutput>
<output xmlns="http://www.intalio.com/workflow/forms/AbsenceRequest/AbsenceRequest">
<approved>${approved}</approved>
<comment>${comment}</comment>
<contactWhileAway>
			<name>${contact.name}</name>
			<phone>${contact.phone}</phone>
			<email>${contact.email}</email>
</contactWhileAway>
</output>
</b4p:taskOutput>
</b4p:completeTaskRequest>