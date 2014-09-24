/**
 * Alfresco Content Recommendation. Copyright (C) 2014 Zaizi Limited.
 *
 * ——————-
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 * ———————
 */
package org.zaizi.mahout.alfresco;

import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.tagging.TaggingService;
import org.apache.log4j.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 05/09/2011
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public class ZaiziAlfrescoServiceUtil {

    private static Logger logger = Logger.getLogger(ZaiziAlfrescoServiceUtil.class);

    private static final String dbUniqueIdQuery = "@sys\\:node-dbid:";

    private static ServiceRegistry serviceRegistry;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        ZaiziAlfrescoServiceUtil.serviceRegistry = serviceRegistry;
    }


    public static StoreRef getStoreRef() {
        return StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;
    }

    public static ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public static PermissionService getPermissionService() {
        return serviceRegistry.getPermissionService();
    }

    public static TaggingService getTaggingService() {
        return serviceRegistry.getTaggingService();
    }

    public static NodeService getNodeService() {
        return serviceRegistry.getNodeService();
    }

    public static PersonService getPersonService() {
        return serviceRegistry.getPersonService();
    }

    public static SearchService getSearchService() {
        return serviceRegistry.getSearchService();
    }

    public static NodeRef getNodeRefByEntryId(final long entryId) {

        SearchParameters searchParams = new SearchParameters();
        searchParams.setQuery(dbUniqueIdQuery + entryId);
        searchParams.setLanguage(SearchService.LANGUAGE_LUCENE);
        searchParams.addStore(getStoreRef());


        ResultSet results = null;
        try {

            results = getSearchService().query(searchParams);


            if (results.length() > 0) {
                return results.getRow(0).getNodeRef();
            } else {
                return null;
            }

        } finally {
            if (results != null) {
                results.close();
            }
        }


    }

    public static String getUsername(long userID) {
        NodeRef user = getNodeRefByEntryId(userID);
        String username = getNodeService().getProperty(user, ContentModel.PROP_USERNAME).toString();
        logger.trace("username=" + username);
        return username;
    }


    public static long getNodeId(NodeRef nodeRef) {
        return (Long) getNodeService().getProperty(nodeRef, ContentModel.PROP_NODE_DBID);

    }

    public static long getUserId(String username) {
        return getNodeId(getPersonService().getPerson(username));
    }


}
