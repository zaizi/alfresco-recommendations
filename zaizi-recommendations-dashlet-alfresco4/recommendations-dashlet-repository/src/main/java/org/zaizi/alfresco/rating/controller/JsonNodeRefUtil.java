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
package org.zaizi.alfresco.rating.controller;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.template.TemplateNode;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zaizi.mahout.alfresco.ZaiziAlfrescoServiceUtil;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: jcarrey
 * Date: 08/09/2011
 * Time: 08:50
 * To change this template use File | Settings | File Templates.
 */
public class JsonNodeRefUtil {

    private static Logger logger = Logger.getLogger(JsonNodeRefUtil.class);

    // TODO check evaluator.lib.js for complete node transformation.

    public static JSONObject transform(List<NodeRef> nodeRefs) throws JSONException {
        JSONObject root = new JSONObject();
        root.put("totalRecords", nodeRefs.size());
        root.put("startIndex", 0);
        root.put("metadata", getMetadata(nodeRefs));
        root.put("items", getItems(nodeRefs));
        return root;
    }


    private static JSONArray getItems(List<NodeRef> nodeRefs) throws JSONException {
        JSONArray items = new JSONArray();
        for (NodeRef nodeRef : nodeRefs) {
            if (nodeRef != null) {
                items.put(getItem(nodeRef, getTemplateNode(nodeRef)));
            }
        }

        return items;
    }

    private static TemplateNode getTemplateNode(NodeRef nodeRef) {
        return new TemplateNode(nodeRef, ZaiziAlfrescoServiceUtil.getServiceRegistry(), null);
    }

    /*
    {
                       "nodeRef": "workspace://SpacesStore/63475428-bd70-4b80-8a83-12754ef05427",
                       "nodeType": "cm:content",
                       "type": "document",
                       "mimetype": "text\/plain",
                       "isFolder": false,
                       "isLink": false,
                       "fileName": "14",
                       "displayName": "14",
                       "status": "",
                       "title": "",
                       "description": "",
                       "author": "",
                       "createdOn": "2011-09-07T12:13:04.118Z",
                       "createdBy": "Administrator",
                       "createdByUser": "admin",
                       "modifiedOn": "2011-09-07T12:18:52.245Z",
                       "modifiedBy": "test3 test3",
                       "modifiedByUser": "test3",
                       "lockedBy": "",
                       "lockedByUser": "",
                       "size": "0",
                       "version": "1.0",
                       "contentUrl": "api/node/content/workspace/SpacesStore/63475428-bd70-4b80-8a83-12754ef05427/14",
                       "webdavUrl": "\/webdav\/Sites\/test\/documentLibrary\/14",
                       "actionSet": "document",
                       "tags": [],
                       "categories": [],
                       "activeWorkflows": "",
                       "isFavourite": false,


                 }, ...
     */
    private static JSONObject getItem(NodeRef nodeRef, TemplateNode templateNode) throws JSONException {
        JSONObject item = new JSONObject();
        /*

        */


        FileInfo info = ZaiziAlfrescoServiceUtil.getServiceRegistry().getFileFolderService().getFileInfo(nodeRef);

        item.put("nodeRef", templateNode.getNodeRef().toString());
        item.put("nodeType", templateNode.getTypeShort());
        item.put("type", "document");
        item.put("mimetype", templateNode.getMimetype());
        item.put("isFolder", info.isFolder());
        item.put("isLink", info.isLink());
        item.put("fileName", info.getName());

        String title = (String) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(nodeRef, ContentModel.PROP_TITLE);
        String displayName = title == null || "".equals(title) ? info.getName() : title;

        item.put("title", title);
        item.put("displayName", displayName);
        item.put("description", (String)
                ZaiziAlfrescoServiceUtil.getNodeService().getProperty(nodeRef, ContentModel.PROP_DESCRIPTION));
        item.put("author", (String)
                ZaiziAlfrescoServiceUtil.getNodeService().getProperty(nodeRef, ContentModel.PROP_AUTHOR));

        item.put("createdOn", toString((Date)
                ZaiziAlfrescoServiceUtil.getNodeService().getProperty(nodeRef, ContentModel.PROP_CREATED)));
        String username = (String) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(nodeRef, ContentModel.PROP_CREATOR);
        String usernameFriendly;
        if (username == null) {
            username = "";
            usernameFriendly = "";
        } else {
            usernameFriendly = getFriendlyUsername(ZaiziAlfrescoServiceUtil.getPersonService().getPerson(username));
        }
        item.put("createdBy", usernameFriendly);
        item.put("createdByUser", username);


        item.put("modifiedOn", toString((Date)
                ZaiziAlfrescoServiceUtil.getNodeService().getProperty(nodeRef, ContentModel.PROP_MODIFIED)));
        username = (String) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(nodeRef, ContentModel.PROP_MODIFIER);
        if (username == null) {
            username = "";
            usernameFriendly = "";
        } else {
            usernameFriendly = getFriendlyUsername(ZaiziAlfrescoServiceUtil.getPersonService().getPerson(username));

        }

        item.put("modifiedBy", usernameFriendly);
        item.put("modifiedByUser", username);


        username = (String) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(nodeRef, ContentModel.PROP_LOCK_OWNER);
        if (username == null) {
            username = "";
            usernameFriendly = "";
        } else {
            usernameFriendly = getFriendlyUsername(ZaiziAlfrescoServiceUtil.getPersonService().getPerson(username));

        }
        item.put("lockedBy", usernameFriendly);
        item.put("lockedByUser", username);


        item.put("size", templateNode.getSize());


        item.put("contentUrl", getContentUrl(templateNode));
        item.put("webdavUrl", templateNode.getWebdavUrl());


        // List<Action> actions = ZaiziAlfrescoServiceUtil.getServiceRegistry().getActionService().getActions(nodeRef);
        item.put("tags", array(ZaiziAlfrescoServiceUtil.getTaggingService().getTags(nodeRef)));

        item.put("rating", getRating(nodeRef));


        try {

            List<TemplateNode> categoriesList = (List<TemplateNode>) templateNode.getProperties().get("categories");

            if (categoriesList != null) {

                JSONArray categories = new JSONArray();
                JSONArray category;
                for (TemplateNode cat : categoriesList) {
                    category = new JSONArray();
                    category.put(cat.getName());
                    category.put(cat.getDisplayPath().replace("/categories/General", ""));
                    categories.put(category);
                }
                item.put("categories", categories);
            }

        } catch (Exception e) {
            logger.error(e, e);
        }


        List<WorkflowInstance> activeWorkflows =
                ZaiziAlfrescoServiceUtil.getServiceRegistry().getWorkflowService().getWorkflowsForContent(nodeRef, true);


        StringBuilder activeWorkflowsSB = new StringBuilder();
        for (int i = 0; i < activeWorkflows.size(); i++) {
            activeWorkflowsSB.append(activeWorkflows.get(i).getId());
            if (i < activeWorkflows.size() - 1) {
                activeWorkflowsSB.append(",");
            }
        }

        item.put("activeWorkflows", activeWorkflowsSB.toString());
        item.put("isFavourite", false);
        item.put("location", getLocation(nodeRef, templateNode));
        item.put("permissions", getPermissions(nodeRef, templateNode));
        item.put("custom", getCustomProperties(nodeRef, templateNode));
        //item.put("actionLabels", null);
        return item;
    }

