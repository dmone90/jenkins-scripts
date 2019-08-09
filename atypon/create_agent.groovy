import hudson.model.*
import jenkins.model.*
import hudson.slaves.*
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry

import hudson.plugins.sshslaves.verifiers.*

agent_name = "ciath01"
rootfs = "/home/ci/jenkins_agent"
num_executors = 4
node_label = "cisc"
node_usage = Node.Mode.NORMAL

cred_id = "1502e860-197d-4a9d-8b5a-bdceec87dc14" /* ci (Athens Jenkins Slaves CI User) */

SshHostKeyVerificationStrategy hostKeyVerificationStrategy = new KnownHostsFileKeyVerificationStrategy()

// Define a "Launch method": "Launch slave agents via SSH"
ComputerLauncher launcher = new hudson.plugins.sshslaves.SSHLauncher(
        "${agent_name}", // Host
        22, // Port
        "${cred_id}", // Credentials
        (String)"-Djsse.enableSNIExtension=false -Dhudson.tasks.MailSender.SEND_TO_USERS_WITHOUT_READ=true", // JVM Options
        (String)null, // JavaPath
        (String)null, // Prefix Start Slave Command
        (String)null, // Suffix Start Slave Command
        (Integer)30, // Connection Timeout in Seconds
        (Integer)3, // Maximum Number of Retries
        (Integer)3, // The number of seconds to wait between retries
        hostKeyVerificationStrategy // Host Key Verification Strategy
)

// Define a "Permanent Agent"
Slave agent = new DumbSlave(agent_name, rootfs, launcher)
agent.nodeDescription = "CI Agent agent"
agent.numExecutors = num_executors
agent.labelString = node_label
agent.mode = node_usage 
agent.retentionStrategy = new RetentionStrategy.Always()

// Create a "Permanent Agent"
Jenkins.instance.addNode(agent)

return "Node has been created successfully."
