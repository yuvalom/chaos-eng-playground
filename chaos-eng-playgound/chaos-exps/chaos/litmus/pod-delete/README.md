# Configuring the HTTP probe
Currently we must edit the HTTP endpoint which is currently `http://192.168.49.2:30002`
according to network entity under

# Running the experiment
kubectl apply -f chaosengine.yaml

# Observing the result
kubectl describe chaosengine web-service-app-chaos

