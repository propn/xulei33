function buttonOver(){
 var obj = window.event.srcElement;
 if(obj.tagName.toLowerCase() == "span"   &&   obj.className.replace(/controlButton/ig,"star") == "star" )
 {
  obj.runtimeStyle.cssText="border-color:#fff #606060 #808080 #fff;padding:3 0 0 0 ";
 }
 if(obj.tagName.toLowerCase() == "span"   &&   obj.parentNode.className.replace(/Ctable/ig,"star") == "star" )
 {
  obj.style.backgroundColor = "#fff";
 }
}
function buttonOut(){
 var obj = window.event.srcElement;
 if(obj.tagName.toLowerCase() == "span"   &&   obj.className.replace(/controlButton/ig,"star") == "star" )
 {
  obj.runtimeStyle.cssText = "";
 }
 if(obj.tagName.toLowerCase() == "span"   &&   obj.parentNode.className.replace(/Ctable/ig,"star") == "star" )
 {
  window.setTimeout(function(){obj.style.backgroundColor = ""; },300);
 }
}
function buttonDown(){
 var obj = window.event.srcElement;
 if(obj.tagName.toLowerCase() == "span"   &&   obj.className.replace(/controlButton/ig,"star") == "star" )
 {
  obj.setCapture();
  obj.runtimeStyle.borderColor="#808080 #fefefe #fefefe #808080";
 }
}
function buttonUp(){
 var obj = window.event.srcElement;
 if(obj.tagName.toLowerCase() == "span"   &&  obj.className.replace(/controlButton/ig,"star") == "star" )
 {
  obj.releaseCapture();
  obj.runtimeStyle.cssText ="";
 }
}
function doCmd(){
 var obj = window.event.srcElement;
 if(obj.tagName.toLowerCase() == "span"   &&  obj.className.replace(/controlButton/ig,"star") == "star" )
 {
   switch(obj.getAttribute("cmd"))
   {
    case "py":
     window.document.all.titleYear.innerHTML=window.TranYearMonthTitle(window.theCalendar.year-1,window.theCalendar.month);
     window.document.all.calendarBox.innerHTML=makeCalendarHtmlStr(window.theCalendar.year-1,window.theCalendar.month);
     break;
    case "pm":
     window.document.all.titleYear.innerHTML=window.TranYearMonthTitle(window.theCalendar.year,window.theCalendar.month-1);
     window.document.all.calendarBox.innerHTML=makeCalendarHtmlStr(window.theCalendar.year,window.theCalendar.month-1);
     break;
    case "nm":
     window.document.all.titleYear.innerHTML=window.TranYearMonthTitle(window.theCalendar.year,window.theCalendar.month+1);
     window.document.all.calendarBox.innerHTML=makeCalendarHtmlStr(window.theCalendar.year,window.theCalendar.month+1);
     break;
    case "ny":
     window.document.all.titleYear.innerHTML=window.TranYearMonthTitle(window.theCalendar.year+1,window.theCalendar.month);
     window.document.all.calendarBox.innerHTML=makeCalendarHtmlStr(window.theCalendar.year+1,window.theCalendar.month);
     break;
   }
   window.document.all.calendarBox.show();
   window.currentSelectDate = starCaTran(Nyear,Nmonth,Ndate);
   window.currentActiveItem = window.document.getElementById(currentSelectDate);
   if( window.currentActiveItem )window.currentActiveItem.runtimeStyle.cssText = "background:url(choiceit.gif) no-repeat 12px 6px;color:#000;padding-top:1px;font-weight:bold";
 }
}
function show()
{
 var box = this;
 window.clearTimeout(box.timeHandle);
 var CdateBoxs = this.getElementsByTagName("span");
 for(var i=0;i<CdateBoxs.length;i++)
 {
  CdateBoxs[i].defaultValue = new Number( CdateBoxs[i].innerHTML );
  CdateBoxs[i].innerHTML = 0;
 }
 showDate();
 function showDate(){
  for(var i=0;i<CdateBoxs.length;i++){
   if( new Number( CdateBoxs[i].innerHTML ) + 1 <= new Number( CdateBoxs[i].defaultValue ) )
    CdateBoxs[i].innerHTML = new Number( CdateBoxs[i].innerHTML ) + 1 
  }
  box.timeHandle = window.setTimeout(showDate,1);
 }
this.show = show1
}
function show1()
{
 var box = this;
 window.clearTimeout(box.timeHandle);
 var CdateBoxs = this.getElementsByTagName("span");
 for(var i=0;i<CdateBoxs.length;i++)CdateBoxs[i].style.display = "none";
 showDate(CdateBoxs[0]);
 function showDate(obj){
  if( !obj )return;
  obj.style.display = "inline";
  box.timeHandle = window.setTimeout(function(){showDate(obj.nextSibling);},1);
 }
this.show = show
}
/*
function show()
{
}
*/