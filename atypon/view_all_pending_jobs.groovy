([] + Jenkins.instance + Jenkins.instance.nodes ).each { 
  println it.displayName 
  println it.toComputer()?.workspaceList?.inUse 
} 
return
  
