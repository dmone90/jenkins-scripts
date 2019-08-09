import org.jenkinsci.plugins.scriptsecurity.scripts.*
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.*
def scr_approval = ScriptApproval.get()
scr_approval.approveSignature('method hudson.model.Run getCause java.lang.Class')
scr_approval.save()
