/**
 * Copyright (c) 2005-2008 Intalio inc.
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