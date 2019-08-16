#! groovy

// Given a list of jobs names find out their github repo and gdsl file.
// Then retrieve the content of that file using java GithubAPI library
// and search for occurences of `node('cisc')` `agent 'cisc'`  
import org.kohsuke.github.*

/**
 * Find the gdsl script and the repo for <code>jobName</code>.
 * @return a hashmap with attributes:
 *    name: jobName
 *    script: 'gdsl script path'
 *    repo: 'git repository in the form <owner|org>/<reponame>'
 */ 
def get_job_info(jobName) {
    def jobInfo = [:]

    j = Jenkins.instance.getItemByFullName(jobName)

    // FYI 
    // j.getDefinition().class == ' org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition'

    // get the gdsl file path
    /* String */ spath = j.getDefinition().getScriptPath()
    
    // get the repository that the above gdsl resides
    /* hudson.plugins.git.GitSCM */ scm = j.getDefinition().getScm()

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
    
    jobInfo << [name: jobName]
    jobInfo << [script: spath]
    jobInfo << [repo: r]
    return jobInfo
}


def jobs = []

for (job_Name in workflowJobsNames) {
    jobs << get_job_info(job_Name)
}

// --------------------------------------------------------------------- //

AUTH_TOKEN = '4001191b346bb11b5b0b6a5e6948fe2d292b30fc'
G = GitHub.connect('atyponci', AUTH_TOKEN)
println G.getRateLimit()

jobs.each {
    repo = G.getRepository(it.repo)    
    is = repo.getFileContent(it.script).read()
    dslContent = is.getText('UTF-8')
    node_pattern = /node\s*\(["'](\w*)["']\)/
    agent_pattern = /agent\s*\{\s*label\s*["'](\w*)["'] \}/

    // try matching node_pattern first
    job_label = dslContent.findAll(node_pattern) { match, word -> return word }
    // if nothing found try the agent_pattern
    if (job_label == []) {
        job_label = dslContent.findAll(agent_pattern) { match, word -> return word }
    }
    // add a new property to the jobInfo map
    it.label = job_label
}

println G.getRateLimit()
