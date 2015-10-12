/*
 * Jsub namespace.
 */
var jsub = {};
jsub.form = {};
jsub.form.properties = {};
jsub.form.properties.blankRef = "&nbsp;";
jsub.form.properties.counter  = 0;

jsub.clearList = new Array();
var conveyorTypesCombobox = Ext.create('Ext.form.ComboBox', {
	fieldLabel : 'Type',
	name : "type",
	editable : false,
	store : fp.ProjectsStore,
	queryMode : 'local',
	displayField : "name",
	valueField : "name",
	listeners : {
		select : {
			fn : function(combobox) {
				Ext.Ajax.request({
					url : '/configure',
					params : {
						type : combobox.getValue()
					},
					success : function(response) {
						var text = response.responseText;
						var json = Ext.JSON.decode(text);
						jsub.clearList = json.clearList;

						Properties.removeAll(false);
						Properties.add({
							xtype : "checkbox",
							fieldLabel : "use run",
							checked : false,
							name : "useRun",
							uncheckedValue : "off",
							listeners : {
								change : function() {
									if (RunsCombobox.isDisabled()) {
										RunsCombobox.setDisabled(false);
									} else {
										RunsCombobox.setDisabled(true);
									}
								}
							}
						});
						Properties.add(RunsCombobox);

						/*
						 * Create "refs" combobox.
						 */
						var store = new Array({"ref" : jsub.form.properties.blankRef});
						for (var i in json.tags) {
							store.push({
								"ref" : i
							});
						}

						Properties.add(Ext.create('Ext.form.ComboBox', {
							fieldLabel : "refs",
							allowSingle : false,
							editable : false,
							displayField : "ref",
							valueField : "ref",
							store : {
								fields : [ "ref" ],
								data : store
							}, listeners : {
								change : function(combo, newValue, oldValue, eOpts) {
									if (newValue == "" || newValue == jsub.form.properties.blankRef) {
										combo.setValue("");
										return;
									}

									for (item in Properties.items.items) {
										var label = Properties.items.items[item].fieldLabel;
										if (label && label != "refs" && label in json.tags[newValue]) {
											Properties.items.items[item].setValue(json.tags[newValue][label]);
										}
									}
								}
							}
						}));

						jsub.form.properties.counter = 0;
						for (var i in json.properties) {
							var property = json.properties[i];
							if (/,/.test(property.value)) {
								store = new Array();
								var values = property.value.split(",");
								for (k in values) {
									store[k] = {
										"value" : values[k]
									};
								}
								var combo = new Ext.form.ComboBox({
									id : property.name,
									fieldLabel : property.name,
									name : "property[" + jsub.form.properties.counter + "]",
									valueField : "value",
									displayField : "value",
									store : {
										fields : [ "value" ],
										data : store
									}
								});

								Properties.add(combo);

								Properties.add(new Ext.form.field.Hidden({
									value : property.name,
									name : "property-name[" + jsub.form.properties.counter + "]"
								}));
							} else {
								var text = new Ext.form.Text({
									id : property.name,
									fieldLabel : property.name,
									value : property.value,
									name : "property[" + jsub.form.properties.counter + "]"
								});

								if (! PropertyUtil.isItemIdFile(property)) {
									text.on('render', function(c) {
										c.getEl().on('dblclick', function() {
											FilePicker.show(c)
										});
									});
								}

								Properties.add(text);

								Properties.add(new Ext.form.field.Hidden({
									value : property.name,
									name : "property-name[" + jsub.form.properties.counter + "]"
								}));
							}

							jsub.form.properties.counter ++;
						}

						TargetList.removeAll();
						ClearList.removeAll();

						for (i in json.jobs) {
							var job = json.jobs[i];
							TargetList.add({
								id : "target-" + job.name,
								xtype : "checkbox",
								fieldLabel : job.name,
								checked : true,
								name : "target-list[" + job.name + "]",
								uncheckedValue : "off",
								listeners : {
									change : function() {

//										var viewport = Ext.getCmp("page-viewport");
										Properties.setLoading(true);

										var targets = new Array()

										isPrevChecked = true;
										TargetList.items.each(function(checkbox, index) {
											var isChecked = checkbox.getValue();
											if (isChecked == true && isPrevChecked == false) {
												targets.push(checkbox.getFieldLabel())
											}

											isPrevChecked = isChecked;
										})

										var defaultProperties = ["use run", "runs", "refs"]
										var currentProperties = []
										for (item in Properties.items.items) {
											var label = Properties.items.items[item].fieldLabel;
											if (label != undefined && defaultProperties.indexOf(label) < 0) {
												currentProperties.push(label)
											}
										}

										Ext.Ajax.request({
											url : "/config/update",
											async : false,
											params : {
												type : conveyorTypesCombobox.getValue(),
												targets : targets
											},
											success : function(response, a, b) {

												var data = Ext.JSON.decode(response.responseText);
												var newProperties = data.newProperties
												var defaultProperties = data.defaultProperties

												/*
												 * Remove unused properties.
												 */
												var responseProperties = newProperties.concat(defaultProperties)
												for (i in currentProperties) {
													var property = currentProperties[i];
													if (responseProperties.indexOf(property) < 0) {
														var field = jsub.form.panel.getForm().findField(property);
														Properties.remove(field)
														var field = jsub.form.panel.getForm().findField("hidden-" + property);
														Properties.remove(field)
													}
												}

												/*
												 * Add new properties.
												 */
												for (i in newProperties) {
													var property = newProperties[i]
													if (currentProperties.indexOf(property) >= 0) {
														continue;
													}

													var text = new Ext.form.Text({
														id : property,
														allowBlank: false,
														fieldLabel : property,
														name : "property[" + jsub.form.properties.counter + "]"
													});

													if (! PropertyUtil.isItemIdFile(property)) {
														text.on("render", function(c) {
															c.getEl().on("dblclick", function() {
																FilePicker.show(c)
															});
														});
													}

													Properties.add(text);

													Properties.add(new Ext.form.field.Hidden({
														id : "hidden-" + property,
														value : property,
														name : "property-name[" + jsub.form.properties.counter + "]"
													}));

													jsub.form.properties.counter ++;
												}

											},
											failure : function() {

											}
										})

										Properties.setLoading(false);
									}
								}
							});
							ClearList.add({
								id : "clear-" + job.name,
								xtype : "checkbox",
								fieldLabel : job.name,
								checked : jsub.clearList.indexOf(job.name) >= 0,
								name : "clear-list[" + job.name + "]"
							});
						}
					}
				});
			}
		}
	}
});

