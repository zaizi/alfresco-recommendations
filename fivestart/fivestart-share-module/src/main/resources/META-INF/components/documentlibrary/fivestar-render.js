(function()
{
	/**
	 * FiveStart Metadata Renders
	 */
	if (Alfresco.DocumentList)
	{

		/**
		 * Generate "FiveStart" UI
		 * 
		 */
		YAHOO.Bubbling.fire("registerRenderer",
		{
			propertyName : "fivestar",
			renderer : function(record, label)
			{
		        var id = mratings.id + "_" + record.nodeRef.replace("workspace://SpacesStore/", ""),
		        rating = record.fivestar.averageRating;

		        //Render datatable
		        this.widgets.dataTable.subscribe("renderEvent", mratings.onMetadataRefresh, id, mratings);

		        return '<span class="item"><div class="rating-label"><em>Rating:</em></div><div id="' + id + '" class="rating">' + rating + '</div></span>';
			}
		});

	}
})();
