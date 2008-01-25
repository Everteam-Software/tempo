--  Copyright (c) 2005-2006 Intalio inc.
--
--  All rights reserved. This program and the accompanying materials
--  are made available under the terms of the Eclipse Public License v1.0
--  which accompanies this distribution, and is available at
--  http://www.eclipse.org/legal/epl-v10.html
--
--  Contributors:
--  Intalio inc. - initial API and implementation

connect 'jdbc:derby://localhost/BPMSWorkflowDeploymentDB;create=true';

-- Data items
CREATE TABLE items (
	uri 		 VARCHAR (1024) NOT NULL,
	content_type VARCHAR (256) 	NOT NULL,
	data 		 BLOB 			NOT NULL,

	PRIMARY KEY (uri)
);

exit;
