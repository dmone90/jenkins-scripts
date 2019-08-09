Jenkins.instance.getItemByFullName("JOBNAME").getBuildByNumber(5).finish(hudson.model.Result.ABORTED, new java.io.IOException("Aborting build")); 

