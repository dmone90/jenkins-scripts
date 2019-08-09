#! groovy
// Delete **all** jobs from a jenkins instance excpet Solr-CI.
// This includes WorkflowJobs, Freestyle jobs as well as Folders
Jenkins.instance.getAllItems().each { it ->
  def jobName = it.getDisplayName()
  if (jobName != "Solr-CI") {
    println "Will delete $jobName - ${it.class}"
    it.delete()
  }
}