var process = {};
process.poller = null;


var ProcessesStore = Ext.create("Ext.data.Store", {
	fields : [ "startDate", "stopDate", "currentJob", "projectTag", "projectName", "projectType", "runId", "systemUserLogin", "userEmail" ],
	autoload : true,
	proxy : {
		type : "ajax",
		url : "/process/list",
		reader : {
			type : "json",
			root : "processes",
			totalProperty : "totalCount"
		}
	}
});


var ProcessesFilesStore = Ext.create("Ext.data.Store", {
	fields : [ "name", "size" ],
	proxy : {
		type : "ajax",
		url : "/file/list",
		reader : {
			type : "json",
			root : "files",
			totalProperty : "totalCount"
		}
	}
});


var FilesGrid = Ext.create("Ext.grid.Panel", {
	border: false,
	header : false,
	store : ProcessesFilesStore,
	columns : [
		{
			text : "name",
			dataIndex : "name",
			flex : true
		}, {
			text : "size",
			dataIndex : "size"
		}
	],
	listeners : {
		beforeitemdblclick : function(grid, record, item, index, event) {
			location.href="/file/download?runId=" + Popup.getProcessId() + "&filename=" + record.get("name");
		}
	},
	bbar : Ext.create("Ext.PagingToolbar", {
		store : ProcessesFilesStore,
		displayInfo : true,
		displayMsg : "Displaying files {0} - {1} of {2}",
		emptyMsg : "No files to display"
	})
});


var Popup = new function() {
	this._processId;
	this._window = new Ext.Window({
	    layout      : 'fit',
	    padding     : 10,
	    closeAction :'hide',
	    plain       : true,
	    items: [ FilesGrid ],
	    listeners : {
	    	close: function(window) {
	    		ProcessesFilesStore.removeAll();
	    	}
	    }
	});
	this.show = function(processId) {
		this._processId = processId;
		this._window.setHeight(Ext.getBody().getHeight() * 0.8);
		this._window.setWidth(Ext.getBody().getWidth() * 0.8);
		this._window.show();
	};
	this.getProcessId = function() {
		return this._processId;
	}
}


var FileFactory = {
	getFile : function(name) {
		return new Ext.panel.Panel({
			height: 25,
			border : true,
			html : name
		});
	}
}


var ProcessesGrid = Ext.create("Ext.grid.Panel", {
	border: false,
	header : false,
	loadMask : true,
	store : ProcessesStore,
	columns : [
		{
			xtype: "checkcolumn",
			text : "",
			dataIndex : "stopDate",
			width: 35,
			renderer: function(stopDate, meta, record) {
				if (stopDate) {
					return (new Ext.ux.CheckColumn()).renderer(true);
				} else {
					return (new Ext.ux.CheckColumn()).renderer(false);
				}
			}
		}, {
			text : "id",
			dataIndex : "runId"
		}, {
			text : "type",
			dataIndex : "projectType",
			flex : true
		}, {
			text : "tag",
			dataIndex : "projectTag",
			width : 200
		}, {
			text : "name",
			dataIndex : "projectName",
			width : 200
		}, {
			text : "user",
			dataIndex : "userEmail"
		}, {
			text : "status",
			dataIndex : "currentJob"
		}, {
			text : "start",
			dataIndex : "startDate",
			width : 200,
			renderer: function(startDate) {
				if (startDate) {
					return Ext.Date.format(new Date(startDate), "H:i:s d.m.Y");
				}
			}
		}, {
			text : "stop",
			dataIndex : "stopDate",
			width : 200,
			renderer: function(stopDate) {
				if (stopDate) {
					return Ext.Date.format(new Date(stopDate), "H:i:s d.m.Y");
				}
			}
		} 
	],
	listeners : {
		beforeitemdblclick : function(grid, record, item, index, event) {
			Popup.show(record.get("runId"));
			ProcessesFilesStore.load({
    			params: { runId : record.get("runId") }
    		});
		}
	},
	bbar : Ext.create("Ext.PagingToolbar", {
		store : ProcessesStore,
		displayInfo : true,
		displayMsg : "Displaying references {0} - {1} of {2}",
		emptyMsg : "No references to display"
	})
});


function uploadProcessList() {
	ProcessesStore.load();
}


var ProcessPanel = new Ext.panel.Panel({
	layout : "fit",
	border : false,
	items : [ ProcessesGrid ]
});