<#comment>
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
</#comment>
<#macro dateFormat date>${date?string("dd MMM yyyy HH:mm:ss 'GMT'Z '('zzz')'")}</#macro>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
      "nodeRef": "${nodeRef}",
      "ratings":
      [
         <#list ratings as rating>
         {
            "ratingScheme": "${schemeName!""}",
            "rating": ${rating.score?c},
            "appliedBy": "${rating.user!""}"
         }<#if rating_has_next>,</#if>
         </#list>
      ],
      "nodeStatistics":
      {
         "${schemeName!""}":
         {
            "averageRating": ${rating.average?c},
            "ratingsCount": ${rating.count?c}
         }
      }
   }
}
</#escape>