<#macro formatValue value><#if value?is_number>${value}</#if><#if value?is_date>'${value?string("yyyy-MM-dd HH:mm:ss")}'</#if><#if value?is_string>'${value}'</#if></#macro>
