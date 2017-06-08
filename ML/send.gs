function doGet(e){
  return handleResponse(e); 
}
 
//Recieve parameter and pass it to function to handle
 
function doPost(e){
  return handleResponse(e);
}  
 
// here handle with parameter
 
function handleResponse(request) {
  var output  = ContentService.createTextOutput();
  
  //create varibles to recieve respective parameters
  var user = request.parameter.user;
  var location = request.parameter.location;
  var preference = request.parameter.preference;
  var id = request.parameter.id;
  var X = request.parameter.X;
  
  //open your Spread sheet by passing id
  var ss= SpreadsheetApp.openById(id);
  var sheet=ss.getSheetByName("Sheet1");
  
  if (X == "0" || X == 0) { //runs when the data needs to be posted
  
    //Check if user id is present
    var column = 1; //column Index   
    var columnValues = sheet.getRange(1, column, sheet.getLastRow()).getValues(); //1st is header row
    var searchResult = columnValues.findIndex(user); //Row Index - 2
    var newRowIndex = columnValues.length; 
    //If the user_id isnt found, create new user
    if(searchResult == -1)
    {
      var rowData = [user];
      for (i = 1; i < 31; i++) { 
        if(i == location){
          rowData.push(preference);
        }else{
          rowData.push("0");
        }
      }
      sheet.appendRow(rowData);
      var histCell = sheet.getRange(newRowIndex+1, 29);
      histCell.setValue(location);
      
      //Request Reccomendation by Setting status to 1
      var requestCell = sheet.getRange(newRowIndex+1, 30);
      requestCell.setValue(1);
    } else { //user id already present so just modify the preference list
      var row_index = parseInt(location,10); //convert location from str to int
      var cell = sheet.getRange(searchResult+1, row_index + 1);
      cell.setValue(preference);
      
      var histCell = sheet.getRange(searchResult+1, 29);
      histCell.setValue(location);
  
      
      //Request Reccomendation by Setting status to 1
      var requestCell = sheet.getRange(searchResult+1, 30);
      requestCell.setValue(1);
    }
    
    //Notify Update
    var updateCell = sheet.getRange(1, 32);
    updateCell.setValue(1);
    
    var callback = request.parameters.callback;
    if (callback === undefined) {
      output.setContent(JSON.stringify("Updating Recommendation"));
    } else {
      output.setContent(callback + "(" + JSON.stringify("Success") + ")");
    }
    output.setMimeType(ContentService.MimeType.JSON);
    return output;
  } else { //Runs when recommendation is needed
    //Get row number
    var column = 1; //column Index   
    var columnValues = sheet.getRange(1, column, sheet.getLastRow()).getValues(); //1st is header row
    var searchResult = columnValues.findIndex(user); //Row Index - 2
    
    var recCell = sheet.getRange(searchResult+1, 31);
    var callback = request.parameters.callback;
    if (callback === undefined) {
      output.setContent(JSON.stringify(recCell.getValue()));
    } else {
      output.setContent(callback + "(" + JSON.stringify("Success") + ")");
    }
    output.setMimeType(ContentService.MimeType.JSON);
    return output;
  }
}


Array.prototype.findIndex = function(search){
  if(search == "") return false;
  for (var i=0; i<this.length; i++)
    if (this[i].toString().indexOf(search) > -1 ) return i;
  return -1;
} 