import tensorflow as tf
import pip
import os

#can be called during the initialization (boot-time of the appilcation),

DEPENDENCIES=['tensorflow','flask'] #Others will be automatically downloaded as they are required by TensorFlow
INCEPTION_DIRECTORY='inception'

#Inception is a neural network model built using TensorFlow SLIM Architecture
#More info: https://medium.com/initialized-capital/we-need-to-go-deeper-a-practical-guide-to-tensorflow-and-inception-50e66281804f
#Inception provides a world class Image Recognition capabilites
MODEL_NAME='classify_image_graph_def.pb'

def graph_define():
    #inception=classify_image_graph_def.pb --> loads the model and returns a FastGFile object
    #FastGFiles are used to to load the TensorFlow graph using high level Parallel computing technique
    model_dir=os.path.join(INCEPTION_DIRECTORY,MODEL_NAME)
    if not os.path.exists(model_dir):
        print('initialization error, Make sure you have inception Graph model and labels in ../inception directory')
        return None
    return tf.gfile.FastGFile(model_dir,mode='rb')

def initialization():
    #Load the defined graph here:
    graph=graph_define()
    if graph==None:
        return
    #Construct a graph using TensorFlow GraphDef() class and return it
    #Received at inference.py-->loadOnce()
    tensorflow_graph_builder=tf.GraphDef()
    tensorflow_graph_builder.ParseFromString(graph.read())
    return tf.import_graph_def(tensorflow_graph_builder,name="")
    pass
