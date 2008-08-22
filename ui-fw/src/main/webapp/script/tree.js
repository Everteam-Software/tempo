			
function registerEvent(widgetId, eventName, handler) {
	
	dojo.addOnLoad(function() {
		
		var widget = dojo.widget.manager.getWidgetById(widgetId);
		dojo.event.topic.subscribe(
			widget.eventNames[eventName],
			handler,
			"go"
		);
	});
}
			
function expandNode(nodeId, controllerId) {
	
	if (nodeId != null) {
		this.controller = dojo.widget.manager.getWidgetById(controllerId);
		
		if (!nodeId.isExpanded) {
			controller.expand(nodeId);
		}
	}
}

function showNode(nodeId, controllerId) {
	
	if (nodeId != null) {
		this.controller = dojo.widget.manager.getWidgetById(controllerId);
		
		var parentNode = nodeId.parent;
		
		while (parentNode != nodeId.tree) {
		
			if (!parentNode.isExpanded) {
				controller.expand(parentNode);
			}
			parentNode = parentNode.parent;
		}
	}
}

function selectNode(nodeId, controllerId) {
	
	if (nodeId != null) {
		this.controller = dojo.widget.manager.getWidgetById(controllerId);
		
		if (nodeId.tree.selector.selectedNode) {
			controller.deselect(nodeId.tree.selector.selectedNode);
		}
		
		nodeId.tree.selector.selectedNode = nodeId;
		controller.select(nodeId);
		
		showNode(nodeId, controllerId);
/*		
		var parentNode = nodeId.parent;
		
		while (parentNode != nodeId.tree) {
		
			if (!parentNode.isExpanded) {
				controller.expand(parentNode);
			}
			parentNode = parentNode.parent;
		}
*/		
	}
}

function hideIcon(node) {
	
	if (node.edit) {
		node.edit({childIconSrc: "none"});
	}
	
	for (var i = 0; i < node.children.length; i++) {
		hideIcon(node.children[i]);
	}
}

function hideIcons(treeId) {
	
	var tree = dojo.widget.manager.getWidgetById(treeId);
	
	if (tree) {
		
		for (var i = 0; i < tree.children.length; i++) {
			hideIcon(tree.children[i]);
		}
		tree.updateIconTree();
	}
}
