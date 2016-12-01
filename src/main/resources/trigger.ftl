<?xml version="1.0" encoding="UTF-8"?>
  <trg:Trigger  xmlns:trg="urn:nnds:trigger:01:01" 
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        xsi:schemaLocation="urn:nnds:trigger:01:01 triggering.xsd"
        xsi:type="trg:UnscheduledTrigger">
      <trg:Target>${target}</trg:Target>
      <trg:ActualTime>${actualTime}</trg:ActualTime>
      <trg:Type><#list type as type>${type}</#list></trg:Type>
      <trg:Key>${key}</trg:Key>
      <trg:UriExt>${uriExt}</trg:UriExt>
   </trg:Trigger>