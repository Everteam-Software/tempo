/**
 * Copyright (c) 2005-2006 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: $
 * $Log:$
 */
function submitAction(action) {
	actionNameObj = document.getElementById('actionName').value = action;
	document.getElementById('form').submit();
}

function submitActionToURL(url, actionName) {
	formObj = document.getElementById('form');
	formObj.action = url;
	document.getElementById('actionName').value = actionName;
	formObj.submit();
}

function changetab(obj,tab_container,tab_name){
	/**
	*	Hide all tabs show clicked
	*/
	
	var tabcontainer = document.getElementById(tab_container);	
	var tablerow = obj.parentNode;
	
	// Activeate tab item
	for (i=0; i < tablerow.childNodes.length; i++){
		if (tablerow.childNodes[i].id == 'ActiveTab')
			tablerow.childNodes[i].id = 'notActiveTab';
	}
	obj.id = 'ActiveTab';

	// Show tab
	
	for (i=0; i < tabcontainer.childNodes.length; i++){
		a = tabcontainer.childNodes[i].tagName;
		if (a != null){
			if (a.toLowerCase() == 'div'){
				tabcontainer.childNodes[i].style.display = 'none';
				
				if (tabcontainer.childNodes[i].id == tab_name )
					tabcontainer.childNodes[i].style.display = 'block';
			}
		}
	}

}
