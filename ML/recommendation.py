from __future__ import print_function
import os, time, sys, select, datetime
import gspread
from numpy import *
from oauth2client.service_account import ServiceAccountCredentials
from keras.models import Sequential
from keras.layers import Dense

scope = ['https://spreadsheets.google.com/feeds']
accuracy_list = []
""" Class which is used for building of Neural Networks
    Remove instances where user has not visited a location (0)
    Reduces possible Bias in training the Neural Networks
"""
class location_learner:
    def __init__(self, location_number):
        self.location_number = location_number

    """ Processes Data before training Neural Networks
        Remove instances where user has not visited a location (0)
        Reduces possible Bias in training the Neural Networks
        Inputs: data : Numpy matrix containing all user preference
        location : The model's location, an int from 0 to 26
        Output: The processed data
    """
    def process_data(self,data,location):
        self.col = []
        #Removes data where the current location wasn't visited, to minimize bias#
        for i in range(shape(data)[0]):
            if data[i,location] != 0:
                self.col.append(i)
        return copy(data[self.col,:])

    """ Builds the Neural Network
        Inputs: X - User Preference Data for all locations except for the current model location
                Y - User Preference Data for the current model location
        Output: The built neural network
    """
    def build(self,X,Y):
        # fix random seed for reproducibility
        random.seed(7)
        # create model
        self.model = Sequential()
        self.model.add(Dense(50, input_dim=26, activation='relu'))
        self.model.add(Dense(26, activation='relu'))
        self.model.add(Dense(1, activation='sigmoid'))
        # Compile model
        self.model.compile(loss='binary_crossentropy', optimizer='adam', metrics=['accuracy'])
        # Fit the model
        self.model.fit(X, Y, epochs=150, batch_size=10)
        # evaluate the model
        return self.model

    """ Builds and Tests the Neural Network
        Inputs: data - The whole numpy matrix from the database
        Output: The final prediction neural network
    """   
    def train_model(self, data):
        print(self.location_number)

        self.data = data
        self.temp = self.process_data(self.data,self.location_number-1)
        self.output_Y = copy(self.temp[:,[self.location_number-1]])
        self.train_X = delete(self.temp, self.location_number-1, axis=1)
        self.model = self.build(self.train_X,self.output_Y)
        self.scores = self.model.evaluate(self.train_X, self.output_Y)
        self.prediction_model = self.model
        print("\n%s: %.2f%%" % (self.model.metrics_names[1], self.scores[1]*100))
        accuracy_list.append(self.scores[1]*100)
        return self.model
"""Processes row data for prediction querying data
   Inputs: row - Current User's Preferences
           location - location to query
    Output: returns the query data for prediction
"""
def build_query(row,location):
    tmp = ones((1,26),int)
    index = 0
    for j in range(shape(row)[0]):
        if j != location:
            tmp[0,index] = row[j]
            index += 1
    return tmp
"""Executes the prediction for the location in question
   Inputs: prediction_models - Array of neural networks
           location - location to query
           user_data - the query data/current user's preferences
   Output: returns the result of the prediction 1.0 or -1.0
"""
def predict_user(prediction_models,location,user_data):
    predictions = prediction_models[location].predict(user_data)
    # round predictions
    rounded = [round(x[0]) for x in predictions]
    return rounded
"""Retrieves suggested locations using the Neural Networks and updates database
   Inputs: prediction_models - Array of neural networks
           temp - entire Numpy matrix with user preference data
           num - the user that has requested an update
           worksheet - The google sheet database
"""
def update_recommendation(prediction_models, temp, num, worksheet):
    #Model to redetermine the recommendation#
    row = temp[1:-2]
    row = row.astype(int)
    current_location = row[-2] - 1
    recommendation_list = []
    #Query all 27 location#
    for location in range(0,27):
        #Checks if location is unvisited#
        if row[location] == 0:
            #Builds query data#
            query = build_query(row,location)
            #checks if ML Model for location thinks whether user will like or not#
            if predict_user(prediction_models,location,query)[0] == 1.0: 
                #Adds the location to the list#
                recommendation_list.append(location)

    #Update Recommendation#
    worksheet.update_cell(num+2,31,recommendation_list)
    #clear the request#
    worksheet.update_cell(num+2,30,'0')
    worksheet.update_acell('AF1','0')
