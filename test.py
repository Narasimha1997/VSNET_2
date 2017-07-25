import initialise as VSNET_INITIALIZER
import inference as inf
import time

inf.loadOnce()
i=0

times_i=time.time()
while i<100:
    inf.inference_trigger('image.jpg')
    i=i+1
times_f=time.time()-times_i
print(str(times_f))
