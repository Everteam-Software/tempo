<output xmlns="http://www.intalio.com/workflow/forms/AbsenceRequest/AbsenceRequest">
	<request>
		<employee>
			<name>${employee.name}</name>
			<phone>${employee.phone}</phone>
			<email>${employee.email}</email>
		</employee>
		<details>
			<#list requests as request>
			<request>
				<from>${request.from}</from>
				<to>${request.to}</to>
				<type>${request.type}</type>
				<hours>${request.hours}</hours>
			</request>
			</#list>
		</details>
		<contactWhileAway>
			<name>${contact.name}</name>
			<phone>${contact.phone}</phone>
			<email>${contact.email}</email>
		</contactWhileAway>
		<notes>${notes}</notes>
	</request>
</output>