var TagsCombobox = Ext.create('Ext.form.ComboBox', {
	fieldLabel : "Tag",
	name : "tag",
	allowSingle : false,
	editable : true,
	displayField : "title",
	valueField : "title",
	store : new Ext.data.ArrayStore({
		fields : [ {
			name : "title",
			convert : function(v, record) {
				return record.raw
			}
		} ],
		autoLoad : true,
		proxy : {
			type : 'ajax',
			url : '/tags',
			reader : {
				type : 'json',
				root : 'tags'
			}
		}
	})
});

var TargetList = new Ext.form.CheckboxGroup({
	layout : {
		type : 'vbox',
		align : 'left'
	}
});

var ClearList = new Ext.form.CheckboxGroup({
	layout : {
		type : 'vbox',
		align : 'left'
	}
});


RunsStore = Ext.create('Ext.data.Store', {
	model : "Runs",
	fields : ["runId", 'name', 'description', "sequencerId", "seqsProject", "sampleId", "createDate"],
	remoteSort : true,
	autoLoad : false,
	pageSize: 10,
	proxy: {
		type : 'ajax',
	    url: '/runs',
	    reader: {
	    	type : 'json',
	        root: 'runs',
	        totalProperty: 'totalCount'
	    }
	}
});


var RunsCombobox = Ext.create('Ext.form.ComboBox', {
	fieldLabel : "runs",
	disabled : true,
	name : "runId",
	editable : true,
	displayField : "name",
	valueField : "runId",
	store : RunsStore,
	pageSize : 10,
	minChars : 2
});

var Properties = new Ext.form.FieldSet({
    title : "Properties",
	defaults : {
		anchor : '100%'
	}
});

var checkFitnessFunction = function() {
	SystemFitnessPanel.setLoading(true);
	Ext.Ajax.request({
		url : '/system',
		success : function(response) {
			var fitness = Ext.JSON.decode(response.responseText).fitness;
			for (entity in fitness) {

				var component = Ext.getCmp(entity);

				if (! component) {
					continue;
				}

				if (fitness[entity]) {
					Ext.getCmp(entity).setBodyStyle("background-color", "#82ed7d");
				} else {
					Ext.getCmp(entity).setBodyStyle("background-color", "#f94b4b");
				}
			}
			SystemFitnessPanel.setLoading(false);
		},
		failure : function() {
			Ext.each(SystemFitnessPanel.query(), function(child) {
				if (child.getXType() == "panel") {
					child.setBodyStyle("background-color", "#f94b4b");
				}
			});
			SystemFitnessPanel.setLoading(false);
		}
	})
}

