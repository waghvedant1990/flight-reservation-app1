pipeline{
    agent any 
    environment {
        REPONAME = 'vedantwagh'
        IMAGE_NAME = 'flight-reservation-cdec-b50'
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages{
        stage('checkout'){
            steps{
                 git branch: 'main', url: 'https://github.com/waghvedant1990/flight-reservation-app1.git' 
            }

        }
        stage('build'){
            steps{
                sh '''
                     /usr/lib/jvm/java-17-openjdk-amd64/bin/java -version
                     /usr/lib/jvm/java-17-openjdk-amd64/bin/javac -version

                     export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
                     export PATH=/usr/lib/jvm/java-17-openjdk-amd64/bin:$PATH

                     mvn -version

                     cd FlightReservationApplication
                     mvn clean package -DskipTests
                '''
            }
        }
        stage('SonarQube Analysis'){
            steps{
                withSonarQubeEnv(credentialsId: 'sonar-sec', installationName: 'sonar-secret') {
                sh '''
                    export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
                    export PATH=$JAVA_HOME/bin:$PATH

                    cd FlightReservationApplication
                    mvn sonar:sonar \
                    -Dsonar.projectKey=flightreservationApplication \
                    -Dsonar.projectName=flightreservationApplication
                '''
                }
            }
        }
        stage('Dockerbuild'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    docker build -t $REPONAME/$IMAGE_NAME:$BUILD_NUMBER .
                    docker push $REPONAME/$IMAGE_NAME:$BUILD_NUMBER
                '''
            }
        }
        stage('Deploy to EKS'){
            steps{
                sh '''
                    cd FlightReservationApplication
                    sed -i "s|image: mayurwagh/flight-reservation-app:latest|image: $REPONAME/$IMAGE_NAME:$BUILD_NUMBER|g" k8s/deployment.yaml
                    kubectl apply -f k8s/deployment.yaml
                    kubectl apply -f k8s/service.yaml
                '''
            }
        }
    }
}