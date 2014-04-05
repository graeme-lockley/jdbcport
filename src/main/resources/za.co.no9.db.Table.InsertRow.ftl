<#include "za.co.no9.db.Common.ftl">
insert into ${tableName} (
<#list fields?keys as field>
${field}<#if field_has_next>, </#if>
</#list>) values (
<#list fields?keys as field>
<@formatValue value=fields[field]/><#if field_has_next>, </#if>
</#list>)
