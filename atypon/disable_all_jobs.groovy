import hudson.model.*

def disableChildren(items) {
  for (item in items) {
  if (item.class.canonicalName == 'com.cloudbees.hudson.plugins.folder.Folder') {
    disableChildren(((com.cloudbees.hudson.plugins.folder.Folder) item).getItems())
  } else {
    try{
      item.disabled=true
      item.save()
      println(item.name)
    } catch(err) {}
   }
  }
 }
// OR
//Jenkins.instance.getAllItems(org.jenkinsci.plugins.workflow.job.WorkflowJob.class).each {i -> i.setDisabled(true); i.save() }
//Jenkins.instance.getAllItems(hudson.model.AbstractProject.class).each {i -> i.setDisabled(true); i.save() }
// disable by view
// jenkins.instance.getView("View Name").items*.disabled = true
