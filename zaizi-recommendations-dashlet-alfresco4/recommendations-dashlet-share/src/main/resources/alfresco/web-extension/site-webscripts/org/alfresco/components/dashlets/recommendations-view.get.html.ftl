<#--

    Alfresco Content Recommendation. Copyright (C) 2014 Zaizi Limited.

    ——————-
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software Foundation,
    Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
    ———————

-->

<#macro doclibUrl doc>
	<#if doc.location.site?string=="">
		<#assign detailsURL="${url.context}/page/document-details?nodeRef=${doc.nodeRef}"/>
	<#else>
		<#assign detailsURL="${url.context}/page/site/${doc.location.site}/document-details?nodeRef=${doc.nodeRef}"/>
	</#if>
   <a href="${detailsURL}" class="theme-color-1">${doc.displayName?html}</a>
</#macro>
<div class="dashlet">
   <div class="title">${msg("header.docSummary")}</div>
   <div class="body scrollableList">
   <#if docs.message?exists>
      <div class="detail-list-item first-item last-item">
         <div class="error">${docs.message}</div>
      </div>
   <#else>
      <#if docs.items?size == 0>
      <div class="detail-list-item first-item last-item">
         <span>${msg("label.noItems")}</span>
      </div>
      <#else>
         <#list docs.items as doc>
            <#assign modifiedBy><a href="${url.context}/page/user/${doc.modifiedByUser?url}/profile" class="theme-color-1">${doc.modifiedBy?html}</a></#assign>
      <div class="detail-list-item <#if doc_index = 0>first-item<#elseif !doc_has_next>last-item</#if>">
         <div>
            <div class="icon">
               <img src="${url.context}/res/components/images/generic-file-32.png" alt="${doc.displayName?html}" />
            </div>
            <div class="details">
               <h4><@doclibUrl doc /></h4>
               <div>
                  ${msg("text.modified-by", modifiedBy)} ${msg("text.modified-on", xmldate(doc.modifiedOn)?string("EEE dd MMM yyyy HH:mm:ss z"))}
               </div>
            </div>
         </div>
      </div>
         </#list>
      </#if>
   </#if>
   </div>
</div>