    private static String toString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return dateFormat.format(date.clone()); // //011-09-06T13:59:06.000Z
    }

    private static String getContentUrl(TemplateNode templateNode) {
        //"api/node/content/${node.storeType}/${node.storeId}/${node.id}/${node.name?url}"
        return new StringBuilder("api/node/content/")
                .append(templateNode.getStoreType())
                .append("/").append(templateNode.getStoreId())
                .append("/").append(templateNode.getId())
                .append("/").append(templateNode.getName())
                .toString();
    }

    private static String getFriendlyUsername(NodeRef person) {
        return compoundName(
                (String) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(person, ContentModel.PROP_FIRSTNAME)
                , (String) ZaiziAlfrescoServiceUtil.getNodeService().getProperty(person, ContentModel.PROP_LASTNAME));
    }

    private static String compoundName(String firstName, String lastName) {
        if (lastName == null) {
            return firstName;
        } else {
            return firstName + " " + lastName;
        }
    }


    private static JSONObject getCustomProperties(NodeRef nodeRef, TemplateNode templateNode) throws JSONException {

        JSONObject custom = new JSONObject();
        for (Map.Entry<String, Serializable> entry : templateNode.getProperties().entrySet()) {
            if (!entry.getKey().contains("{http://www.alfresco.org/model")) {
                custom.put(entry.getKey().substring(entry.getKey().lastIndexOf("}") + 1), entry.getValue());
            }
        }


        return custom;
    }


    private static JSONObject getPermissions(NodeRef nodeRef, TemplateNode templateNode) throws JSONException {
        JSONObject permissions = new JSONObject();
        permissions.put("inherited", templateNode.getInheritsPermissions());
        permissions.put("roles", array(templateNode.getFullPermissions()));
        permissions.put("userAccess", getUserAccess(nodeRef, templateNode));
        return permissions;
    }

    private static JSONObject getUserAccess(NodeRef nodeRef, TemplateNode templateNode) throws JSONException {
        JSONObject userAccess = new JSONObject();


        userAccess.put("inline-edit", templateNode.hasAspect("app:inlineeditable"));

        userAccess.put("permissions",
                ZaiziAlfrescoServiceUtil.getPermissionService().hasPermission(nodeRef, PermissionService.CHANGE_PERMISSIONS)
                        == AccessStatus.ALLOWED);

        userAccess.put("edit",
                ZaiziAlfrescoServiceUtil.getPermissionService().hasPermission(nodeRef, PermissionService.WRITE)
                        == AccessStatus.ALLOWED);

        userAccess.put("delete",
                ZaiziAlfrescoServiceUtil.getPermissionService().hasPermission(nodeRef, PermissionService.DELETE)
                        == AccessStatus.ALLOWED);

        userAccess.put("cancel-checkout",
                ZaiziAlfrescoServiceUtil.getPermissionService().hasPermission(nodeRef, PermissionService.CANCEL_CHECK_OUT)
                        == AccessStatus.ALLOWED);

        userAccess.put("create",
                ZaiziAlfrescoServiceUtil.getPermissionService().hasPermission(nodeRef, PermissionService.CREATE_CHILDREN)
                        == AccessStatus.ALLOWED);


        return userAccess;
    }

    private static JSONArray array(List<String> values) {
        JSONArray array = new JSONArray();
        for (String value : values) {
            array.put(value);
        }
        return array;
    }

    private static JSONArray array(String... values) {
        JSONArray array = new JSONArray();
        for (String value : values) {
            array.put(value);
        }
        return array;
    }

    /*
       "location":
       {
          "repositoryId": "41b73894-b515-4f3e-ae99-e88fe4857702",
          "site": "test",
          "siteTitle": "test",
          "container": "documentLibrary",
          "path": "\/",
          "file": "14",
          "parent":
          {
          }
       },
     */
    private static JSONObject getLocation(NodeRef nodeRef, TemplateNode templateNode) throws JSONException {
        JSONObject location = new JSONObject();

        location.put("repositoryId", ZaiziAlfrescoServiceUtil.getServiceRegistry().getDescriptorService().getCurrentRepositoryDescriptor().getId());

        SiteInfo site = ZaiziAlfrescoServiceUtil.getServiceRegistry().getSiteService().getSite(nodeRef);

        location.put("site", site!=null?site.getShortName():new String());
        location.put("siteTitle", site!=null?site.getTitle():new String());
        location.put("container", "documentLibrary");
        location.put("path", "/");
        location.put("file", "");
        location.put("parent", "");
        return location;
    }

    /*
     "metadata":
           {
              "repositoryId": "41b73894-b515-4f3e-ae99-e88fe4857702",

              "parent":
              {
                 "nodeRef": "workspace://SpacesStore/c8409645-05f5-4ff9-9f09-ffdf6003274d",
                 "permissions":
                 {
                    "userAccess":
                    {
                       "permissions": true,
                       "edit": true,
                       "delete": true,
                       "cancel-checkout": false,
                       "create": true
                    }
                 }
              },
              "onlineEditing": false,
              "itemCounts":
              {
                 "folders": 0,
                 "documents": 25
              }
           }
    */
    private static JSONObject getMetadata(List<NodeRef> nodeRefs) throws JSONException {
        JSONObject metadata = new JSONObject();
        metadata.put("repositoryId", ZaiziAlfrescoServiceUtil.getServiceRegistry().getDescriptorService().getCurrentRepositoryDescriptor().getId());
        // metadata.put("parent", null);
        metadata.put("onlineEditing", false);

        // itemCounts
        JSONObject itemCounts = new JSONObject();
        itemCounts.put("folders", 0);
        itemCounts.put("documents", nodeRefs.size());

        metadata.put("itemCounts", itemCounts);
        return metadata;
    }

    public static JSONObject getRating(NodeRef nodeRef) throws JSONException {
        JSONObject rating = new JSONObject();
        // Ratings - Is rateable = true allways.

        rating.put("isRateable", true);

        rating.put("ratingsCount",
                ZaiziAlfrescoServiceUtil.getServiceRegistry().getRatingService()
                        .getRatingsCount(nodeRef, "fiveStarRatingScheme"));

        rating.put("averageRating",
                ZaiziAlfrescoServiceUtil.getServiceRegistry().getRatingService()
                        .getAverageRating(nodeRef, "fiveStarRatingScheme"));

        rating.put("totalRating",
                ZaiziAlfrescoServiceUtil.getServiceRegistry().getRatingService()
                        .getTotalRating(nodeRef, "fiveStarRatingScheme"));

        return rating;
    }
}
