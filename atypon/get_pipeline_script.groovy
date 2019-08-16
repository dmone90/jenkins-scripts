#! groovy

// Given a list of jobs or job names find out their github repo and gdsl file.
// Then retrieve the content of that file using java GithubAPI library
// and search for occurences of `node('cisc')` `agent 'cisc'`  
import org.kohsuke.github.*
import org.jenkinsci.plugins.workflow.job.*

G = null

try {
    G = GitHub.connect('atyponci', AUTH_TOKEN)
    println 'Connected!'
} catch (java.io.IOException e) {
    println 'Cannot establish connection to github'
    return -5
}

/**
 * Find the gdsl script and the repo for <code>jobName</code>.
 * @param jobName the full name of the pipeline job i.e 1910/Unittest_Gradle, id-1910/atm-it-1910
 * @return a hashmap with attributes:
 *    name: jobName
 *    script: 'gdsl script path'
 *    repo: 'git repository in the form <owner|org>/<reponame>'
 */ 
def get_pipeline_job_info(String jobName) {
    if (jobName == null) return -1
    j = Jenkins.instance.getItemByFullName(jobName)
    get_pipeline_job_info(j)
}

/**
 * Find the gdsl script and the repo for <code>jobName</code>.
 * @param job a org.jenkinsci.plugins.workflow.job.WorkflowJob instance.
 * @return a hashmap with attributes:
 *    name: jobName
 *    script: 'gdsl script path'
 *    repo: 'git repository in the form <owner|org>/<reponame>'
 *    label: the label used by the gdsl script
 */ 
def get_pipeline_job_info(WorkflowJob job) {
    if (job == null) return -1
    def jobInfo = [:]
    jobName = job.getFullName()

    // FYI 
    // job.getDefinition().class == ' org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition'

    // get the gdsl file path
    /* String */ spath = job.getDefinition().getScriptPath()
    
    // get the repository that the above gdsl resides
    /* hudson.plugins.git.GitSCM */ scm = job.getDefinition().getScm()

    // get a list of repositories configured in the job
    /* List<org.eclipse.jgit.transport.RemoteConfig> */ repos = scm.getRepositories()
    
    r = ''
    // 99% we will have 1 repository in the job configuration
    if (repos.size == 1) {
        repo = repos.get(0).getURIs().get(0).toString();
        if (repo.startsWith('git@')) {
            // git@github.com:atypon/cms-ps-wiley.git
            r = repo.split(':')[1].split('\\.')[0]
        } else if (repo.startsWith('https://')) {
            // https://github.com/atypon/cms-ps-wiley
            r = repo.split('/')[2..3].join('/')
        }
    }
    
    /* GHRepository */ repo = G.getRepository(r)
    /* InputStream */ is = repo.getFileContent(spath).read()
    dslContent = is.getText('UTF-8')
    node_pattern = /node\s*\(["'](\w*)["']\)/
    agent_pattern = /agent\s*\{\s*label\s*["'](\w*)["'] \}/

    // try matching node_pattern first
    job_label = dslContent.findAll(node_pattern) { match, word -> return word }
    // if nothing found try the agent_pattern
    if (job_label == []) {
        job_label = dslContent.findAll(agent_pattern) { match, word -> return word }
    }

    jobInfo << [name: jobName]
    jobInfo << [script: spath]
    jobInfo << [repo: r]
    jobInfo << [label: job_label]
    return jobInfo
}


// Examples:
// def jobs = ['1910/Unittest_Gradle', '1910/All_Product_1910']
// for (j in jobs) {
//     jobs << get_pipeline_job_info(j)
// }



