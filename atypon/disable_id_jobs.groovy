Jenkins.instance.getView("Information Discovery").items.each{ j ->
  if (j.class.canonicalName == 'com.cloudbees.hudson.plugins.folder.Folder') {
  	j.getItems().each { i ->
     	i.disabled = true
  	}	
  } else {
  	j.disabled = true
  }
}