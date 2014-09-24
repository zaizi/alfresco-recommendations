<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   var nodeRef=AlfrescoUtil.param('nodeRef');
   var metadata=AlfrescoUtil.getNodeDetails(nodeRef);
   model.nodeRef=nodeRef;
   model.metadata=metadata;
}

main();