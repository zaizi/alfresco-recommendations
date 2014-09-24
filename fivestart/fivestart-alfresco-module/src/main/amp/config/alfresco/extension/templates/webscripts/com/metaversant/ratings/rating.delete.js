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

    mratings.deleteRatings(curNode);

    model.nodeRef = nodeRefStr;
    model.schemeName = "fiveStarRatingScheme"; // 3.4 compat

}

main();