var SystemFitnessPanel = Ext.create('Ext.form.FieldSet', {
	title : "System fitness",
	defaults : {
		xtype : "panel",
		bodyPadding: 5,
		padding : 1,
		height : 40
	},
	items : [
		{
			id : "bioDbFromTomcat",
			html : '<div style="font-size:200%; color:#15428B">' +
						'<a href="http://redmine.ripcm.com/projects/jsub-wev/wiki/How_to_fix_services_access_problems">bioDbFromTomcat</a>' + 
					'</div>',
		}, {
			id : "bioDbFromNodes",
			html : '<div style="font-size:200%; color:#15428B">' +
						'<a href="http://redmine.ripcm.com/projects/jsub-wev/wiki/How_to_fix_services_access_problems#bioDb">bioDbFromNodes</a>' + 
					'</div>',
		}, {
			id : "OGE",
			html : '<div style="font-size:200%; color:#15428B">' + 
						'<a href="http://redmine.ripcm.com/projects/jsub-wev/wiki/How_to_fix_services_access_problems#OGE">OGE</a>' +
					'</div>',
		},
//		{
//			id : "activeMQ",
//			html : '<div style="font-size:200%; color:#15428B">' + 
//						'<a href="http://redmine.ripcm.com/projects/jsub-wev/wiki/How_to_fix_services_access_problems#activeMQ">activeMQ</a>' +
//					'</div>',
//		}, {
//			id : "conveyorService",
//			html : '<div style="font-size:200%; color:#15428B">' +
//						'<a href="http://redmine.ripcm.com/projects/jsub-wev/wiki/How_to_fix_services_access_problems#conveyorService">conveyorService</a>' +
//					'</div>',
//		},
		{
			xtype : "button",
			text: "check again",
			height : 30,
		    margin : "5 0 0 0",
		    handler: checkFitnessFunction
		}
	],
	listeners : {
		afterrender : checkFitnessFunction
	}
})

//var form = Ext.create('Ext.form.Panel', {
jsub.form.panel = Ext.create('Ext.form.Panel', {
	timeout : 300,
	bodyPadding : 5,
	width : 350,
	url : '/execute',
	layout : 'anchor',
	autoScroll : true,
	defaults : {
		anchor : '100%'
	},
	defaultType : 'textfield',
	items : [ 
	    SystemFitnessPanel, {
		xtype : 'textfield',
		name : 'name',
		fieldLabel : 'Name',
		allowBlank : false
	}, conveyorTypesCombobox, TagsCombobox, {
		xtype : "checkbox",
		name : "force",
		fieldLabel : "Override existent project"
	}, Properties, {
		xtype : "panel",
		layout : "hbox",
		border : false,
		items : [ {
			xtype : "fieldset",
			title : "Targets",
			flex : 1,
			items : [ TargetList ]
		}, {
			xtype : "fieldset",
			title : "Clear input list",
			flex : 1,
			items : [ ClearList ]
		} ]
	} ],
	buttons : [ {
		text : 'Submit',
		formBind : true,
		disabled : true,
		handler : function() {

			var viewport = Ext.getCmp("page-viewport");
			viewport.setLoading(true);

			var form = this.up('form').getForm();
			if (form.isValid()) {

				form.submit({
					params : {
						startPahse : TargetList.getChecked()[0].getFieldLabel()
					},
					success : function(form, action) {
						viewport.setLoading(false);
						Ext.Msg.alert('Success', action.result.msg);
					},
					failure : function(form, action) {
						viewport.setLoading(false);
						Ext.Msg.alert('Failed', action.result.msg);
					}
				});
			}
		}
	} ],
});

Ext.application({
	name : 'HelloExt',
	launch : function() {
		Ext.create('Ext.container.Viewport', {
			id : "page-viewport",
			layout : 'fit',
			items : [ {
				xtype : "tabpanel",
				activeTab : 1,
				defaults : {
					layout : "fit"
				},
				items : [ {
					title : "Process list",
					items : [ ProcessPanel ],
					listeners : {
						activate : function() {
							uploadProcessList();
							process.poller = setInterval(uploadProcessList, 5000);
						},
						deactivate : function() {
							clearInterval(process.poller)
						}
					}
				}, {
					title : "RunRun",
					items : [ jsub.form.panel ]
				} ]
			} ]
		});
	}
});