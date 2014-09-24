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
function main() {
    // extract mandatory data from request body
    if (!json.length() > 0) {
        status.setCode(status.STATUS_BAD_REQUEST, "No rating data provided");
        return;
    }

    if (url.templateArgs.id == null || url.templateArgs.id.length == 0) {
        logger.log("Node ID argument not set");
        status.code = 400;
        status.message = "Node ID argument not set";
        status.redirect = true;
        return;
    }

    if (url.templateArgs.store_type == null || url.templateArgs.store_type.length ==0) {
        logger.log("Store type not set");
        status.code = 400;
        status.message = "Store type not set";
        status.redirect = true;
        return;
    }

    if (url.templateArgs.store_id == null || url.templateArgs.store_id.length ==0) {
        logger.log("Store ID not set");
        status.code = 400;
        status.message = "Store ID not set";
        status.redirect = true;
        return;
    }

    var nodeRefStr = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
    logger.log("Getting current node");
    var curNode = search.findNode(nodeRefStr);
    if (curNode == null) {
        logger.log("Node not found");
        status.code = 404;
        status.message = "No node found for node ref:" + nodeRefStr;
        status.redirect = true;
        return;
    }

    var storeType = url.templateArgs.store_type;
    var storeId = url.templateArgs.store_id;
    var id = url.templateArgs.id;
    var rating = json.get("rating");

    if (rating == null || rating.length == 0) {
        logger.log("Rating arg not set");
        status.code = 400;
        status.message = "Rating has not been provided";
        status.redirect = true;
        return;
    }
    
    if (rating < 1 || rating > 5) {
        logger.log("Rating out of range");
        status.code = 400;
        status.message = "Rating value must be between 1 and 5 inclusive";
        status.redirect = true;
        return;
    }
    
    var ratingVal = parseInt(rating);
    var userName = person.properties['cm:userName'];
        
    logger.log("Setting rating");

    mratings.rate(curNode, ratingVal, userName); 
    
    model.nodeRef = nodeRefStr;
    model.user = userName;
    model.rating = ratingVal;
    model.schemeName = "fiveStarRatingScheme"; // 3.4 compat

}

main();