"""Initiates the Neural Networks for all locations
   Inputs: worksheet - The google sheet database
   Output: The array of Prediction Models for all locations
"""
def init_models(worksheet):
    model_array = []
    prediction_models = []
    temp = get_data(worksheet)
    #Removes names and unneccessary column#
    temp = delete(temp,(0),axis=1)
    temp = delete(temp,(-1),axis=1)
    temp = delete(temp,(-1),axis=1)
    temp = temp.astype(int) #convert all elements to integers
    #temp[temp==0] = -1 #replace non-visit 0's tp -1's

    #Create Class Instances for each location and adds to an array#
    model_one = location_learner(1)
    model_array.append(model_one)

    model_two = location_learner(2)
    model_array.append(model_two)
  
    model_three = location_learner(3)
    model_array.append(model_three)

    model_four = location_learner(4)
    model_array.append(model_four)

    model_five = location_learner(5)
    model_array.append(model_five)

    model_six = location_learner(6)
    model_array.append(model_six)

    model_seven = location_learner(7)
    model_array.append(model_seven)

    model_eight = location_learner(8)
    model_array.append(model_eight)

    model_nine = location_learner(9)
    model_array.append(model_nine)

    model_ten = location_learner(10)
    model_array.append(model_ten)

    model_eleven = location_learner(11)
    model_array.append(model_eleven)

    model_twelve = location_learner(12)
    model_array.append(model_twelve)

    model_thirteen = location_learner(13)
    model_array.append(model_thirteen)

    model_fourteen = location_learner(14)
    model_array.append(model_fourteen)

    model_fifteen = location_learner(15)
    model_array.append(model_fifteen)

    model_sixteen = location_learner(16)
    model_array.append(model_sixteen)

    model_seventeen = location_learner(17)
    model_array.append(model_seventeen)

    model_eighteen = location_learner(18)
    model_array.append(model_eighteen)

    model_nineteen = location_learner(19)
    model_array.append(model_nineteen)

    model_twenty = location_learner(20)
    model_array.append(model_twenty)

    model_twentyone = location_learner(21)
    model_array.append(model_twentyone)

    model_twentytwo = location_learner(22)
    model_array.append(model_twentytwo)

    model_twentythree = location_learner(23)
    model_array.append(model_twentythree)

    model_twentyfour = location_learner(24)
    model_array.append(model_twentyfour)

    model_twentyfive = location_learner(25)
    model_array.append(model_twentyfive)

    model_twentysix = location_learner(26)
    model_array.append(model_twentysix)

    model_twentyseven = location_learner(27)
    model_array.append(model_twentyseven)

    #TRAINS ALL THE LOCATION MODELS#
    for k in range(0,27):
        prediction_models.append(model_array[k].train_model(temp))
    return prediction_models
"""Retrieves and stores data from Database
   Inputs: worksheet - The google sheet database
   Output: The numpy matrix containing the user preference data from database
"""
def get_data(worksheet):
    #`Retrieves the data from the database#
    temp = asarray(worksheet.get_all_values())
    #Removes headings and server communication columns#
    temp = delete(temp,(0), axis=0)
    temp = delete(temp,(-1),axis=1)
    temp = delete(temp,(-1),axis=1)
    return temp
""" MAIN LOOP
"""
def main():
    #Setup to read/write to sheet using gspread#
    credentials = ServiceAccountCredentials.from_json_keyfile_name('My Project-e401bb1cb11e.json', scope)
    gs = gspread.authorize(credentials)
    worksheet = gs.open("CSSE4011Dataset").sheet1  
    count = 0

    #Initiate ML Models#
    print("Start Building Neural Networks")
    prediction_models = init_models(worksheet)

    print ("Neural Networks Have Been Constructed")
    print("Accuracy List: ", accuracy_list)
    print ("Starting Server...")

    #Runs Forever#
    while True:    
        if (worksheet.acell('AF1').value == '1'): #Checks global request cell
            print("Request Detected! Predicting Locations!")
            temp = get_data(worksheet) #gets the user data
            #Gets which users need updating, incase multiple requests have been made#
            i = 0
            request_log = []
            for val in temp[:, -1]:
                if val == '1': #request detected
                    request_log.append(i)
                i+=1
            #updates each user#
            for num in request_log:
                update_recommendation(prediction_models, temp[num], num, worksheet)
        #Keeps Track of iterations/check if server still alive
        print("Iteration = ", count)
        time.sleep(1) #delay so check only happens every second
        count+=1
if __name__ == '__main__':
    main()






