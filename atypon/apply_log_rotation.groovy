// Apply discard policy manually for a given job name
import hudson.tasks.LogRotator
def thejob = Jenkins.instance.getItemByFullName('<job-name>')
// https://javadoc.jenkins.io/hudson/tasks/LogRotator.html#LogRotator-int-int-int-int
def lastFiveJobsRotator = new LogRotator(5, 5, 0, 0)
lastFiveJobsRotator.perform(thejob)
