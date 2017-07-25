import tensorflow as tf
import initialise as VSNET_INITIALIZER
import node_lookup as NODE_GENERATE
import os
import numpy
#initialization of models and label files takes place at node_lookup
#it will get the probability value from sigmoid of last layer and calculates a human_readable UID
#human_readable UIDs are then used to fetch human_readable labels

def loadOnce():
    #loads the tensorflow inception model for inference
    _=VSNET_INITIALIZER.initialization()

IMAGE_DIR='images'
IMAGE_FILE='image.jpg'

#This is where the TensorFlow session runs and performs image Recognition
#ImageDecoder --> Pixels --> Form polygons --> Combine them to produce meaningful shapes --> Generate probability vector
# softmax:0 is the last layer which returns a tensor of dim(786x786) (Task is intensive for low end devices)
def inference_brain(image,graph_session=None):
    with tf.Session() as tf_session:
        softmax0=tf_session.graph.get_tensor_by_name('softmax:0')
        prediction=tf_session.run(softmax0,{"DecodeJpeg/contents:0":image})
        return numpy.squeeze(prediction)

def run_send(image_data):
    np_array=inference_brain(image_data)
    np_array=np_array.argsort()[-4:][::-1]
    human_labels=[]
    ids=NODE_GENERATE.NodeLookup()
    for labels in np_array:
        human_labels.append(ids.id_to_string(labels))
    return human_labels
#make use of this method to trigger main inference function
#also load the image binary data here, steps:
# --> K=Load .jpg image data into image_
# --> Make sure we have UID and Human readable labels present in inception/ directory
def inference_trigger(image_data):
    image_=tf.gfile.FastGFile(os.path.join(IMAGE_DIR,image_data),mode='rb').read()
    labels_dir_UID=VSNET_INITIALIZER.INCEPTION_DIRECTORY+'/imagenet_2012_challenge_label_map_proto.pbtxt'
    labels_dir_humans=VSNET_INITIALIZER.INCEPTION_DIRECTORY+'/imagenet_synset_to_human_label_map.txt'
    if os.path.exists(labels_dir_UID) and os.path.exists(labels_dir_humans):
        return run_send(image_)
