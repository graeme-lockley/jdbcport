<#include "za.co.no9.db.Common.ftl">
delete from ${tableName} where
<#list primaryKey.names as keyNames>
${keyNames} = <@formatValue value=primaryKey.values[keyNames_index]/> <#if keyNames_has_next> and </#if>
</#list>
