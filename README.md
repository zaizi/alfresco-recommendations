alfresco-recommendations
========================

Installation guide

This project contains three subprojects

- fivestar
- zaizi-mahout-alfresco4
- zaizi-recommendations-dashlet-alfresco4

Fivestar installation

It is a maven project based on alfresco allinone archetype, so you only need to do a mvn clean package to get
the amps files. If you have any doubt please visit http://docs.alfresco.com/4.2/tasks/dev-extensions-maven-sdk-tutorials-all-in-one-archetype.html.


Zaizi-mahout-alfresco4 installation

This project has two modules:

- zaizi-mahout-core
- zaizi-alfresco-mahout (depends on zaizi-mahout-core)

Install zaizi-mahout-core before, because it is a dependecy of the zaizi-alfresco-mahout project.
To install it execute mvn clean isntall.

After that you should install the other module named zaizi-alfresco-mahout. Executing mvn clean isntall.


Zaizi-recommendations-dashlet-alfresco4 installation

This project has two modules:

- recommendations-dashlet-repository (depends on zaizi-alfresco-mahout)
- recommendations-dashlet-share

Execute mvn clean package in the root folder to get both amp files.

Finally you will have the next amp files to install in you Alfresco instance:

Alfresco Amps

- fivestart_alfresco_module.amp
- recommendations-dashlet-repository-1.0.amp

Share Amps

- fivestart_share_module.amp
- recommendations-dashlet-share-1.0.amp


