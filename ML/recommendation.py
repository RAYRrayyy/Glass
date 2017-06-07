from __future__ import print_function
import os, time, sys, select, datetime
import gspread
from numpy import *
from oauth2client.service_account import ServiceAccountCredentials

#RECOMMENDATIONS#

#Class for learning models, different for each location#
class location_learner:
    def __init__(self, location_number):
        self.location_number = location_number

    def train_model(self, data):
        self.data = data

        #TRAINING FOR LOCATION 1 - Chancellor's Place#
        if self.location_number == '1':
            #Possible destinations: 13,18,6,4#
            self.temp = copy(data[:,[4,6,13,18,1]])
            #Replace zeros with -1#
            self.train_data = copy(self.temp)
            self.train_data[self.temp=="0"] = "-1"
            print(self.train_data)



    #Given data (user's past locations) and current location#
    def determine_location(self, data,location_number):
        print('stuff')

#MAIN LOOP AND GOOGLE SHEET COMMUNICATION#

# If modifying these scopes, delete your previously saved credentials
# at ~/.credentials/sheets.googleapis.com-python-quickstart.json
scope = ['https://spreadsheets.google.com/feeds']

def update_recommendation(temp, num, worksheet):
    #Model to redetermine the recommendation#
    #print("data row",temp)
    #print("row number",num)
    #Update Recommendation#
    worksheet.update_cell(num+2,31,"update")
    #clear the request#
    worksheet.update_cell(num+2,30,'0')
    worksheet.update_acell('AF1','0')

def init_models(worksheet):
    temp = get_data(worksheet)
    #Removes names and unneccessary column#
    temp = delete(temp,(0),axis=1)
    temp = delete(temp,(-1),axis=1)

    #Create Class Instances for each location#
    model_one = location_learner("1")
    model_one.train_model(temp)

def get_data(worksheet):
    #`Retrieves the data from the database#
    temp = asarray(worksheet.get_all_values())

    #Removes headings and server communication columns#
    temp = delete(temp,(0), axis=0)
    temp = delete(temp,(-1),axis=1)
    temp = delete(temp,(-1),axis=1)
    temp = delete(temp,(-2),axis=1)
    return temp

def main():
    """ Retrieves data from the google sheet and checks if a request has been made
    """
    #Setup to read/write to sheet using gspread#
    credentials = ServiceAccountCredentials.from_json_keyfile_name('My Project-e401bb1cb11e.json', scope)
    gs = gspread.authorize(credentials)
    worksheet = gs.open("CSSE4011Dataset").sheet1  
    count = 0

    #Initiate ML Models#
    init_models(worksheet)

    while True:       
        if (worksheet.acell('AF1').value == '1'):
            temp = get_data(worksheet)
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
        print("Iteration = ", count)
        count+=1

if __name__ == '__main__':
    main()
