from flask import Flask as fs
from werkzeug import secure_filename
import inference
from flask import jsonify, request
import os
import sys
inference.loadOnce()
app=fs(__name__)
app.config['UPLOAD_FOLDER']='images/'

def receiveEmbedImage(request):
    file=request.files['file']
    file.save(os.path.join(app.config['UPLOAD_FOLDER'], 'image.jpg'))


@app.route('/VSNET_domain', methods=['POST'])
def funct():
    #Function to receive images
    receiveEmbedImage(request)
    data=inference.inference_trigger('image.jpg')
    data_dict={'result1: ': data[0], 'result1': data[1], 'result2': data[2], 'result3': data[3]}
    return jsonify(data_dict)

@app.route('/about')
def function():
    return 'In progress! :)'
