/*
 * File picker namespace.
 */
var fp = {};

fp.fileStoreHelper = {}
fp.currentDirectory = "/";

/*
 * Files store.
 */
var FilesStore = new Ext.data.JsonStore({
	fields : [ "path", "volume" ],
	proxy : {
		type : "ajax",
		url : "/non-existent-fake-url",
		reader : {
			type : "json",
			root : "files",
			totalProperty : "totalCount"
		}
	}
});

/*
 * Projects store
 */
fp.ProjectsStore = new Ext.data.JsonStore({
	autoLoad : true,
	fields : [ "name" ],
	proxy : {
		type : "ajax",
		url : "/list",
		reader : {
			root : "scenarios"
		}
	}
});

var FilepathFilter = new Ext.form.field.Text({
	fieldLabel : "Filter",
	listeners : {
		change : function(textfield, value) {
			FilesStore.load({
				params : {
					path : value
				}
			});
		}
	}
});

var FilesGridPager = new Ext.PagingToolbar({
	store : FilesStore,
	displayInfo : true,
	displayMsg : "Displaying files {0} - {1} of {2}",
	emptyMsg : "No files to display",
	listeners : {
		change : function(pager, data) {

			if (data == undefined) {
				return
			}

			var label = FilePicker.getField().fieldLabel
			if (! (label in fp.fileStoreHelper)) {
				fp.fileStoreHelper[label] = data.pageCount
			} else if (fp.fileStoreHelper[label] != data.pageCount) {
				fp.fileStoreHelper[label] = data.pageCount
			}
		}
	}
})

fp.FilesGrid = Ext.create("Ext.grid.Panel", {
	header : false,
	loadMask : true,
	store : FilesStore,
	columns : [
	new Ext.grid.RowNumberer(),
	{
		flex : true,
		text : "path",
		dataIndex : "path",
		renderer: function(x, meta, record) {
			if (record.raw.directory) {
				return record.get("path") + "/";
			} else {
				return record.get("path");
			}
		}
	}, {
		text : "size",
		dataIndex : "volume"
	} ],
	listeners : {
		beforeitemdblclick : function(grid, record) {
			/*
			 * Record from database.
			 */
			if (record.raw.fileId) {
				FilePicker.close(record.get("path"));
			/*
			 * Directory from file system.
			 */
			} else if (record.raw.directory) {
				fp.currentDirectory = record.get("path")
				console.log(fp.currentDirectory)
				FilesStore.load({params : {
					directory : fp.currentDirectory
				}});
			/*
			 * File from file system.
			 */
			} else {
				FilePicker.close(record.get("path"));
			}
		}
	},
	bbar : FilesGridPager,
	dockedItems : [ {
		xtype : "toolbar",
		items : [ FilepathFilter ]
	} ]
});

var FilePicker = new function() {

	this.field;
	this.window = null;

	this.getField = function() {
		return this.field;
	}

	this.getWindow = function() {
		if (this.window == null) {
			this.window = new Ext.Window({
				layout : "fit",
				width : Ext.getBody().getWidth() * 0.8,
				height : Ext.getBody().getHeight() * 0.8,
				closeAction : "hide",
				plain : true,
				items : [ fp.FilesGrid ],
				listeners : {
					close : function() {

					}
				}
			});
		}

		return this.window;
	}

	this.show = function(field) {

		this.field = field;

		FilesStore.setProxy(PropertyUtil.getProxy(field.fieldLabel));

		var label = field.fieldLabel
		if (label in fp.fileStoreHelper) {
			if (FilesStore.currentPage > fp.fileStoreHelper[label]) {
				FilesStore.loadPage(fp.fileStoreHelper[label], { params: {
					path : FilepathFilter.getValue()
				}});
			} else {
				FilesStore.load({ params : {
					path : FilepathFilter.getValue()
				}});
			}
		} else {
			FilesStore.loadPage(1, { params: {
				path : FilepathFilter.getValue()
			}});
		}

		this.getWindow().show();
	};

	this.close = function(value) {
		this.getWindow().hide();
		this.field.setValue(value);
	}
};