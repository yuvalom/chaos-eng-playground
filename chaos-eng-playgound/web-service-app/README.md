Perform these steps in the following order:
1. Build your docker image (`docker build -t ylom/web-service-app .`)
2. Login to your docker registry (`docker login -u ${username} -p ${password} docker.io`)
3. Push your image into the docker registry (`docker push ylom/web-service-app`)
4. Navigate to the k8s yml config files (`chaos-eng-playgound\web-service-app\k8s`)
5. Create the local storage specs (`kubectl apply -f local-storage.yaml`)
6. Create the mysql components (`kubectl apply -f mysql-config-components.yaml`)
7. Create the DB schema table in case needed (`kubectl exec -it mysql-set-0 -- mysql -u root -p -h mysql-service -P 3306`)
8. Create the application components (`kubectl apply -f web-service-app-config-components.yaml`)

In minikube, after completing this steps a service called web-service-app-svc will be running as a LoadBalancer type (external service) with <pending> external ip address status.
In order to assign an external ip address to the service run:

`minikube service web-service-app-svc`

This command will assign an external ip address to the service, and will open a tunnel in the same terminal.

In order to test you service, open a browser/postman/another terminal and execute the http request