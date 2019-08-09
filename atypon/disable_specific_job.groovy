Jenkins.instance.getAllItems().each { it ->
	// cases like 1810/jobname 
	def jobName = it.getDisplayName()
	if (jobName == "CheckBuildStatus") {
		it.disabled = true
	}
}
