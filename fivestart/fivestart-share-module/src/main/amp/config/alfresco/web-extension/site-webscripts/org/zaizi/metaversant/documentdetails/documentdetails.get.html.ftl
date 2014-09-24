<#if metadata.item.node.aspects?seq_contains("mr:rateable")>
	<@markup id="js">
	   <#-- JavaScript Dependencies -->
	   <@script src="${url.context}/res/extension/js/metaversant-ratings.js" group="document-details"/>
	</@>
	<@markup id="css" >
	   <#-- CSS Dependencies -->
	   <@link rel="stylesheet" type="text/css" href="${url.context}/res/extension/css/metaversant-ratings.css" group="document-details"/>
	</@>
	<script>
		var mratings = new Metaversant.Ratings('${args.htmlid}').setOptions({
			targetUrl: "${url.context}/proxy/alfresco/api/node/workspace/SpacesStore/{id}/mratings",
			fireMetadataRefresh: true
		});
	</script>
		<div>
		   	<div>
				<div class="document-metadata-header document-details-panel">
					<h2 id="${args.htmlid}-heading" class="thin dark">
					   	${msg("heading")}
					</h2>
		        	<div style="display: block;">
		            	<div class="form-container">
							<div class="form-field">
	      						<div class="viewmode-field">
	         						<span class="viewmode-label">${msg('prop.mr_averageRating')}:</span>
	         						<span class="viewmode-value"><div id="${args.htmlid}_${nodeRef?replace('workspace://SpacesStore/','')}" class="rating" style="display:inline-block"><#if !(metadata.item.node.properties["mr:averageRating"]??) || metadata.item.node.properties["mr:averageRating"]?string == ''>0<#else>${metadata.item.node.properties["mr:averageRating"]}</#if></div></span>
	      						</div>
							</div>
							<div class="form-field">
	      						<div class="viewmode-field">
	         						<span class="viewmode-label">${msg('prop.mr_ratingCount')}:</span>
	         						<span class="viewmode-value"><#if !(metadata.item.node.properties["mr:ratingCount"]??) || metadata.item.node.properties["mr:ratingCount"]?string == ''>0<#else>${metadata.item.node.properties["mr:ratingCount"]}</#if></span>
	      						</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
</#if>