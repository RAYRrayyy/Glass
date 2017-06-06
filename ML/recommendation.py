from __future__ import print_function
import os, time, sys, select, datetime
import gspread
from numpy import *
from oauth2client.service_account import ServiceAccountCredentials

# If modifying these scopes, delete your previously saved credentials
# at ~/.credentials/sheets.googleapis.com-python-quickstart.json
scope = ['https://spreadsheets.google.com/feeds']

def update_recommendation(temp, num, worksheet):
    #Model to redetermine the recommendation#
    #print("data row",temp)
    #print("row number",num)
    #Update Recommendation#
    worksheet.update_cell(num+2,30,"update")
    #clear the request#
    worksheet.update_cell(num+2,29,'0')
    worksheet.update_acell('AE1','0')

#def trainModel(temp)


def main():
    """ Retrieves data from the google sheet and checks if a request has been made
    """
    #Setup to read/write to sheet using gspread#
    credentials = ServiceAccountCredentials.from_json_keyfile_name('My Project-e401bb1cb11e.json', scope)
    gs = gspread.authorize(credentials)
    worksheet = gs.open("CSSE4011Dataset").sheet1  
    i = 0
    #TRAIN THE ML MODEL#
    #`Retrieves the data from the database#
    #temp = asarray(worksheet.get_all_values())
    #temp = delete(temp,(0), axis=0)
    #predictionModel = trainModel(temp)

    while True:       
        if (worksheet.acell('AE1').value == '1'):
            #`Retrieves the data from the database#
            temp = asarray(worksheet.get_all_values())
            temp = delete(temp,(0), axis=0)
            temp = delete(temp,(-1), axis=1)
            temp = delete(temp,(-1),axis=1)
            #Gets which users need updating#
            i = 0
            request_log = []
            for val in temp[:, -1]:
                if val == '1': #request detected
                    request_log.append(i)
                i+=1
            for num in request_log:
                update_recommendation(temp[num], num, worksheet)

            #time.sleep() #sleeps so a check is made every ~15 seconds

        #Keeps Track of iterations/check if server still alive
        print("Iteration = ", i)
        i+=1

if __name__ == '__main__':
    main()
