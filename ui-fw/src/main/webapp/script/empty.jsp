<html>
<head>
<style type="text/css">
html,body {
  height:100%;
  width:100%;
  overflow:hidden;
}
</style>
</head>
<body>
<div id="message"></div>
</body>
<script>
try
{
  var loc = window.parent.location.href;
  if(loc.indexOf("ui-fw") > -1){
  }else{
  document.getElementById('message').innerHTML="<center>Kindly close the window</center>";
  }
}catch (e)
{
 document.getElementById('message').innerHTML="<center>Kindly close the window</center>";
}
</script>
</html>