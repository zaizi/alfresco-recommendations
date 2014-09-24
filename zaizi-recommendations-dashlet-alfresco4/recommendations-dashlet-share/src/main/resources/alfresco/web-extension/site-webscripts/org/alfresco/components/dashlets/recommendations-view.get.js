// Get the Documents Modified data for this site
var url = "/api/recommendations/list";
var json = remote.call(url + "?maxResults=5");
var obj = eval('(' + json + ')');
if (json.status == 200)
{
   // Create the model
   model.docs = obj;
}
else
{
   model.docs = {message: obj.message};
}