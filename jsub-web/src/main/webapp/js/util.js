var ProxyFactory = {

	getProxy : function(url, root) {
		return new Ext.data.proxy.Ajax({
			url : url,
			reader : {
				type : "json",
				root : root,
				totalProperty : "totalCount"
			}
		})
	}
}


var PropertyUtil = new function() {


	this.isArchiveFile = function(filename) {

		return /.*?\.gz|bz2|rar|zip$/.test(filename);
	}


	this.isItemIdFile = function(property) {

		if (property instanceof Object) {
			property = property.name;
		}

		return property.split(".")[1] == "item-id";
	}


	this.isFile = function(property) {

		if (property instanceof Object) {
			property = property.name;
		}

		return property.split(".").slice(-1)[0] == "file";
	}


	this.getProxy = function(property) {

		var matches;

		if (property == "input.ref-genome.file") {
			return ProxyFactory.getProxy("/genome/list", "files");			
		} else if (property == "input.fai-index.file") {
			return ProxyFactory.getProxy("/fasta/indexes", "files");
		} else if (/.*-index\.file/.test(property)) {
			return ProxyFactory.getProxy("/reference/list", "files");
		} else if (/.*\.index/.test(property)) {
			return ProxyFactory.getProxy("/bowtie/indexes", "files");
		} else if (/.*\.qual\.file/.test(property)) {
			return ProxyFactory.getProxy("/readSet/list?type=qual", "files");
		} else if ((matches = /.*\.(fasta|fastq|fa|fna|csfasta)\.file/.exec(property)) != null) {
			return ProxyFactory.getProxy("/readSet/list?type=" + matches[1], "files");
		} else {
			return ProxyFactory.getProxy("/file/system", "files");
		}
	}
}