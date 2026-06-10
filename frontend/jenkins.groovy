pipeline {
    agent any
    stages {
        stage('PULL'){
            steps{
                git branch: 'main', url: 'https://github.com/waghvedant1990/flight-reservation-app1.git'
            }
        }
        stage('BUILD'){
            steps {
                sh'''
                    cd frontend
                    npm install
                    npm run build
                '''
            }
        }
        stage('DEPLOY'){
            steps{
                sh'''
                cd frontend
                aws s3 sync dist/ s3://cbz-frontend-project-bux-vedant-2026 / 
                '''
            }
        }
    }
    // post {
    //     success {
    //         echo 'Pipeline completed successfully!'
    //     }
    //     failure {
    //         echo 'Pipeline failed!'
    //     }
    //     always {
    //         cleanWs()
    //     }
    // }
}