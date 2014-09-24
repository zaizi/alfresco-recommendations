<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/extension/js/metaversant-ratings.js" group="documentlibrary"/>
   <@script src="${url.context}/res/components/documentlibrary/fivestar-render.js" group="documentlibrary"/>
</@>
<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/extension/css/metaversant-ratings.css" group="documentlibrary"/>
</@>
<script type="text/javascript">//<![CDATA[
	var mratings = new Metaversant.Ratings("${args.htmlid}_mrating").setOptions({
		targetUrl: "${url.context}/proxy/alfresco/api/node/workspace/SpacesStore/{id}/mratings",
		readOnly: false
	});
//]]></script>