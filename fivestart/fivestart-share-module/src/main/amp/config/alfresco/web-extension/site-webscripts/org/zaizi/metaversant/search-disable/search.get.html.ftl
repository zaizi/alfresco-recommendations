<#--
<@markup id="js">
   <@script src="${url.context}/res/extension/js/metaversant-ratings.js" group="search"/>
   <@script src="${url.context}/res/extension/js/metaversant-search.js" group="search"/>
</@>

<script type="text/javascript">//<![CDATA[
	var mratings = new Metaversant.Ratings("${args.htmlid}_mrating").setOptions({
		targetUrl: "${url.context}/proxy/alfresco/api/node/workspace/SpacesStore/{id}/mratings"
	});
//]]></script>
-->