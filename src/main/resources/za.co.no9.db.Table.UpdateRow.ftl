<#include "za.co.no9.db.Common.ftl">
update ${tableName} set
<#list fields?keys as field>
  ${field} = <@formatValue value=fields[field]/><#if field_has_next>, </#if>
</#list>
where
<#list primaryKey.names as keyNames>
  ${keyNames} = <@formatValue value=primaryKey.values[keyNames_index]/> <#if keyNames_has_next> and </#if>
</#list>
