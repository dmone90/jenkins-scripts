#! groovy
import jenkins.*
import jenkins.model.*

import hudson.*
import hudson.model.*

import org.jenkinsci.plugins.workflow.job.*

/**
 * Find the jobs under view @viewName
 */
def getJobsByView(String viewName) {
    Jenkins.instance.getView("$viewName")
    return []
}

/**
 * Return the job identified by the given name
 * @return an instance of {@link org.jenkinsci.plugins.workflow.job.WorkflowJob} or {@link hudson.model.FreeStyleProject}
 */
def getJobByName(String jobName) {
    return ''
}

/**
 * 
 */
def hasParemeters(WorkflowJob job) {
    return false
}

/**
 *
 */
def hasParameters(FreeStyleProject job) {
    return false
}


// Given a job name check whether it is parametrized and if so
// get the value of a specific parameter, or all of them (default) 

jobs_names = ['Solr-CI', '1910/All_Products_1910']
jobs_names.each { 
    j = Jenkins.instance.getItemByFullName("${it}")
    if (j.isParameterized()) {
        println "${j.getDisplayName()} is parametrized"
        paramsProp = j.getProperty(ParametersDefinitionProperty.class)
        for(param in paramsProp.getParameterDefinitions()) {
            try {
                println(param.name + " " + param.defaultValue)
            }
            catch(Exception e) {
                println(param.name)
            }
        }
    }
}
