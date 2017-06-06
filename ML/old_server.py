from __future__ import print_function
import httplib2
import os, time, sys, select, datetime
import gspread
from numpy import *
from apiclient import discovery
from oauth2client import client
from oauth2client import tools
from oauth2client.file import Storage

temp = array([])

try:
    import argparse
    flags = argparse.ArgumentParser(parents=[tools.argparser]).parse_args()
except ImportError:
    flags = None

# If modifying these scopes, delete your previously saved credentials
# at ~/.credentials/sheets.googleapis.com-python-quickstart.json
SCOPES = 'https://www.googleapis.com/auth/spreadsheets.readonly'
CLIENT_SECRET_FILE = 'client_secret.json'
APPLICATION_NAME = 'Google Sheets API Python Quickstart'

def get_credentials():
    """Gets valid user credentials from storage.

    If nothing has been stored, or if the stored credentials are invalid,
    the OAuth2 flow is completed to obtain the new credentials.

    Returns:
        Credentials, the obtained credential.
    """
    home_dir = os.path.expanduser('~')
    credential_dir = os.path.join(home_dir, '.credentials')
    if not os.path.exists(credential_dir):
        os.makedirs(credential_dir)
    credential_path = os.path.join(credential_dir,
                                   'sheets.googleapis.com-python-quickstart.json')
    store = Storage(credential_path)

    credentials = store.get()

    if not credentials or credentials.invalid:
        flow = client.flow_from_clientsecrets(CLIENT_SECRET_FILE, SCOPES)
        flow.user_agent = APPLICATION_NAME
        if flags:
            credentials = tools.run_flow(flow, store, flags)
        else: # Needed only for compatibility with Python 2.6
            credentials = tools.run(flow, store)
        print('Storing credentials to ' + credential_path)
    return credentials

def retrieve_data(service, spreadsheetId, rangeName):
    
    result = service.spreadsheets().values().get(spreadsheetId=spreadsheetId, range=rangeName).execute()
    values = result.get('values', [])
    if not values:
        print('No data found.')
    else: 
        temp = asarray(values)
    return temp

def update_recommendation(temp, num, worksheet):
    #Model to redetermine the recommendation#
    print("data row",temp)
    print("row number",num)
    string = "loc " + time.time();
    #Update Recommendation#
    worksheet.update_cell(num+2,30,string)

    #clear the request#
    worksheet.update_cell(num+2,29,'0')


def main():
    """ Retrieves data from the google sheet and checks if a request has been made
    """
    #Setup to read from the sheet#
    credentials = get_credentials()
    http = credentials.authorize(httplib2.Http())
    discoveryUrl = ('https://sheets.googleapis.com/$discovery/rest?' 'version=v4')
    service = discovery.build('sheets', 'v4', http=http, discoveryServiceUrl=discoveryUrl)
    spreadsheetId = '13ordFQXWTch9kvvulG08rtA76W-nKKcHwk887UiY59Q'
    rangeName = 'A2:AC'

    #Setup to write to sheet using gspread#
    #gs = gspread.authorize(credentials)
    #worksheet = gs.open("CSSE4011Dataset").Sheet1  

    while True:
        temp = retrieve_data(service, spreadsheetId, rangeName)
        #Gets the time stamp right after data has been retrieved
        utc_time = time.time()
        time_stamp = datetime.datetime.fromtimestamp(utc_time).strftime('%Y-%m-%d %H:%M:%S')
        print("Time Stamp = ", time_stamp)
        print(temp)

        #Checks if a new request has been posted#
        i = 0
        request_log = []
        for val in temp[:, -1]:
            print(val)
            if val == '1': #request detected
                request_log.append(i)
            i+=1
        print(request_log)
        #for num in request_log:
            #update_recommendation(temp[num], num, worksheet)

        time.sleep(2) #sleeps so a check is made every ~15 seconds
        
if __name__ == '__main__':
    main()
