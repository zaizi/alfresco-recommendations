/** 
   Copyright 2011, Jeff Potts

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
if (typeof Metaversant == "undefined" || !Metaversant)
{
    var Metaversant = {};
} 

/**
 * Metaversant five star ratings component
 * 
 * @namespace Metaversant
 * @class Metaversant.Ratings
 */
(function()
{
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;

    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML,
        $hasEventInterest = Alfresco.util.hasEventInterest;

    /**
     * Ratings constructor.
     * 
     * @param {String} htmlId The HTML id of the parent element
     * @param {String} currentValueHtmlId The HTML id of the parent element
     * @return {Metaversant.Ratings} The new Ratings instance
     * @constructor
     */
    Metaversant.Ratings = function(htmlId)
    {
        Metaversant.Ratings.superclass.constructor.call(this, "Metaversant.Ratings", htmlId, ["button", "menu", "container", "resize", "datasource", "datatable"]);

        // Mandatory properties
        this.name = "Metaversant.Ratings";
        this.id = htmlId;

        /* Register this component */
        Alfresco.util.ComponentManager.register(this);
        
        YAHOO.Bubbling.on("metadataRefresh", this.onReady, this);

        return this;
    };
   
    YAHOO.extend(Metaversant.Ratings, Alfresco.component.Base,
    {
        /**
         * @property numStars
         * @type int
         */
        numStars: 5,
      
        /**
         * Object container for initialization options
         *
         * @property options
         * @type object
         */
        options:
        {
            /**
             * Target URL that the rating should be posted to.
             *
             * @property targetUrl
             * @type String
             */
            targetUrl: "",
           
            /**
             * Specifies whether or not a metadataRefresh event should
             * be fired after an update.
             *
             * @property fireMetadataRefresh
             * @type boolean
             */
            fireMetadataRefresh: false,
            
            /**
             * Specifies whether or not the ratings should be rendered
             * as read-only.
             *
             * @property readOnly
             * @type boolean
             */
            readOnly: false,
      
        },

        /**
         * Set multiple initialization options at once.
         *
         * @method setOptions
         * @param obj {object} Object literal specifying a set of options
         * @return {Metaversant.Ratings} returns 'this' for method chaining
         */
        setOptions: function MetaversantRatings_setOptions(obj)
        {
            Metaversant.Ratings.superclass.setOptions.call(this, obj);
            return this;
        },
      
        /**
         * Set messages for this component.
         *
         * @method setMessages
         * @param obj {object} Object literal specifying a set of messages
         * @return {Metaversant.Ratings} returns 'this' for method chaining
         */
        setMessages: function MetaversantRatings_setMessages(obj)
        {
            Metaversant.Ratings.superclass.setMessages.call(this, obj);
            return this;
        },

        /**
         * Fired by YUILoaderHelper when required component script files have
         * been loaded into the browser.
         * @method  onComponentsLoaded
         */
        onComponentsLoaded: function MetaversantRatings_onComponentsLoaded()
        {
            /* Register the onReady callback when the DOM has been loaded */
            YAHOO.util.Event.onDOMReady(this.onReady, this, true);
        },

        /**
         * Fired by YUI when parent element is available for scripting.
         * Component initialisation, including instantiation of YUI widgets and event listener binding.
         *
         * @method onReady
         */
        onReady: function MetaversantRatings_onReady()
        {
            this.renderRatingsMarkup();
        },

        /**
         * Finds all rating divs on the page and calls renderRatingMarkup for each one.
         * @method renderRatingsMarkup
         */
        renderRatingsMarkup: function MetaversantRatings_renderRatingsMarkup()
        {
            var ratings = document.getElementsByTagName('div');
            for (var i = 0; i < ratings.length; i++) {
                if (!(ratings[i].className == 'rating' && ratings[i].id.indexOf(this.id) >= 0))
                {
                    continue;
                }
                this.renderRatingMarkup(ratings[i]);
            }      
        },
        
        /**
         * Given a specific ID, finds that div and calls renderRatingMarkup.
         * @method renderRatingsMarkupById
         * @param id {String} representing the ID of the div to be rendered
         */
        renderRatingsMarkupById: function MetaversantRatings_renderRatingsMarkupById(id){
            var ratingDiv = YAHOO.util.Dom.get(id);
            if (ratingDiv != undefined)
            {
                this.renderRatingMarkup(ratingDiv);
            }
        },

        /**
         * Renders the rating markup for all rating elements on the page.
         *
         * @method renderRatingMarkup
         * @param ratingDiv {object} Div containing the rating widget
         */
        renderRatingMarkup: function MetaversantRatings_renderRatingMarkup(ratingDiv)
        {
            var url = this.options.targetUrl;
            var rating = ratingDiv.firstChild.nodeValue;
            if (isNaN(rating) || rating === null)
            {
                return;
            }
            ratingDiv.removeChild(ratingDiv.firstChild);
            if (rating > this.numStars || rating < 0)
            {
                return;
            }

            for (var j = 1; j <= this.numStars; j++)
            {
                var star = document.createElement('img');
                if (rating >= 1) {
                    star.setAttribute('src', Alfresco.constants.URL_CONTEXT + 'extension/images/stars/rating_on.gif');
                    star.className = 'on';
                    rating--;
                } else if(rating > 0 && rating < 1) {
                    star.setAttribute('src', Alfresco.constants.URL_CONTEXT + 'extension/images/stars/rating_half.gif');
                    star.className = 'half';
                    rating = 0;
                } else {
                    star.setAttribute('src', Alfresco.constants.URL_CONTEXT + 'extension/images/stars/rating_off.gif');
                    star.className = 'off';
                }
                var divIdEls = ratingDiv.id.split('_');
                var nodeId = divIdEls[divIdEls.length - 1];
                var widgetId = this.id + '_' + nodeId + '_' + j;
                star.setAttribute('id', widgetId);
                if (!this.options.readOnly)
                {
                    YAHOO.util.Event.addListener(widgetId, "mouseover", this.displayHover, this);
                    YAHOO.util.Event.addListener(widgetId, "mouseout", this.displayNormal, this);
                    YAHOO.util.Event.addListener(widgetId, "click", this.postRating, this);
                }
               
                ratingDiv.appendChild(star);
            }
            ratingDiv.style.visibility = "visible";
        },

        /**
         * Removes the child nodes of the specified div, retrieves the current rating, and writes it to the div.
         * @method refreshRatingMarkup
         * @param id {String} representing the ID of the div that needs to be refreshed.
         */
        refreshRatingMarkup: function MetaversantRatings_refreshRatingMarkup(id)
        {
            var url = this.options.targetUrl;
            var handleSuccess = function(o) {
                var el = document.getElementById(this.id + '_' + id);
                if (el.hasChildNodes()) {
                    el.style.visibility = "hidden";
                    var guard = el.childNodes.length;
                    for(var i = 0; i < guard; i++) {
                        el.removeChild(el.childNodes[0]);
                    }
                }
                var ratingValue = document.createTextNode(o.json.data.nodeStatistics.fiveStarRatingScheme.averageRating);
                el.appendChild(ratingValue);
                if (this.options.fireMetadataRefresh)
                {
                    YAHOO.Bubbling.fire("metadataRefresh"); 
                }
                else
                {
                    this.renderRatingMarkup(el);
                }
            };

            var handleFailure = function(o) {
            };

            Alfresco.util.Ajax.request(
            {
                method: "GET",
                responseContentType: Alfresco.util.Ajax.JSON,
                url: YAHOO.lang.substitute(url,
                {
                    id: id
                }),
                successCallback: {
                    fn: handleSuccess,
                    scope: this
                },
                failureCallback: {
                    fn: handleFailure,
                    scope: this
                }
            });
        }, 

        /**
         * Event handler for metadata refresh.
         * @method onMetadataRefresh
         * @param layer
         * @param args
         */
        onMetadataRefresh: function MetaversantRatings_onMetadataRefresh(layer, args)
        {
            this.renderRatingsMarkupById(args);
        },

        /**
         * Event handler for mouseover.
         * @method displayHover
         * @param e
         */
        displayHover: function MetaversantRatings_displayHover(e, obj)
        {
            var targ;
            if (!e) var e = window.event;
            if (e.target)
            {
                targ = e.target;
            }
            else if (e.srcElement)
            {
                targ = e.srcElement;
            }
            if (targ.nodeType == 3)
            {
                // defeat Safari bug
                targ = targ.parentNode;
            }

            var idEls = targ.id.split("_");
            var id = idEls[idEls.length - 2];
            var rating = idEls[idEls.length - 1];
            for (var i = 1; i <= rating; i++) {
                document.getElementById(obj.id + '_' + id + '_' + i).setAttribute('src', Alfresco.constants.URL_CONTEXT + 'extension/images/stars/rating_over.gif');
            }
        },

        /**
         * Event handler for mouse out.
         * @method displayNormal
         * @param e
         */
        displayNormal: function MetaversantRatings_displayNormal(e, obj)
        {
            var targ;
            if (!e) var e = window.event;
            if (e.target)
            {
                targ = e.target;
            }
            else if (e.srcElement)
            {
                targ = e.srcElement;
            }
            if (targ.nodeType == 3)
            {
                // defeat Safari bug
                targ = targ.parentNode;
            }

            var idEls = targ.id.split("_");
            var id = idEls[idEls.length - 2];
            var rating = idEls[idEls.length - 1];
            for (var i = 1; i <= rating; i++) {
                var status = document.getElementById(obj.id + '_' + id + '_' + i).className;
                document.getElementById(obj.id + '_' + id + '_' + i).setAttribute('src', Alfresco.constants.URL_CONTEXT + 'extension/images/stars/rating_' + status + '.gif');
            }
        },

        /**
         * Event handler for click.
         * @method postRating
         * @param e
         */
        postRating: function MetaversantRatings_postRating(e, obj)
        {
            var targ;
            if (!e) var e = window.event;
            if (e.target)
            {
                targ = e.target;
            }
            else if (e.srcElement)
            {
                targ = e.srcElement;
            }
            if (targ.nodeType == 3)
            {
                // defeat Safari bug
                targ = targ.parentNode;
            }

            var idEls = targ.id.split("_");
            var id = idEls[idEls.length - 2];
            var rating = idEls[idEls.length - 1];
            var url = obj.options.targetUrl;
            var handleSuccess = function(o) {
                obj.refreshRatingMarkup(id);
            };

            var handleFailure = function(o) {
                // Failure handler
                alert("Failed to post rating");
            };

            Alfresco.util.Ajax.request(
            {
                method: "POST",
                requestContentType: Alfresco.util.Ajax.JSON,
                responseContentType: Alfresco.util.Ajax.JSON,
                url: YAHOO.lang.substitute(url,
                {
                    id: id
                }),
                dataObj:
                {
                    id: id,
                    rating: rating,
                    ratingScheme: "fiveStarRatingScheme"
                },
                successCallback:
                {
                    fn: handleSuccess,
                    scope: this
                },
                failureCallback:
                {
                    fn: handleFailure,
                    scope: this
                }
            });        
        }
    });
})();