//michaeldkfowler
import jenkins.model.*
Jenkins.instance.getAllItems(AbstractProject.class)
.findAll { it.logRotator }
    .each {
      it.logRotator.perform(it)
    }